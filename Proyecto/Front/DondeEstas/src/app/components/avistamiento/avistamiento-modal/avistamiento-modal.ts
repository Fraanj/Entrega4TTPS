import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { AvistamientoService } from '../../../services/avistamiento.service';
import * as L from 'leaflet';

@Component({
  selector: 'app-avistamiento-modal',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './avistamiento-modal.html'
})
export class AvistamientoModalComponent {
  @Input() mascotaId!: number;
  @Output() close = new EventEmitter<void>();
  @Output() success = new EventEmitter<void>();

  avistamientoForm: FormGroup;
  loading = false;
  fotoBase64: string | null = null;

  private map: any;
  private marker: any;
  selectedLat: number | null = null;
  selectedLng: number | null = null;
  private defaultLat = -34.9214;
  private defaultLng = -57.9546;

  constructor(
    private fb: FormBuilder,
    private avistamientoService: AvistamientoService
  ) {
    this.avistamientoForm = this.fb.group({
      comentario: ['', [Validators.required, Validators.minLength(10)]]
    });
  }

  ngAfterViewInit() {
    setTimeout(() => this.initMap(), 100);
  }

  initMap() {
    this.map = L.map('map-avistamiento').setView([this.defaultLat, this.defaultLng], 13);

    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      attribution: '© OpenStreetMap'
    }).addTo(this.map);

    this.map.on('click', (e: any) => {
      const { lat, lng } = e.latlng;
      this.selectedLat = lat;
      this.selectedLng = lng;

      if (this.marker) this.map.removeLayer(this.marker);

      this.marker = L.marker([lat, lng]).addTo(this.map);
    });
  }

  onFileSelected(event: any) {
    const file = event.target.files[0];
    if (!file) return;

    // Validar tamaño (máximo 5MB antes de comprimir)
    if (file.size > 5 * 1024 * 1024) {
      alert('La imagen es muy grande. Máximo 5MB.');
      return;
    }

    const reader = new FileReader();
    reader.onload = () => {
      const img = new Image();
      img.onload = () => {
        // Crear canvas para redimensionar
        const canvas = document.createElement('canvas');
        const MAX_WIDTH = 800;
        const MAX_HEIGHT = 600;
        
        let width = img.width;
        let height = img.height;

        // Calcular nuevo tamaño manteniendo proporción
        if (width > height) {
          if (width > MAX_WIDTH) {
            height *= MAX_WIDTH / width;
            width = MAX_WIDTH;
          }
        } else {
          if (height > MAX_HEIGHT) {
            width *= MAX_HEIGHT / height;
            height = MAX_HEIGHT;
          }
        }

        canvas.width = width;
        canvas.height = height;
        const ctx = canvas.getContext('2d');
        ctx?.drawImage(img, 0, 0, width, height);

        // Convertir a base64 con calidad 0.7 (70%)
        this.fotoBase64 = canvas.toDataURL('image/jpeg', 0.7);
        console.log('📸 Imagen procesada:', (this.fotoBase64.length / 1024).toFixed(2), 'KB');
      };
      img.src = reader.result as string;
    };
    reader.readAsDataURL(file);
  }

  onSubmit() {
    if (this.avistamientoForm.invalid || !this.selectedLat || !this.selectedLng) {
      alert('Completá todos los campos y marcá la ubicación en el mapa');
      return;
    }

    this.loading = true;

    const avistamiento = {
      mascotaId: this.mascotaId,
      comentario: this.avistamientoForm.value.comentario,
      fecha: new Date().toISOString().split('T')[0],
      ubicacion: {
        latitud: this.selectedLat,
        longitud: this.selectedLng,
        barrio: ''
      },
      foto: this.fotoBase64
    };

    this.avistamientoService.crearAvistamiento(avistamiento).subscribe({
      next: () => {
        this.loading = false;
        this.success.emit();
        this.close.emit();
      },
      error: (err) => {
        console.error(err);
        this.loading = false;
        alert('Error al reportar avistamiento');
      }
    });
  }

  cerrar() {
    this.close.emit();
  }
}