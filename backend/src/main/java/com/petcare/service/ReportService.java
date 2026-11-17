package com.petcare.service;

import com.petcare.dto.DashboardStatsDto;
import com.petcare.entity.*;
import com.petcare.repository.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

@Service
public class ReportService {

    private final PetRepository petRepository;
    private final BoardingBookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final FeedingScheduleRepository feedingRepository;
    private final MedicationRepository medicationRepository;
    private final VaccinationRepository vaccinationRepository;
    private final GroomingAppointmentRepository groomingRepository;
    private final InvoiceRepository invoiceRepository;
    private final NotificationRepository notificationRepository;

    public ReportService(
            PetRepository petRepository,
            BoardingBookingRepository bookingRepository,
            UserRepository userRepository,
            FeedingScheduleRepository feedingRepository,
            MedicationRepository medicationRepository,
            VaccinationRepository vaccinationRepository,
            GroomingAppointmentRepository groomingRepository,
            InvoiceRepository invoiceRepository,
            NotificationRepository notificationRepository
    ) {
        this.petRepository = petRepository;
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.feedingRepository = feedingRepository;
        this.medicationRepository = medicationRepository;
        this.vaccinationRepository = vaccinationRepository;
        this.groomingRepository = groomingRepository;
        this.invoiceRepository = invoiceRepository;
        this.notificationRepository = notificationRepository;
    }

    public DashboardStatsDto getDashboardStats(User currentUser) {
        boolean isOwner = currentUser.getRole() == Role.owner;
        List<Pet> pets = isOwner ? petRepository.findByOwnerId(currentUser.getId()) : petRepository.findAll();
        List<BoardingBooking> bookings = isOwner ? bookingRepository.findByOwnerId(currentUser.getId()) : bookingRepository.findAll();
        List<FeedingSchedule> feedings = isOwner ? feedingRepository.findByPetOwnerId(currentUser.getId()) : feedingRepository.findAll();
        List<Medication> medications = isOwner ? medicationRepository.findByPetOwnerId(currentUser.getId()) : medicationRepository.findAll();
        List<Vaccination> vaccinations = isOwner ? vaccinationRepository.findByPetOwnerId(currentUser.getId()) : vaccinationRepository.findAll();
        List<GroomingAppointment> groomings = isOwner ? groomingRepository.findByPetOwnerId(currentUser.getId()) : groomingRepository.findAll();
        List<Invoice> invoices = isOwner ? invoiceRepository.findByOwnerId(currentUser.getId()) : invoiceRepository.findAll();

        DashboardStatsDto dto = new DashboardStatsDto();
        dto.setTotalPets(pets.size());
        dto.setBoardedPets(pets.stream().filter(Pet::isBoarded).count());
        dto.setTotalBookings(bookings.size());
        dto.setActiveBookings(bookings.stream().filter(b -> "Checked-In".equalsIgnoreCase(b.getStatus()) || "Confirmed".equalsIgnoreCase(b.getStatus())).count());
        dto.setTotalUsers(isOwner ? 1 : userRepository.count());
        dto.setPendingFeedings(feedings.stream().filter(f -> !f.isCompleted()).count());
        dto.setPendingMedications(medications.stream().filter(m -> !m.isGivenToday()).count());
        dto.setDueVaccinations(vaccinations.stream().filter(v -> "Due Soon".equalsIgnoreCase(v.getStatus()) || "Overdue".equalsIgnoreCase(v.getStatus())).count());
        dto.setScheduledGroomings(groomings.stream().filter(g -> !"Completed".equalsIgnoreCase(g.getStatus())).count());
        dto.setPaidRevenue(invoices.stream().filter(i -> "Paid".equalsIgnoreCase(i.getStatus())).map(Invoice::getTotal).reduce(BigDecimal.ZERO, BigDecimal::add));
        dto.setPendingRevenue(invoices.stream().filter(i -> !"Paid".equalsIgnoreCase(i.getStatus())).map(Invoice::getTotal).reduce(BigDecimal.ZERO, BigDecimal::add));
        dto.setUnreadNotifications(notificationRepository.findAll().stream().filter(n -> !n.isRead()).count());
        return dto;
    }

