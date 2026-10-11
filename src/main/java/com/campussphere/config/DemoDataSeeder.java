package com.campussphere.config;

import com.campussphere.entity.*;
import com.campussphere.entity.enums.*;
import com.campussphere.repository.*;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Set;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import org.springframework.web.multipart.MultipartFile;
import com.campussphere.service.DocumentStorageService;

/** Explicit, opt-in illustrative data. It never creates users or changes existing records. */
@Component
@org.springframework.context.annotation.Profile("local")
@ConditionalOnProperty(name = "campussphere.demo-seed", havingValue = "true")
public class DemoDataSeeder implements ApplicationRunner {
    private final UserRepository users;
    private final VenueRepository venues;
    private final ClubRepository clubs;
    private final ProjectRepository projects;
    private final EquipmentRepository equipment;
    private final KnowledgeRecordRepository knowledge;
    private final HistoricalRecordRepository history;
    private final DocumentRepository documents;
    private final DocumentStorageService documentStorage;

    public DemoDataSeeder(UserRepository users, VenueRepository venues, ClubRepository clubs,
                          ProjectRepository projects, EquipmentRepository equipment,
                          KnowledgeRecordRepository knowledge, HistoricalRecordRepository history,
                          DocumentRepository documents, DocumentStorageService documentStorage) {
        this.users = users; this.venues = venues; this.clubs = clubs;
        this.projects = projects; this.equipment = equipment; this.knowledge = knowledge; this.history = history;
        this.documents = documents; this.documentStorage = documentStorage;
    }

