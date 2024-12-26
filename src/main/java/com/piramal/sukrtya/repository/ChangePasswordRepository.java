package com.piramal.sukrtya.repository;

import com.piramal.sukrtya.entity.TblUser;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface ChangePasswordRepository extends JpaRepository<TblUser, String> {

    @Query(value = "SELECT userpassword FROM tbluser WHERE username = :username", nativeQuery = true)
    String findPasswordByUsername(@Param("username") String username);

    @Modifying
    @Transactional
    @Query(value = "UPDATE tbluser SET userpassword = :newPassword WHERE username = :username", nativeQuery = true)
    int updatePassword(@Param("username") String username, @Param("newPassword") String newPassword);
}
