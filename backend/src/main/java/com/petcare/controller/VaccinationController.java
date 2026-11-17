package com.petcare.controller;

import com.petcare.dto.VaccinationDto;
import com.petcare.security.CustomUserDetails;
import com.petcare.service.VaccinationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/vaccinations")
public class VaccinationController {

    private final VaccinationService vaccinationService;

    public VaccinationController(VaccinationService vaccinationService) {
        this.vaccinationService = vaccinationService;
    }

    @GetMapping
    public ResponseEntity<List<VaccinationDto>> getVaccinations(@AuthenticationPrincipal CustomUserDetails principal) {
        return ResponseEntity.ok(vaccinationService.getVaccinations(principal.getUser()));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','DOCTOR','STAFF')")
    public ResponseEntity<VaccinationDto> createVaccination(
            @Valid @RequestBody VaccinationDto dto,
            @AuthenticationPrincipal CustomUserDetails principal
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(vaccinationService.createVaccination(dto, principal.getUser()));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','DOCTOR')")
    public ResponseEntity<VaccinationDto> updateVaccination(
            @PathVariable String id,
            @RequestBody VaccinationDto dto,
            @AuthenticationPrincipal CustomUserDetails principal
    ) {
        return ResponseEntity.ok(vaccinationService.updateVaccination(id, dto, principal.getUser()));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','DOCTOR')")
    public ResponseEntity<Map<String, Object>> deleteVaccination(@PathVariable String id) {
        vaccinationService.deleteVaccination(id);
        return ResponseEntity.ok(Map.of("deleted", true, "id", id));
    }
}
