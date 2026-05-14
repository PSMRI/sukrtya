package com.sukrtya.siwan.master;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface HealthWorkerRepository extends JpaRepository<HealthWorker, Long> {

	Optional<HealthWorker> findFirstByMobilePhone(String mobilePhone);

	Optional<HealthWorker> findFirstByFullNameIgnoreCaseAndMobilePhoneIsNull(String fullName);
}
