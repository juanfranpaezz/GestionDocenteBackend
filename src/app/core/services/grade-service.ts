import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Grade } from '../models/grade';
import { StudentGroupedAverages } from '../models/grouped-average';
import { Observable } from 'rxjs';
import { API_CONFIG } from '../config/api.config';

@Injectable({ providedIn: 'root' })
export class GradeService {
  private http = inject(HttpClient);
  private readonly BASE_URL = API_CONFIG.BASE_URL;
  private readonly api = `${this.BASE_URL}${API_CONFIG.GRADES.BASE}`;

  // Crea o actualiza una nota
  setGrade(grade: Grade): Observable<Grade> {
    if (grade.id) {
      return this.http.put<Grade>(`${this.api}/${grade.id}`, grade);
    }
    return this.http.post<Grade>(this.api, grade);
  }

  getGradesByCourse(courseId: number): Observable<Grade[]> {
    return this.http.get<Grade[]>(`${this.BASE_URL}${API_CONFIG.GRADES.BY_COURSE}/${courseId}`);
  }

  getGradesByEvaluation(evaluationId: number): Observable<Grade[]> {
    return this.http.get<Grade[]>(`${this.BASE_URL}${API_CONFIG.GRADES.BY_EVALUATION}/${evaluationId}`);
  }

  getAveragesByCourse(courseId: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.BASE_URL}${API_CONFIG.GRADES.AVERAGES}/${courseId}/averages`);
  }

  getStudentAverage(studentId: number, courseId: number, subjectId?: number): Observable<{ average: number | null; studentId: number; courseId: number; message?: string }> {
    let url = `${this.BASE_URL}${API_CONFIG.GRADES.STUDENT_AVERAGE}/${studentId}/course/${courseId}/average`;
    if (subjectId) {
      url += `?subjectId=${subjectId}`;
    }
    return this.http.get<{ average: number | null; studentId: number; courseId: number; message?: string }>(url);
  }

  getGroupedAverages(studentId: number, courseId: number): Observable<StudentGroupedAverages> {
    return this.http.get<StudentGroupedAverages>(
      `${this.BASE_URL}${API_CONFIG.GRADES.STUDENT_GROUPED_AVERAGES}/${studentId}/course/${courseId}/grouped-averages`
    );
  }

  getGroupedAveragesByCourse(courseId: number): Observable<StudentGroupedAverages[]> {
    return this.http.get<StudentGroupedAverages[]>(
      `${this.BASE_URL}${API_CONFIG.GRADES.GROUPED_AVERAGES}/${courseId}/grouped-averages`
    );
  }

  exportGradesToExcel(courseId: number): Observable<Blob> {
    // Este método se implementará en el frontend usando exceljs
    // Por ahora retornamos un Observable vacío
    throw new Error('Not implemented yet');
  }
}
