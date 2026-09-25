-- ============================================================================
-- ESTATEFLOW REAL ESTATE DATABASE SCHEMA FOR POSTGRESQL & PGADMIN 4
-- Compatible with: PostgreSQL 12+, 13, 14, 15, 16, 17
-- Created for: Estateflow Mobile & Web Presentation
-- ============================================================================

-- STEP 1: CREATE DATABASE (Run this separately if estateflow_db does not exist yet)
-- CREATE DATABASE estateflow_db;
-- \c estateflow_db;

DROP TABLE IF EXISTS chat_messages CASCADE;
DROP TABLE IF EXISTS inquiries CASCADE;
DROP TABLE IF EXISTS viewing_appointments CASCADE;
DROP TABLE IF EXISTS notifications CASCADE;
DROP TABLE IF EXISTS properties CASCADE;
DROP TABLE IF EXISTS agent_profiles CASCADE;
DROP TABLE IF EXISTS user_accounts CASCADE;

CREATE TABLE properties (
    id VARCHAR(64) PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    address VARCHAR(255) NOT NULL,
    city_state_zip VARCHAR(128) NOT NULL,
    price BIGINT NOT NULL,
    price_formatted VARCHAR(64) NOT NULL,
    is_rental BOOLEAN DEFAULT FALSE,
    beds NUMERIC(4, 1) NOT NULL,
    baths NUMERIC(4, 1) NOT NULL,
    sqft INTEGER NOT NULL,
    property_type VARCHAR(64) NOT NULL,
    description TEXT NOT NULL,
    image_res_id INTEGER DEFAULT 0,
    media_uris TEXT DEFAULT '',
    is_favorite BOOLEAN DEFAULT FALSE,
    status VARCHAR(64) DEFAULT 'Active',
    available_dates TEXT DEFAULT 'Mon, Oct 14,Tue, Oct 15,Wed, Oct 16,Thu, Oct 17',
    available_time_slots TEXT DEFAULT '10:00 AM,1:30 PM,4:00 PM,5:30 PM',
    amenities TEXT DEFAULT 'Private Rooftop Deck,Direct Garage Access,Central A/C,Pet Friendly',
    year_built INTEGER DEFAULT 2022,
    agent_name VARCHAR(128) DEFAULT 'Sarah Jenkins',
    agent_title VARCHAR(255) DEFAULT 'Premier Real Estate Broker • Cebu City Properties',
    agent_phone VARCHAR(64) DEFAULT '+63 917 555 0192',
    agent_email VARCHAR(128) DEFAULT 'sarah.jenkins@estateflow.com',
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE viewing_appointments (
    id VARCHAR(64) PRIMARY KEY,
    property_id VARCHAR(64) REFERENCES properties(id) ON DELETE CASCADE,
    property_title VARCHAR(255) NOT NULL,
    property_address VARCHAR(255) NOT NULL,
    client_name VARCHAR(128) NOT NULL,
    client_phone VARCHAR(64) DEFAULT '+1 (555) 012-3456',
    client_email VARCHAR(128) DEFAULT 'client@example.com',
    appointment_date VARCHAR(64) NOT NULL,
    time_slot VARCHAR(64) NOT NULL,
    appointment_type VARCHAR(64) DEFAULT 'Viewing',
    status VARCHAR(32) DEFAULT 'UPCOMING',
    notes TEXT DEFAULT '',
    timestamp_epoch BIGINT NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE inquiries (
    id VARCHAR(64) PRIMARY KEY,
    property_id VARCHAR(64) REFERENCES properties(id) ON DELETE CASCADE,
    property_title VARCHAR(255) NOT NULL,
    property_address VARCHAR(255) NOT NULL,
    sender_name VARCHAR(128) NOT NULL,
    sender_email VARCHAR(128) NOT NULL,
    sender_phone VARCHAR(64) NOT NULL,
    message TEXT NOT NULL,
    time_ago VARCHAR(64) DEFAULT 'Just now',
    is_unread BOOLEAN DEFAULT TRUE,
    updated_at_epoch BIGINT NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE chat_messages (
    id VARCHAR(64) PRIMARY KEY,
    inquiry_id VARCHAR(64) REFERENCES inquiries(id) ON DELETE CASCADE,
    sender VARCHAR(32) NOT NULL,
    message_text TEXT NOT NULL,
    time_sent VARCHAR(64) NOT NULL,
    is_from_me BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE notifications (
    id VARCHAR(64) PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    message TEXT NOT NULL,
    timestamp_formatted VARCHAR(64) NOT NULL,
    notification_type VARCHAR(64) NOT NULL,
    is_read BOOLEAN DEFAULT FALSE,
    target_id VARCHAR(64),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE agent_profiles (
    id VARCHAR(64) PRIMARY KEY DEFAULT 'sarah_jenkins_profile',
    name VARCHAR(128) NOT NULL,
    title VARCHAR(255) NOT NULL,
    license_no VARCHAR(64) NOT NULL,
    phone VARCHAR(64) NOT NULL,
    email VARCHAR(128) NOT NULL,
    rating NUMERIC(3, 2) DEFAULT 4.90,
    reviews_count INTEGER DEFAULT 124,
    total_sales_volume VARCHAR(64) DEFAULT '₱48.2M',
    active_listings_count INTEGER DEFAULT 4,
    bio TEXT NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE user_accounts (
    id VARCHAR(64) PRIMARY KEY,
    name VARCHAR(128) NOT NULL,
    email VARCHAR(128) NOT NULL UNIQUE,
    phone VARCHAR(64) DEFAULT '',
    role VARCHAR(32) DEFAULT 'BUYER',
    license_no VARCHAR(64) DEFAULT '',
    agency VARCHAR(128) DEFAULT '',
    professional_role VARCHAR(128) DEFAULT 'Verified Buyer',
    password_hash VARCHAR(255) DEFAULT '',
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_properties_type ON properties(property_type);
CREATE INDEX idx_properties_price ON properties(price);
CREATE INDEX idx_properties_favorite ON properties(is_favorite);
CREATE INDEX idx_appointments_property ON viewing_appointments(property_id);
CREATE INDEX idx_inquiries_property ON inquiries(property_id);
CREATE INDEX idx_chat_inquiry ON chat_messages(inquiry_id);

INSERT INTO properties (
    id, title, address, city_state_zip, price, price_formatted, is_rental,
    beds, baths, sqft, property_type, description, is_favorite, status,
    available_dates, available_time_slots, amenities, year_built, agent_name, agent_phone
) VALUES
(
    'prop_pinecrest_01',
    'Pinecrest Executive Villa',
    '88 Pinecrest Ridge, Banilad',
    'Cebu City, 6000',
    18500000,
    '₱18,500,000',
    FALSE,
    4.0, 3.5, 3200,
    'House & Lot',
    'Magnificent gated modern villa featuring sweeping panoramic mountain views, double-height great room with floor-to-ceiling glass, custom European kitchen with quartz island, resort infinity plunge pool, and smart solar climate control.',
    TRUE,
    'Active Listing',
    'Mon, Oct 14,Tue, Oct 15,Wed, Oct 16,Fri, Oct 18',
    '10:00 AM,1:30 PM,4:00 PM,5:30 PM',
    'Resort Infinity Pool,Solar Energy System,Double-Height Ceilings,Gated 24/7 Security,3-Car Covered Garage,Private Landscaped Garden',
    2023,
    'Sarah Jenkins',
    '+63 917 555 0192'
),
(
    'prop_skyline_02',
    'Skyline Glass Tower Penthouse',
    'Level 42, Cebu IT Park Luxury Tower',
    'Cebu City, 6000',
    24000000,
    '₱24,000,000',
    FALSE,
    3.0, 3.0, 2450,
    'Condominium',
    'Ultra-luxury high-rise penthouse commanding dramatic 360-degree city and ocean horizons. Features private elevator foyer, wraparound sky terrace, automated motorized shades, Miele appliances, and direct access to IT Park fine dining.',
    TRUE,
    'Active Listing',
    'Tue, Oct 15,Wed, Oct 16,Thu, Oct 17,Sat, Oct 19',
    '11:00 AM,2:00 PM,4:30 PM,6:00 PM',
    'Direct Private Elevator,Wraparound Sky Terrace,Automated Smart Home,Concierge 24/7,Sky Gym & Spa,Covered Basement Parking',
    2024,
    'Sarah Jenkins',
    '+63 917 555 0192'
),
(
    'prop_sunny_meadows_03',
    'Sunny Meadows Family Estate',
    '142 Orchid Boulevard, Kasambagan',
    'Cebu City, 6000',
    12800000,
    '₱12,800,000',
    FALSE,
    4.0, 3.0, 2850,
    'House & Lot',
    'Charming contemporary multi-generational family estate with expansive manicured lawn, outdoor chef pavilion with BBQ grill, spacious home office with fiber optic link, and serene private courtyard ideal for entertaining.',
    FALSE,
    'Active Listing',
    'Mon, Oct 14,Wed, Oct 16,Thu, Oct 17,Fri, Oct 18',
    '9:30 AM,1:00 PM,3:30 PM,5:00 PM',
    'Outdoor Chef Pavilion,Fiber Gigabit Ready,Private Courtyard,Pet Paradise Enclosure,Dedicated Home Office,2-Car Enclosed Garage',
    2021,
    'Sarah Jenkins',
    '+63 917 555 0192'
),
(
    'prop_urban_loft_04',
    'Urban Loft IT Park',
    'Tower 3, The Meridian Suites, IT Park',
    'Cebu City, 6000',
    65000,
    '₱65,000 / mo',
    TRUE,
    1.5, 1.5, 920,
    'Apartment',
    'Chic industrial-modern loft featuring 14-foot exposed concrete ceilings, polished terrazzo floors, custom black steel mezzanine bedroom, and high-speed Wi-Fi, perfect for tech executives and digital nomads.',
    FALSE,
    'For Rent',
    'Daily Walkthroughs Available',
    '10:30 AM,1:30 PM,3:00 PM,5:30 PM',
    '14ft Loft Ceilings,High Speed Fiber Wi-Fi,Infinity Lap Pool,Co-Working Lounge,24/7 Security,Furnished Designer Pieces',
    2022,
    'Sarah Jenkins',
    '+63 917 555 0192'
);

INSERT INTO agent_profiles (
    id, name, title, license_no, phone, email, rating, reviews_count,
    total_sales_volume, active_listings_count, bio
) VALUES (
    'sarah_jenkins_profile',
    'Sarah Jenkins',
    'Premier Real Estate Broker • Cebu City Luxury Estates',
    'PRC-REB-0094821',
    '+63 917 555 0192',
    'sarah.jenkins@estateflow.com',
    4.95,
    148,
    '₱62.4M',
    4,
    'Senior Managing Broker at Estateflow Realty with over 12 years of specialized luxury condominium and executive residential sales across Cebu City, Cebu IT Park, and Cebu Business Park.'
);

INSERT INTO viewing_appointments (
    id, property_id, property_title, property_address,
    client_name, client_phone, client_email,
    appointment_date, time_slot, appointment_type, status, notes, timestamp_epoch
) VALUES
(
    'appt_001',
    'prop_pinecrest_01',
    'Pinecrest Executive Villa',
    '88 Pinecrest Ridge, Banilad',
    'David Harrison',
    '+1 (555) 019-8234',
    'david.harrison@investor.com',
    'Tue, Oct 15',
    '1:30 PM',
    'In-Person Walkthrough',
    'UPCOMING',
    'Client requested architectural floor plans and title review.',
    1729000000000
),
(
    'appt_002',
    'prop_skyline_02',
    'Skyline Glass Tower Penthouse',
    'Level 42, Cebu IT Park Luxury Tower',
    'Elena Rostova',
    '+63 920 441 9081',
    'elena.rostova@globaltech.io',
    'Wed, Oct 16',
    '4:30 PM',
    'VIP Sunset Tour',
    'UPCOMING',
    'Wants to observe sunset view from the 360 wraparound terrace.',
    1729086400000
);

INSERT INTO inquiries (
    id, property_id, property_title, property_address,
    sender_name, sender_email, sender_phone,
    message, time_ago, is_unread, updated_at_epoch
) VALUES
(
    'inq_001',
    'prop_pinecrest_01',
    'Pinecrest Executive Villa',
    '88 Pinecrest Ridge, Banilad',
    'David Harrison',
    'david.harrison@investor.com',
    '+1 (555) 019-8234',
    'Hello Sarah, is the Pinecrest Villa available for a private walkthrough this Tuesday? Also, are solar warranties transferable?',
    '10 mins ago',
    TRUE,
    1729000500000
);

INSERT INTO chat_messages (
    id, inquiry_id, sender, message_text, time_sent, is_from_me
) VALUES
(
    'msg_001',
    'inq_001',
    'client',
    'Hello Sarah, is the Pinecrest Villa available for a private walkthrough this Tuesday? Also, are solar warranties transferable?',
    '10:15 AM',
    FALSE
),
(
    'msg_002',
    'inq_001',
    'agent',
    'Hi David! Yes, absolutely. Tuesday at 1:30 PM is confirmed. All Tier-1 solar panel warranties are 100% transferable to the new owner.',
    '10:18 AM',
    TRUE
);

INSERT INTO notifications (
    id, title, message, timestamp_formatted, notification_type, is_read, target_id
) VALUES
(
    'notif_001',
    'Viewing Confirmed: Pinecrest Villa',
    'Walkthrough with David Harrison scheduled for Tue, Oct 15 at 1:30 PM.',
    '10 mins ago',
    'VIEWING_BOOKED',
    FALSE,
    'appt_001'
);
