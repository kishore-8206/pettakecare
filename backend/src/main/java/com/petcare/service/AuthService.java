package com.petcare.service;

import com.petcare.dto.AuthDtos.*;
import com.petcare.entity.PetOwner;
import com.petcare.entity.Role;
import com.petcare.entity.User;
import com.petcare.repository.PetOwnerRepository;
import com.petcare.repository.UserRepository;
import com.petcare.security.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PetOwnerRepository petOwnerRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(
            UserRepository userRepository,
            PetOwnerRepository petOwnerRepository,
            PasswordEncoder passwordEncoder,
            JwtUtil jwtUtil
    ) {
        this.userRepository = userRepository;
        this.petOwnerRepository = petOwnerRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public AuthResponse login(LoginRequest req) {
        String email = req.getEmail() == null ? "" : req.getEmail().trim().toLowerCase();
        String password = req.getPassword() == null ? "" : req.getPassword().trim();

        User user = userRepository.findByEmailIgnoreCase(email).orElse(null);
        if (user == null || !user.isActive()) {
            return new AuthResponse(false, null, null, "Invalid email or password. Please verify your credentials.");
        }

        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            return new AuthResponse(false, null, null, "Invalid password. Please check your spelling and try again.");
        }

        if (req.getExpectedRole() != null && user.getRole() != req.getExpectedRole()) {
            String roleTitle = switch (user.getRole()) {
                case admin -> "Administrator";
                case doctor -> "Doctor / Veterinarian";
                case staff -> "Care Staff";
                case owner -> "Pet Owner";
            };
            return new AuthResponse(false, null, null,
                    "This account is registered with the role \"" + roleTitle + "\". Please use the " + roleTitle + " portal.");
        }

        String token = jwtUtil.generateToken(user);
        return new AuthResponse(true, token, toDto(user), null);
    }

    @Transactional
    public AuthResponse registerOwner(RegisterRequest req) {
        String email = req.getEmail().trim().toLowerCase();
        if (req.getConfirmPassword() != null && !req.getPassword().equals(req.getConfirmPassword())) {
            return new AuthResponse(false, null, null, "Password and Confirm Password do not match.");
        }

        if (userRepository.existsByEmailIgnoreCase(email)) {
            return new AuthResponse(false, null, null,
                    "An account with this email address already exists. Please log in instead.");
        }

        User user = new User();
        user.setId("usr_owner_" + UUID.randomUUID().toString().substring(0, 8));
        user.setName(req.getName().trim());
        user.setEmail(email);
        user.setPhone(req.getPhone().trim());
        user.setRole(Role.owner); // Public registration strictly restricted to Pet Owner
        user.setPasswordHash(passwordEncoder.encode(req.getPassword().trim()));
        user.setAvatar("https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=150&auto=format&fit=crop&q=80");
        user.setActive(true);
        userRepository.save(user);

        PetOwner ownerProfile = new PetOwner();
        ownerProfile.setId("own_" + UUID.randomUUID().toString().substring(0, 8));
        ownerProfile.setUser(user);
        ownerProfile.setAddress(req.getAddress());
        ownerProfile.setEmergencyContact(req.getEmergencyContact() != null ? req.getEmergencyContact() : req.getPhone().trim());
        petOwnerRepository.save(ownerProfile);

        String token = jwtUtil.generateToken(user);
        return new AuthResponse(true, token, toDto(user), null);
    }

    public UserDto toDto(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setName(user.getName());
        dto.setRole(user.getRole());
        dto.setPhone(user.getPhone());
        dto.setAvatar(user.getAvatar());
        dto.setDepartment(user.getDepartment());
        dto.setSpecialization(user.getSpecialization());
        dto.setCreatedAt(user.getCreatedAt() != null ? user.getCreatedAt().toString() : null);
        return dto;
    }
}
