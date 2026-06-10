import { Component, OnInit, inject, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { forkJoin } from 'rxjs';

// Servicios
import { HabitacionService } from '../../core/services/habitacion.service';
import { PisoService } from '../../core/services/piso.service';
import { CategoriaService } from '../../core/services/categoria.service';
import { EstadoHabitacionService } from '../../core/services/EstadoHabitacionService';

// Modelos
import { Habitacion } from '../../core/models/Habitacion';
import { Piso } from '../../core/models/piso';

@Component({
  selector: 'app-recepcion',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './recepcion.html'
})
export class RecepcionComponent implements OnInit {
  private readonly habService = inject(HabitacionService);
  private readonly router = inject(Router);
  private readonly cdr = inject(ChangeDetectorRef);
  private readonly pisoService = inject(PisoService);
  private readonly catService = inject(CategoriaService);
  private readonly estService = inject(EstadoHabitacionService);

  habitaciones: Habitacion[] = [];
  pisos: Piso[] = [];
  idPisoSeleccionado: number = 0;
  isLoading: boolean = false;

  ngOnInit(): void {
    this.cargarDatosIniciales();
  }

  private cargarDatosIniciales(): void {
    this.isLoading = true;

    // Ejecución paralela eficiente
    forkJoin({
      pisos: this.pisoService.listar(),
      habitaciones: this.habService.listar(),
      categorias: this.catService.listar(),
      estados: this.estService.listar()
    }).subscribe({
      next: (res) => {
        this.pisos = res.pisos.data ?? [];
        const cats = res.categorias.data ?? [];
        const ests = res.estados.data ?? [];

        this.habitaciones = (res.habitaciones.data ?? []).map(h => ({
          ...h,
          categoriaNombre: cats.find(c => Number(c.idCategoria) === Number(h.idCategoria))?.descripcion ?? 'N/A',
          estadoDescripcion: ests.find(e => Number(e.idEstadoHabitacion) === Number(h.idEstadoHabitacion))?.descripcion ?? 'N/A'
        }));

        this.isLoading = false;
        this.cdr.markForCheck();
      },
      error: (err) => {
        console.error('Error en carga de datos:', err);
        this.isLoading = false;
        this.cdr.markForCheck();
      }
    });
  }

  // Getter optimizado y seguro para el template
  get habitacionesFiltradas(): Habitacion[] {
    const pId = Number(this.idPisoSeleccionado);
    return pId === 0
      ? this.habitaciones
      : this.habitaciones.filter(h => Number(h.idPiso) === pId);
  }

  onPisoChange(): void {
    this.cdr.markForCheck();
  }

  abrirDetalle(h: Habitacion): void {
    if (h.idHabitacion) {
      this.router.navigate(['/admin/recepciondetalle', h.idHabitacion]);
    }
  }
}
