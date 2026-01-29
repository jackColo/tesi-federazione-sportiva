package com.tesi.federazione.backend.state.event;

import com.tesi.federazione.backend.exception.ActionNotAllowedException;
import com.tesi.federazione.backend.model.Event;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class CompletedStateTest {

    private final CompletedState state = new CompletedState();

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
        assertThrows(ActionNotAllowedException.class, () -> state.closeRegistrations(event));
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
        assertThrows(ActionNotAllowedException.class, () -> state.validateRegistration(event, true, true));
    }

}