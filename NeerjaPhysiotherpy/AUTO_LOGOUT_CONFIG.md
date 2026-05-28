# Auto-Logout on Idle Session - Configuration Guide

## Overview
Implemented automatic logout functionality when the application screen is idle for a configurable duration (default: 10 minutes). Users receive a 1-minute warning before being logged out.

## Features

✅ **Automatic Idle Detection**
- Tracks mouse movement, keyboard input, scroll, and touch events
- Resets timer on any user activity
- Respects window focus state

✅ **Configurable from Properties**
- No code changes needed to modify timeout duration
- Changes apply immediately on application restart
- Two separate timeout settings (main timeout + warning)

✅ **User-Friendly Warning**
- Modal warning appears 1 minute before logout (configurable)
- Users can click "Keep Me Logged In" to reset timer
- Option to logout immediately

✅ **Session Security**
- Prevents unauthorized access on unattended devices
- Automatically clears session storage on timeout
- Redirects to login page after logout

---

## Configuration

### 1. **Properties File Settings**

Edit `src/main/resources/application.properties`:

```properties
# Session & Idle Timeout Configuration (in milliseconds)
# Default: 600000 ms = 10 minutes
app.session.idle-timeout-ms=600000

# Time to show warning before logout (in milliseconds)
# Default: 60000 ms = 1 minute
app.session.warning-before-logout-ms=60000
```

### 2. **Common Timeout Values**

```properties
# 5 minutes
app.session.idle-timeout-ms=300000

# 10 minutes (DEFAULT)
app.session.idle-timeout-ms=600000

# 15 minutes
app.session.idle-timeout-ms=900000

# 30 minutes
app.session.idle-timeout-ms=1800000

# 1 hour
app.session.idle-timeout-ms=3600000
```

### 3. **Warning Time Examples**

```properties
# 30 seconds warning
app.session.warning-before-logout-ms=30000

# 1 minute warning (DEFAULT)
app.session.warning-before-logout-ms=60000

# 2 minutes warning
app.session.warning-before-logout-ms=120000
```

---

## How It Works

### Backend
- **Endpoint**: `GET /api/auth/session-config`
- **Returns**: JSON with `idleTimeoutMs` and `warningBeforeLogoutMs` values
- **Controller**: `AuthController.java` with `@Value` annotations reading from properties

### Frontend (JavaScript)
1. **Initialization** (in `index.html`)
   - Loads session config from backend on login
   - Initializes idle timers
   - Attaches event listeners for user activity

2. **Activity Monitoring**
   - Listens to: `mousemove`, `mousedown`, `keypress`, `scroll`, `touchstart`, `focus`
   - Resets timers on any detected activity
   - Ignores activity when window is not focused

3. **Warning Phase**
   - Shows modal alert 1 minute before logout (configurable)
   - Users can extend session or logout immediately
   - Auto-closes warning if user doesn't interact

4. **Logout Phase**
   - Clears session storage
   - Shows alert message
   - Redirects to login page

---

## Modal UI

When idle timeout warning appears:

```
┌─────────────────────────────────────┐
│ ⚠️  Session Timeout Warning          │
├─────────────────────────────────────┤
│                                     │
│ Your session is about to expire     │
│ due to inactivity.                  │
│                                     │
│ You will be automatically logged    │
│ out in 1 minute.                    │
│                                     │
│ Click "Keep Me Logged In" to        │
│ continue your session.              │
│                                     │
├─────────────────────────────────────┤
│   [Keep Me Logged In]  [Logout Now] │
└─────────────────────────────────────┘
```

---

## JavaScript Functions

### Key Functions in `index.html`

```javascript
// Load configuration from backend
loadSessionConfig()

// Reset idle timer (called on user activity)
resetIdleTimer()

// Show warning modal
showIdleWarning()

// Auto logout due to inactivity
autoLogout()

// Keep session alive (called from warning modal)
keepSessionAlive()
```

---

## Testing the Feature

### Test 1: Basic Timeout
1. Login to the application
2. Leave the screen idle for configured timeout duration
3. Verify automatic logout occurs
4. Verify redirect to login page

### Test 2: Warning Message
1. Login to the application
2. Wait for `(timeout - warning)` duration
3. Verify warning modal appears
4. Verify countdown timer works

