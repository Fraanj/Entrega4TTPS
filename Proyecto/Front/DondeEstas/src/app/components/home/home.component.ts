import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { MascotaService } from '../../services/mascota.service';
import { AuthService } from '../../services/auth.service'; // 👈 AGREGAR
import { DomSanitizer, SafeUrl } from '@angular/platform-browser';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './home.component.html',
  styleUrl: './home.component.css'
})
export class HomeComponent implements OnInit {
  errorMessage: string = '';

  estadisticas = {
    perdidas: 0, 
    recuperadas: 1234,
    adoptadas: 567
  };

  mascotasRecientes: any[] = [];
  loading = true;

  constructor(
    private mascotaService: MascotaService,
    public authService: AuthService, // 👈 AGREGAR (public para usarlo en el template)
    private cdr: ChangeDetectorRef,
    private sanitizer: DomSanitizer
  ) {}

  ngOnInit(): void {
    this.cargarMascotas();
  }

  // Método helper para obtener el nombre
  get nombreUsuario(): string {
    const usuario = this.authService.getCurrentUser();
    return usuario?.nombre || 'Usuario';
  }

  cargarMascotas() {
    this.loading = true;
    this.errorMessage = ''; 
    this.mascotaService.getMascotasPerdidas().subscribe({
      next: (data) => {
        this.mascotasRecientes = data;
        this.estadisticas.perdidas = data.length; 
        this.loading = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('❌ ERROR DEL BACKEND:', err); 
        this.errorMessage = 'No se pudo conectar. Revisá la consola (F12).';
        this.loading = false;
        this.cdr.detectChanges();
      }
    });
  }

  getEstadoClasses(estado: string): string {
    const base = 'px-3 py-1 rounded-full text-xs font-bold uppercase tracking-wide border ';
    switch (estado) {
      case 'PERDIDO_PROPIO': 
        return base + 'bg-red-500/20 text-red-200 border-red-500/30';
      case 'PERDIDO_AJENO': 
        return base + 'bg-amber-500/20 text-amber-200 border-amber-500/30';
      case 'RECUPERADO': 
        return base + 'bg-emerald-500/20 text-emerald-200 border-emerald-500/30';
      case 'ADOPTADO': 
        return base + 'bg-blue-500/20 text-blue-200 border-blue-500/30';
      default: 
        return base + 'bg-slate-500/20 text-slate-200 border-slate-500/30';
    }
  }

  getFotoSegura(base64: string): SafeUrl {
    if (!base64) return '';

    let imagenData = base64;
    if (!imagenData.startsWith('data:image')) {
        imagenData = 'data:image/jpeg;base64,' + base64;
    }

    return this.sanitizer.bypassSecurityTrustUrl(imagenData);
  }

  getEstadoLabel(estado: string): string {
    const labels: {[key: string]: string} = {
      'PERDIDO_PROPIO': 'Perdido',
      'PERDIDO_AJENO': 'Visto',
      'RECUPERADO': 'Recuperado',
      'ADOPTADO': 'Adoptado'
    };
    return labels[estado] || estado;
  }
}