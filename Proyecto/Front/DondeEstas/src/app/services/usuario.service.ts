import { Injectable } from '@angular/core';
import { ApiService } from './fetch.api.service';
import { Usuario } from '../models/usuario.model';

@Injectable({
  providedIn: 'root'
})
export class UsuarioService {

  constructor(private apiService: ApiService) {}

  async actualizarPerfil(id: number, datos: Partial<Usuario>): Promise<Usuario> {
    return await this.apiService.put<Usuario>(`/usuarios/${id}`, datos);
  }
}