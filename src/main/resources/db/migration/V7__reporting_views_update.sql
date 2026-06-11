-- =============================================================================
-- Sukrtya Siwan — reporting views update
--
-- Flyway note:
--   Never edit already-applied migrations (like V6). Add a new migration that
--   CREATE OR REPLACE's the views instead.
--
-- What changed:
--   * Add convenience long view for newly added PNC form.
--   * Keep the canonical v_form_answers_long unchanged (it already supports any
--     new form automatically).
-- =============================================================================

-- Convenience filtered view for PNC (new form).
CREATE OR REPLACE VIEW v_pnc_answers_long AS
SELECT * FROM v_form_answers_long WHERE form_code = 'PNC';

