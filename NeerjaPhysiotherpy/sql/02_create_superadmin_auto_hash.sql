-- =====================================================
-- Neerja Physiotherapy Clinic - Super Admin Setup SQL
-- Using MySQL SHA2() function to generate SHA-256 hash
-- =====================================================

-- Create users table if it doesn't exist
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    full_name VARCHAR(100) NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL,
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_username (username),
    INDEX idx_role (role),
    INDEX idx_active (active)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Insert default super admin user with SHA2 hash generation
-- Plain password: SuperAdmin@123
INSERT INTO users (username, full_name, password, role, active, created_at)
VALUES (
    'superadmin',
    'Super Administrator',
    SHA2('SuperAdmin@123', 256),
    'SUPER_ADMIN',
    TRUE,
    NOW()
)
ON DUPLICATE KEY UPDATE
    full_name = 'Super Administrator',
    password = SHA2('SuperAdmin@123', 256),
    role = 'SUPER_ADMIN',
    active = TRUE;

-- Verify the insertion and show the password hash
SELECT 
    id,
    username,
    full_name,
    password AS password_hash,
    role,
    active,
    created_at
FROM users
WHERE username = 'superadmin';

-- Display all users
SELECT 
    id,
    username,
    full_name,
    role,
    active,
    created_at,
    updated_at
FROM users
ORDER BY created_at DESC;
