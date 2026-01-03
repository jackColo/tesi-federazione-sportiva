import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { map, Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { CreateEnrollmentDTO, CreateEventDTO, EnrollmentDTO, EventDTO } from '../../models/dtos';
import { Event } from '../../models/event.model';
import { Enrollment } from '../../models/enrollment.model';
import { EventStatus } from '../../enums/event-status.enum';

@Injectable({ providedIn: 'root' })
export class EventService {
  private http = inject(HttpClient);
  private apiUrl = `${environment.apiUrl}/event/`;


  getEventById(eventId :string): Observable<Event> {
    return this.http
      .get<EventDTO>(`${this.apiUrl}${eventId}`)
      .pipe(map((data) => new Event(data)));
  }

  createEvent(createEvent: CreateEventDTO): Observable<Event> {
    return this.http
      .post<EventDTO>(`${this.apiUrl}create`, createEvent)
      .pipe(map((data) => new Event(data)));
  }

  updateEvent(event: Event): Observable<Event> {
    const eventDTO :EventDTO = event.toDTO();
    return this.http
      .patch<EventDTO>(`${this.apiUrl}update`, eventDTO)
      .pipe(map((data) => new Event(data)));
  }

  updateEventState(eventId: string, newState: EventStatus): Observable<void> {
    return this.http
      .get<void>(`${this.apiUrl}update-state/${eventId}/${newState}`);
  }

  getAllEvents(): Observable<Event[]> {
    return this.http
      .get<EventDTO[]>(`${this.apiUrl}all`)
      .pipe(map((data) => data.map((d) => new Event(d))));
  }

  enrollAthlete(createEnrollment: CreateEnrollmentDTO): Observable<Enrollment> {
    return this.http
      .post<EnrollmentDTO>(`${this.apiUrl}enroll`, createEnrollment)
      .pipe(map((data) => new Enrollment(data)));
  }
}
