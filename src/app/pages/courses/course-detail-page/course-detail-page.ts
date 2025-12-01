import { CommonModule } from "@angular/common";
import { computed } from "@angular/core";
import { Component, inject, OnInit, signal, ViewChild } from "@angular/core";
import { CourseStudentComponent } from "../../../components/courses/course-student-component/course-student-component";
import { CourseGradesComponent } from "../../../components/courses/course-grade-component/course-grade-component";
import { CourseAttendanceComponent } from "../../../components/courses/course-asistent-component/course-asistent-component";
import { CourseScheduleComponent } from "../../../components/courses/course-schedule-component/course-schedule-component";
import { ActivatedRoute, Router } from "@angular/router";
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from "@angular/forms";
import { Student } from "../../../core/models/student";
import { Course } from "../../../core/models/course";
import { Subject } from "../../../core/models/subject";
import { CourseService } from "../../../core/services/course-service";
import { StudentService } from "../../../core/services/student-service";
import { SubjectService } from "../../../core/services/subject-service";
import { EmailTemplateService, EmailTemplate } from "../../../core/services/email-template-service";

@Component({
  selector: 'app-course-detail-page',
  standalone: true,
  templateUrl: './course-detail-page.html',
  styleUrls: ['./course-detail-page.css'],
  imports: [
  CommonModule,
  ReactiveFormsModule,
  CourseStudentComponent,
  CourseAttendanceComponent,
  CourseGradesComponent,
  CourseScheduleComponent,
],

})
export class CourseDetailPage implements OnInit
{
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private courseService = inject(CourseService);
  private studentService = inject(StudentService);
  private subjectService = inject(SubjectService);
  private emailTemplateService = inject(EmailTemplateService);
  private fb = inject(FormBuilder);
  
  courseId = signal<number>(0);
  course = signal<Course | null>(null);
  mostrarAsistencias = signal<boolean>(false);
  students = signal<Student[]>([]); // Lista de estudiantes para pasar a los componentes hijos
  showEmailModal = signal(false);
  emailTemplates = signal<EmailTemplate[]>([]);
  emailForm: FormGroup;
  
  // Materias y sistema de tabs
  subjects = signal<Subject[]>([]);
  activeSubjectId = signal<number | null>(null); // ID de la materia del tab activo
  activeSubject = computed(() => {
    const activeId = this.activeSubjectId();
    if (!activeId) return null;
    return this.subjects().find(s => s.id === activeId) || null;
  });
  editingSubjectName = signal<number | null>(null); // ID de materia cuyo nombre se está editando
  editingSubjectNameValue = signal<string>(''); // Valor temporal del nombre en edición
  addingSubject = signal(false);
  subjectForm: FormGroup;

  // Computed para pasar estudiantes a los hijos
  studentsForChildren = computed(() => this.students());

  // Referencias a componentes hijos
  @ViewChild(CourseGradesComponent) gradesComp?: CourseGradesComponent;
  @ViewChild(CourseStudentComponent) studentsComp?: CourseStudentComponent;

  constructor() {
    this.emailForm = this.fb.group({
      subject: ['', Validators.required],
      message: ['', Validators.required],
      templateId: [null]
    });
    
    this.subjectForm = this.fb.group({
      name: ['', [Validators.required, Validators.minLength(1)]]
    });
  }

  ngOnInit() {
    const idParam = this.route.snapshot.paramMap.get('id');
    if (idParam) {
      this.courseId.set(Number(idParam));
      this.loadCourse();
      this.loadStudents();
      this.loadSubjects();
    }
  }

  loadCourse(){
    if (this.courseId() > 0) {
      this.courseService.getCourseById(this.courseId()).subscribe({
        next: c => this.course.set(c)
      });
    }
  }

  loadStudents() {
    if (this.courseId() > 0) {
      this.studentService.getStudentsByCourse(this.courseId()).subscribe({
        next: students => this.students.set(students),
        error: err => console.error('Error cargando alumnos', err)
      });
    }
  }

