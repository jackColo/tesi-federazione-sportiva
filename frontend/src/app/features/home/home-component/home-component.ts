import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { 
  faArrowRight, 
  faTrophy, 
  faUsers, 
  faCalendarCheck, 
  faHandshake 
} from '@fortawesome/free-solid-svg-icons';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-home-component',
  standalone: true,
  imports: [CommonModule, RouterLink, FontAwesomeModule],
  templateUrl: './home-component.html',
})
export class HomeComponent {
  private authService = inject(AuthService)
  isLoggedIn = this.authService.isLoggedIn;
  
  // Icone
  icons = {
    arrow: faArrowRight,
    trophy: faTrophy,
    users: faUsers,
    calendar: faCalendarCheck,
    handshake: faHandshake
  };

  // Dati Mock per le statistiche
  stats = signal([
    { label: 'Società Affiliate', value: '150+', icon: faHandshake },
    { label: 'Atleti Tesserati', value: '5.000+', icon: faUsers },
    { label: 'Eventi Annuali', value: '45', icon: faCalendarCheck },
    { label: 'Medaglie Internazionali', value: '12', icon: faTrophy },
  ]);


}