import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { API_CONFIG } from '../config/api.config';

export interface EmailTemplate {
  id?: number;
  name: string;
  subject: string;
  body: string;
  professorId?: number;
  isGlobal: boolean;
}

@Injectable({ providedIn: 'root' })
export class EmailTemplateService {
  private http = inject(HttpClient);
  private readonly BASE_URL = API_CONFIG.BASE_URL;
  private readonly apiUrl = `${this.BASE_URL}${API_CONFIG.EMAIL_TEMPLATES.BASE}`;

  getEmailTemplates(includeGlobal: boolean = false): Observable<EmailTemplate[]> {
    let params = new HttpParams();
    if (includeGlobal) {
      params = params.set('global', 'true');
    }
    return this.http.get<EmailTemplate[]>(this.apiUrl, { params });
  }

  getEmailTemplateById(id: number): Observable<EmailTemplate> {
    return this.http.get<EmailTemplate>(`${this.apiUrl}/${id}`);
  }

  createEmailTemplate(template: Omit<EmailTemplate, 'id'>): Observable<EmailTemplate> {
    return this.http.post<EmailTemplate>(this.apiUrl, template);
  }

  updateEmailTemplate(id: number, template: Partial<EmailTemplate>): Observable<EmailTemplate> {
    return this.http.put<EmailTemplate>(`${this.apiUrl}/${id}`, template);
  }

  deleteEmailTemplate(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}

