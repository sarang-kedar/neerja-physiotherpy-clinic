# Auto-Logout on Idle Session - Quick Start Guide

## What's New?

Your Neerja Physiotherapy Clinic application now includes **automatic logout on inactivity** with a configurable timeout!

- **Default Timeout**: 10 minutes of inactivity
- **Warning**: Users get a 1-minute warning before logout
- **Configurable**: Change timeout without code modifications

---

## Quick Configuration

### Change the Idle Timeout

Edit `src/main/resources/application.properties`:

```properties
# Change from 10 minutes (600000 ms) to desired timeout
app.session.idle-timeout-ms=600000
```

**Common Values:**
- 5 min: `300000`
- 10 min: `600000` (default)
- 15 min: `900000`
- 30 min: `1800000`

### Restart Application

```bash
mvn clean install
mvn spring-boot:run
```

---

## How Users Experience It

### During Active Use
- User works normally
- Timer resets on every mouse movement, keystroke, or scroll
- No disruption

### When Inactive
1. **Minutes 0-9**: Silent operation (configurable)
2. **Minute 9**: Warning modal appears with message:
   ```
   Your session is about to expire due to inactivity.
   You will be automatically logged out in 1 minute.
   ```
3. **Minute 10**: Automatic logout if no action taken
   - Session cleared
   - Redirected to login page
   - User must login again

### User Options in Warning Modal
- ✅ **"Keep Me Logged In"** - Reset timer and continue
- ❌ **"Logout Now"** - Logout immediately

---

## Configuration Examples

### Example 1: Strict Security (5 minutes)
```properties
app.session.idle-timeout-ms=300000
app.session.warning-before-logout-ms=60000
```
Users get 5 minutes of inactivity before logout, 1-minute warning.

### Example 2: Moderate (15 minutes)
```properties
app.session.idle-timeout-ms=900000
app.session.warning-before-logout-ms=120000
```
Users get 15 minutes of inactivity, 2-minute warning.

### Example 3: Relaxed (1 hour)
```properties
app.session.idle-timeout-ms=3600000
app.session.warning-before-logout-ms=300000
```
Users get 1 hour of inactivity, 5-minute warning.

---

## Files Modified

| File | Change |
|------|--------|
| `application.properties` | Added `app.session.idle-timeout-ms` and `app.session.warning-before-logout-ms` |
| `AuthController.java` | Added `/api/auth/session-config` endpoint |
| `index.html` | Added idle detection logic and warning modal |

---

## Testing

### Test Idle Timeout (5-minute example)
1. Edit properties: `app.session.idle-timeout-ms=300000`
2. Restart application
3. Login
4. Wait 5 minutes without moving mouse or clicking
5. Verify automatic logout

### Test Warning Modal
1. Set: `app.session.idle-timeout-ms=120000` (2 min)
2. Set: `app.session.warning-before-logout-ms=60000` (1 min)
3. Restart application
4. Login
5. Wait 1 minute (warning appears)
6. Wait another 1 minute (auto logout)

---

## API Reference

### Get Session Configuration
```
GET /api/auth/session-config
```

**Response:**
```json
{
  "success": true,
  "message": "Session configuration retrieved",
  "data": {
    "idleTimeoutMs": 600000,
    "warningBeforeLogoutMs": 60000
  }
}
```

---

## Browser Console Logging

The application logs session configuration on login:

```
Session config loaded: Idle timeout = 600000ms, Warning = 60000ms
```

Check browser console (F12) for debugging.

---

## Security Notes

- ✅ All timeouts are in **milliseconds** in configuration
- ✅ Timeout is measured from **last user activity** (mouse, keyboard, touch, scroll)
- ✅ Window focus state is respected (prevents resets when window is not focused)
- ✅ Session storage is **completely cleared** on logout
- ✅ Warning modal is **non-dismissible** (can only interact with buttons)

---

## Troubleshooting

| Problem | Solution |
|---------|----------|
| Logout not triggering | Check properties are correct, restart application |
| Warning not showing | Ensure warning timeout < main timeout |
| Configuration not applied | Restart application (properties require restart) |
| Session keeps resetting | Check browser console for activity events |

---

## Next Steps

1. ✅ Review `AUTO_LOGOUT_CONFIG.md` for detailed documentation
2. ✅ Test with different timeout values
3. ✅ Adjust timeout based on your security requirements
4. ✅ Deploy to production

---

**Documentation Date**: May 29, 2026  
**Default Timeout**: 10 minutes (600,000 ms)  
**Status**: Production Ready ✓
