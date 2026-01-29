package com.tesi.federazione.backend.state.event;

import com.tesi.federazione.backend.exception.ActionNotAllowedException;
import com.tesi.federazione.backend.model.Event;
import com.tesi.federazione.backend.model.enums.EventStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class CancelledStateTest {

    private final CancelledState state = new CancelledState();

    @Test
    @DisplayName("Test per resumeEvent() - Evento scaduto")
    void resumeEventTest_fail() {
        Event event = new Event();
        event.setDate(LocalDate.of(2020, 1, 1));
        assertThrows(ActionNotAllowedException.class, () -> state.resumeEvent(event));
    }
    @Test
    @DisplayName("Test per resumeEvent() - Evento valido")
    void resumeEventTest_success() {
        Event event = new Event();
        event.setDate(LocalDate.of(2030, 1, 1));

        state.resumeEvent(event);

        assertEquals(EventStatus.SCHEDULED, event.getStatus());
        assertInstanceOf(ScheduledState.class, event.getState());
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
    @DisplayName("Test per validateRegistration")
    void validateRegistrationTest() {
        Event event = new Event();
        assertThrows(ActionNotAllowedException.class, () -> state.validateRegistration(event, true,true));
    }

}