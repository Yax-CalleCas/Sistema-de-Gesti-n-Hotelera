import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';

@Component({
  selector: 'app-layout',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './layout.html',
  styleUrls: ['./layout.scss']
})
export class Layout {
  private readonly router = inject(Router);

  // Obtenemos el usuario del localStorage para mostrarlo en el header
  get usuarioNombre(): string {
    return localStorage.getItem('usuario') || 'Usuario';
  }

  cerrarSesion(): void {
    localStorage.clear(); // Limpieza total
    this.router.navigate(['/login']);
  }
}
