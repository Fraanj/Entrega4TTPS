import { Injectable } from '@angular/core';
import { Observable, from } from 'rxjs';
import { Mascota } from '../models/mascota.model';
import { ApiService } from './fetch.api.service';
import { AuthService } from './auth.service'; // Para obtener el ID del usuario

@Injectable({
  providedIn: 'root'
})
export class MascotaService {
  constructor(
    private api: ApiService,
    private auth: AuthService
  ) { }

  // CREAR MASCOTA
  crearMascota(mascota: any): Observable<Mascota> {
    const user = this.auth.getCurrentUser();
    if (!user) throw new Error('Usuario no autenticado');

    return from(this.api.post<Mascota>(`/mascotas?usuarioId=${user.id}`, mascota, true));
  }

  // OBTENER POR ID
  getMascotaById(id: number): Observable<Mascota> {
    return from(this.api.get<Mascota>(`/mascotas/${id}`, false)); // No requiere auth 
  }

  // OBTENER MASCOTAS PERDIDAS
  getMascotasPerdidas(): Observable<Mascota[]> {
    return from(this.api.get<Mascota[]>('/mascotas/perdidas', false)); // No requiere auth 
  }

  getMascotasAdoptadas(): Observable<Mascota[]> {
    return from(this.api.get<Mascota[]>('/mascotas/adoptadas', false)); // No requiere auth
  }
  
  getMascotasRecuperadas(): Observable<Mascota[]> {
    return from(this.api.get<Mascota[]>('/mascotas/recuperadas', false));
  }

  // OBTENER TODAS LAS MASCOTAS
  getMascotas(): Observable<Mascota[]> {
    return from(this.api.get<Mascota[]>('/mascotas/obtenerTodas', false)); // No requiere auth 
  }

  // Obtener SOLO las del usuario logueado (Privado)
  getMisMascotas(): Observable<Mascota[]> {
    const user = this.auth.getCurrentUser();
    if (!user) return from(Promise.resolve([]));
    
    return from(this.api.get<Mascota[]>('/mascotas/misMascotas', true));
  }

  // Eliminar Mascota
  eliminarMascota(id: number): Observable<void> {
    return from(this.api.delete<void>(`/mascotas/${id}`, true));
  }

  // Editar Mascota
  editarMascota(id: number, mascota: any): Observable<Mascota> {
    return from(this.api.put<Mascota>(`/mascotas/${id}`, mascota, true));
  }
}