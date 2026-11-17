package com.petcare.repository;

import com.petcare.entity.Pet;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PetRepository extends JpaRepository<Pet, String> {
    List<Pet> findByOwnerId(String ownerId);
    List<Pet> findByIsBoardedTrue();
    List<Pet> findByNameContainingIgnoreCaseOrBreedContainingIgnoreCase(String name, String breed);
}
