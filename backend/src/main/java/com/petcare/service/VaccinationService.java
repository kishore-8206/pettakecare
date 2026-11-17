package com.petcare.service;

import com.petcare.dto.VaccinationDto;
import com.petcare.entity.Pet;
import com.petcare.entity.Role;
import com.petcare.entity.User;
import com.petcare.entity.Vaccination;
import com.petcare.repository.PetRepository;
import com.petcare.repository.VaccinationRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class VaccinationService {

    private final VaccinationRepository vaccinationRepository;
    private final PetRepository petRepository;

    public VaccinationService(VaccinationRepository vaccinationRepository, PetRepository petRepository) {
        this.vaccinationRepository = vaccinationRepository;
        this.petRepository = petRepository;
    }

    public List<VaccinationDto> getVaccinations(User currentUser) {
        List<Vaccination> list = currentUser.getRole() == Role.owner
                ? vaccinationRepository.findByPetOwnerId(currentUser.getId())
                : vaccinationRepository.findAll();
        return list.stream().map(this::toDto).toList();
    }

    @Transactional
    public VaccinationDto createVaccination(VaccinationDto dto, User currentUser) {
        if (currentUser.getRole() == Role.owner) {
            throw new AccessDeniedException("Permission Denied: Only veterinary doctors or administrators can add vaccination records.");
        }
        Pet pet = petRepository.findById(dto.getPetId())
                .orElseThrow(() -> new IllegalArgumentException("Pet not found: " + dto.getPetId()));

        Vaccination vac = new Vaccination();
        vac.setId("vac_" + UUID.randomUUID().toString().substring(0, 8));
        vac.setPet(pet);
        vac.setDoctor(currentUser.getRole() == Role.doctor ? currentUser : null);
        vac.setVaccineName(dto.getVaccineName());
        vac.setDateAdministered(LocalDate.parse(dto.getDateAdministered()));
        LocalDate dueDate = LocalDate.parse(dto.getDueDate());
        vac.setDueDate(dueDate);
        vac.setBatchNumber(dto.getBatchNumber());
        vac.setAdministeredBy(dto.getAdministeredBy() != null && !dto.getAdministeredBy().isBlank()
                ? dto.getAdministeredBy() : currentUser.getName());
        vac.setStatus(dto.getStatus() != null && !dto.getStatus().isBlank()
                ? dto.getStatus() : computeStatus(dueDate));
        vac.setCertificateId(dto.getCertificateId() != null && !dto.getCertificateId().isBlank()
                ? dto.getCertificateId()
                : "CERT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());

        return toDto(vaccinationRepository.save(vac));
    }

    @Transactional
    public VaccinationDto updateVaccination(String id, VaccinationDto dto, User currentUser) {
        if (currentUser.getRole() == Role.owner) {
            throw new AccessDeniedException("Permission Denied: Owners cannot modify vaccination schedules.");
        }
        Vaccination vac = vaccinationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Vaccination record not found: " + id));
        if (dto.getDueDate() != null) {
            LocalDate due = LocalDate.parse(dto.getDueDate());
            vac.setDueDate(due);
            vac.setStatus(dto.getStatus() != null ? dto.getStatus() : computeStatus(due));
        } else if (dto.getStatus() != null) {
            vac.setStatus(dto.getStatus());
        }
        if (dto.getVaccineName() != null) vac.setVaccineName(dto.getVaccineName());
        if (dto.getBatchNumber() != null) vac.setBatchNumber(dto.getBatchNumber());
        return toDto(vaccinationRepository.save(vac));
    }

    @Transactional
    public void deleteVaccination(String id) {
        vaccinationRepository.deleteById(id);
    }

    private String computeStatus(LocalDate dueDate) {
        LocalDate today = LocalDate.now();
        if (dueDate.isBefore(today)) return "Overdue";
        if (!dueDate.isAfter(today.plusDays(14))) return "Due Soon";
        return "Up to Date";
    }

    public VaccinationDto toDto(Vaccination v) {
        VaccinationDto dto = new VaccinationDto();
        dto.setId(v.getId());
        dto.setPetId(v.getPet().getId());
        dto.setPetName(v.getPet().getName());
        dto.setVaccineName(v.getVaccineName());
        dto.setDateAdministered(v.getDateAdministered().toString());
        dto.setDueDate(v.getDueDate().toString());
        dto.setBatchNumber(v.getBatchNumber());
        dto.setAdministeredBy(v.getAdministeredBy());
        dto.setStatus(v.getStatus());
        dto.setCertificateId(v.getCertificateId());
        return dto;
    }
}
