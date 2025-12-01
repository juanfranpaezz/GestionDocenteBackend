export interface EvaluationType {
  id?: number;
  nombre: string;
  courseId: number;
  weight?: number | null; // Porcentaje de peso para el promedio (0-100), null = peso igual
}

export type EvaluationTypeCreate = Omit<EvaluationType, 'id'>;

