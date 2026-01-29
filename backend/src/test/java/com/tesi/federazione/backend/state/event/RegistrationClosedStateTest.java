package com.tesi.federazione.backend.state.event;

import com.tesi.federazione.backend.exception.ActionNotAllowedException;
import com.tesi.federazione.backend.model.Event;
import com.tesi.federazione.backend.model.enums.EventStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RegistrationClosedStateTest {

    private final RegistrationClosedState state = new RegistrationClosedState();

    @Test
    @DisplayName("Test per resumeEvent()")
    void resumeEventTest() {
        Event event = new Event();
        assertThrows(ActionNotAllowedException.class, () -> state.resumeEvent(event));
    }

    @Test
    @DisplayName("Test per operRegistrations()")
    void openRegistrationsTest() {
        Event event = new Event();
        event.setId("eventId");

        state.openRegistrations(event);

        assertEquals(EventStatus.REGISTRATION_OPEN, event.getStatus());

        assertInstanceOf(RegistrationOpenState.class, event.getState());
    }

    @Test
    @DisplayName("Test per closeRegistrations()")
    void closeRegistrationsTest() {
        Event event = new Event();
        assertThrows(ActionNotAllowedException.class, () -> state.closeRegistrations(event));
    }

    @Test
    @DisplayName("Test per completeEvent()")
    void completeEventTest() {
        Event event = new Event();
        event.setId("eventId");

        state.completeEvent(event);

        assertEquals(EventStatus.COMPLETED, event.getStatus());
        assertInstanceOf(CompletedState.class, event.getState());
    }

    @Test
    @DisplayName("Test per cancelEvent()")
    void cancelEventTest() {
        Event event = new Event();
        event.setId("eventId");

        state.cancelEvent(event);

        assertEquals(EventStatus.CANCELLED, event.getStatus());
        assertInstanceOf(CancelledState.class, event.getState());
    }


    @Test
    @DisplayName("Test per validateRegistration - FederationManager")
    void validateRegistrationTest_Admin() {
        Event event = new Event();
        boolean isDraft = false;
        boolean isFederationManager = true;

        assertDoesNotThrow(() -> state.validateRegistration(event, isDraft, isFederationManager));
    }

    @Test
    @DisplayName("Test per validateRegistration - Atlete o ClubManager")
    void validateRegistration_NotAdmin() {
        Event event = new Event();
        boolean isDraft = false;
        boolean isFederationManager = false;

        assertThrows(ActionNotAllowedException.class,
                () -> state.validateRegistration(event, isDraft, isFederationManager));
    }
}