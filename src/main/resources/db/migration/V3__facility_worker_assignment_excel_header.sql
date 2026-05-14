-- Preserve original Excel column header per facility staff row (CHO Name, ANM Name, etc.)

ALTER TABLE facility_worker_assignment
    ADD COLUMN IF NOT EXISTS excel_staff_header varchar(200);

COMMENT ON COLUMN facility_worker_assignment.excel_staff_header IS
    'Excel header text for this slot (e.g. CHO Name, ANM Name, ASHA Facilitator, Asha Name) at import time.';
