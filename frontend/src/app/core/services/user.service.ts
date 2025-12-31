import { HttpClient } from "@angular/common/http";
import { inject, Injectable } from "@angular/core";
import { map, Observable } from "rxjs";
import { environment } from "../../../environments/environment";
import { AthleteDTO, ClubManagerDTO, CreateUserDTO, FederationManagerDTO, UserDTO } from "../../models/dtos";
import { User } from "../../models/user.model";
import { Role } from "../../enums/role.enum";
import { Athlete } from "../../models/athlete.model";
import { ClubManager } from "../../models/club-manager.model";
import { FederationManager } from "../../models/federation-manager.model";

@Injectable({ providedIn: 'root' })
export class UserService {
  private http = inject(HttpClient);
  private apiUrl = `${environment.apiUrl}/user/`;
  
  getUserByEmail(email: string): Observable<User> {
    return this.http.get<UserDTO>(`${this.apiUrl}email/${email}`).pipe(
      map(data => this.createUserInstance(data))
    ); 
  }

  getUserById(id: string): Observable<User> {
    return this.http.get<UserDTO>(`${this.apiUrl}${id}`).pipe(
      map(data => this.createUserInstance(data))
    ); 
  }

  createUser(user: CreateUserDTO): Observable<User> { 
    return this.http.post<UserDTO>(`${this.apiUrl}create`, user).pipe(
      map(data => this.createUserInstance(data))
    );
  }

  updateUser(user: UserDTO): Observable<User> { 
    return this.http.patch<UserDTO>(`${this.apiUrl}update/${user.id}`, user).pipe(
      map(data => this.createUserInstance(data))
    );
  }

  private createUserInstance(dto: UserDTO): User {
  switch (dto.role) {
    case Role.ATHLETE:
      return new Athlete(dto as AthleteDTO);
    case Role.CLUB_MANAGER:
      return new ClubManager(dto as ClubManagerDTO);
    case Role.FEDERATION_MANAGER:
      return new FederationManager(dto as FederationManagerDTO);

    default:
      return new User(dto);
  }
}
}