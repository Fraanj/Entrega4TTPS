import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { firstValueFrom } from 'rxjs';
import { GeorefResponse, Ciudad } from '../models/ubicacion.model';

@Injectable({
  providedIn: 'root'
})
export class CiudadService {
  private readonly GEOREF_API = 'https://apis.datos.gob.ar/georef/api/localidades';

  constructor(private http: HttpClient) {}

  async getCiudades(provincia?: string): Promise<Ciudad[]> {
    try {
      const params: any = {
        max: 1000, // Limitar a 1000 resultados
        campos: 'id,nombre,provincia.id,provincia.nombre',
        orden: 'nombre'
      };

      // Filtrar por provincia si se especifica (ej: Buenos Aires)
      if (provincia) {
        params.provincia = provincia;
      }

      const response = await firstValueFrom(
        this.http.get<GeorefResponse>(this.GEOREF_API, { params })
      );

      return response.localidades || [];
    } catch (error) {
      console.error('Error al cargar ciudades:', error);
      throw error;
    }
  }

  // Método alternativo: solo ciudades de Buenos Aires
  async getCiudadesBuenosAires(): Promise<Ciudad[]> {
    return this.getCiudades('Buenos Aires');
  }
}