# Migration Guide: UserIdentity Enhancement

## Overview

The security-starter has been enhanced to support both **USER** and **SERVICE** identities. This is a **breaking change** to the `UserIdentityResolver` interface.

This guide helps you migrate existing services to the new identity model.

## Breaking Changes

### 1. UserIdentityResolver Return Type

**Before:**
```java
public interface UserIdentityResolver {
    Long resolve(HttpServletRequest request);  // Returns userId
}
```

**After:**
```java
public interface UserIdentityResolver {
    UserIdentity resolve(HttpServletRequest request);  // Returns UserIdentity
}
```

### 2. UserPrincipal Structure

**Before:**
```java
public record UserPrincipal(Long userId) {}
```

**After:**
```java
public record UserPrincipal(UserIdentity identity) {
    public Long getUserId() { ... }
    public String getServiceName() { ... }
    public boolean isUser() { ... }
    public boolean isService() { ... }
}
```

## Migration Steps

### Step 1: Update UserIdentityResolver Implementations

**Before:**
```java
@Component
public class MyUserIdentityResolver implements UserIdentityResolver {

    @Override
    public Long resolve(HttpServletRequest request) {
        String userIdHeader = request.getHeader("X-User-Id");
        if (userIdHeader == null) {
            return null;
        }
        return Long.parseLong(userIdHeader);
    }
}
```

**After:**
```java
@Component
public class MyUserIdentityResolver implements UserIdentityResolver {

    @Override
    public UserIdentity resolve(HttpServletRequest request) {
        String userIdHeader = request.getHeader("X-User-Id");
        if (userIdHeader == null) {
            return null;
        }
        Long userId = Long.parseLong(userIdHeader);
        return UserIdentity.user(userId);  // Wrap in UserIdentity
    }
}
```

### Step 2: Update Controller Code

**Before:**
```java
@GetMapping("/me")
public ResponseEntity<?> getCurrentUser() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    UserPrincipal principal = (UserPrincipal) auth.getPrincipal();

    Long userId = principal.userId();  // Direct access

    return ResponseEntity.ok(Map.of("userId", userId));
}
```

**After:**
```java
@GetMapping("/me")
public ResponseEntity<?> getCurrentUser() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    UserPrincipal principal = (UserPrincipal) auth.getPrincipal();

    Long userId = principal.getUserId();  // Use getter method

    return ResponseEntity.ok(Map.of("userId", userId));
}
```

### Step 3: Update Tests

**Before:**
```java
@Test
void shouldResolveUserId() {
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.addHeader("X-User-Id", "12345");

    Long userId = resolver.resolve(request);

    assertThat(userId).isEqualTo(12345L);
}
```

**After:**
```java
@Test
void shouldResolveUserId() {
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.addHeader("X-User-Id", "12345");

    UserIdentity identity = resolver.resolve(request);

    assertThat(identity).isNotNull();
    assertThat(identity.userId()).isEqualTo(12345L);
    assertThat(identity.isUser()).isTrue();
}
```

### Step 4: Update UserPrincipal Creation (if manually creating)

**Before:**
```java
UserPrincipal principal = new UserPrincipal(12345L);
```

**After:**
```java
UserIdentity identity = UserIdentity.user(12345L);
UserPrincipal principal = new UserPrincipal(identity);
```

## New Features Available After Migration

### 1. Service Identity Support

```java
// Check if request is from a service
if (principal.isService()) {
    String serviceName = principal.getServiceName();
    log.info("Request from service: {}", serviceName);
}
```

### 2. Roles from JWT

```java
// Access roles extracted from JWT
UserIdentity identity = principal.identity();
List<String> roles = identity.roles();

if (roles.contains("ROLE_ADMIN")) {
    // Admin-only logic
}
```

### 3. Custom Attributes (JWT Claims)

```java
// Access custom JWT claims
UserIdentity identity = principal.identity();
Object email = identity.getAttribute("email");
Object organizationId = identity.getAttribute("org_id");
```

### 4. Automatic Feign Header Propagation

```java
// No code changes needed - automatic
// Feign clients auto-add X-Service-Name and X-User-Id
@FeignClient(name = "other-service")
public interface OtherServiceClient {
    @GetMapping("/api/data")
    Data getData();
}
```

## Compatibility Matrix

| Service | security-starter Version | Migration Required |
|---------|-------------------------|-------------------|
| account-service | ✅ Updated | Yes - already migrated in this PR |
| kyc-service | ✅ Updated | Yes - already migrated in this PR |
| fraud-detection | ⚠️ Not updated | No - uses default resolver |
| notification-service | ⚠️ Not updated | No - uses default resolver |

## Common Migration Errors

### Error 1: Compilation Error

**Error Message:**
```
incompatible types: UserIdentity cannot be converted to Long
```

