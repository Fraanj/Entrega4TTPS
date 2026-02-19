import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { MascotaService } from '../../services/mascota.service';
import * as L from 'leaflet';

interface MascotaPerdida {
  id?: number;
  nombre: string;
  color: string;
  descripcion: string;
  estado: string;
  ubicacion?: { latitud: number; longitud: number; barrio?: string };
  fotos?: string[];
  publicadorNombre?: string;
}

@Component({
  selector: 'app-mapa',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './mapa.component.html',
  styleUrls: []
})
export class MapaComponent implements OnInit, OnDestroy {
  loading = true;
  mascotas: MascotaPerdida[] = [];
  private map: L.Map | null = null;
  private markers: L.Marker[] = [];

  private defaultLat = -34.9214;
  private defaultLng = -57.9546;

  constructor(
    private mascotaService: MascotaService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.cargarMascotasPerdidas();
  }

  ngOnDestroy(): void {
    this.limpiarMapa();
  }

  cargarMascotasPerdidas() {
    this.loading = true;
    this.mascotaService.getMascotasPerdidas().subscribe({
      next: (data) => {
        this.mascotas = data;
        this.loading = false;
        setTimeout(() => this.initMap(), 50);
      },
      error: () => {
        this.loading = false;
      }
    });
  }

  private initMap(): void {
    if (this.map) return;

    this.map = L.map('mapa-perdidas').setView([this.defaultLat, this.defaultLng], 11);

    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      attribution: '© OpenStreetMap contributors'
    }).addTo(this.map);

    this.agregarMarcadores();
  }

  private agregarMarcadores(): void {
    if (!this.map) return;

    this.limpiarMarcadores();

    const mascotasConUbicacion = this.mascotas.filter(
      m => m.ubicacion?.latitud != null && m.ubicacion?.longitud != null
    );

    if (mascotasConUbicacion.length > 0) {
      const bounds: L.LatLngBoundsExpression = mascotasConUbicacion.map(m =>
        [m.ubicacion!.latitud, m.ubicacion!.longitud] as [number, number]
      );
      this.map.fitBounds(bounds, { padding: [30, 30], maxZoom: 14 });
    }

    const icono = L.icon({
      iconUrl: 'https://raw.githubusercontent.com/pointhi/leaflet-color-markers/master/img/marker-icon-2x-red.png',
      shadowUrl: 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.7.1/images/marker-shadow.png',
      iconSize: [25, 41],
      iconAnchor: [12, 41],
      popupAnchor: [1, -34]
    });

    for (const m of mascotasConUbicacion) {
      const lat = m.ubicacion!.latitud;
      const lng = m.ubicacion!.longitud;

      const popupContent = `
        <div class="text-sm text-left mapa-popup" style="min-width: 180px;">
          <strong class="text-indigo-600">${m.nombre}</strong>
          <p class="text-slate-600 mt-1">${m.ubicacion?.barrio || 'Sin zona'}</p>
          ${m.id ? `<a href="#" class="mapa-popup-link text-indigo-500 hover:underline font-medium" data-mascota-id="${m.id}">Ver detalle →</a>` : ''}
        </div>
      `;

      const marker = L.marker([lat, lng], { icon: icono })
        .addTo(this.map!)
        .bindPopup(popupContent);

      marker.on('popupopen', () => {
        const el = marker.getPopup()?.getElement();
        const link = el?.querySelector('.mapa-popup-link');
        if (link) {
          link.addEventListener('click', (e) => {
            e.preventDefault();
            this.router.navigate(['/mascotas', m.id]);
          });
        }
      });

      this.markers.push(marker);
    }
  }

  private limpiarMarcadores(): void {
    for (const m of this.markers) {
      m.remove();
    }
    this.markers = [];
  }

  private limpiarMapa(): void {
    this.limpiarMarcadores();
    if (this.map) {
      this.map.remove();
      this.map = null;
    }
  }
}
