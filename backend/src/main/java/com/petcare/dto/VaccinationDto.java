package com.petcare.dto;

import jakarta.validation.constraints.NotBlank;

public class VaccinationDto {
    private String id;
    @NotBlank
    private String petId;
    private String petName;
    @NotBlank
    private String vaccineName;
    @NotBlank
    private String dateAdministered;
    @NotBlank
    private String dueDate;
    @NotBlank
    private String batchNumber;
    private String administeredBy;
    private String status;
    private String certificateId;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getPetId() { return petId; }
    public void setPetId(String petId) { this.petId = petId; }
    public String getPetName() { return petName; }
    public void setPetName(String petName) { this.petName = petName; }
    public String getVaccineName() { return vaccineName; }
    public void setVaccineName(String vaccineName) { this.vaccineName = vaccineName; }
    public String getDateAdministered() { return dateAdministered; }
    public void setDateAdministered(String dateAdministered) { this.dateAdministered = dateAdministered; }
    public String getDueDate() { return dueDate; }
    public void setDueDate(String dueDate) { this.dueDate = dueDate; }
    public String getBatchNumber() { return batchNumber; }
    public void setBatchNumber(String batchNumber) { this.batchNumber = batchNumber; }
    public String getAdministeredBy() { return administeredBy; }
    public void setAdministeredBy(String administeredBy) { this.administeredBy = administeredBy; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getCertificateId() { return certificateId; }
    public void setCertificateId(String certificateId) { this.certificateId = certificateId; }
}
