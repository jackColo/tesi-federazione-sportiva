package com.tesi.federazione.backend.state.event;

import com.tesi.federazione.backend.exception.ActionNotAllowedException;
import com.tesi.federazione.backend.model.Event;
import com.tesi.federazione.backend.model.enums.EventStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class RegistrationOpenStateTest {

    private final RegistrationOpenState state = new RegistrationOpenState();

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
        assertThrows(ActionNotAllowedException.class, () -> state.openRegistrations(event));
    }

    @Test
    @DisplayName("Test per closeRegistrations()")
    void closeRegistrationsTest() {
        Event event = new Event();

        state.closeRegistrations(event);

        assertEquals(EventStatus.REGISTRATION_CLOSED, event.getStatus());

        assertInstanceOf(RegistrationClosedState.class, event.getState());
    }

    @Test
    @DisplayName("Test per completeEvent()")
    void completeEventTest() {
        Event event = new Event();
        assertThrows(ActionNotAllowedException.class, () -> state.completeEvent(event));
    }

    @Test
    @DisplayName("Test per cancelEvent()")
    void cancelEventTest() {
        Event event = new Event();
        assertThrows(ActionNotAllowedException.class, () -> state.cancelEvent(event));
    }


    @Test
    @DisplayName("Test per validateRegistration()")
    void validateRegistrationTest() {
        Event event = new Event();
        assertDoesNotThrow(() -> state.validateRegistration(event, true, true));
    }

}