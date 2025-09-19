package org.os.bayturabackend.entities;

public enum NotificationType {
    // User & Engagement
    REGISTERED,
    FAVORITE,
    UNFAVORITE,
    MESSAGE,

    // Property Lifecycle
    APPROVAL,
    REJECTION,
    PROPERTY_CREATED,
    PROPERTY_MODIFIED,
    PROPERTY_DELETED,
    MEDIA_UPLOADED,
    MEDIA_DELETED,

    // Requests
    REQUEST_CREATED,
    REQUEST_ACCEPTED,
    REQUEST_REJECTED,
    REQUEST_DELETED,

    // Profile
    PROFILE_UPDATED,
    PROFILE_DELETED,
    PROFILE_PICTURE_UPDATED,
    PROFILE_PICTURE_DELETED,

    // System & Security
    SYSTEM,
    ACCOUNT_APPROVED,
    ACCOUNT_REJECTED,

    GENERAL
}
