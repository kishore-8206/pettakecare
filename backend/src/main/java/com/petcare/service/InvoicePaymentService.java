package com.petcare.service;

import com.petcare.dto.InvoiceDto;
import com.petcare.dto.PaymentDto;
import com.petcare.entity.*;
import com.petcare.repository.InvoiceRepository;
import com.petcare.repository.PaymentRepository;
import com.petcare.repository.PetRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class InvoicePaymentService {

    private static final BigDecimal GST_RATE = new BigDecimal("0.18");

    private final InvoiceRepository invoiceRepository;
    private final PaymentRepository paymentRepository;
    private final PetRepository petRepository;

    public InvoicePaymentService(
            InvoiceRepository invoiceRepository,
            PaymentRepository paymentRepository,
            PetRepository petRepository
    ) {
        this.invoiceRepository = invoiceRepository;
        this.paymentRepository = paymentRepository;
        this.petRepository = petRepository;
    }

    public List<InvoiceDto> getInvoices(User currentUser) {
        List<Invoice> list = currentUser.getRole() == Role.owner
                ? invoiceRepository.findByOwnerId(currentUser.getId())
                : invoiceRepository.findAll();
        return list.stream().map(this::toDto).toList();
    }

    @Transactional
    public InvoiceDto createInvoice(InvoiceDto dto, User currentUser) {
        if (currentUser.getRole() == Role.owner || currentUser.getRole() == Role.doctor) {
            throw new AccessDeniedException("Permission Denied: Only Administrators and Care Staff can generate invoices.");
        }
        Pet pet = petRepository.findById(dto.getPetId())
                .orElseThrow(() -> new IllegalArgumentException("Pet not found: " + dto.getPetId()));

        Invoice inv = new Invoice();
        inv.setId("inv_" + UUID.randomUUID().toString().substring(0, 8));
        inv.setInvoiceNumber(dto.getInvoiceNumber() != null && !dto.getInvoiceNumber().isBlank()
                ? dto.getInvoiceNumber()
                : "INV-" + LocalDate.now().getYear() + "-" + (1000 + (int) (Math.random() * 9000)));
        inv.setPet(pet);
        inv.setOwner(pet.getOwner());
        inv.setInvoiceDate(dto.getDate() != null && !dto.getDate().isBlank() ? LocalDate.parse(dto.getDate()) : LocalDate.now());
        inv.setDueDate(LocalDate.parse(dto.getDueDate()));
        inv.setStatus("Pending");

        BigDecimal subtotal = BigDecimal.ZERO;
        for (InvoiceDto.InvoiceItemDto itemDto : dto.getItems()) {
            BigDecimal lineTotal = itemDto.getUnitPrice()
                    .multiply(BigDecimal.valueOf(itemDto.getQuantity()))
                    .setScale(2, RoundingMode.HALF_UP);
            InvoiceItem item = new InvoiceItem();
            item.setInvoice(inv);
            item.setDescription(itemDto.getDescription());
            item.setQuantity(itemDto.getQuantity());
            item.setUnitPrice(itemDto.getUnitPrice());
            item.setTotal(lineTotal);
            inv.getItems().add(item);
            subtotal = subtotal.add(lineTotal);
        }

        BigDecimal tax = subtotal.multiply(GST_RATE).setScale(2, RoundingMode.HALF_UP);
        BigDecimal total = subtotal.add(tax).setScale(2, RoundingMode.HALF_UP);

        inv.setSubtotal(subtotal);
        inv.setTax(tax);
        inv.setTotal(total);

        return toDto(invoiceRepository.save(inv));
    }

    @Transactional
    public InvoiceDto markInvoicePaid(String id, String paymentMethod, User currentUser) {
        Invoice inv = invoiceRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invoice not found: " + id));

        if (currentUser.getRole() == Role.owner && !inv.getOwner().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("Permission Denied: You cannot settle an invoice belonging to another owner.");
        }
        if ("Paid".equalsIgnoreCase(inv.getStatus())) {
            throw new IllegalStateException("This invoice has already been settled.");
        }

        String method = (paymentMethod != null && !paymentMethod.isBlank()) ? paymentMethod : "Credit Card";
        inv.setStatus("Paid");
        inv.setPaymentMethod(method);
        invoiceRepository.save(inv);

        Payment payment = new Payment();
        payment.setId("pay_" + UUID.randomUUID().toString().substring(0, 8));
        payment.setInvoice(inv);
        payment.setOwner(inv.getOwner());
        payment.setAmount(inv.getTotal());
        payment.setPaymentMethod(method);
        payment.setPaymentStatus("Completed");
        payment.setTransactionReference("TXN-" + System.currentTimeMillis());
        paymentRepository.save(payment);

        return toDto(inv);
    }

    public List<PaymentDto> getPayments(User currentUser) {
        List<Payment> list = currentUser.getRole() == Role.owner
                ? paymentRepository.findByOwnerIdOrderByPaidAtDesc(currentUser.getId())
                : paymentRepository.findAll();
        return list.stream().map(this::toPaymentDto).toList();
    }

    public InvoiceDto toDto(Invoice i) {
        InvoiceDto dto = new InvoiceDto();
        dto.setId(i.getId());
        dto.setInvoiceNumber(i.getInvoiceNumber());
        dto.setPetId(i.getPet().getId());
        dto.setPetName(i.getPet().getName());
        dto.setOwnerId(i.getOwner().getId());
        dto.setOwnerName(i.getOwner().getName());
        dto.setDate(i.getInvoiceDate().toString());
        dto.setDueDate(i.getDueDate().toString());
        dto.setSubtotal(i.getSubtotal());
        dto.setTax(i.getTax());
        dto.setTotal(i.getTotal());
        dto.setStatus(i.getStatus());
        dto.setPaymentMethod(i.getPaymentMethod());
        dto.setItems(i.getItems().stream().map(it -> {
            InvoiceDto.InvoiceItemDto itemDto = new InvoiceDto.InvoiceItemDto();
            itemDto.setDescription(it.getDescription());
            itemDto.setQuantity(it.getQuantity());
            itemDto.setUnitPrice(it.getUnitPrice());
            itemDto.setTotal(it.getTotal());
            return itemDto;
        }).toList());
        return dto;
    }

    public PaymentDto toPaymentDto(Payment p) {
        PaymentDto dto = new PaymentDto();
        dto.setId(p.getId());
        dto.setInvoiceId(p.getInvoice().getId());
        dto.setInvoiceNumber(p.getInvoice().getInvoiceNumber());
        dto.setOwnerId(p.getOwner().getId());
        dto.setOwnerName(p.getOwner().getName());
        dto.setAmount(p.getAmount());
        dto.setPaymentMethod(p.getPaymentMethod());
        dto.setPaymentStatus(p.getPaymentStatus());
        dto.setTransactionReference(p.getTransactionReference());
        dto.setPaidAt(p.getPaidAt() != null ? p.getPaidAt().toString() : null);
        return dto;
    }
}
