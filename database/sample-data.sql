-- =============================================================================
-- PetCare Management System - Sample Seed Data for MySQL 8.x
-- Passwords (BCrypt cost 10):
--   admin@petcare.com  / admin123
--   doctor@petcare.com / doctor123
--   staff@petcare.com  / staff123
--   owner@petcare.com  / owner123
-- =============================================================================

USE petcare_db;

-- 1. USERS
INSERT INTO users (id, name, email, password_hash, phone, role, avatar, department, specialization, active) VALUES
('usr_admin_01', 'Dr. Sarah Jenkins', 'admin@petcare.com', '$2a$10$AyZfFO2BSln6F4Ag6etTpuOkDK82vARCMYV/bqRviXLBcjUFgONt.', '+91 98765 43210', 'admin', 'https://images.unsplash.com/photo-1573496359142-b8d87734a5a2?w=150&auto=format&fit=crop&q=80', 'Hospital Administration', NULL, TRUE),
('usr_doctor_01', 'Dr. Marcus Vance, DVM', 'doctor@petcare.com', '$2a$10$By8.CggoqzABQwBHXbQTuOxWd/41Z6T3/Sy0aT9/dR75nxMdQg73G', '+91 98765 43211', 'doctor', 'https://images.unsplash.com/photo-1622253692010-333f2da6031d?w=150&auto=format&fit=crop&q=80', 'Veterinary Medicine', 'Senior Veterinary Surgeon & Diagnostician', TRUE),
('usr_staff_01', 'Elena Rostova', 'staff@petcare.com', '$2a$10$KkQJdXq3KrVkFqO5L2VDH.sRx5EZBTrlYW3bFT5XvZqn0IpVN/LFm', '+91 98765 43212', 'staff', 'https://images.unsplash.com/photo-1580489944761-15a19d654956?w=150&auto=format&fit=crop&q=80', 'Boarding & Intensive Care Lead', NULL, TRUE),
('usr_owner_01', 'David & Clara Miller', 'owner@petcare.com', '$2a$10$rWL.0F8cuxD7iUGyeonvwehy.dC8k1S0UmrYv.fEtt/7zUcEKuJ1e', '+91 98765 43213', 'owner', 'https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=150&auto=format&fit=crop&q=80', NULL, NULL, TRUE),
('usr_owner_02', 'Sarah Connor', 'sarah.connor@petcare.com', '$2a$10$rWL.0F8cuxD7iUGyeonvwehy.dC8k1S0UmrYv.fEtt/7zUcEKuJ1e', '+91 98765 43214', 'owner', NULL, NULL, NULL, TRUE),
('usr_owner_03', 'Robert Vance', 'robert.vance@petcare.com', '$2a$10$rWL.0F8cuxD7iUGyeonvwehy.dC8k1S0UmrYv.fEtt/7zUcEKuJ1e', '+91 98765 43215', 'owner', NULL, NULL, NULL, TRUE),
('usr_owner_04', 'Emily Watson', 'emily.watson@petcare.com', '$2a$10$rWL.0F8cuxD7iUGyeonvwehy.dC8k1S0UmrYv.fEtt/7zUcEKuJ1e', '+91 98765 43216', 'owner', NULL, NULL, NULL, TRUE);

-- 2. PET OWNERS
INSERT INTO pet_owners (id, user_id, address, emergency_contact, preferred_contact_method) VALUES
('own_01', 'usr_owner_01', '42 Indiranagar 100ft Rd, Bengaluru', '+91 98765 43219', 'Phone'),
('own_02', 'usr_owner_02', '18 Koramangala 4th Block, Bengaluru', '+91 98765 43214', 'Email'),
('own_03', 'usr_owner_03', '77 Bandra West, Mumbai', '+91 98765 43220', 'Phone'),
('own_04', 'usr_owner_04', '14 Jubilee Hills, Hyderabad', '+91 98765 43221', 'Phone');

-- 3. DOCTORS
INSERT INTO doctors (id, user_id, specialization, license_number, experience_years, available) VALUES
('doc_01', 'usr_doctor_01', 'Senior Veterinary Surgeon & Diagnostician', 'VET-IN-2016-4410', 10, TRUE);

