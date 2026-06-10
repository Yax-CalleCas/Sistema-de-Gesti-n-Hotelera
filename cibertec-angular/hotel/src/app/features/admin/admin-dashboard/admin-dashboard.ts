import { Component } from '@angular/core';
import { Router, RouterLink } from '@angular/router';

@Component({
  selector: 'app-admin-dashboard',
  standalone: true,
  templateUrl: './admin-dashboard.html'
})
export class AdminDashboard {

  constructor(private router: Router) {}

  cerrarSesion(): void {


    localStorage.removeItem('token');

    localStorage.removeItem('usuario');
    localStorage.removeItem('rol');

    // Redirige al login
    this.router.navigate(['/login']);
  }
}
