export interface Grade 
{
  id?: number;
  courseId: number;
  studentId: number;
  evaluationId: number;
  grade?: number | null; // Opcional si hay gradeValue (notas categóricas)
  gradeValue?: string | null; // Para notas categóricas (ej: "aprobado", "distinguido")
}