-- 4. STAFF
INSERT INTO staff (id, user_id, department, shift_timing, assigned_zone, active) VALUES
('stf_01', 'usr_staff_01', 'Boarding & Intensive Care Lead', '07:00 AM - 03:00 PM', 'Suites & Cat Condos', TRUE);

-- 5. PETS
INSERT INTO pets (id, name, species, breed, age, gender, weight, owner_id, owner_name, owner_phone, microchip_id, avatar_url, allergies_json, medical_notes, is_boarded, room_number) VALUES
('pet_01', 'Barnaby', 'Dog', 'Golden Retriever', 4, 'Male', 31.50, 'usr_owner_01', 'David & Clara Miller', '+91 98765 43213', '985-1410-0921-A', 'https://images.unsplash.com/photo-1552053831-71594a27632d?w=300&auto=format&fit=crop&q=80', '["Chicken byproduct","Penicillin"]', 'Mild hip dysplasia history; requires low-impact exercise.', TRUE, 'Suite 101'),
('pet_02', 'Cleo', 'Cat', 'Scottish Fold', 2, 'Female', 3.80, 'usr_owner_01', 'David & Clara Miller', '+91 98765 43213', '985-1410-8832-B', 'https://images.unsplash.com/photo-1514888286974-6c03e2ca1dba?w=300&auto=format&fit=crop&q=80', '["Dust mites"]', 'Sensitive gastrointestinal tract. Sensitive stomach wet food only.', TRUE, 'Cat Condo C-3'),
('pet_03', 'Thor', 'Dog', 'Siberian Husky', 3, 'Male', 27.20, 'usr_owner_02', 'Sarah Connor', '+91 98765 43214', '985-2239-0112-C', 'https://images.unsplash.com/photo-1537151625747-768eb6cf92b2?w=300&auto=format&fit=crop&q=80', '[]', 'Active; loves morning agility yard.', FALSE, NULL),
('pet_04', 'Luna', 'Cat', 'Ragdoll', 1, 'Female', 4.10, 'usr_owner_03', 'Robert Vance', '+91 98765 43215', '985-9981-4433-D', 'https://images.unsplash.com/photo-1573865526739-10659fec78a5?w=300&auto=format&fit=crop&q=80', '["Soy"]', 'Routine wellness examination scheduled.', TRUE, 'Cat Condo C-1'),
('pet_05', 'Cooper', 'Dog', 'French Bulldog', 5, 'Male', 12.40, 'usr_owner_04', 'Emily Watson', '+91 98765 43216', '985-4421-7766-E', 'https://images.unsplash.com/photo-1583511655857-d19b40a7a54e?w=300&auto=format&fit=crop&q=80', '["Beef protein"]', 'Brachycephalic syndrome; keep in climate-controlled temperature.', TRUE, 'VIP Penthouse 202'),
('pet_06', 'Kiwi', 'Bird', 'Green-cheeked Conure', 2, 'Female', 0.08, 'usr_owner_01', 'David & Clara Miller', '+91 98765 43213', '985-6112-9901-F', 'https://images.unsplash.com/photo-1552728089-57bdde30beb3?w=300&auto=format&fit=crop&q=80', '[]', 'Gentle handling. Enjoys fresh apple slices and seeds.', FALSE, NULL),
('pet_07', 'Snowball', 'Rabbit', 'Holland Lop', 3, 'Female', 1.80, 'usr_owner_01', 'David & Clara Miller', '+91 98765 43213', '985-3341-2299-G', 'https://images.unsplash.com/photo-1585110396000-c9ffd4e4b308?w=300&auto=format&fit=crop&q=80', '["Alfalfa hay (timothy only)"]', 'Requires unlimited Timothy hay and daily grooming for fur balls.', FALSE, NULL);

