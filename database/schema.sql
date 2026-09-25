-- ============================================================================
-- REAL ESTATE MANAGEMENT SYSTEM - POSTGRESQL SCHEMA INITIALIZATION
-- Database: real_estate
-- Tool: pgAdmin 4 / psql
-- ============================================================================

-- Step 1: In pgAdmin 4, create database 'real_estate' if it does not already exist:
-- CREATE DATABASE real_estate;
-- Connect to 'real_estate' database before running the script below.

-- ----------------------------------------------------------------------------
-- Drop existing tables in reverse dependency order (safe for re-runs)
-- ----------------------------------------------------------------------------
DROP TABLE IF EXISTS properties CASCADE;
DROP TABLE IF EXISTS clients CASCADE;
DROP TABLE IF EXISTS agents CASCADE;

-- ----------------------------------------------------------------------------
-- TABLE: agents
-- Stores licensed real estate agents managing property listings
-- ----------------------------------------------------------------------------
CREATE TABLE agents (
    id BIGSERIAL PRIMARY KEY,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    phone VARCHAR(50),
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Index for fast agent email lookups
CREATE INDEX ix_agents_email ON agents(email);

-- ----------------------------------------------------------------------------
-- TABLE: clients
-- Stores prospective buyers and renters
-- ----------------------------------------------------------------------------
CREATE TABLE clients (
    id BIGSERIAL PRIMARY KEY,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    phone VARCHAR(50),
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Index for fast client email lookups
CREATE INDEX ix_clients_email ON clients(email);

-- ----------------------------------------------------------------------------
-- TABLE: properties
-- Stores real estate listings linked to managing agents
-- Uses precise NUMERIC types for monetary and dimensional values (no float)
-- ----------------------------------------------------------------------------
CREATE TABLE properties (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    property_type VARCHAR(100) NOT NULL,
    price NUMERIC(12, 2) NOT NULL CHECK (price > 0),
    address VARCHAR(255) NOT NULL,
    city VARCHAR(100) NOT NULL,
    bedrooms INTEGER NOT NULL DEFAULT 1 CHECK (bedrooms >= 0),
    bathrooms NUMERIC(3, 1) NOT NULL DEFAULT 1.0 CHECK (bathrooms >= 0),
    area NUMERIC(10, 2) NOT NULL DEFAULT 0.0 CHECK (area > 0),
    status VARCHAR(50) NOT NULL DEFAULT 'available',
    agent_id BIGINT NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_properties_agent
        FOREIGN KEY (agent_id)
        REFERENCES agents(id)
        ON DELETE CASCADE
);

-- Search optimization indexes
CREATE INDEX ix_properties_title ON properties(title);
CREATE INDEX ix_properties_city ON properties(city);
CREATE INDEX ix_properties_property_type ON properties(property_type);
CREATE INDEX ix_properties_status ON properties(status);
CREATE INDEX ix_properties_agent_id ON properties(agent_id);
CREATE INDEX ix_properties_city_status ON properties(city, status);

-- ----------------------------------------------------------------------------
-- SAMPLE DATA FOR DEMONSTRATION
-- ----------------------------------------------------------------------------

-- 1. Insert Initial Agents
INSERT INTO agents (id, first_name, last_name, email, phone, created_at, updated_at)
VALUES
    (1, 'Sarah', 'Jenkins', 'sarah.jenkins@estateflow.com', '+1 (555) 234-5678', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (2, 'Marcus', 'Vance', 'marcus.vance@estateflow.com', '+1 (555) 345-6789', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (3, 'Elena', 'Rostova', 'elena.rostova@estateflow.com', '+1 (555) 456-7890', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Sync primary key sequence with inserted records
SELECT setval(pg_get_serial_sequence('agents', 'id'), coalesce(max(id), 1)) FROM agents;

-- 2. Insert Initial Clients
INSERT INTO clients (id, first_name, last_name, email, phone, created_at, updated_at)
VALUES
    (1, 'David', 'Miller', 'david.miller@example.com', '+1 (555) 789-0123', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (2, 'Sophia', 'Chen', 'sophia.chen@example.com', '+1 (555) 890-1234', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (3, 'James', 'Wilson', 'james.wilson@example.com', '+1 (555) 901-2345', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Sync primary key sequence with inserted records
SELECT setval(pg_get_serial_sequence('clients', 'id'), coalesce(max(id), 1)) FROM clients;

-- 3. Insert Initial Properties with valid agent_id references
INSERT INTO properties (id, title, description, property_type, price, address, city, bedrooms, bathrooms, area, status, agent_id, created_at, updated_at)
VALUES
    (
        1,
        'Pinecrest Modern Townhouse',
        'Contemporary multi-level townhouse in prime residential district with private garage and terrace.',
        'Townhouse',
        680000.00,
        '742 Evergreen Terrace',
        'Springfield',
        3,
        2.5,
        2100.00,
        'available',
        1,
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP
    ),
    (
        2,
        'Skyline Luxury Penthouse',
        'Stunning penthouse with panoramic 360-degree skyline views, private elevator access, and designer finishes.',
        'Apartment',
        1450000.00,
        '100 Ocean Avenue, Unit 42A',
        'Metropolis',
        4,
        3.5,
        3400.00,
        'available',
        1,
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP
    ),
    (
        3,
        'Urban Industrial Loft',
        'Spacious brick-and-beam open layout loft featuring 14ft ceilings, polished concrete floors, and oversized windows.',
        'Condo',
        495000.00,
        '512 Foundry Way, #3B',
        'Downtown',
        2,
        2.0,
        1650.00,
        'available',
        2,
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP
    ),
    (
        4,
        'Sunny Meadows Family Home',
        'Charming suburban single-family home with expansive landscaped backyard, updated kitchen, and top-rated school district.',
        'House',
        785000.00,
        '12 Meadowbrook Drive',
        'Greenville',
        4,
        3.0,
        2850.00,
        'pending',
        3,
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP
    ),
    (
        5,
        'Boutique Coastal Villa',
        'Exclusive waterfront villa featuring private infinity pool, solar energy system, and private dock access.',
        'Villa',
        2200000.00,
        '88 Seaside Boulevard',
        'Bayshore',
        5,
        5.0,
        4800.00,
        'available',
        2,
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP
    );

-- Sync primary key sequence with inserted records
SELECT setval(pg_get_serial_sequence('properties', 'id'), coalesce(max(id), 1)) FROM properties;
