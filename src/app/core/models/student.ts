export interface Student {
  id?: number;
  firstName: string;
  lastName?: string;
  cel?: string;
  email?: string;
  document?: string; // DNI o Legajo
  courseId: number; // a qué curso pertenece
}
export type StudentCreate = Omit<Student, 'id'>;
