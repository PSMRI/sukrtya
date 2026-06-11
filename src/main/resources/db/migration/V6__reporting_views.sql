-- =============================================================================
-- Sukrtya Siwan — reporting views over JSONB form answers
--
-- Purpose:
--   Provide tabular SQL-friendly access to answers stored in
--   beneficiary_form_response.answers (jsonb) without changing the flexible
--   storage model.
--
-- Notes:
--   * Views are read-only projections.
--   * v_form_answers_long is the canonical "one row per answer key" view and
--     works for every current/future form.
--   * Convenience views filter the long view for common form codes.
-- =============================================================================

-- One row per (beneficiary, form, question_code). Best for exports and BI tools.
CREATE OR REPLACE VIEW v_form_answers_long AS
SELECT
	b.id                         AS beneficiary_id,
	b.code                       AS beneficiary_code,
	b.full_name                  AS beneficiary_full_name,
	b.husband_name               AS beneficiary_husband_name,
	b.mobile_phone               AS beneficiary_mobile_phone,
	b.abha_id                    AS beneficiary_abha_id,
	b.village                    AS beneficiary_village,
	b.age                        AS beneficiary_age,
	b.lmp_date                   AS beneficiary_lmp_date,
	b.edd_date                   AS beneficiary_edd_date,
	b.status                     AS beneficiary_status,
	b.facility_id                AS facility_id,
	b.asha_assignment_id         AS asha_assignment_id,

	f.id                         AS form_id,
	f.code                       AS form_code,
	f.name                       AS form_name,
	f.sequence                   AS form_sequence,
	f.prerequisite_code          AS form_prerequisite_code,

	r.id                         AS response_id,
	r.form_version               AS response_form_version,
	r.status                     AS response_status,
	r.submitted_by_id            AS response_submitted_by_id,
	r.submitted_at               AS response_submitted_at,
	r.created_at                 AS response_created_at,
	r.updated_at                 AS response_updated_at,

	q.key                        AS question_code,
	q.value                      AS answer_text,
	r.answers                    AS answers_jsonb
FROM beneficiary_form_response r
JOIN beneficiary b ON b.id = r.beneficiary_id
JOIN form f ON f.id = r.form_id
LEFT JOIN LATERAL jsonb_each_text(r.answers) q ON TRUE;

COMMENT ON VIEW v_form_answers_long IS
	'Long/tabular view of all answers: one row per JSON key in beneficiary_form_response.answers.';

-- Convenience filtered views for commonly used codes.
CREATE OR REPLACE VIEW v_basic_answers_long AS
SELECT * FROM v_form_answers_long WHERE form_code = 'BASIC';

CREATE OR REPLACE VIEW v_anc1_answers_long AS
SELECT * FROM v_form_answers_long WHERE form_code = 'ANC1';

CREATE OR REPLACE VIEW v_anc2_answers_long AS
SELECT * FROM v_form_answers_long WHERE form_code = 'ANC2';

CREATE OR REPLACE VIEW v_anc3_answers_long AS
SELECT * FROM v_form_answers_long WHERE form_code = 'ANC3';

CREATE OR REPLACE VIEW v_anc4_answers_long AS
SELECT * FROM v_form_answers_long WHERE form_code = 'ANC4';

