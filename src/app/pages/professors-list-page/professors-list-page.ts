import { Component, computed, inject, OnInit, signal } from '@angular/core';
import { DatePipe, AsyncPipe } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { ProfessorService } from '../../core/services/professor-service';
import { AuthService } from '../../core/services/auth-service';
import { Professor } from '../../core/models/professor';
import { ReactiveFormsModule, FormBuilder } from '@angular/forms';

@Component({
  selector: 'app-professors-list',
  standalone: true,
  imports: [ReactiveFormsModule],
  templateUrl: './professors-list-page.html',
  styleUrls: ['./professors-list-page.css']
})
export class ProfessorsListPage implements OnInit {
  private professorService = inject(ProfessorService);
  private authService = inject(AuthService);
  private router = inject(Router);
  private fb = inject(FormBuilder);

  currentUser = signal<Professor | null>(null);
  professors = signal<Professor[]>([]);
  loading = signal(true);
  error = signal<string | null>(null);

  // Búsqueda
  searchForm = this.fb.nonNullable.group({
    query: ['']
  });

  // Paginación
  page = signal(0);
  pageSize = 20;
  
  // Helper para calcular total de páginas
  getTotalPages(): number {
    return Math.ceil(this.filteredProfessors().length / this.pageSize) || 1;
  }

  // Lista filtrada por búsqueda
  filteredProfessors = computed(() => {
    const term = this.searchForm.controls.query.value?.toLowerCase() || '';
    const all = this.professors();

    if (!term) return all;
    return all.filter(p =>
      p.lastname?.toLowerCase().includes(term) ||
      p.name?.toLowerCase().includes(term) ||
      p.email?.toLowerCase().includes(term)
    );
  });

  // Lista paginada
  pagedProfessors = computed(() => {
    const start = this.page() * this.pageSize;
    const end = start + this.pageSize;
    return this.filteredProfessors().slice(start, end);
  });

  ngOnInit(): void {
    this.currentUser.set(this.authService.currentProfessor());
    this.loadProfessors();
  }

  loadProfessors(): void {
    this.loading.set(true);
    this.error.set(null);

    this.professorService.getProfessors().subscribe({
      next: (data) => {
        this.professors.set(data);
        this.loading.set(false);
        this.page.set(0); // Resetear a primera página
      },
      error: (err) => {
        this.error.set('Error al cargar los profesores');
        this.loading.set(false);
      }
    });
  }

  searchProfessors(event?: Event) {
    if (event) {
      event.preventDefault();
    }

    const query = this.searchForm.controls.query.value?.trim() || '';
    if (!query) {
      this.loadProfessors();
      return;
    }

    this.loading.set(true);
    this.error.set(null);
    this.professorService.searchProfessors(query).subscribe({
      next: professors => {
        this.professors.set(professors || []);
        this.loading.set(false);
        this.error.set(null);
        this.page.set(0); // Resetear a primera página
      },
      error: err => {
        let errorMsg = 'Error al buscar profesores';
        if (err.error?.error) {
          errorMsg = err.error.error;
        } else if (err.status === 403 || err.status === 401) {
          errorMsg = 'No tienes permisos para buscar profesores';
        } else if (err.status === 0) {
          errorMsg = 'Error de conexión. Verifica que el servidor esté ejecutándose.';
        }
        this.error.set(errorMsg);
        this.loading.set(false);
        this.professors.set([]);
      }
    });
  }

  isCurrentUser(professor: Professor): boolean {
    const current = this.currentUser();
    return current !== null && current.id === professor.id;
  }

  editProfessor(id: number | string) {
    this.router.navigate(['/auth/edit', id]);
  }

  deleteProfessor(id: number | string) {
    const ok = confirm('¿Seguro que desea eliminar al usuario?');

    if (!ok) return;

    this.professorService.deleteProfessor(Number(id)).subscribe({
      next: () => {
        alert('Profesor eliminado correctamente');
        this.loadProfessors();
      },
      error: (err) => {
        let errorMsg = 'Error al eliminar profesor';
        if (err.error?.error) {
          errorMsg = err.error.error;
        } else if (err.status === 403 || err.status === 401) {
          errorMsg = 'No tienes permisos para eliminar profesores';
        } else if (err.status === 409) {
          errorMsg = 'No se puede eliminar el profesor porque tiene cursos asociados';
        }
        alert(errorMsg);
      }
    });
  }

  nextPage() {
    const total = this.filteredProfessors().length;
    const maxPage = Math.floor((total - 1) / this.pageSize);

    if (this.page() < maxPage) {
      this.page.update(p => p + 1);
    }
  }

  prevPage() {
    if (this.page() > 0) {
      this.page.update(p => p - 1);
    }
  }
}
