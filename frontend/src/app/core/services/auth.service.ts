import { Injectable, inject, PLATFORM_ID, computed, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { isPlatformBrowser } from '@angular/common';
import { Router } from '@angular/router';
import { Observable, tap } from 'rxjs';
import { LoginRequest, LoginResponse, RegistroRequest, Rol, UserSession } from '../models/user.model';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private http = inject(HttpClient);
  private router = inject(Router);
  private platformId = inject(PLATFORM_ID);

  private _session = signal<UserSession | null>(this.loadSession());

  readonly session = this._session.asReadonly();
  readonly isAuthenticated = computed(() => this._session() !== null);
  readonly currentRole = computed(() => this._session()?.rol ?? null);
  readonly currentUser = computed(() => this._session());

  private loadSession(): UserSession | null {
    if (!isPlatformBrowser(this.platformId)) return null;
    const token = localStorage.getItem('token');
    const id = localStorage.getItem('userId');
    const nombre = localStorage.getItem('userName');
    const rol = localStorage.getItem('userRol') as Rol;
    if (token && id && nombre && rol) {
      return { token, id: Number(id), nombre, rol };
    }
    return null;
  }

  login(request: LoginRequest): Observable<LoginResponse> {
    return this.http.post<LoginResponse>('/api/auth/login', request).pipe(
      tap(response => this.saveSession(response))
    );
  }

  registro(request: RegistroRequest): Observable<unknown> {
    return this.http.post('/api/auth/registro', request);
  }

  private saveSession(response: LoginResponse): void {
    let userId = response.id;
    if (!userId && response.token) {
      userId = this.extractIdFromJwt(response.token);
    }
    const session: UserSession = {
      token: response.token,
      id: userId ?? 0,
      nombre: response.nombre,
      rol: response.rol,
    };
    if (isPlatformBrowser(this.platformId)) {
      localStorage.setItem('token', session.token);
      localStorage.setItem('userId', String(session.id));
      localStorage.setItem('userName', session.nombre);
      localStorage.setItem('userRol', session.rol);
    }
    this._session.set(session);
  }

  private extractIdFromJwt(token: string): number | undefined {
    try {
      const payload = JSON.parse(atob(token.split('.')[1].replace(/-/g, '+').replace(/_/g, '/')));
      return payload.id ?? payload.userId ?? undefined;
    } catch {
      return undefined;
    }
  }

  logout(): void {
    if (isPlatformBrowser(this.platformId)) {
      localStorage.clear();
    }
    this._session.set(null);
    this.router.navigate(['/login']);
  }

  getToken(): string | null {
    return this._session()?.token ?? null;
  }

  getUserId(): number {
    return this._session()?.id ?? 0;
  }

  hasRole(...roles: Rol[]): boolean {
    const rol = this.currentRole();
    return rol ? roles.includes(rol) : false;
  }
}
