package com.tesi.federazione.backend.model.enums;

/**
 * Definisce gli stati possibili per l'affiliazione di un Club o il tesseramento di un Atleta.
 */
public enum AffiliationStatus {
    SUBMITTED,
    ACCEPTED,
    REJECTED,
    EXPIRED;

    /**
     * Verifica se la transizione verso il nuovo stato sia valida
     *
     * @param newStatus Il nuovo stato proposto.
     * @return true se la transizione è permessa, false altrimenti.
     */
    public boolean canTransitionTo(AffiliationStatus newStatus) {

        if (this == newStatus) return true;

        return switch (this) {
            case SUBMITTED -> newStatus.equals(AffiliationStatus.ACCEPTED) || newStatus.equals(AffiliationStatus.REJECTED);
            case ACCEPTED -> newStatus.equals(AffiliationStatus.EXPIRED);
            case REJECTED, EXPIRED -> newStatus.equals(AffiliationStatus.SUBMITTED);
        };
    }
}