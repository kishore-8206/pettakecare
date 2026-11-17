package com.petcare.repository;

import com.petcare.entity.PetOwner;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PetOwnerRepository extends JpaRepository<PetOwner, String> {
    Optional<PetOwner> findByUserId(String userId);
}
