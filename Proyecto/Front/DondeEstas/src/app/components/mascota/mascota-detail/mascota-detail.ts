import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { MascotaService } from '../../../services/mascota.service';
import { DomSanitizer, SafeUrl } from '@angular/platform-browser';
import * as L from 'leaflet'; // Para mostrar mapa estático
import { AuthService } from '../../../services/auth.service';
import { AvistamientoService } from '../../../services/avistamiento.service';
import { AvistamientoModalComponent } from '../../avistamiento/avistamiento-modal/avistamiento-modal';
import { AvistamientoDetailModalComponent } from '../../avistamiento/avistamiento-detail/avistamiento-detail';

@Component({
  selector: 'app-mascota-detail',
  standalone: true,
  imports: [CommonModule, RouterModule, AvistamientoModalComponent, AvistamientoDetailModalComponent],
  templateUrl: './mascota-detail.html',
  styleUrls: []
})
export class MascotaDetailComponent implements OnInit {
  avistamientoSeleccionado: any = null;
  pet: any = null;
  loading = true;
  fotoSeleccionada: SafeUrl | null = null; // Para la galería
  esMio = false;

  avistamientos: any[] = [];
  mostrarModalAvistamiento = false;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private mascotaService: MascotaService,
    private avistamientoService: AvistamientoService,
    private authService: AuthService,
    private cdr: ChangeDetectorRef,
    private sanitizer: DomSanitizer
  ) {}

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.cargarDetalle(+id);
      this.cargarAvistamientos(+id);
    }
  }

  verDetalleAvistamiento(avistamiento: any) {
    this.avistamientoSeleccionado = avistamiento;
  }

  cerrarDetalleAvistamiento() {
    this.avistamientoSeleccionado = null;
  }

  cargarAvistamientos(mascotaId: number) {
    this.avistamientoService.getAvistamientosPorMascota(mascotaId).subscribe({
      next: (data) => {
        this.avistamientos = data;
        this.cdr.detectChanges();
      },
      error: (err) => console.error('Error cargando avistamientos:', err)
    });
  }

  abrirModalAvistamiento() {
    this.mostrarModalAvistamiento = true;
  }

  cerrarModalAvistamiento() {
    this.mostrarModalAvistamiento = false;
  }

  onAvistamientoCreado() {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) this.cargarAvistamientos(+id);
  }

  cargarDetalle(id: number) {
    this.mascotaService.getMascotaById(id).subscribe({
      next: (data) => {
        this.pet = data;

        // Verificar dueño
        const currentUser = this.authService.getCurrentUser();
        if (currentUser && this.pet?.publicadorId === currentUser.id) {
          this.esMio = true;
        }

        if (this.pet.fotos?.length > 0) {
          this.fotoSeleccionada = this.getFotoSegura(this.pet.fotos[0]);
        }
        this.loading = false;
        this.cdr.detectChanges();
        
        // Aquí podrías iniciar un mapa pequeño si quisieras
      },
      error: (err) => {
        console.error(err);
        this.loading = false;
        this.cdr.detectChanges();
      }
    });
  }

  eliminarMascota() {
    if (!this.pet?.id) return;
    if (confirm('¿Estás seguro de que querés eliminar esta publicación?')) {
      this.mascotaService.eliminarMascota(this.pet.id).subscribe({
        next: () => {
          alert('Publicación eliminada');
          this.router.navigate(['/mascotas']);
        },
        error: () => alert('Error al eliminar')
      });
    }
  }

  getFotoSegura(base64: string): SafeUrl {
    if (!base64) return '';
    let img = base64.startsWith('data:image') ? base64 : 'data:image/jpeg;base64,' + base64;
    return this.sanitizer.bypassSecurityTrustUrl(img);
  }

  seleccionarFoto(foto: string) {
    this.fotoSeleccionada = this.getFotoSegura(foto);
  }
}