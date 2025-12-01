import { Component, Input, inject, signal, computed, OnInit, OnChanges, SimpleChanges, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import ExcelJS from 'exceljs';
import { saveAs } from 'file-saver';
import { Student } from '../../../core/models/student';
import { Evaluation } from '../../../core/models/evaluation';
import { Grade } from '../../../core/models/grade';
import { EvaluationType } from '../../../core/models/evaluation-type';
import { StudentGroupedAverages } from '../../../core/models/grouped-average';
import { StudentService } from '../../../core/services/student-service';
import { EvaluationService } from '../../../core/services/evaluation-service';
import { EvaluationTypeService } from '../../../core/services/evaluation-type-service';
import { GradeService } from '../../../core/services/grade-service';
import { EmailTemplateService, EmailTemplate } from '../../../core/services/email-template-service';
import { GradeScaleService, GradeScale } from '../../../core/services/grade-scale-service';
import { SubjectService } from '../../../core/services/subject-service';
import { Subject } from '../../../core/models/subject';
import { CourseService } from '../../../core/services/course-service';
import { Course } from '../../../core/models/course';

@Component({
  selector: 'app-course-grades',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './course-grade-component.html',
  styleUrls: ['./course-grade-component.css']
})

export class CourseGradesComponent implements OnInit, OnChanges {
  @Input({ required: true }) courseId!: number;
  @Input() courseName: string = '';
  @Input() subjectId?: number; // ID de la materia para filtrar
  @Output() gradesUpdated = new EventEmitter<void>();

  ngOnChanges(changes: SimpleChanges) {
    // Si cambia el subjectId, recargar evaluaciones y notas
    if (changes['subjectId'] && !changes['subjectId'].firstChange) {
      this.loadAll();
    }
  }

  private studentService = inject(StudentService);
  private evaluationService = inject(EvaluationService);
  private evaluationTypeService = inject(EvaluationTypeService);
  private gradeService = inject(GradeService);
  private emailTemplateService = inject(EmailTemplateService);
  private gradeScaleService = inject(GradeScaleService);
  private subjectService = inject(SubjectService);
  private courseService = inject(CourseService);
  private fb = inject(FormBuilder);

  students = signal<Student[]>([]);
  evaluations = signal<Evaluation[]>([]);
  grades = signal<Grade[]>([]);
  evaluationTypes = signal<EvaluationType[]>([]);
  subjects = signal<Subject[]>([]);
  groupedAverages = signal<StudentGroupedAverages[]>([]);
  course = signal<Course | null>(null);
  loading = signal(false);
  error = signal<string | null>(null);
  errorMessage = signal<string | null>(null);
  showTypeManager = signal(false);
  emailTemplates = signal<EmailTemplate[]>([]);
  gradeScales = signal<GradeScale[]>([]);
  showEmailModal = signal(false);
  selectedEvaluationForEmail = signal<Evaluation | null>(null);
  showGradeScaleManager = signal(false);

  // Evaluaciones filtradas por materia
  filteredEvaluations = computed(() => {
    const subjectId = this.subjectId;
    const evaluations = this.evaluations();
    console.log('🔍 Filtrando evaluaciones - materia:', subjectId, 'total evaluaciones:', evaluations.length);
    if (!subjectId) {
      console.log('⚠️ No hay materia seleccionada, mostrando todas las evaluaciones');
      return evaluations;
    }
    const filtered = evaluations.filter((e: Evaluation) => e.subjectId === subjectId);
    console.log('✅ Evaluaciones filtradas por materia', subjectId, ':', filtered.length);
    console.log('📋 Evaluaciones encontradas:', filtered.map(e => ({ id: e.id, nombre: e.nombre, subjectId: e.subjectId })));
    return filtered;
  });

  // Notas filtradas por materia (solo de evaluaciones de la materia)
  filteredGrades = computed(() => {
    const subjectId = this.subjectId;
    const filteredEvalIds = new Set(this.filteredEvaluations().map((e: Evaluation) => e.id));
    const grades = this.grades();
    console.log('🔍 Filtrando notas - materia:', subjectId, 'evaluaciones filtradas:', filteredEvalIds.size, 'total notas:', grades.length);
    if (!subjectId) {
      return grades;
    }
    const filtered = grades.filter((g: Grade) => g.evaluationId && filteredEvalIds.has(g.evaluationId));
    console.log('✅ Notas filtradas por materia', subjectId, ':', filtered.length);
    return filtered;
  });

  // formulario rápido para crear una evaluación
  evalForm = this.fb.nonNullable.group({
    name: ['', Validators.required],
    date: [''],
    evaluationTypeId: [null as number | null],
    gradeScaleId: [null as number | null],
    approvalGrade: [null as number | null, [Validators.min(0), Validators.max(10)]],
    qualificationGrade: [null as number | null, [Validators.min(0), Validators.max(10)]]
  });

  // formulario para envío personalizado de emails
  emailForm = this.fb.nonNullable.group({
    templateId: [null as number | null],
    customMessage: [''],
    useTemplate: [true]
  });

  // formulario para crear tipo de evaluación
  typeForm = this.fb.nonNullable.group({
    nombre: ['', Validators.required],
    weight: [null as number | null, [Validators.min(0), Validators.max(100)]]
  });
  
  // Map para editar weight de tipos existentes
  editingTypeWeight = signal<Map<number, number | null>>(new Map());

  // carga inicial
  ngOnInit() {
    this.loadAll();
    this.loadEmailTemplates();
    this.loadGradeScales();
    this.loadSubjects();
  }

  loadAll() {
    this.loading.set(true);
    this.error.set(null);

    // cargar curso para obtener valores de aprobación y habilitación
    this.courseService.getCourseById(this.courseId).subscribe({
      next: course => {
        this.course.set(course);
        // Presetear valores en el formulario si el curso los tiene
        if (course.approvalGrade !== null && course.approvalGrade !== undefined) {
          this.evalForm.patchValue({ approvalGrade: course.approvalGrade });
        }
        if (course.qualificationGrade !== null && course.qualificationGrade !== undefined) {
          this.evalForm.patchValue({ qualificationGrade: course.qualificationGrade });
          this.promotionEnabled.set(true);
        } else {
          // Si no hay valor en el curso, por defecto está habilitado (con promoción)
          this.promotionEnabled.set(true);
          this.evalForm.patchValue({ qualificationGrade: null });
        }
      },
      error: () => console.error('Error al cargar curso')
    });

    // cargar estudiantes
    this.studentService.getStudentsByCourse(this.courseId).subscribe({
      next: s => this.students.set(s),
      error: () => this.error.set('Error al cargar alumnos')
    });
    
    // cargar tipos de evaluación
    this.evaluationTypeService.getEvaluationTypesByCourse(this.courseId).subscribe({
      next: types => this.evaluationTypes.set(types),
      error: () => this.error.set('Error al cargar tipos de evaluación')
    });
    
    // cargar evaluaciones (se filtrarán por computed)
    this.evaluationService.getEvaluationsByCourse(this.courseId).subscribe({
      next: e => {
        this.evaluations.set(e);
        this.loadGroupedAverages();
      },
      error: () => this.error.set('Error al cargar evaluaciones')
    });

    // cargar notas
    this.gradeService.getGradesByCourse(this.courseId).subscribe({
      next: g => {
        this.grades.set(g);
        this.loading.set(false);
        this.loadGroupedAverages();
      },
      error: () => {
        this.error.set('Error al cargar notas');
        this.loading.set(false);
      }
    });
  }

  loadEmailTemplates() {
    this.emailTemplateService.getEmailTemplates(true).subscribe({
      next: templates => this.emailTemplates.set(templates),
      error: () => console.error('Error al cargar templates de email')
    });
  }

  loadGradeScales() {
    this.gradeScaleService.getGradeScales(true).subscribe({
      next: scales => this.gradeScales.set(scales),
      error: () => console.error('Error al cargar escalas de notas')
    });
  }

  loadSubjects() {
    this.subjectService.getSubjectsByCourse(this.courseId).subscribe({
      next: subjects => this.subjects.set(subjects),
      error: () => console.error('Error al cargar materias')
    });
  }

  loadGroupedAverages() {
    this.gradeService.getGroupedAveragesByCourse(this.courseId).subscribe({
      next: averages => this.groupedAverages.set(averages),
      error: () => console.error('Error al cargar promedios agrupados')
    });
  }

  // helper: obtener nota existente o null (usa notas filtradas)
  getGradeStatusClass(studentId: number, evaluationId: number): string {
    const grade = this.getGrade(studentId, evaluationId);
    if (grade === '' || grade === null || grade === undefined) return '';
    
    const evaluation = this.evaluations().find(e => e.id === evaluationId);
    if (!evaluation) return '';
    
    const approvalGrade = evaluation.approvalGrade ?? this.course()?.approvalGrade ?? 6;
    const qualificationGrade = evaluation.qualificationGrade ?? this.course()?.qualificationGrade;
    
    // Si no hay nota de habilitación definida, solo hay aprobado/desaprobado
    if (qualificationGrade === null || qualificationGrade === undefined) {
      return grade >= approvalGrade ? 'grade-passed' : 'grade-failed';
    }
    
    // Con habilitación: 
    // - grade < qualificationGrade = desaprobado (rojo) - no puede rendir final
    // - qualificationGrade <= grade < approvalGrade = aprobado (verde) - debe rendir final
    // - grade >= approvalGrade = promocionado (azul) - no rinde final
    if (grade < qualificationGrade) {
      return 'grade-failed'; // Desaprobado - rojo
    } else if (grade < approvalGrade) {
      return 'grade-passed'; // Aprobado pero debe rendir final - verde
    } else {
      return 'grade-promoted'; // Promocionado - azul
    }
  }

  getGrade(studentId: number, evaluationId: number): number | '' {
    const g = this.filteredGrades().find((x: Grade) => x.studentId === studentId && x.evaluationId === evaluationId);
    return (g && g.grade !== null && g.grade !== undefined) ? g.grade : '';
  }

  // crear evaluación rápida
  addEvaluation() {
    if (this.evalForm.invalid) {
      this.error.set('Por favor completa el nombre de la evaluación.');
      return;
    }

    const nombre = this.evalForm.value.name!.trim();
    if (!nombre || nombre === '') {
      this.error.set('El nombre de la evaluación no puede estar vacío.');
      return;
    }

    const fecha = this.evalForm.value.date || new Date().toISOString().split('T')[0];
    if (!fecha) {
      this.error.set('La fecha es obligatoria.');
      return;
    }

    const payload: Omit<Evaluation, 'id'> = {
      courseId: this.courseId,
      nombre: nombre,
      date: fecha,
      tipo: 'examen', // Mantenido para compatibilidad
      evaluationTypeId: this.evalForm.value.evaluationTypeId || undefined,
      gradeScaleId: this.evalForm.value.gradeScaleId || undefined,
      subjectId: this.subjectId || undefined, // Usar subjectId del input (tab activo)
      approvalGrade: this.evalForm.value.approvalGrade !== null && this.evalForm.value.approvalGrade !== undefined 
        ? this.evalForm.value.approvalGrade 
        : (this.course()?.approvalGrade ?? null),
          qualificationGrade: this.promotionEnabled() 
            ? (this.evalForm.value.qualificationGrade !== null && this.evalForm.value.qualificationGrade !== undefined
                ? this.evalForm.value.qualificationGrade
                : (this.course()?.qualificationGrade ?? null))
            : null
    };

    this.evaluationService.addEvaluation(payload).subscribe({
      next: (created) => {
        // Recargar evaluaciones desde el servidor para asegurar que se muestren correctamente
        this.evaluationService.getEvaluationsByCourse(this.courseId).subscribe({
          next: (evaluations) => {
            this.evaluations.set(evaluations);
            this.loadGroupedAverages();
          },
          error: () => {
            // Si falla la recarga, al menos agregar la creada
            this.evaluations.update(list => [...list, created]);
          }
        });
        // Resetear formulario pero mantener valores del curso como preset
        this.evalForm.reset({
          name: '',
          date: '',
          evaluationTypeId: null,
          gradeScaleId: null,
          approvalGrade: this.course()?.approvalGrade ?? null,
          qualificationGrade: this.course()?.qualificationGrade ?? null
        });
        this.error.set(null);
      },
      error: (err) => {
        console.error('❌ Error al crear evaluación:', err);
        let errorMsg = 'No se pudo crear la evaluación';
        
        if (err.error?.error) {
          errorMsg = err.error.error;
        } else if (err.status === 400) {
          errorMsg = 'Error al crear la evaluación. Verifica los datos ingresados.';
        } else if (err.status === 0) {
          errorMsg = 'No se pudo conectar con el servidor. Verifica que el backend esté corriendo.';
        }
        
        this.error.set(errorMsg);
      }
    });
  }

  // actualizar o crear nota
  updateGrade(studentId: number, evaluationId: number, rawValue: string) {
    // Validar rango 0-10 (o 0-100 según corresponda)
    const parsed = rawValue === '' ? 0 : Number(rawValue);
    const value: number = Number.isNaN(parsed) ? 0 : parsed;
    
    // Validar rango (0-10 según el backend)
    if (value < 0 || value > 10) {
      this.error.set(`La nota debe estar entre 0 y 10. Valor ingresado: ${value}`);
      // No actualizar si está fuera de rango
      return;
    }

    // buscar existente
    const existing = this.grades().find(g => g.studentId === studentId && g.evaluationId === evaluationId);

    // preparar objeto para enviar al backend (Grade)
    const gradePayload: Grade = existing
      ? { ...existing, grade: value }
      : {
          id: undefined,
          courseId: this.courseId,
          studentId,
          evaluationId,
          grade: value
        };

    // llamar al servicio (setGrade maneja POST vs PUT)
    this.gradeService.setGrade(gradePayload).subscribe({
      next: (saved: Grade) => {
        // guardar en signal de forma tipada
        this.grades.update((list: Grade[]): Grade[] => {
          if (existing) {
            return list.map(g => g.id === saved.id ? saved : g);
          }
          return [...list, saved];
        });
        // Emitir evento para actualizar promedios
        this.gradesUpdated.emit();
        this.loadGroupedAverages();
      },
      error: () => {
        this.error.set('No se pudo guardar la nota');
      }
    });
  }

  // borrar evaluación (opcional)
  removeEvaluation(evId: number) {
    if (!confirm('¿Eliminar evaluación? Se perderán sus notas.')) return;
    this.evaluationService.deleteEvaluation(evId).subscribe({
      next: () => {
        // quitar evaluación y sus notas locales
        this.evaluations.update(list => list.filter(e => e.id !== evId));
        this.grades.update(list => list.filter(g => g.evaluationId !== evId));
        this.loadGroupedAverages();
      },
      error: () => this.error.set('No se pudo eliminar la evaluación')
    });
  }

  // enviar notas por email (método simple)
  sendGradesByEmail(evaluationId: number) {
    if (!confirm('¿Enviar las notas de esta evaluación por email a todos los alumnos?')) {
      return;
    }

    this.error.set(null);
    this.errorMessage.set(null);

    this.evaluationService.sendGradesByEmail(evaluationId).subscribe({
      next: (response) => {
        console.log('✅ Notas enviadas por email:', response);
        alert('Notas enviadas por email exitosamente a todos los alumnos.');
        // Actualizar evaluación para marcar que se enviaron
        this.evaluations.update(list => 
          list.map(e => e.id === evaluationId ? { ...e, gradesSentByEmail: true } : e)
        );
      },
      error: (err) => {
        console.error('❌ Error al enviar notas por email:', err);
        let errorMsg = 'No se pudieron enviar las notas por email';
        
        if (err.error?.error) {
          errorMsg = err.error.error;
        } else if (err.status === 404) {
          errorMsg = 'La evaluación no existe.';
        } else if (err.status === 0) {
          errorMsg = 'No se pudo conectar con el servidor. Verifica que el backend esté corriendo.';
        }
        
        this.errorMessage.set(errorMsg);
        alert('Error: ' + errorMsg);
      }
    });
  }

  // abrir modal para envío personalizado
  openEmailModal(evaluation: Evaluation) {
    this.selectedEvaluationForEmail.set(evaluation);
    this.emailForm.reset({ templateId: null, customMessage: '', useTemplate: true });
    this.showEmailModal.set(true);
  }

  // cerrar modal
  closeEmailModal() {
    this.showEmailModal.set(false);
    this.selectedEvaluationForEmail.set(null);
  }

  // enviar notas personalizado
  sendGradesCustom() {
    const evaluation = this.selectedEvaluationForEmail();
    if (!evaluation || !evaluation.id) return;

    const formValue = this.emailForm.value;
    this.evaluationService.sendGradesByEmailCustom(
      evaluation.id,
      formValue.templateId || undefined,
      formValue.customMessage || undefined,
      formValue.useTemplate ?? true
    ).subscribe({
      next: (response) => {
        alert('Emails enviados exitosamente');
        this.closeEmailModal();
        // Actualizar evaluación
        this.evaluations.update(list => 
          list.map(e => e.id === evaluation.id ? { ...e, gradesSentByEmail: true } : e)
        );
      },
      error: (err) => {
        let errorMsg = 'Error al enviar emails';
        if (err.error?.error) {
          errorMsg = err.error.error;
        }
        this.errorMessage.set(errorMsg);
        alert('Error: ' + errorMsg);
      }
    });
  }

  // verificar si se puede editar evaluación
  canEditEvaluation(evaluation: Evaluation): boolean {
    return !evaluation.gradesSentByEmail;
  }

  // Gestión de tipos de evaluación
  addEvaluationType() {
    if (this.typeForm.invalid) {
      this.error.set('Por favor completa el nombre del tipo de evaluación.');
      return;
    }

    const nombre = this.typeForm.value.nombre!.trim();
    if (!nombre || nombre === '') {
      this.error.set('El nombre del tipo de evaluación no puede estar vacío.');
      return;
    }

    const weight = this.typeForm.value.weight;
    const payload: Omit<EvaluationType, 'id'> = {
      courseId: this.courseId,
      nombre: nombre,
      weight: weight !== null && weight !== undefined ? weight : null
    };

    this.evaluationTypeService.createEvaluationType(payload).subscribe({
      next: (created) => {
        this.evaluationTypes.update(list => [...list, created]);
        this.typeForm.reset();
        this.error.set(null);
      },
      error: (err) => {
        console.error('❌ Error al crear tipo de evaluación:', err);
        let errorMsg = 'No se pudo crear el tipo de evaluación';
        
        if (err.error?.error) {
          errorMsg = err.error.error;
        } else if (err.status === 400) {
          errorMsg = 'Error al crear el tipo de evaluación. Verifica los datos ingresados.';
        } else if (err.status === 0) {
          errorMsg = 'No se pudo conectar con el servidor. Verifica que el backend esté corriendo.';
        }
        
        this.error.set(errorMsg);
      }
    });
  }

  deleteEvaluationType(typeId: number) {
    if (!confirm('¿Eliminar este tipo de evaluación? Las evaluaciones que lo usen no se eliminarán, pero perderán su agrupación.')) return;
    
    this.evaluationTypeService.deleteEvaluationType(typeId).subscribe({
      next: () => {
        this.evaluationTypes.update(list => list.filter(t => t.id !== typeId));
        // Actualizar evaluaciones para quitar el tipo eliminado
        this.evaluations.update(list => 
          list.map(e => e.evaluationTypeId === typeId ? { ...e, evaluationTypeId: undefined } : e)
        );
        this.loadGroupedAverages();
      },
      error: (err) => {
        console.error('❌ Error al eliminar tipo de evaluación:', err);
        let errorMsg = 'No se pudo eliminar el tipo de evaluación';
        
        if (err.error?.error) {
          errorMsg = err.error.error;
        }
        
        this.error.set(errorMsg);
      }
    });
  }

  getGroupedAverageForStudent(studentId: number): StudentGroupedAverages | null {
    return this.groupedAverages().find(avg => avg.studentId === studentId) || null;
  }

  toggleTypeManager() {
    this.showTypeManager.update(v => !v);
  }
  
  // Signal para controlar si está habilitada la promoción (por defecto true = con promoción)
  promotionEnabled = signal<boolean>(true);
  
  isPromotionEnabled(): boolean {
    return this.promotionEnabled();
  }
  
  getQualificationGradeValue(): string | number {
    if (!this.promotionEnabled()) {
      return '';
    }
    const formValue = this.evalForm.value.qualificationGrade;
    if (formValue !== null && formValue !== undefined) {
      return formValue;
    }
    return this.course()?.qualificationGrade ?? '';
  }
  
  togglePromotion() {
    if (this.promotionEnabled()) {
      // Desactivar promoción
      this.promotionEnabled.set(false);
      this.evalForm.patchValue({ qualificationGrade: null });
    } else {
      // Activar promoción
      this.promotionEnabled.set(true);
      // Restaurar el valor del curso si existe
      const courseQualification = this.course()?.qualificationGrade;
      if (courseQualification !== null && courseQualification !== undefined) {
        this.evalForm.patchValue({ qualificationGrade: courseQualification });
      } else {
        this.evalForm.patchValue({ qualificationGrade: null });
      }
    }
  }
  
  getAutoWeight(typeId: number): string {
    const currentType = this.evaluationTypes().find(t => t.id === typeId);
    if (!currentType) return '';
    
    // Si tiene peso asignado, no mostrar auto
    if (currentType.weight !== null && currentType.weight !== undefined) {
      return '';
    }
    
    // Calcular el peso automático (distribución igual entre los que no tienen peso)
    const typesWithoutWeight = this.evaluationTypes().filter(t => 
      t.id !== typeId && (t.weight === null || t.weight === undefined)
    );
    const typesWithWeight = this.evaluationTypes().filter(t => 
      t.id !== typeId && t.weight !== null && t.weight !== undefined
    );
    
    const sumWithWeight = typesWithWeight.reduce((sum, t) => sum + (t.weight || 0), 0);
    const remaining = 100 - sumWithWeight;
    
    if (typesWithoutWeight.length === 0) {
      // Si no hay otros sin peso, el auto sería el resto
      return String(Math.max(0, Math.round(remaining)));
    }
    
    // Distribución igual entre los que no tienen peso (incluyendo este)
    const autoWeight = remaining / (typesWithoutWeight.length + 1);
    return String(Math.max(0, Math.round(autoWeight)));
  }
  
  onApprovalGradeChange(value: string) {
    const numValue = value && value.trim() !== '' ? parseFloat(value) : null;
    this.evalForm.patchValue({ approvalGrade: numValue });
  }
  
  onQualificationGradeChange(value: string) {
    const numValue = value && value.trim() !== '' ? parseFloat(value) : null;
    this.evalForm.patchValue({ qualificationGrade: numValue });
  }
  
  getMinWeight(typeId: number): number {
    return 0;
  }
  
  getMaxWeight(typeId: number): number {
    const currentType = this.evaluationTypes().find(t => t.id === typeId);
    const otherTypes = this.evaluationTypes().filter(t => t.id !== typeId);
    const sumOtherWeights = otherTypes.reduce((sum, t) => {
      const weight = this.editingTypeWeight().get(t.id!) ?? t.weight ?? 0;
      return sum + (weight || 0);
    }, 0);
    return Math.max(0, 100 - sumOtherWeights);
  }
  
  onEvaluationTypeChange(typeId: number | null) {
    if (typeId) {
      // Buscar si hay evaluaciones previas de este tipo para cargar sus valores
      const evaluationsOfType = this.evaluations().filter(e => e.evaluationTypeId === typeId);
      if (evaluationsOfType.length > 0) {
        // Tomar los valores de la primera evaluación de este tipo
        const firstEval = evaluationsOfType[0];
        if (firstEval.approvalGrade !== null && firstEval.approvalGrade !== undefined) {
          this.evalForm.patchValue({ approvalGrade: firstEval.approvalGrade });
        } else if (this.course()?.approvalGrade !== null && this.course()?.approvalGrade !== undefined) {
          this.evalForm.patchValue({ approvalGrade: this.course()?.approvalGrade });
        }
        
        // Verificar si la evaluación previa tiene nota de promoción definida
        if (firstEval.qualificationGrade !== null && firstEval.qualificationGrade !== undefined) {
          // Si tiene nota de promoción, habilitarla y cargar el valor
          this.evalForm.patchValue({ qualificationGrade: firstEval.qualificationGrade });
          this.promotionEnabled.set(true);
        } else {
          // Si NO tiene nota de promoción definida en la evaluación previa, deshabilitarla
          this.promotionEnabled.set(false);
          this.evalForm.patchValue({ qualificationGrade: null });
        }
      } else {
        // Si no hay evaluaciones previas, usar valores del curso
        if (this.course()?.approvalGrade !== null && this.course()?.approvalGrade !== undefined) {
          this.evalForm.patchValue({ approvalGrade: this.course()?.approvalGrade });
        }
        if (this.course()?.qualificationGrade !== null && this.course()?.qualificationGrade !== undefined) {
          this.evalForm.patchValue({ qualificationGrade: this.course()?.qualificationGrade });
          this.promotionEnabled.set(true);
        } else {
          this.promotionEnabled.set(true);
          this.evalForm.patchValue({ qualificationGrade: null });
        }
      }
    }
  }
  
  updateTypeWeight(typeId: number, value: string) {
    const numValue = value === '' ? null : parseFloat(value);
    this.editingTypeWeight.update(map => {
      const newMap = new Map(map);
      newMap.set(typeId, numValue);
      return newMap;
    });
  }
  
  saveTypeWeight(typeId: number) {
    const weight = this.editingTypeWeight().get(typeId);
    const type = this.evaluationTypes().find(t => t.id === typeId);
    if (!type) return;
    
    // Solo actualizar si cambió
    if (type.weight !== weight) {
      const updated: EvaluationType = {
        ...type,
        weight: weight !== null && weight !== undefined ? weight : null
      };
      
      this.evaluationTypeService.updateEvaluationType(typeId, updated).subscribe({
        next: (updatedType) => {
          this.evaluationTypes.update(list => 
            list.map(t => t.id === typeId ? updatedType : t)
          );
          this.loadGroupedAverages(); // Recargar promedios con nuevos pesos
        },
        error: (err) => {
          console.error('Error al actualizar peso del tipo:', err);
          // Revertir cambio en el map
          this.editingTypeWeight.update(map => {
            const newMap = new Map(map);
            newMap.set(typeId, type.weight ?? null);
            return newMap;
          });
        }
      });
    }
  }

  hasGroupedAverages(): boolean {
    return this.groupedAverages().length > 0 && 
           this.groupedAverages()[0].groupedAverages.length > 0;
  }

  getTypesWithAverages(): EvaluationType[] {
    if (!this.hasGroupedAverages()) {
      return [];
    }
    const typeIds = this.groupedAverages()[0].groupedAverages.map(ga => ga.evaluationTypeId);
    return this.evaluationTypes().filter(type => typeIds.includes(type.id!));
  }

  // obtener escala de notas para una evaluación
  getGradeScaleForEvaluation(evaluation: Evaluation): GradeScale | null {
    if (!evaluation.gradeScaleId) return null;
    return this.gradeScales().find(s => s.id === evaluation.gradeScaleId) || null;
  }

  // obtener opciones de escala para una evaluación
  getGradeScaleOptions(evaluation: Evaluation): Array<{ label: string; value: string }> {
    const scale = this.getGradeScaleForEvaluation(evaluation);
    if (!scale) return [];
    return scale.options.map(opt => ({ label: opt.label, value: opt.label }));
  }

  // actualizar nota (soporte para categóricas)
  updateGradeCategorical(studentId: number, evaluationId: number, value: string) {
    const existing = this.grades().find(g => g.studentId === studentId && g.evaluationId === evaluationId);
    const gradePayload: Grade = existing
      ? { ...existing, gradeValue: value, grade: null }
      : {
          id: undefined,
          courseId: this.courseId,
          studentId,
          evaluationId,
          gradeValue: value,
          grade: null
        };

    this.gradeService.setGrade(gradePayload).subscribe({
      next: (saved: Grade) => {
        this.grades.update((list: Grade[]): Grade[] => {
          if (existing) {
            return list.map(g => g.id === saved.id ? saved : g);
          }
          return [...list, saved];
        });
        this.gradesUpdated.emit();
        this.loadGroupedAverages();
      },
      error: () => {
        this.error.set('No se pudo guardar la nota');
      }
    });
  }

  // obtener valor de nota (numérica o categórica) para mostrar
  getGradeValue(studentId: number, evaluationId: number): string {
    const g = this.grades().find(x => x.studentId === studentId && x.evaluationId === evaluationId);
    if (!g) return '';
    if (g.gradeValue) return g.gradeValue;
    if (g.grade !== null && g.grade !== undefined) return g.grade.toString();
    return '';
  }

  // exportar a Excel
  exportToExcel() {
    const students = this.students();
    const evaluations = this.evaluations();
    const grouped = this.groupedAverages();

    if (!students.length || !evaluations.length) {
      alert('No hay datos suficientes para exportar la planilla de notas.');
      return;
    }

    const workbook = new ExcelJS.Workbook();
    const sheet = workbook.addWorksheet('Notas');

    // Columnas: Alumno + una por evaluación + promedios agrupados + promedio final
    const allTypeNames = Array.from(
      new Set(
        grouped.flatMap(g => g.groupedAverages?.map(ga => ga.evaluationTypeName) || [])
      )
    );

    const evalCount = evaluations.length;
    const groupedCount = allTypeNames.length;

    const totalCols = 1 + evalCount + groupedCount + 1; // Alumno + evals + grupos + final
    const lastColLetter = String.fromCharCode(64 + totalCols);

    // Título
    sheet.mergeCells(`A1:${lastColLetter}1`);
    sheet.getCell('A1').value = `Planilla de Notas - Curso ${this.courseId}`;
    sheet.getCell('A1').font = { size: 16, bold: true };
    sheet.getCell('A1').alignment = { horizontal: 'center' };

    // Encabezados
    const header = sheet.getRow(3);
    header.getCell(1).value = 'Alumno';

    evaluations.forEach((ev, idx) => {
      const col = 2 + idx;
      const labelParts = [ev.nombre];
      if (ev.date) {
        labelParts.push(`(${ev.date})`);
      }
      header.getCell(col).value = labelParts.join(' ');
    });

    // Columnas de promedios agrupados
    allTypeNames.forEach((name, index) => {
      header.getCell(2 + evalCount + index).value = `Prom. ${name}`;
    });

    // Columna de promedio final
    header.getCell(2 + evalCount + groupedCount).value = 'Prom. Final';
    header.font = { bold: true };

    // Datos por alumno
    students.forEach((s, rowIdx) => {
      if (!s.id) return;

      const row = sheet.getRow(4 + rowIdx);
      const fullName = [s.firstName, s.lastName].filter(Boolean).join(' ');
      row.getCell(1).value = fullName || s.firstName;

      // Notas por evaluación
      evaluations.forEach((ev, colIdx) => {
        const val = this.getGradeValue(s.id!, ev.id!);
        row.getCell(2 + colIdx).value = val;
      });

      const studentGrouped = grouped.find(g => g.studentId === s.id);

      // Promedios agrupados
      allTypeNames.forEach((name, index) => {
        const gAvg = studentGrouped?.groupedAverages?.find(ga => ga.evaluationTypeName === name);
        const avg = gAvg?.average ?? null;
        const cell = row.getCell(2 + evalCount + index);
        if (avg !== null && avg !== undefined) {
          cell.value = Number(avg.toFixed(2));
        } else {
          cell.value = '';
        }
      });

      // Promedio final
      const finalAvg = studentGrouped?.finalAverage ?? null;
      const finalCell = row.getCell(2 + evalCount + groupedCount);
      if (finalAvg !== null && finalAvg !== undefined) {
        finalCell.value = Number(finalAvg.toFixed(2));
        finalCell.font = { bold: true };
      } else {
        finalCell.value = '';
      }
    });

    // Auto tamaño columnas
    sheet.columns.forEach(col => {
      let max = 10;
      col.eachCell?.({ includeEmpty: true }, cell => {
        const len = cell.value ? String(cell.value).toString().length : 10;
        if (len > max) max = len;
      });
      col.width = max + 3;
    });

    workbook.xlsx.writeBuffer().then(buffer => {
      const courseName = this.courseName || `Curso_${this.courseId}`;
      const sanitizedCourseName = courseName.replace(/[^a-zA-Z0-9]/g, '_');
      const fecha = new Date().toISOString().split('T')[0].replace(/-/g, '');
      const fileName = `Notas_${sanitizedCourseName}_${fecha}.xlsx`;
      saveAs(new Blob([buffer]), fileName);
    }).catch(err => {
      console.error('Error al generar Excel de notas:', err);
      alert('No se pudo generar el archivo de Excel de notas.');
    });
  }
  
  // Helper para parsear números (usado en templates)
  parseNumber(value: string | null): number | null {
    if (!value) return null;
    const parsed = parseFloat(value);
    return isNaN(parsed) ? null : parsed;
  }
  
  // Helper para formatear números (usado en templates)
  formatNumber(value: number | null | undefined): string {
    if (value === null || value === undefined) return '';
    return String(value);
  }
}

