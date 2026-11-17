package com.petcare.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class FeedingDto {
    private String id;
    @NotBlank
    private String petId;
    private String petName;
    @NotBlank
    private String mealType;
    @NotBlank
    private String scheduledTime;
    @NotBlank
    private String foodBrand;
    @NotNull
    private Integer portionGrams;
    private String dietaryRestrictions;
    @JsonProperty("isCompleted")
    private boolean isCompleted;
    private String completedAt;
    private String staffName;
    private String notes;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getPetId() { return petId; }
    public void setPetId(String petId) { this.petId = petId; }
    public String getPetName() { return petName; }
    public void setPetName(String petName) { this.petName = petName; }
    public String getMealType() { return mealType; }
    public void setMealType(String mealType) { this.mealType = mealType; }
    public String getScheduledTime() { return scheduledTime; }
    public void setScheduledTime(String scheduledTime) { this.scheduledTime = scheduledTime; }
    public String getFoodBrand() { return foodBrand; }
    public void setFoodBrand(String foodBrand) { this.foodBrand = foodBrand; }
    public Integer getPortionGrams() { return portionGrams; }
    public void setPortionGrams(Integer portionGrams) { this.portionGrams = portionGrams; }
    public String getDietaryRestrictions() { return dietaryRestrictions; }
    public void setDietaryRestrictions(String dietaryRestrictions) { this.dietaryRestrictions = dietaryRestrictions; }
    @JsonProperty("isCompleted")
    public boolean isCompleted() { return isCompleted; }
    @JsonProperty("isCompleted")
    public void setCompleted(boolean completed) { isCompleted = completed; }
    public String getCompletedAt() { return completedAt; }
    public void setCompletedAt(String completedAt) { this.completedAt = completedAt; }
    public String getStaffName() { return staffName; }
    public void setStaffName(String staffName) { this.staffName = staffName; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
