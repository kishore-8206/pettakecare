package com.petcare.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

public class MedicationDto {
    private String id;
    @NotBlank
    private String petId;
    private String petName;
    @NotBlank
    private String name;
    @NotBlank
    private String dosage;
    @NotBlank
    private String frequency;
    @NotBlank
    private String route;
    private String prescribedBy;
    @NotBlank
    private String startDate;
    @NotBlank
    private String endDate;
    @JsonProperty("isGivenToday")
    private boolean isGivenToday;
    private String lastAdministered;
    @NotBlank
    private String instructions;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getPetId() { return petId; }
    public void setPetId(String petId) { this.petId = petId; }
    public String getPetName() { return petName; }
    public void setPetName(String petName) { this.petName = petName; }
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
    public String getStartDate() { return startDate; }
    public void setStartDate(String startDate) { this.startDate = startDate; }
    public String getEndDate() { return endDate; }
    public void setEndDate(String endDate) { this.endDate = endDate; }
    @JsonProperty("isGivenToday")
    public boolean isGivenToday() { return isGivenToday; }
    @JsonProperty("isGivenToday")
    public void setGivenToday(boolean givenToday) { isGivenToday = givenToday; }
    public String getLastAdministered() { return lastAdministered; }
    public void setLastAdministered(String lastAdministered) { this.lastAdministered = lastAdministered; }
    public String getInstructions() { return instructions; }
    public void setInstructions(String instructions) { this.instructions = instructions; }
}
