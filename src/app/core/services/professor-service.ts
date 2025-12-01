import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Professor } from '../models/professor';
import { API_CONFIG } from '../config/api.config';

@Injectable({ providedIn: 'root' })
export class ProfessorService {
  private http = inject(HttpClient);
  private readonly BASE_URL = API_CONFIG.BASE_URL;

  getProfessors(): Observable<Professor[]> {
    return this.http.get<Professor[]>(`${this.BASE_URL}${API_CONFIG.PROFESSORS.BASE}`);
  }

  getProfessorById(id: number): Observable<Professor> {
    return this.http.get<Professor>(`${this.BASE_URL}${API_CONFIG.PROFESSORS.BASE}/${id}`);
  }

  searchProfessorsByLastname(lastname: string): Observable<Professor[]> {
    return this.http.get<Professor[]>(`${this.BASE_URL}${API_CONFIG.PROFESSORS.BASE}/search`, {
      params: { lastname }
    });
  }

  searchProfessors(query: string): Observable<Professor[]> {
    return this.http.get<Professor[]>(`${this.BASE_URL}${API_CONFIG.PROFESSORS.BASE}/search`, {
      params: { query }
    });
  }

  updateProfessor(id: number, professor: Partial<Professor>): Observable<Professor> {
    return this.http.put<Professor>(`${this.BASE_URL}${API_CONFIG.PROFESSORS.BASE}/${id}`, professor);
  }

  deleteProfessor(id: number): Observable<void> {
    return this.http.delete<void>(`${this.BASE_URL}${API_CONFIG.PROFESSORS.BASE}/${id}`);
  }
}