  toggleVista() {
    this.mostrarAsistencias.update(v => !v);
  }

  archiveCourse() {
    if (!confirm('¿Archivar este curso? No se eliminará, pero dejará de aparecer en la lista principal.')) return;
    
    const courseId = this.courseId();
    if (!courseId) return;

    this.courseService.archiveCourse(courseId).subscribe({
      next: (archived) => {
        this.course.set(archived);
        alert('Curso archivado exitosamente');
      },
      error: (err) => {
        console.error('Error al archivar curso:', err);
        alert('Error al archivar el curso');
      }
    });
  }

  unarchiveCourse() {
    if (!confirm('¿Des-archivar este curso?')) return;
    
    const courseId = this.courseId();
    if (!courseId) return;

    this.courseService.unarchiveCourse(courseId).subscribe({
      next: (unarchived) => {
        this.course.set(unarchived);
        alert('Curso des-archivado exitosamente');
      },
      error: (err) => {
        console.error('Error al des-archivar curso:', err);
        alert('Error al des-archivar el curso');
      }
    });
  }

  duplicateCourse() {
    if (!confirm('¿Crear una copia de este curso? Se copiarán los estudiantes, pero no los tipos de evaluación ni las notas.')) return;
    
    const courseId = this.courseId();
    if (!courseId) return;

    this.courseService.duplicateCourse(courseId, {
      copyStudents: true,
      copyEvaluationTypes: false, // No copiar tipos de evaluación
      copyEvaluations: false,
      copySchedules: true
    }).subscribe({
      next: (response) => {
        alert(`Curso duplicado exitosamente. Nuevo curso: ${response.newCourseName}`);
        // Opcional: redirigir al nuevo curso
        // this.router.navigate(['/course/detail', response.newCourseId]);
      },
      error: (err) => {
        console.error('Error al duplicar curso:', err);
        alert('Error al duplicar el curso');
      }
    });
  }

  onStudentAdded(newStudent: Student) {
    // Actualizar lista local de estudiantes
    this.students.update(list => [...list, newStudent]);
    // Actualizar directamente la lista de estudiantes en el componente de notas
    this.gradesComp?.students.update(list => [...list, newStudent]);
  }

  onStudentRemoved(studentId: number) {
    // Actualizar lista local de estudiantes
    this.students.update(list => list.filter(s => s.id !== studentId));
    // Eliminar estudiante de la lista en el componente de notas
    this.gradesComp?.students.update(list => list.filter(s => s.id !== studentId));
    // También eliminar sus notas
    this.gradesComp?.grades.update(list => list.filter(g => g.studentId !== studentId));
  }

  onStudentUpdated(updatedStudent: Student) {
    // Actualizar lista local de estudiantes
    this.students.update(list => 
      list.map(s => s.id === updatedStudent.id ? updatedStudent : s)
    );
    // Actualizar estudiante en la lista del componente de notas
    this.gradesComp?.students.update(list => 
      list.map(s => s.id === updatedStudent.id ? updatedStudent : s)
    );
  }

  onGradesUpdated() {
    // Cuando se actualizan las notas, refrescar los promedios en el componente de estudiantes
    this.studentsComp?.refreshAverages();
  }

  onAttendancesUpdated() {
    // Cuando se actualizan las asistencias, refrescar los promedios de asistencia en el componente de estudiantes
    this.studentsComp?.refreshAttendanceAverages();
  }

  openEmailModal() {
    this.loadEmailTemplates();
    this.showEmailModal.set(true);
  }

  closeEmailModal() {
    this.showEmailModal.set(false);
    this.emailForm.reset();
  }

  loadEmailTemplates() {
    this.emailTemplateService.getEmailTemplates(true).subscribe({
      next: (templates) => this.emailTemplates.set(templates),
      error: (err) => console.error('Error cargando templates:', err)
    });
  }

