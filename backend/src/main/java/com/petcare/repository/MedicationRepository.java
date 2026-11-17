package com.petcare.repository;

import com.petcare.entity.Medication;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MedicationRepository extends JpaRepository<Medication, String> {
    List<Medication> findByPetId(String petId);
    List<Medication> findByPetOwnerId(String ownerId);
}
