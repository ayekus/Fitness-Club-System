CREATE TABLE Members (
    member_id SERIAL PRIMARY KEY,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    phone VARCHAR(20) UNIQUE,
    date_of_birth DATE,
    height DECIMAL(5, 2),
    weight DECIMAL(5, 2),
    fitness_goal VARCHAR(255),
    exercise_routines VARCHAR(255),
    fitness_achievements VARCHAR(255),
    health_stats VARCHAR(255)
);

CREATE TABLE Rooms (
    room_id SERIAL PRIMARY KEY,
    room_desc VARCHAR(255)
);

CREATE TABLE Trainers (
    trainer_id SERIAL PRIMARY KEY,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    phone VARCHAR(20) UNIQUE,
    date_of_birth DATE
);

CREATE TABLE TrainerAvailability (
    availability_id SERIAL PRIMARY KEY,
    trainer_id INT NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    is_group_availability BOOLEAN NOT NULL,
    session_name VARCHAR(255),
    FOREIGN KEY (trainer_id) REFERENCES Trainers(trainer_id)
);


CREATE TABLE Admin (
    admin_id SERIAL PRIMARY KEY,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    phone VARCHAR(20) UNIQUE,
    date_of_birth DATE
);

CREATE TABLE EquipmentMaintenance (
    maintenance_id SERIAL PRIMARY KEY,
    equipment_name VARCHAR(255) NOT NULL,
    admin_id INT NOT NULL,
    date_added DATE DEFAULT CURRENT_DATE,
    FOREIGN KEY (admin_id) REFERENCES Admin(admin_id)
);

CREATE TABLE ApprovedSessions (
    session_id SERIAL PRIMARY KEY,
    trainer_id INTEGER NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    is_group_session BOOLEAN NOT NULL,
    session_name VARCHAR(255),
    room_id INTEGER,
    FOREIGN KEY (trainer_id) REFERENCES Trainers(trainer_id),
    FOREIGN KEY (room_id) REFERENCES Rooms(room_id)
);

CREATE TABLE TrainingSession (
    training_session_id SERIAL PRIMARY KEY,
    member_id INTEGER NOT NULL,
    trainer_id INTEGER NOT NULL,
    session_date DATE NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    session_name VARCHAR(255) NOT NULL,
    room_id INTEGER,
    FOREIGN KEY (room_id) REFERENCES Rooms(room_id),
    FOREIGN KEY (member_id) REFERENCES Members(member_id),
    FOREIGN KEY (trainer_id) REFERENCES Trainers(trainer_id)
);

CREATE TABLE GroupSession (
    group_session_id SERIAL PRIMARY KEY,
    session_name VARCHAR(255) NOT NULL,
    session_date DATE NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    trainer_id INTEGER NOT NULL,
    room_id INTEGER,
    FOREIGN KEY (trainer_id) REFERENCES Trainers(trainer_id),
    FOREIGN KEY (room_id) REFERENCES Rooms(room_id)
);

CREATE TABLE GroupSessionEnrollment (
    enrollment_id SERIAL PRIMARY KEY,
    group_session_id INTEGER NOT NULL,
    member_id INTEGER NOT NULL,
    FOREIGN KEY (group_session_id) REFERENCES GroupSession(group_session_id),
    FOREIGN KEY (member_id) REFERENCES Members(member_id)
);

CREATE TABLE Payments (
    payment_id SERIAL PRIMARY KEY,
    member_id INTEGER NOT NULL,
    amount DECIMAL(10, 2) NOT NULL,
    payment_desc VARCHAR(255),
    payment_date DATE DEFAULT CURRENT_DATE,
    FOREIGN KEY (member_id) REFERENCES Members(member_id)
);

CREATE TABLE ApprovedPayments (
    approved_payment_id SERIAL PRIMARY KEY,
    member_id INTEGER NOT NULL,
    approved_amount DECIMAL(10, 2) NOT NULL,
    payment_desc VARCHAR(255),
    admin_id INTEGER NOT NULL,
    payment_date DATE NOT NULL,
    date_approved DATE DEFAULT CURRENT_DATE,
    FOREIGN KEY (member_id) REFERENCES Members(member_id),
    FOREIGN KEY (admin_id) REFERENCES Admin(admin_id)
);
