package com.sukrtya.siwan.config;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.sukrtya.siwan.portal.PortalUserProvisioningService;

/**
 * Seeds a default {@code ADMIN} portal user on startup when none exists for the configured username.
 */
@Component
@Order(0)
public class DefaultAdminUserInitializer implements ApplicationRunner {

	private final PortalUserProvisioningService portalUserProvisioningService;

	public DefaultAdminUserInitializer(PortalUserProvisioningService portalUserProvisioningService) {
		this.portalUserProvisioningService = portalUserProvisioningService;
	}

	@Override
	public void run(ApplicationArguments args) {
		portalUserProvisioningService.ensureDefaultAdmin();
	}
}
