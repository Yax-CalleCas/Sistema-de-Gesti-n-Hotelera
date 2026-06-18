import { Component, OnInit, inject, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { forkJoin } from 'rxjs';

import { HabitacionService } from '../../core/services/habitacion.service';
import { PisoService } from '../../core/services/piso.service';
import { CategoriaService } from '../../core/services/categoria.service';
import { EstadoHabitacionService } from '../../core/services/EstadoHabitacionService';
import { Habitacion } from '../../core/models/Habitacion';

@Component({
  selector: 'app-recepcion',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './recepcion.html'
})
export class RecepcionComponent implements OnInit {
  // Inyección de dependencias
  private readonly habService = inject(HabitacionService);
  private readonly router = inject(Router);
  private readonly pisoService = inject(PisoService);
  private readonly catService = inject(CategoriaService);
  private readonly estService = inject(EstadoHabitacionService);

  // Signals para estado de UI y datos
  habitaciones = signal<Habitacion[]>([]);
  pisos = signal<any[]>([]);
  idPisoSeleccionado = signal<number>(0);
  isLoading = signal(false);

  // Mapas para resolución eficiente de nombres (O(1) complejidad)
  private catsMap = new Map<number, string>();
  private estsMap = new Map<number, string>();

  // Filtrado reactivo: se recalcula automáticamente si cambia el piso o las habitaciones
  habitacionesFiltradas = computed(() => {
    const pId = Number(this.idPisoSeleccionado());
    return pId === 0
      ? this.habitaciones()
      : this.habitaciones().filter(h => Number(h.idPiso) === pId);
  });

  ngOnInit(): void {
    this.cargarDatos();
  }

  /**
   * Carga inicial de datos mediante forkJoin para asegurar
   * que todo el tablero esté listo antes de mostrar la UI.
   */
  cargarDatos(): void {
    this.isLoading.set(true);
    forkJoin({
      pisos: this.pisoService.listar(),
      habitaciones: this.habService.listar(),
      categorias: this.catService.listar(),
      estados: this.estService.listar()
    }).subscribe({
      next: ({ pisos, habitaciones, categorias, estados }) => {
        // Asignación de catálogos
        this.pisos.set(pisos.data ?? []);

        // Construcción de mapas para lookup rápido en el HTML
        this.catsMap = new Map((categorias.data ?? []).map(c => [Number(c.idCategoria), c.descripcion]));
        this.estsMap = new Map((estados.data ?? []).map(e => [Number(e.idEstadoHabitacion), e.descripcion]));

        // Asignación de habitaciones
        this.habitaciones.set(habitaciones.data ?? []);
        this.isLoading.set(false);
      },
      error: () => this.isLoading.set(false)
    });
  }

  // Métodos de resolución de datos para la vista
  getCatNombre(id: number): string {
    return this.catsMap.get(Number(id)) ?? 'N/A';
  }

  getEstNombre(id: number): string {
    return this.estsMap.get(Number(id)) ?? 'N/A';
  }

  abrirDetalle(id?: number): void {
    if (id) {
      this.router.navigate(['/admin/recepciondetalle', id]);
    }
  }

  refrescarDatos(): void {
    this.habService.listar().subscribe(res => {
      this.habitaciones.set(res.data ?? []);
    });
  }
}
