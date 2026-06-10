import { Component, OnInit, inject, ChangeDetectorRef } from '@angular/core';
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
  amountAdelantado?: number; // Por si acaso se requiere mapear
  cantidadAdelantado: number;
  cantidadRestante: number;
}

export interface ItemServicio {
  producto: string;
  cantidad: number;
  precioUnitario: number;
  estadoVenta: any; // Cambio estratégico a 'any' para evitar bloqueos por tipado estricto en las vistas de Angular
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
  private readonly cdr = inject(ChangeDetectorRef);
  private readonly recService = inject(RecepcionService);
  private readonly ventaService = inject(VentaService);

  idHabitacion!: number;
  isLoading = false;

  datosHospedaje: HospedajeResumen | null = null;
  servicios: ItemServicio[] = [];
  costoPenalidad: number = 0;
  totalConsumosPendientes: number = 0;
  totalNetoAPagar: number = 0;

  ngOnInit(): void {
    this.idHabitacion = Number(this.route.snapshot.paramMap.get('id'));
    if (this.idHabitacion) {
      this.cargarDatosHospedajeActivo();
    }
  }

cargarDatosHospedajeActivo(): void {
    this.isLoading = true;
    this.recService.buscarActivaPorHabitacion(this.idHabitacion).subscribe({
      next: (res) => {
        const data = res.data || res;
        this.datosHospedaje = {
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
        };

        // OPTIMIZACIÓN: Usamos el método específico pasando el ID de la recepción activa
        this.ventaService.buscarPorRecepcion(this.datosHospedaje.idRecepcion).subscribe({
          next: (ventasRes) => {
            // Evaluamos si la respuesta viene envuelta en un 'data' o directo como array
            const listaVentas = ventasRes?.data || ventasRes || [];
            const serviciosMapeados: ItemServicio[] = [];

            listaVentas.forEach((v: any) => {
              // Validamos ambas nomenclaturas (idRecepcion e idrecepcion) por seguridad
              const vIdRecepcion = v.idRecepcion ?? v.idrecepcion;

              if (vIdRecepcion === this.datosHospedaje?.idRecepcion) {
                // Si tu backend maneja una relación Lazy/Eager y trae la lista de productos:
                if (v.detalles && Array.isArray(v.detalles) && v.detalles.length > 0) {
                  v.detalles.forEach((d: any) => {
                    serviciosMapeados.push({
                      producto: d.nombreProducto || d.producto?.nombre || 'Producto',
                      cantidad: d.cantidad,
                      precioUnitario: d.precioUnitario || d.precio,
                      estadoVenta: v.estado, // Captura 'DEBE' o 'PAGADO' de la bd
                      subTotal: d.cantidad * (d.precioUnitario || d.precio)
                    });
                  });
                } else {
                  // CONTINGENCIA: Si el backend no trae la lista de productos anidada,
                  // mapeamos la fila maestra usando el 'total' económico para que figure en la cuenta.
                  serviciosMapeados.push({
                    producto: `Consumo Registrado (Venta N° ${v.idventa || v.idVenta})`,
                    cantidad: 1,
                    precioUnitario: v.total,
                    estadoVenta: v.estado, // Evaluará 'PAGADO' o 'DEBE'
                    subTotal: v.total
                  });
                }
              }
            });

            this.servicios = serviciosMapeados;
            this.calcularTotalFinal();
            this.isLoading = false;
            this.cdr.detectChanges();
          },
          error: (err) => {
            console.error("Error al recuperar ventas por recepción:", err);
            this.servicios = [];
            this.calcularTotalFinal();
            this.isLoading = false;
            this.cdr.detectChanges();
          }
        });
      },
      error: (err) => {
        this.isLoading = false;
        console.error(err);
        Swal.fire('Error', 'No se pudo cargar la información del hospedaje', 'error');
      }
    });
  }

  calcularTotalFinal(): void {
    // Filtrado robusto contra deudas pendientes de caja
    this.totalConsumosPendientes = this.servicios
      .filter(s => s.estadoVenta === 'DEBE' || s.estadoVenta === 'PENDIENTE')
      .reduce((sum, current) => sum + current.subTotal, 0);

    const restante = this.datosHospedaje?.cantidadRestante ?? 0;
    const penalidad = this.costoPenalidad >= 0 ? this.costoPenalidad : 0;

    this.totalNetoAPagar = restante + penalidad + this.totalConsumosPendientes;
  }

  finalizarCheckOut(): void {
    if (!this.datosHospedaje) return;

    Swal.fire({
      title: '¿Confirmar Check-Out?',
      text: `Total a cobrar al cliente: S/. ${this.totalNetoAPagar.toFixed(2)} (Incluye deudas de habitación y consumos de barra)`,
      icon: 'warning',
      showCancelButton: true,
      confirmButtonText: 'Sí, Facturar y Desocupar',
      cancelButtonColor: '#d33',
      confirmButtonColor: '#10b981'
    }).then((result) => {
      if (result.isConfirmed) {
        this.isLoading = true;

        this.recService.registrarSalida({
          idRecepcion: this.datosHospedaje!.idRecepcion,
          idHabitacion: this.idHabitacion,
          costoPenalidad: this.costoPenalidad,
          totalPagado: this.totalNetoAPagar
        }).subscribe({
          next: () => {
            Swal.fire('Éxito', 'Pago procesado y habitación desocupada con éxito', 'success')
              .then(() => this.router.navigate(['/admin/salidaHabitacion']));
          },
          error: (err) => {
            this.isLoading = false;
            console.error('Error Status:', err.status);
            Swal.fire('Error', err.error?.message || 'Error físico al registrar salida en el servidor.', 'error');
          }
        });
      }
    });
  }
}
