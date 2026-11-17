package com.petcare.service;

import com.petcare.dto.MedicalRecordDto;
import com.petcare.entity.MedicalRecord;
import com.petcare.entity.Pet;
import com.petcare.entity.Role;
import com.petcare.entity.User;
import com.petcare.repository.MedicalRecordRepository;
import com.petcare.repository.PetRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class MedicalRecordService {

    private final MedicalRecordRepository medicalRecordRepository;
    private final PetRepository petRepository;

    public MedicalRecordService(MedicalRecordRepository medicalRecordRepository, PetRepository petRepository) {
        this.medicalRecordRepository = medicalRecordRepository;
        this.petRepository = petRepository;
    }

    public List<MedicalRecordDto> getMedicalRecords(User currentUser, String petId) {
        if (currentUser.getRole() == Role.owner) {
            throw new AccessDeniedException("Permission Denied: Clinical medical records are restricted to veterinary & care personnel.");
        }
        List<MedicalRecord> records = (petId != null && !petId.isBlank())
                ? medicalRecordRepository.findByPetIdOrderByRecordDateDesc(petId)
                : medicalRecordRepository.findAll();
        return records.stream().map(this::toDto).toList();
    }

    @Transactional
    public MedicalRecordDto createMedicalRecord(MedicalRecordDto dto, User currentUser) {
        if (currentUser.getRole() == Role.owner || currentUser.getRole() == Role.staff) {
            throw new AccessDeniedException("Permission Denied: Only Veterinarians and Administrators may record clinical medical records.");
        }
        Pet pet = petRepository.findById(dto.getPetId())
                .orElseThrow(() -> new IllegalArgumentException("Pet not found: " + dto.getPetId()));

        MedicalRecord rec = new MedicalRecord();
        rec.setId("rec_" + UUID.randomUUID().toString().substring(0, 8));
        rec.setPet(pet);
        rec.setDoctor(currentUser);
        rec.setRecordDate(dto.getDate() != null && !dto.getDate().isBlank() ? LocalDate.parse(dto.getDate()) : LocalDate.now());
        rec.setDoctorName(dto.getDoctorName() != null && !dto.getDoctorName().isBlank() ? dto.getDoctorName() : currentUser.getName());
        rec.setDoctorEmail(dto.getDoctorEmail() != null && !dto.getDoctorEmail().isBlank() ? dto.getDoctorEmail() : currentUser.getEmail());
        rec.setDiagnosis(dto.getDiagnosis());
        rec.setSymptoms(dto.getSymptoms());
        rec.setTreatmentPlan(dto.getTreatmentPlan());
        if (dto.getFollowUpDate() != null && !dto.getFollowUpDate().isBlank()) {
            rec.setFollowUpDate(LocalDate.parse(dto.getFollowUpDate()));
        }
        if (dto.getVitals() != null) {
            rec.setTemperature(dto.getVitals().getTemperature());
            rec.setHeartRate(dto.getVitals().getHeartRate());
            rec.setRespiratoryRate(dto.getVitals().getRespiratoryRate());
            rec.setWeight(dto.getVitals().getWeight());
        }
        return toDto(medicalRecordRepository.save(rec));
    }

    @Transactional
    public void deleteMedicalRecord(String id) {
        medicalRecordRepository.deleteById(id);
    }

    public MedicalRecordDto toDto(MedicalRecord r) {
        MedicalRecordDto dto = new MedicalRecordDto();
        dto.setId(r.getId());
        dto.setPetId(r.getPet().getId());
        dto.setPetName(r.getPet().getName());
        dto.setDate(r.getRecordDate().toString());
        dto.setDoctorName(r.getDoctorName());
        dto.setDoctorEmail(r.getDoctorEmail());
        dto.setDiagnosis(r.getDiagnosis());
        dto.setSymptoms(r.getSymptoms());
        dto.setTreatmentPlan(r.getTreatmentPlan());
        dto.setFollowUpDate(r.getFollowUpDate() != null ? r.getFollowUpDate().toString() : null);

        MedicalRecordDto.VitalsDto vitals = new MedicalRecordDto.VitalsDto();
        vitals.setTemperature(r.getTemperature());
        vitals.setHeartRate(r.getHeartRate());
        vitals.setRespiratoryRate(r.getRespiratoryRate());
        vitals.setWeight(r.getWeight());
        dto.setVitals(vitals);
        return dto;
    }
}
