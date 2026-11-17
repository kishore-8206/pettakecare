package com.petcare.controller;

import com.petcare.dto.DashboardStatsDto;
import com.petcare.security.CustomUserDetails;
import com.petcare.service.ReportService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/dashboard")
    public ResponseEntity<DashboardStatsDto> getDashboardStats(@AuthenticationPrincipal CustomUserDetails principal) {
        return ResponseEntity.ok(reportService.getDashboardStats(principal.getUser()));
    }

    @GetMapping("/export")
    @PreAuthorize("hasAnyRole('ADMIN','DOCTOR','STAFF')")
    public ResponseEntity<byte[]> exportExcel() throws IOException {
        byte[] bytes = reportService.exportMasterExcelWorkbook();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=PetCare_Master_Report.xlsx")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(bytes);
    }
}
