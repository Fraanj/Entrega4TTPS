import { Component } from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms'; // ⭐ AGREGAR
import { CommonModule } from '@angular/common'; // ⭐ AGREGAR
import { AuthService } from '../../../services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './login.html',
  styleUrls: ['./login.css']
})
export class LoginComponent {
  email: string = '';
  password: string = '';
  errorMessage: string = '';
  isLoading: boolean = false;

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  async onLogin() {
    if (!this.email || !this.password) {
      this.errorMessage = 'Por favor complete todos los campos';
      return;
    }

    this.isLoading = true;
    this.errorMessage = '';

    try {
      const response = await this.authService.login(this.email, this.password);
      console.log('Login exitoso:', response);
      
      this.router.navigate(['/home']);
    } catch (error: any) {
      console.error('Error en login:', error);
      this.errorMessage = 'Credenciales inválidas. Intente nuevamente.';
    } finally {
      this.isLoading = false;
    }
  }
}