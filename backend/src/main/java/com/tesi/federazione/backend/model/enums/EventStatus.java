package com.tesi.federazione.backend.model.enums;

/**
 * Rappresenta stati gli possibili per un Evento
 * Ciascuno di questi valori è mappato dalle classi concrete del Design Pattern State
 * tramite l'EventStateFactory
 */
public enum EventStatus {
    SCHEDULED,
    REGISTRATION_OPEN,
    REGISTRATION_CLOSED,
    COMPLETED,
    CANCELLED
}