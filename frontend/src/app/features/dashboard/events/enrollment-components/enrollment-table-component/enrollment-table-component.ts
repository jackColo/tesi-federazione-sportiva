import { CommonModule } from '@angular/common';
import {
    Component,
    computed,
    inject,
    input,
    signal,
    Signal
} from '@angular/core';
import { toObservable, toSignal } from '@angular/core/rxjs-interop';
import { RouterLink } from '@angular/router';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { faCheck, faClose, faEye, faFilePdf, faPaperPlane, faUserSlash } from '@fortawesome/free-solid-svg-icons';
import { combineLatest, of, switchMap } from 'rxjs';
import { AuthService } from '../../../../../core/services/auth.service';
import { EventService } from '../../../../../core/services/event.service';
import { EnrollmentStatus, enrollmentStatusColorClass, readableEnrollmentStatus } from '../../../../../enums/enrollment-status.enum';
import { Role } from '../../../../../enums/role.enum';
import { Enrollment } from '../../../../../models/enrollment.model';
import { CompetitionType, readableCompetitionType } from '../../../../../enums/competition-type.enum';
import { ErrorResponse } from '../../../../../models/dtos';
import { AffiliationStatus, affiliationStatusColorClass, readableAffiliationStatus } from '../../../../../enums/affiliation-status.enum';

@Component({
  selector: 'app-enrollment-table',
  standalone: true,
  imports: [CommonModule, FontAwesomeModule, RouterLink],
  templateUrl: './enrollment-table-component.html',
})
export class EnrollmentTableComponent {
  private eventService = inject(EventService);
  private authService = inject(AuthService);
  clubId = input<string>();
  eventId = input.required<string>();
  athleteId = input<string>();
  
  icons = { faUserSlash, faFilePdf, faEye, faCheck, faClose, faPaperPlane};
  
  isFederation = computed(() => this.authService.userRole() === Role.FEDERATION_MANAGER);
  isClub = computed(() => this.authService.userRole() === Role.CLUB_MANAGER);
  isAthlete = computed(() => this.authService.userRole() === Role.ATHLETE);

  showApprovedOnly = signal(false);

  enrollments: Signal<Enrollment[] | null> = toSignal(
    combineLatest([
      toObservable(this.eventId),
      toObservable(this.athleteId),
      toObservable(this.clubId),
      toObservable(this.showApprovedOnly)
    ]).pipe(
      switchMap(([eventId, athleteId, clubId, approvedOnly]) => {
        if (!eventId) return of([]);

        if (this.isAthlete()) {
          return athleteId ? this.eventService.getEventEnrollments(eventId, clubId, athleteId) : [];
        }

        if (this.isClub()) {
          return clubId ? this.eventService.getEventEnrollments(eventId, clubId) : [];
        }

        if (this.isFederation()) {
           if (approvedOnly) {
             return this.eventService.getApprovedEventEnrollments(eventId);
           }
           return this.eventService.getEventEnrollments(eventId);
        }

        return [];
      })
    ),
    { initialValue: null }
  );

  changeEnrollmentStatus(newStatus: EnrollmentStatus, enrollId: string, message: string) {
    if (confirm(message)) {
      this.eventService.updateEnrollmentStatus(enrollId, newStatus).subscribe({
        next: () => {
          alert(`Stato dell'iscrizione modificato con successo!`);
          window.location.reload();
        },
        error: (err: ErrorResponse) => {
          alert(`Errore durante la modifica dello stato dell'iscrizione: ${err.error.message}`);
        },
      });
    }
  }

  acceptEnrollment(enrollId: string) {
    this.changeEnrollmentStatus(EnrollmentStatus.APPROVED, enrollId, "Sei sicuro di voler accettare l'iscrizione?");
  }

  rejectEnrollment(enrollId: string) {
    this.changeEnrollmentStatus(EnrollmentStatus.REJECTED, enrollId, "Sei sicuro di voler rifiutare l'iscrizione?");
  }

  submitEnrollment(enrollId: string) {
    this.changeEnrollmentStatus(EnrollmentStatus.SUBMITTED, enrollId, "Sei sicuro di voler inviare l'iscrizione?");
  }


  getReadableCompetitonType(type: CompetitionType) {
    return readableCompetitionType(type);
  }

  getReadableEnrollmentStatus(status: EnrollmentStatus) {
    return readableEnrollmentStatus(status);
  }

  getEnrollmentStatusColorClass(status: EnrollmentStatus) {
    return enrollmentStatusColorClass(status)
  }

  getReadableAffiliationStatus(status: AffiliationStatus) {
    return readableAffiliationStatus(status);
  }

  getAffiliationStatusColorClass(status: AffiliationStatus) {
    return affiliationStatusColorClass(status)
  }

}