-- 6. BOARDING BOOKINGS
INSERT INTO boarding_bookings (id, pet_id, owner_id, assigned_staff_id, room_type, room_number, check_in_date, check_out_date, status, daily_rate, special_instructions, emergency_contact) VALUES
('bk_101', 'pet_01', 'usr_owner_01', 'usr_staff_01', 'Deluxe Suite', 'Suite 101', '2026-10-06', '2026-10-12', 'Checked-In', 2500.00, 'Morning walk in agility yard; brush coat daily.', '+91 98765 43219'),
('bk_102', 'pet_02', 'usr_owner_01', 'usr_staff_01', 'Cat Condo', 'Cat Condo C-3', '2026-10-07', '2026-10-11', 'Checked-In', 1500.00, 'Calming pheromone diffuser active.', '+91 98765 43219'),
('bk_103', 'pet_04', 'usr_owner_03', 'usr_staff_01', 'Cat Condo', 'Cat Condo C-1', '2026-10-08', '2026-10-14', 'Checked-In', 1500.00, NULL, '+91 98765 43220'),
('bk_104', 'pet_05', 'usr_owner_04', 'usr_staff_01', 'VIP Penthouse', 'VIP Penthouse 202', '2026-10-09', '2026-10-15', 'Checked-In', 4000.00, NULL, '+91 98765 43221');

-- 7. FEEDING SCHEDULES
INSERT INTO feeding_schedules (id, pet_id, meal_type, scheduled_time, food_brand, portion_grams, dietary_restrictions, is_completed, completed_at, staff_id, staff_name, notes) VALUES
('fd_01', 'pet_01', 'Breakfast', '08:00 AM', 'Royal Canin Golden Retriever Adult', 280, 'No chicken, add joint supplement oil', TRUE, '08:15 AM', 'usr_staff_01', 'Elena Rostova', 'Ate all food enthusiastically.'),
('fd_02', 'pet_01', 'Dinner', '06:00 PM', 'Royal Canin Golden Retriever Adult', 280, 'No chicken', FALSE, NULL, NULL, NULL, 'Add fresh filtered water bowl.'),
('fd_03', 'pet_02', 'Breakfast', '08:30 AM', 'Hill\'s Science Diet Gastrointestinal Biome', 75, 'Sensitive stomach wet can only', TRUE, '08:35 AM', 'usr_staff_01', 'Elena Rostova', 'Mixed with probiotic powder.'),
('fd_04', 'pet_05', 'Breakfast', '08:15 AM', 'Purina Pro Plan Sensitive Salmon', 150, 'No beef protein', TRUE, '08:20 AM', 'usr_staff_01', 'Elena Rostova', 'Slow-feeder bowl used.');

-- 8. MEDICATIONS
INSERT INTO medications (id, pet_id, doctor_id, name, dosage, frequency, route, prescribed_by, start_date, end_date, is_given_today, last_administered, instructions) VALUES
('med_01', 'pet_01', 'usr_doctor_01', 'Carprofen (Rimadyl)', '75 mg chewable tablet', 'Once daily with breakfast', 'Oral', 'Dr. Marcus Vance, DVM', '2026-10-01', '2026-10-15', TRUE, 'Today, 08:15 AM', 'Administer with meal for stifle joint support.'),
('med_02', 'pet_02', 'usr_doctor_01', 'FortiFlora Feline Probiotic', '1 sachet powder', 'Once daily with breakfast', 'Oral', 'Dr. Marcus Vance, DVM', '2026-10-05', '2026-10-20', TRUE, 'Today, 08:35 AM', 'Sprinkle completely over wet food.'),
('med_03', 'pet_05', 'usr_doctor_01', 'Apoquel (Oclacitinib)', '5.4 mg tablet', 'Twice daily', 'Oral', 'Dr. Marcus Vance, DVM', '2026-10-07', '2026-10-21', FALSE, NULL, 'Give inside hypoallergenic pill pocket.');

-- 9. VACCINATIONS
INSERT INTO vaccinations (id, pet_id, doctor_id, vaccine_name, date_administered, due_date, batch_number, administered_by, status, certificate_id) VALUES
('vac_01', 'pet_01', 'usr_doctor_01', 'Rabies 3-Year Vaccine', '2024-05-12', '2027-05-12', 'DEF-88392-RAB', 'Dr. Marcus Vance, DVM', 'Up to Date', 'CERT-RAB-2024-019'),
('vac_02', 'pet_01', 'usr_doctor_01', 'DHPP (Distemper / Parvo)', '2025-10-15', '2026-10-15', 'VET-44912-DHPP', 'Dr. Marcus Vance, DVM', 'Due Soon', 'CERT-DHPP-2025-412'),
('vac_03', 'pet_02', 'usr_doctor_01', 'FVRCP (Feline Core 3-in-1)', '2025-08-20', '2026-08-20', 'FEL-99014-FVR', 'Dr. Marcus Vance, DVM', 'Overdue', 'CERT-FVR-2025-110');

