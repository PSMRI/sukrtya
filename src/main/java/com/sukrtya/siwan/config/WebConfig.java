package com.sukrtya.siwan.config;

import java.util.Arrays;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * CORS via Spring MVC (same style as legacy Sukrtya apps). Spring Security {@code http.cors()}
 * picks this up when no {@code CorsConfigurationSource} bean is defined.
 * <p>
 * Set comma-separated {@code FRONTEND_URL} and optional {@code BACKEND_URL} (env or
 * {@code application.properties}). Uses {@link CorsRegistry#allowedOriginPatterns(String...)} so
 * {@code allowCredentials(true)} is allowed.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

	private final String frontendUrls;
	private final String backendUrls;

	public WebConfig(
			@Value("${FRONTEND_URL:http://localhost:3000,https://sukrtya-siwan.psmri.in}") String frontendUrls,
			@Value("${BACKEND_URL:}") String backendUrls) {
		this.frontendUrls = frontendUrls;
		this.backendUrls = backendUrls;
	}

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		String[] allowedOriginPatterns = Stream.concat(
				Arrays.stream(frontendUrls.split(",")),
				Arrays.stream(backendUrls.split(",")))
				.map(String::trim)
				.filter(url -> !url.isEmpty())
				.toArray(String[]::new);

		registry.addMapping("/**")
				.allowedOriginPatterns(allowedOriginPatterns)
				.allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
				.allowedHeaders("*")
				.exposedHeaders("Authorization", "Content-Type", "Content-Disposition")
				.allowCredentials(true)
				.maxAge(3600);
	}
}
