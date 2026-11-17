package com.petcare.repository;

import com.petcare.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, String> {
    List<Payment> findByOwnerIdOrderByPaidAtDesc(String ownerId);
    List<Payment> findByInvoiceId(String invoiceId);
}
