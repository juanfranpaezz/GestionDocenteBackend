export interface Evaluation 
{
    id?: number;
    courseId: number;
    nombre: string;       // Ej: "Parcial 1", "TP", etc.
    date: string;         // Fecha en formato ISO string
    tipo: string;         // Ej: "examen", "práctica", "tarea" (mantenido para compatibilidad)
    evaluationTypeId?: number; // ID del tipo de evaluación agrupado (opcional)
    gradesSentByEmail?: boolean;
    customMessage?: string;
    gradeScaleId?: number; // ID de la escala de notas (null = numérico)
    subjectId?: number; // ID de la materia (opcional)
    approvalGrade?: number | null; // Nota mínima para aprobar (opcional, sobrescribe default del curso)
    qualificationGrade?: number | null; // Nota mínima para habilitar (opcional, sobrescribe default del curso)
}
export type EvaluationCreate = Omit<Evaluation, 'id'>;
