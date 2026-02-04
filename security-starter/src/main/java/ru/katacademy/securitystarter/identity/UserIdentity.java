package ru.katacademy.securitystarter.identity;

import java.util.List;
import java.util.Map;

/**
 * Immutable identity model supporting both USER and SERVICE authentication types.
 *
 * <p>This record represents the authenticated principal in the system, which can be either:
 * <ul>
 *   <li><b>USER</b> - An end user authenticated via X-User-Id or JWT</li>
 *   <li><b>SERVICE</b> - A service authenticated via X-Service-Name</li>
 * </ul>
 *
 * <p><b>Factory Methods:</b>
 * <pre>{@code
 * // Create user identity
 * UserIdentity user = UserIdentity.user(123L);
 *
 * // Create user identity with roles
 * UserIdentity userWithRoles = UserIdentity.user(123L, List.of("ROLE_USER", "ROLE_ADMIN"));
 *
 * // Create user identity with custom attributes (e.g., from JWT claims)
 * UserIdentity userWithClaims = UserIdentity.userWithAttributes(
 *     123L,
 *     List.of("ROLE_USER"),
 *     Map.of("email", "user@example.com", "exp", 1234567890)
 * );
 *
 * // Create service identity
 * UserIdentity service = UserIdentity.service("account-service");
 * }</pre>
 */
public record UserIdentity(
    IdentityType type,
    Long userId,
    String serviceName,
    List<String> roles,
    Map<String, Object> attributes
) {

    public enum IdentityType {
        USER,
        SERVICE
    }

    /**
     * Compact constructor with validation and immutability enforcement.
     */
    public UserIdentity {
        if (type == null) {
            throw new IllegalArgumentException("Identity type cannot be null");
        }

        if (type == IdentityType.USER && userId == null) {
            throw new IllegalArgumentException("User identity must have userId");
        }

        if (type == IdentityType.SERVICE && (serviceName == null || serviceName.isBlank())) {
            throw new IllegalArgumentException("Service identity must have serviceName");
        }

        // Make collections immutable
        if (roles != null) {
            roles = List.copyOf(roles);
        } else {
            roles = List.of();
        }
        if (attributes != null) {
            attributes = Map.copyOf(attributes);
        } else {
            attributes = Map.of();
        }
    }

    // Factory methods for USER type

    /**
     * Creates a user identity with the given userId.
     *
     * @param userId the user identifier
     * @return immutable UserIdentity representing a user
     */
    public static UserIdentity user(Long userId) {
        return new UserIdentity(IdentityType.USER, userId, null, List.of(), Map.of());
    }

    /**
     * Creates a user identity with the given userId and roles.
     *
     * @param userId the user identifier
     * @param roles the list of role names
     * @return immutable UserIdentity representing a user with roles
     */
    public static UserIdentity user(Long userId, List<String> roles) {
        return new UserIdentity(IdentityType.USER, userId, null, roles, Map.of());
    }

    /**
     * Creates a user identity with the given userId, roles, and custom attributes.
     * Typically used when extracting data from JWT claims.
     *
     * @param userId the user identifier
     * @param roles the list of role names
     * @param attributes custom attributes (e.g., JWT claims like email, exp, iat)
     * @return immutable UserIdentity representing a user with roles and attributes
     */
    public static UserIdentity userWithAttributes(Long userId, List<String> roles, Map<String, Object> attributes) {
        return new UserIdentity(IdentityType.USER, userId, null, roles, attributes);
    }

    // Factory method for SERVICE type

    /**
     * Creates a service identity with the given service name.
     *
     * @param serviceName the name of the service (e.g., "account-service")
     * @return immutable UserIdentity representing a service
     */
    public static UserIdentity service(String serviceName) {
        return new UserIdentity(IdentityType.SERVICE, null, serviceName, List.of(), Map.of());
    }

    // Convenience methods

    /**
     * Checks if this identity represents a user.
     *
     * @return true if type is USER
     */
    public boolean isUser() {
        return type == IdentityType.USER;
    }

    /**
     * Checks if this identity represents a service.
     *
     * @return true if type is SERVICE
     */
    public boolean isService() {
        return type == IdentityType.SERVICE;
    }

    /**
     * Gets a custom attribute by key.
     *
     * @param key the attribute key
     * @return the attribute value or null if not present
     */
    public Object getAttribute(String key) {
        return attributes.get(key);
    }

    /**
     * Gets the display name for this identity.
     * For users, returns "user:<userId>".
     * For services, returns "service:<serviceName>".
     *
     * @return a string representation suitable for logging
     */
    public String getDisplayName() {
        return switch (type) {
            case USER -> "user:" + userId;
            case SERVICE -> "service:" + serviceName;
        };
    }
}
