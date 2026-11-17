package com.petcare.controller;

import com.petcare.dto.FeedingDto;
import com.petcare.security.CustomUserDetails;
import com.petcare.service.FeedingService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/feedings")
public class FeedingController {

    private final FeedingService feedingService;

    public FeedingController(FeedingService feedingService) {
        this.feedingService = feedingService;
    }

    @GetMapping
    public ResponseEntity<List<FeedingDto>> getFeedings(@AuthenticationPrincipal CustomUserDetails principal) {
        return ResponseEntity.ok(feedingService.getFeedings(principal.getUser()));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','DOCTOR','STAFF')")
    public ResponseEntity<FeedingDto> createFeeding(
            @Valid @RequestBody FeedingDto dto,
            @AuthenticationPrincipal CustomUserDetails principal
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(feedingService.createFeeding(dto, principal.getUser()));
    }

    @PatchMapping("/{id}/toggle")
    @PreAuthorize("hasAnyRole('ADMIN','DOCTOR','STAFF')")
    public ResponseEntity<FeedingDto> toggleCompleted(
            @PathVariable String id,
            @AuthenticationPrincipal CustomUserDetails principal
    ) {
        return ResponseEntity.ok(feedingService.toggleCompleted(id, principal.getUser()));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ResponseEntity<Map<String, Object>> deleteFeeding(@PathVariable String id) {
        feedingService.deleteFeeding(id);
        return ResponseEntity.ok(Map.of("deleted", true, "id", id));
    }
}
