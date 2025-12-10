import { HttpClient } from "@angular/common/http";
import { inject, Injectable } from "@angular/core";
import { environment } from "../../../environments/environment";
import { lastValueFrom } from "rxjs";
import { ClubDTO, CreateClubDTO } from "../../models/dtos";

@Injectable({ providedIn: 'root' })
export class ClubService {
  private http = inject(HttpClient);
  private apiUrl = environment.apiUrl + '/club/';

  
  async createClub(createClub: CreateClubDTO): Promise<ClubDTO> { 
    const observable = this.http.post<ClubDTO>(this.apiUrl + 'create', createClub);

    return await lastValueFrom(observable); 
  }
}