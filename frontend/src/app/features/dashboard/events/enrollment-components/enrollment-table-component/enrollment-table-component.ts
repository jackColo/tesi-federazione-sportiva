import { CommonModule } from '@angular/common';
import {
    Component,
    computed,
    inject,
    Input,
    input,
    Signal
} from '@angular/core';
import { toObservable, toSignal } from '@angular/core/rxjs-interop';
import { RouterLink } from '@angular/router';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { faEye, faFilePdf, faUserSlash } from '@fortawesome/free-solid-svg-icons';
import { combineLatest, of, switchMap } from 'rxjs';
import { AuthService } from '../../../../../core/services/auth.service';
import { EventService } from '../../../../../core/services/event.service';
import { EnrollmentStatus, enrollmentStatusColorClass, readableEnrollmentStatus } from '../../../../../enums/enrollment-status.enum';
import { Role } from '../../../../../enums/role.enum';
import { Enrollment } from '../../../../../models/enrollment.model';
import { CompetitionType, readableCompetitionType } from '../../../../../enums/competition-type.enum';

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

  icons = { faUserSlash, faFilePdf, faEye };

  isFederation = computed(() => this.authService.userRole() === Role.FEDERATION_MANAGER);
  isClub = computed(() => this.authService.userRole() === Role.CLUB_MANAGER);
  isAthlete = computed(() => this.authService.userRole() === Role.ATHLETE);

  enrollments: Signal<Enrollment[] | null> = toSignal(
    combineLatest([
      toObservable(this.eventId),
      toObservable(this.athleteId),
      toObservable(this.clubId),
    ]).pipe(
      switchMap(([eventId, athleteId, clubId]) => {
        if (!eventId) return of([]);

        if (this.isAthlete()) {
          return athleteId ? this.eventService.getEventEnrollments(eventId, clubId, athleteId) : [];
        }

        if (this.isClub()) {
          return clubId ? this.eventService.getEventEnrollments(eventId, clubId) : [];
        }

        return this.eventService.getEventEnrollments(eventId);
      })
    ),
    { initialValue: null }
  );

  downloadReport() {}

  changeEnrollmentStatus(newStatus: EnrollmentStatus) {}

  acceptEnrollment() {
    this.changeEnrollmentStatus(EnrollmentStatus.APPROVED);
  }

  rejectEnrollment() {
    this.changeEnrollmentStatus(EnrollmentStatus.REJECTED);
  }

  submitEnrollment() {
    this.changeEnrollmentStatus(EnrollmentStatus.SUBMITTED);
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

}
