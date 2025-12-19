import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { MascotaService } from '../../../services/mascota.service';
import { DomSanitizer, SafeUrl } from '@angular/platform-browser';
import { NotificationService } from '../../../services/notificacion.services';

@Component({
  selector: 'app-mis-mascotas',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './mis-mascotas.html',
  styleUrls: []
})
export class MisMascotasComponent implements OnInit {
  mascotas: any[] = [];
  loading = true;

  constructor(
    private mascotaService: MascotaService,
    private sanitizer: DomSanitizer,
    private cdr: ChangeDetectorRef,
    private notification: NotificationService
  ) {}

  ngOnInit(): void {
    this.cargarMisMascotas();
  }

  cargarMisMascotas() {
    this.loading = true;
    this.mascotaService.getMisMascotas().subscribe({
      next: (data) => {
        this.mascotas = data;
        this.loading = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error(err);
        this.loading = false;
        this.cdr.detectChanges();
      }
    });
  }

  eliminar(id: number) {
    if(!confirm('¿Estás seguro de que querés eliminar esta publicación?')) return;

    this.mascotaService.eliminarMascota(id).subscribe({
      next: () => {
        this.notification.success('Mascota eliminada correctamente', 'Éxito');
        this.cargarMisMascotas(); // Recargar lista
        this.cdr.detectChanges();
      },
      error: () => this.notification.error('No se pudo eliminar', 'Error')
    });
  }

  getFotoSegura(base64: string): SafeUrl {
    if (!base64) return '';
    let img = base64.startsWith('data:image') ? base64 : 'data:image/jpeg;base64,' + base64;
    return this.sanitizer.bypassSecurityTrustUrl(img);
  }

  getEstadoClass(estado: string): string {
    const base = 'px-3 py-1 rounded-full text-xs font-bold uppercase ';
    switch(estado) {
      case 'PERDIDO_PROPIO':
        return base + 'bg-red-500/90 text-white';
      case 'PERDIDO_AJENO':
        return base + 'bg-amber-500/90 text-white';
      case 'RECUPERADO':
        return base + 'bg-emerald-500/90 text-white';
      case 'ADOPTADO':
        return base + 'bg-blue-500/90 text-white';
      default:
        return base + 'bg-slate-500/90 text-white';
    }
  }

  getEstadoLabel(estado: string): string {
    switch(estado) {
      case 'PERDIDO_PROPIO': return 'Perdido';
      case 'PERDIDO_AJENO': return 'Visto';
      case 'RECUPERADO': return '✅ Recuperado';
      case 'ADOPTADO': return '🏠 Adoptado';
      default: return estado;
    }
  }
}