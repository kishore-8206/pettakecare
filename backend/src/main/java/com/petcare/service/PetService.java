package com.petcare.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.petcare.dto.PetDto;
import com.petcare.entity.Pet;
import com.petcare.entity.Role;
import com.petcare.entity.User;
import com.petcare.repository.PetRepository;
import com.petcare.repository.UserRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
public class PetService {

    private final PetRepository petRepository;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    public PetService(PetRepository petRepository, UserRepository userRepository, ObjectMapper objectMapper) {
        this.petRepository = petRepository;
        this.userRepository = userRepository;
        this.objectMapper = objectMapper;
    }

    public List<PetDto> getPets(User currentUser, String search, String species) {
        List<Pet> list = currentUser.getRole() == Role.owner
                ? petRepository.findByOwnerId(currentUser.getId())
                : petRepository.findAll();

        return list.stream()
                .filter(p -> search == null || search.isBlank()
                        || p.getName().toLowerCase().contains(search.trim().toLowerCase())
                        || p.getBreed().toLowerCase().contains(search.trim().toLowerCase())
                        || p.getOwnerName().toLowerCase().contains(search.trim().toLowerCase()))
                .filter(p -> species == null || species.isBlank() || "All".equalsIgnoreCase(species)
                        || p.getSpecies().equalsIgnoreCase(species))
                .map(this::toDto)
                .toList();
    }

    public PetDto getPetById(String id, User currentUser) {
        Pet pet = petRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Pet not found: " + id));
        if (currentUser.getRole() == Role.owner && !pet.getOwner().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("Permission Denied: You can only view your own registered pets.");
        }
        return toDto(pet);
    }

    @Transactional
    public PetDto createPet(PetDto dto, User currentUser) {
        User owner;
        if (currentUser.getRole() == Role.owner) {
            owner = currentUser;
        } else if (dto.getOwnerId() != null && !dto.getOwnerId().isBlank()) {
            owner = userRepository.findById(dto.getOwnerId())
                    .orElseThrow(() -> new IllegalArgumentException("Owner not found: " + dto.getOwnerId()));
        } else {
            owner = currentUser;
        }

        Pet pet = new Pet();
        pet.setId("pet_" + UUID.randomUUID().toString().substring(0, 8));
        pet.setName(dto.getName().trim());
        pet.setSpecies(dto.getSpecies());
        pet.setBreed(dto.getBreed().trim());
        pet.setAge(dto.getAge());
        pet.setGender(dto.getGender());
        pet.setWeight(dto.getWeight());
        pet.setOwner(owner);
        pet.setOwnerName(dto.getOwnerName() != null && !dto.getOwnerName().isBlank() ? dto.getOwnerName() : owner.getName());
        pet.setOwnerPhone(dto.getOwnerPhone() != null && !dto.getOwnerPhone().isBlank()
                ? dto.getOwnerPhone() : (owner.getPhone() != null ? owner.getPhone() : "N/A"));
        pet.setMicrochipId(dto.getMicrochipId());
        pet.setAvatarUrl(dto.getAvatarUrl());
        pet.setAllergiesJson(writeJsonList(dto.getAllergies()));
        pet.setMedicalNotes(dto.getMedicalNotes());
        pet.setBoarded(currentUser.getRole() != Role.owner && dto.isBoarded());
        pet.setRoomNumber(currentUser.getRole() != Role.owner ? dto.getRoomNumber() : null);

        return toDto(petRepository.save(pet));
    }

    @Transactional
    public PetDto updatePet(String id, PetDto dto, User currentUser) {
        Pet pet = petRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Pet not found: " + id));

        if (currentUser.getRole() == Role.owner && !pet.getOwner().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("Permission Denied: You cannot edit another owner's pet profile.");
        }

        if (dto.getName() != null) pet.setName(dto.getName().trim());
        if (dto.getSpecies() != null) pet.setSpecies(dto.getSpecies());
        if (dto.getBreed() != null) pet.setBreed(dto.getBreed().trim());
        if (dto.getAge() != null) pet.setAge(dto.getAge());
        if (dto.getGender() != null) pet.setGender(dto.getGender());
        if (dto.getWeight() != null) pet.setWeight(dto.getWeight());
        if (dto.getMicrochipId() != null) pet.setMicrochipId(dto.getMicrochipId());
        if (dto.getAvatarUrl() != null) pet.setAvatarUrl(dto.getAvatarUrl());
        if (dto.getAllergies() != null) pet.setAllergiesJson(writeJsonList(dto.getAllergies()));
        if (dto.getMedicalNotes() != null) pet.setMedicalNotes(dto.getMedicalNotes());
        if (currentUser.getRole() != Role.owner) {
            pet.setBoarded(dto.isBoarded());
            if (dto.getRoomNumber() != null) pet.setRoomNumber(dto.getRoomNumber());
        }

        return toDto(petRepository.save(pet));
    }

    @Transactional
    public void deletePet(String id, User currentUser) {
        Pet pet = petRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Pet not found: " + id));
        if (currentUser.getRole() == Role.owner && !pet.getOwner().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("Permission Denied: You cannot delete another owner's pet profile.");
        }
        petRepository.delete(pet);
    }

    public PetDto toDto(Pet p) {
        PetDto dto = new PetDto();
        dto.setId(p.getId());
        dto.setName(p.getName());
        dto.setSpecies(p.getSpecies());
        dto.setBreed(p.getBreed());
        dto.setAge(p.getAge());
        dto.setGender(p.getGender());
        dto.setWeight(p.getWeight());
        dto.setOwnerId(p.getOwner().getId());
        dto.setOwnerName(p.getOwnerName());
        dto.setOwnerPhone(p.getOwnerPhone());
        dto.setMicrochipId(p.getMicrochipId());
        dto.setAvatarUrl(p.getAvatarUrl());
        dto.setAllergies(readJsonList(p.getAllergiesJson()));
        dto.setMedicalNotes(p.getMedicalNotes());
        dto.setBoarded(p.isBoarded());
        dto.setRoomNumber(p.getRoomNumber());
        dto.setCreatedAt(p.getCreatedAt() != null ? p.getCreatedAt().toString() : null);
        return dto;
    }

    private String writeJsonList(List<String> items) {
        try {
            return objectMapper.writeValueAsString(items == null ? Collections.emptyList() : items);
        } catch (Exception e) {
            return "[]";
        }
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
