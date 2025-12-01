export interface Course 
{
  id?: number;
  name: string;
  school: string;
  description?: string;
  professorId?: number;
  archived?: boolean;
  archivedDate?: string; // ISO date string
  approvalGrade?: number | null; // Nota mínima para aprobar (default del curso)
  qualificationGrade?: number | null; // Nota mínima para habilitar (default del curso)
}
