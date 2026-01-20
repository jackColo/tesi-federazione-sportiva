import { CommonModule } from '@angular/common';
import { Component, computed, inject } from '@angular/core';
import { RouterLink } from '@angular/router';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { faCalendarWeek, faMessage, faRightFromBracket, faTrophy, faUser, IconDefinition } from '@fortawesome/free-solid-svg-icons';
import { AuthService } from '../../../core/services/auth.service';

interface HeaderItem {
  id: string;
  icon: IconDefinition | null;
  routerLink: string;
  showWhenLoggedIn?: boolean;
  showWhenLoggedInRoles?: string[];
}

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [
    CommonModule,
    RouterLink,
    FontAwesomeModule
  ],
  templateUrl: './header.html',
})
export class Header {
  protected authService = inject(AuthService);
  faTrophy = faTrophy;
  faRightFromBracket = faRightFromBracket;

  private userRole = computed(() => this.authService.userRole());

  items: HeaderItem[] = [
    {
      id: 'events',
      icon: faCalendarWeek,
      routerLink: '/events',
    },
    {
      id: 'profile-dashboard',
      icon: faUser,
      routerLink: '/dashboard',
      showWhenLoggedIn: true,
    },
    {
      id: 'chat',
      icon: faMessage,
      routerLink: this.userRole() === 'CLUB_MANAGER' ? '/dashboard/support' : '/dashboard/inbox',
      showWhenLoggedIn: true,
    },
    {
      id: 'login',
      icon: faUser,
      routerLink: '/auth/login',
      showWhenLoggedIn: false,
    }
  ];

  public isLoggedIn = this.authService.isLoggedIn;
  public authUserRole = this.authService.userRole;

  public filteredItems = computed(() => {
      const isLoggedIn = this.isLoggedIn();
      const userRole = this.authUserRole();

      return this.items.filter(item => {
          if (item.showWhenLoggedIn && !isLoggedIn) {
            return false;
          }

          if (item.showWhenLoggedIn === false && isLoggedIn) {
            return false;
          }

          if (item.showWhenLoggedInRoles) {
            return item.showWhenLoggedInRoles.includes(userRole as string); 
          }

          return true;
      });
  });
}
