import { CommonModule } from '@angular/common';
import { Component, computed, inject, input, InputSignal } from '@angular/core';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { Athlete } from '../../../../../models/athlete.model';
import {
  faBuilding,
  faCalendarAlt,
  faCheckCircle,
  faChevronRight,
  faExclamationCircle,
  faFileMedical,
  faMars,
  faRulerVertical,
  faUser,
  faVenus,
  faWeight,
  faIdCard,
  faCalendarCheck,
  faHistory,
  faClock,
  faBan,
} from '@fortawesome/free-solid-svg-icons';
import {
  AffiliationStatus,
  affiliationStatusColorClass,
  readableAffiliationStatus,
} from '../../../../../enums/affiliation-status.enum';
import { RouterLink } from '@angular/router';
import { ClubService } from '../../../../../core/services/club.service';
import { toObservable, toSignal } from '@angular/core/rxjs-interop';
import { switchMap } from 'rxjs';
import { Role } from '../../../../../enums/role.enum';
import { AthleteService } from '../../../../../core/services/athlete.service';
import { AuthService } from '../../../../../core/services/auth.service';

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
    faBan,
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

  approveAthleteAffiliation(clubId: string) {
    this.athleteService.approveAthlete(clubId).subscribe({
      next: () => {
        alert(`Affiliazione confermata con successo!`);
      },
      error: (err) => {
        alert(`Errore durante la conferma dell'affiliazione': ${err.message}`);
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
      icon: isValid ? this.icons.faCheckCircle : this.icons.faExclamationCircle,
    };
  });

  affiliationUI = computed(() => {
    const status = this.athlete().affiliationStatus;
    const label = readableAffiliationStatus(status);
    const cssClasses = affiliationStatusColorClass(status);

    let icon;
    switch (status) {
      case AffiliationStatus.SUBMITTED:
        icon = this.icons.faClock;
        break;
      case AffiliationStatus.ACCEPTED:
        icon = this.icons.faCheckCircle;
        break;
      case AffiliationStatus.REJECTED:
        icon = this.icons.faBan;
        break;
      case AffiliationStatus.EXPIRED:
        icon = this.icons.faExclamationCircle;
        break;
      default:
        icon = this.icons.faExclamationCircle;
    }

    return { label, cssClasses, icon };
  });
}
