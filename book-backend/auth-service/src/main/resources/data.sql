-- Ensure the 'users' table exists
CREATE TABLE IF NOT EXISTS "users" (
                                       id UUID PRIMARY KEY,
                                       username VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL
    );

-- Insert the test user if no existing user with the same id or username exists
INSERT INTO "users" (id, username, password)
SELECT '223e4567-e89b-12d3-a456-426614174006', 'testuser',
       '$2b$12$7hoRZfJrRKD2nIm2vHLs7OBETy.LWenXXMLKf99W8M4PUwO6KB7fu'
    WHERE NOT EXISTS (
    SELECT 1
    FROM "users"
    WHERE id = '223e4567-e89b-12d3-a456-426614174006'
       OR username = 'testuser'
);

-- Insert admin user with fixed UUID
INSERT INTO "users" (id, username, password)
SELECT '11111111-1111-1111-1111-111111111111', 'admin',
       '$2b$12$7hoRZfJrRKD2nIm2vHLs7OBETy.LWenXXMLKf99W8M4PUwO6KB7fu'
    WHERE NOT EXISTS (
    SELECT 1
    FROM "users"
    WHERE id = '11111111-1111-1111-1111-111111111111'
       OR username = 'admin'
);

-- Insert user roles
INSERT INTO user_roles (user_id, role) VALUES
('223e4567-e89b-12d3-a456-426614174006', 'ROLE_USER'),
('11111111-1111-1111-1111-111111111111', 'ROLE_USER'),
('11111111-1111-1111-1111-111111111111', 'ROLE_ADMIN')
ON CONFLICT DO NOTHING;