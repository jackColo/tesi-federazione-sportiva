import { CommonModule } from '@angular/common';
import { Component, computed, inject, input, InputSignal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { faCalendarAlt, faMapMarkerAlt } from '@fortawesome/free-solid-svg-icons';
import { AuthService } from '../../../core/services/auth.service';
import {
  EventStatus,
  eventStatusColorClass,
  readableEventStatus,
} from '../../../enums/event-status.enum';
import { Role } from '../../../enums/role.enum';
import { Event } from '../../../models/event.model';

@Component({
  selector: 'app-event-card-component',
  standalone: true,
  imports: [CommonModule, FontAwesomeModule, RouterLink],
  templateUrl: './event-card-component.html',
})
export class EventCardComponent {
  private authService = inject(AuthService);
  event: InputSignal<Event> = input.required<Event>();
  isDashboard: InputSignal<boolean> = input.required<boolean>();

  canSubscribe = computed(() => {
    const eventStatus = this.event().status;
    const userRole = this.authService.userRole();

    return (
      eventStatus === EventStatus.REGISTRATION_OPEN &&
      (userRole === Role.ATHLETE || userRole === Role.CLUB_MANAGER)
    );
  });

  isFederation = computed(() => this.authService.userRole() === Role.FEDERATION_MANAGER);

  icons = {
    faCalendarAlt,
    faMapMarkerAlt,
  };

  getStatusLabel(status: EventStatus): string {
    return readableEventStatus(status);
  }

  getStatusClass(status: EventStatus): string {
    return eventStatusColorClass(status);
  }
}
