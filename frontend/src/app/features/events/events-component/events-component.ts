import { CommonModule } from '@angular/common';
import { Component, inject, Signal } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { FormsModule } from '@angular/forms';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import {
  faArrowRight,
  faCalendarAlt,
  faFilter,
  faMapMarkerAlt,
  faSearch,
  faTrophy,
  IconDefinition
} from '@fortawesome/free-solid-svg-icons';
import { from } from 'rxjs';

import { EventService } from '../../../core/services/event.service';
import { EventStatus, eventStatusColorClass, readableEventStatus } from '../../../enums/event-status.enum';
import { EventDTO } from '../../../models/dtos';

@Component({
  selector: 'app-events-component',
  standalone: true,
  imports: [CommonModule, FormsModule, FontAwesomeModule],
  templateUrl: './events-component.html',
})
export class EventsComponent {
  private eventService = inject(EventService);
  protected readonly EventStatus = EventStatus;
  
  icons: { [key: string]: IconDefinition } = {
    search: faSearch,
    calendar: faCalendarAlt,
    map: faMapMarkerAlt,
    trophy: faTrophy,
    filter: faFilter,
    arrow: faArrowRight
  };

  allEvents: Signal<EventDTO[]> = toSignal(
    from(this.eventService.getAllEvents()),
    { initialValue: [] }
  );

  getStatusLabel(status: EventStatus): string {
    return readableEventStatus(status);
  }

  getStatusClass(status: EventStatus): string {
    return eventStatusColorClass(status);
  }
}