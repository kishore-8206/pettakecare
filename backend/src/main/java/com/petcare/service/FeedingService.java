package com.petcare.service;

import com.petcare.dto.FeedingDto;
import com.petcare.entity.FeedingSchedule;
import com.petcare.entity.Pet;
import com.petcare.entity.Role;
import com.petcare.entity.User;
import com.petcare.repository.FeedingScheduleRepository;
import com.petcare.repository.PetRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
public class FeedingService {

    private final FeedingScheduleRepository feedingRepository;
    private final PetRepository petRepository;

    public FeedingService(FeedingScheduleRepository feedingRepository, PetRepository petRepository) {
        this.feedingRepository = feedingRepository;
        this.petRepository = petRepository;
    }

    public List<FeedingDto> getFeedings(User currentUser) {
        List<FeedingSchedule> list = currentUser.getRole() == Role.owner
                ? feedingRepository.findByPetOwnerId(currentUser.getId())
                : feedingRepository.findAll();
        return list.stream().map(this::toDto).toList();
    }

    @Transactional
    public FeedingDto createFeeding(FeedingDto dto, User currentUser) {
        if (currentUser.getRole() == Role.owner) {
            throw new AccessDeniedException("Permission Denied: Pet Owners cannot create or edit feeding schedules.");
        }
        Pet pet = petRepository.findById(dto.getPetId())
                .orElseThrow(() -> new IllegalArgumentException("Pet not found: " + dto.getPetId()));

        FeedingSchedule fs = new FeedingSchedule();
        fs.setId("fd_" + UUID.randomUUID().toString().substring(0, 8));
        fs.setPet(pet);
        fs.setMealType(dto.getMealType());
        fs.setScheduledTime(dto.getScheduledTime());
        fs.setFoodBrand(dto.getFoodBrand());
        fs.setPortionGrams(dto.getPortionGrams());
        fs.setDietaryRestrictions(dto.getDietaryRestrictions());
        fs.setCompleted(false);
        fs.setNotes(dto.getNotes());
        return toDto(feedingRepository.save(fs));
    }

    @Transactional
    public FeedingDto toggleCompleted(String id, User currentUser) {
        if (currentUser.getRole() == Role.owner) {
            throw new AccessDeniedException("Permission Denied: Pet Owners cannot modify feeding schedules or mark meals as completed.");
        }
        FeedingSchedule fs = feedingRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Feeding schedule not found: " + id));
        boolean nextState = !fs.isCompleted();
        fs.setCompleted(nextState);
        if (nextState) {
            fs.setCompletedAt(LocalTime.now().format(DateTimeFormatter.ofPattern("hh:mm a")));
            fs.setStaff(currentUser);
            fs.setStaffName(currentUser.getName());
        } else {
            fs.setCompletedAt(null);
            fs.setStaff(null);
            fs.setStaffName(null);
        }
        return toDto(feedingRepository.save(fs));
    }

    @Transactional
    public void deleteFeeding(String id) {
        feedingRepository.deleteById(id);
    }

    public FeedingDto toDto(FeedingSchedule f) {
        FeedingDto dto = new FeedingDto();
        dto.setId(f.getId());
        dto.setPetId(f.getPet().getId());
        dto.setPetName(f.getPet().getName());
        dto.setMealType(f.getMealType());
        dto.setScheduledTime(f.getScheduledTime());
        dto.setFoodBrand(f.getFoodBrand());
        dto.setPortionGrams(f.getPortionGrams());
        dto.setDietaryRestrictions(f.getDietaryRestrictions());
        dto.setCompleted(f.isCompleted());
        dto.setCompletedAt(f.getCompletedAt());
        dto.setStaffName(f.getStaffName());
        dto.setNotes(f.getNotes());
        return dto;
    }
}
