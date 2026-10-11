package com.campussphere.service.impl;

import com.campussphere.dto.*;
import com.campussphere.entity.*;
import com.campussphere.entity.enums.*;
import com.campussphere.exception.*;
import com.campussphere.repository.*;
import com.campussphere.service.*;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DocumentServiceImpl implements DocumentService {
    private final DocumentRepository documents; private final ProjectRepository projects; private final ClubRepository clubs;
    private final KnowledgeRecordRepository knowledge; private final UserRepository users;
    private final ProjectMembershipRepository projectMembers; private final ClubMembershipRepository clubMembers;
    private final DocumentStorageService storage;
    public DocumentServiceImpl(DocumentRepository documents, ProjectRepository projects, ClubRepository clubs,
            KnowledgeRecordRepository knowledge, UserRepository users, ProjectMembershipRepository projectMembers,
            ClubMembershipRepository clubMembers, DocumentStorageService storage) {
        this.documents=documents; this.projects=projects; this.clubs=clubs; this.knowledge=knowledge; this.users=users;
        this.projectMembers=projectMembers; this.clubMembers=clubMembers; this.storage=storage;
    }
    @Override public List<DocumentDto> list(Long projectId, Long clubId, Long knowledgeRecordId, String actor) {
        List<DocumentEntity> found = projectId != null ? documents.findByProjectId(projectId) : clubId != null ? documents.findByClubId(clubId) : knowledgeRecordId != null ? documents.findByKnowledgeRecordId(knowledgeRecordId) : documents.findAll();
        return found.stream().filter(d -> (projectId == null || (d.getProject()!=null && projectId.equals(d.getProject().getId()))) && (clubId == null || (d.getClub()!=null && clubId.equals(d.getClub().getId()))) && (knowledgeRecordId == null || (d.getKnowledgeRecord()!=null && knowledgeRecordId.equals(d.getKnowledgeRecord().getId())))).filter(d -> canRead(d, actor)).map(DocumentDto::new).collect(Collectors.toList());
    }
    @Override public DocumentDto get(Long id, String actor) { DocumentEntity d=find(id); requireRead(d,actor); return new DocumentDto(d); }
    @Override @Transactional
    public DocumentDto upload(DocumentUploadRequest request, String actorEmail) {
        User actor=user(actorEmail); DocumentEntity d=new DocumentEntity();
        d.setTitle(request.getTitle().trim()); d.setDescription(request.getDescription()); d.setDocumentType(request.getDocumentType()); d.setTags(request.getTags());
        if(request.getProjectId()!=null){ Project p=projects.findById(request.getProjectId()).orElseThrow(()->new ResourceNotFoundException("Project not found")); requireProjectManage(p,actor); d.setProject(p); }
        if(request.getClubId()!=null){ Club c=clubs.findById(request.getClubId()).orElseThrow(()->new ResourceNotFoundException("Club not found")); requireClubManage(c,actor); d.setClub(c); }
        if(request.getKnowledgeRecordId()!=null){ KnowledgeRecord k=knowledge.findById(request.getKnowledgeRecordId()).orElseThrow(()->new ResourceNotFoundException("Knowledge record not found")); if(!canManageKnowledge(k,actor)) throw new ForbiddenException("Not authorized to attach documents to this knowledge record"); d.setKnowledgeRecord(k); if(d.getProject()==null)d.setProject(k.getProject()); if(d.getClub()==null)d.setClub(k.getClubEntity()); }
        if(d.getProject()==null && d.getClub()==null && d.getKnowledgeRecord()==null && !isManager(actor)) throw new ForbiddenException("Documents must be associated with a project, club, or knowledge record");
        d.setUploader(actor); String key=storage.store(request.getFile()); d.setStorageLocation(key); d.setOriginalFileName(safeFilename(request.getFile().getOriginalFilename())); d.setFileSize(request.getFile().getSize()); d.setFileType(request.getFile().getContentType()); d.setFileUrl("/api/documents");
        try { DocumentEntity saved=documents.saveAndFlush(d); cleanupOnRollback(key); return new DocumentDto(saved); }
        catch(RuntimeException e){ storage.delete(key); throw e; }
    }
    @Override @Transactional(readOnly=true) public DocumentDownload download(Long id,String actor){ DocumentEntity d=find(id); requireRead(d,actor); return new DocumentDownload(storage.load(d.getStorageLocation()), safeFilename(d.getOriginalFileName()), d.getFileType()); }
    @Override @Transactional public void delete(Long id,String actorEmail){ DocumentEntity d=find(id); User actor=user(actorEmail); if(!canManageDocument(d,actor)) throw new ForbiddenException("Not authorized to delete this document"); String key=d.getStorageLocation(); documents.delete(d); TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization(){ @Override public void afterCommit(){ storage.delete(key); }}); }
    @Override public List<DocumentDto> getProjectDocuments(Long projectId,String actorEmail){ Project p=projects.findById(projectId).orElseThrow(()->new ResourceNotFoundException("Project not found")); User actor=user(actorEmail); if(!isManager(actor) && !p.getOwner().getId().equals(actor.getId()) && !projectMembers.existsByProjectIdAndUserIdAndStatus(projectId,actor.getId(),MembershipStatus.APPROVED)) throw new ForbiddenException("Not authorized to view project documents"); return documents.findByProjectId(projectId).stream().map(DocumentDto::new).collect(Collectors.toList()); }
    private DocumentEntity find(Long id){return documents.findById(id).orElseThrow(()->new ResourceNotFoundException("Document not found with id: "+id));}
    private User user(String email){return users.findByEmail(email).orElseThrow(()->new ResourceNotFoundException("User not found"));}
    private boolean isManager(User u){return u.getRole()==Role.ADMIN||u.getRole()==Role.FACULTY||u.getRole()==Role.RESOURCE_MANAGER;}
    private boolean canRead(DocumentEntity d,String email){ User u=user(email); if(isManager(u)||d.getUploader().getId().equals(u.getId()))return true; if(d.getProject()!=null&&(d.getProject().getOwner().getId().equals(u.getId())||projectMembers.existsByProjectIdAndUserIdAndStatus(d.getProject().getId(),u.getId(),MembershipStatus.APPROVED)))return true; if(d.getClub()!=null&&clubMembers.findByClubIdAndUserId(d.getClub().getId(),u.getId()).filter(m->m.getStatus()==MembershipStatus.APPROVED).isPresent())return true; if(d.getKnowledgeRecord()!=null&&d.getKnowledgeRecord().getAuthor()!=null&&d.getKnowledgeRecord().getAuthor().getId().equals(u.getId()))return true; return false; }
    private void requireRead(DocumentEntity d,String email){if(!canRead(d,email))throw new ForbiddenException("Not authorized to access this document");}
    private boolean canManageDocument(DocumentEntity d,User u){if(isManager(u)||d.getUploader().getId().equals(u.getId()))return true; if(d.getProject()!=null&&d.getProject().getOwner().getId().equals(u.getId()))return true; if(d.getClub()!=null&&clubMembers.findByClubIdAndUserId(d.getClub().getId(),u.getId()).filter(m->m.getStatus()==MembershipStatus.APPROVED&&(m.getRole()==ClubRole.ADMIN||m.getRole()==ClubRole.OFFICER)).isPresent())return true; return d.getKnowledgeRecord()!=null&&d.getKnowledgeRecord().getAuthor()!=null&&d.getKnowledgeRecord().getAuthor().getId().equals(u.getId()); }
    private void requireProjectManage(Project p,User u){if(!isManager(u)&&!p.getOwner().getId().equals(u.getId())&&!projectMembers.existsByProjectIdAndUserIdAndStatus(p.getId(),u.getId(),MembershipStatus.APPROVED))throw new ForbiddenException("Not authorized to manage project documents");}
    private void requireClubManage(Club c,User u){if(isManager(u))return; if(clubMembers.findByClubIdAndUserId(c.getId(),u.getId()).filter(m->m.getStatus()==MembershipStatus.APPROVED).isEmpty())throw new ForbiddenException("Not authorized to manage club documents");}
    private boolean canManageKnowledge(KnowledgeRecord k,User u){return isManager(u)||(k.getAuthor()!=null&&k.getAuthor().getId().equals(u.getId()))||(k.getProject()!=null&&(k.getProject().getOwner().getId().equals(u.getId())||projectMembers.existsByProjectIdAndUserIdAndStatus(k.getProject().getId(),u.getId(),MembershipStatus.APPROVED)));}
    private String safeFilename(String name){if(name==null||name.isBlank())return "document";String n=name.replace('\\','/');n=n.substring(n.lastIndexOf('/')+1).replaceAll("[\\r\\n\\p{Cntrl}]", "_");return n.length()>180?n.substring(n.length()-180):n;}
    private void cleanupOnRollback(String key){TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization(){@Override public void afterCompletion(int status){if(status!=STATUS_COMMITTED)storage.delete(key);}});}
}