  sendPersonalizedEmail() {
    if (this.emailForm.invalid) return;
    
    const formValue = this.emailForm.value;
    const courseId = this.courseId();
    if (!courseId) return;

    if (!confirm('¿Enviar este mensaje personalizado a todos los estudiantes del curso?')) {
      return;
    }

    this.courseService.sendPersonalizedMessage(
      courseId,
      formValue.subject,
      formValue.message
    ).subscribe({
      next: (response) => {
        alert('Mensajes enviados exitosamente a todos los estudiantes del curso.');
        this.closeEmailModal();
      },
      error: (err) => {
        console.error('Error al enviar mensajes:', err);
        const errorMsg = err.error?.error || 'Error al enviar los mensajes. Por favor, intenta nuevamente.';
        alert(errorMsg);
      }
    });
  }

  // Métodos para gestión de materias
  loadSubjects() {
    const courseId = this.courseId();
    if (courseId > 0) {
      console.log('📚 Cargando materias para curso:', courseId);
      this.subjectService.getSubjectsByCourse(courseId).subscribe({
        next: (subjects) => {
          console.log('✅ Materias recibidas:', subjects);
          // Si no hay materias, crear una materia default sin nombre
          if (subjects.length === 0) {
            console.log('⚠️ No hay materias, creando materia default...');
            this.createDefaultSubject(courseId);
          } else {
            this.subjects.set(subjects);
            
            // Verificar si hay subjectId en query params (redirección desde login)
            const subjectIdFromQuery = this.route.snapshot.queryParams['subjectId'];
            if (subjectIdFromQuery) {
              const subjectIdNum = Number(subjectIdFromQuery);
              // Verificar que la materia existe en el curso
              const subjectExists = subjects.find(s => s.id === subjectIdNum);
              if (subjectExists) {
                console.log('📌 Seleccionando materia desde query param:', subjectIdNum);
                this.activeSubjectId.set(subjectIdNum);
                // Limpiar query param para evitar recursión
                this.router.navigate([], {
                  relativeTo: this.route,
                  queryParams: {},
                  replaceUrl: true
                });
              }
            } else if (!this.activeSubjectId() && subjects.length > 0) {
              // Si no hay materia activa y no hay query param, seleccionar la primera (o la default sin nombre)
              // Priorizar materia sin nombre, sino la primera
              const defaultSubject = subjects.find(s => !s.name || s.name.trim() === '') || subjects[0];
              if (defaultSubject?.id) {
                this.activeSubjectId.set(defaultSubject.id);
              }
            }
          }
        },
        error: (err) => {
          console.error('❌ Error cargando materias:', err);
          console.error('Error completo:', JSON.stringify(err, null, 2));
          // Si hay error 404 o similar, intentar crear materia default
          if (err.status === 404 || err.status === 0 || !err.status) {
            console.log('⚠️ Error 404 o sin conexión, intentando crear materia default...');
            this.createDefaultSubject(courseId);
          } else {
            this.subjects.set([]);
          }
        }
      });
    }
  }

  private createDefaultSubject(courseId: number) {
    // Crear materia sin nombre (default) - enviar null
    console.log('🔄 Creando materia default para curso:', courseId);
    const subjectData: any = { 
      name: null, 
      courseId 
    };
    
    this.subjectService.createSubject(courseId, subjectData).subscribe({
      next: (newSubject) => {
        console.log('✅ Materia default creada:', newSubject);
        this.subjects.set([newSubject]);
        if (newSubject.id) {
          this.activeSubjectId.set(newSubject.id);
        }
      },
      error: (err) => {
        console.error('❌ Error al crear materia default:', err);
        console.error('Error completo:', JSON.stringify(err, null, 2));
        // Si falla, intentar con string vacío
        console.log('🔄 Intentando con nombre vacío...');
        this.subjectService.createSubject(courseId, { name: '', courseId }).subscribe({
          next: (newSubject) => {
            console.log('✅ Materia default creada con nombre vacío:', newSubject);
            this.subjects.set([newSubject]);
            if (newSubject.id) {
              this.activeSubjectId.set(newSubject.id);
            }
          },
          error: (err2) => {
            console.error('❌ Error al crear materia default con nombre vacío:', err2);
            this.subjects.set([]);
          }
        });
      }
    });
  }

