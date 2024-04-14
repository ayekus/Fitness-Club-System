INSERT INTO Members (first_name, last_name, email, password, phone, date_of_birth, height, weight, fitness_goal) VALUES
('John', 'Doe', 'johndoe@example.com', 'password123', '555-0101', '1990-06-15', 180.50, 75.00, 'Build muscle'),
('Jane', 'Smith', 'janesmith@example.com', 'mypassword', '555-0102', '1985-03-22', 165.00, 65.00, 'Lose weight');

INSERT INTO Trainers (first_name, last_name, email, password, phone, date_of_birth) VALUES
('Alice', 'Roberts', 'aliceroberts@example.com', 'securepassword1', '555-555-0201', '1982-07-05'),
('Bob', 'Johnson', 'bobjohnson@example.com', 'securepassword2', '555-555-0202', '1979-12-15');

INSERT INTO TrainerAvailability (trainer_id, start_time, end_time, is_group_availability, session_name) VALUES
(1, '08:00:00', '12:00:00', true, 'Group Running'),
(1, '14:00:00', '18:00:00', false, null),
(2, '09:00:00', '13:00:00', false, null),
(2, '15:00:00', '19:00:00', true, 'Group Yoga');

INSERT INTO Admin (first_name, last_name, email, password, phone, date_of_birth) VALUES
('Carol', 'Taylor', 'caroltaylor@example.com', 'adminpassword1', '555-555-0301', '1975-03-11'),
('David', 'Smith', 'davidsmith@example.com', 'adminpassword2', '555-555-0302', '1985-08-19'),
('Joe', 'Ryan', 'joeryan@example.com', 'adminpassword3', '555-555-0303', '1995-04-27');

INSERT INTO EquipmentMaintenance (equipment_name, admin_id) VALUES
('Treadmill', 1),
('Elliptical Machine', 2),
('Stationary Bike', 3);

INSERT INTO Rooms (room_desc) VALUES
('Gym'),
('Yoga'),
('Bike'),
('Boxing'),
('Group Fitness');

INSERT INTO ApprovedSessions (trainer_id, start_time, end_time, is_group_session, session_name, room_id) VALUES (1, '10:00:00', '11:00:00', FALSE, 'Yoga Basics', 1);

INSERT INTO TrainingSession (member_id, trainer_id, session_date, start_time, end_time, session_name, room_id) VALUES (1, 1, '2024-04-25', '09:00:00', '10:00:00', 'Personal Training', 1);

INSERT INTO GroupSession (session_name, session_date, start_time, end_time, trainer_id, room_id) VALUES ('Advanced Spin Class', '2024-04-20', '18:00:00', '19:00:00', 1, 1);

INSERT INTO GroupSessionEnrollment (group_session_id, member_id) VALUES (1, 1);

INSERT INTO Payments (member_id, amount, payment_desc) VALUES (1, 5, 'Registration Fee');

INSERT INTO ApprovedPayments (member_id, approved_amount, payment_desc, admin_id, payment_date) VALUES (1, 10, 'Session Registration Fee', 1, '2024-04-15');
