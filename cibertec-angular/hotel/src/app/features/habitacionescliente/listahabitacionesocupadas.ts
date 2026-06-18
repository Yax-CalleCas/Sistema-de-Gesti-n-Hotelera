import { Component, OnInit, inject, computed, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import { forkJoin } from 'rxjs';
import Swal from 'sweetalert2';
import { HabitacionService } from '../../core/services/habitacion.service';
import { PisoService } from '../../core/services/piso.service';
import { CategoriaService } from '../../core/services/categoria.service';
import { Habitacion } from '../../core/models/Habitacion';

@Component({
  selector: 'app-listahabitacionesocupadas',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './listahabitacionesocupadas.html',
  styleUrls: ['./listahabitacionesocupadas.css']
})
export class ListaHabitacionesEstadoComponent implements OnInit {
  // Inyecciones modernas
  private readonly habService = inject(HabitacionService);
  private readonly router = inject(Router);
  private readonly route = inject(ActivatedRoute);
  private readonly pisoService = inject(PisoService);
  private readonly catService = inject(CategoriaService);

  // Estados como Signals (reactividad granular)
  habitaciones = signal<Habitacion[]>([]);
  pisos = signal<any[]>([]);
  idPisoSeleccionado = signal<number>(0);
  estadoFiltro = signal<number>(2); // 2 suele ser ocupado por convención
  isLoading = signal<boolean>(false);
  categoriasMap = signal<Map<number, string>>(new Map());

  // Computados reactivos (se recalculan solo cuando cambian sus dependencias)
  readonly esVistaOcupadas = computed(() => this.estadoFiltro() === 2);

  readonly habitacionesFiltradas = computed(() => {
    const pId = Number(this.idPisoSeleccionado());
    const lista = this.habitaciones();
    return pId === 0 ? lista : lista.filter(h => Number(h.idPiso) === pId);
  });

  ngOnInit(): void {
    // Captura el parámetro de estado desde el routing, por defecto 2
    this.estadoFiltro.set(this.route.snapshot.data['tipoEstado'] ?? 2);
    this.cargarDatos();
  }

  private cargarDatos(): void {
    this.isLoading.set(true);

    // Carga paralela de catálogos
    forkJoin({
      h: this.habService.listar(),
      c: this.catService.listar(),
      p: this.pisoService.listar()
    }).subscribe({
      next: ({ h, c, p }) => {
        // Filtrar habitaciones por el estado configurado
        this.habitaciones.set((h.data || []).filter(item =>
          Number(item.idEstadoHabitacion) === this.estadoFiltro() && item.estado !== false
        ));

        this.pisos.set(p.data || []);

        // Crear mapa para búsqueda rápida de categorías
        const map = new Map<number, string>();
        c.data?.forEach(cat => map.set(Number(cat.idCategoria), cat.descripcion));
        this.categoriasMap.set(map);

        this.isLoading.set(false);
      },
      error: (err) => {
        this.isLoading.set(false);
        console.error(err);
        Swal.fire('Error', 'No se pudieron recuperar los datos.', 'error');
      }
    });
  }

  // Método helper para el template
  getCategoriaNombre(id: any): string {
    return this.categoriasMap().get(Number(id)) || 'Estándar';
  }

  trackById(index: number, item: any): number {
  return item.idHabitacion;
}
  // Navegación al componente de ventas
  irAVenta(id?: number): void {
    if (id) this.router.navigate(['/admin/ventaproductos', id]);
  }
}