-- 10. GROOMING APPOINTMENTS
INSERT INTO grooming_appointments (id, pet_id, staff_id, services_json, appointment_date, appointment_time, groomer_name, status, price, notes) VALUES
('grm_01', 'pet_01', 'usr_staff_01', '["Hydrobath","Nail Dremel","Ear Cleaning"]', '2026-10-10', '10:30 AM', 'Elena Rostova', 'Scheduled', 1800.00, 'Sensitive skin shampoo requested.'),
('grm_02', 'pet_04', 'usr_staff_01', '["Lion Cut Styling","Sanitary Trim"]', '2026-10-09', '02:00 PM', 'Elena Rostova', 'In-Progress', 2400.00, 'Extra calming music in grooming suite.'),
('grm_03', 'pet_05', 'usr_staff_01', '["Hydrotherapy Bath","Facial Fold Care","Paw Balm"]', '2026-10-08', '11:00 AM', 'Elena Rostova', 'Ready for Pickup', 1600.00, 'Skin folds dried and protected.');

-- 11. MEDICAL RECORDS
INSERT INTO medical_records (id, pet_id, doctor_id, record_date, doctor_name, doctor_email, diagnosis, symptoms, temperature, heart_rate, respiratory_rate, weight, treatment_plan, follow_up_date) VALUES
('rec_01', 'pet_01', 'usr_doctor_01', '2026-10-06', 'Dr. Marcus Vance, DVM', 'doctor@petcare.com', 'Pre-Boarding Clinical Exam & Stifle Stiffness', 'Subtle reluctance when rising.', 38.40, 88, 22, 31.50, 'Maintain daily Carprofen 75mg.', '2026-10-14'),
('rec_02', 'pet_02', 'usr_doctor_01', '2026-10-07', 'Dr. Marcus Vance, DVM', 'doctor@petcare.com', 'Mild Stress-Induced Dysbiosis', 'Soft stool prior to arrival, otherwise hydrated.', 38.60, 144, 28, 3.80, 'Initiate FortiFlora daily sachet in prescription wet food.', '2026-10-16');

-- 12. INVOICES
INSERT INTO invoices (id, invoice_number, pet_id, owner_id, invoice_date, due_date, subtotal, tax, total, status, payment_method) VALUES
('inv_01', 'INV-2026-1001', 'pet_01', 'usr_owner_01', '2026-10-06', '2026-10-13', 16200.00, 2916.00, 19116.00, 'Paid', 'Credit Card'),
('inv_02', 'INV-2026-1002', 'pet_02', 'usr_owner_01', '2026-10-07', '2026-10-14', 6000.00, 1080.00, 7080.00, 'Pending', NULL);

-- 13. INVOICE ITEMS
INSERT INTO invoice_items (invoice_id, description, quantity, unit_price, total) VALUES
('inv_01', 'Deluxe Suite Boarding (6 nights)', 6, 2500.00, 15000.00),
('inv_01', 'Veterinary Admission Exam & Vitals', 1, 1200.00, 1200.00),
('inv_02', 'Cat Condo Boarding (4 nights)', 4, 1500.00, 6000.00);

-- 14. PAYMENTS
INSERT INTO payments (id, invoice_id, owner_id, amount, payment_method, payment_status, transaction_reference) VALUES
('pay_01', 'inv_01', 'usr_owner_01', 19116.00, 'Credit Card', 'Completed', 'TXN-2026-1001-CC');

-- 15. NOTIFICATIONS
INSERT INTO notifications (id, title, message, type, target_role, target_user_id, timestamp_label, is_read, link) VALUES
('notif_01', 'Vaccine Due Soon: Barnaby', 'DHPP booster is due this week for Barnaby.', 'warning', 'all', NULL, '10 mins ago', FALSE, 'vaccinations'),
('notif_02', 'Evening Medication Scheduled', 'Boarded patients require evening medication rounds.', 'info', 'staff', NULL, '45 mins ago', FALSE, 'medications'),
('notif_03', 'Clinical Follow-up: Cleo', 'Review stool consistency and GI response to FortiFlora.', 'alert', 'doctor', NULL, '1 hour ago', FALSE, 'records');
