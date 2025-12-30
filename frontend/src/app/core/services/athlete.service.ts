import { HttpClient } from "@angular/common/http";
import { inject, Injectable } from "@angular/core";
import { map, Observable } from "rxjs";
import { environment } from "../../../environments/environment";
import { AthleteDTO, CreateAthleteDTO } from "../../models/dtos";
import { Athlete } from "../../models/athlete.model";

@Injectable({ providedIn: 'root' })
export class AthleteService {
  private http = inject(HttpClient);
  private apiUrl = `${environment.apiUrl}/athlete/`;
  
  createAthlete(createAthlete: CreateAthleteDTO): Observable<Athlete> { 
    return this.http.post<AthleteDTO>(`${this.apiUrl}create`, createAthlete).pipe(
      map(data => new Athlete(data))
    );
  }

  approveAthlete(athleteId: string): Observable<void> { 
    return this.http.post<void>(`${this.apiUrl}approve/${athleteId}`, null); 
  }

  getClubAthletesToApprove(): Observable<Athlete[]> { 
    return this.http.get<AthleteDTO[]>(`${this.apiUrl}to-approve`).pipe(
      map(data => data.map(item => new Athlete(item)))
    );
  }
}