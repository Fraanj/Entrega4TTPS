import { Component, Input, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { AvistamientoService } from '../../../services/avistamiento.service';
import { Avistamiento } from '../../../models/avistamiento.model';

@Component({
  selector: 'app-avistamiento-list',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './avistamiento-list.html',
  styleUrls: []
})
export class AvistamientoListComponent implements OnInit {
  @Input() mascotaId!: number;
  
  avistamientos: Avistamiento[] = [];
  loading = true;
  error = false;

  constructor(private avistamientoService: AvistamientoService) {}

  ngOnInit(): void {
    if (this.mascotaId) {
      this.cargarAvistamientos();
    }
  }

  cargarAvistamientos() {
    this.loading = true;
    this.error = false;

    this.avistamientoService.getAvistamientosByMascota(this.mascotaId).subscribe({
      next: (data) => {
        this.avistamientos = data;
        this.loading = false;
      },
      error: (err) => {
        console.error('Error al cargar avistamientos:', err);
        this.error = true;
        this.loading = false;
      }
    });
  }

  formatearFecha(fecha: string): string {
    const date = new Date(fecha);
    const ahora = new Date();
    const diff = ahora.getTime() - date.getTime();
    const dias = Math.floor(diff / (1000 * 60 * 60 * 24));

    if (dias === 0) return 'Hoy';
    if (dias === 1) return 'Ayer';
    if (dias < 7) return `Hace ${dias} días`;
    if (dias < 30) return `Hace ${Math.floor(dias / 7)} semanas`;
    return date.toLocaleDateString('es-AR');
  }
}
