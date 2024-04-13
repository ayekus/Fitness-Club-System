INSERT INTO Members (first_name, last_name, email, password, phone, date_of_birth, height, weight, fitness_goal, join_date) VALUES
('John', 'Doe', 'johndoe@example.com', 'password123', '555-0101', '1990-06-15', 180.50, 75.00, 'Build muscle', '2022-01-01'),
('Jane', 'Smith', 'janesmith@example.com', 'mypassword', '555-0102', '1985-03-22', 165.00, 65.00, 'Lose weight', '2022-02-15');

INSERT INTO Trainers (first_name, last_name, email, password, phone, date_of_birth) VALUES
('Alice', 'Roberts', 'aliceroberts@example.com', 'securepassword1', '555-555-0201', '1982-07-05'),
('Bob', 'Johnson', 'bobjohnson@example.com', 'securepassword2', '555-555-0202', '1979-12-15');

INSERT INTO TrainerAvailability (trainer_id, start_time, end_time) VALUES
(1, '08:00:00', '12:00:00'),
(1, '14:00:00', '18:00:00'),
(2, '09:00:00', '13:00:00'),
(2, '15:00:00', '19:00:00');

INSERT INTO Admin (first_name, last_name, email, password, phone, date_of_birth) VALUES
('Carol', 'Taylor', 'caroltaylor@example.com', 'adminpassword1', '555-555-0301', '1975-03-11'),
('David', 'Smith', 'davidsmith@example.com', 'adminpassword2', '555-555-0302', '1985-08-19');
('Joe', 'Ryan', 'joeryan@example.com', 'adminpassword3', '555-555-0303', '1995-04-27');

INSERT INTO EquipmentMaintenance (equipment_name, admin_id) VALUES
('Treadmill', 1),
('Elliptical Machine', 2),
('Stationary Bike', 3),