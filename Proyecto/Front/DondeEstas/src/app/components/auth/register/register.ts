import { Component, OnInit } from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../../services/auth.service';
import { CiudadService } from '../../../services/ciudad.service';
import { Ciudad } from '../../../models/ubicacion.model';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './register.html',
  styleUrls: ['./register.css']
})
export class RegisterComponent implements OnInit {
  formData = {
    nombre: '',
    apellido: '',
    email: '',
    password: '',
    telefono: '',
    ciudad: ''
  };
  
  ciudades: Ciudad[] = [];
  loadingCiudades: boolean = false;
  errorMessage: string = '';
  isLoading: boolean = false;

  constructor(
    private authService: AuthService,
    private ciudadService: CiudadService,
    private router: Router
  ) {}

  async ngOnInit() {
    await this.cargarCiudades();
  }

  async cargarCiudades() {
    this.loadingCiudades = true;
    try {
      // Opción 1: Solo Buenos Aires (más rápido)
      this.ciudades = await this.ciudadService.getCiudadesBuenosAires();
      
      // Opción 2: Todas las ciudades (más lento)
      // this.ciudades = await this.ciudadService.getCiudades();
      
      console.log(`✅ ${this.ciudades.length} ciudades cargadas`);
    } catch (error) {
      console.error('Error al cargar ciudades:', error);
      // Fallback a ciudades hardcodeadas
      this.ciudades = [
        { id: '1', nombre: 'La Plata', provincia: { id: '06', nombre: 'Buenos Aires' } },
        { id: '2', nombre: 'Buenos Aires', provincia: { id: '06', nombre: 'Buenos Aires' } },
        { id: '3', nombre: 'Córdoba', provincia: { id: '14', nombre: 'Córdoba' } }
      ];
    } finally {
      this.loadingCiudades = false;
    }
  }

  async onRegister() {
    if (!this.formData.nombre || !this.formData.apellido || !this.formData.email || !this.formData.password) {
      this.errorMessage = 'Por favor complete los campos obligatorios';
      return;
    }

    this.isLoading = true;
    this.errorMessage = '';

    try {
      const response = await this.authService.register(this.formData);
      console.log('Registro exitoso:', response);
      
      this.router.navigate(['/home']);
    } catch (error: any) {
      console.error('Error en registro:', error);
      // El interceptor ya mostró el error
      this.errorMessage = error?.error?.message || 'Error al registrar usuario';
    } finally {
      this.isLoading = false;
    }
  }
}