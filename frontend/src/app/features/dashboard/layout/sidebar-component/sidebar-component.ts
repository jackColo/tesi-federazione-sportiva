import { CommonModule } from '@angular/common';
import { Component, computed, inject, Signal } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import {
  faCalendar,
  faChartLine,
  faEnvelope,
  faHeadset,
  faRightFromBracket,
  faUsers,
  faUserShield
} from '@fortawesome/free-solid-svg-icons';
import { AuthService } from '../../../../core/services/auth.service';
import { UserService } from '../../../../core/services/user.service';
import { readableRole, Role } from '../../../../enums/role.enum';
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
  
  icons = {
    faEnvelope,
    faHeadset,
    faChartLine,
    faCalendar,
    faUsers,
    faRightFromBracket,
    faUserShield
  }

  userEmail = computed(() => {
    return this.authService.currentUserEmail() || 'Utente sconosciuto';
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
    return this.authService.userRole();
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

  showAdminChat = computed(() => {
    return this.userRole() === Role.FEDERATION_MANAGER;
  });

  showClubChat = computed(() => {
    return this.userRole() === Role.CLUB_MANAGER;
  });

  showFederationManagers = computed(() => {
    return this.userRole() === Role.FEDERATION_MANAGER;
  });

  logout(): void {
    this.authService.logout();
  }
}