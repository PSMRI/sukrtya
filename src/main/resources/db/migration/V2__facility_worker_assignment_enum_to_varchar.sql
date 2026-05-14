-- Hibernate @Enumerated(STRING) uses varchar; PG enums break JPQL comparisons (operator does not exist).
-- Drop dependent indexes and CHECK before changing column types.

DROP INDEX IF EXISTS uq_one_active_cho_per_facility;
DROP INDEX IF EXISTS uq_one_active_anm_per_facility;

ALTER TABLE facility_worker_assignment
    DROP CONSTRAINT IF EXISTS chk_supervisor_rules;

ALTER TABLE facility_worker_assignment
    ALTER COLUMN role TYPE varchar(40) USING role::text;

ALTER TABLE facility_worker_assignment
    ALTER COLUMN status DROP DEFAULT;

ALTER TABLE facility_worker_assignment
    ALTER COLUMN status TYPE varchar(40) USING status::text;

ALTER TABLE facility_worker_assignment
    ALTER COLUMN status SET DEFAULT 'ACTIVE';

ALTER TABLE facility_worker_assignment
    ADD CONSTRAINT chk_supervisor_rules CHECK (
        (role = 'ASHA' AND supervisor_assignment_id IS NOT NULL)
        OR (role <> 'ASHA' AND supervisor_assignment_id IS NULL)
    );

CREATE UNIQUE INDEX uq_one_active_cho_per_facility
    ON facility_worker_assignment (facility_id)
    WHERE role = 'CHO' AND status = 'ACTIVE';

CREATE UNIQUE INDEX uq_one_active_anm_per_facility
    ON facility_worker_assignment (facility_id)
    WHERE role = 'ANM' AND status = 'ACTIVE';

-- Old enum types may remain in pg_catalog; they are unused after this migration and can be dropped manually if desired.
