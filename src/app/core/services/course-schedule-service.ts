import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { API_CONFIG } from '../config/api.config';

export interface CourseSchedule {
  id?: number;
  courseId: number;
  dayOfWeek: 'MONDAY' | 'TUESDAY' | 'WEDNESDAY' | 'THURSDAY' | 'FRIDAY' | 'SATURDAY' | 'SUNDAY';
  startTime: string; // Format: "HH:mm"
  endTime: string; // Format: "HH:mm"
  subjectId?: number; // ID de la materia (opcional)
}

export interface CurrentScheduleRedirect {
  hasCurrentCourse: boolean;
  courseId?: number;
  subjectId?: number; // ID de la materia del horario activo
  courseName?: string;
}

@Injectable({ providedIn: 'root' })
export class CourseScheduleService {
  private http = inject(HttpClient);
  private readonly BASE_URL = API_CONFIG.BASE_URL;
  private readonly apiUrl = `${this.BASE_URL}${API_CONFIG.COURSE_SCHEDULES.BASE}`;

  getSchedulesByCourse(courseId: number): Observable<CourseSchedule[]> {
    return this.http.get<CourseSchedule[]>(`${this.apiUrl}/${courseId}/schedules`);
  }

  createSchedules(courseId: number, schedules: Omit<CourseSchedule, 'id' | 'courseId'>[]): Observable<CourseSchedule[]> {
    const schedulesWithCourseId = schedules.map(s => ({ ...s, courseId }));
    return this.http.post<CourseSchedule[]>(`${this.apiUrl}/${courseId}/schedules`, schedulesWithCourseId);
  }

  deleteSchedule(courseId: number, scheduleId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${courseId}/schedules/${scheduleId}`);
  }

  getCurrentScheduleRedirect(): Observable<CurrentScheduleRedirect> {
    return this.http.get<CurrentScheduleRedirect>(`${this.BASE_URL}${API_CONFIG.COURSE_SCHEDULES.CURRENT_REDIRECT}`);
  }
}

