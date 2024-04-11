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
    fitness_goal VARCHAR(255)
    join_date DATE DEFAULT CURRENT_DATE
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
)

CREATE TABLE Admin (
    admin_id SERIAL PRIMARY KEY,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    phone VARCHAR(20) UNIQUE,
    date_of_birth DATE
)

