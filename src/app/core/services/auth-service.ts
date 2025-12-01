import { inject, Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Professor } from '../models/professor';
import { LoginResponse } from '../models/login-response';
import { map, tap, Observable, catchError, of } from 'rxjs';
import { API_CONFIG } from '../config/api.config';

@Injectable({ providedIn: 'root' })
export class AuthService {

  private http = inject(HttpClient);
  private readonly BASE_URL = API_CONFIG.BASE_URL;
  private readonly TOKEN_KEY = 'jwt_token';
  private readonly SESSION_KEY = 'loggedProfessor';

  // === STATE SIGNAL ===
  currentProfessor = signal<Professor | null>(null);

  constructor() {
    const saved = this.getLoggedProfessor();
    if (saved) {
      this.currentProfessor.set(saved);
    }
  }

  // --- REGISTER ---
  register(professor: {
    name: string;
    lastname: string;
    email: string;
    password: string;
    cel?: string;
    photoUrl?: string;
  }): Observable<Professor> {
    return this.http.post<Professor>(`${this.BASE_URL}${API_CONFIG.AUTH.REGISTER}`, professor).pipe(
      tap((createdProfessor) => {
        // Guardar profesor registrado
        this.currentProfessor.set(createdProfessor);
        localStorage.setItem(this.SESSION_KEY, JSON.stringify(createdProfessor));
      })
    );
  }

  // --- CHECK EMAIL ---
  emailExists(email: string): Observable<boolean> {
    return this.http.get<boolean>(`${this.BASE_URL}${API_CONFIG.PROFESSORS.EMAIL_EXISTS}`, {
      params: { email }
    }).pipe(
      catchError(() => of(false))
    );
  }

  // --- LOGIN ---
  login(email: string, password: string): Observable<boolean> {
    return this.http.post<LoginResponse>(`${this.BASE_URL}${API_CONFIG.AUTH.LOGIN}`, {
      email,
      password
    }).pipe(
      map((response: LoginResponse) => {
        // Guardar token JWT
        localStorage.setItem(this.TOKEN_KEY, response.token);
        
        // Guardar profesor y actualizar signal
        const professor = response.professor;
        localStorage.setItem(this.SESSION_KEY, JSON.stringify(professor));
        this.currentProfessor.set(professor);
        
        return true;
      }),
      catchError(() => {
        return of(false);
      })
    );
  }

  // --- LOGOUT ---
  logout(): void {
    // Eliminar token y datos de sesión
    localStorage.removeItem(this.TOKEN_KEY);
    localStorage.removeItem(this.SESSION_KEY);
    this.currentProfessor.set(null);
    
    // Opcional: llamar al endpoint de logout del backend
    this.http.post(`${this.BASE_URL}${API_CONFIG.AUTH.LOGOUT}`, {}).subscribe({
      error: () => {
        // Ignorar errores en logout (el token ya fue eliminado localmente)
      }
    });
  }

  // --- GET TOKEN ---
  getToken(): string | null {
    return localStorage.getItem(this.TOKEN_KEY);
  }

  // --- GET LOGGED USER ---
  getLoggedProfessor(): Professor | null {
    const saved = localStorage.getItem(this.SESSION_KEY);
    if (saved && saved !== 'null') {
      try {
        return JSON.parse(saved);
      } catch {
        return null;
      }
    }
    return null;
  }

  // --- IS AUTHENTICATED ---
  isAuthenticated(): boolean {
    return !!this.getToken() && !!this.getLoggedProfessor();
  }

  // --- UPDATE PHOTO ---
  updatePhoto(userId: number, base64: string): Observable<Professor> {
    return this.http.put<Professor>(`${this.BASE_URL}${API_CONFIG.PROFESSORS.BASE}/${userId}`, {
      photoUrl: base64
    }).pipe(
      tap((updated: Professor) => {
        // Actualizar en localStorage y signal
        localStorage.setItem(this.SESSION_KEY, JSON.stringify(updated));
        this.currentProfessor.set(updated);
      })
    );
  }

  // --- UPDATE PROFESSOR ---
  updateProfessor(id: number, data: Partial<Professor>): Observable<Professor> {
    return this.http.put<Professor>(`${this.BASE_URL}${API_CONFIG.PROFESSORS.BASE}/${id}`, data).pipe(
      tap((updated: Professor) => {
        // Actualizar en localStorage y signal
        localStorage.setItem(this.SESSION_KEY, JSON.stringify(updated));
        this.currentProfessor.set(updated);
      })
    );
  }

  // --- GET CURRENT PROFESSOR FROM API ---
  getCurrentProfessor(): Observable<Professor> {
    return this.http.get<Professor>(`${this.BASE_URL}${API_CONFIG.AUTH.ME}`).pipe(
      tap((professor) => {
        // Actualizar en localStorage y signal
        localStorage.setItem(this.SESSION_KEY, JSON.stringify(professor));
        this.currentProfessor.set(professor);
      })
    );
  }

  // --- VERIFY EMAIL ---
  verifyEmail(token: string): Observable<{ message: string; verified: boolean }> {
    return this.http.post<{ message: string; verified: boolean }>(
      `${this.BASE_URL}${API_CONFIG.AUTH.VERIFY_EMAIL}`,
      { token }
    );
  }
}
