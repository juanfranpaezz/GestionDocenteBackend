import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Professor } from '../models/professor';
import { API_CONFIG } from '../config/api.config';

@Injectable({ providedIn: 'root' })
export class SearchService {
  private http = inject(HttpClient);
  private readonly BASE_URL = API_CONFIG.BASE_URL;

  searchProfessorsByLastname(lastname: string): Observable<Professor[]> {
    return this.http.get<Professor[]>(`${this.BASE_URL}${API_CONFIG.PROFESSORS.BASE}/search`, {
      params: { lastname }
    });
  }
}

