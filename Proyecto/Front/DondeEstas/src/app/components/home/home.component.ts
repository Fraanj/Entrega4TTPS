import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { Mascota } from '../../models/mascota.model';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './home.component.html',
  styleUrl: './home.component.css'
})
export class HomeComponent {

  estadisticas = {
    perdidas: 142,
    recuperadas: 1234,
    adoptadas: 567
  };

  mascotasRecientes: Mascota[] = [
    {
      id: 1,
      nombre: 'Luna',
      color: 'Dorado',
      descripcion: 'Golden retriever',
      estado: 'PERDIDO_PROPIO',
      tamanioNombre: 'GRANDE',
      ubicacion: { latitud: 0, longitud: 0, barrio: 'Palermo' },
      fecha: '2025-01-03'
    },
    {
      id: 2,
      nombre: 'Mimi',
      color: 'Gris',
      descripcion: 'Tímida',
      estado: 'RECUPERADO',
      tamanioNombre: 'CHICO',
      ubicacion: { latitud: 0, longitud: 0, barrio: 'San Telmo' },
      fecha: '2025-01-02'
    },
    {
      id: 3,
      nombre: 'Rocky',
      color: 'Negro',
      descripcion: 'Sin collar',
      estado: 'PERDIDO_AJENO',
      tamanioNombre: 'MEDIANO',
      ubicacion: { latitud: 0, longitud: 0, barrio: 'Belgrano' },
      fecha: '2025-01-01'
    }
  ];

  constructor() {}

  // Retorna las clases exactas de Tailwind según el estado
  getEstadoClasses(estado: string): string {
    const base = 'px-3 py-1 rounded-full text-xs font-bold uppercase tracking-wide border ';
    
    switch (estado) {
      case 'PERDIDO_PROPIO': 
        return base + 'bg-red-500/20 text-red-200 border-red-500/30';
      case 'PERDIDO_AJENO': 
        return base + 'bg-amber-500/20 text-amber-200 border-amber-500/30';
      case 'RECUPERADO': 
        return base + 'bg-emerald-500/20 text-emerald-200 border-emerald-500/30';
      case 'ADOPTADO': 
        return base + 'bg-blue-500/20 text-blue-200 border-blue-500/30';
      default: 
        return base + 'bg-slate-500/20 text-slate-200 border-slate-500/30';
    }
  }

  getEstadoLabel(estado: string): string {
    const labels: {[key: string]: string} = {
      'PERDIDO_PROPIO': 'Perdido',
      'PERDIDO_AJENO': 'Visto',
      'RECUPERADO': 'Recuperado',
      'ADOPTADO': 'Adoptado'
    };
    return labels[estado] || estado;
  }
}