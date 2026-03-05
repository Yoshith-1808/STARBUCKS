# 🔐 SecureVault - Password Manager

A full-stack, secure password manager built with **Spring Boot**, **Thymeleaf**, and **H2 embedded database**.

## 🚀 Quick Start

### Prerequisites
- **Java 17+** (JDK)
- **Maven 3.6+**

### Run the Application
```bash
cd password-manager
mvn spring-boot:run
```
Then open: **http://localhost:8080/auth/login**

> **No database installation required!** H2 embedded database is used automatically.

---

## 🔑 Default Demo Credentials
Register a new account at `/auth/register` — no pre-configured users.

---

## 🏗️ Architecture

```
password-manager/
├── src/main/java/com/passmanager/
│   ├── config/          # Spring Security configuration
│   ├── controller/      # MVC controllers (Auth, Vault, Generator, Security, Profile)
│   ├── dto/             # Data Transfer Objects
│   ├── entity/          # JPA entities (User, PasswordEntry, SecurityQuestion, VerificationCode)
│   ├── repository/      # Spring Data JPA repositories
│   ├── security/        # Custom UserDetails & UserDetailsService
│   ├── service/         # Business logic (UserService, VaultService)
│   └── util/            # Utilities (EncryptionUtil, PasswordStrengthUtil, PasswordGeneratorUtil)
└── src/main/resources/
    ├── templates/        # Thymeleaf HTML templates
    │   ├── auth/         # Login, Register, Forgot Password
    │   ├── dashboard/    # Main dashboard
    │   ├── vault/        # Password vault CRUD
    │   ├── generator/    # Password generator
    │   ├── security/     # Audit reports, 2FA
    │   └── profile/      # User profile management
    └── static/           # CSS and JS assets
```

## 🛡️ Security Features

| Feature | Implementation |
|---|---|
| Master Password Hashing | BCrypt (strength 12) |
| Vault Encryption | AES-256-CBC + PBKDF2 key derivation |
| Session Management | Spring Security Sessions (30 min timeout) |
| Two-Factor Auth | TOTP simulation (Google Authenticator compatible) |
| Security Questions | BCrypt hashed answers |
| Re-authentication | Required to view/decrypt stored passwords |
| Verification Codes | Single-use, 10-minute expiry |
| CSRF Protection | Spring Security CSRF tokens |

## 🗄️ Database (ERD)

**users** → (1:N) → **password_entries**  
**users** → (1:N) → **security_questions**  
**users** → (1:N) → **verification_codes**

H2 Console available at: `http://localhost:8080/h2-console`  
JDBC URL: `jdbc:h2:mem:passmanagerdb`

## 📋 Features

- ✅ User registration with security questions (min. 3)
- ✅ Secure login (username or email)
- ✅ Dashboard with vault summary
- ✅ Password vault — add, edit, delete, view entries
- ✅ Password categories (Banking, Social Media, Email, etc.)
- ✅ Search and filter passwords
- ✅ Favorite passwords
- ✅ Password reveal (requires master password re-entry)
- ✅ Password generator (configurable length, charset, exclude similar)
- ✅ Strength indicator (Weak/Medium/Strong/Very Strong)
- ✅ Save generated passwords directly to vault
- ✅ Security audit (weak passwords, reused passwords, score)
- ✅ Two-factor authentication (2FA) toggle
- ✅ Profile management (name, email, phone)
- ✅ Master password change
- ✅ Encrypted vault export
- ✅ Forgot password / security question recovery

## 🧪 Running Tests

```bash
mvn test
```

## 🛠️ Technologies

- **Java 17**
- **Spring Boot 3.2**
- **Spring Security**
- **Spring Data JPA**
- **Thymeleaf** (template engine)
- **H2** (embedded database)
- **BCrypt** (password hashing)
- **AES-256-CBC + PBKDF2** (vault encryption)
- **Bootstrap 5.3** (UI framework)
- **Log4j2** (logging)
- **JUnit 5** (testing)
- **Maven** (build tool)
