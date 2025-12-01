import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { API_CONFIG } from '../config/api.config';

export interface GradeScaleOption {
  id?: number;
  label: string;
  numericValue?: number | null;
  order: number;
}

export interface GradeScale {
  id?: number;
  name: string;
  professorId?: number;
  isGlobal: boolean;
  options: GradeScaleOption[];
}

@Injectable({ providedIn: 'root' })
export class GradeScaleService {
  private http = inject(HttpClient);
  private readonly BASE_URL = API_CONFIG.BASE_URL;
  private readonly apiUrl = `${this.BASE_URL}${API_CONFIG.GRADE_SCALES.BASE}`;

  getGradeScales(includeGlobal: boolean = false): Observable<GradeScale[]> {
    let params = new HttpParams();
    if (includeGlobal) {
      params = params.set('global', 'true');
    }
    return this.http.get<GradeScale[]>(this.apiUrl, { params });
  }

  createGradeScale(scale: Omit<GradeScale, 'id'>): Observable<GradeScale> {
    return this.http.post<GradeScale>(this.apiUrl, scale);
  }

  updateGradeScale(id: number, scale: Partial<GradeScale>): Observable<GradeScale> {
    return this.http.put<GradeScale>(`${this.apiUrl}/${id}`, scale);
  }

  deleteGradeScale(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  getAvailableGradeScalesForEvaluation(evaluationId: number): Observable<GradeScale[]> {
    return this.http.get<GradeScale[]>(`${this.apiUrl}/evaluations/${evaluationId}/available`);
  }
}

