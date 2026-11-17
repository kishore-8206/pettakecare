package com.petcare.controller;

import com.petcare.dto.MedicationDto;
import com.petcare.security.CustomUserDetails;
import com.petcare.service.MedicationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/medications")
public class MedicationController {

    private final MedicationService medicationService;

    public MedicationController(MedicationService medicationService) {
        this.medicationService = medicationService;
    }

    @GetMapping
    public ResponseEntity<List<MedicationDto>> getMedications(@AuthenticationPrincipal CustomUserDetails principal) {
        return ResponseEntity.ok(medicationService.getMedications(principal.getUser()));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','DOCTOR','STAFF')")
    public ResponseEntity<MedicationDto> createMedication(
            @Valid @RequestBody MedicationDto dto,
            @AuthenticationPrincipal CustomUserDetails principal
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(medicationService.createMedication(dto, principal.getUser()));
    }

    @PatchMapping("/{id}/toggle")
    @PreAuthorize("hasAnyRole('ADMIN','DOCTOR','STAFF')")
    public ResponseEntity<MedicationDto> toggleGiven(
            @PathVariable String id,
            @AuthenticationPrincipal CustomUserDetails principal
    ) {
        return ResponseEntity.ok(medicationService.toggleGiven(id, principal.getUser()));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','DOCTOR')")
    public ResponseEntity<Map<String, Object>> deleteMedication(@PathVariable String id) {
        medicationService.deleteMedication(id);
        return ResponseEntity.ok(Map.of("deleted", true, "id", id));
    }
}
