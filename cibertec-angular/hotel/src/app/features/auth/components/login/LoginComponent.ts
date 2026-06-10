// src/app/features/auth/components/login/login.component.ts
import { Component, inject } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../../../core/services/auth.service';
import { LoginRequest } from '../../../../core/models/auth.models';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [ReactiveFormsModule],
  templateUrl: './LoginComponent.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent {
  private fb = inject(FormBuilder);
  private authService = inject(AuthService);
  private router = inject(Router);

  // Variable para almacenar el mensaje de error del backend
  errorMessage: string | null = null;

  loginForm: FormGroup = this.fb.group({
    correo: ['', [Validators.required, Validators.email]],
    clave: ['', [Validators.required]]
  });

  onSubmit(): void {
    if (this.loginForm.valid) {
      // Limpiamos errores previos al intentar de nuevo
      this.errorMessage = null;

      const credentials: LoginRequest = {
        correo: this.loginForm.value.correo,
        clave: this.loginForm.value.clave
      };

      this.authService.login(credentials).subscribe({
        next: (response) => {
          const role = response.data.tipoPersona;
          console.log('Login exitoso. Rol detectado:', role);

          switch (role) {
            case 'Administrador':
              this.router.navigate(['/admin']);
              break;
            case 'Empleado':
              this.router.navigate(['/empleado']);
              break;
            case 'Cliente':
              this.router.navigate(['/cliente']);
              break;
            default:
              this.router.navigate(['/dashboard']);
              break;
          }
        },
        error: (err) => {
          console.error('Error en el login:', err);

          // Suposición: El backend responde con una estructura estándar donde el error
          // viene en err.error.message o err.message. Si no, mostramos un mensaje genérico.
          if (err.status === 401 || err.status === 403) {
            this.errorMessage = 'Correo o contraseña incorrectos. Inténtalo de nuevo.';
          } else {
            this.errorMessage = 'Ocurrió un problema en el servidor. Inténtalo más tarde.';
          }
        }
      });
    }
  }
}
