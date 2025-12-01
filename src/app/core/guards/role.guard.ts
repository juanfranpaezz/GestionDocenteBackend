import { inject } from '@angular/core';
import { Router, CanActivateFn } from '@angular/router';
import { AuthService } from '../services/auth-service';
import { Role } from '../../enums/roles';

/**
 * Guard que protege rutas basándose en el rol del usuario.
 * Requiere que el usuario esté autenticado y tenga el rol especificado.
 * 
 * Uso en rutas:
 * { 
 *   path: 'admin/route', 
 *   component: AdminComponent,
 *   canActivate: [authGuard, roleGuard],
 *   data: { roles: [Role.ADMIN] }
 * }
 */
export const roleGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  // Primero verificar autenticación
  if (!authService.isAuthenticated()) {
    router.navigate(['/auth/login'], { 
      queryParams: { returnUrl: state.url } 
    });
    return false;
  }

  // Obtener roles requeridos de la configuración de la ruta
  const requiredRoles = route.data?.['roles'] as Role[];
  
  if (!requiredRoles || requiredRoles.length === 0) {
    // Si no se especifican roles, permitir acceso a cualquier usuario autenticado
    return true;
  }

  // Obtener el rol del usuario actual
  const currentUser = authService.currentProfessor();
  const userRole = currentUser?.role;

  if (!userRole) {
    // Usuario sin rol asignado, denegar acceso y redirigir según rol por defecto
    router.navigate(['/auth/login']);
    return false;
  }

  // Verificar si el usuario tiene alguno de los roles requeridos
  if (requiredRoles.includes(userRole)) {
    return true;
  }

  // Usuario no tiene el rol requerido, redirigir según su rol
  if (userRole === Role.ADMIN) {
    router.navigate(['/professors/list']); // Admin va a lista de profesores
  } else {
    router.navigate(['/course/list']); // Profesor va a sus cursos
  }
  return false;
};

