import { Injectable } from '@angular/core';
import { Observable, from } from 'rxjs';
import { Avistamiento } from '../models/avistamiento.model';
import { ApiService } from './fetch.api.service';
import { AuthService } from './auth.service';

@Injectable({
  providedIn: 'root'
})
export class AvistamientoService {
  constructor(
    private api: ApiService,
    private auth: AuthService
  ) {}

  // Crear avistamiento
  crearAvistamiento(avistamiento: any): Observable<Avistamiento> {
    const user = this.auth.getCurrentUser();
    if (!user) throw new Error('Usuario no autenticado');

    return from(this.api.post<Avistamiento>(
      `/avistamientos`, 
      avistamiento, 
      true
    ));
  }

  // Obtener todos los avistamientos
  getAvistamientos(): Observable<Avistamiento[]> {
    return from(this.api.get<Avistamiento[]>('/avistamientos', false));
  }

  // Obtener avistamientos de una mascota específica
  getAvistamientosPorMascota(mascotaId: number): Observable<Avistamiento[]> {
    return from(this.api.get<Avistamiento[]>(`/avistamientos/mascota/${mascotaId}`, false));
  }

  // Obtener avistamientos del usuario actual
  getMisAvistamientos(): Observable<Avistamiento[]> {
    const user = this.auth.getCurrentUser();
    if (!user) return from(Promise.resolve([]));
    
    return from(this.api.get<Avistamiento[]>(`/avistamientos/usuario/${user.id}`, true));
  }
}