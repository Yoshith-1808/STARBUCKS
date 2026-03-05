package com.passmanager.repository;

import com.passmanager.entity.User;
import com.passmanager.entity.VerificationCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

public interface VerificationCodeRepository extends JpaRepository<VerificationCode, Long> {
    Optional<VerificationCode> findByUserAndCodeAndUsedFalseAndType(User user, String code, VerificationCode.CodeType type);
    
    @Modifying
    @Query("DELETE FROM VerificationCode v WHERE v.user = :user AND v.type = :type")
    void deleteByUserAndType(@Param("user") User user, @Param("type") VerificationCode.CodeType type);
}
