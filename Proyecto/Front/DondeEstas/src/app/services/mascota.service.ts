import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Mascota } from '../models/mascota.model';

@Injectable({
  providedIn: 'root'
})
export class MascotaService {
  private apiUrl = 'http://localhost:8080/Proyecto/api/mascotas'; 

  // HARDCODE TEMPORAL PARA SIMULAR USUARIO LOGUEADO
  private MOCK_USER_ID = 1; 

  constructor(private http: HttpClient) { }

  crearMascota(mascota: any): Observable<any> {
    // El backend espera ?usuarioId=X
    return this.http.post(`${this.apiUrl}?usuarioId=${this.MOCK_USER_ID}`, mascota);
  }

  getMascotaById(id: number): Observable<any> {
    return this.http.get(`${this.apiUrl}/${id}`);
  }

  getMascotasPerdidas(): Observable<Mascota[]> {
    return this.http.get<Mascota[]>(`${this.apiUrl}/perdidas`);
  }
}