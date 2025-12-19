import { Component, Input, Output, EventEmitter, AfterViewInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { DomSanitizer, SafeUrl } from '@angular/platform-browser';
import * as L from 'leaflet';

@Component({
  selector: 'app-avistamiento-detail-modal',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './avistamiento-detail.html'
})
export class AvistamientoDetailModalComponent implements AfterViewInit {
  @Input() avistamiento: any;
  @Output() close = new EventEmitter<void>();

  private map: any;

  constructor(private sanitizer: DomSanitizer) {}

  ngAfterViewInit() {
    if (this.avistamiento?.ubicacion?.latitud && this.avistamiento?.ubicacion?.longitud) {
      setTimeout(() => this.initMap(), 100);
    }
  }

  initMap() {
    const lat = this.avistamiento.ubicacion.latitud;
    const lng = this.avistamiento.ubicacion.longitud;

    this.map = L.map('map-detalle-avistamiento').setView([lat, lng], 15);

    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      attribution: '© OpenStreetMap'
    }).addTo(this.map);

    L.marker([lat, lng]).addTo(this.map)
      .bindPopup(`<b>${this.avistamiento.mascotaNombre}</b><br>${this.avistamiento.ubicacion.barrio}`)
      .openPopup();
  }

  getFotoSegura(base64: string): SafeUrl {
    if (!base64) return '';
    let img = base64.startsWith('data:image') ? base64 : 'data:image/jpeg;base64,' + base64;
    return this.sanitizer.bypassSecurityTrustUrl(img);
  }

  cerrar() {
    this.close.emit();
  }
}