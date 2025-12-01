export interface Subject {
  id?: number;
  name: string | null | undefined; // Permite null para materia default sin nombre
  courseId: number;
}

