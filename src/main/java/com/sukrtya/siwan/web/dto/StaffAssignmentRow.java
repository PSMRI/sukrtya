package com.sukrtya.siwan.web.dto;

public record StaffAssignmentRow(
		long assignmentId,
		String role,
		String status,
		String staffType,
		HealthWorkerSummary healthWorker,
		Long supervisorAssignmentId) {
}
