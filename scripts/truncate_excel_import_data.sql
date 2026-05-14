-- Reset all geography + HWC roster data loaded from the master Excel import.
-- Run in psql or any PostgreSQL client connected to sukrtya_siwan.
--
-- Order: children first (FK targets), then parents. RESTART IDENTITY resets SERIAL/BIGSERIAL.

BEGIN;

-- Depends on facility + portal_user (leave portal_user; remove mappings if present)
TRUNCATE TABLE portal_user_facility RESTART IDENTITY;

-- Depends on facility + portal_user
TRUNCATE TABLE data_submission RESTART IDENTITY;

-- Roster rows (FK to facility, health_worker, self optional)
TRUNCATE TABLE facility_worker_assignment RESTART IDENTITY;

-- HWC (FK to block)
TRUNCATE TABLE facility RESTART IDENTITY;

-- Block (FK to district)
TRUNCATE TABLE block RESTART IDENTITY;

-- District
TRUNCATE TABLE district RESTART IDENTITY;

-- People created for CHO / ANM / facilitator / ASHA (no FKs pointing *to* health_worker after fwa is gone)
TRUNCATE TABLE health_worker RESTART IDENTITY;

COMMIT;
