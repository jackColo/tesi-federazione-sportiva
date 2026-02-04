import { Injectable } from '@angular/core';
import { map, Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Athlete } from '../../models/athlete.model';
import { AthleteDTO, UserDTO } from '../../models/dtos';
import { UserService } from './user.service';
import { AffiliationStatus } from '../../enums/affiliation-status.enum';

@Injectable({ providedIn: 'root' })
export class AthleteService extends UserService {
  override apiUrl = `${environment.apiUrl}/athlete/`;

  updateAthleteAffiliationStatus(athleteId: string, status: AffiliationStatus): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}update-status/${athleteId}/${status}`, null);
  }

  renewAthleteAffiliationRequest(athleteId: string): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}renew-submission/${athleteId}`, null);
  }

  getClubAthletesToApprove(): Observable<Athlete[]> {
    return this.http.get<AthleteDTO[]>(`${this.apiUrl}to-approve`).pipe(
      map((data) => {
        const users = data.map((d) => this.createUserInstance(d));
        return users.filter((u): u is Athlete => u instanceof Athlete);
      })
    );
  }

  getAthletesByClubId(clubId: string): Observable<Athlete[]> {
    return this.http.get<UserDTO[]>(`${this.apiUrl}club/${clubId}`).pipe(
      map((data) => {
        const users = data.map((d) => this.createUserInstance(d));
        return users.filter((u): u is Athlete => u instanceof Athlete);
      })
    );
  }

  getAthletes(): Observable<Athlete[]> {
    return this.http.get<UserDTO[]>(`${this.apiUrl}all`).pipe(
      map((data) => {
        const users = data.map((d) => this.createUserInstance(d));
        return users.filter((u): u is Athlete => u instanceof Athlete);
      })
    );
  }
}
