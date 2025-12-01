import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Evaluation } from '../models/evaluation';
import { Observable } from 'rxjs';
import { API_CONFIG } from '../config/api.config';

@Injectable({ providedIn: 'root' })
export class EvaluationService {

  private http = inject(HttpClient);
  private readonly BASE_URL = API_CONFIG.BASE_URL;
  private readonly apiUrl = `${this.BASE_URL}${API_CONFIG.EVALUATIONS.BASE}`;

  getEvaluationsByCourse(courseId: number): Observable<Evaluation[]> {
    return this.http.get<Evaluation[]>(`${this.BASE_URL}${API_CONFIG.EVALUATIONS.BY_COURSE}/${courseId}`);
  }

  addEvaluation(evaluation: Omit<Evaluation, 'id'>): Observable<Evaluation> {
    return this.http.post<Evaluation>(this.apiUrl, evaluation);
  }

  deleteEvaluation(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  sendGradesByEmail(evaluationId: number): Observable<{ message: string }> {
    return this.http.post<{ message: string }>(`${this.apiUrl}/${evaluationId}/send-grades`, {});
  }

  updateEvaluation(id: number, evaluation: Partial<Evaluation>): Observable<Evaluation> {
    return this.http.put<Evaluation>(`${this.apiUrl}/${id}`, evaluation);
  }

  sendGradesByEmailCustom(evaluationId: number, templateId?: number, customMessage?: string, useTemplate: boolean = true): Observable<{ message: string; emailsSent?: number; emailsFailed?: number }> {
    return this.http.post<{ message: string; emailsSent?: number; emailsFailed?: number }>(
      `${this.apiUrl}/${evaluationId}/send-grades-custom`,
      { templateId, customMessage, useTemplate }
    );
  }

  updateEvaluationGradeScale(evaluationId: number, gradeScaleId: number | null): Observable<Evaluation> {
    return this.http.put<Evaluation>(`${this.apiUrl}/${evaluationId}/grade-scale`, { gradeScaleId });
  }
}
