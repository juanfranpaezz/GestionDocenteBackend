import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Subject } from '../models/subject';
import { API_CONFIG } from '../config/api.config';

@Injectable({ providedIn: 'root' })
export class SubjectService {
  private http = inject(HttpClient);
  private readonly BASE_URL = API_CONFIG.BASE_URL;

  getSubjectsByCourse(courseId: number): Observable<Subject[]> {
    return this.http.get<Subject[]>(`${this.BASE_URL}${API_CONFIG.SUBJECTS.BY_COURSE}/${courseId}/subjects`);
  }

  getDefaultSubject(courseId: number): Observable<Subject> {
    return this.http.get<Subject>(`${this.BASE_URL}${API_CONFIG.SUBJECTS.BY_COURSE}/${courseId}/subjects/default`);
  }

  createSubject(courseId: number, subject: Omit<Subject, 'id'>): Observable<Subject> {
    return this.http.post<Subject>(`${this.BASE_URL}${API_CONFIG.SUBJECTS.BY_COURSE}/${courseId}/subjects`, subject);
  }

  updateSubject(id: number, subject: Partial<Subject>): Observable<Subject> {
    return this.http.put<Subject>(`${this.BASE_URL}${API_CONFIG.SUBJECTS.BASE}/${id}`, subject);
  }

  deleteSubject(id: number): Observable<void> {
    return this.http.delete<void>(`${this.BASE_URL}${API_CONFIG.SUBJECTS.BASE}/${id}`);
  }
}

