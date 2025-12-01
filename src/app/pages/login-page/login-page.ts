import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { AuthService } from '../../core/services/auth-service';
import { Role } from '../../enums/roles';
import { CourseScheduleService } from '../../core/services/course-schedule-service';

// Angular Material
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { Router, RouterLink, ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-login-page',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    RouterLink
  ],
  templateUrl: './login-page.html',
  styleUrls: ['./login-page.css']
})
export class LoginPage {
  loading = signal(false);
  submitted = false;
  errorMessage = signal<string | null>(null);
  private fb = inject(FormBuilder);
  private auth = inject(AuthService);
  private router = inject(Router);
  private route = inject(ActivatedRoute);
  private scheduleService = inject(CourseScheduleService);

  form = this.fb.group({
    email: ['', [Validators.required, Validators.email]],
    password: ['', Validators.required],
  });

  onSubmit() {
  if (this.form.invalid) return;

  this.loading.set(true);

  const { email, password } = this.form.value;

  this.auth.login(email!, password!).subscribe({
    next: ok => {
      this.loading.set(false);
      if (!ok) {
        this.errorMessage.set("Email o contraseña incorrectos.");
        return;
      }

      // Verificar que el token se guardó correctamente
      const token = this.auth.getToken();
      if (!token) {
        console.error('⚠️ Token no se guardó después del login');
        this.errorMessage.set("Error: No se pudo guardar la sesión. Intenta nuevamente.");
        return;
      }

      console.log('✅ Login exitoso, token guardado:', token.substring(0, 20) + '...');
      
      // Obtener el profesor logueado para determinar la redirección según su rol
      const professor = this.auth.currentProfessor();
      const userRole = professor?.role;
      
      // Obtener URL de retorno si existe (desde query params del guard)
      let returnUrl = this.route.snapshot.queryParams['returnUrl'];
      
      // Si hay returnUrl explícito (por ejemplo, desde un guard), usarlo directamente
      if (returnUrl) {
        this.router.navigate([returnUrl]);
        return;
      }
      
      // Si es ADMIN, redirigir a la lista de profesores
      if (userRole === Role.ADMIN) {
        this.router.navigate(['/professors/list']);
        return;
      }
      
      // Para PROFESSOR: verificar si hay un curso con horario activo
      // Solo al iniciar sesión, después puede navegar libremente
      this.scheduleService.getCurrentScheduleRedirect().subscribe({
        next: (redirect) => {
          if (redirect.hasCurrentCourse && redirect.courseId) {
            // Redirigir al curso que tiene horario activo, con la materia correspondiente
            console.log(`📅 Redirigiendo al curso activo: ${redirect.courseName} (ID: ${redirect.courseId}, Materia: ${redirect.subjectId || 'sin materia'})`);
            
            // Construir ruta con query params para la materia
            const navigationExtras: any = {};
            if (redirect.subjectId) {
              navigationExtras.queryParams = { subjectId: redirect.subjectId };
            }
            
            this.router.navigate(['/course/detail', redirect.courseId], navigationExtras);
          } else {
            // No hay curso activo, ir a la lista de cursos
            this.router.navigate(['/course/list']);
          }
        },
        error: (err) => {
          // Si hay error al obtener el curso activo, ir a la lista de cursos
          console.warn('⚠️ No se pudo obtener el curso activo, redirigiendo a lista de cursos:', err);
          this.router.navigate(['/course/list']);
        }
      });
    },
    error: (err) => {
      this.loading.set(false);
      console.error('❌ Error en login:', err);
      
      let errorMsg = "Error en el servidor.";
      if (err.error?.error) {
        errorMsg = err.error.error;
      } else if (err.status === 401) {
        errorMsg = "Email o contraseña incorrectos.";
      } else if (err.status === 0) {
        errorMsg = "No se pudo conectar con el servidor. Verifica que el backend esté corriendo.";
      }
      
      this.errorMessage.set(errorMsg);
    }
  });
}

}
