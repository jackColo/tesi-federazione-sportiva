import { HttpClient } from "@angular/common/http";
import { inject, Injectable } from "@angular/core";
import { map, Observable } from "rxjs";
import { environment } from "../../../environments/environment";
import { CreateEnrollmentDTO, CreateEventDTO, EnrollmentDTO, EventDTO } from "../../models/dtos";
import { Event } from "../../models/event.model";

@Injectable({ providedIn: 'root' })
export class EventService {
  private http = inject(HttpClient);
  private apiUrl = `${environment.apiUrl}/event/`;
  
  createEvent(createEvent: CreateEventDTO): Observable<Event> { 
    return this.http.post<EventDTO>(`${this.apiUrl}create`, createEvent).pipe(
      map(data => new Event(data))
    ); 
  }

  getAllEvents(): Observable<Event[]> {
    return this.http.get<EventDTO[]>(`${this.apiUrl}all`).pipe(
      map(data => data.map(d => new Event(d)))
    );
  }

  enrollAthlete(createEnrollment: CreateEnrollmentDTO): Observable<EnrollmentDTO> { 
    return this.http.post<EnrollmentDTO>(`${this.apiUrl}enroll`, createEnrollment); 
  }
}