# Design Decisions

## Step 2: Database & Persistence

**Database structure:** Following the technical assessment requirements - users, profiles, credentials, and subscriptions tables.

**Indexes created:** To improve read performance on common queries:
- `users(email)` - Login lookups
- `profiles(user_id)` - Fetch user's profiles
- `credentials(profile_id)` - Profile's credentials
- `credentials(status)` - Filter by approval status
- `credentials(expiry_date)` - Daily expiration job
- `subscriptions(user_id)` - Active subscription lookup

**Flyway:** Versioned migrations (V1-V4)

## Step 3: Security & JWT Authentication

**Access token (15 min) + Refresh token (7 days):** Short-lived access token minimizes risk if stolen. Refresh token allows seamless renewal.

**BCrypt cost factor 12:** Balance between security (4096 rounds) and performance (~250ms per hash).

**Public endpoints:** Only `/api/v1/auth/**` (login, register). Everything else requires JWT.

---