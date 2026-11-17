package com.petcare.service;

import com.petcare.dto.AuthDtos.CreateUserRequest;
import com.petcare.dto.AuthDtos.UserDto;
import com.petcare.entity.*;
import com.petcare.repository.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PetOwnerRepository petOwnerRepository;
    private final DoctorRepository doctorRepository;
    private final StaffMemberRepository staffMemberRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthService authService;

    public UserService(
            UserRepository userRepository,
            PetOwnerRepository petOwnerRepository,
            DoctorRepository doctorRepository,
            StaffMemberRepository staffMemberRepository,
            PasswordEncoder passwordEncoder,
            AuthService authService
    ) {
        this.userRepository = userRepository;
        this.petOwnerRepository = petOwnerRepository;
        this.doctorRepository = doctorRepository;
        this.staffMemberRepository = staffMemberRepository;
        this.passwordEncoder = passwordEncoder;
        this.authService = authService;
    }

    public List<UserDto> getAllUsers(String search, Role roleFilter) {
        List<User> users;
        if (search != null && !search.isBlank()) {
            users = userRepository.findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase(search.trim(), search.trim());
        } else if (roleFilter != null) {
            users = userRepository.findByRole(roleFilter);
        } else {
            users = userRepository.findAll();
        }
        return users.stream()
                .filter(u -> roleFilter == null || u.getRole() == roleFilter)
                .map(authService::toDto)
                .toList();
    }

    public UserDto getUserById(String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + id));
        return authService.toDto(user);
    }

    @Transactional
    public UserDto createUser(CreateUserRequest req) {
        String email = req.getEmail().trim().toLowerCase();
        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new IllegalStateException("An account with this email already exists: " + email);
        }

        Role role = req.getRole() != null ? req.getRole() : Role.owner;
        User user = new User();
        user.setId("usr_" + role.name() + "_" + UUID.randomUUID().toString().substring(0, 8));
        user.setName(req.getName().trim());
        user.setEmail(email);
        user.setPhone(req.getPhone());
        user.setRole(role);
        user.setDepartment(req.getDepartment());
        user.setSpecialization(req.getSpecialization());
        user.setAvatar(req.getAvatar());
        user.setPasswordHash(passwordEncoder.encode(req.getPassword() != null ? req.getPassword() : "password123"));
        user.setActive(true);
        userRepository.save(user);

        if (role == Role.doctor) {
            Doctor doctor = new Doctor();
            doctor.setId("doc_" + UUID.randomUUID().toString().substring(0, 8));
            doctor.setUser(user);
            doctor.setSpecialization(req.getSpecialization() != null ? req.getSpecialization() : "General Veterinary Care");
            doctor.setLicenseNumber(req.getLicenseNumber());
            doctor.setExperienceYears(req.getExperienceYears() != null ? req.getExperienceYears() : 2);
            doctorRepository.save(doctor);
        } else if (role == Role.staff) {
            StaffMember staff = new StaffMember();
            staff.setId("stf_" + UUID.randomUUID().toString().substring(0, 8));
            staff.setUser(user);
            staff.setDepartment(req.getDepartment() != null ? req.getDepartment() : "Boarding & Care");
            staff.setShiftTiming(req.getShiftTiming() != null ? req.getShiftTiming() : "08:00 AM - 04:00 PM");
            staff.setAssignedZone(req.getAssignedZone());
            staffMemberRepository.save(staff);
        } else if (role == Role.owner) {
            PetOwner owner = new PetOwner();
            owner.setId("own_" + UUID.randomUUID().toString().substring(0, 8));
            owner.setUser(user);
            owner.setAddress(req.getAddress());
            owner.setEmergencyContact(req.getPhone());
            petOwnerRepository.save(owner);
        }

        return authService.toDto(user);
    }

    @Transactional
    public void deleteUser(String id) {
        if ("usr_admin_01".equals(id)) {
            throw new IllegalStateException("Root administrator account cannot be deleted.");
        }
        if (!userRepository.existsById(id)) {
            throw new IllegalArgumentException("User not found: " + id);
        }
        userRepository.deleteById(id);
    }
}
