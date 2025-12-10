import { Injectable, inject, signal, computed } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { lastValueFrom, Observable, tap } from 'rxjs';
import { jwtDecode } from 'jwt-decode';
import { JwtResponseDTO, LogUserDTO } from '../../models/dtos';
import { environment } from '../../../environments/environment';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private http = inject(HttpClient);
  private apiUrl = environment.apiUrl + '/auth/';

  private isLoggedInSignal = signal<boolean>(false);
  private userClaimsSignal = signal<any>(null);

  public isLoggedIn = this.isLoggedInSignal.asReadonly();
  public userClaims = this.userClaimsSignal.asReadonly();

  public userRole = computed<string | null>(() => {
    const claims = this.userClaims();

    return claims?.roles?.[0]?.authority;
  });

  constructor() {
    this.initializeState();
  }

  private initializeState(): void {
    const token = this.getToken();
    this.handleToken(token);
  }

  private handleToken(token: string | null): void {
    if (!token) {
      this.isLoggedInSignal.set(false);
      this.userClaimsSignal.set(null);
      return;
    }

    const decodedClaims = this.decodeToken(token);
    const isTokenExpired = decodedClaims.exp * 1000 < Date.now();

    if (isTokenExpired) {
      this.logout();
      return;
    }

    this.isLoggedInSignal.set(true);
    this.userClaimsSignal.set(decodedClaims);
  }

  public getToken(): string | null {
    return localStorage.getItem('jwt_token');
  }

  private decodeToken(token: string): any | null {
    try {
      return jwtDecode(token);
    } catch (e) {
      return null;
    }
  }

  async login(credentials: LogUserDTO): Promise<JwtResponseDTO> { 
    const observable = this.http.post<JwtResponseDTO>(this.apiUrl + 'login', credentials).pipe(
      tap((response) => {
        localStorage.setItem('jwt_token', response.token);
        this.handleToken(response.token);
        
      })
    );

    return await lastValueFrom(observable); 
  }

  logout(): void {
    localStorage.removeItem('jwt_token');
    this.handleToken(null);
  }

}
