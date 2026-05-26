-- =============================================================================
-- Sukrtya Siwan — forms engine (admin-configurable surveys + beneficiary tracking)
--
-- Design summary:
--   * `form` is the admin-managed catalog of survey types (BASIC, ANC1..4, PNC1, ...).
--   * `form_question` rows are owned by a form and are re-loaded from Excel on each
--     admin upload. Stable `code` = "<FORM_CODE>_<faQId>" so answers JSON keys survive
--     re-uploads as long as faQId stays the same.
--   * `option_set` + `option_value` come from the `OptionMaster` sheet (shared across
--     forms by `code`).
--   * `beneficiary` is the pregnant-woman master, linked to a single ASHA assignment.
--   * `beneficiary_form_response` is the ONE table for every form answer ever — keyed
--     by (beneficiary_id, form_id) so each woman has at most one row per form.
-- =============================================================================

-- V1 created `data_submission` but nothing writes to it; superseded by beneficiary_form_response.
DROP TABLE IF EXISTS data_submission;

-- ============================================================================
-- Form catalog (admin-managed; each row = one survey type)
-- ============================================================================
CREATE TABLE form (
	id                  BIGSERIAL PRIMARY KEY,
	code                VARCHAR(50)  NOT NULL UNIQUE,
	name                VARCHAR(200) NOT NULL,
	sequence            INT          NOT NULL DEFAULT 0,
	prerequisite_code   VARCHAR(50),
	description         TEXT,
	is_active           BOOLEAN      NOT NULL DEFAULT TRUE,
	version             INT          NOT NULL DEFAULT 0,
	created_by          BIGINT REFERENCES portal_user (id) ON DELETE SET NULL,
	created_at          TIMESTAMPTZ  NOT NULL DEFAULT now(),
	updated_at          TIMESTAMPTZ  NOT NULL DEFAULT now()
);

COMMENT ON TABLE  form IS 'Catalog of survey types (BASIC, ANC1..4, PNC, etc.). Admin-managed.';
COMMENT ON COLUMN form.code IS 'Stable short code referenced by Excel uploads and response rows.';
COMMENT ON COLUMN form.sequence IS 'Display order in the data-collection UI.';
COMMENT ON COLUMN form.prerequisite_code IS 'Form code that must be completed before this form is enabled (nullable).';
COMMENT ON COLUMN form.version IS 'Incremented each time the question Excel is uploaded for this form.';

-- ============================================================================
-- Option sets (shared dropdown values: YES_NO, HRP_TYPES, CATEGORISATION, ...)
-- ============================================================================
CREATE TABLE option_set (
	id          BIGSERIAL    PRIMARY KEY,
	code        VARCHAR(50)  NOT NULL UNIQUE,
	name        VARCHAR(200) NOT NULL,
	created_at  TIMESTAMPTZ  NOT NULL DEFAULT now(),
	updated_at  TIMESTAMPTZ  NOT NULL DEFAULT now()
);

COMMENT ON TABLE option_set IS 'Named dropdown option groups loaded from the OptionMaster sheet.';

CREATE TABLE option_value (
	id            BIGSERIAL    PRIMARY KEY,
	option_set_id BIGINT       NOT NULL REFERENCES option_set (id) ON DELETE CASCADE,
	value         VARCHAR(50)  NOT NULL,
	label_en      VARCHAR(300) NOT NULL,
	label_hi      VARCHAR(300),
	sequence      INT          NOT NULL DEFAULT 0,
	UNIQUE (option_set_id, value)
);

CREATE INDEX idx_option_value_set ON option_value (option_set_id);

COMMENT ON TABLE option_value IS 'Individual options inside an option_set. `value` is what gets stored in answers JSON.';

-- ============================================================================
-- Form questions (replaced wholesale per form on each Excel upload)
-- ============================================================================
CREATE TABLE form_question (
	id                    BIGSERIAL   PRIMARY KEY,
	form_id               BIGINT      NOT NULL REFERENCES form (id) ON DELETE CASCADE,
	fa_qid                INT         NOT NULL,
	code                  VARCHAR(80) NOT NULL,
	label_en              VARCHAR(500) NOT NULL,
	label_hi              VARCHAR(500),
	answer_type           VARCHAR(20) NOT NULL,
	is_mandatory          BOOLEAN     NOT NULL DEFAULT FALSE,
	min_value             VARCHAR(50),
	max_value             VARCHAR(50),
	default_value         VARCHAR(200),
	option_set_code       VARCHAR(50),
	skip_value            VARCHAR(50),
	skip_to_qid           INT,
	computed_kind         VARCHAR(30),
	computed_source_code  VARCHAR(80),
	computed_offset_days  INT,
	sequence              INT         NOT NULL DEFAULT 0,
	remarks               TEXT,
	UNIQUE (form_id, fa_qid),
	UNIQUE (form_id, code),
	CONSTRAINT chk_form_question_answer_type CHECK (
		answer_type IN ('TEXT', 'NUMERIC', 'DATE', 'SINGLE_CHOICE', 'MULTI_CHOICE')
	)
);

