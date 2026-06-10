import { Component, OnInit, inject, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { forkJoin } from 'rxjs';
import Swal from 'sweetalert2';

import { HabitacionService } from '../../core/services/habitacion.service';
import { EstadoHabitacionService } from '../../core/services/EstadoHabitacionService';
import { CategoriaService } from '../../core/services/categoria.service';
import { PisoService } from '../../core/services/piso.service';
import { Habitacion } from '../../core/models/Habitacion';
import { EstadoHabitacion } from '../../core/models/estado-habitacion.model';
import { Categoria } from '../../core/models/categoria.model';
import { Piso } from '../../core/models/piso';

interface HabitacionExtendida extends Habitacion {
  categoriaDesc?: string;
  pisoDesc?: string;
  estadoHabitacionDesc?: string;
}

@Component({
  selector: 'app-habitacion-list',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './habitacion-list.html'
})
export class HabitacionListComponent implements OnInit {
  private readonly service = inject(HabitacionService);
  private readonly estadoService = inject(EstadoHabitacionService);
  private readonly categoriaService = inject(CategoriaService);
  private readonly pisoService = inject(PisoService);
  private readonly cdr = inject(ChangeDetectorRef);

  habitaciones: HabitacionExtendida[] = [];
  estados: EstadoHabitacion[] = [];
  categorias: Categoria[] = [];
  pisos: Piso[] = [];

  // Se inicializa con valores por defecto crudos para mitigar errores de lectura en las directivas de enlace del HTML (ngModel)
  habitacion: Habitacion = {
    numero: '',
    detalle: '',
    precio: 0,
    idEstadoHabitacion: 1,
    idPiso: 1,
    idCategoria: 1,
    estado: true
  };

  isProcessing = false;
  filtroTexto: string = '';
  isLoading = false;

  ngOnInit(): void {
    this.cargarDatos();
  }

  cargarDatos(): void {
    this.isLoading = true;

    forkJoin({
      habitaciones: this.service.listar(),
      estados: this.estadoService.listar(),
      categorias: this.categoriaService.listar(),
      pisos: this.pisoService.listar()
    }).subscribe({
      next: (res) => {
        this.estados = res.estados.data || [];
        this.categorias = (res.categorias.data || []).filter(c => c.estado);
        this.pisos = (res.pisos.data || []).filter(p => p.estado);

        const listaHab = res.habitaciones.data || [];
        this.habitaciones = listaHab.map((h: Habitacion) => ({
          ...h,
          categoriaDesc: this.categorias.find(c => c.idCategoria === h.idCategoria)?.descripcion || 'N/A',
          pisoDesc: this.pisos.find(p => p.idPiso === h.idPiso)?.descripcion || 'N/A',
          estadoHabitacionDesc: this.estados.find(e => e.idEstadoHabitacion === h.idEstadoHabitacion)?.descripcion || 'Desconocido'
        }));

        this.habitacion = this.nuevoModelo();
        this.isLoading = false;

        // Forzar detección manual inmediata para desacoplar el estado asíncrono y destruir el spinner en el DOM
        this.cdr.detectChanges();
      },
      error: () => {
        this.isLoading = false;
        Swal.fire('Error', 'No se pudieron cargar los datos', 'error');
      }
    });
  }

  nuevoModelo(): Habitacion {
    const primerEstado = this.estados?.[0]?.idEstadoHabitacion ?? 1;
    const primerPiso = this.pisos?.[0]?.idPiso ?? 1;
    const primeraCat = this.categorias?.[0]?.idCategoria ?? 1;

    return {
      numero: '',
      detalle: '',
      precio: 0,
      idEstadoHabitacion: primerEstado,
      idPiso: primerPiso,
      idCategoria: primeraCat,
      estado: true
    };
  }

  abrir(h?: Habitacion): void {
    this.habitacion = h ? { ...h } : this.nuevoModelo();
    const modalEl = document.getElementById('modalHab');
    if (modalEl) {
      (window as any).bootstrap.Modal.getOrCreateInstance(modalEl).show();
    }
  }

  guardar(): void {
    if (this.isProcessing) return;
    this.isProcessing = true;

    this.service.guardar(this.habitacion).subscribe({
      next: () => {
        Swal.fire('Éxito', 'Habitación guardada correctamente', 'success');
        this.cargarDatos();
        this.cerrarModal();
      },
      error: () => {
        this.isProcessing = false;
        Swal.fire('Error', 'No se pudo guardar la habitación', 'error');
      },
      complete: () => this.isProcessing = false
    });
  }

  cerrarModal(): void {
    const modalEl = document.getElementById('modalHab');
    if (modalEl) {
      const instance = (window as any).bootstrap.Modal.getInstance(modalEl);
      if (instance) instance.hide();
    }
  }

  get habitacionesFiltradas(): HabitacionExtendida[] {
    if (!this.habitaciones) return [];
    const filtro = this.filtroTexto.toLowerCase().trim();
    return this.habitaciones.filter(h =>
      h.numero.toLowerCase().includes(filtro) ||
      h.categoriaDesc?.toLowerCase().includes(filtro)
    );
  }

  eliminar(id?: number): void {
    if (!id) return;

    Swal.fire({
      title: '¿Estás seguro?',
      text: "La habitación cambiará de estado a inactiva",
      icon: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#d33',
      confirmButtonText: 'Sí, dar de baja'
    }).then((result) => {
      if (result.isConfirmed) {
        this.service.eliminar(id).subscribe({
          next: () => {
            this.cargarDatos();
            Swal.fire('Baja Exitosa', 'Habitación desactivada.', 'success');
          },
          error: () => Swal.fire('Error', 'No se pudo procesar la baja', 'error')
        });
      }
    });
  }

  getEstadoBadgeClass(idEstado: number): string {
    const clases: Record<number, string> = {
      1: 'bg-success',
      2: 'bg-danger',
      3: 'bg-warning text-dark',
    };
    return clases[idEstado] || 'bg-secondary';
  }
}
