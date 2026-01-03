import { CommonModule } from '@angular/common';
import { Component, inject, Signal } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { FormsModule } from '@angular/forms';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import {
  faArrowRight,
  faFilter,
  faSearch,
  faTrophy,
  IconDefinition
} from '@fortawesome/free-solid-svg-icons';
import { from } from 'rxjs';

import { EventService } from '../../../core/services/event.service';
import { EventStatus } from '../../../enums/event-status.enum';
import { Event } from '../../../models/event.model';
import { EventCardComponent } from '../../../shared/components/event-card-component/event-card-component';

@Component({
  selector: 'app-events-component',
  standalone: true,
  imports: [CommonModule, FormsModule, FontAwesomeModule, EventCardComponent],
  templateUrl: './events-component.html',
})
export class EventsComponent {
  private eventService = inject(EventService);
  protected readonly EventStatus = EventStatus;
  
  icons: { [key: string]: IconDefinition } = {
    search: faSearch,
    trophy: faTrophy,
    filter: faFilter,
    arrow: faArrowRight
  };

  allEvents: Signal<Event[]> = toSignal(
    from(this.eventService.getAllEvents()),
    { initialValue: [] }
  );
}