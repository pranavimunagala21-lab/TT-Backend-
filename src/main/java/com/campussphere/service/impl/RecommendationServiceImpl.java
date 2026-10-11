package com.campussphere.service.impl;

import com.campussphere.dto.*;
import com.campussphere.entity.*;
import com.campussphere.entity.enums.*;
import com.campussphere.exception.BadRequestException;
import com.campussphere.exception.ResourceNotFoundException;
import com.campussphere.repository.*;
import com.campussphere.service.*;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class RecommendationServiceImpl implements RecommendationService {
    private final ProjectRepository projects;
    private final UserRepository users;
    private final ProjectMembershipRepository memberships;
    private final KnowledgeRecordRepository knowledge;
    private final VenueService venues;
    private final BookingService bookings;
    private final EquipmentService equipment;

    public RecommendationServiceImpl(ProjectRepository projects, UserRepository users, ProjectMembershipRepository memberships,
            KnowledgeRecordRepository knowledge, VenueService venues, BookingService bookings, EquipmentService equipment) {
        this.projects=projects; this.users=users; this.memberships=memberships; this.knowledge=knowledge;
        this.venues=venues; this.bookings=bookings; this.equipment=equipment;
    }

    @Override
    public Page<RankedPersonMatchDto> peopleForProject(Long projectId, Role role, String query, Pageable pageable) {
        Project project = project(projectId);
        Set<Long> already = new HashSet<>();
        already.add(project.getOwner().getId());
        memberships.findByProjectId(projectId).stream().filter(m -> m.getStatus()==MembershipStatus.APPROVED).forEach(m -> already.add(m.getUser().getId()));
        List<String> required = lower(project.getRequiredSkills());
        Set<String> context = terms(project.getTitle()+" "+project.getDescription()+" "+project.getProblemStatement()+" "+project.getCategory()+" "+String.join(" ",project.getTechnologies()));
        List<ProjectMembership> history = memberships.findAll();
        List<KnowledgeRecord> records = knowledge.findAll();
        String q = query==null ? null : query.trim().toLowerCase(Locale.ROOT);
        List<RankedPersonMatchDto> matches = new ArrayList<>();
        for (User user : users.findAll()) {
            if (already.contains(user.getId()) || (role!=null && user.getRole()!=role) || (user.getRole()!=Role.STUDENT && user.getRole()!=Role.FACULTY)) continue;
            if (q!=null && !q.isEmpty() && !contains(user.getName(),q) && !contains(user.getDepartment(),q)) continue;
            List<String> skills=lower(user.getSkills());
            List<String> skillHits=required.stream().filter(r -> skills.stream().anyMatch(s -> s.equals(r)||s.contains(r)||r.contains(s))).distinct().toList();
            List<String> reasons=new ArrayList<>(); double score=0;
            if (!required.isEmpty() && !skillHits.isEmpty()) { score+=Math.min(65,65.0*skillHits.size()/required.size()); reasons.add("Skills match: "+String.join(", ",skillHits)); }
            List<String> interestHits=lower(user.getInterests()).stream().filter(i -> context.stream().anyMatch(t -> t.length()>2&&(t.contains(i)||i.contains(t)))).distinct().toList();
            if (!interestHits.isEmpty()) { score+=Math.min(20,interestHits.size()*10); reasons.add("Interests align: "+String.join(", ",interestHits)); }
            if (project.getClub()!=null && project.getClub().getDepartment()!=null && project.getClub().getDepartment().equalsIgnoreCase(user.getDepartment())) { score+=5; reasons.add("Same department as the project club"); }
            long related=history.stream().filter(m -> m.getUser().getId().equals(user.getId()) && !m.getProject().getId().equals(projectId)).map(ProjectMembership::getProject).filter(p -> sameContext(project,p)).count();
            long authored=records.stream().filter(k -> k.getAuthor()!=null && k.getAuthor().getId().equals(user.getId()) && k.getProject()!=null && sameContext(project,k.getProject())).count();
            if (related>0) { score+=Math.min(7,related*3); reasons.add("Participated in "+related+" related project(s)"); }
            if (authored>0) { score+=Math.min(8,authored*4); reasons.add("Authored "+authored+" knowledge record(s) for related projects"); }
            if (user.getRole()==Role.FACULTY) { score+=3; reasons.add("Faculty mentor candidate"); }
            if (!reasons.isEmpty()) matches.add(new RankedPersonMatchDto(user.getId(),user.getName(),user.getRole(),user.getDepartment(),Math.min(100,score),reasons));
        }
        matches.sort(Comparator.comparingDouble(RankedPersonMatchDto::getScore).reversed().thenComparing(RankedPersonMatchDto::getName,String.CASE_INSENSITIVE_ORDER));
        return page(matches,pageable);
    }

    @Override
    public List<RecommendationDto> venuesForProject(Long projectId, LocalDateTime start, LocalDateTime end, Integer attendees, Set<String> requestedFacilities) {
        Project project=project(projectId);
        if (start==null || end==null || !end.isAfter(start)) throw new BadRequestException("Valid startTime and endTime are required");
        if (attendees!=null && attendees<1) throw new BadRequestException("attendees must be at least 1");
        Map<Long,Boolean> available=bookings.searchAvailability(start,end).stream().collect(Collectors.toMap(VenueAvailabilityDto::getVenueId,VenueAvailabilityDto::isAvailable));
        Set<String> requested=requestedFacilities==null ? Set.of() : new HashSet<>(lower(requestedFacilities));
        List<RecommendationDto> result=new ArrayList<>();
        for (VenueDto venue : venues.findVenues(null,null,attendees,null)) {
            if (!Boolean.TRUE.equals(available.get(venue.getId()))) continue;
            Set<String> actual=new HashSet<>(lower(venue.getFacilities()));
            List<String> hits=requested.stream().filter(f -> actual.stream().anyMatch(a -> a.equals(f)||a.contains(f)||f.contains(a))).toList();
            if (!requested.isEmpty() && hits.isEmpty()) continue;
            double score=60; List<String> why=new ArrayList<>(); why.add("Available for the requested time");
            if (attendees!=null) { score+=20; why.add("Capacity covers "+attendees+" attendees"); }
            if (!requested.isEmpty()) { score+=20.0*hits.size()/requested.size(); why.add("Facilities match: "+String.join(", ",hits)); }
            if (project.getRequiredVenue()!=null && contains(venue.getName()+" "+venue.getDescription(),project.getRequiredVenue().toLowerCase(Locale.ROOT))) { score=Math.min(100,score+10); why.add("Matches the project's requested venue description"); }
            result.add(new RecommendationDto(venue.getName(),Math.min(100,score),String.join("; ",why),"VENUE",venue.getId()));
        }
        result.sort(Comparator.comparing(RecommendationDto::getScore).reversed()); return result;
    }

    @Override
    public List<RecommendationDto> equipmentForProject(Long projectId) {
        Project project=project(projectId); List<String> needs=lower(project.getRequiredEquipment()); if (needs.isEmpty()) return List.of();
        List<RecommendationDto> result=new ArrayList<>();
        for (EquipmentDto item : equipment.searchEquipment(null,null,null,true)) {
            if (item.getAvailableQuantity()==null || item.getAvailableQuantity()<1 || item.getStatus()==EquipmentStatus.MAINTENANCE) continue;
            String desc=(n(item.getName())+" "+n(item.getCategory())+" "+n(item.getDescription())).toLowerCase(Locale.ROOT);
            List<String> hits=needs.stream().filter(need -> desc.contains(need)||need.contains(n(item.getName()).toLowerCase(Locale.ROOT))).toList();
            if (!hits.isEmpty()) result.add(new RecommendationDto(item.getName(),Math.min(100.0,65.0+hits.size()*15),"Available inventory matches project needs: "+String.join(", ",hits),"EQUIPMENT",item.getId()));
        }
        result.sort(Comparator.comparing(RecommendationDto::getScore).reversed()); return result;
    }

    @Override
    public Page<DiscoveryRecommendationDto> relatedProjects(Long projectId, Pageable pageable) {
        Project source=project(projectId); List<DiscoveryRecommendationDto> out=new ArrayList<>();
        for (Project candidate : projects.findAll()) {
            if (candidate.getId().equals(source.getId())) continue;
            List<String> reasons=new ArrayList<>(); double score=0;
            if (same(source.getCategory(),candidate.getCategory())) { score+=25; reasons.add("Same category: "+source.getCategory()); }
            List<String> tech=intersections(lower(source.getTechnologies()),lower(candidate.getTechnologies()));
            if (!tech.isEmpty()) { score+=Math.min(35,tech.size()*12); reasons.add("Shared technologies: "+String.join(", ",tech)); }
            List<String> skills=intersections(lower(source.getRequiredSkills()),lower(candidate.getRequiredSkills()));
            if (!skills.isEmpty()) { score+=Math.min(20,skills.size()*10); reasons.add("Similar skill requirements: "+String.join(", ",skills)); }
            Set<String> overlap=intersection(terms(source.getTitle()+" "+source.getDescription()),terms(candidate.getTitle()+" "+candidate.getDescription()));
            if (!overlap.isEmpty()) { score+=Math.min(20,overlap.size()*3); reasons.add("Description/title overlap: "+String.join(", ",overlap.stream().limit(5).toList())); }
            if (score>0) out.add(new DiscoveryRecommendationDto("PROJECT",candidate.getId(),candidate.getTitle(),Math.min(100,score),reasons,new ProjectDto(candidate),null));
        }
        out.sort(Comparator.comparingDouble(DiscoveryRecommendationDto::getScore).reversed()); return page(out,pageable);
    }

    @Override
    public Page<DiscoveryRecommendationDto> relatedKnowledge(Long projectId, Pageable pageable) {
        Project source=project(projectId); List<DiscoveryRecommendationDto> out=new ArrayList<>();
        for (KnowledgeRecord record : knowledge.findAll()) {
            List<String> reasons=new ArrayList<>(); double score=0;
            if (record.getProject()!=null && record.getProject().getId().equals(projectId)) { score+=50; reasons.add("Linked directly to this project"); }
            if (same(source.getCategory(),record.getCategory())) { score+=20; reasons.add("Same category: "+source.getCategory()); }
            List<String> tech=lower(source.getTechnologies()).stream().filter(t -> record.getTechnology()!=null&&record.getTechnology().toLowerCase(Locale.ROOT).contains(t)).toList();
            if (!tech.isEmpty()) { score+=Math.min(20,tech.size()*10); reasons.add("Shared technologies: "+String.join(", ",tech)); }
            Set<String> overlap=intersection(terms(source.getTitle()+" "+source.getDescription()+" "+String.join(" ",source.getTechnologies())),terms(record.getTitle()+" "+record.getDescription()+" "+record.getTags()+" "+record.getContent()));
            if (!overlap.isEmpty()) { score+=Math.min(20,overlap.size()*2); reasons.add("Related terms: "+String.join(", ",overlap.stream().limit(5).toList())); }
            if (score>0) out.add(new DiscoveryRecommendationDto("KNOWLEDGE",record.getId(),record.getTitle(),Math.min(100,score),reasons,null,new KnowledgeRecordDto(record)));
        }
        out.sort(Comparator.comparingDouble(DiscoveryRecommendationDto::getScore).reversed()); return page(out,pageable);
    }

    private Project project(Long id) { return projects.findById(id).orElseThrow(() -> new ResourceNotFoundException("Project not found with id: "+id)); }
    private boolean sameContext(Project a,Project b) { return same(a.getCategory(),b.getCategory()) || !intersections(lower(a.getTechnologies()),lower(b.getTechnologies())).isEmpty(); }
    private boolean same(String a,String b) { return a!=null && b!=null && a.equalsIgnoreCase(b); }
    private List<String> lower(Collection<String> values) { return values==null ? List.of() : values.stream().filter(Objects::nonNull).map(s->s.trim().toLowerCase(Locale.ROOT)).filter(s->!s.isEmpty()).distinct().toList(); }
    private List<String> intersections(List<String> a,List<String> b) { return a.stream().filter(b::contains).distinct().toList(); }
    private Set<String> intersection(Set<String> a,Set<String> b) { return a.stream().filter(b::contains).collect(Collectors.toCollection(LinkedHashSet::new)); }
    private Set<String> terms(String text) { if (text==null) return Set.of(); return Arrays.stream(text.toLowerCase(Locale.ROOT).split("[^a-z0-9+#.-]+" )).filter(t->t.length()>2).collect(Collectors.toCollection(LinkedHashSet::new)); }
    private boolean contains(String text,String q) { return text!=null && text.toLowerCase(Locale.ROOT).contains(q); }
    private String n(String value) { return value==null ? "" : value; }
    private <T> Page<T> page(List<T> list,Pageable pageable) { if (pageable.isUnpaged()) return new PageImpl<>(list,pageable,list.size()); int from=(int)Math.min(pageable.getOffset(),list.size()); int to=Math.min(from+pageable.getPageSize(),list.size()); return new PageImpl<>(list.subList(from,to),pageable,list.size()); }
}
