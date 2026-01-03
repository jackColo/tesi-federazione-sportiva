import { CommonModule } from "@angular/common";
import { Component, computed, inject, input, InputSignal } from "@angular/core";
import { FontAwesomeModule } from "@fortawesome/angular-fontawesome";
import { Event } from "../../../models/event.model";
import { EventStatus, eventStatusColorClass, readableEventStatus } from "../../../enums/event-status.enum";
import { faCalendarAlt, faMapMarkerAlt, IconDefinition } from "@fortawesome/free-solid-svg-icons";
import { Role } from "../../../enums/role.enum";
import { AuthService } from "../../../core/services/auth.service";
import { RouterLink } from "@angular/router";

@Component({
  selector: 'app-event-card-component',
  standalone: true,
  imports: [CommonModule, FontAwesomeModule, RouterLink],
  templateUrl: './event-card-component.html',
})
export class EventCardComponent {
  private authService = inject(AuthService)
  event: InputSignal<Event> = input.required<Event>()
  isDashboard: InputSignal<boolean> = input.required<boolean>()

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