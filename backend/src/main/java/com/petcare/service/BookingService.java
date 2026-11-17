package com.petcare.service;

import com.petcare.dto.BookingDto;
import com.petcare.entity.BoardingBooking;
import com.petcare.entity.Pet;
import com.petcare.entity.Role;
import com.petcare.entity.User;
import com.petcare.repository.BoardingBookingRepository;
import com.petcare.repository.PetRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class BookingService {

    private final BoardingBookingRepository bookingRepository;
    private final PetRepository petRepository;
    private final NotificationService notificationService;

    public BookingService(
            BoardingBookingRepository bookingRepository,
            PetRepository petRepository,
            NotificationService notificationService
    ) {
        this.bookingRepository = bookingRepository;
        this.petRepository = petRepository;
        this.notificationService = notificationService;
    }

    public List<BookingDto> getBookings(User currentUser) {
        List<BoardingBooking> list = currentUser.getRole() == Role.owner
                ? bookingRepository.findByOwnerId(currentUser.getId())
                : bookingRepository.findAll();
        return list.stream().map(this::toDto).toList();
    }

    @Transactional
    public BookingDto createBooking(BookingDto dto, User currentUser) {
        Pet pet = petRepository.findById(dto.getPetId())
                .orElseThrow(() -> new IllegalArgumentException("Pet not found: " + dto.getPetId()));

        if (currentUser.getRole() == Role.owner && !pet.getOwner().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("Permission Denied: You can only book stays for your own registered pets.");
        }

        BoardingBooking booking = new BoardingBooking();
        booking.setId("bk_" + UUID.randomUUID().toString().substring(0, 8));
        booking.setPet(pet);
        booking.setOwner(pet.getOwner());
        booking.setRoomType(dto.getRoomType());
        booking.setRoomNumber(dto.getRoomNumber());
        booking.setCheckInDate(LocalDate.parse(dto.getCheckInDate()));
        booking.setCheckOutDate(LocalDate.parse(dto.getCheckOutDate()));
        booking.setStatus(currentUser.getRole() == Role.owner ? "Confirmed" : (dto.getStatus() != null ? dto.getStatus() : "Confirmed"));
        booking.setDailyRate(dto.getDailyRate());
        booking.setSpecialInstructions(dto.getSpecialInstructions());
        booking.setEmergencyContact(dto.getEmergencyContact());

        if ("Checked-In".equalsIgnoreCase(booking.getStatus())) {
            pet.setBoarded(true);
            pet.setRoomNumber(booking.getRoomNumber());
            petRepository.save(pet);
        }

        BoardingBooking saved = bookingRepository.save(booking);
        notificationService.createSystemNotification(
                "Boarding Booking Confirmed: " + pet.getName(),
                pet.getName() + " booked in " + booking.getRoomType() + " (" + booking.getRoomNumber() + ") from " + booking.getCheckInDate() + ".",
                "success",
                "owner",
                pet.getOwner(),
                "boarding"
        );
        return toDto(saved);
    }

    @Transactional
    public BookingDto updateBooking(String id, BookingDto dto, User currentUser) {
        BoardingBooking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found: " + id));

        if (currentUser.getRole() == Role.owner) {
            if (dto.getStatus() != null && !dto.getStatus().equals(booking.getStatus())) {
                throw new AccessDeniedException("Permission Denied: Pet Owners are not permitted to change booking or boarding status.");
            }
            if (!booking.getOwner().getId().equals(currentUser.getId())) {
                throw new AccessDeniedException("Permission Denied: You cannot modify another owner's booking.");
            }
        }

        if (dto.getRoomType() != null) booking.setRoomType(dto.getRoomType());
        if (dto.getRoomNumber() != null) booking.setRoomNumber(dto.getRoomNumber());
        if (dto.getCheckInDate() != null) booking.setCheckInDate(LocalDate.parse(dto.getCheckInDate()));
        if (dto.getCheckOutDate() != null) booking.setCheckOutDate(LocalDate.parse(dto.getCheckOutDate()));
        if (dto.getSpecialInstructions() != null) booking.setSpecialInstructions(dto.getSpecialInstructions());
        if (dto.getEmergencyContact() != null) booking.setEmergencyContact(dto.getEmergencyContact());

        if (currentUser.getRole() != Role.owner && dto.getStatus() != null) {
            booking.setStatus(dto.getStatus());
            Pet pet = booking.getPet();
            if ("Checked-In".equalsIgnoreCase(dto.getStatus())) {
                pet.setBoarded(true);
                pet.setRoomNumber(booking.getRoomNumber());
                booking.setAssignedStaff(currentUser.getRole() == Role.staff ? currentUser : booking.getAssignedStaff());
            } else if ("Completed".equalsIgnoreCase(dto.getStatus()) || "Cancelled".equalsIgnoreCase(dto.getStatus())) {
                pet.setBoarded(false);
                pet.setRoomNumber(null);
            }
            petRepository.save(pet);
        }

        return toDto(bookingRepository.save(booking));
    }

    @Transactional
    public void deleteBooking(String id, User currentUser) {
        BoardingBooking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found: " + id));
        if (currentUser.getRole() == Role.owner && !booking.getOwner().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("Permission Denied: You cannot delete another owner's booking.");
        }
        bookingRepository.delete(booking);
    }

    public BookingDto toDto(BoardingBooking b) {
        BookingDto dto = new BookingDto();
        dto.setId(b.getId());
        dto.setPetId(b.getPet().getId());
        dto.setPetName(b.getPet().getName());
        dto.setOwnerId(b.getOwner().getId());
        dto.setOwnerName(b.getOwner().getName());
        dto.setRoomType(b.getRoomType());
        dto.setRoomNumber(b.getRoomNumber());
        dto.setCheckInDate(b.getCheckInDate().toString());
        dto.setCheckOutDate(b.getCheckOutDate().toString());
        dto.setStatus(b.getStatus());
        dto.setDailyRate(b.getDailyRate());
        dto.setSpecialInstructions(b.getSpecialInstructions());
        dto.setEmergencyContact(b.getEmergencyContact());
        return dto;
    }
}
