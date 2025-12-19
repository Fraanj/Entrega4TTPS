import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { AvistamientoService } from '../../../services/avistamiento.service';
import { AuthService } from '../../../services/auth.service';
import { DomSanitizer, SafeUrl } from '@angular/platform-browser';
import { AvistamientoDetailModalComponent } from '../avistamiento-detail/avistamiento-detail'; 

@Component({
  selector: 'app-avistamiento-list',
  standalone: true,
  imports: [CommonModule, RouterModule, AvistamientoDetailModalComponent],
  templateUrl: './avistamiento-list.html'
})
export class AvistamientoListComponent implements OnInit {
  avistamientoSeleccionado: any = null;
  avistamientos: any[] = [];
  loading = true;
  soloMios = false;

  constructor(
    private avistamientoService: AvistamientoService,
    private sanitizer: DomSanitizer,
    private authService: AuthService 
  ) {}

  ngOnInit() {
    this.cargarAvistamientos();
  }

  verDetalle(avistamiento: any) {
    this.avistamientoSeleccionado = avistamiento;
  }

  cerrarDetalle() {
    this.avistamientoSeleccionado = null;
  }

  cargarAvistamientos() {
    this.loading = true;
    const request$ = this.soloMios 
      ? this.avistamientoService.getMisAvistamientos()
      : this.avistamientoService.getAvistamientos();

    request$.subscribe({
      next: (data) => {
        this.avistamientos = data;
        this.loading = false;
      },
      error: () => this.loading = false
    });
  }

  isLoggedIn(): boolean {
    return this.authService.isAuthenticated();
  }

  toggleFiltro() {
    this.soloMios = !this.soloMios;
    this.cargarAvistamientos();
  }

  getFotoSegura(base64: string): SafeUrl {
    if (!base64) return '';
    let img = base64.startsWith('data:image') ? base64 : 'data:image/jpeg;base64,' + base64;
    return this.sanitizer.bypassSecurityTrustUrl(img);
  }
}