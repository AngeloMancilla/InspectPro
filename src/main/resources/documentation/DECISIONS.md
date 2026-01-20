# Design Decisions

## Step 2: Database & Persistence

**Multiple profiles per user:** Enables personal/company context (Challenge A).

**VP status logic:** Profile type based on credential count (≥1 approved). Alternative: store type directly, but deriving ensures consistency.

**Subscriptions at user-level:** Billing is per account, not per profile.

**Indexes created:**
- `users(email)` - Login lookups
- `profiles(user_id)` - Fetch user's profiles
- `credentials(profile_id)` - Profile's credentials
- `credentials(status)` - Filter by approval status
- `credentials(expiry_date)` - Daily expiration job
- `subscriptions(user_id)` - Active subscription lookup

**Flyway:** Versioned migrations (V1-V4)

---