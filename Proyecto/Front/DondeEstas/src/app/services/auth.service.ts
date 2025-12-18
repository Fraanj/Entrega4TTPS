import { Injectable } from '@angular/core';
import { ApiService } from './fetch.api.service';
import { UsuarioLogged, Usuario } from '../models/usuario.model';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private readonly TOKEN_KEY = 'accessToken';
  private readonly USER_KEY = 'usuario';

  constructor(private apiService: ApiService) {}

  // Login
  async login(email: string, password: string): Promise<UsuarioLogged> {
    const response = await this.apiService.post<UsuarioLogged>(
      '/auth/login',
      { email, password },
      false // No requiere autenticación para login
    );

    // Guardar en localStorage
    this.setToken(response.accessToken);
    this.setUser(response.usuario);

    return response;
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
    const response = await this.apiService.post<UsuarioLogged>(
      '/auth/register',
      userData,
      false // No requiere autenticación para registro
    );

    // Guardar en localStorage
    this.setToken(response.accessToken);
    this.setUser(response.usuario);

    return response;
  }

  // Logout
  logout(): void {
    localStorage.removeItem(this.TOKEN_KEY);
    localStorage.removeItem(this.USER_KEY);
  }

  // Verificar si está autenticado
  isAuthenticated(): boolean {
    return !!this.getToken();
  }

  // Obtener token
  getToken(): string | null {
    return localStorage.getItem(this.TOKEN_KEY);
  }

  // Obtener usuario actual
  getCurrentUser(): Usuario | null {
    const userStr = localStorage.getItem(this.USER_KEY);
    return userStr ? JSON.parse(userStr) : null;
  }

  // Verificar si el token expiró
  isTokenExpired(): boolean {
    // Implementar lógica de expiración si es necesario
    // Por ahora retorna false
    return false;
  }

  // Métodos privados para gestionar localStorage
  private setToken(token: string): void {
    localStorage.setItem(this.TOKEN_KEY, token);
  }

  private setUser(user: Usuario): void {
    localStorage.setItem(this.USER_KEY, JSON.stringify(user));
  }
}