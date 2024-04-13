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
    trainer_id INT NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    is_group_availability BOOLEAN NOT NULL,
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

CREATE TABLE TrainingSession (
    session_id SERIAL PRIMARY KEY,
    member_id INTEGER NOT NULL,
    trainer_id INTEGER NOT NULL,
    session_date DATE NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    session_name VARCHAR(255) NOT NULL,
    FOREIGN KEY (member_id) REFERENCES Members(member_id),
    FOREIGN KEY (trainer_id) REFERENCES Trainers(trainer_id)
    );

CREATE TABLE GroupSession (
    session_id SERIAL PRIMARY KEY,
    session_name VARCHAR(255) NOT NULL,
    session_date DATE NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    max_participants INTEGER,
    trainer_id INTEGER NOT NULL,
    FOREIGN KEY (trainer_id) REFERENCES Trainers(trainer_id)
);

CREATE TABLE GroupSessionEnrollment (
    enrollment_id SERIAL PRIMARY KEY,
    session_id INTEGER NOT NULL,
    member_id INTEGER NOT NULL,
    FOREIGN KEY (session_id) REFERENCES GroupSession(session_id),
    FOREIGN KEY (member_id) REFERENCES Members(member_id)
);

CREATE TABLE Payments (
    payment_id SERIAL PRIMARY KEY,
    member_id INTEGER NOT NULL,
    payment_date DATE NOT NULL,
    amount DECIMAL(10, 2) NOT NULL,
    payment_desc VARCHAR(255),
    FOREIGN KEY (member_id) REFERENCES Members(member_id)
);

CREATE TABLE ApprovedPayments (
    payment_id INTEGER NOT NULL,
    member_id INTEGER NOT NULL,
    payment_date DATE NOT NULL,
    admin_id INTEGER NOT NULL,
    date_approved DATE NOT NULL,
    payment_desc VARCHAR(255),
    FOREIGN KEY (payment_id) REFERENCES Payments(payment_id),
    FOREIGN KEY (admin_id) REFERENCES Admin(admin_id),
    PRIMARY KEY (payment_id)
);