-- =============================================================================
-- PetCare Management System - MySQL 8.x Database Schema
-- Replaces browser localStorage and removes Smart Live Monitoring telemetry.
-- =============================================================================

CREATE DATABASE IF NOT EXISTS petcare_db
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE petcare_db;

-- Disable FK checks temporarily for clean drop/re-creation if needed
SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS notifications;
DROP TABLE IF EXISTS payments;
DROP TABLE IF EXISTS invoice_items;
DROP TABLE IF EXISTS invoices;
DROP TABLE IF EXISTS medical_records;
DROP TABLE IF EXISTS grooming_appointments;
DROP TABLE IF EXISTS vaccinations;
DROP TABLE IF EXISTS medications;
DROP TABLE IF EXISTS feeding_schedules;
DROP TABLE IF EXISTS boarding_bookings;
DROP TABLE IF EXISTS pets;
DROP TABLE IF EXISTS staff;
DROP TABLE IF EXISTS doctors;
DROP TABLE IF EXISTS pet_owners;
DROP TABLE IF EXISTS users;

SET FOREIGN_KEY_CHECKS = 1;

-- 1. USERS (Core authentication & RBAC table)
CREATE TABLE users (
    id VARCHAR(64) PRIMARY KEY,
    name VARCHAR(120) NOT NULL,
    email VARCHAR(160) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    phone VARCHAR(30),
    role ENUM('admin', 'doctor', 'staff', 'owner') NOT NULL,
    avatar VARCHAR(500),
    department VARCHAR(120),
    specialization VARCHAR(160),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_users_role (role),
    INDEX idx_users_email (email)
) ENGINE=InnoDB;

