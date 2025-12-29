import { Component, computed, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { 
  faChartLine, 
  faCalendar, 
  faUsers, 
  faRightFromBracket
} from '@fortawesome/free-solid-svg-icons';
import { AuthService } from '../../../core/services/auth.service';
import { readableRole, Role } from '../../../enums/role.enum';

@Component({
  selector: 'app-sidebar',
  standalone: true,
  imports: [CommonModule, RouterLink, RouterLinkActive, FontAwesomeModule],
  templateUrl: './sidebar-component.html',
  styleUrl: './sidebar-component.scss'
})
export class SidebarComponent {
  private authService = inject(AuthService);
  protected readonly Role = Role;
  
  faChartLine = faChartLine;
  faCalendar = faCalendar;
  faUsers = faUsers;
  faSignOut = faRightFromBracket;

  userClaims = this.authService.userClaims;

  userInitial = computed(() => {
    const sub = this.userClaims()?.sub;
    return sub ? sub.charAt(0).toUpperCase() : 'A';
  });

  userEmail = computed(() => {
    return this.userClaims()?.sub || 'Utente';
  });


  userRole = computed(() => {
    return this.userClaims()?.roles[0]?.authority;
  });

  userReadableRole = computed(() => {
    return readableRole(this.userRole() as Role);
  });

  logout(): void {
    this.authService.logout();
  }
}