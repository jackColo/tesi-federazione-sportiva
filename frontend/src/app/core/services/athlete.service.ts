import { HttpClient } from "@angular/common/http";
import { inject, Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { environment } from "../../../environments/environment";
import { AthleteDTO, CreateAthleteDTO } from "../../models/dtos";

@Injectable({ providedIn: 'root' })
export class AthleteService {
  private http = inject(HttpClient);
  private apiUrl = `${environment.apiUrl}/athlete/`;
  
  createAthlete(createAthlete: CreateAthleteDTO): Observable<AthleteDTO> { 
    return this.http.post<AthleteDTO>(`${this.apiUrl}create`, createAthlete); 
  }

  approveAthlete(athleteId: string): Observable<void> { 
    return this.http.post<void>(`${this.apiUrl}approve/${athleteId}`, null); 
  }

  getClubAthletesToApprove(): Observable<AthleteDTO[]> { 
    return this.http.get<AthleteDTO[]>(`${this.apiUrl}to-approve`); 
  }
}