package com.petcare.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class PetHealthCareDto {
    private String petId;
    private String petName;
    private String species;
    private String breed;
    private int age;
    private BigDecimal weight;
    @JsonProperty("isBoarded")
    private boolean isBoarded;
    private String roomNumber;
    private List<String> allergies = new ArrayList<>();
    private String medicalNotes;
    private String careStatus; // Healthy, Attention Needed, Under Treatment
    private String vaccinationStatus; // Up to Date, Due Soon, Overdue
    private int totalVaccinations;
    private int dueVaccinationsCount;
    private int activeMedicationsCount;
    private int medicationsGivenToday;
    private int totalFeedingsToday;
    private int completedFeedingsToday;
    private String latestDiagnosis;
    private String followUpDate;

    public String getPetId() { return petId; }
    public void setPetId(String petId) { this.petId = petId; }
    public String getPetName() { return petName; }
    public void setPetName(String petName) { this.petName = petName; }
    public String getSpecies() { return species; }
    public void setSpecies(String species) { this.species = species; }
    public String getBreed() { return breed; }
    public void setBreed(String breed) { this.breed = breed; }
    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }
    public BigDecimal getWeight() { return weight; }
    public void setWeight(BigDecimal weight) { this.weight = weight; }
    @JsonProperty("isBoarded")
    public boolean isBoarded() { return isBoarded; }
    @JsonProperty("isBoarded")
    public void setBoarded(boolean boarded) { isBoarded = boarded; }
    public String getRoomNumber() { return roomNumber; }
    public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }
    public List<String> getAllergies() { return allergies; }
    public void setAllergies(List<String> allergies) { this.allergies = allergies; }
    public String getMedicalNotes() { return medicalNotes; }
    public void setMedicalNotes(String medicalNotes) { this.medicalNotes = medicalNotes; }
    public String getCareStatus() { return careStatus; }
    public void setCareStatus(String careStatus) { this.careStatus = careStatus; }
    public String getVaccinationStatus() { return vaccinationStatus; }
    public void setVaccinationStatus(String vaccinationStatus) { this.vaccinationStatus = vaccinationStatus; }
    public int getTotalVaccinations() { return totalVaccinations; }
    public void setTotalVaccinations(int totalVaccinations) { this.totalVaccinations = totalVaccinations; }
    public int getDueVaccinationsCount() { return dueVaccinationsCount; }
    public void setDueVaccinationsCount(int dueVaccinationsCount) { this.dueVaccinationsCount = dueVaccinationsCount; }
    public int getActiveMedicationsCount() { return activeMedicationsCount; }
    public void setActiveMedicationsCount(int activeMedicationsCount) { this.activeMedicationsCount = activeMedicationsCount; }
    public int getMedicationsGivenToday() { return medicationsGivenToday; }
    public void setMedicationsGivenToday(int medicationsGivenToday) { this.medicationsGivenToday = medicationsGivenToday; }
    public int getTotalFeedingsToday() { return totalFeedingsToday; }
    public void setTotalFeedingsToday(int totalFeedingsToday) { this.totalFeedingsToday = totalFeedingsToday; }
    public int getCompletedFeedingsToday() { return completedFeedingsToday; }
    public void setCompletedFeedingsToday(int completedFeedingsToday) { this.completedFeedingsToday = completedFeedingsToday; }
    public String getLatestDiagnosis() { return latestDiagnosis; }
    public void setLatestDiagnosis(String latestDiagnosis) { this.latestDiagnosis = latestDiagnosis; }
    public String getFollowUpDate() { return followUpDate; }
    public void setFollowUpDate(String followUpDate) { this.followUpDate = followUpDate; }
}
