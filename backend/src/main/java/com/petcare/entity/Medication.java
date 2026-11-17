package com.petcare.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "medications")
public class Medication {

    @Id
    @Column(length = 64)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pet_id", nullable = false)
    private Pet pet;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id")
    private User doctor;

    @Column(nullable = false, length = 160)
    private String name;

    @Column(nullable = false, length = 100)
    private String dosage;

    @Column(nullable = false, length = 100)
    private String frequency;

    @Column(nullable = false, length = 30)
    private String route; // Oral, Topical, Injection, Eye Drops

    @Column(name = "prescribed_by", nullable = false, length = 120)
    private String prescribedBy;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "is_given_today", nullable = false)
    private boolean isGivenToday = false;

    @Column(name = "last_administered", length = 60)
    private String lastAdministered;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String instructions;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public Pet getPet() { return pet; }
    public void setPet(Pet pet) { this.pet = pet; }

    public User getDoctor() { return doctor; }
    public void setDoctor(User doctor) { this.doctor = doctor; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDosage() { return dosage; }
    public void setDosage(String dosage) { this.dosage = dosage; }

    public String getFrequency() { return frequency; }
    public void setFrequency(String frequency) { this.frequency = frequency; }

    public String getRoute() { return route; }
    public void setRoute(String route) { this.route = route; }

    public String getPrescribedBy() { return prescribedBy; }
    public void setPrescribedBy(String prescribedBy) { this.prescribedBy = prescribedBy; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public boolean isGivenToday() { return isGivenToday; }
    public void setGivenToday(boolean givenToday) { isGivenToday = givenToday; }

    public String getLastAdministered() { return lastAdministered; }
    public void setLastAdministered(String lastAdministered) { this.lastAdministered = lastAdministered; }

    public String getInstructions() { return instructions; }
    public void setInstructions(String instructions) { this.instructions = instructions; }
}
