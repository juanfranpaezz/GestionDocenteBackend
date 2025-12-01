import { Component, EventEmitter, inject, Input, OnInit, Output, signal } from '@angular/core';
import { StudentService } from '../../../core/services/student-service';
import { AbstractControl, FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Student, StudentCreate } from '../../../core/models/student';
import { Course } from '../../../core/models/course';
import { CourseService } from '../../../core/services/course-service';
import { GradeService } from '../../../core/services/grade-service';
import { PresentService } from '../../../core/services/present-service';

@Component({
  selector: 'app-course-students',
  imports: [ReactiveFormsModule],
  templateUrl: './course-student-component.html',
  styleUrl: './course-student-component.css',
})
export class CourseStudentComponent implements OnInit
{
  @Input() courseId!: number;

  @Output() studentAdded = new EventEmitter<Student>();
  @Output() studentRemoved = new EventEmitter<number>();
  @Output() studentUpdated = new EventEmitter<Student>();

  private studentService = inject(StudentService);
  private fb = inject(FormBuilder);
  private courseService = inject(CourseService);
  private gradeService = inject(GradeService);
  private presentService = inject(PresentService);

  students = signal<Student[]>([]);
  filteredStudents = signal<Student[]>([]);
  searchTerm = signal<string>('');
  sortField = signal<'lastFirst' | 'firstLast' | 'grade' | 'attendance'>('lastFirst');
  sortDirection = signal<'asc' | 'desc'>('asc');
  errorMessage = signal<string | null>(null);
  editingStudent = signal<Student | null>(null);
  studentAverages = signal<Map<number, number>>(new Map());
  attendanceAverages = signal<Map<number, number | null>>(new Map());
  submitted = signal(false);
  showForm = signal(false);

  form = this.fb.nonNullable.group({
    firstName: ['', Validators.required],
    lastName: [''],
    cel: ['', this.celularValidator.bind(this)],
    email: ['',  [Validators.email]],
    document: ['', Validators.maxLength(20)],
  });

  // Validador personalizado para celular (10-15 dígitos)
  private celularValidator(control: AbstractControl) {
    const value = control.value?.toString().trim();
    if (!value || value === '') {
      return null; // Campo opcional, no hay error si está vacío
    }
    return /^\d{10,15}$/.test(value) ? null : { celularInvalido: true };
  }

  ngOnInit(): void {
    this.loadStudents();
  }

  loadStudents() {
    console.log('📚 Cargando estudiantes del curso ID:', this.courseId);
    this.studentService.getStudentsByCourse(this.courseId).subscribe({
      next: (students) => {
        console.log('✅ Estudiantes cargados:', students);
        this.students.set(students);
        this.errorMessage.set(null);
        // Cargar promedios de todos los estudiantes
        this.loadAllAverages(students);
        // Cargar promedios de asistencia
        this.loadAttendanceAverages();
        this.applyFilterAndSort();
      },
      error: (err) => {
        console.error('❌ Error al cargar alumnos:', err);
        let errorMsg = 'Error al cargar alumnos';
        if (err.error?.error) {
          errorMsg = err.error.error;
        }
        this.errorMessage.set(errorMsg);
      }
    });
  }

  loadAllAverages(students: Student[]) {
    const averages = new Map<number, number>();
    let completed = 0;
    
    if (students.length === 0) {
      this.studentAverages.set(averages);
      return;
    }

    students.forEach(student => {
      if (student.id) {
        this.gradeService.getStudentAverage(student.id, this.courseId).subscribe({
          next: (response: any) => {
            if (response.average !== null && response.average !== undefined) {
              averages.set(student.id!, response.average);
            }
            completed++;
            if (completed === students.length) {
              this.studentAverages.set(averages);
            }
          },
          error: () => {
            completed++;
            if (completed === students.length) {
              this.studentAverages.set(averages);
            }
          }
        });
      }
    });
  }

  // Método público para refrescar promedios (llamado desde el componente padre)
  refreshAverages() {
    this.loadAllAverages(this.students());
    this.loadAttendanceAverages();
    this.applyFilterAndSort();
  }

  // Método público para refrescar solo promedios de asistencia
  refreshAttendanceAverages() {
    this.loadAttendanceAverages();
  }

