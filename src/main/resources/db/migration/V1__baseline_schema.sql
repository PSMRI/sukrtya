-- =============================================================================
-- Sukrtya Siwan — baseline schema (geography, portal users, HWC staff, forms)
-- =============================================================================

-- App login roles only (separate from CHO/ANM/ASHA job types on assignments)
CREATE TABLE portal_role (
    id          BIGSERIAL PRIMARY KEY,
    role_name   VARCHAR(100) NOT NULL UNIQUE,
    is_active   BOOLEAN NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT now()
);

INSERT INTO portal_role (role_name, is_active) VALUES
    ('ADMIN', TRUE),
    ('DATA_COLLECTOR', TRUE);

-- Geography: district -> block -> facility (district is not duplicated on facility)
CREATE TABLE district (
    id          BIGSERIAL PRIMARY KEY,
    code        VARCHAR(50),
    name        VARCHAR(200) NOT NULL,
    is_active   BOOLEAN NOT NULL DEFAULT TRUE,
    created_by  BIGINT,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at  TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE block (
    id          BIGSERIAL PRIMARY KEY,
    district_id BIGINT NOT NULL REFERENCES district (id) ON DELETE RESTRICT,
    code        VARCHAR(50),
    name        VARCHAR(200) NOT NULL,
    is_active   BOOLEAN NOT NULL DEFAULT TRUE,
    created_by  BIGINT,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at  TIMESTAMPTZ NOT NULL DEFAULT now(),
    UNIQUE (district_id, name)
);

CREATE INDEX idx_block_district ON block (district_id);

CREATE TABLE facility (
    id            BIGSERIAL PRIMARY KEY,
    block_id      BIGINT NOT NULL REFERENCES block (id) ON DELETE RESTRICT,
    code          VARCHAR(100),
    name          VARCHAR(300) NOT NULL,
    facility_type VARCHAR(100),
    is_active     BOOLEAN NOT NULL DEFAULT TRUE,
    created_by    BIGINT,
    created_at    TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at    TIMESTAMPTZ NOT NULL DEFAULT now(),
    UNIQUE (block_id, name)
);

CREATE INDEX idx_facility_block ON facility (block_id);

-- Portal users (admin / data collector)
CREATE TABLE portal_user (
    id               BIGSERIAL PRIMARY KEY,
    username         VARCHAR(200) NOT NULL UNIQUE,
    mobile_phone     VARCHAR(20) UNIQUE,
    password_hash    TEXT NOT NULL,
    portal_role_id   BIGINT NOT NULL REFERENCES portal_role (id) ON DELETE RESTRICT,
    display_name     VARCHAR(300),
    email            VARCHAR(320),
    account_enabled  BOOLEAN NOT NULL DEFAULT TRUE,
    last_login_at    TIMESTAMPTZ,
    created_by       BIGINT,
    created_at       TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at       TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT fk_portal_user_created_by FOREIGN KEY (created_by) REFERENCES portal_user (id) ON DELETE SET NULL
);

CREATE INDEX idx_portal_user_role ON portal_user (portal_role_id);

ALTER TABLE district
    ADD CONSTRAINT fk_district_created_by FOREIGN KEY (created_by) REFERENCES portal_user (id) ON DELETE SET NULL;

ALTER TABLE block
    ADD CONSTRAINT fk_block_created_by FOREIGN KEY (created_by) REFERENCES portal_user (id) ON DELETE SET NULL;

ALTER TABLE facility
    ADD CONSTRAINT fk_facility_created_by FOREIGN KEY (created_by) REFERENCES portal_user (id) ON DELETE SET NULL;

-- Many facilities per user; soft-disable per mapping; one default HWC among active rows
CREATE TABLE portal_user_facility (
    id              BIGSERIAL PRIMARY KEY,
    portal_user_id  BIGINT NOT NULL REFERENCES portal_user (id) ON DELETE CASCADE,
    facility_id     BIGINT NOT NULL REFERENCES facility (id) ON DELETE RESTRICT,
    is_primary      BOOLEAN NOT NULL DEFAULT FALSE,
    is_active       BOOLEAN NOT NULL DEFAULT TRUE,
    created_by      BIGINT REFERENCES portal_user (id) ON DELETE SET NULL,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    UNIQUE (portal_user_id, facility_id)
);

CREATE INDEX idx_portal_user_facility_facility ON portal_user_facility (facility_id);
CREATE INDEX idx_portal_user_facility_user ON portal_user_facility (portal_user_id);

CREATE UNIQUE INDEX uq_portal_user_one_primary_facility
    ON portal_user_facility (portal_user_id)
    WHERE is_primary AND is_active;

-- Master person (CHO / ANM / ASHA facilitator / ASHA)
CREATE TABLE health_worker (
    id            BIGSERIAL PRIMARY KEY,
    full_name     VARCHAR(200) NOT NULL,
    mobile_phone  VARCHAR(20),
    notes         TEXT,
    is_active     BOOLEAN NOT NULL DEFAULT TRUE,
    created_by    BIGINT REFERENCES portal_user (id) ON DELETE SET NULL,
    created_at    TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at    TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TYPE health_worker_role AS ENUM ('CHO', 'ANM', 'ASHA_FACILITATOR', 'ASHA');

CREATE TYPE facility_assignment_status AS ENUM (
    'ACTIVE',
    'DISABLED',
    'UNMAPPED'
);

CREATE TABLE facility_worker_assignment (
    id                        BIGSERIAL PRIMARY KEY,
    facility_id               BIGINT NOT NULL REFERENCES facility (id) ON DELETE CASCADE,
    health_worker_id          BIGINT NOT NULL REFERENCES health_worker (id) ON DELETE RESTRICT,
    role                      health_worker_role NOT NULL,
    status                    facility_assignment_status NOT NULL DEFAULT 'ACTIVE',
    supervisor_assignment_id  BIGINT REFERENCES facility_worker_assignment (id) ON DELETE SET NULL,
    created_by                BIGINT REFERENCES portal_user (id) ON DELETE SET NULL,
    created_at                TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at                TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT chk_supervisor_rules CHECK (
        (role = 'ASHA' AND supervisor_assignment_id IS NOT NULL)
        OR (role <> 'ASHA' AND supervisor_assignment_id IS NULL)
    )
);

CREATE INDEX idx_fwa_facility ON facility_worker_assignment (facility_id);
CREATE INDEX idx_fwa_worker ON facility_worker_assignment (health_worker_id);
CREATE INDEX idx_fwa_supervisor ON facility_worker_assignment (supervisor_assignment_id);

CREATE UNIQUE INDEX uq_one_active_cho_per_facility
    ON facility_worker_assignment (facility_id)
    WHERE role = 'CHO' AND status = 'ACTIVE';

CREATE UNIQUE INDEX uq_one_active_anm_per_facility
    ON facility_worker_assignment (facility_id)
    WHERE role = 'ANM' AND status = 'ACTIVE';

-- Form submissions: answers + frozen geography/staff context
CREATE TABLE data_submission (
    id              BIGSERIAL PRIMARY KEY,
    facility_id     BIGINT NOT NULL REFERENCES facility (id) ON DELETE RESTRICT,
    submitted_by_id BIGINT NOT NULL REFERENCES portal_user (id) ON DELETE RESTRICT,
    form_payload    JSONB NOT NULL DEFAULT '{}'::jsonb,
    staff_context   JSONB NOT NULL DEFAULT '{}'::jsonb,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_data_submission_facility_time ON data_submission (facility_id, created_at DESC);
CREATE INDEX idx_data_submission_user_time ON data_submission (submitted_by_id, created_at DESC);

COMMENT ON TABLE portal_role IS 'Login authorization roles only (ADMIN, DATA_COLLECTOR).';
COMMENT ON TABLE district IS 'District master.';
COMMENT ON TABLE block IS 'Block within a district.';
COMMENT ON TABLE facility IS 'HWC / collection site; district is implied via block.';
COMMENT ON TABLE portal_user IS 'Application user; role references portal_role.';
COMMENT ON TABLE portal_user_facility IS 'User to facility mapping; many HWCs per user; is_primary = default in UI.';
COMMENT ON TABLE health_worker IS 'Person master for facility roster.';
COMMENT ON TABLE facility_worker_assignment IS 'Staff role at facility; status = active/disabled/unmapped; ASHA links to facilitator row.';
COMMENT ON COLUMN facility_worker_assignment.supervisor_assignment_id IS 'ASHA only: FK to ASHA_FACILITATOR assignment (same facility enforced in application).';
COMMENT ON COLUMN data_submission.staff_context IS 'Snapshot of district/block/facility and staff at submit time.';
COMMENT ON COLUMN data_submission.form_payload IS 'Form answers JSON.';
