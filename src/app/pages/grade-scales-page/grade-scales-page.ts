import { Component, inject, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators, FormArray } from '@angular/forms';
import { Router } from '@angular/router';
import { GradeScaleService, GradeScale, GradeScaleOption } from '../../core/services/grade-scale-service';

@Component({
  selector: 'app-grade-scales-page',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './grade-scales-page.html',
  styleUrls: ['./grade-scales-page.css']
})
export class GradeScalesPage implements OnInit {
  private gradeScaleService = inject(GradeScaleService);
  private router = inject(Router);
  private fb = inject(FormBuilder);

  gradeScales = signal<GradeScale[]>([]);
  loading = signal(false);
  errorMessage = signal<string | null>(null);
  showCreateForm = signal(false);
  editingScale = signal<GradeScale | null>(null);

  scaleForm: FormGroup;

  constructor() {
    this.scaleForm = this.fb.group({
      name: ['', Validators.required],
      options: this.fb.array([])
    });
  }

  ngOnInit() {
    this.loadGradeScales();
  }

  get optionsArray(): FormArray {
    return this.scaleForm.get('options') as FormArray;
  }

  loadGradeScales() {
    this.loading.set(true);
    this.errorMessage.set(null);
    
    this.gradeScaleService.getGradeScales(false).subscribe({
      next: (scales) => {
        this.gradeScales.set(scales);
        this.loading.set(false);
      },
      error: (err) => {
        console.error('Error cargando escalas de notas:', err);
        this.errorMessage.set('Error al cargar las escalas de notas.');
        this.loading.set(false);
      }
    });
  }

  openCreateForm() {
    this.editingScale.set(null);
    this.scaleForm.reset({
      name: '',
      options: []
    });
    this.optionsArray.clear();
    this.addOption();
    this.showCreateForm.set(true);
  }

  openEditForm(scale: GradeScale) {
    this.editingScale.set(scale);
    this.scaleForm.patchValue({
      name: scale.name
    });
    
    this.optionsArray.clear();
    if (scale.options && scale.options.length > 0) {
      scale.options.forEach(opt => {
        this.addOption(opt);
      });
    } else {
      this.addOption();
    }
    
    this.showCreateForm.set(true);
  }

  closeForm() {
    this.showCreateForm.set(false);
    this.editingScale.set(null);
    this.scaleForm.reset();
    this.optionsArray.clear();
  }

  addOption(option?: GradeScaleOption) {
    const optionGroup = this.fb.group({
      label: [option?.label || '', Validators.required],
      useNumericValue: [option?.numericValue !== null && option?.numericValue !== undefined],
      numericValue: [option?.numericValue || null],
      order: [option?.order ?? this.optionsArray.length + 1, Validators.required]
    });
    this.optionsArray.push(optionGroup);
  }

  removeOption(index: number) {
    this.optionsArray.removeAt(index);
    // Reordenar
    this.optionsArray.controls.forEach((control, i) => {
      control.patchValue({ order: i + 1 });
    });
  }

  moveOptionUp(index: number) {
    if (index > 0) {
      const options = this.optionsArray.controls;
      const temp = options[index];
      options[index] = options[index - 1];
      options[index - 1] = temp;
      
      // Actualizar orden
      options.forEach((control, i) => {
        control.patchValue({ order: i + 1 });
      });
    }
  }

  moveOptionDown(index: number) {
    if (index < this.optionsArray.length - 1) {
      const options = this.optionsArray.controls;
      const temp = options[index];
      options[index] = options[index + 1];
      options[index + 1] = temp;
      
      // Actualizar orden
      options.forEach((control, i) => {
        control.patchValue({ order: i + 1 });
      });
    }
  }

  saveScale() {
    if (this.scaleForm.invalid) {
      this.scaleForm.markAllAsTouched();
      return;
    }

    const formValue = this.scaleForm.value;
    const scaleData: Omit<GradeScale, 'id'> = {
      name: formValue.name,
      isGlobal: false, // Siempre false - no se permiten escalas globales
      options: formValue.options.map((opt: any, index: number) => ({
        label: opt.label,
        numericValue: opt.useNumericValue ? (opt.numericValue || null) : null,
        order: opt.order || index + 1
      }))
    };

    if (this.editingScale()?.id) {
      // Actualizar
      this.gradeScaleService.updateGradeScale(this.editingScale()!.id!, scaleData).subscribe({
        next: () => {
          this.loadGradeScales();
          this.closeForm();
        },
        error: (err) => {
          console.error('Error actualizando escala:', err);
          this.errorMessage.set('Error al actualizar la escala de notas.');
        }
      });
    } else {
      // Crear
      this.gradeScaleService.createGradeScale(scaleData).subscribe({
        next: () => {
          this.loadGradeScales();
          this.closeForm();
        },
        error: (err) => {
          console.error('Error creando escala:', err);
          this.errorMessage.set('Error al crear la escala de notas.');
        }
      });
    }
  }

  deleteScale(id: number) {
    if (!confirm('¿Estás seguro de que deseas eliminar esta escala de notas? Esta acción no se puede deshacer.')) {
      return;
    }

    this.gradeScaleService.deleteGradeScale(id).subscribe({
      next: () => {
        this.loadGradeScales();
      },
      error: (err) => {
        console.error('Error eliminando escala:', err);
        this.errorMessage.set('Error al eliminar la escala de notas.');
      }
    });
  }

  goBack() {
    this.router.navigate(['/course/list']);
  }
}

