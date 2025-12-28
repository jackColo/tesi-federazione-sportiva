package com.tesi.federazione.backend.model.enums;

public enum AffiliationStatus {
    SUBMITTED,
    ACCEPTED,
    REJECTED,
    EXPIRED;

    public boolean canTransitionTo(AffiliationStatus newStatus) {

        if (this == newStatus) return true;

        return switch (this) {
            case SUBMITTED -> newStatus.equals(AffiliationStatus.ACCEPTED) || newStatus.equals(AffiliationStatus.REJECTED);
            case ACCEPTED -> newStatus.equals(AffiliationStatus.EXPIRED);
            case REJECTED, EXPIRED -> newStatus.equals(AffiliationStatus.SUBMITTED);
        };
    }

    public boolean canOperate(AffiliationStatus status) {
        return switch (this) {
            case ACCEPTED -> true;
            case SUBMITTED, REJECTED, EXPIRED -> false;
        };
    }
}