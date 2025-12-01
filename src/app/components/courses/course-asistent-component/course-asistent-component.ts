import { Component, computed, input, OnInit, effect, signal, inject, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import ExcelJS from 'exceljs';
import { saveAs } from 'file-saver';

import { Student } from '../../../core/models/student';
import { Present } from '../../../core/models/present';
import { PresentService } from '../../../core/services/present-service';
import { SubjectService } from '../../../core/services/subject-service';
import { Subject } from '../../../core/models/subject';

@Component({
  selector: 'app-course-attendance',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './course-asistent-component.html',
  styleUrls: ['./course-asistent-component.css']
})
export class CourseAttendanceComponent implements OnInit {

  // ─────────────────────────────────────────────────────────────
  // INPUTS
  // ─────────────────────────────────────────────────────────────
  courseId = input.required<number>();
  students = input.required<Student[]>();
  courseName = input<string>('');
  subjectId = input<number | undefined>(); // ID de la materia para filtrar

  // ─────────────────────────────────────────────────────────────
  // OUTPUTS
  // ─────────────────────────────────────────────────────────────
  @Output() attendancesUpdated = new EventEmitter<void>();

  private subjectIdInitialized = false;

  constructor() {
    // Efecto para recargar asistencias cuando cambia el subjectId (después del primer cambio)
    effect(() => {
      const subjectId = this.subjectId();
      if (this.subjectIdInitialized && subjectId !== undefined) {
        // Recargar datos cuando cambia la materia (solo después del primer cambio)
        this.cargarDatos();
      }
      this.subjectIdInitialized = true;
    });
  }

  // ─────────────────────────────────────────────────────────────
  // Servicios
  // ─────────────────────────────────────────────────────────────
  private presentService = inject(PresentService);
  private subjectService = inject(SubjectService);

  // ─────────────────────────────────────────────────────────────
  // Estado interno
  // ─────────────────────────────────────────────────────────────
  registros = signal<Present[]>([]);
  subjects = signal<Subject[]>([]);
  selectedDate = signal<string>(new Date().toISOString().split('T')[0]); // Fecha seleccionada para marcar asistencias
  pendingAttendances = signal<Map<number, boolean>>(new Map()); // Asistencias pendientes de guardar para la fecha seleccionada
  saving = signal(false);

  // Registros filtrados por materia
  filteredRegistros = computed(() => {
    const subjectId = this.subjectId();
    const registros = this.registros();
    console.log('🔍 Filtrando registros - materia:', subjectId, 'total registros:', registros.length);
    if (!subjectId) {
      console.log('⚠️ No hay materia seleccionada, mostrando todas las asistencias');
      return registros;
    }
    const filtered = registros.filter(r => r.subjectId === subjectId);
    console.log('✅ Registros filtrados por materia', subjectId, ':', filtered.length);
    return filtered;
  });

  // ─────────────────────────────────────────────────────────────
  // Computed reactivos
  // ─────────────────────────────────────────────────────────────

  fechas = computed(() =>
    [...new Set(this.filteredRegistros().map(r => r.date))].sort()
  );

  presenteMap = computed(() => {
    const map = new Map<string, Map<number, boolean>>();

    // Aplicar los registros guardados (filtrados por materia)
    this.filteredRegistros().forEach(r => {
      if (!map.has(r.date)) map.set(r.date, new Map());
      map.get(r.date)!.set(r.studentId, r.present);
    });

    return map;
  });

  // Asistencias para la fecha seleccionada (combinando guardadas y pendientes)
  attendancesForSelectedDate = computed(() => {
    const map = new Map<number, boolean>();
    const fecha = this.selectedDate();
    
    // Primero cargar las asistencias guardadas para esta fecha
    this.registros()
      .filter(r => r.date === fecha)
      .forEach(r => {
        map.set(r.studentId, r.present);
      });
    
    // Luego aplicar los cambios pendientes (sobrescriben los guardados)
    this.pendingAttendances().forEach((present, studentId) => {
      map.set(studentId, present);
    });
    
    return map;
  });

  hasPendingChanges = computed(() => this.pendingAttendances().size > 0);

  porcentajePorAlumno = computed(() => {
    const total = this.fechas().length;
    const map = new Map<number, number>();
    if (total === 0) return map;

    for (const s of this.students()) {
      if (!s.id) continue;
      let presentes = 0;
      for (const f of this.fechas()) {
        if (this.presenteMap().get(f)?.get(s.id)) presentes++;
      }
      map.set(s.id, Math.round((presentes / total) * 100));
    }

    return map;
  });

  totalPresentesPorFecha = computed(() => {
    const map = new Map<string, number>();

    for (const f of this.fechas()) {
      let count = 0;
      for (const s of this.students()) {
        if (s.id && this.presenteMap().get(f)?.get(s.id)) count++;
      }
      map.set(f, count);
    }

    return map;
  });

  hoyFormateado = computed(() =>
    new Date().toLocaleDateString('es-ES', {
      weekday: 'long',
      day: 'numeric',
      month: 'long',
      year: 'numeric'
    })
  );

  // ─────────────────────────────────────────────────────────────
  // INIT
  // ─────────────────────────────────────────────────────────────

  ngOnInit(): void {
    this.cargarDatos();
    this.loadSubjects();
  }

  private loadSubjects() {
    const cid = this.courseId();
    this.subjectService.getSubjectsByCourse(cid).subscribe({
      next: subjects => {
        this.subjects.set(subjects);
      },
      error: err => console.error('Error al cargar materias:', err)
    });
  }

  private cargarDatos() {
    const cid = this.courseId();
    const subjectId = this.subjectId();

    console.log('📚 Cargando asistencias - curso:', cid, 'materia:', subjectId);

    this.presentService.getAttendancesByCourse(cid).subscribe({
      next: data => {
        console.log('✅ Asistencias recibidas (total):', data.length);
        console.log('✅ Asistencias con subjectId:', data.filter(r => r.subjectId).length);
        if (subjectId) {
          const filtered = data.filter(r => r.subjectId === subjectId);
          console.log('✅ Asistencias filtradas por materia', subjectId, ':', filtered.length);
        }
        this.registros.set(data);
        // Inicializar asistencias para la fecha seleccionada
        this.inicializarAsistenciasFecha();
      },
      error: err => {
        console.error('❌ Error al cargar asistencias:', err);
        // Aún así inicializar con valores por defecto
        this.inicializarAsistenciasFecha();
      }
    });
  }

  private inicializarAsistenciasFecha(): void {
    const fecha = this.selectedDate();
    const asistenciasExistentes = new Map<number, boolean>();
    
    // Cargar asistencias guardadas para la fecha seleccionada (filtradas por materia)
    this.filteredRegistros()
      .filter(r => r.date === fecha)
      .forEach(r => {
        asistenciasExistentes.set(r.studentId, r.present);
      });
    
    // Si no hay asistencias guardadas, inicializar todas como presentes por defecto
    if (asistenciasExistentes.size === 0) {
      this.students().forEach(student => {
        if (student.id) {
          asistenciasExistentes.set(student.id, true);
        }
      });
    }
    
    this.pendingAttendances.set(asistenciasExistentes);
  }

  // ─────────────────────────────────────────────────────────────
  // MÉTODOS PÚBLICOS — visibles desde el template
  // ─────────────────────────────────────────────────────────────

  public onDateChange(newDate: string): void {
    // Al cambiar la fecha, cargar las asistencias existentes para esa fecha
    this.selectedDate.set(newDate);
    
    // Cargar asistencias guardadas para la nueva fecha (filtradas por materia)
    const asistenciasExistentes = new Map<number, boolean>();
    this.filteredRegistros()
      .filter(r => r.date === newDate)
      .forEach(r => {
        asistenciasExistentes.set(r.studentId, r.present);
      });
    
    // Si no hay asistencias guardadas, inicializar todas como presentes por defecto
    if (asistenciasExistentes.size === 0) {
      this.students().forEach(student => {
        if (student.id) {
          asistenciasExistentes.set(student.id, true);
        }
      });
    }
    
    this.pendingAttendances.set(asistenciasExistentes);
  }


  public toggleAsistencia(studentId: number, checked: boolean): void {
    // Actualizar el mapa de asistencias pendientes para la fecha seleccionada
    this.pendingAttendances.update(prev => {
      const newMap = new Map(prev);
      newMap.set(studentId, checked);
      return newMap;
    });
  }

  public marcarTodos(present: boolean): void {
    // Marcar todos los estudiantes como presentes o ausentes
    this.pendingAttendances.update(prev => {
      const newMap = new Map(prev);
      this.students().forEach(student => {
        if (student.id) {
          newMap.set(student.id, present);
        }
      });
      return newMap;
    });
  }

  public cargarAsistencias(): void {
    if (this.pendingAttendances().size === 0) {
      alert('No hay cambios para guardar');
      return;
    }

    if (!confirm('¿Guardar las asistencias de esta fecha?')) {
      return;
    }

    this.saving.set(true);
    const fecha = this.selectedDate();
    const cid = this.courseId();
    
    // Crear array de asistencias a guardar (usar subjectId del input - tab activo)
    const subjectId = this.subjectId();
        const asistencias: Present[] = Array.from(this.pendingAttendances().entries()).map(([studentId, present]) => ({
      courseId: cid,
      studentId: studentId,
      date: fecha,
      present: present,
      subjectId: subjectId || undefined // Usar subjectId del input (tab activo)
    }));

    this.presentService.saveAttendancesBulk(asistencias).subscribe({
      next: (guardados) => {
        // Recargar asistencias desde el servidor para asegurar filtrado correcto por materia
        this.cargarDatos();
        
        // Emitir evento para que el componente padre refresque los promedios de asistencia
        this.attendancesUpdated.emit();
        
        // Limpiar cambios pendientes
        this.pendingAttendances.set(new Map());
        this.saving.set(false);
        alert('Asistencias guardadas exitosamente');
      },
      error: (err) => {
        console.error('Error al guardar asistencias:', err);
        alert('Error al guardar las asistencias. Por favor, intenta nuevamente.');
        this.saving.set(false);
      }
    });
  }

  public cancelarCambios(): void {
    if (confirm('¿Deseas descartar todos los cambios sin guardar?')) {
      this.pendingAttendances.set(new Map());
    }
  }

  public formatearFecha(iso: string): string {
    // Parsear la fecha manualmente para evitar problemas de zona horaria
    const [year, month, day] = iso.split('-').map(Number);
    const date = new Date(year, month - 1, day); // month es 0-indexed
    return date.toLocaleDateString('es-ES', {
      weekday: 'short',
      day: '2-digit',
      month: 'short'
    }).replace('.', '');
  }

  // ─────────────────────────────────────────────────────────────
  // Exportar Excel
  // ─────────────────────────────────────────────────────────────

  public async exportarExcel(): Promise<void> {
    const workbook = new ExcelJS.Workbook();
    const sheet = workbook.addWorksheet('Asistencias');

    const numFechas = this.fechas().length;
    const lastCol = String.fromCharCode(65 + numFechas + 1);
    
    sheet.mergeCells('A1', `${lastCol}1`);
    sheet.getCell('A1').value = `Asistencias - Curso ${this.courseId()}`;
    sheet.getCell('A1').font = { size: 16, bold: true };
    sheet.getCell('A1').alignment = { horizontal: 'center' };

    // Cabeceras
    const header = sheet.getRow(3);
    header.getCell(1).value = 'Alumno';

    this.fechas().forEach((f, i) => {
      header.getCell(2 + i).value = this.formatearFecha(f);
    });

    header.getCell(2 + numFechas).value = '% Asistencia';
    header.font = { bold: true };

    // Datos
    this.students().forEach((s, rowIdx) => {
      if (!s.id) return;
      
      const row = sheet.getRow(4 + rowIdx);
      row.getCell(1).value = s.firstName;

      this.fechas().forEach((f, colIdx) => {
        const presente = this.presenteMap().get(f)?.get(s.id!) ?? false;
        const cell = row.getCell(2 + colIdx);
        cell.value = presente ? 'P' : 'A';
        cell.fill = {
          type: 'pattern',
          pattern: 'solid',
          fgColor: { argb: presente ? 'FFCCFFCC' : 'FFFFCCCC' }
        };
      });

      const porc = this.porcentajePorAlumno().get(s.id!) ?? 0;
      const cell = row.getCell(2 + numFechas);
      cell.value = `${porc}%`;
      cell.font = {
        bold: true,
        color: { argb: porc >= 90 ? 'FF006400' : porc >= 75 ? 'FF8B8000' : 'FFDC143C' }
      };
    });

    // Auto tamaño columnas
    sheet.columns.forEach(col => {
      let max = 10;
      col.eachCell?.({ includeEmpty: true }, cell => {
        const len = cell.value ? String(cell.value).length : 10;
        if (len > max) max = len;
      });
      col.width = max + 3;
    });

    const buffer = await workbook.xlsx.writeBuffer();
    const courseName = this.courseName() || `Curso_${this.courseId()}`;
    const sanitizedCourseName = courseName.replace(/[^a-zA-Z0-9]/g, '_');
    const fecha = new Date().toISOString().split('T')[0].replace(/-/g, '');
    const fileName = `Asistencias_${sanitizedCourseName}_${fecha}.xlsx`;
    saveAs(new Blob([buffer]), fileName);
  }
}
