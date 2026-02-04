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
  readableAffiliationStatus,
} from '../../../../../enums/affiliation-status.enum';
import {
  CompetitionType,
  readableCompetitionType,
} from '../../../../../enums/competition-type.enum';
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
import { AthleteService } from '../../../../../core/services/athlete.service';

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
  private athleteService = inject(AthleteService);
  private eventService = inject(EventService);
  private userService = inject(UserService);

  enrollId: InputSignal<string | undefined> = input<string>();
  id: InputSignal<string | undefined> = input<string>();
  clubId: InputSignal<string | undefined> = input<string>();
  athleteId: InputSignal<string | undefined> = input<string>();

  isEditing = false;
  competitionTypes = Object.values(CompetitionType);
  avaiableEnrollStatus = Object.values(EnrollmentStatus);
  affiliationStatuses = Object.values(AffiliationStatus);
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
    athleteMedicalCertificateExpireDate: ['', Validators.required],
    athleteAffiliationStatus: [''],
  });

  enrollment: Signal<Enrollment | null> = toSignal(
    combineLatest([
      toObservable(this.enrollId),
      toObservable(this.athleteId),
      toObservable(this.clubId),
      toObservable(this.id),
    ]).pipe(
      switchMap(([enrollId, athleteId, clubId, eventId]) => {
        this.avaiableEnrollStatus = Object.values(EnrollmentStatus);

        if (enrollId) {
          return this.eventService.getEnrollmentById(enrollId).pipe(
            switchMap((enrll) => {
              this.eventService.getEventById(enrll.eventId).subscribe({
                next: (event) => {
                  this.competitionTypes = this.competitionTypes.filter((type) =>
                    event.disciplines.find((d) => d === type),
                  );
                  this.eventName = event.name;
                },
                error: (err: ErrorResponse) =>
                  alert("Errore durante il recupero dell'iscrizione: " + err.error.message),
              });
              return of(enrll);
            }),
          );
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
                event.disciplines.find((d) => d === type),
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
            }),
          );
        }
        return of(new Enrollment(draftEnrollment));
      }),
    ),
    { initialValue: null },
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
          athleteMedicalCertificateExpireDate: enrl.athleteMedicalCertificateExpireDate
            ? enrl.athleteMedicalCertificateExpireDate.split('T')[0]
            : '',
          athleteAffiliationStatus: enrl.athleteAffiliationStatus,
        });
        this.form.disable();
      }
    });
  }

  // Restituisce gli stati disponibili per la transizione in base al ruolo e allo stato corrente dell'iscrizione
  getAvailableStatuses(currentStatus: EnrollmentStatus): EnrollmentStatus[] {
    if (this.isFederation()) {
      return [EnrollmentStatus.SUBMITTED, EnrollmentStatus.APPROVED, EnrollmentStatus.REJECTED];
    }

    if (this.isClub()) {
      if (currentStatus === EnrollmentStatus.DRAFT) {
        return [EnrollmentStatus.DRAFT, EnrollmentStatus.SUBMITTED];
      }
      if (currentStatus === EnrollmentStatus.REJECTED) {
        return [EnrollmentStatus.REJECTED, EnrollmentStatus.DRAFT];
      }
      if (
        currentStatus === EnrollmentStatus.SUBMITTED ||
        currentStatus === EnrollmentStatus.APPROVED
      ) {
        return [currentStatus, EnrollmentStatus.RETIRED];
      }
    }

    if (this.isAthlete()) {
      return [EnrollmentStatus.DRAFT];
    }

    return [currentStatus];
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
    const currentStatus = this.form.get('status')?.value as EnrollmentStatus;
    const isDraft = currentStatus === EnrollmentStatus.DRAFT;

    if (this.isFederation()) {
      this.form.enable(); // Federation Manager può modificare tutto
    } else {
      // Club Manager e Atleta:
      // - in bozza possono modificare tutto tranne lo stato dell'affiliazione
      // - fuori bozza, l'atleta non può fare nulla mentre il club manager può solo cambiare lo stato (verso stati consentiti)
      if (isDraft) {
        this.form.enable();
        this.form.get('athleteAffiliationStatus')?.disable();
      } else {
        this.form.disable();
        if (this.isClub()) {
          this.form.get('status')?.enable();
        }
      }
    }
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
        athleteMedicalCertificateExpireDate: enrl.athleteMedicalCertificateExpireDate
          ? enrl.athleteMedicalCertificateExpireDate.split('T')[0]
          : '',
        athleteAffiliationStatus: enrl.athleteAffiliationStatus,
      });
    }
  }

  saveChanges() {
    if (this.form.invalid || !this.enrollment()) return;

    // Controllo modifiche per alert di conferma
    const certDateControl = this.form.get('athleteMedicalCertificateExpireDate');
    const affStatusControl = this.form.get('athleteAffiliationStatus');

    if (certDateControl?.dirty) {
      const msg =
        "Attenzione: La data di scadenza del certificato verrà aggiornata solo per l'iscrizione attuale.\n\nRicordati di aggiornarla anche nella scheda dell'atleta.";
      if (!confirm(msg)) return;
    }
    const shouldUpdateAthleteStatus = this.isFederation() && affStatusControl?.dirty;

    if (shouldUpdateAthleteStatus) {
      const msg =
        "Attenzione: Stai modificando lo stato di affiliazione.\n\nQuesto aggiornamento verrà applicato anche al profilo dell'atleta. Continuare?";
      if (!confirm(msg)) return;
    }

    const rawData: EnrollmentDTO = {
      ...this.enrollment()?.toDTO(),
      ...this.form.getRawValue(),
    };

    const addFixedTime = (dateStr: string) => {
      if (!dateStr) return '';
      return dateStr.includes('T') ? dateStr : `${dateStr}T03:00:00`;
    };

    let updatedData = {
      ...rawData,
      athleteMedicalCertificateExpireDate: addFixedTime(
        rawData.athleteMedicalCertificateExpireDate,
      ),
      athleteAffiliationStatus: rawData.athleteAffiliationStatus,
    };

    if (!this.enrollId()) {
      const newEnrollment: CreateEnrollmentDTO = {
        ...updatedData,
        draft: updatedData.status === EnrollmentStatus.DRAFT,
      };

      this.eventService.enrollAthlete(newEnrollment).subscribe({
        next: () => {
          alert('Iscrizione compilata con successo');
          this.isEditing = false;
          this.form.disable();
          this.router.navigate(['../../../'], { relativeTo: this.route });
        },

        error: (err: ErrorResponse) => alert('Errore nel salvataggio: ' + err.error.message),
      });
    } else {
      updatedData.id = this.enrollId() ?? '';

      const athleteId = this.enrollment()?.athleteId;
      if (shouldUpdateAthleteStatus && athleteId) {
        // Aggiornamento Atleta
        this.athleteService
          .updateAthleteAffiliationStatus(athleteId, updatedData.athleteAffiliationStatus)
          .subscribe({
            next: () => {
              alert(`Affiliazione confermata con successo!`);
              const newEnrollmentData: Enrollment = new Enrollment(updatedData);
              // Aggiornamento Iscrizione
              return this.eventService.updateEnrollment(newEnrollmentData).subscribe({
                next: () => {
                  alert('Iscrizione modificata con successo');
                  this.finalizeSave();
                },
                error: (err: ErrorResponse) => {
                  alert(`Errore': ${err.error.message}`);
                },
              });
            },
            error: (err: ErrorResponse) => {
              alert(`Errore': ${err.error.message}`);
            },
          });
      } else {
        const newEnrollmentData: Enrollment = new Enrollment(updatedData);
        this.eventService.updateEnrollment(newEnrollmentData).subscribe({
          next: () => {
            alert('Iscrizione modificata con successo');
            this.finalizeSave();
          },
          error: (err: ErrorResponse) => alert('Errore nel salvataggio: ' + err.error.message),
        });
      }
    }
  }

  finalizeSave() {
    this.isEditing = false;
    this.form.disable();
    window.location.reload();
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
