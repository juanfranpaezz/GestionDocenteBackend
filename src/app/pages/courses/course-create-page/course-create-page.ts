import { Component, inject, signal, ViewChild, ElementRef } from '@angular/core';
import { FormBuilder, Validators, ReactiveFormsModule } from '@angular/forms';
import { CourseService } from '../../../core/services/course-service';
import { StudentService } from '../../../core/services/student-service';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../../core/services/auth-service';
import { Course } from '../../../core/models/course';
import { StudentCreate } from '../../../core/models/student';
import { Router } from '@angular/router';
import ExcelJS from 'exceljs';

@Component({
  standalone: true,
  selector: 'app-course-create-page',
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './course-create-page.html',
  styleUrls: ['./course-create-page.css']
})
export class CourseCreatePage {

  public errorMessage = signal<string>('');
  public successMessage = signal<string>('');
  public submitted = signal(false);
  public importing = signal(false);
  public selectedFileName = signal<string | null>(null);

  @ViewChild('fileInput') fileInput!: ElementRef<HTMLInputElement>;

  private fb = inject(FormBuilder);
  private courseService = inject(CourseService);
  private studentService = inject(StudentService);
  private authService = inject(AuthService);
  private router = inject(Router);

 form = this.fb.nonNullable.group({
  name: ['', [Validators.required]],
  school: ['', [Validators.required]],
  description: [''],
  approvalGrade: [null as number | null, [Validators.min(0), Validators.max(10)]],
  qualificationGrade: [null as number | null, [Validators.min(0), Validators.max(10)]]
});

onSubmit() {
  this.submitted.set(true);
  this.errorMessage.set('');
  this.successMessage.set('');

  if (this.form.invalid) {
    this.errorMessage.set('Por favor completa todos los campos obligatorios.');
    return;
  }

  const professor = this.authService.getLoggedProfessor();
  if (!professor) {
    this.errorMessage.set('No hay sesión activa. Por favor inicia sesión.');
    return;
  }

  // Verificar que hay token JWT
  const token = this.authService.getToken();
  if (!token) {
    this.errorMessage.set('No hay token de autenticación. Por favor inicia sesión nuevamente.');
    return;
  }

  console.log('Creando curso con profesor ID:', professor.id);
  console.log('Token JWT presente:', !!token);

  // El backend obtiene el professorId del JWT automáticamente,
  // pero lo incluimos por si acaso (el backend lo validará)
  const newCourse: Course = {
    ...this.form.getRawValue(),
    professorId: professor.id!
  };

  this.courseService.createCourse(newCourse).subscribe({
    next: (createdCourse) => {
      this.successMessage.set('Curso creado exitosamente');
      this.errorMessage.set('');
      
      // Si hay un archivo Excel seleccionado, importar estudiantes
      const file = this.fileInput?.nativeElement?.files?.[0];
      if (file && createdCourse.id) {
        this.importStudentsFromExcel(file, createdCourse.id);
      } else {
        // Limpiar mensaje de éxito después de 3 segundos y navegar
        setTimeout(() => {
          this.router.navigate(['/course/list']);
        }, 1500);
      }
    },
    error: (err) => {
      console.error('Error al crear curso:', err);
      let errorMsg = 'Error al guardar el curso';
      
      // Mostrar detalles del error si están disponibles
      if (err.error?.error) {
        errorMsg = err.error.error;
      } else if (err.error?.mensaje) {
        errorMsg = err.error.mensaje;
      } else if (err.error?.campos) {
        // Si hay errores de validación por campo, mostrarlos
        const campos = Object.values(err.error.campos).join(', ');
        errorMsg = `Error de validación: ${campos}`;
      } else if (err.message) {
        errorMsg = err.message;
      } else if (err.status === 401) {
        errorMsg = 'No estás autenticado. Por favor inicia sesión nuevamente.';
      } else if (err.status === 403) {
        errorMsg = 'No tienes permisos para realizar esta acción.';
      } else if (err.status === 0) {
        errorMsg = 'No se pudo conectar con el servidor. Verifica que el backend esté corriendo.';
      }
      
      this.errorMessage.set(errorMsg);
      this.successMessage.set('');
    }
  });
}

openFilePicker() {
  this.fileInput?.nativeElement?.click();
}

onFileSelected(event: Event) {
  const input = event.target as HTMLInputElement;
  const file = input.files?.[0];
  if (file) {
    this.selectedFileName.set(file.name);
  } else {
    this.selectedFileName.set(null);
  }
}

// Función para validar formato de email
private isValidEmail(email: string): boolean {
  if (!email || email.trim() === '') {
    return false;
  }
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  return emailRegex.test(email.trim());
}

async importStudentsFromExcel(file: File, courseId: number) {
  this.importing.set(true);
  this.errorMessage.set('');
  
  try {
    const workbook = new ExcelJS.Workbook();
    await workbook.xlsx.load(await file.arrayBuffer());
    
    const worksheet = workbook.getWorksheet(1); // Primera hoja
    if (!worksheet) {
      throw new Error('El archivo Excel no contiene hojas');
    }
    
    const students: StudentCreate[] = [];
    const errors: string[] = [];
    let headerRow: string[] = [];
    let isFirstRow = true;
    
    // Función para obtener valor de celda
    const getCellValue = (row: ExcelJS.Row, col: number): string => {
      const cell = row.getCell(col);
      if (!cell || cell.value === null || cell.value === undefined) {
        return '';
      }
      // Si es un objeto con richText, obtener el texto
      if (typeof cell.value === 'object' && 'richText' in cell.value) {
        return (cell.value as any).richText.map((rt: any) => rt.text).join('');
      }
      return String(cell.value).trim();
    };
    
    // Buscar índices de columnas requeridas (índices de columna Excel, 1-based)
    let firstNameCol = -1;
    let lastNameCol = -1;
    let emailCol = -1;
    let celCol = -1;
    let docCol = -1;
    
    worksheet.eachRow((row, rowNumber) => {
      if (isFirstRow) {
        // Primera fila: encabezados - buscar solo las columnas que necesitamos
        const maxCol = row.cellCount;
        for (let col = 1; col <= maxCol; col++) {
          const value = getCellValue(row, col);
          if (value) {
            const lowerValue = value.toLowerCase();
            // Buscar columnas específicas (ignorar el resto)
            if (lowerValue.includes('nombre') && !lowerValue.includes('apellido') && firstNameCol === -1) {
              firstNameCol = col;
            } else if ((lowerValue.includes('apellido') || lowerValue.includes('lastname')) && lastNameCol === -1) {
              lastNameCol = col;
            } else if (lowerValue.includes('email') && emailCol === -1) {
              emailCol = col;
            } else if ((lowerValue.includes('cel') || lowerValue.includes('telefono') || lowerValue.includes('tel')) && celCol === -1) {
              celCol = col;
            } else if ((lowerValue.includes('documento') || lowerValue.includes('dni') || lowerValue.includes('doc')) && docCol === -1) {
              docCol = col;
            }
            headerRow.push(value);
          }
        }
        
        // Validar que existan las columnas obligatorias
        if (firstNameCol === -1) {
          throw new Error('No se encontró la columna "Nombre" en el archivo Excel');
        }
        if (lastNameCol === -1) {
          throw new Error('No se encontró la columna "Apellido" en el archivo Excel');
        }
        if (emailCol === -1) {
          throw new Error('No se encontró la columna "Email" en el archivo Excel');
        }
        
        isFirstRow = false;
        return;
      }
      
      // Procesar filas de datos - solo leer las columnas que necesitamos (ignorar el resto)
      const firstName = firstNameCol > 0 ? getCellValue(row, firstNameCol) : '';
      const lastName = lastNameCol > 0 ? getCellValue(row, lastNameCol) : '';
      const email = emailCol > 0 ? getCellValue(row, emailCol) : '';
      const cel = celCol > 0 ? getCellValue(row, celCol) : '';
      const document = docCol > 0 ? getCellValue(row, docCol) : '';
      
      // Validar campos obligatorios
      if (!firstName || firstName.trim() === '') {
        errors.push(`Fila ${rowNumber}: Falta el nombre`);
        return;
      }
      
      if (!lastName || lastName.trim() === '') {
        errors.push(`Fila ${rowNumber}: Falta el apellido`);
        return;
      }
      
      if (!email || email.trim() === '') {
        errors.push(`Fila ${rowNumber}: Falta el email`);
        return;
      }
      
      // Validar formato de email
      if (!this.isValidEmail(email)) {
        errors.push(`Fila ${rowNumber}: El email "${email}" no tiene un formato válido`);
        return;
      }
      
      // Si pasa todas las validaciones, agregar estudiante
      students.push({
        firstName: firstName.trim(),
        lastName: lastName.trim(),
        email: email.trim(),
        cel: cel || undefined,
        document: document || undefined,
        courseId
      });
    });
    
    if (students.length === 0) {
      throw new Error('No se encontraron estudiantes válidos en el archivo Excel');
    }
    
    // Mostrar errores si los hay
    if (errors.length > 0) {
      const errorMsg = `Se importaron ${students.length} estudiante(s), pero hubo ${errors.length} error(es):\n\n${errors.slice(0, 20).join('\n')}${errors.length > 20 ? `\n\n... y ${errors.length - 20} error(es) más` : ''}`;
      console.warn('Errores de importación:', errors);
      // Mostrar en un alert más legible
      alert(errorMsg);
    }
    
    // Importar estudiantes
    this.studentService.importStudents(courseId, students).subscribe({
      next: (response) => {
        let successMsg = `Curso creado exitosamente. ${response.count} estudiante(s) importado(s).`;
        if (errors.length > 0) {
          successMsg += ` ${errors.length} fila(s) no se pudieron importar (ver detalles en la consola).`;
        }
        this.successMessage.set(successMsg);
        this.importing.set(false);
        setTimeout(() => {
          this.router.navigate(['/course/list']);
        }, errors.length > 0 ? 4000 : 2000);
      },
      error: (err) => {
        console.error('Error al importar estudiantes:', err);
        this.errorMessage.set('Curso creado, pero hubo un error al importar estudiantes: ' + 
          (err.error?.error || err.message || 'Error desconocido'));
        this.importing.set(false);
        setTimeout(() => {
          this.router.navigate(['/course/list']);
        }, 3000);
      }
    });
    
  } catch (error: any) {
    console.error('Error al procesar archivo Excel:', error);
    this.errorMessage.set('Error al procesar el archivo Excel: ' + (error.message || 'Error desconocido'));
    this.importing.set(false);
    setTimeout(() => {
      this.router.navigate(['/course/list']);
    }, 3000);
  }
}

}
