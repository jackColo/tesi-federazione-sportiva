import { CommonModule } from '@angular/common';
import { Component, computed, inject, input, InputSignal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import {
  AffiliationStatus,
  affiliationStatusColorClass,
  readableAffiliationStatus,
} from '../../../../../enums/affiliation-status.enum';
import { Club } from '../../../../../models/club.model';
import {
  faBan,
  faBuilding,
  faCalendarCheck,
  faCheckCircle,
  faClock,
  faExclamationCircle,
  faHistory,
  faIdCard,
  faMapMarkerAlt,
  faPen,
} from '@fortawesome/free-solid-svg-icons';
import { ClubService } from '../../../../../core/services/club.service';
import { AuthService } from '../../../../../core/services/auth.service';
import { Role } from '../../../../../enums/role.enum';

@Component({
  selector: 'app-dashboard-clubs-card-component',
  standalone: true,
  imports: [CommonModule, FontAwesomeModule, RouterLink],
  templateUrl: './dashboard-clubs-card-component.html',
})
export class DashboardClubsCardComponent {
  private clubService = inject(ClubService);
  private authService = inject(AuthService);
  club: InputSignal<Club> = input.required<Club>();

  icons = {
    faClock,
    faCheckCircle,
    faBan,
    faExclamationCircle,
    faBuilding,
    faPen,
    faIdCard,
    faMapMarkerAlt,
    faCalendarCheck,
    faHistory,
  };

  isAdmin = computed(() => this.authService.userRole() === Role.FEDERATION_MANAGER);

  approveClubAffiliation(clubId: string) {
    this.clubService.updateAffiliationStatus(clubId, AffiliationStatus.ACCEPTED).subscribe({
      next: () => {
        alert(`Club approvato con successo!`);
      },
      error: (err) => {
        alert(`Errore durante l'approvazione del club': ${err.message}`);
      },
    });
  }

  rejectClubAffiliation(clubId: string) {
    this.clubService.updateAffiliationStatus(clubId, AffiliationStatus.REJECTED).subscribe({
      next: () => {
        alert(`Richiesta del club respinta con successo!`);
      },
      error: (err) => {
        alert(`Errore durante il rifiuto della richiesta del club': ${err.message}`);
      },
    });
  }

  statusColorClass(status: AffiliationStatus) {
    affiliationStatusColorClass(status);
  }

  readableStatusName(status: AffiliationStatus) {
    readableAffiliationStatus(status);
  }

  affiliationUI = computed(() => {
    const status = this.club().affiliationStatus;
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
