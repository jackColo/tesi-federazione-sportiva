import { CommonModule } from '@angular/common';
import { Component, computed, inject, Signal, signal } from '@angular/core';
import { toObservable, toSignal } from '@angular/core/rxjs-interop';
import { ActivatedRoute, Router } from '@angular/router';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import {
  faBuilding,
  faCheck,
  faCheckCircle,
  faPen,
  faUserPlus,
  faUsers,
} from '@fortawesome/free-solid-svg-icons';
import { filter, from, switchMap } from 'rxjs';
import { AthleteService } from '../../../../core/services/athlete.service';
import { AuthService } from '../../../../core/services/auth.service';
import { ClubService } from '../../../../core/services/club.service';
import {
  AffiliationStatus,
  affiliationStatusColorClass,
  readableAffiliationStatus,
} from '../../../../enums/affiliation-status.enum';
import { Role } from '../../../../enums/role.enum';
import { Athlete } from '../../../../models/athlete.model';
import { Club } from '../../../../models/club.model';
import { DashboardClubsAthleteCardComponent } from './dashboard-clubs-athlete-card-component/dashboard-clubs-athlete-card-component';
import { DashboardClubsCardComponent } from './dashboard-clubs-card-component/dashboard-clubs-card-component';

type FedTab = 'CLUB' | 'ATHLETE';

@Component({
  selector: 'app-dashboard-clubs-component',
  standalone: true,
  imports: [CommonModule, FontAwesomeModule, DashboardClubsAthleteCardComponent, DashboardClubsCardComponent],
  templateUrl: './dashboard-clubs-component.html',
})
export class DashboardClubsComponent {
  private authService = inject(AuthService);
  private clubService = inject(ClubService);
  private athleteService = inject(AthleteService);
  private router = inject(Router);
  private route = inject(ActivatedRoute);

  icons = {
    faUserPlus,
    faBuilding,
    faUsers,
    faCheckCircle,
    faPen,
    faCheck,
  };

  role = computed(() => this.authService.userRole());
  isFederation = computed(() => this.role() === Role.FEDERATION_MANAGER);
  isClub = computed(() => this.role() === Role.CLUB_MANAGER);

  currentFedTab = signal<FedTab>('CLUB');

  pendingAffiliations = signal<number>(0);

  myClub: Signal<Club | null> = this.isClub()
    ? toSignal(from(this.clubService.getClub('6939613521b52e78f714e402')), { initialValue: null })
    : signal(null);

  athletes: Signal<Athlete[] | null> = toSignal(
    this.isClub()
      ? toObservable(this.myClub).pipe(
          filter((u): u is Club => !!u),
          switchMap((club) => this.athleteService.getAtheltes(club.id))
        )
      : this.athleteService.getAthletes(),
    { initialValue: null }
  );

  clubs: Signal<Club[] | null> = toSignal(
    this.clubService.getAllClubs(),
    {initialValue: null}
  )

  statusReadableName(status: AffiliationStatus) {
    return readableAffiliationStatus(status);
  }

  statusClass(status: AffiliationStatus) {
    return affiliationStatusColorClass(status);
  }

  navigateToAddAthlete() {
    this.router.navigate(['dashboard', 'user', 'new']);
  }

  navigateToAthleteDetail(clubId: string) {
    this.router.navigate(['athletes', clubId], { relativeTo: this.route });
  }

  navigateToMyClubDetail() {
    const clubId = this.myClub()?.id;
    if (clubId) {
      this.router.navigate([clubId], { relativeTo: this.route });
    }
  }

  navigateToClubDetail(clubId: string) {
    this.router.navigate([clubId], { relativeTo: this.route });
  }

  setTab(tab: FedTab) {
    this.currentFedTab.set(tab);
    switch (tab) {
      case 'CLUB':
        // Carica la lista dei club
        break;
      case 'ATHLETE':
        // Carica la lista degli atleti
        break;
    }
  }
}
