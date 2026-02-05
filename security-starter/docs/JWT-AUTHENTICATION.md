# JWT Authentication Guide

## Overview

The security-starter supports **JWT (JSON Web Token) authentication** as an optional feature alongside the existing X-User-Id header authentication. This enables a hybrid approach where:

1. **API Gateway validates JWT** and extracts userId
2. **Services can optionally re-validate JWT** for critical operations
3. **Fallback to X-User-Id** if JWT is not present

## JWT Claims Format

The JWT parser expects the following standard claims:

### Required Claims

| Claim | Type | Description | Example |
|-------|------|-------------|---------|
| `sub` | String/Number | User identifier (userId) | `"12345"` or `12345` |

### Optional Claims

| Claim | Type | Description | Example |
|-------|------|-------------|---------|
| `roles` | List<String> | User roles/permissions | `["ROLE_USER", "ROLE_ADMIN"]` |
| `iat` | Number | Issued at (Unix timestamp) | `1706745600` |
| `exp` | Number | Expiration time (Unix timestamp) | `1706831999` |
| `email` | String | User email | `"user@example.com"` |

**Note:** All JWT claims are stored in `UserIdentity.attributes` for custom processing.

## Configuration

### Enable JWT Support

Add the following to your `application.yml`:

```yaml
bank:
  security:
    jwt:
      secret: "your-secret-key-must-be-at-least-32-characters-long"
      validate-signature: true  # Set to false if Gateway already validated
```

### Configuration Properties

| Property | Required | Default | Description |
|----------|----------|---------|-------------|
| `bank.security.jwt.secret` | Yes | - | Secret key for HMAC-SHA256 signature validation (min 32 chars) |
| `bank.security.jwt.validate-signature` | No | `true` | Enable/disable signature validation |

### Add Dependencies

JWT support requires JJWT library (added as compileOnly dependency):

```gradle
// Already included in security-starter as compileOnly
compileOnly 'io.jsonwebtoken:jjwt-api:0.12.6'
runtimeOnly 'io.jsonwebtoken:jjwt-impl:0.12.6'
runtimeOnly 'io.jsonwebtoken:jjwt-jackson:0.12.6'
```

If your service uses JWT, add these to your service's `build.gradle`:

```gradle
implementation 'io.jsonwebtoken:jjwt-api:0.12.6'
runtimeOnly 'io.jsonwebtoken:jjwt-impl:0.12.6'
runtimeOnly 'io.jsonwebtoken:jjwt-jackson:0.12.6'
```

## Hybrid Authentication Approach

### Scenario 1: Gateway Validates JWT

**Architecture:**
```
Client → API Gateway (validates JWT) → Service
         └─ Adds X-User-Id header
```

**Service Configuration:**
```yaml
# No JWT config needed - use default X-User-Id resolver
spring:
  application:
    name: my-service
```

### Scenario 2: Service Re-validates JWT

**Architecture:**
```
Client → API Gateway → Service (re-validates JWT)
         └─ Forwards Authorization: Bearer token
```

**Service Configuration:**
```yaml
spring:
  application:
    name: my-service

bank:
  security:
    jwt:
      secret: "shared-secret-with-gateway"
      validate-signature: false  # Gateway already validated
```

### Scenario 3: Full JWT Validation

**Architecture:**
```
Client → Service (validates JWT directly)
```

**Service Configuration:**
```yaml
spring:
  application:
    name: my-service

bank:
  security:
    jwt:
      secret: "service-jwt-secret"
      validate-signature: true  # Full validation
```

## Usage in Controllers

### Access User Identity

```java
@RestController
@RequestMapping("/api/v1/accounts")
public class AccountController {

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal principal = (UserPrincipal) auth.getPrincipal();

        // Access basic info
        Long userId = principal.getUserId();

        // Access JWT claims (if JWT was used)
        UserIdentity identity = principal.identity();
        List<String> roles = identity.roles();
        Object email = identity.getAttribute("email");

        return ResponseEntity.ok(Map.of(
            "userId", userId,
            "roles", roles,
            "email", email
        ));
    }
}
```

### Role-Based Access

```java
@GetMapping("/admin")
public ResponseEntity<?> adminEndpoint() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();

    // Check roles from JWT claims
    boolean isAdmin = auth.getAuthorities().stream()
        .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

    if (!isAdmin) {
        return ResponseEntity.status(403).body("Access denied");
    }

    return ResponseEntity.ok("Admin data");
}
```

## Authentication Priority

The `JwtUserIdentityResolver` follows this resolution order:

1. **JWT token** (from `Authorization: Bearer <token>` header)
2. **X-User-Id header** (fallback if JWT not present or invalid)

This ensures backward compatibility with existing X-User-Id authentication.

## Security Considerations

### 1. Secret Key Security

- **NEVER** commit secrets to git
- Use environment variables: `${JWT_SECRET}`
- Minimum length: 32 characters
- Use different secrets per environment

### 2. Signature Validation

- Set `validate-signature: true` for critical services
- Set `validate-signature: false` only if Gateway already validated
- Invalid signatures are logged as warnings

### 3. Token Expiration

- JWT expiration (`exp` claim) is validated automatically
- Expired tokens are rejected
- No manual expiration check needed

## Example JWT Token

### Header
```json
{
  "alg": "HS256",
  "typ": "JWT"
}
```

### Payload
```json
{
  "sub": "12345",
  "roles": ["ROLE_USER", "ROLE_PREMIUM"],
  "email": "user@example.com",
  "iat": 1706745600,
  "exp": 1706831999
}
```

### Usage
```bash
curl -H "Authorization: Bearer eyJhbGc..." http://localhost:8081/api/v1/accounts/me
```

## Troubleshooting

### Issue: JWT not parsed

**Symptoms:** Falls back to X-User-Id despite sending JWT

**Causes:**
- Missing `bank.security.jwt.secret` configuration
- JWT dependencies not in classpath
- Invalid JWT format

**Solution:**
```yaml
# Verify configuration
bank:
  security:
    jwt:
      secret: "${JWT_SECRET}"
```

### Issue: Signature validation fails

**Symptoms:** Logs show "Failed to parse JWT token"

**Causes:**
- Wrong secret key
- JWT signed with different algorithm
- Token expired

**Solution:**
- Verify secret matches the one used to sign tokens
- Check token expiration
- Set `validate-signature: false` if Gateway already validated

### Issue: Missing userId

**Symptoms:** JWT parsed but no userId extracted

**Cause:** JWT missing `sub` claim

**Solution:** Ensure JWT contains `sub` claim with userId

## Migration Path

### Step 1: Add JWT to critical services
```yaml
# Only for services that need extra security
bank:
  security:
    jwt:
      secret: "${JWT_SECRET}"
      validate-signature: false
```

### Step 2: Test hybrid approach
- Send both `Authorization: Bearer` and `X-User-Id`
- JWT should take priority
- Verify fallback to X-User-Id works

### Step 3: Enable full validation (optional)
```yaml
bank:
  security:
    jwt:
      validate-signature: true
```

## References

- [JJWT Library Documentation](https://github.com/jwtk/jjwt)
- [RFC 7519: JSON Web Token](https://tools.ietf.org/html/rfc7519)
- [UserIdentity Contract](../SECURITY_IDENTITY_CONTRACT.md)
