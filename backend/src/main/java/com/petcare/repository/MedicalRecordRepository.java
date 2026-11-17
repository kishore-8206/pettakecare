package com.petcare.repository;

import com.petcare.entity.MedicalRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MedicalRecordRepository extends JpaRepository<MedicalRecord, String> {
    List<MedicalRecord> findByPetIdOrderByRecordDateDesc(String petId);
    List<MedicalRecord> findByPetOwnerIdOrderByRecordDateDesc(String ownerId);
}
