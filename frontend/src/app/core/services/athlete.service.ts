import { HttpClient } from "@angular/common/http";
import { inject, Injectable } from "@angular/core";
import { lastValueFrom } from "rxjs";
import { environment } from "../../../environments/environment";
import { AthleteDTO, CreateAthleteDTO } from "../../models/dtos";

@Injectable({ providedIn: 'root' })
export class AthleteService {
  private http = inject(HttpClient);
  private apiUrl = environment.apiUrl + '/athlete/';
  
  async createAthlete(createAthlete: CreateAthleteDTO): Promise<AthleteDTO> { 
    const observable = this.http.post<AthleteDTO>(this.apiUrl + 'create', createAthlete);

    return await lastValueFrom(observable); 
  }

  async approveAthlete(athleteId: String): Promise<void> { 
    const observable = this.http.post<void>(this.apiUrl + 'approve/' + athleteId, null);

    return await lastValueFrom(observable); 
  }

  async getClubAthletesToApprove(): Promise<AthleteDTO[]> { 
    const observable = this.http.get<AthleteDTO[]>(this.apiUrl + 'to-approve');

    return await lastValueFrom(observable); 
  }
}