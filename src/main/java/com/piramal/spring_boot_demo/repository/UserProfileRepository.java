package com.piramal.spring_boot_demo.repository;

import com.piramal.spring_boot_demo.models.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {
}
