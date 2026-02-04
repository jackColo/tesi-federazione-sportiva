import { CommonModule } from '@angular/common';
import { Component, inject } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { UserService } from '../../../../core/services/user.service';
import { FederationManager } from '../../../../models/federation-manager.model';
import { DashboardFederationManagerCardComponent } from './dashboard-federation-manager-card-component/dashboard-federation-manager-card-component';

@Component({
  selector: 'app-dashboard-federation-managers-component',
  standalone: true,
  imports: [CommonModule, FontAwesomeModule, DashboardFederationManagerCardComponent],
  template: `
   <div class="py-2 text-center bg-white rounded shadow-sm">
    <h2 class="text-2xl font-semibold m-4">Amministratori della Federazione</h2>
      @if (federationManagers(); as federationManagers) { @for (fedManager of federationManagers; track fedManager.id) {
      <div class="m-8">
        <app-dashboard-federation-manager-card-component
          [federationManager]="fedManager"
        ></app-dashboard-federation-manager-card-component>
      </div>
        } }
    </div>
  `,
})
export class DashboardFederationManagersComponent {
    private userService = inject(UserService);
  
    federationManagers = toSignal<FederationManager[] | null>(
     this.userService.getFederationManagers(),
    { initialValue: null }
  );


}