export interface Present {
  id?: number;
  date: string;
  present: boolean;
  courseId: number;
  studentId: number;
  subjectId?: number; // ID de la materia (opcional)
}

