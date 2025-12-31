import { Component, computed, inject, Signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { 
  faChartLine, 
  faCalendar, 
  faUsers, 
  faRightFromBracket
} from '@fortawesome/free-solid-svg-icons';
import { AuthService } from '../../../../core/services/auth.service';
import { readableRole, Role } from '../../../../enums/role.enum';
import { UserDTO } from '../../../../models/dtos';
import { UserService } from '../../../../core/services/user.service';
import { toSignal } from '@angular/core/rxjs-interop';
import { User } from '../../../../models/user.model';

@Component({
  selector: 'app-sidebar',
  standalone: true,
  imports: [CommonModule, RouterLink, RouterLinkActive, FontAwesomeModule],
  templateUrl: './sidebar-component.html',
})
export class SidebarComponent {
  private authService = inject(AuthService);
  private userService = inject(UserService);
  protected readonly Role = Role;
  
  faChartLine = faChartLine;
  faCalendar = faCalendar;
  faUsers = faUsers;
  faSignOut = faRightFromBracket;

  userClaims = this.authService.userClaims;

  userEmail = computed(() => {
    return this.userClaims()?.sub || 'Utente';
  });

  user: Signal<User | null> = toSignal(
    this.userService.getUserByEmail(this.userEmail()),
    { initialValue: null }
  );

  userInitial = computed(() => {
    const u = this.user();
    return (u ? u.firstName.charAt(0) : '').toUpperCase();
  });

  userRole = computed(() => {
    return this.userClaims()?.roles[0]?.authority;
  });

  userReadableRole = computed(() => {
    return readableRole(this.userRole() as Role);
  });

  showEventsSection = computed(() => {
    return [Role.FEDERATION_MANAGER, Role.CLUB_MANAGER, Role.ATHLETE].includes(this.userRole() as Role);
  });

  showClubsSection = computed(() => {
    return [Role.FEDERATION_MANAGER, Role.CLUB_MANAGER].includes(this.userRole() as Role);
  });

  logout(): void {
    this.authService.logout();
  }
}