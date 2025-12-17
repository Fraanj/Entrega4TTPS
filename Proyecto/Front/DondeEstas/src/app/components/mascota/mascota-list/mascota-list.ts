import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { MascotaService } from '../../../services/mascota.service';
import { DomSanitizer, SafeUrl } from '@angular/platform-browser';

@Component({
  selector: 'app-mascota-list',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './mascota-list.html',
  styleUrls: [] // Tailwind
})
export class MascotaListComponent implements OnInit {
  mascotas: any[] = [];
  loading = true;
  error: string | null = null;

  constructor(
    private mascotaService: MascotaService,
    private cdr: ChangeDetectorRef,
    private sanitizer: DomSanitizer
  ) {}

  ngOnInit(): void {
    this.cargarMascotas();
  }

  cargarMascotas() {
    this.loading = true;
    this.mascotaService.getMascotas().subscribe({
      next: (data) => {
        this.mascotas = data;
        this.loading = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error(err);
        this.error = 'No se pudieron cargar las mascotas.';
        this.loading = false;
        this.cdr.detectChanges();
      }
    });
  }

  getFotoSegura(base64: string): SafeUrl {
    if (!base64) return '';
    let img = base64.startsWith('data:image') ? base64 : 'data:image/jpeg;base64,' + base64;
    return this.sanitizer.bypassSecurityTrustUrl(img);
  }

  // Helpers para colores (Igual que en Home)
  getEstadoClass(estado: string): string {
    const base = 'px-2 py-1 rounded text-xs font-bold uppercase ';
    return estado === 'PERDIDO_PROPIO' ? base + 'bg-red-100 text-red-700' : base + 'bg-orange-100 text-orange-700';
  }
}