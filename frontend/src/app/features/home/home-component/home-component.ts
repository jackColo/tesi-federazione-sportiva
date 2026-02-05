import { CommonModule } from '@angular/common';
import { Component, inject } from '@angular/core';
import { RouterLink } from '@angular/router';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import {
  faArrowRight
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
  
  icons = {
    faArrowRight  
  };

}