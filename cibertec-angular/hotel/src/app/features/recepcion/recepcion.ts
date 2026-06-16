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
  private readonly habService = inject(HabitacionService);
  private readonly router = inject(Router);
  private readonly pisoService = inject(PisoService);
  private readonly catService = inject(CategoriaService);
  private readonly estService = inject(EstadoHabitacionService);

  // Signals
  habitaciones = signal<Habitacion[]>([]);
  pisos = signal<any[]>([]);
  idPisoSeleccionado = signal<number>(0);
  isLoading = signal(false);

  // Filtrado reactivo (si cambias piso o habitaciones, se recalcula solo)
  habitacionesFiltradas = computed(() => {
    const pId = Number(this.idPisoSeleccionado());
    return pId === 0
      ? this.habitaciones()
      : this.habitaciones().filter(h => Number(h.idPiso) === pId);
  });

  ngOnInit(): void { this.cargarDatos(); }

  // Puedes llamar a esto después de que el usuario regrese a la pantalla
refrescarDatos(): void {
  this.habService.listar().subscribe(res => {

    this.habitaciones.set(res.data ?? []);
  });
}
  cargarDatos(): void {
    this.isLoading.set(true);
    forkJoin({
      pisos: this.pisoService.listar(),
      habitaciones: this.habService.listar(),
      categorias: this.catService.listar(),
      estados: this.estService.listar()
    }).subscribe({
      next: ({ pisos, habitaciones, categorias, estados }) => {
        this.pisos.set(pisos.data ?? []);

        const catsMap = new Map((categorias.data ?? []).map(c => [Number(c.idCategoria), c.descripcion]));
        const estsMap = new Map((estados.data ?? []).map(e => [Number(e.idEstadoHabitacion), e.descripcion]));

        this.habitaciones.set((habitaciones.data ?? []).map(h => ({
          ...h,
          categoriaNombre: catsMap.get(Number(h.idCategoria)) ?? 'N/A',
          estadoDescripcion: estsMap.get(Number(h.idEstadoHabitacion)) ?? 'N/A'
        })));

        this.isLoading.set(false);
      },
      error: () => this.isLoading.set(false)
    });
  }

  abrirDetalle(id?: number): void {
    if (id) this.router.navigate(['/admin/recepciondetalle', id]);
  }
}
