package com.petcare.config;

import com.petcare.entity.*;
import com.petcare.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner seedDefaultUsersIfEmpty(
            UserRepository userRepository,
            PetOwnerRepository petOwnerRepository,
            DoctorRepository doctorRepository,
            StaffMemberRepository staffMemberRepository,
            PasswordEncoder passwordEncoder
    ) {
        return args -> {
            if (userRepository.count() > 0) {
                return;
            }

            User admin = new User();
            admin.setId("usr_admin_01");
            admin.setName("Dr. Sarah Jenkins");
            admin.setEmail("admin@petcare.com");
            admin.setPasswordHash(passwordEncoder.encode("admin123"));
            admin.setPhone("+91 98765 43210");
            admin.setRole(Role.admin);
            admin.setDepartment("Hospital Administration");
            admin.setAvatar("https://images.unsplash.com/photo-1573496359142-b8d87734a5a2?w=150&auto=format&fit=crop&q=80");
            userRepository.save(admin);

            User docUser = new User();
            docUser.setId("usr_doctor_01");
            docUser.setName("Dr. Marcus Vance, DVM");
            docUser.setEmail("doctor@petcare.com");
            docUser.setPasswordHash(passwordEncoder.encode("doctor123"));
            docUser.setPhone("+91 98765 43211");
            docUser.setRole(Role.doctor);
            docUser.setSpecialization("Senior Veterinary Surgeon & Diagnostician");
            docUser.setAvatar("https://images.unsplash.com/photo-1622253692010-333f2da6031d?w=150&auto=format&fit=crop&q=80");
            userRepository.save(docUser);

            Doctor doctor = new Doctor();
            doctor.setId("doc_01");
            doctor.setUser(docUser);
            doctor.setSpecialization("Senior Veterinary Surgeon & Diagnostician");
            doctor.setLicenseNumber("VET-IN-2016-4410");
            doctor.setExperienceYears(10);
            doctorRepository.save(doctor);

            User staffUser = new User();
            staffUser.setId("usr_staff_01");
            staffUser.setName("Elena Rostova");
            staffUser.setEmail("staff@petcare.com");
            staffUser.setPasswordHash(passwordEncoder.encode("staff123"));
            staffUser.setPhone("+91 98765 43212");
            staffUser.setRole(Role.staff);
            staffUser.setDepartment("Boarding & Intensive Care Lead");
            staffUser.setAvatar("https://images.unsplash.com/photo-1580489944761-15a19d654956?w=150&auto=format&fit=crop&q=80");
            userRepository.save(staffUser);

            StaffMember staff = new StaffMember();
            staff.setId("stf_01");
            staff.setUser(staffUser);
            staff.setDepartment("Boarding & Intensive Care Lead");
            staff.setShiftTiming("07:00 AM - 03:00 PM");
            staff.setAssignedZone("Suites & Cat Condos");
            staffMemberRepository.save(staff);

            User ownerUser = new User();
            ownerUser.setId("usr_owner_01");
            ownerUser.setName("David & Clara Miller");
            ownerUser.setEmail("owner@petcare.com");
            ownerUser.setPasswordHash(passwordEncoder.encode("owner123"));
            ownerUser.setPhone("+91 98765 43213");
            ownerUser.setRole(Role.owner);
            ownerUser.setAvatar("https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=150&auto=format&fit=crop&q=80");
            userRepository.save(ownerUser);

            PetOwner owner = new PetOwner();
            owner.setId("own_01");
            owner.setUser(ownerUser);
            owner.setAddress("42 Indiranagar 100ft Rd, Bengaluru");
            owner.setEmergencyContact("+91 98765 43219");
            petOwnerRepository.save(owner);
        };
    }
}
