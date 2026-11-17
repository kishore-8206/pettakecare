package com.petcare.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "feeding_schedules")
public class FeedingSchedule {

    @Id
    @Column(length = 64)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pet_id", nullable = false)
    private Pet pet;

    @Column(name = "meal_type", nullable = false, length = 20)
    private String mealType; // Breakfast, Lunch, Dinner, Snack

    @Column(name = "scheduled_time", nullable = false, length = 30)
    private String scheduledTime;

    @Column(name = "food_brand", nullable = false, length = 160)
    private String foodBrand;

    @Column(name = "portion_grams", nullable = false)
    private int portionGrams;

    @Column(name = "dietary_restrictions", length = 255)
    private String dietaryRestrictions;

    @Column(name = "is_completed", nullable = false)
    private boolean isCompleted = false;

    @Column(name = "completed_at", length = 40)
    private String completedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "staff_id")
    private User staff;

    @Column(name = "staff_name", length = 120)
    private String staffName;

    @Column(columnDefinition = "TEXT")
    private String notes;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public Pet getPet() { return pet; }
    public void setPet(Pet pet) { this.pet = pet; }

    public String getMealType() { return mealType; }
    public void setMealType(String mealType) { this.mealType = mealType; }

    public String getScheduledTime() { return scheduledTime; }
    public void setScheduledTime(String scheduledTime) { this.scheduledTime = scheduledTime; }

    public String getFoodBrand() { return foodBrand; }
    public void setFoodBrand(String foodBrand) { this.foodBrand = foodBrand; }

    public int getPortionGrams() { return portionGrams; }
    public void setPortionGrams(int portionGrams) { this.portionGrams = portionGrams; }

    public String getDietaryRestrictions() { return dietaryRestrictions; }
    public void setDietaryRestrictions(String dietaryRestrictions) { this.dietaryRestrictions = dietaryRestrictions; }

    public boolean isCompleted() { return isCompleted; }
    public void setCompleted(boolean completed) { isCompleted = completed; }

    public String getCompletedAt() { return completedAt; }
    public void setCompletedAt(String completedAt) { this.completedAt = completedAt; }

    public User getStaff() { return staff; }
    public void setStaff(User staff) { this.staff = staff; }

    public String getStaffName() { return staffName; }
    public void setStaffName(String staffName) { this.staffName = staffName; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
