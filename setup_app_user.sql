-- ============================================================================
-- ESTATEFLOW DEDICATED POSTGRESQL APPLICATION ROLE SETUP
-- Run this script in pgAdmin 4 (Query Tool) or psql connected to your database
-- ============================================================================

-- INSTRUCTIONS:
-- 1. Replace 'your_database' below with your actual database name (e.g. estateflow_db).
-- 2. Replace 'REPLACE_WITH_A_STRONG_PASSWORD' with your strong, unique password.
-- 3. Execute the script to create the dedicated least-privilege application role.

CREATE ROLE app_user WITH
    LOGIN
    PASSWORD 'REPLACE_WITH_A_STRONG_PASSWORD';

GRANT CONNECT ON DATABASE your_database TO app_user;

GRANT USAGE ON SCHEMA public TO app_user;
GRANT SELECT, INSERT, UPDATE, DELETE
    ON ALL TABLES IN SCHEMA public
    TO app_user;

GRANT USAGE, SELECT, UPDATE
    ON ALL SEQUENCES IN SCHEMA public
    TO app_user;

ALTER DEFAULT PRIVILEGES IN SCHEMA public
GRANT SELECT, INSERT, UPDATE, DELETE ON TABLES TO app_user;

ALTER DEFAULT PRIVILEGES IN SCHEMA public
GRANT USAGE, SELECT, UPDATE ON SEQUENCES TO app_user;
