import { Component } from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../../services/auth.service';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './register.html',
  styleUrls: ['./register.css']
})
export class RegisterComponent {
  formData = {
    nombre: '',
    apellido: '',
    email: '',
    password: '',
    telefono: '',
    ciudad: ''
  };
  
  errorMessage: string = '';
  isLoading: boolean = false;

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  async onRegister() {
    if (!this.formData.nombre || !this.formData.email || !this.formData.password) {
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
      this.errorMessage = 'Error al registrar usuario. Intente nuevamente.';
    } finally {
      this.isLoading = false;
    }
  }
}