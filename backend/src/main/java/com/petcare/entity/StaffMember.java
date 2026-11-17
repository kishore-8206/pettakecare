package com.petcare.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "staff")
public class StaffMember {

    @Id
    @Column(length = 64)
    private String id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(nullable = false, length = 120)
    private String department;

    @Column(name = "shift_timing", length = 80)
    private String shiftTiming;

    @Column(name = "assigned_zone", length = 80)
    private String assignedZone;

    @Column(nullable = false)
    private boolean active = true;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getShiftTiming() { return shiftTiming; }
    public void setShiftTiming(String shiftTiming) { this.shiftTiming = shiftTiming; }

    public String getAssignedZone() { return assignedZone; }
    public void setAssignedZone(String assignedZone) { this.assignedZone = assignedZone; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
