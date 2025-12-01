import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Present } from '../models/present';
import { API_CONFIG } from '../config/api.config';

@Injectable({ providedIn: 'root' })
export class PresentService {
  private http = inject(HttpClient);
  private readonly BASE_URL = API_CONFIG.BASE_URL;

  getAttendancesByCourse(courseId: number): Observable<Present[]> {
    return this.http.get<Present[]>(`${this.BASE_URL}/attendances/course/${courseId}`);
  }

  getAttendancesByStudent(studentId: number): Observable<Present[]> {
    return this.http.get<Present[]>(`${this.BASE_URL}/attendances/student/${studentId}`);
  }

  markAttendance(attendance: Present): Observable<Present> {
    return this.http.post<Present>(`${this.BASE_URL}/attendances`, attendance);
  }

  updateAttendance(id: number, attendance: Present): Observable<Present> {
    return this.http.put<Present>(`${this.BASE_URL}/attendances/${id}`, attendance);
  }

  getAttendancePercentage(studentId: number, courseId: number): Observable<number> {
    return this.http.get<number>(`${this.BASE_URL}/attendances/student/${studentId}/course/${courseId}/percentage`);
  }

  /**
   * Crea múltiples asistencias en una sola operación.
   * Nota: El backend no tiene un endpoint batch, así que hacemos múltiples requests.
   */
  createManyAttendances(attendances: Present[]): Observable<Present[]> {
    // El backend no tiene un endpoint batch, así que creamos cada asistencia individualmente
    // En el futuro se podría optimizar con un endpoint batch en el backend
    const requests = attendances.map(attendance => this.markAttendance(attendance));
    return new Observable(observer => {
      const results: Present[] = [];
      let completed = 0;
      const total = requests.length;

      if (total === 0) {
        observer.next(results);
        observer.complete();
        return;
      }

      requests.forEach((request, index) => {
        request.subscribe({
          next: (result) => {
            results[index] = result;
            completed++;
            if (completed === total) {
              observer.next(results);
              observer.complete();
            }
          },
          error: (err) => {
            console.error(`Error al crear asistencia ${index}:`, err);
            completed++;
            if (completed === total) {
              // Aún así retornamos los resultados que se crearon exitosamente
              observer.next(results);
              observer.complete();
            }
          }
        });
      });
    });
  }

  getAttendanceAverages(courseId: number, subjectId?: number): Observable<Array<{ studentId: number; firstName: string; lastName: string; attendancePercentage: number | null; totalDays: number; presentDays: number; absentDays: number }>> {
    let url = `${this.BASE_URL}${API_CONFIG.ATTENDANCES.AVERAGES}/${courseId}/averages`;
    if (subjectId) {
      url += `?subjectId=${subjectId}`;
    }
    return this.http.get<Array<{ studentId: number; firstName: string; lastName: string; attendancePercentage: number | null; totalDays: number; presentDays: number; absentDays: number }>>(url);
  }

  saveAttendancesBulk(attendances: Present[]): Observable<Present[]> {
    return this.http.post<Present[]>(`${this.BASE_URL}${API_CONFIG.ATTENDANCES.BULK}`, attendances);
  }
}

