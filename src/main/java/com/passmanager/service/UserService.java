package com.passmanager.service;

import com.passmanager.dto.RegisterDto;
import com.passmanager.entity.*;
import com.passmanager.repository.*;
import com.passmanager.security.CustomUserDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final SecurityQuestionRepository securityQuestionRepository;
    private final VerificationCodeRepository verificationCodeRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
                       SecurityQuestionRepository securityQuestionRepository,
                       VerificationCodeRepository verificationCodeRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.securityQuestionRepository = securityQuestionRepository;
        this.verificationCodeRepository = verificationCodeRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof CustomUserDetails) {
            CustomUserDetails details = (CustomUserDetails) auth.getPrincipal();
            return userRepository.findById(details.getId())
                    .orElseThrow(() -> new RuntimeException("User not found"));
        }
        throw new RuntimeException("No authenticated user");
    }

    @Transactional
    public User register(RegisterDto dto) {
        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("Email already registered");
        }
        if (!dto.getMasterPassword().equals(dto.getConfirmPassword())) {
            throw new IllegalArgumentException("Passwords do not match");
        }

        User user = User.builder()
                .username(dto.getUsername())
                .email(dto.getEmail())
                .masterPasswordHash(passwordEncoder.encode(dto.getMasterPassword()))
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .phoneNumber(dto.getPhoneNumber())
                .build();

        if (isNotEmpty(dto.getQuestion1()) && isNotEmpty(dto.getAnswer1())) {
            user.setSecurityQuestion1(dto.getQuestion1());
            user.setSecurityAnswer1Hash(passwordEncoder.encode(dto.getAnswer1().toLowerCase().trim()));
        }
        if (isNotEmpty(dto.getQuestion2()) && isNotEmpty(dto.getAnswer2())) {
            user.setSecurityQuestion2(dto.getQuestion2());
            user.setSecurityAnswer2Hash(passwordEncoder.encode(dto.getAnswer2().toLowerCase().trim()));
        }
        user = userRepository.save(user);

        log.info("New user registered: {}", user.getUsername());
        return user;
    }

    private boolean isNotEmpty(String s) {
        return s != null && !s.trim().isEmpty();
    }

    public boolean verifyMasterPassword(String password) {
        User user = getCurrentUser();
        return passwordEncoder.matches(password, user.getMasterPasswordHash());
    }

    @Transactional
    public void changeMasterPassword(String currentPassword, String newPassword) {
        User user = getCurrentUser();
        if (!passwordEncoder.matches(currentPassword, user.getMasterPasswordHash())) {
            throw new IllegalArgumentException("Current password is incorrect");
        }
        user.setMasterPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        log.info("Master password changed for user: {}", user.getUsername());
    }

    @Transactional
    public void updateProfile(User updates) {
        User user = getCurrentUser();
        if (updates.getFirstName() != null)
            user.setFirstName(updates.getFirstName());
        if (updates.getLastName() != null)
            user.setLastName(updates.getLastName());
        if (updates.getPhoneNumber() != null)
            user.setPhoneNumber(updates.getPhoneNumber());
        if (updates.getEmail() != null && !updates.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(updates.getEmail())) {
                throw new IllegalArgumentException("Email already in use");
            }
            user.setEmail(updates.getEmail());
        }
        userRepository.save(user);
    }

    public String generateVerificationCode(User user, VerificationCode.CodeType type) {
        verificationCodeRepository.deleteByUserAndType(user, type);
        String code = String.format("%06d", new Random().nextInt(999999));
        VerificationCode vc = VerificationCode.builder()
                .user(user).code(code).type(type)
                .expiresAt(LocalDateTime.now().plusMinutes(10))
                .build();
        verificationCodeRepository.save(vc);
        return code;
    }

    public boolean verifyCode(User user, String code, VerificationCode.CodeType type) {
        Optional<VerificationCode> vc = verificationCodeRepository
                .findByUserAndCodeAndUsedFalseAndType(user, code, type);
        if (vc.isPresent() && !vc.get().isExpired()) {
            vc.get().setUsed(true);
            verificationCodeRepository.save(vc.get());
            return true;
        }
        return false;
    }

    public Optional<User> findByEmail(String email) {
        if (email == null)
            return Optional.empty();
        return userRepository.findByEmail(email);
    }

    public List<SecurityQuestion> getSecurityQuestions(User user) {
        return securityQuestionRepository.findByUser(user);
    }

    public boolean verifySecurityAnswer(SecurityQuestion question, String answer) {
        return passwordEncoder.matches(answer.toLowerCase().trim(), question.getAnswerHash());
    }

    @Transactional
    public void resetPassword(User user, String newPassword) {
        user.setMasterPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    @Transactional
    public void toggle2FA(boolean enable, String secret) {
        User user = getCurrentUser();
        user.setTwoFactorEnabled(enable);
        user.setTwoFactorSecret(enable ? secret : null);
        userRepository.save(user);
    }

    // --- Forgot Password Methods (OTP & Questions) ---

    public String generateResetOtp(User user) {
        String otp = String.format("%06d", new Random().nextInt(999999));
        user.setResetOtp(otp);
        user.setResetOtpExpiry(LocalDateTime.now().plusMinutes(15));
        userRepository.save(user);
        return otp;
    }

    public boolean verifyResetOtp(User user, String otp) {
        if (user.getResetOtp() != null &&
                user.getResetOtp().equals(otp) &&
                user.getResetOtpExpiry() != null &&
                user.getResetOtpExpiry().isAfter(LocalDateTime.now())) {

            // Clear OTP after successful use
            user.setResetOtp(null);
            user.setResetOtpExpiry(null);
            userRepository.save(user);
            return true;
        }
        return false;
    }

    public boolean verifySecurityAnswers(User user, String answer1, String answer2) {
        if (user.getSecurityAnswer1Hash() == null || user.getSecurityAnswer2Hash() == null) {
            return false;
        }

        boolean match1 = passwordEncoder.matches(answer1.toLowerCase().trim(), user.getSecurityAnswer1Hash());
        boolean match2 = passwordEncoder.matches(answer2.toLowerCase().trim(), user.getSecurityAnswer2Hash());

        return match1 && match2;
    }

    @Transactional
    public void updateSecurityQuestions(String q1, String a1, String q2, String a2) {
        User user = getCurrentUser();
        if (isNotEmpty(q1) && isNotEmpty(a1)) {
            user.setSecurityQuestion1(q1);
            user.setSecurityAnswer1Hash(passwordEncoder.encode(a1.toLowerCase().trim()));
        }
        if (isNotEmpty(q2) && isNotEmpty(a2)) {
            user.setSecurityQuestion2(q2);
            user.setSecurityAnswer2Hash(passwordEncoder.encode(a2.toLowerCase().trim()));
        }
        userRepository.save(user);
    }
}
