import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { AuthService } from '../services/auth-service';

/**
 * Interceptor HTTP que agrega automáticamente el token JWT
 * a todas las peticiones HTTP que requieren autenticación.
 */
export const jwtInterceptor: HttpInterceptorFn = (req, next) => {
  const authService = inject(AuthService);
  const token = authService.getToken();

  // Log para debugging
  console.log('🔐 JWT Interceptor - URL:', req.url);
  console.log('🔐 JWT Interceptor - Token exists:', !!token);
  if (token) {
    console.log('🔐 JWT Interceptor - Token (first 20 chars):', token.substring(0, 20) + '...');
  }

  // Si hay token, agregarlo al header Authorization
  if (token) {
    const clonedRequest = req.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`
      }
    });
    console.log('🔐 JWT Interceptor - Authorization header added');
    return next(clonedRequest);
  }

  // Si no hay token, continuar con la petición original
  console.warn('⚠️ JWT Interceptor - No token found, request sent without Authorization header');
  return next(req);
};

