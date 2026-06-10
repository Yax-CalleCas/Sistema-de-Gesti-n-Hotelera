import { Component, OnInit, inject, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import { forkJoin } from 'rxjs';
import Swal from 'sweetalert2';

import { HabitacionService } from '../../core/services/habitacion.service';
import { PisoService } from '../../core/services/piso.service';
import { CategoriaService } from '../../core/services/categoria.service';
import { Habitacion } from '../../core/models/Habitacion';

interface HabCardExtendida extends Habitacion {
  categoriaNombre: string;
}

@Component({
  selector: 'app-listahabitacionesocupadas',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './listahabitacionesocupadas.html',
  styleUrls: ['./listahabitacionesocupadas.css']
})
export class ListaHabitacionesEstadoComponent implements OnInit {
  private readonly habService = inject(HabitacionService);
  private readonly router = inject(Router);
  private readonly route = inject(ActivatedRoute);
  private readonly cdr = inject(ChangeDetectorRef);
  private readonly pisoService = inject(PisoService);
  private readonly catService = inject(CategoriaService);

  habitaciones: HabCardExtendida[] = [];
  pisos: any[] = [];
  idPisoSeleccionado = 0;
  isLoading = false;

  // Estado del filtro obtenido por la ruta (1 = Disponible, 2 = Ocupado)
  estadoFiltro: number = 2;

  ngOnInit(): void {
    // Captura los metadatos configurados en el app.routes.ts de manera segura
    this.estadoFiltro = this.route.snapshot.data['tipoEstado'] ?? 2;

    this.cargarPisos();
    this.cargarHabitacionesporEstado();
  }

  cargarPisos(): void {
    this.pisoService.listar().subscribe({
      next: (res) => {
        this.pisos = res.data ?? [];
        this.cdr.markForCheck();
      },
      error: () => console.error('Error al cargar la lista de pisos')
    });
  }

  cargarHabitacionesporEstado(): void {
    this.isLoading = true;

    forkJoin({
      habitaciones: this.habService.listar(),
      categorias: this.catService.listar()
    }).subscribe({
      next: (res) => {
        const cats = res.categorias.data ?? [];

        this.habitaciones = (res.habitaciones.data ?? [])
          .filter((h: Habitacion) => Number(h.idEstadoHabitacion) === this.estadoFiltro && h.estado !== false)
          .map((h: Habitacion) => ({
            ...h,
            categoriaNombre: cats.find(c => Number(c.idCategoria) === Number(h.idCategoria))?.descripcion ?? 'Estándar'
          }));

        this.isLoading = false;
        this.cdr.markForCheck();
      },
      error: () => {
        this.isLoading = false;
        this.cdr.markForCheck();
        Swal.fire('Error', 'No se pudieron recuperar las habitaciones desde el servidor.', 'error');
      }
    });
  }

  irAVenta(idHabitacion: number | undefined): void {
    if (!idHabitacion) return;
    this.router.navigate(['/admin/ventaproductos', idHabitacion]);
  }

  get habitacionesFiltradas(): HabCardExtendida[] {
    const pId = Number(this.idPisoSeleccionado);
    return pId === 0 ? this.habitaciones : this.habitaciones.filter(h => Number(h.idPiso) === pId);
  }

  get esVistaOcupadas(): boolean {
    return this.estadoFiltro === 2;
  }
}
