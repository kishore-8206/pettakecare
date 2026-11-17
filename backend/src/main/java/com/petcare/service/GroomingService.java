package com.petcare.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.petcare.dto.GroomingDto;
import com.petcare.entity.GroomingAppointment;
import com.petcare.entity.Pet;
import com.petcare.entity.Role;
import com.petcare.entity.User;
import com.petcare.repository.GroomingAppointmentRepository;
import com.petcare.repository.PetRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
public class GroomingService {

    private final GroomingAppointmentRepository groomingRepository;
    private final PetRepository petRepository;
    private final ObjectMapper objectMapper;
    private final NotificationService notificationService;

    public GroomingService(
            GroomingAppointmentRepository groomingRepository,
            PetRepository petRepository,
            ObjectMapper objectMapper,
            NotificationService notificationService
    ) {
        this.groomingRepository = groomingRepository;
        this.petRepository = petRepository;
        this.objectMapper = objectMapper;
        this.notificationService = notificationService;
    }

    public List<GroomingDto> getGroomings(User currentUser) {
        List<GroomingAppointment> list = currentUser.getRole() == Role.owner
                ? groomingRepository.findByPetOwnerId(currentUser.getId())
                : groomingRepository.findAll();
        return list.stream().map(this::toDto).toList();
    }

    @Transactional
    public GroomingDto createGrooming(GroomingDto dto, User currentUser) {
        Pet pet = petRepository.findById(dto.getPetId())
                .orElseThrow(() -> new IllegalArgumentException("Pet not found: " + dto.getPetId()));
        if (currentUser.getRole() == Role.owner && !pet.getOwner().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("Permission Denied: You can only book grooming appointments for your own pets.");
        }

        GroomingAppointment g = new GroomingAppointment();
        g.setId("grm_" + UUID.randomUUID().toString().substring(0, 8));
        g.setPet(pet);
        g.setServicesJson(writeJsonList(dto.getServices()));
        g.setAppointmentDate(LocalDate.parse(dto.getAppointmentDate()));
        g.setAppointmentTime(dto.getAppointmentTime());
        g.setGroomerName(dto.getGroomerName() != null && !dto.getGroomerName().isBlank()
                ? dto.getGroomerName() : "Elena Rostova");
        g.setStatus(currentUser.getRole() == Role.owner ? "Scheduled" : (dto.getStatus() != null ? dto.getStatus() : "Scheduled"));
        g.setPrice(dto.getPrice());
        g.setNotes(dto.getNotes());

        GroomingAppointment saved = groomingRepository.save(g);
        notificationService.createSystemNotification(
                "Grooming Appointment Scheduled: " + pet.getName(),
                pet.getName() + " scheduled for grooming on " + g.getAppointmentDate() + " at " + g.getAppointmentTime() + ".",
                "info",
                "owner",
                pet.getOwner(),
                "grooming"
        );
        return toDto(saved);
    }

    @Transactional
    public GroomingDto updateStatus(String id, String status, User currentUser) {
        if (currentUser.getRole() == Role.owner) {
            throw new AccessDeniedException("Permission Denied: Pet Owners cannot alter grooming salon appointment statuses.");
        }
        GroomingAppointment g = groomingRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Grooming appointment not found: " + id));
        g.setStatus(status);
        if (currentUser.getRole() == Role.staff) {
            g.setStaff(currentUser);
        }
        return toDto(groomingRepository.save(g));
    }

    @Transactional
    public void deleteGrooming(String id) {
        groomingRepository.deleteById(id);
    }

    public GroomingDto toDto(GroomingAppointment g) {
        GroomingDto dto = new GroomingDto();
        dto.setId(g.getId());
        dto.setPetId(g.getPet().getId());
        dto.setPetName(g.getPet().getName());
        dto.setOwnerName(g.getPet().getOwnerName());
        dto.setServices(readJsonList(g.getServicesJson()));
        dto.setAppointmentDate(g.getAppointmentDate().toString());
        dto.setAppointmentTime(g.getAppointmentTime());
        dto.setGroomerName(g.getGroomerName());
        dto.setStatus(g.getStatus());
        dto.setPrice(g.getPrice());
        dto.setNotes(g.getNotes());
        return dto;
    }

    private String writeJsonList(List<String> items) {
        try {
            return objectMapper.writeValueAsString(items == null ? Collections.emptyList() : items);
        } catch (Exception e) {
            return "[]";
        }
    }

    private List<String> readJsonList(String json) {
        if (json == null || json.isBlank()) return Collections.emptyList();
        try {
            return objectMapper.readValue(json, new TypeReference<>() {});
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }
}
