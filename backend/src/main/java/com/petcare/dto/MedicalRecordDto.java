package com.petcare.dto;

import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;

public class MedicalRecordDto {
    private String id;
    @NotBlank
    private String petId;
    private String petName;
    private String date;
    private String doctorName;
    private String doctorEmail;
    @NotBlank
    private String diagnosis;
    @NotBlank
    private String symptoms;
    private VitalsDto vitals;
    @NotBlank
    private String treatmentPlan;
    private String followUpDate;

    public static class VitalsDto {
        private BigDecimal temperature;
        private Integer heartRate;
        private Integer respiratoryRate;
        private BigDecimal weight;

        public BigDecimal getTemperature() { return temperature; }
        public void setTemperature(BigDecimal temperature) { this.temperature = temperature; }
        public Integer getHeartRate() { return heartRate; }
        public void setHeartRate(Integer heartRate) { this.heartRate = heartRate; }
        public Integer getRespiratoryRate() { return respiratoryRate; }
        public void setRespiratoryRate(Integer respiratoryRate) { this.respiratoryRate = respiratoryRate; }
        public BigDecimal getWeight() { return weight; }
        public void setWeight(BigDecimal weight) { this.weight = weight; }
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getPetId() { return petId; }
    public void setPetId(String petId) { this.petId = petId; }
    public String getPetName() { return petName; }
    public void setPetName(String petName) { this.petName = petName; }
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
    public String getDoctorName() { return doctorName; }
    public void setDoctorName(String doctorName) { this.doctorName = doctorName; }
    public String getDoctorEmail() { return doctorEmail; }
    public void setDoctorEmail(String doctorEmail) { this.doctorEmail = doctorEmail; }
    public String getDiagnosis() { return diagnosis; }
    public void setDiagnosis(String diagnosis) { this.diagnosis = diagnosis; }
    public String getSymptoms() { return symptoms; }
    public void setSymptoms(String symptoms) { this.symptoms = symptoms; }
    public VitalsDto getVitals() { return vitals; }
    public void setVitals(VitalsDto vitals) { this.vitals = vitals; }
    public String getTreatmentPlan() { return treatmentPlan; }
    public void setTreatmentPlan(String treatmentPlan) { this.treatmentPlan = treatmentPlan; }
    public String getFollowUpDate() { return followUpDate; }
    public void setFollowUpDate(String followUpDate) { this.followUpDate = followUpDate; }
}
