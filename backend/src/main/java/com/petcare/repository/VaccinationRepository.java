package com.petcare.repository;

import com.petcare.entity.Vaccination;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;

public interface VaccinationRepository extends JpaRepository<Vaccination, String> {
    List<Vaccination> findByPetId(String petId);
    List<Vaccination> findByPetOwnerId(String ownerId);
    List<Vaccination> findByDueDateBetween(LocalDate start, LocalDate end);
    List<Vaccination> findByDueDateBefore(LocalDate date);
}