**Cause:** Still using old return type

**Fix:**
```java
// Change this:
Long userId = resolver.resolve(request);

// To this:
UserIdentity identity = resolver.resolve(request);
Long userId = identity.userId();
```

### Error 2: NullPointerException

**Error Message:**
```
NullPointerException at UserPrincipal.getUserId()
```

**Cause:** SERVICE identity has no userId

**Fix:**
```java
// Add null check
if (principal.isUser()) {
    Long userId = principal.getUserId();
    // Use userId
} else {
    // Handle service identity
    String serviceName = principal.getServiceName();
}
```

### Error 3: Test Failures

**Error Message:**
```
expected: 12345L but was: <null>
```

**Cause:** Test assertions using old API

**Fix:**
```java
// Change this:
assertThat(principal.userId()).isEqualTo(12345L);

// To this:
assertThat(principal.getUserId()).isEqualTo(12345L);
```

## Rollback Plan

If migration causes issues, you can temporarily rollback:

### Option 1: Git Revert

```bash
git revert <commit-hash>
```

### Option 2: Create Adapter Resolver

Create a temporary adapter to use old code:

```java
@Component
public class LegacyUserIdResolver implements UserIdentityResolver {

    private final OldResolver oldResolver;

    @Override
    public UserIdentity resolve(HttpServletRequest request) {
        Long userId = oldResolver.resolveUserId(request);  // Your old method
        if (userId == null) {
            return null;
        }
        return UserIdentity.user(userId);
    }
}
```

## Gradual Migration Strategy

For large projects, migrate service by service:

### Phase 1: Update Core Services
1. account-service ✅
2. kyc-service ✅

### Phase 2: Update Integration Services
3. fraud-detection
4. notification-service

### Phase 3: Update Remaining Services
5. All other services

**Between phases:**
- Test thoroughly
- Monitor logs for errors
- Verify inter-service communication

## Verification Checklist

After migration, verify:

- [ ] Service compiles without errors
- [ ] All tests pass
- [ ] Existing X-User-Id authentication works
- [ ] Feign clients add X-Service-Name header
- [ ] Logs show correct identity type (USER/SERVICE)
- [ ] Controllers can access userId via `getUserId()`
- [ ] Service-to-service calls work

## Example: Complete Migration

### Before (account-service)

**AccountUserIdentityResolver.java:**
```java
@Component
public class AccountUserIdentityResolver implements UserIdentityResolver {

    @Override
    public Long resolve(HttpServletRequest request) {
        String userId = request.getHeader("X-User-Id");
        return userId != null ? Long.parseLong(userId) : null;
    }
}
```

**AccountController.java:**
```java
@GetMapping("/me")
public ResponseEntity<?> getMe() {
    UserPrincipal principal = (UserPrincipal) auth.getPrincipal();
    return ResponseEntity.ok(Map.of("userId", principal.userId()));
}
```

**AccountUserIdentityResolverTest.java:**
```java
@Test
void test() {
    Long userId = resolver.resolve(request);
    assertThat(userId).isEqualTo(123L);
}
```

### After (account-service)

**AccountUserIdentityResolver.java:**
```java
@Component
public class AccountUserIdentityResolver implements UserIdentityResolver {

    @Override
    public UserIdentity resolve(HttpServletRequest request) {
        String userId = request.getHeader("X-User-Id");
        return userId != null ? UserIdentity.user(Long.parseLong(userId)) : null;
    }
}
```

**AccountController.java:**
```java
@GetMapping("/me")
public ResponseEntity<?> getMe() {
    UserPrincipal principal = (UserPrincipal) auth.getPrincipal();
    return ResponseEntity.ok(Map.of("userId", principal.getUserId()));
}
```

**AccountUserIdentityResolverTest.java:**
```java
@Test
void test() {
    UserIdentity identity = resolver.resolve(request);
    assertThat(identity.userId()).isEqualTo(123L);
}
```

## Support

If you encounter issues during migration:

1. Check this guide for common errors
2. Review the [UserIdentity Contract](../SECURITY_IDENTITY_CONTRACT.md)
3. See [SERVICE-TO-SERVICE.md](./SERVICE-TO-SERVICE.md) for S2S examples
4. See [JWT-AUTHENTICATION.md](./JWT-AUTHENTICATION.md) for JWT examples

## Summary

**Key Changes:**
- `UserIdentityResolver.resolve()` returns `UserIdentity` instead of `Long`
- `UserPrincipal.userId()` → `UserPrincipal.getUserId()`
- Tests need to unwrap `UserIdentity` to access `userId`

**Benefits:**
- Support for SERVICE identities
- JWT claims extraction
- Automatic Feign header propagation
- Role-based authorization

**Migration Time:** ~30 minutes per service (small services with few custom resolvers)
