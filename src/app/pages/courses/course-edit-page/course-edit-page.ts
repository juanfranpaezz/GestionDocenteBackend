import { Component, inject, OnInit, signal } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { CourseService } from '../../../core/services/course-service';
import { AuthService } from '../../../core/services/auth-service';
import { FormBuilder, Validators, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { Course } from '../../../core/models/course';

@Component({
  standalone: true,
  selector: 'app-course-edit-page',
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './course-edit-page.html',
  styleUrls: ['./course-edit-page.css']
})
export class CourseEditPage implements OnInit
{

  private fb = inject(FormBuilder);
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private courseService = inject(CourseService);
  private authService = inject(AuthService);

  errorMessage = signal<string>('');
  successMessage = signal<string>('');
  submitted = signal(false);
  course!: Course;

 form = this.fb.nonNullable.group({
    name: ['', [Validators.required]],
    school: ['', [Validators.required]],
    description: ['']
  });


  id = Number(this.route.snapshot.paramMap.get('id'));


  ngOnInit(): void
  {
    if (this.id) {
      this.loadCourse(this.id);
    }
  }

loadCourse(id: number) {
  const professor = this.authService.getLoggedProfessor();
  if (!professor) {
    this.errorMessage.set('No hay sesión activa.');
    return;
  }

  console.log('📖 Cargando curso con ID:', id);
  
  // El backend valida automáticamente que el curso pertenezca al profesor del JWT
  this.courseService.getCourseById(id).subscribe({
    next: (course) => {
      console.log('✅ Curso cargado:', course);
      this.course = course;
      this.errorMessage.set('');
      this.form.patchValue({
        name: course.name,
        school: course.school,
        description: course.description ?? ''
      });
    },
    error: (err) => {
      console.error('❌ Error al cargar curso:', err);
      let errorMsg = 'Error al cargar el curso.';
      
      if (err.error?.error) {
        errorMsg = err.error.error;
      } else if (err.status === 404) {
        errorMsg = 'Curso no encontrado.';
      } else if (err.status === 401) {
        errorMsg = 'No estás autenticado. Por favor inicia sesión nuevamente.';
      } else if (err.status === 403) {
        errorMsg = 'No tienes acceso a este curso.';
      }
      
      this.errorMessage.set(errorMsg);
    }
  });
}


  onSubmit() 
  {
    this.submitted.set(true);
    this.errorMessage.set('');
    this.successMessage.set('');

    if (this.form.invalid || !this.course) {
      this.errorMessage.set('Por favor completa todos los campos obligatorios.');
      return;
    }

    if (!this.course.id) {
      this.errorMessage.set('Error: El curso no tiene un ID válido.');
      return;
    }

    const updated: Course = {
      ...this.course,
      ...this.form.getRawValue()
    };

    console.log('💾 Guardando cambios del curso:', updated);

    this.courseService.updateCourse(updated).subscribe({
      next: (saved) => {
        console.log('✅ Curso guardado exitosamente:', saved);
        this.successMessage.set('Curso actualizado correctamente');
        this.errorMessage.set('');
        // Limpiar mensaje de éxito después de 3 segundos y navegar
        setTimeout(() => {
          this.router.navigate(['/course/list']);
        }, 1500);
      },
      error: (err) => {
        console.error('❌ Error al guardar curso:', err);
        let errorMsg = 'Error al guardar el curso.';
        
        if (err.error?.error) {
          errorMsg = err.error.error;
        } else if (err.error?.mensaje) {
          errorMsg = err.error.mensaje;
        } else if (err.error?.campos) {
          // Si hay errores de validación por campo, mostrarlos
          const campos = Object.values(err.error.campos).join(', ');
          errorMsg = `Error de validación: ${campos}`;
        } else if (err.status === 401) {
          errorMsg = 'No estás autenticado. Por favor inicia sesión nuevamente.';
        } else if (err.status === 403) {
          errorMsg = 'No tienes permisos para actualizar este curso.';
        } else if (err.status === 404) {
          errorMsg = 'El curso no existe.';
        } else if (err.status === 0) {
          errorMsg = 'No se pudo conectar con el servidor. Verifica que el backend esté corriendo.';
        }
        
        this.errorMessage.set(errorMsg);
        this.successMessage.set('');
      }
    });
  }

}
