import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { map, Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import {
  AthleteDTO,
  ClubManagerDTO,
  CreateUserDTO,
  FederationManagerDTO,
  UserDTO,
} from '../../models/dtos';
import { User } from '../../models/user.model';
import { Role } from '../../enums/role.enum';
import { Athlete } from '../../models/athlete.model';
import { ClubManager } from '../../models/club-manager.model';
import { FederationManager } from '../../models/federation-manager.model';

@Injectable({ providedIn: 'root' })
export class UserService {
  protected http = inject(HttpClient);
  protected apiUrl = `${environment.apiUrl}/user/`;

  // Base user CRUD

  getUserByEmail(email: string): Observable<User> {
    return this.http
      .get<UserDTO>(`${this.apiUrl}email/${email}`)
      .pipe(map((data) => this.createUserInstance(data)));
  }

  getUserById(id: string): Observable<User> {
    return this.http
      .get<UserDTO>(`${this.apiUrl}${id}`)
      .pipe(map((data) => this.createUserInstance(data)));
  }

  createUser(user: CreateUserDTO): Observable<User> {
    return this.http
      .post<UserDTO>(`${this.apiUrl}create`, user)
      .pipe(map((data) => this.createUserInstance(data)));
  }

  updateUser(user: UserDTO): Observable<User> {
    return this.http
      .patch<UserDTO>(`${this.apiUrl}update/${user.id}`, user)
      .pipe(map((data) => this.createUserInstance(data)));
  }

  changeUserPassword(userId: string, oldPassword: string, newPassword: string): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}change-password/${userId}`, {
      oldPassword,
      newPassword,
    });
  }

  getClubManagers(): Observable<ClubManager[]> {
    return this.findByRole(Role.CLUB_MANAGER).pipe(
      map((users) => {
        return users.filter((u): u is ClubManager => u instanceof ClubManager);
      })
    );  
  }
  getFederationManagers(): Observable<FederationManager[]> {
     return this.findByRole(Role.FEDERATION_MANAGER).pipe(
      map((users) => {
        return users.filter((u): u is FederationManager => u instanceof FederationManager);
      })
    );  
  }

  /* UTILS */

  private findByRole(role: Role): Observable<User[]> {
    return this.http.get<UserDTO[]>(`${this.apiUrl}find-by-role/${role}`)
      .pipe(map((data) => data.map((d) => this.createUserInstance(d))));
  }

  // Method to adapt factory method to FE
  protected createUserInstance(dto: UserDTO): User {
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
