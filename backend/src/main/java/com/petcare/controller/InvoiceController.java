package com.petcare.controller;

import com.petcare.dto.InvoiceDto;
import com.petcare.security.CustomUserDetails;
import com.petcare.service.InvoicePaymentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/invoices")
public class InvoiceController {

    private final InvoicePaymentService invoicePaymentService;

    public InvoiceController(InvoicePaymentService invoicePaymentService) {
        this.invoicePaymentService = invoicePaymentService;
    }

    @GetMapping
    public ResponseEntity<List<InvoiceDto>> getInvoices(@AuthenticationPrincipal CustomUserDetails principal) {
        return ResponseEntity.ok(invoicePaymentService.getInvoices(principal.getUser()));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ResponseEntity<InvoiceDto> createInvoice(
            @Valid @RequestBody InvoiceDto dto,
            @AuthenticationPrincipal CustomUserDetails principal
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(invoicePaymentService.createInvoice(dto, principal.getUser()));
    }

    @PostMapping("/{id}/pay")
    public ResponseEntity<InvoiceDto> payInvoice(
            @PathVariable String id,
            @RequestBody(required = false) Map<String, String> body,
            @AuthenticationPrincipal CustomUserDetails principal
    ) {
        String method = body != null ? body.get("paymentMethod") : "Credit Card";
        return ResponseEntity.ok(invoicePaymentService.markInvoicePaid(id, method, principal.getUser()));
    }
}
