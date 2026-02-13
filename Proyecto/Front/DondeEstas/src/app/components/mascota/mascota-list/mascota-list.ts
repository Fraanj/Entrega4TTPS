import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { MascotaService } from '../../../services/mascota.service';
import { DomSanitizer, SafeUrl } from '@angular/platform-browser';

@Component({
  selector: 'app-mascota-list',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule],
  templateUrl: './mascota-list.html',
  styleUrls: [] // Tailwind
})
export class MascotaListComponent implements OnInit {
  mascotas: any[] = [];
  loading = true;
  error: string | null = null;
  
  // Filtros
  mostrarFiltros = false;
  filtros = {
    color: '',
    tamanio: '',
    estado: ''
  };

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

  aplicarFiltros() {
    this.loading = true;
    this.mascotaService.buscarConFiltros(
      this.filtros.color,
      this.filtros.tamanio,
      this.filtros.estado
    ).subscribe({
      next: (data) => {
        this.mascotas = data;
        this.loading = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error(err);
        this.error = 'Error al aplicar filtros.';
        this.loading = false;
        this.cdr.detectChanges();
      }
    });
  }

  limpiarFiltros() {
    this.filtros = {
      color: '',
      tamanio: '',
      estado: ''
    };
    this.cargarMascotas();
  }

  toggleFiltros() {
    this.mostrarFiltros = !this.mostrarFiltros;
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