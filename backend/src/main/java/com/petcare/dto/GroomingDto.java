package com.petcare.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class GroomingDto {
    private String id;
    @NotBlank
    private String petId;
    private String petName;
    private String ownerName;
    private List<String> services = new ArrayList<>();
    @NotBlank
    private String appointmentDate;
    @NotBlank
    private String appointmentTime;
    private String groomerName;
    private String status;
    @NotNull
    private BigDecimal price;
    private String notes;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getPetId() { return petId; }
    public void setPetId(String petId) { this.petId = petId; }
    public String getPetName() { return petName; }
    public void setPetName(String petName) { this.petName = petName; }
    public String getOwnerName() { return ownerName; }
    public void setOwnerName(String ownerName) { this.ownerName = ownerName; }
    public List<String> getServices() { return services; }
    public void setServices(List<String> services) { this.services = services; }
    public String getAppointmentDate() { return appointmentDate; }
    public void setAppointmentDate(String appointmentDate) { this.appointmentDate = appointmentDate; }
    public String getAppointmentTime() { return appointmentTime; }
    public void setAppointmentTime(String appointmentTime) { this.appointmentTime = appointmentTime; }
    public String getGroomerName() { return groomerName; }
    public void setGroomerName(String groomerName) { this.groomerName = groomerName; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
