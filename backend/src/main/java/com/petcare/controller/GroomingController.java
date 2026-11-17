package com.petcare.controller;

import com.petcare.dto.GroomingDto;
import com.petcare.security.CustomUserDetails;
import com.petcare.service.GroomingService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/grooming")
public class GroomingController {

    private final GroomingService groomingService;

    public GroomingController(GroomingService groomingService) {
        this.groomingService = groomingService;
    }

    @GetMapping
    public ResponseEntity<List<GroomingDto>> getGroomings(@AuthenticationPrincipal CustomUserDetails principal) {
        return ResponseEntity.ok(groomingService.getGroomings(principal.getUser()));
    }

    @PostMapping
    public ResponseEntity<GroomingDto> createGrooming(
            @Valid @RequestBody GroomingDto dto,
            @AuthenticationPrincipal CustomUserDetails principal
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(groomingService.createGrooming(dto, principal.getUser()));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN','DOCTOR','STAFF')")
    public ResponseEntity<GroomingDto> updateStatus(
            @PathVariable String id,
            @RequestBody Map<String, String> body,
            @AuthenticationPrincipal CustomUserDetails principal
    ) {
        String status = body.getOrDefault("status", "Scheduled");
        return ResponseEntity.ok(groomingService.updateStatus(id, status, principal.getUser()));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ResponseEntity<Map<String, Object>> deleteGrooming(@PathVariable String id) {
        groomingService.deleteGrooming(id);
        return ResponseEntity.ok(Map.of("deleted", true, "id", id));
    }
}
