package com.petcare.repository;

import com.petcare.entity.GroomingAppointment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface GroomingAppointmentRepository extends JpaRepository<GroomingAppointment, String> {
    List<GroomingAppointment> findByPetId(String petId);
    List<GroomingAppointment> findByPetOwnerId(String ownerId);
}