    @Override @Transactional
    public void run(ApplicationArguments args) {
        User owner = users.findAll().stream().filter(u -> u.getRole() == Role.STUDENT).findFirst().orElse(null);
        if (owner == null) {
            System.out.println("Demo seed skipped: register a student account first. No accounts are created by the seeder.");
            return;
        }
        seedVenue("DEMO - Collaboration Studio", "DEMO Building A", "1", 24,
                Set.of("Projector", "Whiteboard", "Video conferencing"), "Illustrative demo venue; verify location and availability locally.");
        seedVenue("DEMO - Maker Bench", "DEMO Building B", "Ground", 12,
                Set.of("Workbench", "Power outlets", "Safety equipment"), "Illustrative demo facility; not an official campus location.");
        seedVenue("DEMO - Lecture Hall 101", "DEMO Building C", "1", 120, Set.of("Projector", "Tiered seating", "Audio"), "Illustrative lecture facility.");
        seedVenue("DEMO - Seminar Room 204", "DEMO Building C", "2", 28, Set.of("Display", "Whiteboard"), "Illustrative seminar facility.");
        seedVenue("DEMO - Community Auditorium", "DEMO Building D", "Ground", 240, Set.of("Stage", "Audio", "Projector"), "Illustrative event facility.");
        seedVenue("DEMO - Robotics Lab", "DEMO Building B", "1", 18, Set.of("Workbenches", "Safety equipment", "Power"), "Illustrative lab; confirm access and safety rules locally.");
        seedVenue("DEMO - Meeting Room 3", "DEMO Building A", "2", 10, Set.of("Video conferencing", "Display"), "Illustrative meeting facility.");
        seedVenue("DEMO - Data Lab", "DEMO Building E", "1", 32, Set.of("Computers", "Display"), "Illustrative lab facility.");
        Club club = clubs.findAll().stream().filter(c -> c.getName().equals("DEMO - Green Computing Circle")).findFirst().orElseGet(() -> {
            Club c = new Club(); c.setName("DEMO - Green Computing Circle");
            c.setDescription("Illustrative student community exploring sustainable computing."); c.setDepartment("DEMO - Computing");
            c.setCategory("Sustainability"); c.setActivities("Prototype reviews; lifecycle analysis workshops");
            c.setAchievements("Illustrative sample only"); c.setHistory("Demo data, not an official campus club");
            return clubs.save(c);
        });
        seedClub("DEMO - Coding Collective", "Technology", "Illustrative peer coding community.", "Coding practice; open source sessions");
        seedClub("DEMO - Robotics Workshop", "Engineering", "Illustrative robotics and prototyping community.", "Build nights; design reviews");
        seedClub("DEMO - Applied AI Forum", "Artificial Intelligence", "Illustrative responsible AI study group.", "Reading group; model evaluation workshops");
        seedClub("DEMO - Venture Studio", "Entrepreneurship", "Illustrative student venture exploration group.", "Idea clinics; pitch practice");
        seedClub("DEMO - Arts and Culture Exchange", "Culture", "Illustrative cultural exchange community.", "Performances; creative workshops");
        Project project = projects.findAll().stream().filter(p -> p.getTitle().equals("DEMO - Campus Energy Insights")).findFirst().orElseGet(() -> {
            Project p = new Project(); p.setTitle("DEMO - Campus Energy Insights");
            p.setDescription("Illustrative project exploring energy usage dashboards using sample data.");
            p.setProblemStatement("How might a community understand building energy patterns?"); p.setCategory("Sustainability");
            p.setTechnologies(Set.of("React", "Spring Boot", "Data visualization"));
            p.setRequiredSkills(Set.of("Java", "Data analysis", "UX research"));
            p.setRequiredEquipment(Set.of("Laptop")); p.setStatus(ProjectStatus.PLANNING); p.setOwner(owner); p.setClub(club);
            p.setStartDate(LocalDate.now()); p.setEndDate(LocalDate.now().plusMonths(3));
            return projects.save(p);
        });
        seedProject(owner, "DEMO - Accessible Wayfinding Prototype", "Accessibility", ProjectStatus.IDEA, Set.of("React", "Mapping"), Set.of("UX research", "JavaScript"));
        seedProject(owner, "DEMO - Smart Garden Monitor", "Robotics", ProjectStatus.PLANNING, Set.of("Microcontrollers", "Sensors"), Set.of("Embedded systems", "Data analysis"));
        seedProject(owner, "DEMO - Study Group Match", "Community", ProjectStatus.ACTIVE, Set.of("Spring Boot", "React"), Set.of("Java", "Product design"));
        seedProject(owner, "DEMO - Lab Inventory Tracker", "Operations", ProjectStatus.IDEA, Set.of("Java", "MySQL"), Set.of("Backend development", "Data modeling"));
        seedEquipment("DEMO - USB sensor kit", "Sensors", "DEMO-SENSOR-001", 4, "Illustrative inventory; confirm before use.");
        seedEquipment("DEMO - Portable projector", "AV", "DEMO-AV-001", 2, "Illustrative inventory; confirm before use.");
        seedEquipment("DEMO - Robotics kit", "Robotics", "DEMO-ROB-001", 6, "Illustrative inventory; confirm before use.");
        seedEquipment("DEMO - Microcontroller board", "Electronics", "DEMO-MCU-001", 12, "Illustrative inventory; confirm before use.");
        seedEquipment("DEMO - Temperature sensor", "Sensors", "DEMO-SENS-002", 10, "Illustrative inventory; confirm before use.");
        seedEquipment("DEMO - Digital multimeter", "Laboratory", "DEMO-LAB-001", 4, "Illustrative inventory; confirm before use.");
        seedEquipment("DEMO - Soldering station", "Laboratory", "DEMO-LAB-002", 3, "Illustrative inventory; supervised use only.");
        seedEquipment("DEMO - USB camera", "AV", "DEMO-AV-002", 5, "Illustrative inventory; confirm before use.");
        seedEquipment("DEMO - Environmental sensor pack", "Sensors", "DEMO-SENS-003", 5, "Illustrative inventory; confirm before use.");
        if (knowledge.findAll().stream().noneMatch(k -> k.getTitle().equals("DEMO - Energy dashboard kickoff notes"))) {
            KnowledgeRecord k = new KnowledgeRecord(); k.setTitle("DEMO - Energy dashboard kickoff notes");
            k.setDescription("Illustrative planning notes linked to the demo project.");
            k.setContent("Demo record only. Replace with verified campus findings before operational use.");
            k.setCategory("LESSON_LEARNED"); k.setType("DEMO"); k.setDepartment("DEMO - Computing");
            k.setTechnology("React, Spring Boot"); k.setYear(LocalDate.now().getYear()); k.setRecordDate(LocalDate.now());
            k.setTags("demo,sustainability,energy"); k.setAuthor(owner); k.setProject(project); k.setClubEntity(club);
            knowledge.save(k);
        }
        seedKnowledge(owner, "DEMO - Robotics build checklist", "TECH_DOC", "Robotics", "Embedded systems, sensors", "Illustrative setup checklist linked to sample campus projects.");
        seedKnowledge(owner, "DEMO - Peer coding session notes", "EVENT_RECORD", "Community", "JavaScript, Git", "Illustrative event record; not an official campus event.");
        seedHistory(owner, project, club, "DEMO - Energy dashboard prototype milestone", "Prototype", "Illustrative milestone for a sample dashboard prototype.");
        seedHistory(owner, project, club, "DEMO - Student project handoff notes", "Continuity", "Illustrative project continuity and handoff record.");
        seedDocument(owner, project);
        System.out.println("CampusSphere illustrative demo data ensured for existing student " + owner.getEmail() + ". No user accounts were changed.");
    }

