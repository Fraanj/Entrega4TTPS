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

      const isAuthEndpoint = req.url.includes('/auth/login') || req.url.includes('/auth/register');

      const getBackendMessage = (error: HttpErrorResponse): string | null => {
        if (typeof error.error === 'string') return error.error;
        if (error.error?.message) return error.error.message;
        if (error.error?.error) return error.error.error;
        if (error.error?.mensajeError) return error.error.mensajeError;
        if (error.error?.msg) return error.error.msg;
        return null;
      };

      // 🔍 Detectar error de CORS (status 0 con error de tipo ProgressEvent)
      const isCorsError = error.status === 0 && error.error instanceof ProgressEvent;

      if (isCorsError) {
        errorTitle = 'Error de CORS';
        errorMessage = 'El servidor no permite solicitudes desde este origen. Contacta al administrador del backend.';
      } else {
        switch (error.status) {
          case 400:
            errorTitle = 'Datos inválidos';
            errorMessage = getBackendMessage(error) || 'Por favor revisa los datos ingresados';
            break;

          case 401:
            if (isAuthEndpoint) {
              errorTitle = 'Credenciales incorrectas';
              errorMessage = getBackendMessage(error) || 'Email o contraseña incorrectos';
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
            errorMessage = getBackendMessage(error) || 'No tienes permisos para realizar esta acción';
            break;

          case 404:
            errorTitle = 'No encontrado';
            errorMessage = getBackendMessage(error) || 'El recurso solicitado no existe';
            break;

          case 409:
            errorTitle = 'Conflicto';
            errorMessage = getBackendMessage(error) || 'Ya existe un registro con estos datos';
            break;

          case 422:
            errorTitle = 'Validación fallida';
            errorMessage = getBackendMessage(error) || 'Los datos no cumplen con las reglas de negocio';
            break;

          case 500:
            errorTitle = 'Error del servidor';
            errorMessage = getBackendMessage(error) || 'Ocurrió un error en el servidor. Intenta nuevamente más tarde.';
            break;

          case 0:
            errorTitle = 'Sin conexión';
            errorMessage = 'No se pudo conectar con el servidor. Verifica tu conexión a internet.';
            break;

          default:
            errorMessage = getBackendMessage(error) || errorMessage;
        }
      }

      notificationService.error(errorMessage, errorTitle);

      console.error('🔴 HTTP Error:', {
        status: error.status,
        isCorsError,
        title: errorTitle,
        message: errorMessage,
        url: error.url,
        error: error.error
      });

      return throwError(() => error);
    })
  );
};