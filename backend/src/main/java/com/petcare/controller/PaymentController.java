package com.petcare.controller;

import com.petcare.dto.PaymentDto;
import com.petcare.security.CustomUserDetails;
import com.petcare.service.InvoicePaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final InvoicePaymentService invoicePaymentService;

    public PaymentController(InvoicePaymentService invoicePaymentService) {
        this.invoicePaymentService = invoicePaymentService;
    }

    @GetMapping
    public ResponseEntity<List<PaymentDto>> getPayments(@AuthenticationPrincipal CustomUserDetails principal) {
        return ResponseEntity.ok(invoicePaymentService.getPayments(principal.getUser()));
    }
}
