package com.petcare.service;

import com.petcare.dto.NotificationDto;
import com.petcare.entity.*;
import com.petcare.repository.FeedingScheduleRepository;
import com.petcare.repository.MedicationRepository;
import com.petcare.repository.NotificationRepository;
import com.petcare.repository.VaccinationRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final VaccinationRepository vaccinationRepository;
    private final MedicationRepository medicationRepository;
    private final FeedingScheduleRepository feedingRepository;

    public NotificationService(
            NotificationRepository notificationRepository,
            VaccinationRepository vaccinationRepository,
            MedicationRepository medicationRepository,
            FeedingScheduleRepository feedingRepository
    ) {
        this.notificationRepository = notificationRepository;
        this.vaccinationRepository = vaccinationRepository;
        this.medicationRepository = medicationRepository;
        this.feedingRepository = feedingRepository;
    }

    public List<NotificationDto> getNotifications(User currentUser) {
        if (currentUser.getRole() == Role.admin) {
            return notificationRepository.findAllByOrderByCreatedAtDesc().stream().map(this::toDto).toList();
        }
        String roleName = currentUser.getRole().name();
        return notificationRepository.findByTargetRoleOrTargetRoleOrderByCreatedAtDesc("all", roleName)
                .stream()
                .filter(n -> n.getTargetUser() == null || n.getTargetUser().getId().equals(currentUser.getId()))
                .map(this::toDto)
                .toList();
    }

    @Transactional
    public NotificationDto createNotification(NotificationDto dto) {
        Notification n = new Notification();
        n.setId("notif_" + UUID.randomUUID().toString().substring(0, 8));
        n.setTitle(dto.getTitle());
        n.setMessage(dto.getMessage());
        n.setType(dto.getType() != null ? dto.getType() : "info");
        n.setTargetRole(dto.getTargetRole() != null ? dto.getTargetRole() : "all");
        n.setTimestampLabel("Just now");
        n.setRead(false);
        n.setLink(dto.getLink());
        return toDto(notificationRepository.save(n));
    }

    @Transactional
    public void createSystemNotification(String title, String message, String type, String targetRole, User targetUser, String link) {
        if (notificationRepository.existsByTitle(title)) return;
        Notification n = new Notification();
        n.setId("notif_" + UUID.randomUUID().toString().substring(0, 8));
        n.setTitle(title);
        n.setMessage(message);
        n.setType(type);
        n.setTargetRole(targetRole);
        n.setTargetUser(targetUser);
        n.setTimestampLabel("Just now");
        n.setRead(false);
        n.setLink(link);
        notificationRepository.save(n);
    }

    @Transactional
    public void markRead(String id) {
        notificationRepository.findById(id).ifPresent(n -> {
            n.setRead(true);
            notificationRepository.save(n);
        });
    }

    @Transactional
    public void markAllRead(User currentUser) {
        List<Notification> list = currentUser.getRole() == Role.admin
                ? notificationRepository.findAll()
                : notificationRepository.findByTargetRoleOrTargetRoleOrderByCreatedAtDesc("all", currentUser.getRole().name());
        for (Notification n : list) {
            n.setRead(true);
        }
        notificationRepository.saveAll(list);
    }

    @Transactional
    public void deleteNotification(String id) {
        notificationRepository.deleteById(id);
    }

    /**
     * Scheduled automatic reminder job (runs daily at 08:00 AM) for due vaccinations,
     * pending medications, and pending feeding schedules.
     */
    @Scheduled(cron = "0 0 8 * * *")
    @Transactional
    public void generateAutomatedCareReminders() {
        LocalDate upcomingLimit = LocalDate.now().plusDays(14);
        List<Vaccination> dueVacs = vaccinationRepository.findByDueDateBetween(LocalDate.now().minusDays(30), upcomingLimit);
        for (Vaccination v : dueVacs) {
            String title = "Vaccine Reminder: " + v.getPet().getName() + " (" + v.getVaccineName() + ")";
            createSystemNotification(
                    title,
                    v.getVaccineName() + " is due on " + v.getDueDate() + " for " + v.getPet().getName() + ".",
                    "warning",
                    "owner",
                    v.getPet().getOwner(),
                    "vaccinations"
            );
        }

        long pendingMeds = medicationRepository.findAll().stream().filter(m -> !m.isGivenToday()).count();
        if (pendingMeds > 0) {
            createSystemNotification(
                    "Daily Medication Reminder (" + LocalDate.now() + ")",
                    pendingMeds + " scheduled pet medication doses remain pending today.",
                    "info",
                    "staff",
                    null,
                    "medications"
            );
        }

        long pendingMeals = feedingRepository.findAll().stream().filter(f -> !f.isCompleted()).count();
        if (pendingMeals > 0) {
            createSystemNotification(
                    "Feeding Round Reminder (" + LocalDate.now() + ")",
                    pendingMeals + " scheduled pet meals are pending completion.",
                    "info",
                    "staff",
                    null,
                    "feeding"
            );
        }
    }

    public NotificationDto toDto(Notification n) {
        NotificationDto dto = new NotificationDto();
        dto.setId(n.getId());
        dto.setTitle(n.getTitle());
        dto.setMessage(n.getMessage());
        dto.setType(n.getType());
        dto.setTargetRole(n.getTargetRole());
        dto.setTimestamp(n.getTimestampLabel() != null ? n.getTimestampLabel()
                : (n.getCreatedAt() != null ? n.getCreatedAt().toString() : "Just now"));
        dto.setRead(n.isRead());
        dto.setLink(n.getLink());
        return dto;
    }
}
