package com.petcare.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "pets")
public class Pet {

    @Id
    @Column(length = 64)
    private String id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 20)
    private String species; // Dog, Cat, Bird, Rabbit, Other

    @Column(nullable = false, length = 120)
    private String breed;

    @Column(nullable = false)
    private int age;

    @Column(nullable = false, length = 10)
    private String gender; // Male, Female

    @Column(nullable = false, precision = 6, scale = 2)
    private BigDecimal weight;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @Column(name = "owner_name", nullable = false, length = 120)
    private String ownerName;

    @Column(name = "owner_phone", nullable = false, length = 30)
    private String ownerPhone;

    @Column(name = "microchip_id", unique = true, length = 80)
    private String microchipId;

    @Column(name = "avatar_url", length = 500)
    private String avatarUrl;

    @Column(name = "allergies_json", columnDefinition = "TEXT")
    private String allergiesJson;

    @Column(name = "medical_notes", columnDefinition = "TEXT")
    private String medicalNotes;

    @Column(name = "is_boarded", nullable = false)
    private boolean isBoarded = false;

    @Column(name = "room_number", length = 60)
    private String roomNumber;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSpecies() { return species; }
    public void setSpecies(String species) { this.species = species; }

    public String getBreed() { return breed; }
    public void setBreed(String breed) { this.breed = breed; }

    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public BigDecimal getWeight() { return weight; }
    public void setWeight(BigDecimal weight) { this.weight = weight; }

    public User getOwner() { return owner; }
    public void setOwner(User owner) { this.owner = owner; }

    public String getOwnerName() { return ownerName; }
    public void setOwnerName(String ownerName) { this.ownerName = ownerName; }

    public String getOwnerPhone() { return ownerPhone; }
    public void setOwnerPhone(String ownerPhone) { this.ownerPhone = ownerPhone; }

    public String getMicrochipId() { return microchipId; }
    public void setMicrochipId(String microchipId) { this.microchipId = microchipId; }

    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }

    public String getAllergiesJson() { return allergiesJson; }
    public void setAllergiesJson(String allergiesJson) { this.allergiesJson = allergiesJson; }

    public String getMedicalNotes() { return medicalNotes; }
    public void setMedicalNotes(String medicalNotes) { this.medicalNotes = medicalNotes; }

    public boolean isBoarded() { return isBoarded; }
    public void setBoarded(boolean boarded) { isBoarded = boarded; }

    public String getRoomNumber() { return roomNumber; }
    public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
