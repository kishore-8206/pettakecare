package com.petcare.repository;

import com.petcare.entity.FeedingSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface FeedingScheduleRepository extends JpaRepository<FeedingSchedule, String> {
    List<FeedingSchedule> findByPetId(String petId);
    List<FeedingSchedule> findByPetOwnerId(String ownerId);
}
