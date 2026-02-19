import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { UsuarioService, UsuarioRanking } from '../../services/usuario.service';

@Component({
  selector: 'app-ranking',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './ranking.component.html',
  styleUrls: []
})
export class RankingComponent implements OnInit {
  ranking: UsuarioRanking[] = [];
  loading = true;
  error = false;
  limit = 20;

  constructor(private usuarioService: UsuarioService) {}

  ngOnInit(): void {
    this.cargarRanking();
  }

  cargarRanking() {
    this.loading = true;
    this.error = false;
    this.usuarioService.obtenerRanking(this.limit).subscribe({
      next: (data) => {
        this.ranking = data;
        this.loading = false;
      },
      error: () => {
        this.error = true;
        this.loading = false;
      }
    });
  }

  getMedalla(index: number): string {
    if (index === 0) return '🥇';
    if (index === 1) return '🥈';
    if (index === 2) return '🥉';
    return '';
  }

  getPosicionClase(index: number): string {
    if (index === 0) return 'bg-amber-500/20 text-amber-300 border-amber-500/30';
    if (index === 1) return 'bg-slate-400/20 text-slate-300 border-slate-500/30';
    if (index === 2) return 'bg-amber-700/30 text-amber-200 border-amber-800/40';
    return 'bg-slate-800/50 text-slate-400 border-white/10';
  }
}
