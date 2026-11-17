package com.petcare.controller;

import com.petcare.dto.PetDto;
import com.petcare.security.CustomUserDetails;
import com.petcare.service.PetService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/pets")
public class PetController {

    private final PetService petService;

    public PetController(PetService petService) {
        this.petService = petService;
    }

    @GetMapping
    public ResponseEntity<List<PetDto>> getPets(
            @AuthenticationPrincipal CustomUserDetails principal,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String species
    ) {
        return ResponseEntity.ok(petService.getPets(principal.getUser(), search, species));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PetDto> getPet(
            @PathVariable String id,
            @AuthenticationPrincipal CustomUserDetails principal
    ) {
        return ResponseEntity.ok(petService.getPetById(id, principal.getUser()));
    }

    @PostMapping
    public ResponseEntity<PetDto> createPet(
            @Valid @RequestBody PetDto dto,
            @AuthenticationPrincipal CustomUserDetails principal
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(petService.createPet(dto, principal.getUser()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PetDto> updatePet(
            @PathVariable String id,
            @RequestBody PetDto dto,
            @AuthenticationPrincipal CustomUserDetails principal
    ) {
        return ResponseEntity.ok(petService.updatePet(id, dto, principal.getUser()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deletePet(
            @PathVariable String id,
            @AuthenticationPrincipal CustomUserDetails principal
    ) {
        petService.deletePet(id, principal.getUser());
        return ResponseEntity.ok(Map.of("deleted", true, "id", id));
    }
}