  private loadAttendanceAverages() {
    const map = new Map<number, number | null>();
    this.presentService.getAttendanceAverages(this.courseId).subscribe({
      next: (data: Array<{ studentId: number; attendancePercentage: number | null }>) => {
        data.forEach(item => {
          if (item.studentId != null) {
            map.set(item.studentId, item.attendancePercentage);
          }
        });
        this.attendanceAverages.set(map);
      },
      error: (err) => {
        console.error('❌ Error al cargar promedios de asistencia:', err);
      }
    });
  }

  getAttendanceLabel(studentId: number): string {
    const val = this.attendanceAverages().get(studentId);
    if (val === null || val === undefined) return '-';
    return `${Math.round(val)}%`;
  }

  onSearchChange(value: string) {
    this.searchTerm.set(value);
    this.applyFilterAndSort();
  }

  setSortField(field: 'lastFirst' | 'firstLast' | 'grade' | 'attendance') {
    this.sortField.set(field);
    this.applyFilterAndSort();
  }

  toggleSortDirection() {
    this.sortDirection.update(v => (v === 'asc' ? 'desc' : 'asc'));
    this.applyFilterAndSort();
  }

  private applyFilterAndSort() {
    const term = this.searchTerm().toLowerCase().trim();
    let result = [...this.students()];

    // Filtro por texto
    if (term) {
      result = result.filter(s => {
        const fullNameLastFirst = `${s.lastName ?? ''} ${s.firstName ?? ''}`.toLowerCase();
        const fullNameFirstLast = `${s.firstName ?? ''} ${s.lastName ?? ''}`.toLowerCase();
        const email = (s.email ?? '').toLowerCase();
        return (
          fullNameLastFirst.includes(term) ||
          fullNameFirstLast.includes(term) ||
          email.includes(term)
        );
      });
    }

    // Orden
    const direction = this.sortDirection();
    const field = this.sortField();

    result.sort((a, b) => {
      const dir = direction === 'asc' ? 1 : -1;

      const lastA = (a.lastName || '').toLowerCase();
      const lastB = (b.lastName || '').toLowerCase();
      const firstA = (a.firstName || '').toLowerCase();
      const firstB = (b.firstName || '').toLowerCase();

      if (field === 'lastFirst') {
        const cmpLast = lastA.localeCompare(lastB);
        if (cmpLast !== 0) return cmpLast * dir;
        return firstA.localeCompare(firstB) * dir;
      }

      if (field === 'firstLast') {
        const cmpFirst = firstA.localeCompare(firstB);
        if (cmpFirst !== 0) return cmpFirst * dir;
        return lastA.localeCompare(lastB) * dir;
      }

      if (field === 'grade') {
        const avgMap = this.studentAverages();
        const valA = avgMap.get(a.id!) ?? Number.NaN;
        const valB = avgMap.get(b.id!) ?? Number.NaN;
        const aIsNum = !Number.isNaN(valA);
        const bIsNum = !Number.isNaN(valB);
        if (aIsNum && bIsNum) {
          return (valA - valB) * dir;
        }
        if (aIsNum) return -1 * dir;
        if (bIsNum) return 1 * dir;
        return 0;
      }

      if (field === 'attendance') {
        const attMap = this.attendanceAverages();
        const valA = attMap.get(a.id!) ?? Number.NaN;
        const valB = attMap.get(b.id!) ?? Number.NaN;
        const aIsNum = !Number.isNaN(valA);
        const bIsNum = !Number.isNaN(valB);
        if (aIsNum && bIsNum) {
          return (valA - valB) * dir;
        }
        if (aIsNum) return -1 * dir;
        if (bIsNum) return 1 * dir;
        return 0;
      }

      return 0;
    });

    this.filteredStudents.set(result);
  }

