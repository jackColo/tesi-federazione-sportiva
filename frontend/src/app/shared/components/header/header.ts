import { CommonModule } from '@angular/common';
import { Component, computed, inject } from '@angular/core';
import { RouterLink } from '@angular/router';
import { faUser, faCalendarWeek, faTrophy, IconDefinition } from '@fortawesome/free-solid-svg-icons';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { AuthService } from '../../../core/services/auth.service';
import { Role } from '../../../enums/role.enum';

interface HeaderItem {
  label: string | null;
  icon: IconDefinition | null;
  routerLink: string;
  showWhenLoggedIn?: boolean;
  showWhenLoggedInRoles?: string[];
}

@Component({
  selector: 'app-header',
  imports: [
    CommonModule,
    RouterLink,
    FontAwesomeModule
  ],
  templateUrl: './header.html',
  styleUrl: './header.scss',
})
export class Header {
  private authService = inject(AuthService);
  faTrophy = faTrophy;

  items: HeaderItem[] = [
    {
      label: null,
      icon: faCalendarWeek,
      routerLink: '/event',
    },
    {
      label: null,
      icon: faUser,
      routerLink: '/dashboard',
      showWhenLoggedIn: true,
    },
    {
      label: 'Admin',
      icon: null,
      routerLink: '/admin/events/create',
      showWhenLoggedIn: true,
      showWhenLoggedInRoles: [Role.FEDERATION_MANAGER],
    },
    {
      label: null,
      icon: faUser,
      routerLink: '/auth/login',
      showWhenLoggedIn: false,
    },
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
