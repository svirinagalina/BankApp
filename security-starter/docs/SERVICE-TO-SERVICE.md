# Service-to-Service Authentication Guide

## Overview

The security-starter provides **automatic service-to-service (S2S) authentication** using the `X-Service-Name` header. This allows services to:

1. Identify which service is calling them
2. Differentiate between user requests and service requests
3. Automatically propagate security context via Feign clients

## X-Service-Name Header Contract

### Header Format

| Header Name | Value | Example |
|-------------|-------|---------|
| `X-Service-Name` | Service identifier from `spring.application.name` | `account-service` |

### Identity Types

The security system recognizes two types of identities:

| Type | Resolved From | Use Case |
|------|---------------|----------|
| **USER** | `X-User-Id` or JWT | End-user requests via API Gateway |
| **SERVICE** | `X-Service-Name` | Service-to-service internal calls |

## Configuration

### Enable S2S Authentication

Simply set `spring.application.name` in your service:

```yaml
spring:
  application:
    name: account-service
```

**That's it!** The security-starter automatically:
- Accepts `X-Service-Name` in incoming requests
- Adds `X-Service-Name` to all outgoing Feign requests

## Feign Auto-Configuration

### How It Works

When you add `security-starter` to a service with Feign clients:

1. **FeignSecurityInterceptor** is auto-registered
2. **Every Feign request** automatically includes:
   - `X-Service-Name`: from `spring.application.name`
   - `X-User-Id`: from SecurityContext (if USER identity present)

### Example: Account Service → KYC Service

**account-service configuration:**
```yaml
spring:
  application:
    name: account-service
```

**Feign client:**
```java
@FeignClient(name = "kyc-service", url = "${kyc-service.url}")
public interface KycServiceClient {

    @GetMapping("/api/v1/kyc/verify/{userId}")
    VerificationResult verify(@PathVariable Long userId);
}
```

**What happens automatically:**
```
GET /api/v1/kyc/verify/12345
Headers:
  X-Service-Name: account-service
  X-User-Id: 12345  (if user is authenticated in SecurityContext)
```

### User Context Propagation

When a user makes a request to account-service, and account-service calls kyc-service via Feign:

```
User → API Gateway → Account Service → KYC Service
       (X-User-Id: 123)    |              |
                            └─ Feign ─────>|
                              X-Service-Name: account-service
                              X-User-Id: 123 (propagated)
```

**Result:** KYC service knows:
- **Who is calling:** `account-service` (SERVICE identity)
- **On behalf of whom:** userId `123` (if needed)

## Identity Resolution Priority

The `CompositeUserIdentityResolver` uses this priority:

1. **X-Service-Name** → Creates SERVICE identity
2. **X-User-Id** → Creates USER identity

If both headers are present, **SERVICE takes priority**.

## Usage in Controllers

### Detect Service vs User Requests

```java
@RestController
@RequestMapping("/api/v1/kyc")
public class KycController {

    @GetMapping("/verify/{userId}")
    public ResponseEntity<?> verify(@PathVariable Long userId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal principal = (UserPrincipal) auth.getPrincipal();

        if (principal.isService()) {
            String serviceName = principal.getServiceName();
            log.info("Verification requested by service: {}", serviceName);
            // Service-to-service call - allow broader access
        } else {
            Long requestingUserId = principal.getUserId();
            log.info("Verification requested by user: {}", requestingUserId);
            // User call - enforce userId ownership
            if (!userId.equals(requestingUserId)) {
                return ResponseEntity.status(403).body("Access denied");
            }
        }

        return ResponseEntity.ok(verificationService.verify(userId));
    }
}
```

### Service-Only Endpoints

```java
@RestController
@RequestMapping("/api/v1/internal")
public class InternalController {

    @GetMapping("/stats")
    public ResponseEntity<?> getStats() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal principal = (UserPrincipal) auth.getPrincipal();

        if (!principal.isService()) {
            return ResponseEntity.status(403).body("Service-only endpoint");
        }

        return ResponseEntity.ok(statsService.getStats());
    }
}
```

## Service Identity in Logs

The `AuthenticationFilter` logs service identities:

```
DEBUG - Service authenticated: type=SERVICE, serviceName=account-service
```

This helps with:
- Request tracing
- Debugging inter-service calls
- Audit trails

## Custom Service Resolver

If you need custom service authentication logic, override the default:

