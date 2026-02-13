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
  ) { }

  // CREAR AVISTAMIENTO
  crearAvistamiento(avistamiento: Partial<Avistamiento>): Observable<Avistamiento> {
    const user = this.auth.getCurrentUser();
    if (!user) throw new Error('Usuario no autenticado');

    return from(
      this.api.post<Avistamiento>(
        `/avistamientos?reportadorId=${user.id}`, 
        avistamiento, 
        true
      )
    );
  }

  // OBTENER TODOS LOS AVISTAMIENTOS
  getAvistamientos(): Observable<Avistamiento[]> {
    return from(this.api.get<Avistamiento[]>('/avistamientos', false));
  }

  // OBTENER AVISTAMIENTO POR ID
  getAvistamientoById(id: number): Observable<Avistamiento> {
    return from(this.api.get<Avistamiento>(`/avistamientos/${id}`, false));
  }

  // OBTENER AVISTAMIENTOS DE UNA MASCOTA
  getAvistamientosByMascota(mascotaId: number): Observable<Avistamiento[]> {
    return from(this.api.get<Avistamiento[]>(`/avistamientos/mascota/${mascotaId}`, false));
  }
}
