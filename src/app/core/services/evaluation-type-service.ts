import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { EvaluationType } from '../models/evaluation-type';
import { Observable } from 'rxjs';
import { API_CONFIG } from '../config/api.config';

@Injectable({ providedIn: 'root' })
export class EvaluationTypeService {
  private http = inject(HttpClient);
  private readonly BASE_URL = API_CONFIG.BASE_URL;
  private readonly apiUrl = `${this.BASE_URL}${API_CONFIG.EVALUATION_TYPES.BASE}`;

  getEvaluationTypesByCourse(courseId: number): Observable<EvaluationType[]> {
    return this.http.get<EvaluationType[]>(`${this.BASE_URL}${API_CONFIG.EVALUATION_TYPES.BY_COURSE}/${courseId}`);
  }

  createEvaluationType(evaluationType: Omit<EvaluationType, 'id'>): Observable<EvaluationType> {
    return this.http.post<EvaluationType>(this.apiUrl, evaluationType);
  }

  updateEvaluationType(id: number, evaluationType: EvaluationType): Observable<EvaluationType> {
    return this.http.put<EvaluationType>(`${this.apiUrl}/${id}`, evaluationType);
  }

  deleteEvaluationType(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}

