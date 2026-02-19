import { Component, Input, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { AvistamientoService } from '../../../services/avistamiento.service';
import { Avistamiento } from '../../../models/avistamiento.model';
import * as L from 'leaflet';

@Component({
  selector: 'app-avistamiento-list',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './avistamiento-list.html',
  styleUrls: []
})
export class AvistamientoListComponent implements OnInit, OnDestroy {
  @Input() mascotaId!: number;

  avistamientos: Avistamiento[] = [];
  loading = true;
  error = false;
  private miniMaps: Map<number, L.Map> = new Map();

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

  onDetailsToggle(event: Event, avistamiento: Avistamiento, index: number): void {
    const details = event.target as HTMLDetailsElement;
    if (!details.open || !avistamiento.ubicacion?.latitud || !avistamiento.ubicacion?.longitud) {
      return;
    }
    if (this.miniMaps.has(index)) {
      return; // ya inicializado
    }
    setTimeout(() => this.initMiniMap(avistamiento, index), 100);
  }

  private initMiniMap(avistamiento: Avistamiento, index: number): void {
    const lat = avistamiento.ubicacion!.latitud;
    const lng = avistamiento.ubicacion!.longitud;
    const container = document.getElementById(`mapa-av-${index}`);
    if (!container || this.miniMaps.has(index)) return;

    const map = L.map(`mapa-av-${index}`, {
      center: [lat, lng],
      zoom: 15,
      zoomControl: false,
      attributionControl: false
    });

    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      attribution: '© OSM'
    }).addTo(map);

    L.marker([lat, lng], {
      icon: L.icon({
        iconUrl: 'https://raw.githubusercontent.com/pointhi/leaflet-color-markers/master/img/marker-icon-2x-red.png',
        iconSize: [25, 41],
        iconAnchor: [12, 41]
      })
    }).addTo(map);

    this.miniMaps.set(index, map);
    setTimeout(() => map.invalidateSize(), 150);
  }

  ngOnDestroy(): void {
    this.miniMaps.forEach(m => m.remove());
    this.miniMaps.clear();
  }
}
