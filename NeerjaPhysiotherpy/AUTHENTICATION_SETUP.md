# Authentication & Authorization Setup - Neerja Physiotherapy Clinic

## Default Super Admin Credentials

**⚠️ IMPORTANT: Change these credentials immediately after first login**

```
Username: superadmin
Password: SuperAdmin@123
```

## Role-Based Access Control

### Roles Overview

| Role | Access | Restrictions |
|------|--------|--------------|
| **SUPER_ADMIN** | Full access to all features | Can create/manage users |
| **ADMIN** | All pages and reports | Cannot create users, cannot manage system users |
| **USER** | Limited access | Cannot access Audit & Analytics, cannot see payment info on home page |

### Role Permissions

#### SUPER_ADMIN
- ✅ View all patient records
- ✅ Access all reports (Invoice, Medical Certificate, Receipt)
- ✅ View Audit Trail
- ✅ View Revenue Analytics
- ✅ Create new users
- ✅ Manage user roles
- ✅ View payment information
- ✅ Access all billing features

#### ADMIN
- ✅ View all patient records
- ✅ Access all reports (Invoice, Medical Certificate, Receipt)
- ✅ View Audit Trail
- ✅ View Revenue Analytics
- ✅ View payment information
- ✅ Access all billing features
- ❌ Cannot create users
- ❌ Cannot manage other users

#### USER
- ✅ View patient records
- ✅ Access Invoice reports
- ✅ Access Medical Certificate reports
- ✅ Access Receipt reports
- ✅ View some billing features
- ❌ Cannot access Audit Trail
- ❌ Cannot access Revenue Analytics
- ❌ Cannot see payment information on home page

## Features

### Authentication
- Secure login page at `/login.html`
- Session-based authentication using sessionStorage
- Password encryption using SHA-256
- Session validation on page load

### User Management (SUPER_ADMIN Only)
- Create new users with assigned roles
- Assign roles: SUPER_ADMIN, ADMIN, USER
- Deactivate users
- Change passwords
- View all active users

### Security Features
- Passwords encrypted using SHA-256 algorithm
- Session tokens stored in browser sessionStorage
- Automatic redirect to login if session expires
- Role-based access control on UI
- Hidden payment/audit sections for USER role

## Setup Instructions

### 1. Initialize Super Admin
The system will automatically create the default super admin user on first run if it doesn't exist.

### 2. Login
1. Navigate to `/login.html`
2. Enter username: `superadmin`
3. Enter password: `SuperAdmin@123`
4. Click "Sign In"

### 3. Create Additional Users (SUPER_ADMIN Only)
After logging in as super admin, you can create new users through the user management interface.

### 4. Password Management
Users can change their password by navigating to:
- User Settings → Change Password
- Provide old password and new password

## API Endpoints

### Authentication Endpoints

#### Login
```
POST /api/auth/login?username={username}&password={password}
Response: { user: UserDTO, sessionId: String }
```

#### Create User (SUPER_ADMIN Only)
```
POST /api/auth/create-user
Parameters:
  - currentUserRole: User's role (must be SUPER_ADMIN)
  - username: New username
  - fullName: Full name
  - password: Password
  - role: Role (SUPER_ADMIN, ADMIN, or USER)
```

#### Get All Users (SUPER_ADMIN Only)
```
GET /api/auth/users?userRole={role}
Response: List of UserDTO
```

#### Deactivate User (SUPER_ADMIN Only)
```
POST /api/auth/deactivate-user?currentUserRole={role}&userId={id}
```

#### Change Password
```
POST /api/auth/change-password
Parameters:
  - userId: User ID
  - oldPassword: Current password
  - newPassword: New password
```

#### Verify Session
```
GET /api/auth/verify-session?sessionId={id}&username={username}
Response: { user: UserDTO, valid: boolean }
```

## User DTO Structure
```json
{
  "id": 1,
  "username": "superadmin",
  "fullName": "Super Administrator",
  "role": "Super Admin",
  "active": true
}
```

## Security Recommendations

1. **Change Default Password**: Immediately change the default super admin password after first login
2. **Strong Passwords**: Enforce strong password policies (minimum 8 characters, mixed case, numbers)
3. **Session Timeout**: Consider implementing session timeout for inactive users
4. **Audit Logging**: Track all user actions for compliance
5. **Regular Backups**: Backup user database regularly
6. **HTTPS**: Always use HTTPS in production to encrypt authentication data

## Troubleshooting

### Login Issues
- **"Invalid username or password"**: Check credentials (case-sensitive)
- **"Redirected to login page"**: Session expired - login again
- **"User not found"**: Verify username exists in system

### Password Reset
If the default super admin password is lost, contact database administrator to:
1. Reset password using direct database update
2. Or create a new super admin user entry

### Session Problems
- Clear sessionStorage if experiencing persistent login issues
- Check browser console for any JavaScript errors
- Verify API endpoints are accessible

## Password Encryption

Passwords are encrypted using SHA-256 algorithm:
```java
MessageDigest digest = MessageDigest.getInstance("SHA-256");
byte[] hash = digest.digest(password.getBytes("UTF-8"));
```

## Logout

Click the user icon in top-right navbar and select "Logout" to:
- Clear session data
- Redirect to login page
- End user session

---

**Last Updated**: May 28, 2026
**Version**: 1.0