-- 2. PET OWNERS (Profile extension for role = 'owner')
CREATE TABLE pet_owners (
    id VARCHAR(64) PRIMARY KEY,
    user_id VARCHAR(64) NOT NULL UNIQUE,
    address VARCHAR(255),
    emergency_contact VARCHAR(120),
    preferred_contact_method VARCHAR(40) DEFAULT 'Phone',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_pet_owners_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- 3. DOCTORS (Profile extension for role = 'doctor')
CREATE TABLE doctors (
    id VARCHAR(64) PRIMARY KEY,
    user_id VARCHAR(64) NOT NULL UNIQUE,
    specialization VARCHAR(160) NOT NULL,
    license_number VARCHAR(80) UNIQUE,
    experience_years INT NOT NULL DEFAULT 0,
    available BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT fk_doctors_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- 4. STAFF (Profile extension for role = 'staff')
CREATE TABLE staff (
    id VARCHAR(64) PRIMARY KEY,
    user_id VARCHAR(64) NOT NULL UNIQUE,
    department VARCHAR(120) NOT NULL,
    shift_timing VARCHAR(80),
    assigned_zone VARCHAR(80),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT fk_staff_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- 5. PETS
CREATE TABLE pets (
    id VARCHAR(64) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    species ENUM('Dog', 'Cat', 'Bird', 'Rabbit', 'Other') NOT NULL,
    breed VARCHAR(120) NOT NULL,
    age INT NOT NULL,
    gender ENUM('Male', 'Female') NOT NULL,
    weight DECIMAL(6,2) NOT NULL,
    owner_id VARCHAR(64) NOT NULL,
    owner_name VARCHAR(120) NOT NULL,
    owner_phone VARCHAR(30) NOT NULL,
    microchip_id VARCHAR(80) UNIQUE,
    avatar_url VARCHAR(500),
    allergies_json TEXT,
    medical_notes TEXT,
    is_boarded BOOLEAN NOT NULL DEFAULT FALSE,
    room_number VARCHAR(60),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_pets_owner FOREIGN KEY (owner_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_pets_owner (owner_id),
    INDEX idx_pets_species (species)
) ENGINE=InnoDB;

-- 6. BOARDING BOOKINGS
CREATE TABLE boarding_bookings (
    id VARCHAR(64) PRIMARY KEY,
    pet_id VARCHAR(64) NOT NULL,
    owner_id VARCHAR(64) NOT NULL,
    assigned_staff_id VARCHAR(64),
    room_type ENUM('Standard Kennel', 'Deluxe Suite', 'Cat Condo', 'VIP Penthouse') NOT NULL,
    room_number VARCHAR(60) NOT NULL,
    check_in_date DATE NOT NULL,
    check_out_date DATE NOT NULL,
    status ENUM('Confirmed', 'Checked-In', 'Completed', 'Cancelled') NOT NULL DEFAULT 'Confirmed',
    daily_rate DECIMAL(10,2) NOT NULL,
    special_instructions TEXT,
    emergency_contact VARCHAR(120) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_bookings_pet FOREIGN KEY (pet_id) REFERENCES pets(id) ON DELETE CASCADE,
    CONSTRAINT fk_bookings_owner FOREIGN KEY (owner_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_bookings_staff FOREIGN KEY (assigned_staff_id) REFERENCES users(id) ON DELETE SET NULL,
    INDEX idx_bookings_pet (pet_id),
    INDEX idx_bookings_owner (owner_id),
    INDEX idx_bookings_status (status)
) ENGINE=InnoDB;

-- 7. FEEDING SCHEDULES
CREATE TABLE feeding_schedules (
    id VARCHAR(64) PRIMARY KEY,
    pet_id VARCHAR(64) NOT NULL,
    meal_type ENUM('Breakfast', 'Lunch', 'Dinner', 'Snack') NOT NULL,
    scheduled_time VARCHAR(30) NOT NULL,
    food_brand VARCHAR(160) NOT NULL,
    portion_grams INT NOT NULL,
    dietary_restrictions VARCHAR(255),
    is_completed BOOLEAN NOT NULL DEFAULT FALSE,
    completed_at VARCHAR(40),
    staff_id VARCHAR(64),
    staff_name VARCHAR(120),
    notes TEXT,
    CONSTRAINT fk_feeding_pet FOREIGN KEY (pet_id) REFERENCES pets(id) ON DELETE CASCADE,
    CONSTRAINT fk_feeding_staff FOREIGN KEY (staff_id) REFERENCES users(id) ON DELETE SET NULL,
    INDEX idx_feeding_pet (pet_id)
) ENGINE=InnoDB;

-- 8. MEDICATIONS
CREATE TABLE medications (
    id VARCHAR(64) PRIMARY KEY,
    pet_id VARCHAR(64) NOT NULL,
    doctor_id VARCHAR(64),
    name VARCHAR(160) NOT NULL,
    dosage VARCHAR(100) NOT NULL,
    frequency VARCHAR(100) NOT NULL,
    route ENUM('Oral', 'Topical', 'Injection', 'Eye Drops') NOT NULL,
    prescribed_by VARCHAR(120) NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    is_given_today BOOLEAN NOT NULL DEFAULT FALSE,
    last_administered VARCHAR(60),
    instructions TEXT NOT NULL,
    CONSTRAINT fk_medications_pet FOREIGN KEY (pet_id) REFERENCES pets(id) ON DELETE CASCADE,
    CONSTRAINT fk_medications_doctor FOREIGN KEY (doctor_id) REFERENCES users(id) ON DELETE SET NULL,
    INDEX idx_medications_pet (pet_id)
) ENGINE=InnoDB;

-- 9. VACCINATIONS
CREATE TABLE vaccinations (
    id VARCHAR(64) PRIMARY KEY,
    pet_id VARCHAR(64) NOT NULL,
    doctor_id VARCHAR(64),
    vaccine_name VARCHAR(160) NOT NULL,
    date_administered DATE NOT NULL,
    due_date DATE NOT NULL,
    batch_number VARCHAR(80) NOT NULL,
    administered_by VARCHAR(120) NOT NULL,
    status ENUM('Up to Date', 'Due Soon', 'Overdue') NOT NULL DEFAULT 'Up to Date',
    certificate_id VARCHAR(80) NOT NULL UNIQUE,
    CONSTRAINT fk_vaccinations_pet FOREIGN KEY (pet_id) REFERENCES pets(id) ON DELETE CASCADE,
    CONSTRAINT fk_vaccinations_doctor FOREIGN KEY (doctor_id) REFERENCES users(id) ON DELETE SET NULL,
    INDEX idx_vaccinations_pet (pet_id),
    INDEX idx_vaccinations_due (due_date)
) ENGINE=InnoDB;

-- 10. GROOMING APPOINTMENTS
CREATE TABLE grooming_appointments (
    id VARCHAR(64) PRIMARY KEY,
    pet_id VARCHAR(64) NOT NULL,
    staff_id VARCHAR(64),
    services_json TEXT NOT NULL,
    appointment_date DATE NOT NULL,
    appointment_time VARCHAR(30) NOT NULL,
    groomer_name VARCHAR(120) NOT NULL,
    status ENUM('Scheduled', 'In-Progress', 'Ready for Pickup', 'Completed') NOT NULL DEFAULT 'Scheduled',
    price DECIMAL(10,2) NOT NULL,
    notes TEXT,
    CONSTRAINT fk_grooming_pet FOREIGN KEY (pet_id) REFERENCES pets(id) ON DELETE CASCADE,
    CONSTRAINT fk_grooming_staff FOREIGN KEY (staff_id) REFERENCES users(id) ON DELETE SET NULL,
    INDEX idx_grooming_pet (pet_id),
    INDEX idx_grooming_date (appointment_date)
) ENGINE=InnoDB;

-- 11. MEDICAL RECORDS
CREATE TABLE medical_records (
    id VARCHAR(64) PRIMARY KEY,
    pet_id VARCHAR(64) NOT NULL,
    doctor_id VARCHAR(64),
    record_date DATE NOT NULL,
    doctor_name VARCHAR(120) NOT NULL,
    doctor_email VARCHAR(160) NOT NULL,
    diagnosis VARCHAR(255) NOT NULL,
    symptoms TEXT NOT NULL,
    temperature DECIMAL(5,2),
    heart_rate INT,
    respiratory_rate INT,
    weight DECIMAL(6,2),
    treatment_plan TEXT NOT NULL,
    follow_up_date DATE,
    CONSTRAINT fk_records_pet FOREIGN KEY (pet_id) REFERENCES pets(id) ON DELETE CASCADE,
    CONSTRAINT fk_records_doctor FOREIGN KEY (doctor_id) REFERENCES users(id) ON DELETE SET NULL,
    INDEX idx_records_pet (pet_id),
    INDEX idx_records_date (record_date)
) ENGINE=InnoDB;

-- 12. INVOICES
CREATE TABLE invoices (
    id VARCHAR(64) PRIMARY KEY,
    invoice_number VARCHAR(80) NOT NULL UNIQUE,
    pet_id VARCHAR(64) NOT NULL,
    owner_id VARCHAR(64) NOT NULL,
    invoice_date DATE NOT NULL,
    due_date DATE NOT NULL,
    subtotal DECIMAL(12,2) NOT NULL,
    tax DECIMAL(12,2) NOT NULL,
    total DECIMAL(12,2) NOT NULL,
    status ENUM('Paid', 'Pending', 'Overdue') NOT NULL DEFAULT 'Pending',
    payment_method ENUM('Credit Card', 'Cash', 'Online Transfer'),
    CONSTRAINT fk_invoices_pet FOREIGN KEY (pet_id) REFERENCES pets(id) ON DELETE CASCADE,
    CONSTRAINT fk_invoices_owner FOREIGN KEY (owner_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_invoices_owner (owner_id),
    INDEX idx_invoices_status (status)
) ENGINE=InnoDB;

-- 13. INVOICE ITEMS (Normalized line items for invoices)
CREATE TABLE invoice_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    invoice_id VARCHAR(64) NOT NULL,
    description VARCHAR(255) NOT NULL,
    quantity INT NOT NULL,
    unit_price DECIMAL(12,2) NOT NULL,
    total DECIMAL(12,2) NOT NULL,
    CONSTRAINT fk_invoice_items_invoice FOREIGN KEY (invoice_id) REFERENCES invoices(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- 14. PAYMENTS
CREATE TABLE payments (
    id VARCHAR(64) PRIMARY KEY,
    invoice_id VARCHAR(64) NOT NULL,
    owner_id VARCHAR(64) NOT NULL,
    amount DECIMAL(12,2) NOT NULL,
    payment_method ENUM('Credit Card', 'Cash', 'Online Transfer') NOT NULL,
    payment_status ENUM('Completed', 'Pending', 'Failed', 'Refunded') NOT NULL DEFAULT 'Completed',
    transaction_reference VARCHAR(120) NOT NULL UNIQUE,
    paid_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_payments_invoice FOREIGN KEY (invoice_id) REFERENCES invoices(id) ON DELETE CASCADE,
    CONSTRAINT fk_payments_owner FOREIGN KEY (owner_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_payments_invoice (invoice_id),
    INDEX idx_payments_owner (owner_id)
) ENGINE=InnoDB;

-- 15. NOTIFICATIONS
CREATE TABLE notifications (
    id VARCHAR(64) PRIMARY KEY,
    title VARCHAR(180) NOT NULL,
    message TEXT NOT NULL,
    type ENUM('info', 'warning', 'alert', 'success') NOT NULL DEFAULT 'info',
    target_role ENUM('admin', 'doctor', 'staff', 'owner', 'all') NOT NULL DEFAULT 'all',
    target_user_id VARCHAR(64),
    timestamp_label VARCHAR(80),
    is_read BOOLEAN NOT NULL DEFAULT FALSE,
    link VARCHAR(120),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_notifications_user FOREIGN KEY (target_user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_notifications_role (target_role)
) ENGINE=InnoDB;
