package com.petcare.dto;

import java.math.BigDecimal;

public class DashboardStatsDto {
    private long totalPets;
    private long boardedPets;
    private long totalBookings;
    private long activeBookings;
    private long totalUsers;
    private long pendingFeedings;
    private long pendingMedications;
    private long dueVaccinations;
    private long scheduledGroomings;
    private BigDecimal paidRevenue;
    private BigDecimal pendingRevenue;
    private long unreadNotifications;

    public long getTotalPets() { return totalPets; }
    public void setTotalPets(long totalPets) { this.totalPets = totalPets; }
    public long getBoardedPets() { return boardedPets; }
    public void setBoardedPets(long boardedPets) { this.boardedPets = boardedPets; }
    public long getTotalBookings() { return totalBookings; }
    public void setTotalBookings(long totalBookings) { this.totalBookings = totalBookings; }
    public long getActiveBookings() { return activeBookings; }
    public void setActiveBookings(long activeBookings) { this.activeBookings = activeBookings; }
    public long getTotalUsers() { return totalUsers; }
    public void setTotalUsers(long totalUsers) { this.totalUsers = totalUsers; }
    public long getPendingFeedings() { return pendingFeedings; }
    public void setPendingFeedings(long pendingFeedings) { this.pendingFeedings = pendingFeedings; }
    public long getPendingMedications() { return pendingMedications; }
    public void setPendingMedications(long pendingMedications) { this.pendingMedications = pendingMedications; }
    public long getDueVaccinations() { return dueVaccinations; }
    public void setDueVaccinations(long dueVaccinations) { this.dueVaccinations = dueVaccinations; }
    public long getScheduledGroomings() { return scheduledGroomings; }
    public void setScheduledGroomings(long scheduledGroomings) { this.scheduledGroomings = scheduledGroomings; }
    public BigDecimal getPaidRevenue() { return paidRevenue; }
    public void setPaidRevenue(BigDecimal paidRevenue) { this.paidRevenue = paidRevenue; }
    public BigDecimal getPendingRevenue() { return pendingRevenue; }
    public void setPendingRevenue(BigDecimal pendingRevenue) { this.pendingRevenue = pendingRevenue; }
    public long getUnreadNotifications() { return unreadNotifications; }
    public void setUnreadNotifications(long unreadNotifications) { this.unreadNotifications = unreadNotifications; }
}
