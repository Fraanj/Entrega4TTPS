import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { AdminService } from '../../../services/admin.service';
import { NotificationService } from '../../../services/notificacion.services';
import { Usuario } from '../../../models/usuario.model';

@Component({
  selector: 'app-admin-usuarios',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './admin-usuarios.html',
  styleUrls: []
})
export class AdminUsuariosComponent implements OnInit {
  usuarios: Usuario[] = [];
  loading = true;
  error: string | null = null;
  accionandoId: number | null = null;

  constructor(
    private adminService: AdminService,
    private notification: NotificationService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.cargarUsuarios();
  }

  cargarUsuarios() {
    this.loading = true;
    this.error = null;
    this.adminService.listarUsuarios().subscribe({
      next: (data) => {
        this.usuarios = data;
        this.loading = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        this.error = 'No se pudieron cargar los usuarios.';
        this.loading = false;
        this.cdr.detectChanges();
      }
    });
  }

  cambiarEstado(usuario: Usuario) {
    const nuevoEstado = usuario.estadoNombre === 'HABILITADO' ? 'DESHABILITADO' : 'HABILITADO';
    this.accionandoId = usuario.id;
    this.adminService.cambiarEstadoUsuario(usuario.id, nuevoEstado).subscribe({
      next: () => {
        this.notification.success(`Estado actualizado a ${nuevoEstado}`);
        this.cargarUsuarios();
        this.accionandoId = null;
        this.cdr.detectChanges();
      },
      error: (err) => {
        this.notification.error(err?.error?.message || 'Error al cambiar estado');
        this.accionandoId = null;
        this.cdr.detectChanges();
      }
    });
  }

  eliminar(usuario: Usuario) {
    if (!confirm(`¿Eliminar al usuario ${usuario.nombre} ${usuario.apellido}? Esta acción no se puede deshacer.`)) {
      return;
    }
    this.accionandoId = usuario.id;
    this.adminService.eliminarUsuario(usuario.id).subscribe({
      next: () => {
        this.notification.success('Usuario eliminado');
        this.cargarUsuarios();
        this.accionandoId = null;
        this.cdr.detectChanges();
      },
      error: (err) => {
        this.notification.error(err?.error?.message || 'Error al eliminar');
        this.accionandoId = null;
        this.cdr.detectChanges();
      }
    });
  }
}
