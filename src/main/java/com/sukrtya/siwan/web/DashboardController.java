package com.sukrtya.siwan.web;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sukrtya.siwan.master.DashboardStatsService;
import com.sukrtya.siwan.web.dto.DashboardSummaryResponse;

@RestController
@RequestMapping(value = "/api/admin/dashboard", produces = MediaType.APPLICATION_JSON_VALUE)
public class DashboardController {

	private final DashboardStatsService dashboardStatsService;

	public DashboardController(DashboardStatsService dashboardStatsService) {
		this.dashboardStatsService = dashboardStatsService;
	}

	@GetMapping("/summary")
	public DashboardSummaryResponse summary() {
		return dashboardStatsService.buildSummary();
	}
}
