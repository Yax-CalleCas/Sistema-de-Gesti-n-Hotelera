import { Component, OnInit, inject, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { forkJoin } from 'rxjs';
import Swal from 'sweetalert2';

// Servicios
import { HabitacionService } from '../../core/services/habitacion.service';
import { PisoService } from '../../core/services/piso.service';
import { CategoriaService } from '../../core/services/categoria.service';
import { EstadoHabitacionService } from '../../core/services/EstadoHabitacionService';

// Modelos
import { Habitacion } from '../../core/models/Habitacion';
import { Piso } from '../../core/models/piso';

@Component({
  selector: 'app-salida-habitacion',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './salida-habitacion.html',
})
export class SalidaHabitacion implements OnInit {
  private readonly habService = inject(HabitacionService);
  private readonly router = inject(Router);
  private readonly cdr = inject(ChangeDetectorRef);
  private readonly pisoService = inject(PisoService);
  private readonly catService = inject(CategoriaService);
  private readonly estService = inject(EstadoHabitacionService);

  habitaciones: Habitacion[] = [];
  pisos: Piso[] = [];
  idPisoSeleccionado = 0;
  isLoading = false;

  ngOnInit(): void {
    this.cargarPisos();
    this.cargarHabitacionesOcupadas();
  }

  cargarPisos(): void {
    this.pisoService.listar().subscribe({
      next: (res) => {
        this.pisos = res.data ?? [];
        this.cdr.markForCheck();
      },
      error: (err) => {
        if (err.status === 403 || err.status === 401) {
          this.manejarExpiracionSesion();
        } else {
          console.error('Error al cargar pisos:', err);
        }
      }
    });
  }

  cargarHabitacionesOcupadas(): void {
    this.isLoading = true;
    forkJoin({
      habitaciones: this.habService.listar(),
      categorias: this.catService.listar(),
      estados: this.estService.listar()
    }).subscribe({
      next: (res) => {
        const cats = res.categorias.data ?? [];
        const ests = res.estados.data ?? [];
        const completas = res.habitaciones.data ?? [];

        this.habitaciones = completas
          .filter(h => Number(h.idEstadoHabitacion) === 2)
          .map(h => {
            const cat = cats.find(c => Number(c.idCategoria) === Number(h.idCategoria));
            const est = ests.find(e => Number(e.idEstadoHabitacion) === Number(h.idEstadoHabitacion));
            return {
              ...h,
              categoriaNombre: cat?.descripcion ?? 'N/A',
              estadoDescripcion: est?.descripcion ?? 'Ocupado'
            };
          });

        this.isLoading = false;
        this.cdr.markForCheck();
      },
      error: (err) => {
        this.isLoading = false;
        if (err.status === 403 || err.status === 401) {
          this.manejarExpiracionSesion();
        } else {
          console.error('Error al recuperar habitaciones para salida:', err);
          Swal.fire('Error', 'No se pudieron recuperar los datos maestros del servidor.', 'error');
        }
      }
    });
  }

  get habitacionesOcupadasFiltradas(): Habitacion[] {
    const pId = Number(this.idPisoSeleccionado);
    return pId === 0 ? this.habitaciones : this.habitaciones.filter(h => Number(h.idPiso) === pId);
  }

  procesarSalidaVenta(h: Habitacion): void {
    if (h.idHabitacion) {
      console.log('Navegando a salida con ID:', h.idHabitacion);
      this.router.navigate(['/admin/procesarsalida', h.idHabitacion]);
    } else {
      console.error('La habitación seleccionada no cuenta con un idHabitacion válido.');
    }
  }

  private manejarExpiracionSesion(): void {
    Swal.fire('Sesión Caducada', 'No tienes permisos o tu sesión expiró. Inicia sesión nuevamente.', 'error')
      .then(() => {
        this.router.navigate(['/login']);
      });
  }
}
