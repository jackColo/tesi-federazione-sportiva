import { CommonModule } from '@angular/common';
import { Component, input, InputSignal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import {
  faChevronRight
} from '@fortawesome/free-solid-svg-icons';
import { FederationManager } from '../../../../../models/federation-manager.model';

@Component({
  selector: 'app-dashboard-federation-manager-card-component',
  standalone: true,
  imports: [CommonModule, FontAwesomeModule, RouterLink],
  templateUrl: './dashboard-federation-manager-card-component.html',
})
export class DashboardFederationManagerCardComponent {
  federationManager: InputSignal<FederationManager> = input.required<FederationManager>();
  icons = { faChevronRight };

} 