/**
 * Configuración centralizada de endpoints de la API
 */
export const API_CONFIG = {
  BASE_URL: 'http://localhost:8080/api',
  
  AUTH: {
    BASE: '/auth',
    LOGIN: '/auth/login',
    REGISTER: '/auth/register',
    LOGOUT: '/auth/logout',
    ME: '/auth/me',
    VERIFY_EMAIL: '/auth/verify-email'
  },
  
  PROFESSORS: {
    BASE: '/professors',
    EMAIL_EXISTS: '/professors/email-exists'
  },
  
  COURSES: {
    BASE: '/courses',
    BY_PROFESSOR: '/courses/professor'
  },
  
  STUDENTS: {
    BASE: '/students',
    BY_COURSE: '/students/course',
    IMPORT: '/students/import'
  },
  
  EVALUATIONS: {
    BASE: '/evaluations',
    BY_COURSE: '/evaluations/course'
  },
  
  EVALUATION_TYPES: {
    BASE: '/evaluation-types',
    BY_COURSE: '/evaluation-types/course'
  },
  
  GRADES: {
    BASE: '/grades',
    BY_COURSE: '/grades/course',
    BY_EVALUATION: '/grades/evaluation',
    AVERAGES: '/grades',
    STUDENT_AVERAGE: '/grades/student',
    GROUPED_AVERAGES: '/grades/course',
    STUDENT_GROUPED_AVERAGES: '/grades/student'
  },
  
  EMAIL_TEMPLATES: {
    BASE: '/email-templates'
  },
  
  GRADE_SCALES: {
    BASE: '/grade-scales',
    AVAILABLE: '/grade-scales/evaluations'
  },
  
  COURSE_SCHEDULES: {
    BASE: '/courses',
    CURRENT_REDIRECT: '/courses/current-schedule-redirect'
  },
  
  ATTENDANCES: {
    BASE: '/attendances',
    AVERAGES: '/attendances/course',
    BULK: '/attendances/bulk'
  },
  
  SUBJECTS: {
    BASE: '/subjects',
    BY_COURSE: '/courses'
  }
};

