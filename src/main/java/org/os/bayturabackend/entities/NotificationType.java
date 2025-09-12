package org.os.bayturabackend.entities;

public enum NotificationType {
    // User & Engagement
    REGISTERED,
    FAVORITE,
    MESSAGE,

    // Property Lifecycle
    APPROVAL,
    REJECTION,
    PROPERTY_CREATED,
    PROPERTY_MODIFIED,
    PROPERTY_DELETED,

    // System & Security
    SYSTEM,
    SECURITY_ALERT,
    ACCOUNT_APPROVED,
    ACCOUNT_REJECTED
}
