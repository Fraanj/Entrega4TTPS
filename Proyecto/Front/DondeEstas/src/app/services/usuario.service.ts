import { Injectable } from '@angular/core';
import { Observable, from } from 'rxjs';
import { ApiService } from './fetch.api.service';
import { Usuario } from '../models/usuario.model';

export interface UsuarioRanking {
  id: number;
  nombre: string;
  apellido: string;
  cantidadReportes: number;
  nombreCompleto?: string;
}

@Injectable({
  providedIn: 'root'
})
export class UsuarioService {

  constructor(private apiService: ApiService) {}

  async actualizarPerfil(id: number, datos: Partial<Usuario>): Promise<Usuario> {
    return await this.apiService.put<Usuario>(`/usuarios/${id}`, datos);
  }

  obtenerRanking(limit: number = 10): Observable<UsuarioRanking[]> {
    return from(this.apiService.get<UsuarioRanking[]>(`/usuarios/ranking?limit=${limit}`, false));
  }
}