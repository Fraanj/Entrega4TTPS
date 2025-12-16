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
  styleUrls: ['./mascota-form.css'] // Usa styles global via @tailwind
})
export class MascotaFormComponent implements OnInit {
  mascotaForm: FormGroup;
  loading = false;
  private map: any;
  private marker: any;
  selectedLat: number = -34.9214; // Default La Plata
  selectedLng: number = -57.9546;

  ubicacionError = false; // Para mostrar mensaje de error
  
  previewUrl: string | null = null; // Para mostrar la foto cargada
  fotoBase64: string | null = null; // El string que mandamos al back

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
    this.map = L.map('map').setView([this.selectedLat, this.selectedLng], 13);

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
    // Usamos un círculo simple si el ícono falla, o un marcador estándar
    this.marker = L.circleMarker([lat, lng], { radius: 8, color: 'blue' }).addTo(this.map);
    
    this.selectedLat = lat;
    this.selectedLng = lng;
    this.ubicacionError = false; // Limpiamos el error si selecciona algo
  }

  // --- LÓGICA DE FOTO (BASE64) ---
  onFileSelected(event: any): void {
    const file = event.target.files[0];
    if (file) {
      // Validar tamaño (ej: max 2MB)
      if (file.size > 2 * 1024 * 1024) {
        alert('La imagen es muy pesada (Máx 2MB)');
        return;
      }

      const reader = new FileReader();
      reader.onload = (e: any) => {
        this.previewUrl = e.target.result; // Para mostrar en el <img>
        this.fotoBase64 = e.target.result; // String completo data:image/jpeg;base64,...
      };
      reader.readAsDataURL(file);
    }
  }

  onSubmit(): void {
    // 1. Validar Formulario de texto
    if (this.mascotaForm.invalid) {
      this.mascotaForm.markAllAsTouched(); // Para que se pongan rojos los inputs
      return;
    }

    // 2. Validar Mapa
    if (this.selectedLat === null || this.selectedLng === null) {
      this.ubicacionError = true;
      return;
    }

    this.loading = true;
    const formValue = this.mascotaForm.value;

    const nuevaMascota = {
      nombre: formValue.nombre,
      descripcion: formValue.descripcion,
      color: formValue.color,
      tamanioNombre: formValue.tamanioNombre,
      estado: formValue.estado,
      ubicacion: {
        latitud: this.selectedLat,
        longitud: this.selectedLng,
        barrio: '' // El back se encarga
      },
      // Mandamos la foto real si existe
      fotosUrls: this.fotoBase64 ? [this.fotoBase64] : []
    };

    this.mascotaService.crearMascota(nuevaMascota).subscribe({
      next: (res) => {
        alert('¡Mascota publicada con éxito!');
        this.router.navigate(['/']);
      },
      error: (err) => {
        console.error('Error', err);
        this.loading = false;
        alert('Error al guardar. Chequeá que el backend soporte archivos grandes (LONGTEXT en DB).');
      }
    });
  }
}