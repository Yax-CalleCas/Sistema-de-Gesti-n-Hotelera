import { Component, OnInit, inject, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { forkJoin } from 'rxjs';
import Swal from 'sweetalert2';

import { HabitacionService } from '../../core/services/habitacion.service';
import { EstadoHabitacionService } from '../../core/services/EstadoHabitacionService';
import { CategoriaService } from '../../core/services/categoria.service';
import { PisoService } from '../../core/services/piso.service';
import { Habitacion } from '../../core/models/Habitacion';

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

  // Estados como Signals
  habitaciones = signal<any[]>([]);
  filtroTexto = signal('');
  isLoading = signal(false);
  isProcessing = signal(false);

  // Catálogos
  categorias = signal<any[]>([]);
  pisos = signal<any[]>([]);
  estados = signal<any[]>([]);

  // Mapas para búsqueda instantánea O(1)
  private estadosMap = new Map<number, string>();
  private categoriasMap = new Map<number, string>();
  private pisosMap = new Map<number, string>();

  habitacion = signal<Habitacion>(this.nuevoModelo());

  // Filtrado reactivo optimizado
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
        // Actualizar catálogos
        this.estados.set(e.data || []);
        this.categorias.set((c.data || []).filter(x => x.estado));
        this.pisos.set((p.data || []).filter(x => x.estado));

        // Cargar mapas para mapeo rápido
        this.estados().forEach(x => this.estadosMap.set(x.idEstadoHabitacion!, x.descripcion!));
        this.categorias().forEach(x => this.categoriasMap.set(x.idCategoria!, x.descripcion!));
        this.pisos().forEach(x => this.pisosMap.set(x.idPiso!, x.descripcion!));

        this.habitaciones.set((h.data || []).map(item => ({
          ...item,
          categoriaDesc: this.categoriasMap.get(item.idCategoria) || 'N/A',
          pisoDesc: this.pisosMap.get(item.idPiso) || 'N/A',
          estadoHabitacionDesc: this.estadosMap.get(item.idEstadoHabitacion) || 'Desconocido'
        })));
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
    const modalEl = document.getElementById('modalHab');
    if (modalEl) (window as any).bootstrap.Modal.getOrCreateInstance(modalEl).show();
  }

  guardar(): void {
    this.isProcessing.set(true);
    this.service.guardar(this.habitacion()).subscribe({
      next: () => {
        Swal.fire('Éxito', 'Operación correcta', 'success');
        this.cargarDatos();
        this.cerrarModal();
      },
      error: () => Swal.fire('Error', 'No se pudo guardar', 'error'),
      complete: () => this.isProcessing.set(false)
    });
  }

  eliminar(id?: number): void {
    if (!id) return;
    Swal.fire({
      title: '¿Confirmar baja?',
      icon: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#d33',
      confirmButtonText: 'Sí, dar de baja'
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

  trackByHabitacion = (index: number, h: any) => h.idHabitacion;

  getEstadoBadgeClass(id: number): string {
    const dict: Record<number, string> = { 1: 'bg-success', 2: 'bg-danger', 3: 'bg-warning text-dark' };
    return dict[id] || 'bg-secondary';
  }

  cerrarModal(): void {
    const modalEl = document.getElementById('modalHab');
    if (modalEl) (window as any).bootstrap.Modal.getInstance(modalEl)?.hide();
  }

  nuevoModelo(): Habitacion {
    return { numero: '', detalle: '', precio: 0, idEstadoHabitacion: 1, idPiso: 1, idCategoria: 1, estado: true, urlsImagenes: [] };
  }
}
