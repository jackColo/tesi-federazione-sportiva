import { CommonModule } from '@angular/common';
import { Component, computed, inject, input, InputSignal, Signal } from '@angular/core';
import { toObservable, toSignal } from '@angular/core/rxjs-interop';
import { RouterLink } from '@angular/router';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import {
  faArrowLeft,
  faArrowRotateLeft,
  faBan,
  faCalendar,
  faCheck,
  faFilePdf,
  faMap,
  faMapMarkerAlt,
  faPen,
  faTrash,
  faUsers
} from '@fortawesome/free-solid-svg-icons';
import { switchMap } from 'rxjs';
import { AuthService } from '../../../../core/services/auth.service';
import { EventService } from '../../../../core/services/event.service';
import {
  EventStatus,
  eventStatusColorClass,
  readableEventStatus,
} from '../../../../enums/event-status.enum';
import { Role } from '../../../../enums/role.enum';
import { ErrorResponse } from '../../../../models/dtos';
import { Event } from '../../../../models/event.model';

@Component({
  selector: 'app-dashboard-event-detail-component',
  standalone: true,
  imports: [CommonModule, RouterLink, FontAwesomeModule],
  templateUrl: './dashboard-event-detail-component.html',
})
export class DashboardEventDetailComponent {
  private authService = inject(AuthService);
  private eventService = inject(EventService);
  id: InputSignal<string> = input.required<string>();

  icons = {
    faMap,
    faCalendar,
    faMapMarkerAlt,
    faBan,
    faUsers,
    faFilePdf,
    faTrash,
    faArrowLeft,
    faCheck,
    faArrowRotateLeft,
    faPen
  };

  event: Signal<Event | null> = toSignal(
    toObservable(this.id).pipe(switchMap((id) => this.eventService.getEventById(id))),
    { initialValue: null }
  );

  isFederation = computed(() => this.authService.userRole() === Role.FEDERATION_MANAGER);
  isClub = computed(() => this.authService.userRole() === Role.CLUB_MANAGER);

  getStatusLabel(status: EventStatus): string {
    return readableEventStatus(status);
  }

  getStatusClass(status: EventStatus): string {
    return eventStatusColorClass(status);
  }

  openEventRegistrations() {
    const message =
      'Sei sicuro di voler aprire le iscrizioni? Tutti i club riceveranno una notifica.';
    this.updateEventStatus(EventStatus.REGISTRATION_OPEN, message);
  }

  closeEventRegistrations() {
    const message =
      'Sei sicuro di voler chiudere le iscrizioni? Tutti i club riceveranno una notifica.';
    this.updateEventStatus(EventStatus.REGISTRATION_CLOSED, message);
  }

  setCancelledStatus() {
    const message =
      "ATTENZIONE: Stai per annullare l'evento. Tutti i partecipanti riceveranno una notifica e lo stato delle iscrizioni verrà impostato a RIFIUTATO.";
    this.updateEventStatus(EventStatus.CANCELLED, message);
  }

  setScheduledStatus() {
    const message =
      "ATTENZIONE: Stai per ripristinare l'evento. Tutti i partecipanti riceveranno una notifica e lo stato le iscrizioni RIFIUTATE verranno rimesse in BOZZA.";
    this.updateEventStatus(EventStatus.SCHEDULED, message);
  }

  updateEventStatus(newStatus: EventStatus, message: string) {
    if (confirm(message)) {
      this.eventService.updateEventState(this.id(), newStatus).subscribe({
        next: () => {
          alert(`Stato dell'evento modificato con successo!`);
        },
        error: (err: ErrorResponse) => {
          alert(`Errore durante la modifica dello stato dell'evento: ${err.error.message}`);
        },
      });
    }
  }

  downloadReport() {
    console.log('Scaricamento PDF...');
  }
}