    private void seedVenue(String name, String building, String floor, int capacity, Set<String> facilities, String description) {
        if (venues.findAll().stream().noneMatch(v -> v.getName().equals(name))) {
            Venue v = new Venue(); v.setName(name); v.setBuilding(building); v.setFloor(floor);
            v.setCapacity(capacity); v.setFacilities(facilities); v.setDescription(description); v.setStatus("AVAILABLE"); venues.save(v);
        }
    }
    private void seedEquipment(String name, String category, String serial, int quantity, String description) {
        if (equipment.findBySerialNumber(serial).isEmpty()) {
            Equipment e = new Equipment(); e.setName(name); e.setCategory(category); e.setSerialNumber(serial);
            e.setQuantity(quantity); e.setAvailableQuantity(quantity); e.setConditionStatus(EquipmentCondition.GOOD);
            e.setStatus(EquipmentStatus.AVAILABLE); e.setLocation("DEMO storage; verify locally"); e.setDescription(description); equipment.save(e);
        }
    }
    private void seedClub(String name, String category, String description, String activities) {
        if (clubs.findAll().stream().noneMatch(c -> c.getName().equals(name))) {
            Club c = new Club(); c.setName(name); c.setCategory(category); c.setDepartment("DEMO - Campus");
            c.setDescription(description); c.setActivities(activities); c.setHistory("Illustrative demo record; not an official organization."); clubs.save(c);
        }
    }
    private void seedProject(User owner, String title, String category, ProjectStatus status, Set<String> technologies, Set<String> skills) {
        if (projects.findAll().stream().noneMatch(p -> p.getTitle().equals(title))) {
            Project p = new Project(); p.setTitle(title); p.setDescription("Illustrative student project for local demonstration.");
            p.setProblemStatement("Demo prompt: explore a useful solution and validate it with users."); p.setCategory(category);
            p.setTechnologies(technologies); p.setRequiredSkills(skills); p.setStatus(status); p.setOwner(owner);
            p.setStartDate(LocalDate.now()); p.setEndDate(LocalDate.now().plusMonths(4)); projects.save(p);
        }
    }
    private void seedKnowledge(User owner, String title, String category, String department, String technology, String description) {
        if (knowledge.findAll().stream().noneMatch(k -> k.getTitle().equals(title))) {
            KnowledgeRecord k = new KnowledgeRecord(); k.setTitle(title); k.setCategory(category); k.setType("DEMO");
            k.setDepartment("DEMO - " + department); k.setTechnology(technology); k.setDescription(description);
            k.setContent("Demo content only. Replace with verified campus information before operational use.");
            k.setYear(LocalDate.now().getYear()); k.setRecordDate(LocalDate.now()); k.setTags("demo," + department.toLowerCase()); k.setAuthor(owner); knowledge.save(k);
        }
    }
    private void seedHistory(User owner, Project project, Club club, String title, String category, String summary) {
        if (history.findAll().stream().noneMatch(h -> h.getProjectTitle().equals(title))) {
            HistoricalRecord h = new HistoricalRecord(); h.setProjectTitle(title); h.setDepartment("DEMO - Computing");
            h.setYear(LocalDate.now().getYear()); h.setCategory(category); h.setTechnologies("React, Spring Boot");
            h.setSummary(summary); h.setOutcomes("Illustrative demo record; no real campus outcomes are claimed.");
            h.setLessonsLearned("Replace this sample with verified project continuity notes."); h.setProject(project); h.setClub(club);
            h.setClubName(club.getName()); h.setRecordedBy(owner); h.setEventDate(LocalDate.now().minusMonths(1)); history.save(h);
        }
    }
    private void seedDocument(User owner, Project project) {
        String filename = "CampusSphere-demo-project-guide.txt";
        if (documents.findByProjectId(project.getId()).stream().noneMatch(d -> filename.equals(d.getOriginalFileName()))) {
            byte[] content = ("CampusSphere illustrative demo file\n\nThis is sample project documentation, not an official campus record.\nReplace it with verified material before operational use.\n").getBytes(java.nio.charset.StandardCharsets.UTF_8);
            String key = documentStorage.store(new DemoFile(filename, content));
            DocumentEntity d = new DocumentEntity(); d.setTitle("DEMO - Project guide");
            d.setDescription("Illustrative downloadable project document."); d.setDocumentType("REPORT");
            d.setFileUrl("/api/documents"); d.setFileType("text/plain"); d.setFileSize((long) content.length);
            d.setStorageLocation(key); d.setOriginalFileName(filename); d.setTags("demo,project");
            d.setProject(project); d.setUploader(owner); documents.save(d);
        }
    }

    private static final class DemoFile implements MultipartFile {
        private final String name; private final byte[] bytes;
        private DemoFile(String name, byte[] bytes) { this.name=name; this.bytes=bytes; }
        public String getName() { return "file"; }
        public String getOriginalFilename() { return name; }
        public String getContentType() { return "text/plain"; }
        public boolean isEmpty() { return bytes.length == 0; }
        public long getSize() { return bytes.length; }
        public byte[] getBytes() { return bytes.clone(); }
        public InputStream getInputStream() { return new ByteArrayInputStream(bytes); }
        public void transferTo(File destination) throws IOException { Files.write(destination.toPath(), bytes); }
    }
}
