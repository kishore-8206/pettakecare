package com.petcare.controller;

import com.petcare.dto.PetHealthCareDto;
import com.petcare.security.CustomUserDetails;
import com.petcare.service.PetHealthCareService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/health-care")
public class PetHealthCareController {

    private final PetHealthCareService petHealthCareService;

    public PetHealthCareController(PetHealthCareService petHealthCareService) {
        this.petHealthCareService = petHealthCareService;
    }

    @GetMapping
    public ResponseEntity<List<PetHealthCareDto>> getHealthCareSummaries(
            @AuthenticationPrincipal CustomUserDetails principal
    ) {
        return ResponseEntity.ok(petHealthCareService.getHealthCareSummaries(principal.getUser()));
    }
}
