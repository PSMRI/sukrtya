package com.sukrtya.siwan.beneficiary;

import java.time.LocalDate;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.stereotype.Service;

/** Generates short, readable, unique beneficiary codes (e.g. {@code BEN-2026-038492}). */
@Service
public class BeneficiaryCodeService {

	private final BeneficiaryRepository beneficiaryRepository;

	public BeneficiaryCodeService(BeneficiaryRepository beneficiaryRepository) {
		this.beneficiaryRepository = beneficiaryRepository;
	}

	public String nextCode() {
		ThreadLocalRandom rnd = ThreadLocalRandom.current();
		int year = LocalDate.now().getYear();
		for (int attempt = 0; attempt < 50; attempt++) {
			String code = String.format("BEN-%d-%06d", year, rnd.nextInt(1_000_000));
			if (!beneficiaryRepository.existsByCode(code)) {
				return code;
			}
		}
		// Extremely unlikely after 50 collisions; fall back to nanoTime suffix for guaranteed uniqueness.
		return "BEN-" + year + "-" + Math.abs(System.nanoTime() % 1_000_000);
	}
}
