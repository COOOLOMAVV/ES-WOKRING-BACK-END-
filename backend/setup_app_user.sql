-- ============================================================================
-- ESTATEFLOW DEDICATED POSTGRESQL APPLICATION ROLE SETUP
-- Run this script in pgAdmin 4 (Query Tool) or psql connected to your database
-- ============================================================================

-- INSTRUCTIONS:
-- 1. Replace 'your_database' below with your actual database name (e.g. estateflow_db).
-- 2. Replace 'REPLACE_WITH_A_STRONG_PASSWORD' with your strong, unique password.
-- 3. Execute the script to create the dedicated least-privilege application role.

-- In psql, connect to your target database first:
-- \c your_database;

-- 1. Create dedicated application role with login capability
CREATE ROLE app_user WITH
    LOGIN
    PASSWORD 'REPLACE_WITH_A_STRONG_PASSWORD';

-- 2. Grant connection privileges to your application database
GRANT CONNECT ON DATABASE your_database TO app_user;

-- 3. Grant schema usage and CRUD permissions on all tables
GRANT USAGE ON SCHEMA public TO app_user;
GRANT SELECT, INSERT, UPDATE, DELETE
    ON ALL TABLES IN SCHEMA public
    TO app_user;

-- 4. Grant usage on sequences (for auto-incrementing serial/identity keys)
GRANT USAGE, SELECT, UPDATE
    ON ALL SEQUENCES IN SCHEMA public
    TO app_user;

-- 5. Automatically grant CRUD permissions on future tables created in public schema
ALTER DEFAULT PRIVILEGES IN SCHEMA public
GRANT SELECT, INSERT, UPDATE, DELETE ON TABLES TO app_user;

-- 6. Automatically grant usage on future sequences created in public schema
ALTER DEFAULT PRIVILEGES IN SCHEMA public
GRANT USAGE, SELECT, UPDATE ON SEQUENCES TO app_user;

-- ----------------------------------------------------------------------------
-- VERIFICATION (Run this to verify role creation and grants):
-- ----------------------------------------------------------------------------
-- SELECT rolname, rolcanlogin FROM pg_roles WHERE rolname = 'app_user';
