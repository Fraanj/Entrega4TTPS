import { Injectable } from '@angular/core';
import { Observable, from } from 'rxjs';
import { ApiService } from './fetch.api.service';
import { Usuario } from '../models/usuario.model';
import { Mascota } from '../models/mascota.model';

@Injectable({
  providedIn: 'root'
})
export class AdminService {
  constructor(private api: ApiService) {}

  listarUsuarios(): Observable<Usuario[]> {
    return from(this.api.get<Usuario[]>('/admin/usuarios', true));
  }

  cambiarEstadoUsuario(id: number, estado: string): Observable<void> {
    return from(this.api.put<void>(`/admin/usuarios/${id}/estado`, { estado }, true));
  }

  eliminarUsuario(id: number): Observable<void> {
    return from(this.api.delete<void>(`/admin/usuarios/${id}`, true));
  }

  listarMascotas(): Observable<Mascota[]> {
    return from(this.api.get<Mascota[]>('/admin/mascotas', true));
  }

  eliminarMascota(id: number): Observable<void> {
    return from(this.api.delete<void>(`/admin/mascotas/${id}`, true));
  }
}
