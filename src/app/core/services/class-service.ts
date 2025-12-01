import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Class } from '../models/class';
import { API_CONFIG } from '../config/api.config';

@Injectable({ providedIn: 'root' })
export class ClassService {
  private http = inject(HttpClient);
  private readonly BASE_URL = API_CONFIG.BASE_URL;

  getClasses(): Observable<Class[]> {
    return this.http.get<Class[]>(`${this.BASE_URL}/classes`);
  }

  getClassById(id: number): Observable<Class> {
    return this.http.get<Class>(`${this.BASE_URL}/classes/${id}`);
  }

  createClass(classData: Class): Observable<Class> {
    return this.http.post<Class>(`${this.BASE_URL}/classes`, classData);
  }

  updateClass(id: number, classData: Partial<Class>): Observable<Class> {
    return this.http.put<Class>(`${this.BASE_URL}/classes/${id}`, classData);
  }

  deleteClass(id: number): Observable<void> {
    return this.http.delete<void>(`${this.BASE_URL}/classes/${id}`);
  }
}

