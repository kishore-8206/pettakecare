package com.petcare.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.petcare.dto.PetHealthCareDto;
import com.petcare.entity.*;
import com.petcare.repository.*;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class PetHealthCareService {

    private final PetRepository petRepository;
    private final VaccinationRepository vaccinationRepository;
    private final MedicationRepository medicationRepository;
    private final FeedingScheduleRepository feedingRepository;
    private final MedicalRecordRepository medicalRecordRepository;
    private final ObjectMapper objectMapper;

    public PetHealthCareService(
            PetRepository petRepository,
            VaccinationRepository vaccinationRepository,
            MedicationRepository medicationRepository,
            FeedingScheduleRepository feedingRepository,
            MedicalRecordRepository medicalRecordRepository,
            ObjectMapper objectMapper
    ) {
        this.petRepository = petRepository;
        this.vaccinationRepository = vaccinationRepository;
        this.medicationRepository = medicationRepository;
        this.feedingRepository = feedingRepository;
        this.medicalRecordRepository = medicalRecordRepository;
        this.objectMapper = objectMapper;
    }

    public List<PetHealthCareDto> getHealthCareSummaries(User currentUser) {
        List<Pet> pets = currentUser.getRole() == Role.owner
                ? petRepository.findByOwnerId(currentUser.getId())
                : petRepository.findAll();

        return pets.stream().map(pet -> buildSummary(pet, currentUser)).toList();
    }

    private PetHealthCareDto buildSummary(Pet pet, User currentUser) {
        List<Vaccination> vacs = vaccinationRepository.findByPetId(pet.getId());
        long dueCount = vacs.stream()
                .filter(v -> "Due Soon".equalsIgnoreCase(v.getStatus()) || "Overdue".equalsIgnoreCase(v.getStatus()))
                .count();
        boolean hasOverdue = vacs.stream().anyMatch(v -> "Overdue".equalsIgnoreCase(v.getStatus()));
        String vacStatus = hasOverdue ? "Overdue" : (dueCount > 0 ? "Due Soon" : "Up to Date");

        List<Medication> meds = medicationRepository.findByPetId(pet.getId());
        long medsGiven = meds.stream().filter(Medication::isGivenToday).count();

        List<FeedingSchedule> feeds = feedingRepository.findByPetId(pet.getId());
        long feedsDone = feeds.stream().filter(FeedingSchedule::isCompleted).count();

        List<MedicalRecord> records = currentUser.getRole() == Role.owner
                ? Collections.emptyList()
                : medicalRecordRepository.findByPetIdOrderByRecordDateDesc(pet.getId());
        MedicalRecord latest = records.isEmpty() ? null : records.get(0);

        List<String> allergies = readJsonList(pet.getAllergiesJson());

        String careStatus;
        if (!meds.isEmpty() || latest != null) {
            careStatus = "Under Treatment";
        } else if (dueCount > 0 || !allergies.isEmpty()) {
            careStatus = "Attention Needed";
        } else {
            careStatus = "Healthy";
        }

        PetHealthCareDto dto = new PetHealthCareDto();
        dto.setPetId(pet.getId());
        dto.setPetName(pet.getName());
        dto.setSpecies(pet.getSpecies());
        dto.setBreed(pet.getBreed());
        dto.setAge(pet.getAge());
        dto.setWeight(pet.getWeight());
        dto.setBoarded(pet.isBoarded());
        dto.setRoomNumber(pet.getRoomNumber());
        dto.setAllergies(allergies);
        dto.setMedicalNotes(pet.getMedicalNotes());
        dto.setCareStatus(careStatus);
        dto.setVaccinationStatus(vacStatus);
        dto.setTotalVaccinations(vacs.size());
        dto.setDueVaccinationsCount((int) dueCount);
        dto.setActiveMedicationsCount(meds.size());
        dto.setMedicationsGivenToday((int) medsGiven);
        dto.setTotalFeedingsToday(feeds.size());
        dto.setCompletedFeedingsToday((int) feedsDone);
        if (latest != null) {
            dto.setLatestDiagnosis(latest.getDiagnosis());
            dto.setFollowUpDate(latest.getFollowUpDate() != null ? latest.getFollowUpDate().toString() : null);
        }
        return dto;
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
