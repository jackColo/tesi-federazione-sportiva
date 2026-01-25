package com.tesi.federazione.backend.model.enums;

/**
 * Definisce lo stato delle iscrizioni di un atleta a un evento.
 */
public enum EnrollmentStatus {
    DRAFT,
    SUBMITTED,
    APPROVED,
    REJECTED,
    RETIRED;

    /**
     * Verifica la validità del passaggio di stato per un'iscrizione.
     *
     * @param newStatus Il nuovo stato desiderato.
     * @return true se il passaggio è logicamente corretto.
     */
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