### Test 3: Activity Reset
1. Login and wait near the warning timeout
2. Move mouse or press a key
3. Verify warning timer resets
4. Verify no warning appears

### Test 4: Keep Session Alive
1. Wait for warning modal
2. Click "Keep Me Logged In"
3. Verify modal closes
4. Verify timer resets
5. Verify session continues

### Test 5: Configuration Change
1. Edit `application.properties` - change `app.session.idle-timeout-ms=300000` (5 min)
2. Restart application
3. Login and wait 5 minutes
4. Verify logout occurs at 5 minutes (not 10)

---

## Backend Implementation

### File: `AuthController.java`

```java
@Value("${app.session.idle-timeout-ms:600000}")
private long idleTimeoutMs;

@Value("${app.session.warning-before-logout-ms:60000}")
private long warningBeforeLogoutMs;

@GetMapping("/session-config")
public ResponseEntity<ApiResponse<Map<String, Long>>> getSessionConfig() {
    Map<String, Long> config = new HashMap<>();
    config.put("idleTimeoutMs", idleTimeoutMs);
    config.put("warningBeforeLogoutMs", warningBeforeLogoutMs);
    return ResponseEntity.ok(ApiResponse.success("Session configuration retrieved", config));
}
```

---

## Frontend Implementation

### File: `index.html` - Idle Timeout Code

```javascript
// Load configuration from backend
async function loadSessionConfig() {
    try {
        const response = await fetch(`${API_BASE}/auth/session-config`).then(r => r.json());
        if (response.success && response.data) {
            idleTimeoutMs = response.data.idleTimeoutMs || 600000;
            warningBeforeLogoutMs = response.data.warningBeforeLogoutMs || 60000;
        }
    } catch (err) {
        console.warn('Could not load session config, using defaults', err);
    }
}

// Reset idle timer on user activity
function resetIdleTimer() {
    if (idleTimer) clearTimeout(idleTimer);
    if (warningTimer) clearTimeout(warningTimer);
    
    // Set warning timer
    const warningTime = idleTimeoutMs - warningBeforeLogoutMs;
    if (warningTime > 0) {
        warningTimer = setTimeout(() => showIdleWarning(), warningTime);
    }
    
    // Set logout timer
    idleTimer = setTimeout(() => autoLogout(), idleTimeoutMs);
}
```

---

## Security Considerations

1. **Client-Side Detection**: Activity monitoring is client-side. For enhanced security, also implement server-side session timeout.

2. **Warning Modal**: Non-dismissible (data-bs-backdrop="static") to prevent users from bypassing warning.

3. **Session Storage**: Cleared completely on logout, preventing cached session data.

4. **Configurable**: Allows organizations to set appropriate security levels for their use case.

---

## Troubleshooting

### Issue: Idle logout not triggering
- **Solution**: Check browser console for errors
- **Check**: Ensure `app.session.idle-timeout-ms` is set in `application.properties`
- **Check**: Verify `loadSessionConfig()` completes successfully

### Issue: Warning modal not showing
- **Check**: Ensure idle timeout is larger than warning timeout
- **Example**: If timeout=5min and warning=6min, warning won't show

### Issue: Session not resetting on activity
- **Check**: Verify event listeners are attached (check console)
- **Check**: Browser security settings may prevent event firing
- **Check**: Ensure JavaScript is enabled

### Issue: Configuration changes not taking effect
- **Solution**: Restart the application server
- **Note**: Changes to `.properties` require restart

---

## Future Enhancements

- [ ] Server-side session validation
- [ ] Persistent session log for audit trail
- [ ] Per-user configurable timeouts (admin dashboard)
- [ ] Logout analytics tracking
- [ ] Grace period for network latency

---

## Files Modified

1. ✅ `application.properties` - Added configuration properties
2. ✅ `AuthController.java` - Added session-config endpoint
3. ✅ `index.html` - Added idle detection logic and warning modal
4. ✅ `login.html` - Existing login page (no changes needed)

---

## Version Info
- **Implementation Date**: May 29, 2026
- **Framework**: Spring Boot 4.0.5 + iText 8.0.2
- **Frontend**: Bootstrap 5.3.0 + Vanilla JavaScript
- **Default Timeout**: 10 minutes (600,000 ms)
