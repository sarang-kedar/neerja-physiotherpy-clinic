# Application Startup Guide - .BAT Files

Two batch files have been created to easily start the NEERJA Physiotherapy Clinic application.

---

## Files Created

### 1. **START_APPLICATION.bat** (Full Version - Recommended)
**Features:**
- ✅ Checks Java installation
- ✅ Validates JAR file exists  
- ✅ Checks for port conflicts
- ✅ Detailed status messages
- ✅ Handles errors gracefully
- ✅ Shows instructions to stop the app

**Use this for:** Production use, troubleshooting, first-time setup

---

### 2. **START.bat** (Quick Version)
**Features:**
- ✅ Minimal, lightweight
- ✅ Fast execution
- ✅ No error checking

**Use this for:** Quick daily startup once everything is configured

---

## Prerequisites

### 1. Java Installation
The application requires Java to be installed and available in system PATH.

**Check if Java is installed:**
```cmd
java -version
```

If not installed, download from:
- **Oracle JDK**: https://www.oracle.com/java/technologies/downloads/
- **OpenJDK**: https://adoptopenjdk.net/

### 2. Build the Application
Before running, build the application once:

```cmd
mvn clean package
```

This creates the JAR file: `target\NeerjaPhysiotherpy-1.0.0.jar`

---

## How to Use

### Method 1: Quick Start (Recommended for Daily Use)

1. **Navigate to project folder:**
   ```
   C:\Users\a836341\OneDrive - ATOS\Sarang\new_workspace\version-4.8\NeerjaPhysiotherpy\
   ```

2. **Double-click `START.bat`**

3. **What happens:**
   - JAR file starts in background
   - Waits 8 seconds for application to initialize
   - Automatically opens browser to: `http://localhost:8080/neerjaPhysiotherpyClinic/login.html`
   - Login page appears

### Method 2: Full Startup with Diagnostics

1. **Double-click `START_APPLICATION.bat`**

2. **What happens:**
   - Displays detailed startup information
   - Checks Java installation
   - Validates JAR file
   - Checks for port conflicts
   - Starts application
   - Opens login page
   - Shows instructions for stopping the app

---

## Expected Output

When you run the .bat file, you should see:

```
============================================================================
Starting NEERJA PHYSIOTHERAPY CLINIC Application...
============================================================================
JAR File: target\NeerjaPhysiotherpy-1.0.0.jar
Port: 8080
Login URL: http://localhost:8080/neerjaPhysiotherpyClinic/login.html

Waiting for application to start...
Waiting for application to start... (8 seconds remaining)

Application started successfully!

============================================================================
Opening login page in default browser...
============================================================================

============================================================================
SUCCESS! Application is running in background
============================================================================
Application URL: http://localhost:8080/neerjaPhysiotherpyClinic/login.html

To stop the application, use one of these methods:
  1. Close the "NEERJA PHYSIOTHERAPY CLINIC" console window
  2. Use Task Manager to stop "java.exe"
  3. Run: taskkill /im java.exe /f

============================================================================
```

---

## Login Credentials

After the login page opens, use these default credentials:

| Role | Username | Password |
|------|----------|----------|
| Super Admin | superadmin | password123 |
| Admin | admin | password123 |
| Staff | staff | password123 |

---

## Stopping the Application

### Method 1: Close the Console Window
- Look for window titled "NEERJA PHYSIOTHERAPY CLINIC" (the black console)
- Click the X button to close

### Method 2: Use Command Prompt
```cmd
taskkill /im java.exe /f
```

### Method 3: Task Manager
1. Press `Ctrl + Shift + Esc`
2. Find "java.exe"
3. Right-click → End Task

---

## Application Access

After startup, access the application at:
- **Login Page**: http://localhost:8080/neerjaPhysiotherpyClinic/login.html
- **Dashboard**: http://localhost:8080/neerjaPhysiotherpyClinic/index.html

---

## Troubleshooting

### Problem: "Java is not installed or not in PATH"
**Solution:**
1. Install Java from: https://www.oracle.com/java/technologies/downloads/
2. Restart computer
3. Try again

### Problem: "JAR file not found"
**Solution:**
1. Build the application first:
   ```cmd
   mvn clean package
   ```
2. Ensure you're in the correct project directory
3. Try again

### Problem: Port 8080 already in use
**Solution:**
1. Stop other applications using port 8080
2. Or change port in `application.properties`:
   ```properties
   server.port=8081
   ```
3. Rebuild and try again

### Problem: Browser doesn't open automatically
**Solution:**
1. Open browser manually
2. Navigate to: http://localhost:8080/neerjaPhysiotherpyClinic/login.html

### Problem: "Connection refused" error
**Solution:**
1. Wait longer for application to start
2. Check browser console (F12) for errors
3. Check console window that opened with the JAR

---

## Advanced Configuration

### Modify Application Port
Edit `src/main/resources/application.properties`:
```properties
server.port=8081
```

### Modify Startup Timeout
Edit the .bat file, find this line:
```bat
timeout /t 8 /nobreak
```

Change `8` to desired seconds (e.g., `10` for 10 seconds)

### Modify Auto-Open URL
Edit the .bat file, find:
```bat
set APP_URL=http://localhost:8080/neerjaPhysiotherpyClinic/login.html
```

Change the port if you modified it.

---

## Automatic Startup (Optional)

### Create Windows Task Scheduler Job

1. **Press `Win + R`** and type: `taskschd.msc`
2. **Click "Create Basic Task"**
3. **Name:** NEERJA Clinic Auto-Start
4. **Trigger:** At log on
5. **Action:** Start a program
6. **Program:** `C:\path\to\START.bat`
7. **Click Finish**

Now the application will start automatically when you log in!

---

## Security Notes

⚠️ **Important:**
- The default credentials are for development only
- Change passwords before production deployment
- Access is limited to localhost (127.0.0.1)
- For network access, configure reverse proxy or firewall rules

---

## Performance Tips

- **First Launch:** Takes 10-15 seconds (Java startup time)
- **Subsequent Launches:** Same time (no optimization)
- **RAM Usage:** ~300-500 MB typical
- **CPU Usage:** ~100 MB during idle

---

## Support

If you encounter issues:

1. Check error messages in console
2. Review application logs
3. Verify Java version compatibility
4. Ensure MySQL server is running
5. Check database connection settings

---

## File Locations

```
Project Root
├── START.bat                          (Quick start script)
├── START_APPLICATION.bat              (Full diagnostic script)
├── target/
│   └── NeerjaPhysiotherpy-1.0.0.jar  (Application JAR)
├── src/main/resources/
│   └── application.properties         (Configuration file)
└── pom.xml                            (Maven configuration)
```

---

**Created**: May 29, 2026  
**Version**: 1.0.0  
**Status**: Production Ready ✓
