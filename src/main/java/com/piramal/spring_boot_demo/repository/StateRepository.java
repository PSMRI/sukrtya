package com.piramal.spring_boot_demo.repository;
 
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.piramal.spring_boot_demo.models.State;
@Repository
public interface StateRepository extends JpaRepository<State, String> {
    // Custom queries if needed
}
