import { HttpClient } from "@angular/common/http";
import { inject, Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { environment } from "../../../environments/environment";
import { ClubDTO, CreateClubDTO } from "../../models/dtos";

@Injectable({ providedIn: 'root' })
export class ClubService {
  private http = inject(HttpClient);
  private apiUrl = `${environment.apiUrl}/club/`; // Assicurati dello slash finale
  
  createClub(createClub: CreateClubDTO): Observable<ClubDTO> { 
    return this.http.post<ClubDTO>(`${this.apiUrl}create`, createClub); 
  }

  getClub(clubId: string): Observable<ClubDTO> { 
    return this.http.get<ClubDTO>(`${this.apiUrl}${clubId}`); 
  }

  approveClub(clubId: string): Observable<void> { 
    return this.http.post<void>(`${this.apiUrl}approve/${clubId}`, null); 
  }

  getClubsToApprove(): Observable<ClubDTO[]> { 
    return this.http.get<ClubDTO[]>(`${this.apiUrl}to-approve`); 
  }
}