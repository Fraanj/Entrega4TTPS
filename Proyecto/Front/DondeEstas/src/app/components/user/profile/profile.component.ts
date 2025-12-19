import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { AuthService } from '../../../services/auth.service';
import { UsuarioService } from '../../../services/usuario.service';
import { CiudadService } from '../../../services/ciudad.service';
import { NotificationService } from '../../../services/notificacion.services';
import { Usuario } from '../../../models/usuario.model';
import { Ciudad } from '../../../models/ubicacion.model';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule],
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.css']
})
export class ProfileComponent implements OnInit {
  usuario: Usuario | null = null;
  modoEdicion: boolean = false;
  isLoading: boolean = false;
  loadingCiudades: boolean = false;
  ciudades: Ciudad[] = [];
  
  // Formulario de edición
  formData = {
    nombre: '',
    apellido: '',
    email: '', // 👈 AGREGAR (obligatorio para el backend)
    telefono: '',
    ciudad: '',
    password: ''
  };

  constructor(
    public authService: AuthService,
    private usuarioService: UsuarioService,
    private ciudadService: CiudadService,
    private notificationService: NotificationService,
    private router: Router
  ) {}

  ngOnInit() {
    this.usuario = this.authService.getCurrentUser();
    
    if (!this.usuario) {
      this.router.navigate(['/login']);
      return;
    }

    // Inicializar formulario con datos actuales
    this.formData = {
      nombre: this.usuario.nombre,
      apellido: this.usuario.apellido,
      email: this.usuario.email, // 👈 AGREGAR
      telefono: this.usuario.telefono || '',
      ciudad: this.usuario.ciudad || '',
      password: ''
    };
  }

  getIniciales(): string {
    if (!this.usuario) return 'U';
    const nombre = this.usuario.nombre?.charAt(0).toUpperCase() || '';
    const apellido = this.usuario.apellido?.charAt(0).toUpperCase() || '';
    return nombre + apellido;
  }

  async activarModoEdicion() {
    this.modoEdicion = true;
    
    if (this.ciudades.length === 0) {
      await this.cargarCiudades();
    }
  }

  cancelarEdicion() {
    this.modoEdicion = false;
    
    if (this.usuario) {
      this.formData = {
        nombre: this.usuario.nombre,
        apellido: this.usuario.apellido,
        email: this.usuario.email, // 👈 RESTAURAR
        telefono: this.usuario.telefono || '',
        ciudad: this.usuario.ciudad || '',
        password: ''
      };
    }
  }

  async cargarCiudades() {
    this.loadingCiudades = true;
    try {
      this.ciudades = await this.ciudadService.getCiudadesBuenosAires();
    } catch (error) {
      console.error('Error al cargar ciudades:', error);
      this.ciudades = [
        { id: '1', nombre: 'La Plata', provincia: { id: '06', nombre: 'Buenos Aires' } },
        { id: '2', nombre: 'Buenos Aires', provincia: { id: '06', nombre: 'Buenos Aires' } }
      ];
    } finally {
      this.loadingCiudades = false;
    }
  }

  async guardarCambios() {
    if (!this.usuario) return;

    // Validaciones
    if (!this.formData.nombre || !this.formData.apellido || !this.formData.email) {
      this.notificationService.warning('Nombre, apellido y email son obligatorios', 'Datos incompletos');
      return;
    }

    // Validar contraseña si se ingresó
    if (this.formData.password && this.formData.password.length < 8) {
      this.notificationService.warning('La contraseña debe tener al menos 8 caracteres', 'Contraseña inválida');
      return;
    }

    this.isLoading = true;

    try {
      // 🔧 Preparar datos: SIEMPRE enviar nombre, apellido, email
      const datosActualizar: any = {
        nombre: this.formData.nombre,
        apellido: this.formData.apellido,
        email: this.formData.email, // 👈 OBLIGATORIO
        telefono: this.formData.telefono || '', // Enviar string vacío si no hay valor
        ciudad: this.formData.ciudad || '' // Enviar string vacío si no hay valor
      };

      // Solo agregar password si el usuario lo ingresó
      if (this.formData.password && this.formData.password.trim() !== '') {
        datosActualizar.password = this.formData.password;
      }

      console.log('📤 Datos a enviar:', datosActualizar); // Debug

      // Actualizar en el backend
      const usuarioActualizado = await this.usuarioService.actualizarPerfil(
        this.usuario.id,
        datosActualizar
      );

      // Actualizar en localStorage
      const usuarioLocal = { 
        ...this.usuario, 
        nombre: usuarioActualizado.nombre,
        apellido: usuarioActualizado.apellido,
        email: usuarioActualizado.email,
        telefono: usuarioActualizado.telefono,
        ciudad: usuarioActualizado.ciudad
      };
      localStorage.setItem('usuario', JSON.stringify(usuarioLocal));
      this.usuario = usuarioLocal;

      // Limpiar contraseña del formulario
      this.formData.password = '';

      // Salir del modo edición
      this.modoEdicion = false;

      this.notificationService.success(
        datosActualizar.password ? 'Tu perfil y contraseña han sido actualizados' : 'Tu perfil ha sido actualizado correctamente', 
        'Perfil actualizado'
      );
    } catch (error: any) {
      console.error('❌ Error al actualizar perfil:', error);
      // El interceptor ya mostró el error
    } finally {
      this.isLoading = false;
    }
  }

  onLogout() {
    this.authService.logout();
  }
}