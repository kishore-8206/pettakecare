package com.petcare.controller;

import com.petcare.dto.BookingDto;
import com.petcare.security.CustomUserDetails;
import com.petcare.service.BookingService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @GetMapping
    public ResponseEntity<List<BookingDto>> getBookings(@AuthenticationPrincipal CustomUserDetails principal) {
        return ResponseEntity.ok(bookingService.getBookings(principal.getUser()));
    }

    @PostMapping
    public ResponseEntity<BookingDto> createBooking(
            @Valid @RequestBody BookingDto dto,
            @AuthenticationPrincipal CustomUserDetails principal
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(bookingService.createBooking(dto, principal.getUser()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BookingDto> updateBooking(
            @PathVariable String id,
            @RequestBody BookingDto dto,
            @AuthenticationPrincipal CustomUserDetails principal
    ) {
        return ResponseEntity.ok(bookingService.updateBooking(id, dto, principal.getUser()));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<BookingDto> patchBooking(
            @PathVariable String id,
            @RequestBody BookingDto dto,
            @AuthenticationPrincipal CustomUserDetails principal
    ) {
        return ResponseEntity.ok(bookingService.updateBooking(id, dto, principal.getUser()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteBooking(
            @PathVariable String id,
            @AuthenticationPrincipal CustomUserDetails principal
    ) {
        bookingService.deleteBooking(id, principal.getUser());
        return ResponseEntity.ok(Map.of("deleted", true, "id", id));
    }
}
