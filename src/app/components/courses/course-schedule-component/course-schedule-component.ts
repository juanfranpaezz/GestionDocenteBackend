import { Component, Input, OnInit, OnChanges, SimpleChanges, inject, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { CourseScheduleService, CourseSchedule } from '../../../core/services/course-schedule-service';
import { SubjectService } from '../../../core/services/subject-service';
import { Subject } from '../../../core/models/subject';

@Component({
  selector: 'app-course-schedule',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './course-schedule-component.html',
  styleUrls: ['./course-schedule-component.css']
})
export class CourseScheduleComponent implements OnInit, OnChanges {
  @Input() courseId!: number;
  @Input() subjectId?: number; // ID de la materia para filtrar

  private scheduleService = inject(CourseScheduleService);
  private subjectService = inject(SubjectService);
  private fb = inject(FormBuilder);

  schedules = signal<CourseSchedule[]>([]);
  subjects = signal<Subject[]>([]);
  loading = signal(false);
  errorMessage = signal<string | null>(null);
  showForm = signal(false);
  editingSchedule = signal<CourseSchedule | null>(null);

  // Horarios filtrados por materia
  filteredSchedules = computed(() => {
    const subjectId = this.subjectId;
    if (!subjectId) return this.schedules();
    return this.schedules().filter(s => s.subjectId === subjectId);
  });

  ngOnChanges(changes: SimpleChanges) {
    // Si cambia el subjectId, recargar los horarios
    if (changes['subjectId'] && !changes['subjectId'].firstChange) {
      this.loadSchedules();
    }
  }

  daysOfWeek = [
    { value: 'MONDAY', label: 'Lunes' },
    { value: 'TUESDAY', label: 'Martes' },
    { value: 'WEDNESDAY', label: 'Miércoles' },
    { value: 'THURSDAY', label: 'Jueves' },
    { value: 'FRIDAY', label: 'Viernes' },
    { value: 'SATURDAY', label: 'Sábado' },
    { value: 'SUNDAY', label: 'Domingo' }
  ];

  scheduleForm: FormGroup = this.fb.group({
    dayOfWeek: ['', Validators.required],
    startTime: ['', Validators.required],
    endTime: ['', Validators.required]
  });

  ngOnInit() {
    this.loadSchedules();
    this.loadSubjects();
  }

  loadSubjects() {
    this.subjectService.getSubjectsByCourse(this.courseId).subscribe({
      next: (subjects) => this.subjects.set(subjects),
      error: (err) => console.error('Error al cargar materias:', err)
    });
  }

  loadSchedules() {
    this.loading.set(true);
    this.errorMessage.set(null);
    
    this.scheduleService.getSchedulesByCourse(this.courseId).subscribe({
      next: (schedules) => {
        this.schedules.set(schedules);
        this.loading.set(false);
      },
      error: (err) => {
        console.error('Error al cargar horarios:', err);
        this.errorMessage.set('Error al cargar los horarios');
        this.loading.set(false);
      }
    });
  }

  showAddForm() {
    this.editingSchedule.set(null);
    this.scheduleForm.reset();
    this.showForm.set(true);
  }

  editSchedule(schedule: CourseSchedule) {
    this.editingSchedule.set(schedule);
    this.scheduleForm.patchValue({
      dayOfWeek: schedule.dayOfWeek,
      startTime: schedule.startTime,
      endTime: schedule.endTime
    });
    this.showForm.set(true);
  }

  cancelForm() {
    this.showForm.set(false);
    this.editingSchedule.set(null);
    this.scheduleForm.reset();
  }

  saveSchedule() {
    if (this.scheduleForm.invalid) {
      this.scheduleForm.markAllAsTouched();
      return;
    }

    const formValue = this.scheduleForm.value;
    
    // Validar que la hora de fin sea mayor que la de inicio
    if (formValue.startTime >= formValue.endTime) {
      this.errorMessage.set('La hora de fin debe ser mayor que la hora de inicio');
      return;
    }

    this.loading.set(true);
    this.errorMessage.set(null);

    const scheduleData: Omit<CourseSchedule, 'id' | 'courseId'> = {
      dayOfWeek: formValue.dayOfWeek,
      startTime: formValue.startTime,
      endTime: formValue.endTime,
      subjectId: this.subjectId || undefined // Usar subjectId del input (tab activo)
    };

    if (this.editingSchedule()) {
      // Actualizar: eliminar el anterior y crear uno nuevo
      const scheduleId = this.editingSchedule()!.id!;
      this.scheduleService.deleteSchedule(this.courseId, scheduleId).subscribe({
        next: () => {
          this.scheduleService.createSchedules(this.courseId, [scheduleData]).subscribe({
            next: (created) => {
              this.loadSchedules();
              this.cancelForm();
            },
            error: (err) => {
              console.error('Error al actualizar horario:', err);
              this.errorMessage.set('Error al actualizar el horario');
              this.loading.set(false);
            }
          });
        },
        error: (err) => {
          console.error('Error al eliminar horario:', err);
          this.errorMessage.set('Error al actualizar el horario');
          this.loading.set(false);
        }
      });
    } else {
      // Crear nuevo
      this.scheduleService.createSchedules(this.courseId, [scheduleData]).subscribe({
        next: (created) => {
          this.loadSchedules();
          this.cancelForm();
        },
        error: (err) => {
          console.error('Error al crear horario:', err);
          this.errorMessage.set('Error al crear el horario');
          this.loading.set(false);
        }
      });
    }
  }

  deleteSchedule(scheduleId: number) {
    if (!confirm('¿Estás seguro de que deseas eliminar este horario?')) {
      return;
    }

    this.loading.set(true);
    this.scheduleService.deleteSchedule(this.courseId, scheduleId).subscribe({
      next: () => {
        this.loadSchedules();
      },
      error: (err) => {
        console.error('Error al eliminar horario:', err);
        this.errorMessage.set('Error al eliminar el horario');
        this.loading.set(false);
      }
    });
  }

  getDayLabel(day: string): string {
    const dayObj = this.daysOfWeek.find(d => d.value === day);
    return dayObj ? dayObj.label : day;
  }

  formatTime(time: string): string {
    // Formatear tiempo de "HH:mm" a formato legible
    return time;
  }

  calculateDuration(startTime: string, endTime: string): string {
    const start = this.timeToMinutes(startTime);
    const end = this.timeToMinutes(endTime);
    const duration = end - start;
    
    if (duration <= 0) return '0 min';
    
    const hours = Math.floor(duration / 60);
    const minutes = duration % 60;
    
    if (hours === 0) {
      return `${minutes} min`;
    } else if (minutes === 0) {
      return `${hours} h`;
    } else {
      return `${hours} h ${minutes} min`;
    }
  }

  private timeToMinutes(time: string): number {
    const [hours, minutes] = time.split(':').map(Number);
    return hours * 60 + minutes;
  }
}

