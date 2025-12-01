import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Student, StudentCreate } from '../models/student';
import { Observable } from 'rxjs';
import { tap } from 'rxjs/operators';
import { API_CONFIG } from '../config/api.config';

@Injectable({ providedIn: 'root' })
export class StudentService {

  private http = inject(HttpClient);
  private readonly BASE_URL = API_CONFIG.BASE_URL;
  private readonly apiUrl = `${this.BASE_URL}${API_CONFIG.STUDENTS.BASE}`;

  addStudentToCourse(student: StudentCreate): Observable<Student> {
    console.log('➕ Agregando estudiante al curso:', student);
    console.log('📍 URL:', this.apiUrl);
    
    return this.http.post<Student>(this.apiUrl, student).pipe(
      tap({
        next: (created) => console.log('✅ Estudiante agregado exitosamente:', created),
        error: (err) => {
          console.error('❌ Error al agregar estudiante:', err);
          console.error('Status:', err.status);
          console.error('Error body:', err.error);
        }
      })
    );
  }

  updateStudent(id: number, student: Partial<Student>): Observable<Student> {
    console.log('📝 Actualizando estudiante ID:', id);
    return this.http.put<Student>(`${this.apiUrl}/${id}`, student).pipe(
      tap({
        next: (updated) => console.log('✅ Estudiante actualizado:', updated),
        error: (err) => {
          console.error('❌ Error al actualizar estudiante:', err);
          console.error('Status:', err.status);
          console.error('Error body:', err.error);
        }
      })
    );
  }

  removeStudent(id: number): Observable<void> {
    console.log('🗑️ Eliminando estudiante ID:', id);
    return this.http.delete<void>(`${this.apiUrl}/${id}`).pipe(
      tap({
        next: () => console.log('✅ Estudiante eliminado exitosamente'),
        error: (err) => {
          console.error('❌ Error al eliminar estudiante:', err);
          console.error('Status:', err.status);
          console.error('Error body:', err.error);
        }
      })
    );
  }

  getStudentsByCourse(courseId: number): Observable<Student[]> {
    console.log('📚 Obteniendo estudiantes del curso ID:', courseId);
    const url = `${this.BASE_URL}${API_CONFIG.STUDENTS.BY_COURSE}/${courseId}`;
    console.log('📍 URL:', url);
    
    return this.http.get<Student[]>(url).pipe(
      tap({
        next: (students) => console.log('✅ Estudiantes recibidos:', students),
        error: (err) => {
          console.error('❌ Error al obtener estudiantes:', err);
          console.error('Status:', err.status);
          console.error('Error body:', err.error);
        }
      })
    );
  }

  importStudents(courseId: number, students: StudentCreate[]): Observable<{ message: string; count: number; students: Student[] }> {
    const url = `${this.BASE_URL}${API_CONFIG.STUDENTS.IMPORT}/${courseId}`;
    return this.http.post<{ message: string; count: number; students: Student[] }>(url, students);
  }
}
