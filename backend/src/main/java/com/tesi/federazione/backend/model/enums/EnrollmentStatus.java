package com.tesi.federazione.backend.model.enums;

public enum EnrollmentStatus {
    DRAFT,
    SUBMITTED,
    APPROVED,
    REJECTED,
    RETIRED;

    public boolean canTransitionTo(EnrollmentStatus newStatus) {

        if (this == newStatus) return true;

        return switch (this) {
            case DRAFT -> newStatus.equals(EnrollmentStatus.SUBMITTED);
            case SUBMITTED -> newStatus.equals(EnrollmentStatus.APPROVED) || newStatus.equals(EnrollmentStatus.REJECTED);
            case APPROVED -> newStatus.equals(EnrollmentStatus.RETIRED);
            case REJECTED, RETIRED -> newStatus.equals(EnrollmentStatus.DRAFT);
        };
    }
}