import { Injectable } from '@angular/core';
import { ApiService } from './fetch.api.service';
import { Usuario, UsuarioLogged } from '../models/usuario.model';
import { NotificationService } from './notificacion.services';
import { Router } from '@angular/router';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private readonly TOKEN_KEY = 'accessToken';
  private readonly USER_KEY = 'usuario';

  constructor(
    private apiService: ApiService,
    private notificationService: NotificationService,
    private router: Router
  ) {}

  // Login
  async login(email: string, password: string): Promise<UsuarioLogged> {
    try {
      const response = await this.apiService.post<UsuarioLogged>(
        '/auth/login',
        { email, password },
        false
      );
      
      this.setToken(response.accessToken);
      this.setUser(response.usuario);
      
      this.notificationService.success(
        `Bienvenido ${response.usuario.nombre}!`,
        'Inicio de sesión exitoso'
      );
      
      return response;
    } catch (error) {
      // El interceptor ya mostró el error
      throw error;
    }
  }

  // Register
  async register(userData: {
    nombre: string;
    apellido: string;
    email: string;
    password: string;
    telefono: string;
    ciudad: string;
  }): Promise<UsuarioLogged> {
    try {
      const response = await this.apiService.post<UsuarioLogged>(
        '/auth/register',
        userData,
        false
      );

      this.setToken(response.accessToken);
      this.setUser(response.usuario);
      
      this.notificationService.success(
        '¡Tu cuenta ha sido creada exitosamente!',
        'Registro completado'
      );
      
      return response;
    } catch (error) {
      // El interceptor ya mostró el error
      throw error;
    }
  }

  // Logout
  logout(): void {
    localStorage.removeItem(this.TOKEN_KEY);
    localStorage.removeItem(this.USER_KEY);
    this.notificationService.info('Sesión cerrada correctamente', 'Hasta pronto');
    this.router.navigate(['/login']);
  }

  // Verificar si está autenticado
  isAuthenticated(): boolean {
    const token = this.getToken();
    return !!token && !this.isTokenExpired();
  }

  // Obtener token
  getToken(): string | null {
    return localStorage.getItem(this.TOKEN_KEY);
  }

  // Obtener usuario actual
  getCurrentUser(): Usuario | null {
    const userJson = localStorage.getItem(this.USER_KEY);
    return userJson ? JSON.parse(userJson) : null;
  }

  // Verificar si el token expiró
  isTokenExpired(): boolean {
    const token = this.getToken();
    if (!token) return true;

    try {
      const payload = JSON.parse(atob(token.split('.')[1]));
      const exp = payload.exp * 1000;
      return Date.now() >= exp;
    } catch (error) {
      return true;
    }
  }

  // Métodos privados para gestionar localStorage
  private setToken(token: string): void {
    localStorage.setItem(this.TOKEN_KEY, token);
  }

  private setUser(user: Usuario): void {
    localStorage.setItem(this.USER_KEY, JSON.stringify(user));
  }
}