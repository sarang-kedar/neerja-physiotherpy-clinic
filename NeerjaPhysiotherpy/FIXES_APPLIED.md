# Neerja Physiotherapy - Bug Fixes Applied

## Issues Resolved

### Issue 1: Super Admin cannot create users - "Only Super Admin can create users" error

**Root Cause:**
The `UserDTO.fromEntity()` method was converting the Role enum to its display name ("Super Admin") instead of the enum name ("SUPER_ADMIN"). The frontend was sending this display name to the backend, but the backend was checking for the enum name, causing the role validation to fail.

**Solution:**
Modified `UserDTO.fromEntity()` to use `user.getRole().name()` instead of `user.getRole().getDisplayName()`. This returns the enum name "SUPER_ADMIN" which matches the backend's role verification logic.

**Files Changed:**
1. **src/main/java/com/neerjaphysio/dto/UserDTO.java**
   - Changed line 28: `user.getRole().name()` (was: `user.getRole().getDisplayName()`)

2. **src/main/resources/static/index.html**
   - Line 1508: Updated role check from 'Super Admin' && 'Admin' to 'SUPER_ADMIN' && 'ADMIN'
   - Line 2647: Updated role check from 'Super Admin' to 'SUPER_ADMIN'

### Issue 2: Receipts not displaying in the Receipt & Invoice tab

**Root Cause:**
The Receipt entity uses lazy loading (`FetchType.LAZY`) for the patient relationship. When the API endpoint returns receipts, the patient entity was not being eagerly loaded. This caused the `patientName` field in the ReceiptDTO to be null or inaccessible, resulting in empty receipts in the table.

**Solution:**
Added a custom query method to the ReceiptRepository that uses LEFT JOIN FETCH to eagerly load the patient and payment relationships. Updated the ReceiptService to use this new method instead of the generic `findAll()`.

**Files Changed:**
1. **src/main/java/com/neerjaphysio/repository/ReceiptRepository.java**
   - Added custom query method: `findAllWithPatientAndPayment()`
   - Uses JPQL: `SELECT DISTINCT r FROM Receipt r LEFT JOIN FETCH r.patient LEFT JOIN FETCH r.payment ORDER BY r.createdAt DESC`

2. **src/main/java/com/neerjaphysio/service/ReceiptService.java**
   - Updated `listAll()` method to use `receiptRepository.findAllWithPatientAndPayment()`
   - Removed the in-memory sorting (now handled by the query)

## Testing Steps

1. **Test Super Admin User Creation:**
   - Log in as superadmin (username: superadmin, password: SuperAdmin@123)
   - Navigate to User Management section
   - Try to create a new user
   - Expected: User should be created successfully without the "Only Super Admin can create users" error

2. **Test Receipt Display:**
   - Go to the Invoice & Receipt tab
   - Click "Refresh" to load receipts
   - Expected: Receipts with patient names should display in the table

## Technical Details

### Role Enum Values
- `SUPER_ADMIN` - Super Administrator (has full access)
- `ADMIN` - Administrator
- `USER` - Regular User

The display names are still available via `Role.getDisplayName()` for UI purposes, but the enum name is now used for role-based authorization checks.

### Database Query Optimization
The new query using LEFT JOIN FETCH ensures that:
- All receipts are fetched with their related patient and payment entities in a single query
- Prevents N+1 query problem
- Reduces database load
- Ensures patient names are available in the DTO conversion
