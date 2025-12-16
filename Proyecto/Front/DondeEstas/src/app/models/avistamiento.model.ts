import { Ubicacion } from './ubicacion.model';

export interface Avistamiento {
  id?: number;
  mascotaId: number;
  reportadorId: number; // El usuario que avista
  descripcion?: string;
  comentario: string;
  fecha: string;
  ubicacion?: Ubicacion;
  foto?: string;
}