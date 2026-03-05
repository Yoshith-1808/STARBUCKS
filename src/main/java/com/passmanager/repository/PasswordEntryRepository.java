package com.passmanager.repository;

import com.passmanager.entity.PasswordEntry;
import com.passmanager.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface PasswordEntryRepository extends JpaRepository<PasswordEntry, Long> {
    List<PasswordEntry> findByUserOrderByCreatedAtDesc(User user);
    List<PasswordEntry> findByUserAndFavoriteTrueOrderByAccountNameAsc(User user);
    List<PasswordEntry> findByUserAndCategoryOrderByAccountNameAsc(User user, PasswordEntry.Category category);

    @Query("SELECT p FROM PasswordEntry p WHERE p.user = :user AND " +
           "(LOWER(p.accountName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(p.websiteUrl) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(p.usernameOrEmail) LIKE LOWER(CONCAT('%', :query, '%')))")
    List<PasswordEntry> searchByUser(@Param("user") User user, @Param("query") String query);

    long countByUser(User user);
    List<PasswordEntry> findByUserOrderByAccountNameAsc(User user);
    List<PasswordEntry> findByUserOrderByUpdatedAtDesc(User user);
}
