package com.petcare.repository;

import com.petcare.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, String> {
    List<Notification> findByTargetRoleOrTargetRoleOrderByCreatedAtDesc(String role1, String role2);
    List<Notification> findAllByOrderByCreatedAtDesc();
    boolean existsByTitle(String title);
}
