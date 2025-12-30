import { HttpClient } from "@angular/common/http";
import { inject, Injectable } from "@angular/core";
import { map, Observable } from "rxjs";
import { environment } from "../../../environments/environment";
import { UserDTO } from "../../models/dtos";
import { User } from "../../models/user.model";

@Injectable({ providedIn: 'root' })
export class UserService {
  private http = inject(HttpClient);
  private apiUrl = `${environment.apiUrl}/user/`;
  
  getUserByEmail(email: string): Observable<User> {
    return this.http.get<UserDTO>(`${this.apiUrl}email/${email}`).pipe(
      map(data => new User(data))
    ); 
  }

  getUserById(id: string): Observable<User> {
    return this.http.get<UserDTO>(`${this.apiUrl}${id}`).pipe(
      map(data => new User(data))
    ); 
  }

  createUser(user: Partial<UserDTO>): Observable<User> { 
    return this.http.post<UserDTO>(`${this.apiUrl}create`, user).pipe(
      map(data => new User(data))
    );
  }

  updateUser(id: string, user: Partial<UserDTO>): Observable<User> { 
    return this.http.put<UserDTO>(`${this.apiUrl}update/${id}`, user).pipe(
      map(data => new User(data))
    );
  }

}