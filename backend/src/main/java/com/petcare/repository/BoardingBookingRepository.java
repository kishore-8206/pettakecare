package com.petcare.repository;

import com.petcare.entity.BoardingBooking;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BoardingBookingRepository extends JpaRepository<BoardingBooking, String> {
    List<BoardingBooking> findByOwnerId(String ownerId);
    List<BoardingBooking> findByPetId(String petId);
    List<BoardingBooking> findByStatus(String status);
}
