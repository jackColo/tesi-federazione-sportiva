import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { map, Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { CreateEnrollmentDTO, CreateEventDTO, EnrollmentDTO, EventDTO } from '../../models/dtos';
import { Event } from '../../models/event.model';
import { Enrollment } from '../../models/enrollment.model';
import { EventStatus } from '../../enums/event-status.enum';
import { EnrollmentStatus } from '../../enums/enrollment-status.enum';

@Injectable({ providedIn: 'root' })
export class EventService {
  private http = inject(HttpClient);
  private apiUrl = `${environment.apiUrl}/event/`;

  getAllEvents(): Observable<Event[]> {
    return this.http
      .get<EventDTO[]>(`${this.apiUrl}all`)
      .pipe(map((data) => data.map((d) => new Event(d))));
  }

  getEventById(eventId: string): Observable<Event> {
    return this.http.get<EventDTO>(`${this.apiUrl}${eventId}`).pipe(map((data) => new Event(data)));
  }

  updateEventState(eventId: string, newState: EventStatus): Observable<void> {
    const headers = new HttpHeaders({
      'Content-Type': 'application/json'
    });
    const body = JSON.stringify(newState);
    return this.http.patch<void>(`${this.apiUrl}update-state/${eventId}`, body, { headers });
  }

  createEvent(createEvent: CreateEventDTO): Observable<Event> {
    return this.http
      .post<EventDTO>(`${this.apiUrl}create`, createEvent)
      .pipe(map((data) => new Event(data)));
  }

  updateEvent(event: Event): Observable<Event> {
    const eventDTO: EventDTO = event.toDTO();
    return this.http
      .patch<EventDTO>(`${this.apiUrl}update`, eventDTO)
      .pipe(map((data) => new Event(data)));
  }

  // Enrollment methods

  enrollAthlete(createEnrollment: CreateEnrollmentDTO): Observable<Enrollment> {
    return this.http
      .post<EnrollmentDTO>(`${this.apiUrl}enroll`, createEnrollment)
      .pipe(map((data) => new Enrollment(data)));
  }

  getEnrollmentById(id: string): Observable<Enrollment> {
    return this.http
      .get<EnrollmentDTO>(`${this.apiUrl}enroll/${id}`)
      .pipe(map((data) => new Enrollment(data)));
  }

  getEventEnrollments(
    eventId: string,
    clubId?: string,
    athleteId?: string,
  ): Observable<Enrollment[]> {
    let queryParams = new HttpParams();
    if (clubId) {
      queryParams = queryParams.set('clubId', clubId);
    }

    if (athleteId) {
      queryParams = queryParams.set('athleteId', athleteId);
    }

    return this.http
      .get<EnrollmentDTO[]>(`${this.apiUrl}enroll-all/${eventId}`, { params: queryParams })
      .pipe(map((data) => data.map((d) => new Enrollment(d))));
  }

  updateEnrollment(enrollment: Enrollment): Observable<Enrollment> {
    const enrollmentDTO: EnrollmentDTO = enrollment.toDTO();
    return this.http
      .patch<EnrollmentDTO>(`${this.apiUrl}enroll/update`, enrollmentDTO)
      .pipe(map((data) => new Enrollment(data)));
  }

  updateEnrollmentStatus(enrollId: String, newStatus: EnrollmentStatus): Observable<Enrollment> {
    const headers = new HttpHeaders({
      'Content-Type': 'application/json'
    });
    const body = JSON.stringify(newStatus);
    return this.http
      .patch<EnrollmentDTO>(`${this.apiUrl}enroll/update-status/${enrollId}`, body, { headers })
      .pipe(map((data) => new Enrollment(data)));
  }
}
