import { Ubicacion } from './ubicacion.model';

export interface Mascota {
  id?: number; // Opcional porque al crearla no tiene ID
  nombre: string;
  color: string;
  descripcion: string;
  estado: 'PERDIDO_PROPIO' | 'PERDIDO_AJENO' | 'RECUPERADO' | 'ADOPTADO';
  tamanioNombre: 'CHICO' | 'MEDIANO' | 'GRANDE';
  ubicacion: Ubicacion;
  usuarioId?: number; 
  fecha?: string;
  foto?: string;
}