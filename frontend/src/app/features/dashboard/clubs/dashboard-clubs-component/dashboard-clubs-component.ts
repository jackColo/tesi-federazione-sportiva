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
  IconDefinition,
} from '@fortawesome/free-solid-svg-icons';
import { filter, of, switchMap } from 'rxjs';
import { AthleteService } from '../../../../core/services/athlete.service';
import { AuthService } from '../../../../core/services/auth.service';
import { ClubService } from '../../../../core/services/club.service';
import { UserService } from '../../../../core/services/user.service';
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

interface FedTab {
  id: string;
  label: string;
  icon: IconDefinition;
}

@Component({
  selector: 'app-dashboard-clubs-component',
  standalone: true,
  imports: [
    CommonModule,
    FontAwesomeModule,
    DashboardClubsAthleteCardComponent,
    DashboardClubsCardComponent,
  ],
  templateUrl: './dashboard-clubs-component.html',
})
export class DashboardClubsComponent {
  private authService = inject(AuthService);
  private userService = inject(UserService);
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

  tabs: FedTab[] = [
    { id: 'CLUB', label: 'Club', icon: this.icons.faBuilding },
    { id: 'ATHLETE', label: 'Atleti', icon: this.icons.faUsers },
  ];

  role = computed(() => this.authService.userRole());
  isFederation = computed(() => this.role() === Role.FEDERATION_MANAGER);
  isClubManager = computed(() => this.role() === Role.CLUB_MANAGER);

  currentFedTab = signal<FedTab | null>(this.tabs.find(tab => tab.id === 'CLUB') ?? null);

  pendingAffiliations = signal<number>(0);

  myClub: Signal<Club | null> = toSignal(
    toObservable(this.authService.currentUserId).pipe(
      filter((userId) => !!userId && this.isClubManager()),
      switchMap((userId) => this.userService.getUserById(userId!)),
      switchMap((user) => {
        const clubId = (user as any).clubId; 
        if (clubId) {
          return this.clubService.getClub(clubId);
        }
        return of(null);
      })
    ), { initialValue: null });

  athletes: Signal<Athlete[] | null> = toSignal(
    this.isClubManager()
      ? toObservable(this.myClub).pipe(
          filter((u): u is Club => !!u),
          switchMap((club) => this.athleteService.getAtheltes(club.id))
        )
      : this.athleteService.getAthletes(),
    { initialValue: null }
  );

  clubs: Signal<Club[] | null> = toSignal(this.clubService.getAllClubs(), { initialValue: null });

  statusReadableName(status: AffiliationStatus) {
    return readableAffiliationStatus(status);
  }

  statusClass(status: AffiliationStatus) {
    return affiliationStatusColorClass(status);
  }

  navigateToAddAthlete() {
    if (this.myClub()?.affiliationStatus !== AffiliationStatus.ACCEPTED && !this.myClub()?.firstAffiliationDate) {
      alert("L'affiliazione del tuo club deve essere approvata per poter affiliare i tuoi atleti.");
      return;
    }
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

  setTab(tabId: string) {
    this.currentFedTab.set(this.tabs.find(tab => tab.id === tabId) ?? null);
  }
}