    public byte[] exportMasterExcelWorkbook() throws IOException {
        try (Workbook wb = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            // Sheet 1: Pets
            Sheet petSheet = wb.createSheet("Pets");
            Row pHeader = petSheet.createRow(0);
            String[] pCols = {"ID", "Name", "Species", "Breed", "Age", "Gender", "Weight (kg)", "Owner", "Boarded", "Room"};
            for (int i = 0; i < pCols.length; i++) pHeader.createCell(i).setCellValue(pCols[i]);
            List<Pet> pets = petRepository.findAll();
            for (int r = 0; r < pets.size(); r++) {
                Pet p = pets.get(r);
                Row row = petSheet.createRow(r + 1);
                row.createCell(0).setCellValue(p.getId());
                row.createCell(1).setCellValue(p.getName());
                row.createCell(2).setCellValue(p.getSpecies());
                row.createCell(3).setCellValue(p.getBreed());
                row.createCell(4).setCellValue(p.getAge());
                row.createCell(5).setCellValue(p.getGender());
                row.createCell(6).setCellValue(p.getWeight().doubleValue());
                row.createCell(7).setCellValue(p.getOwnerName());
                row.createCell(8).setCellValue(p.isBoarded() ? "Yes" : "No");
                row.createCell(9).setCellValue(p.getRoomNumber() != null ? p.getRoomNumber() : "N/A");
            }

            // Sheet 2: Boarding Bookings
            Sheet bkSheet = wb.createSheet("Boarding Bookings");
            Row bHeader = bkSheet.createRow(0);
            String[] bCols = {"ID", "Pet", "Owner", "Room Type", "Room", "Check-In", "Check-Out", "Daily Rate (INR)", "Status"};
            for (int i = 0; i < bCols.length; i++) bHeader.createCell(i).setCellValue(bCols[i]);
            List<BoardingBooking> bookings = bookingRepository.findAll();
            for (int r = 0; r < bookings.size(); r++) {
                BoardingBooking b = bookings.get(r);
                Row row = bkSheet.createRow(r + 1);
                row.createCell(0).setCellValue(b.getId());
                row.createCell(1).setCellValue(b.getPet().getName());
                row.createCell(2).setCellValue(b.getOwner().getName());
                row.createCell(3).setCellValue(b.getRoomType());
                row.createCell(4).setCellValue(b.getRoomNumber());
                row.createCell(5).setCellValue(b.getCheckInDate().toString());
                row.createCell(6).setCellValue(b.getCheckOutDate().toString());
                row.createCell(7).setCellValue(b.getDailyRate().doubleValue());
                row.createCell(8).setCellValue(b.getStatus());
            }

            // Sheet 3: Vaccinations
            Sheet vacSheet = wb.createSheet("Vaccinations");
            Row vHeader = vacSheet.createRow(0);
            String[] vCols = {"ID", "Pet", "Vaccine", "Administered", "Due Date", "Batch", "Status", "Certificate"};
            for (int i = 0; i < vCols.length; i++) vHeader.createCell(i).setCellValue(vCols[i]);
            List<Vaccination> vacs = vaccinationRepository.findAll();
            for (int r = 0; r < vacs.size(); r++) {
                Vaccination v = vacs.get(r);
                Row row = vacSheet.createRow(r + 1);
                row.createCell(0).setCellValue(v.getId());
                row.createCell(1).setCellValue(v.getPet().getName());
                row.createCell(2).setCellValue(v.getVaccineName());
                row.createCell(3).setCellValue(v.getDateAdministered().toString());
                row.createCell(4).setCellValue(v.getDueDate().toString());
                row.createCell(5).setCellValue(v.getBatchNumber());
                row.createCell(6).setCellValue(v.getStatus());
                row.createCell(7).setCellValue(v.getCertificateId());
            }

            // Sheet 4: Invoices
            Sheet invSheet = wb.createSheet("Invoices");
            Row iHeader = invSheet.createRow(0);
            String[] iCols = {"Invoice No", "Pet", "Owner", "Date", "Due Date", "Subtotal", "GST (18%)", "Total (INR)", "Status", "Method"};
            for (int i = 0; i < iCols.length; i++) iHeader.createCell(i).setCellValue(iCols[i]);
            List<Invoice> invoices = invoiceRepository.findAll();
            for (int r = 0; r < invoices.size(); r++) {
                Invoice inv = invoices.get(r);
                Row row = invSheet.createRow(r + 1);
                row.createCell(0).setCellValue(inv.getInvoiceNumber());
                row.createCell(1).setCellValue(inv.getPet().getName());
                row.createCell(2).setCellValue(inv.getOwner().getName());
                row.createCell(3).setCellValue(inv.getInvoiceDate().toString());
                row.createCell(4).setCellValue(inv.getDueDate().toString());
                row.createCell(5).setCellValue(inv.getSubtotal().doubleValue());
                row.createCell(6).setCellValue(inv.getTax().doubleValue());
                row.createCell(7).setCellValue(inv.getTotal().doubleValue());
                row.createCell(8).setCellValue(inv.getStatus());
                row.createCell(9).setCellValue(inv.getPaymentMethod() != null ? inv.getPaymentMethod() : "Pending");
            }

            wb.write(out);
            return out.toByteArray();
        }
    }
}
