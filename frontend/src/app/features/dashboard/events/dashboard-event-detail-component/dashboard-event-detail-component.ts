import { CommonModule } from '@angular/common';
import { Component, computed, inject, input, InputSignal, signal, Signal } from '@angular/core';
import { toObservable, toSignal } from '@angular/core/rxjs-interop';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
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
  faUsers,
} from '@fortawesome/free-solid-svg-icons';
import { catchError, of, switchMap } from 'rxjs';
import { AthleteService } from '../../../../core/services/athlete.service';
import { AuthService } from '../../../../core/services/auth.service';
import { EventService } from '../../../../core/services/event.service';
import { UserService } from '../../../../core/services/user.service';
import {
  EventStatus,
  eventStatusColorClass,
  readableEventStatus,
} from '../../../../enums/event-status.enum';
import { Role } from '../../../../enums/role.enum';
import { Athlete } from '../../../../models/athlete.model';
import { ErrorResponse } from '../../../../models/dtos';
import { Event as MyEvent } from '../../../../models/event.model';
import { EnrollmentTableComponent } from '../enrollment-components/enrollment-table-component/enrollment-table-component';

@Component({
  selector: 'app-dashboard-event-detail-component',
  standalone: true,
  imports: [
    CommonModule,
    RouterLink,
    FontAwesomeModule,
    EnrollmentTableComponent,
    EnrollmentTableComponent,
  ],
  templateUrl: './dashboard-event-detail-component.html',
})
export class DashboardEventDetailComponent {
  private authService = inject(AuthService);
  private eventService = inject(EventService);
  private userService = inject(UserService);
  private athleteService = inject(AthleteService);
  private router = inject(Router);
  private route = inject(ActivatedRoute);
  id: InputSignal<string> = input.required<string>();
  clubId = '';

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
    faPen,
  };

  event: Signal<MyEvent | null> = toSignal(
    toObservable(this.id).pipe(switchMap((id) => this.eventService.getEventById(id))),
    { initialValue: null }
  );

  isFederation = computed(() => this.authService.userRole() === Role.FEDERATION_MANAGER);
  isClub = computed(() => this.authService.userRole() === Role.CLUB_MANAGER);
  isAthlete = computed(() => this.authService.userRole() === Role.ATHLETE);

  selectedAthleteId = signal<string>('');
  isAthelteEnrolled = signal<boolean>(false)

  onAthleteSelectionChange(event: Event) {
    const selectElement = event.target as HTMLSelectElement;
    this.selectedAthleteId.set(selectElement.value);
  }

  goToEnrollmentPage() {
    this.router.navigate(['enroll/', this.clubId, this.selectedAthleteId()], {
      relativeTo: this.route,
    });
  }

  athletes: Signal<Athlete[] | []> = toSignal(
    toObservable(computed(() => this.authService.userRole())).pipe(
      switchMap((role) => {
        if (role === Role.FEDERATION_MANAGER) {
          return of([]);
        }
        const email = this.authService.currentUserEmail()
        if (!email) {
          return of([]);
        }
        return this.userService.getUserByEmail(email).pipe(
          switchMap((user) => {
            if (user.clubId) {
              this.clubId = user.clubId;
              if (role === Role.CLUB_MANAGER) return this.athleteService.getAtheltes(user.clubId);
              else {
                this.selectedAthleteId.set(user.id);
                return of([]);
              }
            } else return of([]);
          }),
          catchError((err) => {
            console.error('Errore nel recupero atleti:', err);
            return of([]);
          })
        );
      })
    ),
    { initialValue: [] }
  );

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
          window.location.reload();
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
