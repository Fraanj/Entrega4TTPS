import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router, ActivatedRoute, RouterModule } from '@angular/router';
import { AvistamientoService } from '../../../services/avistamiento.service';
import { MascotaService } from '../../../services/mascota.service';
import { NotificationService } from '../../../services/notificacion.services';
import * as L from 'leaflet';

@Component({
  selector: 'app-avistamiento-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule],
  templateUrl: './avistamiento-form.html',
  styleUrls: []
})
export class AvistamientoFormComponent implements OnInit {
  avistamientoForm: FormGroup;
  loading = false;
  mascotaId: number | null = null;
  mascotaNombre: string = '';

  // MAPA
  private map: any;
  private marker: any;
  selectedLat: number | null = null;
  selectedLng: number | null = null;

  // Coordenadas por defecto (La Plata)
  private defaultLat = -34.9214;
  private defaultLng = -57.9546;

  ubicacionError = false;
  
  // Foto única para avistamiento
  fotoBase64: string | null = null;

  constructor(
    private fb: FormBuilder,
    private avistamientoService: AvistamientoService,
    private mascotaService: MascotaService,
    private notificationService: NotificationService,
    private router: Router,
    private route: ActivatedRoute
  ) {
    this.avistamientoForm = this.fb.group({
      comentario: ['', [Validators.required, Validators.minLength(10)]],
      descripcion: ['']
    });
  }

  ngOnInit(): void {
    setTimeout(() => this.initMap(), 100);

    // Obtener mascotaId de la ruta
    const id = this.route.snapshot.paramMap.get('mascotaId');
    if (id) {
      this.mascotaId = +id;
      this.cargarMascota(this.mascotaId);
    }
  }

  cargarMascota(id: number) {
    this.mascotaService.getMascotaById(id).subscribe({
      next: (mascota) => {
        this.mascotaNombre = mascota.nombre;
        // Centrar mapa en la ubicación de la mascota perdida
        if (mascota.ubicacion) {
          this.map.setView([mascota.ubicacion.latitud, mascota.ubicacion.longitud], 14);
        }
      },
      error: (err) => {
        console.error('Error al cargar mascota:', err);
        this.notificationService.error('Error al cargar información de la mascota');
      }
    });
  }

  // --- MAPA ---
  initMap() {
    // Crear mapa
    this.map = L.map('map').setView([this.defaultLat, this.defaultLng], 13);

    // Agregar capa de OpenStreetMap
    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      attribution: '© OpenStreetMap contributors'
    }).addTo(this.map);

    // Evento de click en el mapa
    this.map.on('click', (e: L.LeafletMouseEvent) => {
      this.agregarMarcador(e.latlng.lat, e.latlng.lng);
    });

    // Icono personalizado para el marcador
    const customIcon = L.icon({
      iconUrl: 'https://raw.githubusercontent.com/pointhi/leaflet-color-markers/master/img/marker-icon-2x-red.png',
      shadowUrl: 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.7.1/images/marker-shadow.png',
      iconSize: [25, 41],
      iconAnchor: [12, 41],
      popupAnchor: [1, -34],
      shadowSize: [41, 41]
    });

    // Asignar icono por defecto
    L.Marker.prototype.options.icon = customIcon;
  }

  agregarMarcador(lat: number, lng: number) {
    // Remover marcador anterior si existe
    if (this.marker) {
      this.map.removeLayer(this.marker);
    }

    // Agregar nuevo marcador
    this.marker = L.marker([lat, lng]).addTo(this.map);
    this.selectedLat = lat;
    this.selectedLng = lng;
    this.ubicacionError = false;

    // Popup con las coordenadas
    this.marker.bindPopup(`<b>Ubicación del avistamiento</b><br>Lat: ${lat.toFixed(5)}, Lng: ${lng.toFixed(5)}`).openPopup();
  }

  // --- FOTO ---
  onFileSelected(event: any) {
    const file = event.target.files[0];
    if (!file) return;

    // Validar tamaño (máximo 2MB)
    if (file.size > 2 * 1024 * 1024) {
      this.notificationService.error('La foto no debe superar 2MB');
      return;
    }

    // Validar tipo
    if (!file.type.startsWith('image/')) {
      this.notificationService.error('Solo se permiten imágenes');
      return;
    }

    // Convertir a base64
    const reader = new FileReader();
    reader.onload = () => {
      this.fotoBase64 = reader.result as string;
    };
    reader.readAsDataURL(file);
  }

  eliminarFoto() {
    this.fotoBase64 = null;
  }

  // --- SUBMIT ---
  onSubmit() {
    if (this.avistamientoForm.invalid) {
      this.notificationService.error('Por favor completa todos los campos requeridos');
      return;
    }

    if (!this.selectedLat || !this.selectedLng) {
      this.ubicacionError = true;
      this.notificationService.error('Debes seleccionar una ubicación en el mapa');
      return;
    }

    if (!this.mascotaId) {
      this.notificationService.error('No se pudo identificar la mascota');
      return;
    }

    this.loading = true;

    const avistamientoData = {
      mascotaId: this.mascotaId,
      comentario: this.avistamientoForm.value.comentario,
      descripcion: this.avistamientoForm.value.descripcion || '',
      ubicacion: {
        latitud: this.selectedLat,
        longitud: this.selectedLng,
        barrio: '' // El backend lo obtiene de Georef
      },
      foto: this.fotoBase64 || undefined
    };

    this.avistamientoService.crearAvistamiento(avistamientoData).subscribe({
      next: () => {
        this.notificationService.success('¡Avistamiento reportado exitosamente!');
        this.loading = false;
        // Redirigir al detalle de la mascota
        this.router.navigate(['/mascota', this.mascotaId]);
      },
      error: (err) => {
        console.error('Error al crear avistamiento:', err);
        this.notificationService.error('Error al reportar el avistamiento');
        this.loading = false;
      }
    });
  }

  cancelar() {
    if (this.mascotaId) {
      this.router.navigate(['/mascota', this.mascotaId]);
    } else {
      this.router.navigate(['/mascotas']);
    }
  }
}
