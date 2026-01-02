import { HttpClient } from "@angular/common/http";
import { inject, Injectable } from "@angular/core";
import { map, Observable } from "rxjs";
import { environment } from "../../../environments/environment";
import { ClubDTO, CreateClubDTO } from "../../models/dtos";
import { Club } from "../../models/club.model";
import { AffiliationStatus } from "../../enums/affiliation-status.enum";

@Injectable({ providedIn: 'root' })
export class ClubService {
  private http = inject(HttpClient);
  private apiUrl = `${environment.apiUrl}/club/`;
  
  createClub(createClub: CreateClubDTO): Observable<Club> { 
    return this.http.post<ClubDTO>(`${this.apiUrl}create`, createClub).pipe(
      map(data => new Club(data))
    ); 
  }

  updateClub(clubId:string, updateClub: Club): Observable<Club> { 
    const clubDTO :ClubDTO = updateClub.toDTO()
    return this.http.patch<ClubDTO>(`${this.apiUrl}update/${clubId}`, clubDTO).pipe(
      map(data => new Club(data))
    ); 
  }

  getClub(clubId: string): Observable<Club> { 
    return this.http.get<ClubDTO>(`${this.apiUrl}${clubId}`).pipe(
      map(data => new Club(data))
    ); 
  }

  updateAffiliationStatus(clubId: string, newStatus: AffiliationStatus): Observable<void> { 
    return this.http.post<void>(`${this.apiUrl}update-status/${clubId}/${newStatus}`, null); 
  }

  renewClubAffiliationRequest(clubId: string): Observable<void> { 
    return this.http.post<void>(`${this.apiUrl}renew-submission/${clubId}`, null); 
  }

  getClubsToApprove(): Observable<Club[]> { 
    return this.http.get<ClubDTO[]>(`${this.apiUrl}to-approve`).pipe(
      map(data => data.map(club => new Club(club)))
    );
  }
  
  getAllClubs(): Observable<Club[]> { 
    return this.http.get<ClubDTO[]>(`${this.apiUrl}all`).pipe(
      map(data => data.map(club => new Club(club)))
    );
  }
}