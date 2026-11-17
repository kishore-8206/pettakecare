package com.petcare.service;

import com.petcare.dto.MedicationDto;
import com.petcare.entity.Medication;
import com.petcare.entity.Pet;
import com.petcare.entity.Role;
import com.petcare.entity.User;
import com.petcare.repository.MedicationRepository;
import com.petcare.repository.PetRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
public class MedicationService {

    private final MedicationRepository medicationRepository;
    private final PetRepository petRepository;

    public MedicationService(MedicationRepository medicationRepository, PetRepository petRepository) {
        this.medicationRepository = medicationRepository;
        this.petRepository = petRepository;
    }

    public List<MedicationDto> getMedications(User currentUser) {
        List<Medication> list = currentUser.getRole() == Role.owner
                ? medicationRepository.findByPetOwnerId(currentUser.getId())
                : medicationRepository.findAll();
        return list.stream().map(this::toDto).toList();
    }

    @Transactional
    public MedicationDto createMedication(MedicationDto dto, User currentUser) {
        if (currentUser.getRole() == Role.owner) {
            throw new AccessDeniedException("Permission Denied: Pet Owners cannot prescribe or add medications.");
        }
        Pet pet = petRepository.findById(dto.getPetId())
                .orElseThrow(() -> new IllegalArgumentException("Pet not found: " + dto.getPetId()));

        Medication med = new Medication();
        med.setId("med_" + UUID.randomUUID().toString().substring(0, 8));
        med.setPet(pet);
        med.setDoctor(currentUser.getRole() == Role.doctor ? currentUser : null);
        med.setName(dto.getName());
        med.setDosage(dto.getDosage());
        med.setFrequency(dto.getFrequency());
        med.setRoute(dto.getRoute());
        med.setPrescribedBy(dto.getPrescribedBy() != null && !dto.getPrescribedBy().isBlank()
                ? dto.getPrescribedBy() : currentUser.getName());
        med.setStartDate(LocalDate.parse(dto.getStartDate()));
        med.setEndDate(LocalDate.parse(dto.getEndDate()));
        med.setGivenToday(false);
        med.setInstructions(dto.getInstructions());
        return toDto(medicationRepository.save(med));
    }

    @Transactional
    public MedicationDto toggleGiven(String id, User currentUser) {
        if (currentUser.getRole() == Role.owner) {
            throw new AccessDeniedException("Permission Denied: Pet Owners cannot edit medication or mark medication as administered.");
        }
        Medication med = medicationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Medication not found: " + id));
        boolean nextGiven = !med.isGivenToday();
        med.setGivenToday(nextGiven);
        med.setLastAdministered(nextGiven
                ? "Today, " + LocalTime.now().format(DateTimeFormatter.ofPattern("hh:mm a"))
                : null);
        return toDto(medicationRepository.save(med));
    }

    @Transactional
    public void deleteMedication(String id) {
        medicationRepository.deleteById(id);
    }

    public MedicationDto toDto(Medication m) {
        MedicationDto dto = new MedicationDto();
        dto.setId(m.getId());
        dto.setPetId(m.getPet().getId());
        dto.setPetName(m.getPet().getName());
        dto.setName(m.getName());
        dto.setDosage(m.getDosage());
        dto.setFrequency(m.getFrequency());
        dto.setRoute(m.getRoute());
        dto.setPrescribedBy(m.getPrescribedBy());
        dto.setStartDate(m.getStartDate().toString());
        dto.setEndDate(m.getEndDate().toString());
        dto.setGivenToday(m.isGivenToday());
        dto.setLastAdministered(m.getLastAdministered());
        dto.setInstructions(m.getInstructions());
        return dto;
    }
}
