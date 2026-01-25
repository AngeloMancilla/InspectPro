# Design Decisions

## Business Challenges

### Challenge A: Profile Context Switching ✅

**Problem:** User has multiple profiles. After login, how to handle `POST /api/v1/profiles/switch/{profileId}`?

**Solution:** Redis-based session tracking

- Key: `active_profile:{userId}` → `profileId`
- TTL: 7 days (same as refresh token)
- JWT stays the same, only Redis key changes
- Endpoints `/me` and `/me PUT` read from Redis

**Why not regenerate JWT?**
- Simpler for mobile clients (no token rotation)
- Already using Redis for refresh tokens
- Easy to audit profile switches

---

### Challenge B: Credential Expiration Grace Period ✅

**Problem:** VP's credential expires but renewal is PENDING. Keep VP status?

**Solution:** YES - count PENDING credentials as active

```java
Long active = countByStatus(APPROVED);
Long pending = countByStatus(PENDING);

if (active == 0 && pending == 0) {
    downgradeToBasic(); // Only if no active AND no pending
}
```

**Why?** Don't punish users waiting for admin approval. Grace period until renewal is rejected.

---

### Challenge C: Feature Gating ⚠️ Partial

**Problem:** User on BASIC (4/5 inspections used) upgrades while creating #6. Allow?

**Solution:** REJECT until Stripe webhook confirms payment

**Not implemented:** Inspection tracking is out of scope (profile service only). Would need:
- `inspection_usage` table
- Redis counter for rate limiting
- Stripe webhook integration (currently mock)

---

## Database Design

**Indexes created:**
- `users(email)` - Login lookups
- `profiles(user_id)` - Fetch user's profiles
- `credentials(profile_id, status)` - Profile's credentials
- `credentials(expires_at)` - Daily expiration job
- `subscriptions(user_id)` - Active subscription lookup

## Security & JWT

- Access token: 15 min (short-lived for security)
- Refresh token: 7 days (stored in Redis)
- BCrypt cost: 12 (~250ms per hash)
- Public endpoints: Only `/api/v1/auth/**`

---

## Implementation Notes

### Foreign Key Pattern

All create methods follow same pattern: fetch parent entity first, then set relationship before save. Fixed null FK errors in SubscriptionService, ProfileService, and CredentialService.

### Lombok + JPA Gotcha

`@Builder.Default` doesn't work with JPA's no-args constructor. Had to write custom constructor to initialize ArrayList fields, otherwise got NPE when adding items.

### Scheduler

Credential expiration job runs daily at midnight. Fixed method name typo: `expiresAt` → `expiryDate` to match entity property. In production would need distributed lock for multi-instance.

---