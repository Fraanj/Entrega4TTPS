import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { MascotaService } from '../../../services/mascota.service';
import * as L from 'leaflet'; // Importamos Leaflet

@Component({
  selector: 'app-mascota-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule],
  templateUrl: './mascota-form.html',
  styleUrls: [] // Usa styles global via @tailwind
})
export class MascotaFormComponent implements OnInit {
  mascotaForm: FormGroup;
  loading = false;

  private map: any;
  private marker: any;
  selectedLat: number | null = null;
  selectedLng: number | null = null;

  // Coordenadas por defecto SOLO PARA LA VISTA del mapa (La Plata)
  private defaultLat = -34.9214;
  private defaultLng = -57.9546;

  ubicacionError = false; // Para mostrar mensaje de error
  
  // Fotos Múltiples
  fotosBase64: string[] = []; // Array de strings base64
  MAX_FOTOS = 4;

  // Feedback (Toast Custom)
  toastMessage: string | null = null;
  toastType: 'success' | 'error' = 'success';

  constructor(
    private fb: FormBuilder,
    private mascotaService: MascotaService,
    private router: Router
  ) {
    this.mascotaForm = this.fb.group({
      nombre: ['', Validators.required],
      descripcion: ['', Validators.required],
      color: ['', Validators.required],
      tamanioNombre: ['MEDIANO', Validators.required],
      estado: ['PERDIDO_PROPIO', Validators.required],
    });
  }

  ngOnInit(): void {
    setTimeout(() => this.initMap(), 100);
  }

  // Inicializa el mapa
  private initMap(): void {
    this.map = L.map('map').setView([this.defaultLat, this.defaultLng], 13);

    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      attribution: '© OpenStreetMap contributors'
    }).addTo(this.map);

    // Evento click en el mapa
    this.map.on('click', (e: any) => {
      const { lat, lng } = e.latlng;
      this.setMarker(lat, lng);
    });
  }

  private setMarker(lat: number, lng: number): void {
    if (this.marker) {
      this.map.removeLayer(this.marker);
    }

    this.marker = L.circleMarker([lat, lng], { radius: 8, color: '#4f46e5', fillOpacity: 0.8 }).addTo(this.map);
    
    this.selectedLat = lat;
    this.selectedLng = lng;
    this.ubicacionError = false; // Limpiamos el error si selecciona algo
  }

  // --- FOTOS MÚLTIPLES ---
  onFileSelected(event: any): void {
    if (this.fotosBase64.length >= this.MAX_FOTOS) {
      this.showToast('Máximo 4 fotos permitidas', 'error');
      return;
    }

    const file = event.target.files[0];
    if (file) {
      if (file.size > 2 * 1024 * 1024) {
        this.showToast('La imagen es muy pesada (Máx 2MB)', 'error');
        return;
      }

      const reader = new FileReader();
      reader.onload = (e: any) => {
        // Agregamos al array
        this.fotosBase64.push(e.target.result);
        event.target.value = ''; // Reset input para poder subir la misma si se borró
      };
      reader.readAsDataURL(file);
    }
  }

  removeFoto(index: number): void {
    this.fotosBase64.splice(index, 1);
  }

  // --- SUBMIT ---
  onSubmit(): void {
    if (this.mascotaForm.invalid) {
      this.mascotaForm.markAllAsTouched();
      this.showToast('Por favor completá los campos requeridos', 'error');
      return;
    }

    if (this.selectedLat === null || this.selectedLng === null) {
      this.ubicacionError = true;
      this.showToast('Falta marcar la ubicación en el mapa', 'error');
      return;
    }

    this.loading = true;
    const formValue = this.mascotaForm.value;

    const nuevaMascota = {
      ...formValue,
      ubicacion: {
        latitud: this.selectedLat,
        longitud: this.selectedLng,
        barrio: '' 
      },
      fotos: this.fotosBase64 // Mandamos el array completo
    };

    this.mascotaService.crearMascota(nuevaMascota).subscribe({
      next: () => {
        this.showToast('¡Mascota publicada con éxito!', 'success');
        setTimeout(() => this.router.navigate(['/']), 2000); // Esperar a leer el mensaje
      },
      error: (err) => {
        console.error(err);
        this.loading = false;
        this.showToast('Error al guardar. Intenta nuevamente.', 'error');
      }
    });
  }

  // Helper para validaciones en HTML
  isFieldInvalid(fieldName: string): boolean {
    const field = this.mascotaForm.get(fieldName);
    return !!(field && field.invalid && field.touched);
  }

  // Helper Toast
  private showToast(msg: string, type: 'success' | 'error') {
    this.toastMessage = msg;
    this.toastType = type;
    setTimeout(() => this.toastMessage = null, 4000); // Ocultar a los 4s
  }
}