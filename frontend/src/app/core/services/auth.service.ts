import { HttpClient } from '@angular/common/http';
import { computed, inject, Injectable, signal } from '@angular/core';
import { jwtDecode } from 'jwt-decode';
import { lastValueFrom, tap } from 'rxjs';
import { environment } from '../../../environments/environment';
import { JwtResponsePayload, JwtResponseDTO, LogUserDTO } from '../../models/dtos';
import { Router } from '@angular/router';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private http = inject(HttpClient);
  private router = inject(Router);
  private apiUrl = environment.apiUrl + '/auth/';

  private isLoggedInSignal = signal<boolean>(false);
  private userClaimsSignal = signal<JwtResponsePayload | null>(null);

  public isLoggedIn = this.isLoggedInSignal.asReadonly();
  public userClaims = this.userClaimsSignal.asReadonly();

  public userRole = computed<string | null>(() => {
    const claims = this.userClaims();
    return claims ? claims.role : null;
  });

  public currentUserEmail = computed<string | null>(() => {
    const claims = this.userClaims();
    return claims ? claims.sub : null;
  });

  public currentUserId = computed<string | null>(() => {
    const claims = this.userClaims();
    return claims ? claims.id : null;
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
      this.resetState();
      return;
    }

    const decodedClaims = this.decodeToken(token);

    if (!decodedClaims) {
      this.logout();
      return;
    }

    const isTokenExpired = decodedClaims.exp * 1000 < Date.now();

    if (isTokenExpired) {
      this.logout();
      return;
    }

    this.isLoggedInSignal.set(true);
    this.userClaimsSignal.set(decodedClaims);
  }

  public getToken(): string | null {
    const token = localStorage.getItem('jwt_token');

    if (!token) return null;

    if (this.isTokenExpired(token)) {
      console.warn('Token scaduto rilevato. Eseguo logout automatico.');
      this.logout();
      return null;
    }

    return token;
  }

  private isTokenExpired(token: string): boolean {
    const decoded = this.decodeToken(token);
    if (!decoded || !decoded.exp) return true;
    const expirationDate = decoded.exp * 1000;
    const now = Date.now();

    return now > expirationDate;
  }

  async login(credentials: LogUserDTO): Promise<JwtResponseDTO> {
    const observable = this.http.post<JwtResponseDTO>(this.apiUrl + 'login', credentials).pipe(
      tap((response: JwtResponseDTO) => {
        localStorage.setItem('jwt_token', response.token);
        this.handleToken(response.token);
      })
    );

    return await lastValueFrom(observable);
  }

  logout(): void {
    localStorage.removeItem('jwt_token');
    this.resetState();
    this.router.navigate(['']);
  }

  private resetState(): void {
    this.isLoggedInSignal.set(false);
    this.userClaimsSignal.set(null);
  }

  private decodeToken(token: string): any | null {
    try {
      return jwtDecode(token);
    } catch (e) {
      return null;
    }
  }
}
