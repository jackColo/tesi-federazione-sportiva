import { CommonModule } from '@angular/common';
import { Component, computed, inject, Signal } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { Router } from '@angular/router';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { faPlus } from '@fortawesome/free-solid-svg-icons';
import { AuthService } from '../../../../core/services/auth.service';
import { EventService } from '../../../../core/services/event.service';
import { Role } from '../../../../enums/role.enum';
import { Event } from '../../../../models/event.model';
import { EventCardComponent } from '../../../../shared/components/event-card-component/event-card-component';

@Component({
  selector: 'app-dashboard-events',
  standalone: true,
  imports: [CommonModule, FontAwesomeModule, EventCardComponent],
  templateUrl: './dashboard-events-component.html',
})
export class DashboardEventsComponent {
  private authService = inject(AuthService);
  private eventService = inject(EventService);
  private router = inject(Router);
  
  icons = {
    faPlus
  }

  isFederation = computed(() => this.authService.userRole() === Role.FEDERATION_MANAGER);
  
  events: Signal<Event[]> = toSignal(
    this.eventService.getAllEvents(),
    { initialValue: [] }
  );

  navigateToCreate() {
    this.router.navigate(['dashboard', 'events', 'new']);
  }

}
