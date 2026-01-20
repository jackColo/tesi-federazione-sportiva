import { CommonModule } from '@angular/common';
import { Component, computed, inject, Signal } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { RouterLink } from '@angular/router';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { 
  faEdit, 
  faEnvelope, 
  faUserPlus, 
  faBolt, 
  faCalendar, 
  faUsers, 
  faSearch, 
  faList, 
  faHeadset, 
  faTicket, 
  faBuilding, 
  faUser
} from '@fortawesome/free-solid-svg-icons';
import { AuthService } from '../../../../core/services/auth.service';
import { UserService } from '../../../../core/services/user.service';
import { readableRole, Role, roleClass } from '../../../../enums/role.enum';
import { User } from '../../../../models/user.model';

@Component({
  selector: 'app-dashboard-component',
  standalone: true,
  imports: [CommonModule, RouterLink, FontAwesomeModule],
  templateUrl: './dashboard-component.html',
})
export class DashboardComponent {
  private userService = inject(UserService);
  private authService = inject(AuthService);

  icons = {
    email: faEnvelope,
    edit: faEdit,
    
    // Icone generiche
    add: faUserPlus,       // Usata per "Nuovo Admin"
    userPlus: faUserPlus,  // Usata per "Registra Atleta"
    bolt: faBolt,          // Icona decorativa header
    
    // Icone Admin
    calendar: faCalendar,  // Crea Evento
    users: faUsers,        // Gestione Club
    
    // Icone Atleta
    user: faUser,      // Cerca Eventi
    list: faList,          // Iscrizioni
    headset: faHeadset,    // Supporto
    
    // Icone Club Manager
    ticket: faTicket,      // Iscrivi a Gara
    building: faBuilding   // Il mio Club
  };

  userEmail = computed(() => this.authService.currentUserEmail() || '');

  user: Signal<User | null> = toSignal(
    this.userService.getUserByEmail(this.userEmail()),
    { initialValue: null }
  );

  isAthlete = computed(() => this.user()?.role === Role.ATHLETE);
  isAdmin = computed(() => this.user()?.role === Role.FEDERATION_MANAGER as Role);

  userInitials = computed(() => {
    const u = this.user();
    return u ? (u.firstName.charAt(0) + u.lastName.charAt(0)).toUpperCase() : '';
  });

  userReadableRole = computed(() => {
    const role = this.user()?.role;
    return role ? readableRole(role) : '';
  })

  roleClass = computed(() => {
    const role = this.user()?.role;
    return role ? roleClass(role): '';
  })

}