```java
@Component
public class CustomServiceIdentityResolver implements UserIdentityResolver {

    @Override
    public UserIdentity resolve(HttpServletRequest request) {
        String serviceName = request.getHeader("X-Service-Name");

        // Custom validation
        if (!isValidService(serviceName)) {
            log.warn("Unknown service: {}", serviceName);
            return null;
        }

        return UserIdentity.service(serviceName);
    }

    private boolean isValidService(String serviceName) {
        // Whitelist of allowed services
        return List.of("account-service", "fraud-detection", "kyc-service")
            .contains(serviceName);
    }
}
```

## Security Considerations

### 1. Service Whitelisting

By default, **any** service name is accepted. For production:

```java
@Component
public class WhitelistedServiceResolver implements UserIdentityResolver {

    private static final Set<String> ALLOWED_SERVICES = Set.of(
        "account-service",
        "fraud-detection",
        "kyc-service",
        "notification-service"
    );

    @Override
    public UserIdentity resolve(HttpServletRequest request) {
        String serviceName = request.getHeader("X-Service-Name");

        if (serviceName == null || !ALLOWED_SERVICES.contains(serviceName)) {
            return null;
        }

        return UserIdentity.service(serviceName);
    }
}
```

### 2. Internal Network Only

`X-Service-Name` should **only** be accepted from:
- Internal service mesh
- Kubernetes cluster network
- Private VPC

**API Gateway should strip** `X-Service-Name` from external requests:

```yaml
# API Gateway config (example)
strip-headers:
  - X-Service-Name  # Prevent header injection from external clients
```

### 3. Mutual TLS (mTLS)

For enhanced security, combine with mTLS:
- `X-Service-Name` identifies the calling service
- mTLS certificates verify the service's identity

## Testing

### Test Service Authentication

```java
@Test
void shouldAuthenticateService() {
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.addHeader("X-Service-Name", "test-service");

    UserIdentity identity = resolver.resolve(request);

    assertThat(identity).isNotNull();
    assertThat(identity.isService()).isTrue();
    assertThat(identity.serviceName()).isEqualTo("test-service");
}
```

### Test Feign Header Propagation

```java
@Test
void shouldAddServiceNameToFeignRequest() {
    RequestTemplate template = new RequestTemplate();
    FeignSecurityInterceptor interceptor = new FeignSecurityInterceptor("my-service");

    interceptor.apply(template);

    assertThat(template.headers()).containsEntry("X-Service-Name", List.of("my-service"));
}
```

## Example: Multi-Service Call Chain

**Scenario:** User → Gateway → Account Service → KYC Service

### Step 1: User authenticates at Gateway

```
POST /api/v1/accounts/create
Headers:
  Authorization: Bearer eyJhbGc...
```

Gateway validates JWT and forwards:

```
POST /api/v1/accounts/create
Headers:
  X-User-Id: 12345
```

### Step 2: Account Service receives request

```java
// SecurityContext populated with USER identity
UserPrincipal principal = (UserPrincipal) auth.getPrincipal();
// principal.getUserId() = 12345
// principal.isUser() = true
```

### Step 3: Account Service calls KYC via Feign

```java
kycServiceClient.verify(userId);  // Feign auto-adds headers
```

Actual request:

```
GET /api/v1/kyc/verify/12345
Headers:
  X-Service-Name: account-service  (from spring.application.name)
  X-User-Id: 12345                 (from SecurityContext)
```

### Step 4: KYC Service receives request

```java
// SecurityContext populated with SERVICE identity (priority)
UserPrincipal principal = (UserPrincipal) auth.getPrincipal();
// principal.getServiceName() = "account-service"
// principal.isService() = true

// But X-User-Id is also available if needed
String userIdHeader = request.getHeader("X-User-Id");  // "12345"
```

## Troubleshooting

### Issue: Service name not propagated

**Symptoms:** Downstream service doesn't see `X-Service-Name`

**Causes:**
- Missing `spring.application.name` configuration
- Feign not on classpath
- Not using Feign for HTTP calls

**Solution:**
```yaml
spring:
  application:
    name: my-service
```

### Issue: Both USER and SERVICE identity

**Behavior:** When both headers present, SERVICE takes priority

**Expected:**
```java
// Both X-Service-Name and X-User-Id present
principal.isService() == true
principal.getServiceName() == "account-service"
principal.getUserId() == null  // SERVICE identity has no userId
```

To access original userId, read header manually:
```java
String userId = request.getHeader("X-User-Id");
```

## References

- [UserIdentity Contract](../SECURITY_IDENTITY_CONTRACT.md)
- [Migration Guide](./MIGRATION-GUIDE.md)
- [Feign Documentation](https://github.com/OpenFeign/feign)
