import { CommonModule } from '@angular/common';
import { Component, computed, effect, inject, input, InputSignal, Signal } from '@angular/core';
import { toObservable, toSignal } from '@angular/core/rxjs-interop';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import {
  faArrowLeft,
  faArrowRotateLeft,
  faBan,
  faCalendarAlt,
  faCheck,
  faClipboardList,
  faExclamationTriangle,
  faInfoCircle,
  faMapMarkerAlt,
  faPen,
  faSave,
  faTimes,
  faTrophy,
  faUsers,
} from '@fortawesome/free-solid-svg-icons';
import { combineLatest, map, of, switchMap } from 'rxjs';
import { AuthService } from '../../../../../core/services/auth.service';
import { EventService } from '../../../../../core/services/event.service';
import { UserService } from '../../../../../core/services/user.service';
import {
  AffiliationStatus,
  readableAffiliationStatus
} from '../../../../../enums/affiliation-status.enum';
import { CompetitionType, readableCompetitionType } from '../../../../../enums/competition-type.enum';
import {
  EnrollmentStatus,
  enrollmentStatusColorClass,
  readableEnrollmentStatus,
} from '../../../../../enums/enrollment-status.enum';
import { Role } from '../../../../../enums/role.enum';
import { Athlete } from '../../../../../models/athlete.model';
import { CreateEnrollmentDTO, EnrollmentDTO, ErrorResponse } from '../../../../../models/dtos';
import { Enrollment } from '../../../../../models/enrollment.model';
import { User } from '../../../../../models/user.model';

@Component({
  selector: 'app-dashboard-event-enroll-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, FontAwesomeModule, RouterLink],
  templateUrl: './dashboard-event-enroll-form-component.html',
})
export class DashboardEventEnrollFormComponent {
  private fb = inject(FormBuilder);
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private authService = inject(AuthService);
  private eventService = inject(EventService);
  private userService = inject(UserService);

  enrollId: InputSignal<string | undefined> = input<string>();
  id: InputSignal<string | undefined> = input<string>();
  clubId: InputSignal<string | undefined> = input<string>();
  athleteId: InputSignal<string | undefined> = input<string>();

  isEditing = false;
  competitionTypes = Object.values(CompetitionType);
  eStatus = Object.values(EnrollmentStatus);
  eventName = '';

  icons = {
    faArrowLeft,
    faPen,
    faSave,
    faTimes,
    faInfoCircle,
    faCheck,
    faBan,
    faTrophy,
    faMapMarkerAlt,
    faClipboardList,
    faCalendarAlt,
    faUsers,
    faArrowRotateLeft,
    faExclamationTriangle,
  };

  isFederation = computed(() => this.authService.userRole() === Role.FEDERATION_MANAGER);
  isClub = computed(() => this.authService.userRole() === Role.CLUB_MANAGER);
  isAthlete = computed(() => this.authService.userRole() === Role.ATHLETE);

  form: FormGroup = this.fb.group({
    status: [EnrollmentStatus.DRAFT, Validators.required],
    competitionType: ['', Validators.required],
    athleteWeight: ['', [Validators.required]],
    athleteHeight: ['', [Validators.required]],
  });

  enrollment: Signal<Enrollment | null> = toSignal(
    combineLatest([
      toObservable(this.enrollId),
      toObservable(this.athleteId),
      toObservable(this.clubId),
      toObservable(this.id),
    ]).pipe(
      switchMap(([enrollId, athleteId, clubId, eventId]) => {
        if (this.isAthlete())
          this.eStatus = this.eStatus.filter((type) => type === EnrollmentStatus.DRAFT);
        else if (this.isClub())
          this.eStatus = this.eStatus.filter((type) => type === EnrollmentStatus.DRAFT || type === EnrollmentStatus.SUBMITTED || type === EnrollmentStatus.RETIRED);
        else if (this.isFederation())
          this.eStatus = this.eStatus.filter((type) => type === EnrollmentStatus.SUBMITTED || type === EnrollmentStatus.APPROVED || type === EnrollmentStatus.REJECTED);


        if (enrollId) {
          const enrollment = this.eventService.getEnrollmentById(enrollId);
          enrollment.subscribe({
            next: (enrll) => {
              this.eventService.getEventById(enrll.eventId).subscribe({
                next: (event) => {
                  this.competitionTypes = this.competitionTypes.filter((type) =>
                    event.disciplines.find((d) => d === type)
                  );
                  this.eventName = event.name;
                },
                error: (err: ErrorResponse) => alert('Errore: ' + err.error.message),
              });
            },
            error: (err: ErrorResponse) => alert('Errore: ' + err.error.message),
          });
          return enrollment;
        }

        const draftEnrollment: EnrollmentDTO = {
          id: '',
          eventId: eventId || '',
          clubId: clubId || '',
          competitionType: this.competitionTypes[0],
          enrollmentDate: new Date().toISOString(),
          status: EnrollmentStatus.DRAFT,
          athleteId: '',
          athleteClubName: '',
          athleteFirstname: '',
          athleteLastname: '',
          athleteWeight: '',
          athleteHeight: '',
          athleteGender: '',
          athleteAffiliationStatus: AffiliationStatus.EXPIRED,
          athleteMedicalCertificateExpireDate: '',
        };

        if (eventId) {
          this.eventService.getEventById(eventId).subscribe({
            next: (event) => {
              this.competitionTypes = this.competitionTypes.filter((type) =>
                event.disciplines.find((d) => d === type)
              );
              this.eventName = event.name;
            },
            error: (err: ErrorResponse) => alert('Errore: ' + err.error.message),
          });
        }
        if (athleteId) {
          return this.userService.getUserById(athleteId).pipe(
            map((user: User) => {
              const athleteUser = user as unknown as Athlete;
              return new Enrollment({
                ...draftEnrollment,
                athleteId: athleteId,
                athleteFirstname: athleteUser.firstName,
                athleteLastname: athleteUser.lastName,
                athleteWeight: athleteUser.weight.toString(),
                athleteHeight: athleteUser.height.toString(),
                athleteGender: athleteUser.gender,
                athleteAffiliationStatus: athleteUser.affiliationStatus,
                athleteMedicalCertificateExpireDate: athleteUser.medicalCertificateExpireDate,
              });
            })
          );
        }
        return of(new Enrollment(draftEnrollment));
      })
    ),
    { initialValue: null }
  );

