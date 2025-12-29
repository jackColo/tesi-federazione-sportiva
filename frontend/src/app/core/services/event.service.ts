import { HttpClient } from "@angular/common/http";
import { inject, Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { environment } from "../../../environments/environment";
import { CreateEnrollmentDTO, CreateEventDTO, EnrollmentDTO, EventDTO } from "../../models/dtos";

@Injectable({ providedIn: 'root' })
export class EventService {
  private http = inject(HttpClient);
  private apiUrl = `${environment.apiUrl}/event/`;
  
  createEvent(createEvent: CreateEventDTO): Observable<EventDTO> { 
    return this.http.post<EventDTO>(`${this.apiUrl}create`, createEvent); 
  }

  getAllEvents(): Observable<EventDTO[]> {
    return this.http.get<EventDTO[]>(`${this.apiUrl}all`); 
  }

  enrollAthlete(createEnrollment: CreateEnrollmentDTO): Observable<EnrollmentDTO> { 
    return this.http.post<EnrollmentDTO>(`${this.apiUrl}enroll`, createEnrollment); 
  }
}