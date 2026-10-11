package com.campussphere.dto;

import java.util.Map;

public class AdminDashboardStatsDto {
    private long totalUsers;
    private long totalProjects;
    private long totalClubs;
    private long totalEquipment;
    private long totalVenues;
    private long pendingRequests;
    private double bookingUtilization;
    private double equipmentUtilization;
    private Map<String, Long> projectsByDepartment;
    private Map<String, Long> projectsByTechnology;

    public AdminDashboardStatsDto() {}

    public long getTotalUsers() { return totalUsers; }
    public void setTotalUsers(long totalUsers) { this.totalUsers = totalUsers; }

    public long getTotalProjects() { return totalProjects; }
    public void setTotalProjects(long totalProjects) { this.totalProjects = totalProjects; }

    public long getTotalClubs() { return totalClubs; }
    public void setTotalClubs(long totalClubs) { this.totalClubs = totalClubs; }

    public long getTotalEquipment() { return totalEquipment; }
    public void setTotalEquipment(long totalEquipment) { this.totalEquipment = totalEquipment; }

    public long getTotalVenues() { return totalVenues; }
    public void setTotalVenues(long totalVenues) { this.totalVenues = totalVenues; }

    public long getPendingRequests() { return pendingRequests; }
    public void setPendingRequests(long pendingRequests) { this.pendingRequests = pendingRequests; }

    public double getBookingUtilization() { return bookingUtilization; }
    public void setBookingUtilization(double bookingUtilization) { this.bookingUtilization = bookingUtilization; }

    public double getEquipmentUtilization() { return equipmentUtilization; }
    public void setEquipmentUtilization(double equipmentUtilization) { this.equipmentUtilization = equipmentUtilization; }

    public Map<String, Long> getProjectsByDepartment() { return projectsByDepartment; }
    public void setProjectsByDepartment(Map<String, Long> projectsByDepartment) { this.projectsByDepartment = projectsByDepartment; }

    public Map<String, Long> getProjectsByTechnology() { return projectsByTechnology; }
    public void setProjectsByTechnology(Map<String, Long> projectsByTechnology) { this.projectsByTechnology = projectsByTechnology; }
}