CREATE INDEX idx_form_question_form ON form_question (form_id);

COMMENT ON TABLE  form_question IS 'Questions for a form. Wholesale replaced on each Excel upload for that form.';
COMMENT ON COLUMN form_question.code IS 'Stable key (default "<FORM_CODE>_<faQId>") used as the JSON key in beneficiary_form_response.answers.';
COMMENT ON COLUMN form_question.option_set_code IS 'For SINGLE_CHOICE / MULTI_CHOICE: joins to option_set.code.';
COMMENT ON COLUMN form_question.computed_kind IS 'Optional computed-field marker (e.g. DATE_OFFSET). Derived from the Excel Remarks column.';
COMMENT ON COLUMN form_question.computed_source_code IS 'Question code this field is computed from (e.g. BASIC_16 = LMP).';
COMMENT ON COLUMN form_question.computed_offset_days IS 'Day offset for DATE_OFFSET computed fields (e.g. 280 for EDD, 84 for ANC1 due).';

-- ============================================================================
-- Beneficiary (pregnant woman master)
-- ============================================================================
CREATE TABLE beneficiary (
	id                  BIGSERIAL    PRIMARY KEY,
	facility_id         BIGINT       NOT NULL REFERENCES facility (id) ON DELETE RESTRICT,
	asha_assignment_id  BIGINT       NOT NULL REFERENCES facility_worker_assignment (id) ON DELETE RESTRICT,
	code                VARCHAR(40)  NOT NULL UNIQUE,
	full_name           VARCHAR(200) NOT NULL,
	husband_name        VARCHAR(200),
	mobile_phone        VARCHAR(20),
	abha_id             VARCHAR(50),
	village             VARCHAR(200),
	age                 INT,
	lmp_date            DATE,
	edd_date            DATE,
	status              VARCHAR(20)  NOT NULL DEFAULT 'ACTIVE',
	created_by          BIGINT REFERENCES portal_user (id) ON DELETE SET NULL,
	created_at          TIMESTAMPTZ  NOT NULL DEFAULT now(),
	updated_at          TIMESTAMPTZ  NOT NULL DEFAULT now(),
	CONSTRAINT chk_beneficiary_status CHECK (
		status IN ('ACTIVE', 'DELIVERED', 'LOST_TO_FOLLOWUP', 'INVALID')
	)
);

CREATE INDEX idx_beneficiary_facility ON beneficiary (facility_id);
CREATE INDEX idx_beneficiary_asha     ON beneficiary (asha_assignment_id);
CREATE INDEX idx_beneficiary_status   ON beneficiary (status);

COMMENT ON TABLE  beneficiary IS 'Pregnant-woman master record. Linked to the registering ASHA assignment.';
COMMENT ON COLUMN beneficiary.code IS 'Human-readable system code (e.g. BEN-2025-000123).';
COMMENT ON COLUMN beneficiary.edd_date IS 'Expected Date of Delivery — typically lmp_date + 280 days. Stored for fast filtering.';

-- ============================================================================
-- Beneficiary form responses (single table for ALL form answers)
-- ============================================================================
CREATE TABLE beneficiary_form_response (
	id                BIGSERIAL    PRIMARY KEY,
	beneficiary_id    BIGINT       NOT NULL REFERENCES beneficiary (id) ON DELETE CASCADE,
	form_id           BIGINT       NOT NULL REFERENCES form (id) ON DELETE RESTRICT,
	form_version      INT          NOT NULL,
	status            VARCHAR(20)  NOT NULL DEFAULT 'DRAFT',
	answers           JSONB        NOT NULL DEFAULT '{}'::jsonb,
	submitted_by_id   BIGINT REFERENCES portal_user (id) ON DELETE SET NULL,
	submitted_at      TIMESTAMPTZ,
	created_at        TIMESTAMPTZ  NOT NULL DEFAULT now(),
	updated_at        TIMESTAMPTZ  NOT NULL DEFAULT now(),
	UNIQUE (beneficiary_id, form_id),
	CONSTRAINT chk_bfr_status CHECK (status IN ('DRAFT', 'SUBMITTED'))
);

CREATE INDEX idx_bfr_beneficiary ON beneficiary_form_response (beneficiary_id);
CREATE INDEX idx_bfr_form        ON beneficiary_form_response (form_id);
CREATE INDEX idx_bfr_answers_gin ON beneficiary_form_response USING gin (answers jsonb_path_ops);

COMMENT ON TABLE  beneficiary_form_response IS 'Single table for every form response across every survey type. Keyed by JSONB question code.';
COMMENT ON COLUMN beneficiary_form_response.form_version IS 'Snapshot of form.version at submission time; helps re-render the row with the schema that captured it.';
COMMENT ON COLUMN beneficiary_form_response.answers IS 'Map of question code -> answer value (string, number, ISO date, or array for multi-choice).';
