package com.petcare.repository;

import com.petcare.entity.StaffMember;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface StaffMemberRepository extends JpaRepository<StaffMember, String> {
    Optional<StaffMember> findByUserId(String userId);
}