  constructor() {
    effect(() => {
      const enrl = this.enrollment();
      if (enrl) {
        this.form.patchValue({
          status: enrl.status,
          competitionType: enrl.discipline,
          athleteWeight: enrl.athleteWeight,
          athleteHeight: enrl.athleteHeight,
        });

        this.form.disable();
      }
    });
  }

  isCertificateExpired(dateStr: string | null | undefined): boolean {
    if (!dateStr) return true;
    const expireDate = new Date(dateStr);
    const today = new Date();
    today.setHours(0, 0, 0, 0);
    return expireDate < today;
  }

  isAffiliationValid(status: string | null | undefined): boolean {
    return status === 'ACCEPTED';
  }

  toggleEdit() {
    this.isEditing = true;
    this.form.enable();
  }

  cancelEdit() {
    this.isEditing = false;
    this.form.disable();
    const enrl = this.enrollment();
    if (enrl) {
      this.form.patchValue({
        status: enrl.status,
        competitionType: enrl.discipline,
        athleteWeight: enrl.athleteWeight,
        athleteHeight: enrl.athleteHeight,
      });
    }
  }

  saveChanges() {
    if (this.form.invalid || !this.enrollment()) return;

    const rawData: EnrollmentDTO = {
      ...this.enrollment()?.toDTO(),
      ...this.form.getRawValue(),
    };

    const addFixedTime = (dateStr: string) => {
      return dateStr ? `${dateStr}T03:00:00` : null;
    };

    let updatedData = {
      ...rawData,
      enrollmentDate: rawData.enrollmentDate,
      athleteMedicalCertificateExpireDate:
        addFixedTime(rawData.athleteMedicalCertificateExpireDate) ?? '',
    };

    if (!this.enrollId()) {
      const newEnrollment: CreateEnrollmentDTO = {
        ...updatedData,
        draft: updatedData.status === EnrollmentStatus.DRAFT,
      };

      this.eventService.enrollAthlete(newEnrollment).subscribe({
        next: (res) => {
          alert('Iscrizione compilata con successo');
          this.isEditing = false;
          this.form.disable();
          this.router.navigate(['../../../'], { relativeTo: this.route });
        },

        error: (err: ErrorResponse) => alert('Errore nel salvataggio: ' + err.error.message),
      });
    } else {
      updatedData.id = this.enrollId() ?? '';
      const newEnrollmentData: Enrollment = new Enrollment(updatedData);

      this.eventService.updateEnrollment(newEnrollmentData).subscribe({
        next: (res) => {
          alert('Iscrizione modificata con successo');
          this.isEditing = false;
          this.form.disable();
          window.location.reload();
        },

        error: (err: ErrorResponse) => alert('Errore nel salvataggio: ' + err.error.message),
      });
    }
  }

  getReadableCompetitionType(type: CompetitionType) {
    return readableCompetitionType(type);
  }

  getReadableAffiliationStatus(status: AffiliationStatus) {
    return readableAffiliationStatus(status);
  }

  getReadableEnrollmentStatus(status: EnrollmentStatus) {
    return readableEnrollmentStatus(status);
  }

  getEnrollmentStatusClass(status: EnrollmentStatus) {
    return enrollmentStatusColorClass(status);
  }
}
