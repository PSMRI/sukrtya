-- Rename excel_staff_header -> staff_type (column lives on facility_worker_assignment, not health_worker)

DO $$
BEGIN
	IF EXISTS (
		SELECT 1
		FROM information_schema.columns
		WHERE table_schema = 'public'
		  AND table_name = 'facility_worker_assignment'
		  AND column_name = 'excel_staff_header'
	) THEN
		ALTER TABLE facility_worker_assignment RENAME COLUMN excel_staff_header TO staff_type;
	END IF;
END
$$;

COMMENT ON COLUMN facility_worker_assignment.staff_type IS
	'Staff slot label from the Excel header row (e.g. CHO Name, ANM Name, ASHA Facilitator, Asha Name).';