  // Cambiar tab activo (materia)
  selectSubject(subjectId: number | null) {
    this.activeSubjectId.set(subjectId);
    // Si se está editando un nombre, cancelar edición
    this.editingSubjectName.set(null);
    this.editingSubjectNameValue.set('');
  }

  // Verificar si hay materia sin nombre
  hasUnnamedSubject(): boolean {
    return this.subjects().some(s => !s.name || s.name.trim() === '');
  }

  // Iniciar edición de nombre de materia (desde el tab)
  startEditingSubjectName(subject: Subject) {
    this.editingSubjectName.set(subject.id!);
    this.editingSubjectNameValue.set(subject.name || '');
  }

  // Guardar nombre de materia editado
  saveSubjectName(subjectId: number) {
    const newName = this.editingSubjectNameValue().trim();
    if (!newName) {
      alert('El nombre de la materia no puede estar vacío');
      return;
    }

    this.subjectService.updateSubject(subjectId, { name: newName }).subscribe({
      next: (updatedSubject) => {
        this.subjects.update(list => 
          list.map(s => s.id === subjectId ? updatedSubject : s)
        );
        this.editingSubjectName.set(null);
        this.editingSubjectNameValue.set('');
      },
      error: (err) => {
        console.error('Error al actualizar nombre de materia:', err);
        const errorMsg = err.error?.error || 'Error al actualizar el nombre. Por favor, intenta nuevamente.';
        alert(errorMsg);
      }
    });
  }

  // Cancelar edición de nombre
  cancelEditingSubjectName() {
    this.editingSubjectName.set(null);
    this.editingSubjectNameValue.set('');
  }

  showAddSubjectForm() {
    // Validar que no haya materia sin nombre
    if (this.hasUnnamedSubject()) {
      alert('Primero debes ponerle un nombre a la materia sin nombre antes de agregar otra.');
      return;
    }
    this.addingSubject.set(true);
    this.subjectForm.reset();
  }

  addSubject() {
    if (this.subjectForm.invalid) return;
    
    const courseId = this.courseId();
    if (!courseId) return;

    const subjectData = {
      name: this.subjectForm.value.name.trim(),
      courseId: courseId
    };

    this.subjectService.createSubject(courseId, subjectData).subscribe({
      next: (newSubject) => {
        this.subjects.update(list => [...list, newSubject]);
        this.subjectForm.reset();
        this.addingSubject.set(false);
        // Seleccionar la nueva materia
        if (newSubject.id) {
          this.activeSubjectId.set(newSubject.id);
        }
      },
      error: (err) => {
        console.error('Error al crear materia:', err);
        const errorMsg = err.error?.error || 'Error al crear la materia. Por favor, intenta nuevamente.';
        alert(errorMsg);
      }
    });
  }

  deleteSubject(subjectId: number) {
    if (!confirm('¿Estás seguro de que deseas eliminar esta materia? Esta acción no se puede deshacer.')) {
      return;
    }

    this.subjectService.deleteSubject(subjectId).subscribe({
      next: () => {
        const wasActive = this.activeSubjectId() === subjectId;
        this.subjects.update(list => list.filter(s => s.id !== subjectId));
        
        // Si se eliminó la materia activa, seleccionar otra
        if (wasActive) {
          const remaining = this.subjects();
          if (remaining.length > 0) {
            this.activeSubjectId.set(remaining[0].id!);
          } else {
            this.activeSubjectId.set(null);
          }
        }
      },
      error: (err) => {
        console.error('Error al eliminar materia:', err);
        const errorMsg = err.error?.error || 'Error al eliminar la materia. Por favor, intenta nuevamente.';
        alert(errorMsg);
      }
    });
  }

}
