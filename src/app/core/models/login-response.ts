import { Professor } from './professor';

/**
 * Respuesta del endpoint de login
 */
export interface LoginResponse {
  token: string;
  professor: Professor;
}

