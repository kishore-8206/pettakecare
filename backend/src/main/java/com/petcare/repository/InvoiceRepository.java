package com.petcare.repository;

import com.petcare.entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface InvoiceRepository extends JpaRepository<Invoice, String> {
    List<Invoice> findByOwnerId(String ownerId);
    List<Invoice> findByPetId(String petId);
    List<Invoice> findByStatus(String status);
}
