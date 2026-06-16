import { Component, OnInit, inject, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import Swal from 'sweetalert2';

import { RecepcionService } from '../../../core/services/recepcion.service';
import { VentaService } from '../../../core/services/venta.service';

interface HospedajeResumen {
  idRecepcion: number;
  numeroHabitacion: string;
  detalles: string;
  categoria: string;
  piso: string;
  clienteNombre: string;
  nroDocumento: string;
  correo: string;
  fechaEntrada: string;
  costoHabitacion: number;
  cantidadAdelantado: number;
  cantidadRestante: number;
}

export interface ItemServicio {
  producto: string;
  cantidad: number;
  precioUnitario: number;
  estadoVenta: any;
  subTotal: number;
}

@Component({
  selector: 'app-procesarsalida',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './procesarsalida.html'
})
export class Procesarsalida implements OnInit {
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly recService = inject(RecepcionService);
  private readonly ventaService = inject(VentaService);

  // Signals para el estado
  isLoading = signal(false);
  idHabitacion = signal<number>(0);
  datosHospedaje = signal<HospedajeResumen | null>(null);
  servicios = signal<ItemServicio[]>([]);
  costoPenalidad = signal<number>(0);

  // Cómputo reactivo: Se recalcula automáticamente cada vez que servicios() o costoPenalidad() cambian
  totalConsumosPendientes = computed(() =>
    this.servicios()
      .filter(s => s.estadoVenta === 'DEBE' || s.estadoVenta === 'PENDIENTE')
      .reduce((sum, s) => sum + s.subTotal, 0)
  );

  totalNetoAPagar = computed(() => {
    const hospedaje = this.datosHospedaje();
    return (hospedaje?.cantidadRestante ?? 0) +
           Math.max(this.costoPenalidad(), 0) +
           this.totalConsumosPendientes();
  });

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    if (id) {
      this.idHabitacion.set(id);
      this.cargarDatos(id);
    }
  }

  cargarDatos(idHabitacion: number): void {
    this.isLoading.set(true);
    this.recService.buscarActivaPorHabitacion(idHabitacion).subscribe({
      next: (res) => {
        const data = res.data || res;
        this.datosHospedaje.set({
          idRecepcion: data.idRecepcion,
          numeroHabitacion: data.numero,
          detalles: data.detalleHabitacion || data.detalle,
          categoria: data.categoriaNombre,
          piso: data.pisoNombre,
          clienteNombre: `${data.nombre} ${data.apellido}`,
          nroDocumento: data.documento || data.Dni,
          correo: data.correo,
          fechaEntrada: data.fechaEntrada,
          costoHabitacion: data.precioInicial,
          cantidadAdelantado: data.adelanto,
          cantidadRestante: data.precioRestante
        });

        this.cargarVentas(data.idRecepcion);
      },
      error: (err) => {
        this.isLoading.set(false);
        Swal.fire('Error', 'No se pudo cargar la información del hospedaje', 'error');
      }
    });
  }

  private cargarVentas(idRecepcion: number): void {
    this.ventaService.buscarPorRecepcion(idRecepcion).subscribe({
      next: (ventasRes) => {
        const listaVentas = ventasRes?.data || ventasRes || [];
        const serviciosMapeados: ItemServicio[] = [];

        listaVentas.forEach((v: any) => {
          if ((v.idRecepcion ?? v.idrecepcion) === idRecepcion) {
            if (v.detalles?.length > 0) {
              v.detalles.forEach((d: any) => {
                serviciosMapeados.push({
                  producto: d.nombreProducto || 'Producto',
                  cantidad: d.cantidad,
                  precioUnitario: d.precioUnitario || d.precio,
                  estadoVenta: v.estado,
                  subTotal: d.cantidad * (d.precioUnitario || d.precio)
                });
              });
            } else {
              serviciosMapeados.push({
                producto: `Consumo (Venta N° ${v.idventa || v.idVenta})`,
                cantidad: 1,
                precioUnitario: v.total,
                estadoVenta: v.estado,
                subTotal: v.total
              });
            }
          }
        });
        this.servicios.set(serviciosMapeados);
        this.isLoading.set(false);
      },
      error: () => this.isLoading.set(false)
    });
  }

  finalizarCheckOut(): void {
    const data = this.datosHospedaje();
    if (!data) return;

    Swal.fire({
      title: '¿Confirmar Check-Out?',
      text: `Total a pagar: S/. ${this.totalNetoAPagar().toFixed(2)}`,
      icon: 'warning',
      showCancelButton: true,
      confirmButtonText: 'Sí, Facturar y Desocupar',
      confirmButtonColor: '#10b981'
    }).then((result) => {
      if (result.isConfirmed) {
        this.isLoading.set(true);
        this.recService.registrarSalida({
          idRecepcion: data.idRecepcion,
          idHabitacion: this.idHabitacion(),
          costoPenalidad: this.costoPenalidad(),
          totalPagado: this.totalNetoAPagar()
        }).subscribe({
          next: () => {
            Swal.fire('Éxito', 'Habitación desocupada con éxito', 'success')
              .then(() => this.router.navigate(['/admin/salidaHabitacion']));
          },
          error: (err) => {
            this.isLoading.set(false);
            Swal.fire('Error', err.error?.message || 'Error al procesar salida.', 'error');
          }
        });
      }
    });
  }
}
