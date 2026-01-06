import { CommonModule } from '@angular/common';
import { Component, computed, inject, input, InputSignal } from '@angular/core';
import { toObservable, toSignal } from '@angular/core/rxjs-interop';
import { RouterLink } from '@angular/router';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import {
  faBan,
  faBuilding,
  faCalendarAlt,
  faCalendarCheck,
  faCheckCircle,
  faChevronRight,
  faClock,
  faExclamationCircle,
  faFileMedical,
  faHistory,
  faIdCard,
  faMars,
  faRulerVertical,
  faUser,
  faVenus,
  faWeight
} from '@fortawesome/free-solid-svg-icons';
import { switchMap } from 'rxjs';
import { AthleteService } from '../../../../../core/services/athlete.service';
import { AuthService } from '../../../../../core/services/auth.service';
import { ClubService } from '../../../../../core/services/club.service';
import {
  AffiliationStatus,
  affiliationStatusColorClass,
  readableAffiliationStatus,
  statusIcon,
} from '../../../../../enums/affiliation-status.enum';
import { Role } from '../../../../../enums/role.enum';
import { Athlete } from '../../../../../models/athlete.model';
import { ErrorResponse } from '../../../../../models/dtos';

@Component({
  selector: 'app-dashboard-clubs-athlete-card-component',
  standalone: true,
  imports: [CommonModule, FontAwesomeModule, RouterLink],
  templateUrl: './dashboard-clubs-athlete-card-component.html',
})
export class DashboardClubsAthleteCardComponent {
  private clubService = inject(ClubService);
  private athleteService = inject(AthleteService);
  private authService = inject(AuthService);
  athlete: InputSignal<Athlete> = input.required<Athlete>();
  icons = {
    faUser,
    faWeight,
    faRulerVertical,
    faFileMedical,
    faCalendarAlt,
    faChevronRight,
    faCheckCircle,
    faExclamationCircle,
    faBuilding,
    faMars,
    faVenus,
    faIdCard,
    faCalendarCheck,
    faHistory,
    faClock,
    faBan
  };

  private club = toSignal(
    toObservable(this.athlete).pipe(
      switchMap((athlete) => this.clubService.getClub(athlete.clubId))
    ),
    { initialValue: null }
  );

  getClubName = computed(() => {
    const c = this.club();
    return c ? c.name : 'Nessun Club';
  });

  isAdmin = computed(() => this.authService.userRole() === Role.FEDERATION_MANAGER);
  isManager = computed(() => this.authService.userRole() === Role.CLUB_MANAGER);

  approveAthleteAffiliation(clubId: string) {
    this.athleteService.updateAthleteAffiliationStatus(clubId, AffiliationStatus.ACCEPTED).subscribe({
      next: () => {
        alert(`Affiliazione confermata con successo!`);
      },
      error: (err: ErrorResponse) => {
        alert(`Errore durante la conferma dell'affiliazione': ${err.error.message}`);
      },
    });
  }

  rejectAthleteAffiliation(clubId: string) {
    this.athleteService.updateAthleteAffiliationStatus(clubId, AffiliationStatus.REJECTED).subscribe({
      next: () => {
        alert(`Affiliazione respinta con successo!`);
      },
      error: (err: ErrorResponse) => {
        alert(`Errore durante il rifiuto dell'affiliazione': ${err.error.message}`);
      },
    });
  }

  renewAthleteAffiliation(clubId: string) {
    this.athleteService.renewAthleteAffiliationRequest(clubId).subscribe({
      next: () => {
        alert(`Richiesta inviata con successo!`);
      },
      error: (err: ErrorResponse) => {
        alert(`Errore durante l'invio della richiesta': ${err.error.message}`);
      },
    });
  }

  statusColorClass(status: AffiliationStatus) {
    affiliationStatusColorClass(status);
  }

  readableStatusName(status: AffiliationStatus) {
    readableAffiliationStatus(status);
  }

  medicalStatus = computed(() => {
    const isValid = this.athlete().isMedicalCertificateValid();
    return {
      isValid,
      label: isValid ? 'Valido' : 'Scaduto/Mancante',
      colorClass: isValid ? 'text-emerald-600' : 'text-red-600',
      bgClass: isValid ? 'bg-emerald-50' : 'bg-red-50',
      icon: isValid ? faCheckCircle : faExclamationCircle,
    };
  });

  affiliationUI = computed(() => {
    const status = this.athlete().affiliationStatus;
    const label = readableAffiliationStatus(status);
    const cssClasses = affiliationStatusColorClass(status);
    const icon = statusIcon(status);

    return { label, cssClasses, icon };
  });
}
