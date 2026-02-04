import { CommonModule } from '@angular/common';
import { Component, computed, inject, input, InputSignal } from '@angular/core';
import { toObservable, toSignal } from '@angular/core/rxjs-interop';
import { RouterLink } from '@angular/router';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import {
  faChevronRight
} from '@fortawesome/free-solid-svg-icons';
import { switchMap } from 'rxjs';
import { ClubService } from '../../../../../core/services/club.service';
import { ClubManager } from '../../../../../models/club-manager.model';

@Component({
  selector: 'app-dashboard-clubs-club-manager-card-component',
  standalone: true,
  imports: [CommonModule, FontAwesomeModule, RouterLink],
  templateUrl: './dashboard-clubs-club-manager-card-component.html',
})
export class DashboardClubsClubManagerCardComponent {
  private clubService = inject(ClubService);
  clubManager: InputSignal<ClubManager> = input.required<ClubManager>();
  faChevronRight = faChevronRight;

  private club = toSignal(
    toObservable(this.clubManager).pipe(
      switchMap((clubManager) => this.clubService.getClub(clubManager.clubId))
    ),
    { initialValue: null }
  );

  getClubName = computed(() => {
    const c = this.club();
    return c ? c.name : 'Nessun Club';
  });
}