  addStudent() {
    this.submitted.set(true);
    this.errorMessage.set(null);

    if (this.form.invalid) {
      if (this.form.controls['firstName'].invalid) {
        this.errorMessage.set('El nombre del estudiante es obligatorio.');
      } else if (this.form.controls['email'].invalid) {
        const emailErrors = this.form.controls['email'].errors || {};
        if (emailErrors['required']) {
          this.errorMessage.set('El email es obligatorio.');
        } else if (emailErrors['email']) {
          this.errorMessage.set('El email debe tener un formato válido.');
        } else {
          this.errorMessage.set('Por favor revisa el campo email.');
        }
      } else if (this.form.controls['cel'].invalid) {
        this.errorMessage.set('El celular debe contener entre 10 y 15 dígitos numéricos.');
      } else if (this.form.controls['document'].invalid) {
        this.errorMessage.set('El documento no puede tener más de 20 caracteres.');
      } else {
        this.errorMessage.set('Por favor completa correctamente todos los campos.');
      }
      return;
    }

    const raw = this.form.getRawValue();
    const payload: any = { ...raw, courseId: this.courseId };
    // Si email está vacío ('' o solo espacios), lo eliminamos del payload
    if (payload.email != null && payload.email.toString().trim() === '') {
      delete payload.email;
    }

    console.log('➕ Agregando estudiante (payload):', payload);
    this.errorMessage.set(null);

    this.studentService.addStudentToCourse(payload).subscribe({
      next: (created) => {
        console.log('✅ Estudiante agregado exitosamente:', created);
        // Recargar lista completa para asegurar sincronización
        this.loadStudents();
        this.form.reset();
        this.showForm.set(false);
        this.errorMessage.set(null);
        this.submitted.set(false);

        // ✅ Emitir evento
        this.studentAdded.emit(created);
        
        // Cargar promedio del nuevo estudiante
        if (created.id) {
          this.gradeService.getStudentAverage(created.id, this.courseId).subscribe({
            next: (response: any) => {
              if (response.average !== null && response.average !== undefined) {
                this.studentAverages.update(map => {
                  const newMap = new Map(map);
                  newMap.set(created.id!, response.average);
                  return newMap;
                });
              }
            }
          });
        }
      },
      error: (err) => {
        console.error('❌ Error al agregar alumno:', err);
        let errorMsg = 'No se pudo agregar el alumno';
        
        // Manejar errores de validación del backend (400 con campos)
        if (err.status === 400 && err.error?.campos) {
          const campos = err.error.campos;
          const errores: string[] = [];
          
          if (campos.firstName) {
            errores.push(`Nombre: ${campos.firstName}`);
          }
          if (campos.email) {
            errores.push(`Email: ${campos.email}`);
          }
          if (campos.cel) {
            errores.push(`Celular: ${campos.cel}`);
          }
          if (campos.document) {
            errores.push(`Documento: ${campos.document}`);
          }
          
          errorMsg = errores.length > 0 
            ? `Errores de validación: ${errores.join(', ')}`
            : err.error.mensaje || 'Error de validación. Verifica los campos ingresados.';
        } else if (err.error?.error) {
          errorMsg = err.error.error;
        } else if (err.status === 401) {
          errorMsg = 'No estás autenticado. Por favor inicia sesión nuevamente.';
        } else if (err.status === 403) {
          errorMsg = 'No tienes permisos para agregar estudiantes a este curso.';
        } else if (err.status === 404) {
          errorMsg = 'El curso no existe.';
        } else if (err.status === 0) {
          errorMsg = 'No se pudo conectar con el servidor. Verifica que el backend esté corriendo.';
        }
        
        this.errorMessage.set(errorMsg);
        this.submitted.set(true);
      }
    });
  }

  editStudent(student: Student) {
    this.editingStudent.set(student);
    this.showForm.set(true);
    this.form.patchValue({
      firstName: student.firstName,
      lastName: student.lastName || '',
      cel: student.cel || '',
      email: student.email || '',
      document: student.document || ''
    });
  }

  cancelEdit() {
    this.editingStudent.set(null);
    this.showForm.set(false);
    this.form.reset();
    this.submitted.set(false);
    this.errorMessage.set(null);
  }

  showAddForm() {
    this.editingStudent.set(null);
    this.showForm.set(true);
    this.form.reset();
    this.submitted.set(false);
    this.errorMessage.set(null);
  }

