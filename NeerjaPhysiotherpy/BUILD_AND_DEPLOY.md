# Build and Deployment Instructions

## Prerequisites
- Java 25 installed
- Maven 3.6+ installed
- MySQL 8.0+ running
- Database connection configured in `application.properties`

## Building the Project

### Step 1: Clean and Build
```bash
cd C:\Users\a836341\OneDrive - ATOS\Sarang\new_workspace\version-4.8\NeerjaPhysiotherpy

# On Windows CMD:
mvnw.cmd clean package -DskipTests

# Or use Maven directly:
mvn clean package -DskipTests
```

### Step 2: Verify Build Output
The build should create:
- `target/NeerjaPhysiotherpy-1.0.0.jar` - The application JAR file
- Updated static resources in `target/classes/static/`

## Running the Application

### Option 1: Using Maven (Development)
```bash
mvnw.cmd spring-boot:run
```

### Option 2: Using Java JAR (Production)
```bash
java -jar target/NeerjaPhysiotherpy-1.0.0.jar
```

### Default Configuration
- **Server Port:** 8080
- **Base URL:** http://localhost:8080

## Accessing the Application

1. Open your web browser
2. Navigate to: `http://localhost:8080/login.html`
3. Login with Super Admin credentials:
   - **Username:** superadmin
   - **Password:** SuperAdmin@123

## Verification Checklist

After deployment, verify the fixes by testing:

### Test 1: Super Admin User Creation
- [ ] Login as superadmin
- [ ] Navigate to User Management menu (should be visible)
- [ ] Try creating a new user
- [ ] Verify: User is created successfully without role error

### Test 2: Receipt Display
- [ ] Go to Invoice & Receipt tab
- [ ] Click "Refresh" button
- [ ] Create a test receipt if none exist
- [ ] Verify: Receipts display with patient names in the table

### Test 3: Role-Based Access Control
- [ ] Verify Super Admin can see all admin menu items
- [ ] Create a regular USER account
- [ ] Login with USER account
- [ ] Verify: Admin menu items are hidden
- [ ] Verify: Analytics and Audit sections are hidden

## Database Changes Required

The application uses Flyway for database migrations. No manual SQL changes are needed as:
1. The users table is created by the existing SQL scripts
2. The receipts table structure is unchanged
3. Only query optimization was done in the application layer

## Troubleshooting

### Issue: "Only Super Admin can create users" still appears
**Solution:** 
- Clear browser cache (Ctrl+Shift+Delete)
- Make sure the JAR file is rebuilt with the latest code
- Verify UserDTO is returning role.name() not role.getDisplayName()

### Issue: Receipts still not showing
**Solution:**
- Check that the ReceiptService uses `findAllWithPatientAndPayment()`
- Ensure database has receipt records with valid patient_id references
- Check browser console for JavaScript errors

### Issue: Build fails
**Solution:**
- Verify Java 25 is installed: `java -version`
- Clean Maven cache: `mvnw.cmd clean`
- Delete `target` folder and rebuild

## Rollback Instructions

If you need to revert to the previous version:

1. Restore from backup:
   ```bash
   git checkout HEAD -- src/
   ```

2. Rebuild and redeploy:
   ```bash
   mvnw.cmd clean package -DskipTests
   ```

## Support

For issues or questions about the fixes, refer to `FIXES_APPLIED.md` for detailed technical information.
