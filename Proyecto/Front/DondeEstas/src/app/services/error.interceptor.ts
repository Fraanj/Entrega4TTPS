import { HttpInterceptorFn, HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { catchError, throwError } from 'rxjs';
import { NotificationService } from './notificacion.services';
import { Router } from '@angular/router';

export const errorInterceptor: HttpInterceptorFn = (req, next) => {
  const notificationService = inject(NotificationService);
  const router = inject(Router);

  return next(req).pipe(
    catchError((error: HttpErrorResponse) => {
      let errorMessage = 'Ocurrió un error inesperado';
      let errorTitle = 'Error';

      // 🎯 Detectar si es un error de login/register
      const isAuthEndpoint = req.url.includes('/auth/login') || req.url.includes('/auth/register');

      switch (error.status) {
        case 400:
          errorTitle = 'Datos inválidos';
          errorMessage = error.error?.message || 'Por favor revisa los datos ingresados';
          break;

        case 401:
          // 🎯 Diferenciar login fallido vs sesión expirada
          if (isAuthEndpoint) {
            errorTitle = 'Credenciales incorrectas';
            errorMessage = error.error?.message || 'Email o contraseña incorrectos';
          } else {
            errorTitle = 'Sesión expirada';
            errorMessage = 'Tu sesión ha expirado. Por favor inicia sesión nuevamente.';
            localStorage.removeItem('accessToken');
            localStorage.removeItem('usuario');
            router.navigate(['/login']);
          }
          break;

        case 403:
          errorTitle = 'Acceso denegado';
          errorMessage = 'No tienes permisos para realizar esta acción';
          break;

        case 404:
          errorTitle = 'No encontrado';
          errorMessage = error.error?.message || 'El recurso solicitado no existe';
          break;

        case 409:
          errorTitle = 'Conflicto';
          errorMessage = error.error?.message || 'Ya existe un registro con estos datos';
          break;

        case 422:
          errorTitle = 'Validación fallida';
          errorMessage = error.error?.message || 'Los datos no cumplen con las reglas de negocio';
          break;

        case 500:
          errorTitle = 'Error del servidor';
          errorMessage = 'Ocurrió un error en el servidor. Intenta nuevamente más tarde.';
          break;

        case 0:
          errorTitle = 'Sin conexión';
          errorMessage = 'No se pudo conectar con el servidor. Verifica tu conexión a internet.';
          break;

        default:
          errorMessage = error.error?.message || error.message || errorMessage;
      }

      // Mostrar notificación
      notificationService.error(errorMessage, errorTitle);

      // Log para debugging
      if (!window.location.hostname.includes('produccion')) {
        console.error('HTTP Error:', {
          status: error.status,
          message: errorMessage,
          url: error.url,
          error: error.error
        });
      }

      return throwError(() => error);
    })
  );
};