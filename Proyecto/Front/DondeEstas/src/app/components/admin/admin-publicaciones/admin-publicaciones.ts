import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { DomSanitizer, SafeUrl } from '@angular/platform-browser';
import { AdminService } from '../../../services/admin.service';
import { NotificationService } from '../../../services/notificacion.services';
import { Mascota } from '../../../models/mascota.model';

@Component({
  selector: 'app-admin-publicaciones',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './admin-publicaciones.html',
  styleUrls: []
})
export class AdminPublicacionesComponent implements OnInit {
  mascotas: Mascota[] = [];
  loading = true;
  error: string | null = null;
  eliminandoId: number | null = null;

  constructor(
    private adminService: AdminService,
    private notification: NotificationService,
    private cdr: ChangeDetectorRef,
    private sanitizer: DomSanitizer
  ) {}

  ngOnInit(): void {
    this.cargarMascotas();
  }

  cargarMascotas() {
    this.loading = true;
    this.error = null;
    this.adminService.listarMascotas().subscribe({
      next: (data) => {
        this.mascotas = data;
        this.loading = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        this.error = 'No se pudieron cargar las publicaciones.';
        this.loading = false;
        this.cdr.detectChanges();
      }
    });
  }

  eliminar(mascota: Mascota) {
    if (!confirm(`¿Eliminar la publicación "${mascota.nombre}"? Esta acción no se puede deshacer.`)) {
      return;
    }
    this.eliminandoId = mascota.id!;
    this.adminService.eliminarMascota(mascota.id!).subscribe({
      next: () => {
        this.notification.success('Publicación eliminada');
        this.cargarMascotas();
        this.eliminandoId = null;
        this.cdr.detectChanges();
      },
      error: (err) => {
        this.notification.error(err?.error?.message || 'Error al eliminar');
        this.eliminandoId = null;
        this.cdr.detectChanges();
      }
    });
  }

  getFotoSegura(base64: string): SafeUrl {
    if (!base64) return '';
    const img = base64.startsWith('data:image') ? base64 : 'data:image/jpeg;base64,' + base64;
    return this.sanitizer.bypassSecurityTrustUrl(img);
  }

  getEstadoLabel(estado: string): string {
    const labels: Record<string, string> = {
      'PERDIDO_PROPIO': 'Perdido',
      'PERDIDO_AJENO': 'Visto',
      'RECUPERADO': 'Recuperado',
      'ADOPTADO': 'Adoptado'
    };
    return labels[estado] || estado;
  }
}
