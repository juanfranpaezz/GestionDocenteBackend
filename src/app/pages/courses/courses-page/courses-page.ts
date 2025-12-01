import { Component, signal, inject, effect, HostListener } from '@angular/core';
import { Router, RouterLink, ActivatedRoute } from '@angular/router';
import { CourseService } from '../../../core/services/course-service';
import { AuthService } from '../../../core/services/auth-service';
import { Course } from '../../../core/models/course';

@Component({
  selector: 'app-courses-page',
  standalone: true,
  templateUrl: './courses-page.html',
  styleUrls: ['./courses-page.css'],
  imports: [RouterLink]
})
export class CoursesPage {

  private courseService = inject(CourseService);
  private authService = inject(AuthService);
  private router = inject(Router);
  private route = inject(ActivatedRoute);

  courses = signal<Course[]>([]);
  errorMessage = signal('');
  searchQuery = signal('');
  showArchived = signal(false);
  openMenuId = signal<number | null>(null);

  constructor() {
    // Cargar query params de búsqueda si existen
    this.route.queryParams.subscribe(params => {
      if (params['search']) {
        this.searchQuery.set(params['search']);
      }
    });

    // Se ejecuta cada vez que cambia currentProfessor
    effect(() => {
      const professor = this.authService.currentProfessor(); // ✔ obtener valor

      console.log("Profesor actual:", professor?.name);
      console.log("Profesor id:", professor?.id);

      if (!professor) {
        this.errorMessage.set("No hay sesión activa.");
        this.courses.set([]); // Evita mostrar basura
        return;
      }

      // Cargar cursos del profe logueado
      this.loadCourses(professor.id!);
    });
  }

  loadCourses(professorId: number) {
    console.log('📚 Cargando cursos para profesor ID:', professorId);
    const query = this.searchQuery().trim();
    const archived = this.showArchived();
    
    if (query) {
      this.courseService.searchCourses(query, archived).subscribe({
        next: (data) => {
          console.log('✅ Cursos cargados:', data);
          this.courses.set(data);
          this.errorMessage.set('');
        },
        error: (err) => {
          console.error('❌ Error al cargar cursos:', err);
          this.errorMessage.set("Error al cargar cursos.");
          this.courses.set([]);
        }
      });
    } else if (archived) {
      this.courseService.getArchivedCourses().subscribe({
        next: (data) => {
          console.log('✅ Cursos archivados cargados:', data);
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
      this.courseService.getCourses().subscribe({
        next: (data) => {
          console.log('✅ Cursos cargados:', data);
          this.courses.set(data);
          this.errorMessage.set('');
        },
        error: (err) => {
          console.error('❌ Error al cargar cursos:', err);
          this.errorMessage.set("Error al cargar cursos.");
          this.courses.set([]);
        }
      });
    }
  }

  onSearchChange(query: string) {
    this.searchQuery.set(query);
    const professor = this.authService.currentProfessor();
    if (professor?.id) {
      this.loadCourses(professor.id);
    }
  }

  toggleArchived() {
    this.showArchived.update(v => !v);
    const professor = this.authService.currentProfessor();
    if (professor?.id) {
      this.loadCourses(professor.id);
    }
  }

  goToArchived() {
    this.router.navigate(['/courses/archived']);
  }

  toggleCourseMenu(courseId: number) {
    if (this.openMenuId() === courseId) {
      this.openMenuId.set(null);
    } else {
      this.openMenuId.set(courseId);
    }
  }

  closeMenu() {
    this.openMenuId.set(null);
  }

  archiveCourse(id: number) {
    if (!confirm('¿Archivar este curso? No se eliminará, pero dejará de aparecer en la lista principal.')) return;
    
    this.courseService.archiveCourse(id).subscribe({
      next: () => {
        const professor = this.authService.currentProfessor();
        if (professor?.id) {
          this.loadCourses(professor.id);
        }
        this.errorMessage.set('');
      },
      error: (err) => {
        console.error('Error al archivar curso:', err);
        this.errorMessage.set('Error al archivar el curso.');
      }
    });
  }

  duplicateCourse(id: number) {
    if (!confirm('¿Crear una copia de este curso? Se copiarán los estudiantes, pero no los tipos de evaluación ni las notas.')) return;
    
    this.courseService.duplicateCourse(id, {
      copyStudents: true,
      copyEvaluationTypes: false,
      copyEvaluations: false,
      copySchedules: true
    }).subscribe({
      next: (response) => {
        alert(`Curso duplicado exitosamente. Nuevo curso: ${response.newCourseName}`);
        const professor = this.authService.currentProfessor();
        if (professor?.id) {
          this.loadCourses(professor.id);
        }
        this.errorMessage.set('');
      },
      error: (err) => {
        console.error('Error al duplicar curso:', err);
        this.errorMessage.set('Error al duplicar el curso.');
      }
    });
  }

  deleteCourse(id: number) {
    if (!confirm("¿Seguro que deseas eliminar este curso? Esta acción no se puede deshacer.")) return;

    console.log('🗑️ Intentando eliminar curso ID:', id);
    
    this.courseService.deleteCourse(id).subscribe({
      next: () => {
        console.log('✅ Curso eliminado exitosamente');
        const professor = this.authService.currentProfessor();
        if (professor?.id) {
          this.loadCourses(professor.id);
        }
        this.errorMessage.set('');
        // Asegurar que permanecemos en la página de cursos (no redirigir)
        this.router.navigate(['/course/list']);
      },
      error: (err) => {
        console.error('❌ Error al eliminar curso:', err);
        let errorMsg = 'Error al eliminar el curso.';
        
        if (err.error?.error) {
          errorMsg = err.error.error;
        } else if (err.status === 401) {
          errorMsg = 'No estás autenticado. Por favor inicia sesión nuevamente.';
        } else if (err.status === 403) {
          errorMsg = 'No tienes permisos para eliminar este curso.';
        } else if (err.status === 404) {
          errorMsg = 'El curso no existe.';
        } else if (err.status === 0) {
          errorMsg = 'No se pudo conectar con el servidor. Verifica que el backend esté corriendo.';
        }
        
        this.errorMessage.set(errorMsg);
      }
    });
  }

  editCourse(id: number) {
    this.router.navigate(['/course/edit', id]);
  }

  @HostListener('document:click', ['$event'])
  onDocumentClick(event: MouseEvent) {
    const target = event.target as HTMLElement;
    // Si el click no es dentro del menú o el botón del menú, cerrar el menú
    if (!target.closest('.course-menu-container') && !target.closest('.course-menu-btn')) {
      this.closeMenu();
    }
  }
}
