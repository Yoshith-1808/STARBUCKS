package com.passmanager.repository;

import com.passmanager.entity.SecurityQuestion;
import com.passmanager.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SecurityQuestionRepository extends JpaRepository<SecurityQuestion, Long> {
    List<SecurityQuestion> findByUser(User user);
    long countByUser(User user);
    void deleteByUser(User user);
}
