import { Component, OnInit, inject, signal, computed, ViewChild, ElementRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { forkJoin } from 'rxjs';
import Swal from 'sweetalert2';

import { HabitacionService } from '../../core/services/habitacion.service';
import { EstadoHabitacionService } from '../../core/services/EstadoHabitacionService';
import { CategoriaService } from '../../core/services/categoria.service';
import { PisoService } from '../../core/services/piso.service';
import { Habitacion } from '../../core/models/Habitacion';

// Definición constante fuera de la clase para optimizar rendimiento
const ESTADO_BADGE_MAP: Record<number, string> = {
  1: 'bg-success', 2: 'bg-danger', 3: 'bg-warning text-dark'
};

@Component({
  selector: 'app-habitacion-list',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './habitacion-list.html',
  styleUrls: ['./habitacion-list.css']
})
export class HabitacionListComponent implements OnInit {
  private readonly service = inject(HabitacionService);
  private readonly estadoService = inject(EstadoHabitacionService);
  private readonly categoriaService = inject(CategoriaService);
  private readonly pisoService = inject(PisoService);

  // Referencia al modal para evitar acceso global a window
  @ViewChild('modalHab') modalElement!: ElementRef;

  // Estados reactivos
  habitaciones = signal<any[]>([]);
  filtroTexto = signal('');
  isLoading = signal(false);
  isProcessing = signal(false);

  // Catálogos
  categorias = signal<any[]>([]);
  pisos = signal<any[]>([]);
  estados = signal<any[]>([]);
paginaActual = signal(1);
readonly registrosPorPagina = 10;


  private estadosMap = new Map<number, string>();
  private categoriasMap = new Map<number, string>();
  private pisosMap = new Map<number, string>();

  habitacion = signal<Habitacion>(this.nuevoModelo());

  // Filtrado reactivo
  habitacionesFiltradas = computed(() => {
    const filtro = this.filtroTexto().toLowerCase().trim();
    return this.habitaciones().filter(h =>
      h.numero.toLowerCase().includes(filtro) ||
      h.categoriaDesc?.toLowerCase().includes(filtro)
    );
  });

  ngOnInit(): void { this.cargarDatos(); }

  cargarDatos(): void {
    this.isLoading.set(true);
    forkJoin({
      h: this.service.listar(),
      e: this.estadoService.listar(),
      c: this.categoriaService.listar(),
      p: this.pisoService.listar()
    }).subscribe({
      next: ({ h, e, c, p }) => {
        this.estados.set(e.data || []);
        this.categorias.set((c.data || []).filter(x => x.estado));
        this.pisos.set((p.data || []).filter(x => x.estado));

        // Actualización eficiente de mapas
        this.estadosMap = new Map(this.estados().map(x => [x.idEstadoHabitacion, x.descripcion]));
        this.categoriasMap = new Map(this.categorias().map(x => [x.idCategoria, x.descripcion]));
        this.pisosMap = new Map(this.pisos().map(x => [x.idPiso, x.descripcion]));

        this.habitaciones.set(
  (h.data || [])
    .map(item => ({
      ...item,
      categoriaDesc: this.categoriasMap.get(item.idCategoria) || 'N/A',
      pisoDesc: this.pisosMap.get(item.idPiso) || 'N/A',
      estadoHabitacionDesc:
        this.estadosMap.get(item.idEstadoHabitacion) || 'Desconocido'
    }))
    .sort((a, b) => (b.idHabitacion || 0) - (a.idHabitacion || 0))
);

        this.isLoading.set(false);
      },
      error: () => {
        this.isLoading.set(false);
        Swal.fire('Error', 'No se pudieron cargar los datos', 'error');
      }
    });
  }

  abrir(h?: Habitacion): void {
    this.habitacion.set(h ? { ...h, urlsImagenes: [...(h.urlsImagenes || [])] } : this.nuevoModelo());
    this.toggleModal(true);
  }

  guardar(): void {
    this.isProcessing.set(true);
    this.service.guardar(this.habitacion()).subscribe({
      next: () => {
        Swal.fire('Éxito', 'Operación correcta', 'success');
        this.cargarDatos();
        this.cerrarModal();
      },
      error: () => {
        Swal.fire('Error', 'No se pudo guardar la información', 'error');
        this.isProcessing.set(false);
      },
      complete: () => this.isProcessing.set(false)
    });
  }

  eliminar(id?: number): void {
    if (!id) return;
    Swal.fire({
      title: '¿Confirmar baja?',
      text: 'Esta acción no se puede deshacer',
      icon: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#d33',
      cancelButtonColor: '#6c757d',
      confirmButtonText: 'Sí, eliminar'
    }).then((res) => {
      if (res.isConfirmed) {
        this.service.eliminar(id).subscribe(() => this.cargarDatos());
      }
    });
  }

  agregarImagen(url: string): void {
    const cleanUrl = url.trim();
    if (!cleanUrl.startsWith('http')) return;
    this.habitacion.update(h => ({ ...h, urlsImagenes: [...(h.urlsImagenes || []), cleanUrl] }));
  }

  eliminarImagen(index: number): void {
    this.habitacion.update(h => {
      const nuevas = [...(h.urlsImagenes || [])];
      nuevas.splice(index, 1);
      return { ...h, urlsImagenes: nuevas };
    });
  }


  getEstadoBadgeClass(id: number): string {
    return ESTADO_BADGE_MAP[id] || 'bg-secondary';
  }

  // Métodos privados de control
  private toggleModal(show: boolean): void {
    const modalEl = document.getElementById('modalHab');
    if (modalEl) {
      const modal = (window as any).bootstrap.Modal.getOrCreateInstance(modalEl);
      show ? modal.show() : modal.hide();
    }
  }

  cerrarModal(): void { this.toggleModal(false); }

  trackByHabitacion = (index: number, h: any) => h.idHabitacion;

  totalPaginas = computed(() =>
  Math.ceil(this.habitacionesFiltradas().length / this.registrosPorPagina)
);

habitacionesPaginadas = computed(() => {
  const inicio =
    (this.paginaActual() - 1) * this.registrosPorPagina;

  return this.habitacionesFiltradas().slice(
    inicio,
    inicio + this.registrosPorPagina
  );
});

cambiarPagina(pagina: number): void {
  if (
    pagina < 1 ||
    pagina > this.totalPaginas() ||
    pagina === this.paginaActual()
  ) {
    return;
  }

  this.paginaActual.set(pagina);
}
  private nuevoModelo(): Habitacion {
    return {
      numero: '',
      detalle: '',
      precio: 0,
      idEstadoHabitacion: 1,
      idPiso: 1,
      idCategoria: 1,
      estado: true,
      urlsImagenes: []
    };
  }
}