  updateStudent() {
    this.submitted.set(true);
    this.errorMessage.set(null);

    if (this.form.invalid || !this.editingStudent()) {
      if (this.form.controls['firstName'].invalid) {
        this.errorMessage.set('El nombre del estudiante es obligatorio.');
      } else if (this.form.controls['email'].invalid) {
        const emailErrors = this.form.controls['email'].errors || {};
        if (emailErrors['required']) {
          this.errorMessage.set('El email es obligatorio.');
        } else if (emailErrors['email']) {
          this.errorMessage.set('El email debe tener un formato válido.');
        } else {
          this.errorMessage.set('Por favor revisa el campo email.');
        }
      } else if (this.form.controls['cel'].invalid) {
        this.errorMessage.set('El celular debe contener entre 10 y 15 dígitos numéricos.');
      } else if (this.form.controls['document'].invalid) {
        this.errorMessage.set('El documento no puede tener más de 20 caracteres.');
      } else {
        this.errorMessage.set('Por favor completa correctamente todos los campos.');
      }
      return;
    }

    const studentId = this.editingStudent()!.id!;
    const raw = this.form.getRawValue();
    const updatedData: any = { ...raw, courseId: this.courseId };
    // Si email está vacío, lo eliminamos para evitar validación de cadena vacía en backend
    if (updatedData.email != null && updatedData.email.toString().trim() === '') {
      delete updatedData.email;
    }

    console.log('📝 Actualizando estudiante ID:', studentId, 'payload:', updatedData);
    this.errorMessage.set(null);

    this.studentService.updateStudent(studentId, updatedData).subscribe({
      next: (updated) => {
        console.log('✅ Estudiante actualizado exitosamente:', updated);
        // Recargar lista completa para asegurar sincronización
        this.loadStudents();
        this.editingStudent.set(null);
        this.showForm.set(false);
        this.form.reset();
        this.errorMessage.set(null);
        this.submitted.set(false);
        
        // ✅ Emitir evento de actualización
        this.studentUpdated.emit(updated);
        
        // Actualizar promedio si cambió
        if (updated.id) {
          this.gradeService.getStudentAverage(updated.id, this.courseId).subscribe({
            next: (response: any) => {
              if (response.average !== null && response.average !== undefined) {
                this.studentAverages.update(map => {
                  const newMap = new Map(map);
                  newMap.set(updated.id!, response.average);
                  return newMap;
                });
              } else {
                this.studentAverages.update(map => {
                  const newMap = new Map(map);
                  newMap.delete(updated.id!);
                  return newMap;
                });
              }
            }
          });
        }
      },
      error: (err) => {
        console.error('❌ Error al actualizar alumno:', err);
        let errorMsg = 'No se pudo actualizar el alumno';
        
        if (err.error?.error) {
          errorMsg = err.error.error;
        } else if (err.status === 401) {
          errorMsg = 'No estás autenticado. Por favor inicia sesión nuevamente.';
        } else if (err.status === 403) {
          errorMsg = 'No tienes permisos para editar estudiantes de este curso.';
        } else if (err.status === 404) {
          errorMsg = 'El estudiante no existe.';
        } else if (err.status === 0) {
          errorMsg = 'No se pudo conectar con el servidor. Verifica que el backend esté corriendo.';
        }
        
        this.errorMessage.set(errorMsg);
      }
    });
  }

  deleteStudent(id: number) {
    if (!confirm("¿Seguro que deseas eliminar este estudiante? Esta acción no se puede deshacer.")) {
      return;
    }

    console.log('🗑️ Eliminando estudiante ID:', id);
    this.errorMessage.set(null);

    this.studentService.removeStudent(id).subscribe({
      next: () => {
        console.log('✅ Estudiante eliminado exitosamente');
        this.students.update(current => current.filter(s => s.id !== id));
        this.errorMessage.set(null);
        
        // ✅ Emitir evento de eliminación
        this.studentRemoved.emit(id);
      },
      error: (err) => {
        console.error('❌ Error al eliminar alumno:', err);
        let errorMsg = 'No se pudo eliminar el alumno';
        
        if (err.error?.error) {
          errorMsg = err.error.error;
        } else if (err.status === 401) {
          errorMsg = 'No estás autenticado. Por favor inicia sesión nuevamente.';
        } else if (err.status === 403) {
          errorMsg = 'No tienes permisos para eliminar estudiantes de este curso.';
        } else if (err.status === 404) {
          errorMsg = 'El estudiante no existe.';
        } else if (err.status === 0) {
          errorMsg = 'No se pudo conectar con el servidor. Verifica que el backend esté corriendo.';
        }
        
        this.errorMessage.set(errorMsg);
      }
    });
  }


}