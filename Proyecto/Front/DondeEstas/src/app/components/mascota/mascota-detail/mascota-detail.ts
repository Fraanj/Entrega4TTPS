import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { MascotaService } from '../../../services/mascota.service';
import { DomSanitizer, SafeUrl } from '@angular/platform-browser';
import * as L from 'leaflet'; // Para mostrar mapa estático
import { AuthService } from '../../../services/auth.service';
import { AvistamientoListComponent } from '../../avistamiento/avistamiento-list/avistamiento-list';

@Component({
  selector: 'app-mascota-detail',
  standalone: true,
  imports: [CommonModule, RouterModule, AvistamientoListComponent],
  templateUrl: './mascota-detail.html',
  styleUrls: []
})
export class MascotaDetailComponent implements OnInit {
  pet: any = null;
  loading = true;
  fotoSeleccionada: SafeUrl | null = null; // Para la galería
  esMio = false;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private mascotaService: MascotaService,
    private authService: AuthService,
    private cdr: ChangeDetectorRef,
    private sanitizer: DomSanitizer
  ) {}

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) this.cargarDetalle(+id);
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