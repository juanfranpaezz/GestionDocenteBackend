import { Component, signal, inject, effect } from '@angular/core';
import { Router, RouterLink, ActivatedRoute } from '@angular/router';
import { CommonModule, DatePipe } from '@angular/common';
import { CourseService } from '../../../core/services/course-service';
import { AuthService } from '../../../core/services/auth-service';
import { Course } from '../../../core/models/course';

@Component({
  selector: 'app-archived-courses-page',
  standalone: true,
  templateUrl: './archived-courses-page.html',
  styleUrls: ['./archived-courses-page.css'],
  imports: [RouterLink, CommonModule, DatePipe]
})
export class ArchivedCoursesPage {

  private courseService = inject(CourseService);
  private authService = inject(AuthService);
  private router = inject(Router);
  private route = inject(ActivatedRoute);

  courses = signal<Course[]>([]);
  errorMessage = signal('');
  searchQuery = signal('');

  constructor() {
    // Cargar query params de búsqueda si existen
    this.route.queryParams.subscribe(params => {
      if (params['search']) {
        this.searchQuery.set(params['search']);
      } else {
        this.searchQuery.set('');
      }
      // Recargar cursos cuando cambien los query params
      const professor = this.authService.currentProfessor();
      if (professor) {
        this.loadArchivedCourses();
      }
    });

    effect(() => {
      const professor = this.authService.currentProfessor();

      if (!professor) {
        this.errorMessage.set("No hay sesión activa.");
        this.courses.set([]);
        return;
      }

      // Solo cargar si no hay query params (para evitar doble carga)
      if (!this.route.snapshot.queryParams['search']) {
        this.loadArchivedCourses();
      }
    });
  }

  loadArchivedCourses() {
    const query = this.searchQuery().trim();
    
    if (query) {
      this.courseService.searchCourses(query, true).subscribe({
        next: (data) => {
          this.courses.set(data);
          this.errorMessage.set('');
        },
        error: (err) => {
          console.error('❌ Error al cargar cursos archivados:', err);
          this.errorMessage.set("Error al cargar cursos archivados.");
          this.courses.set([]);
        }
      });
    } else {
      this.courseService.getArchivedCourses().subscribe({
        next: (data) => {
          this.courses.set(data);
          this.errorMessage.set('');
        },
        error: (err) => {
          console.error('❌ Error al cargar cursos archivados:', err);
          this.errorMessage.set("Error al cargar cursos archivados.");
          this.courses.set([]);
        }
      });
    }
  }

  unarchiveCourse(id: number) {
    if (!confirm("¿Des-archivar este curso?")) return;

    this.courseService.unarchiveCourse(id).subscribe({
      next: () => {
        this.loadArchivedCourses();
        this.errorMessage.set('');
      },
      error: (err) => {
        console.error('❌ Error al des-archivar curso:', err);
        let errorMsg = 'Error al des-archivar el curso.';
        
        if (err.error?.error) {
          errorMsg = err.error.error;
        }
        
        this.errorMessage.set(errorMsg);
      }
    });
  }

  goBack() {
    this.router.navigate(['/course/list']);
  }
}

