import { Component, OnInit, inject, ChangeDetectorRef, PLATFORM_ID } from '@angular/core';
import { CommonModule, isPlatformBrowser } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import Swal from 'sweetalert2';

// Servicios y Modelos
import { HabitacionService } from '../../../core/services/habitacion.service';
import { RecepcionService } from '../../../core/services/recepcion.service';
import { PersonaService } from '../../../core/services/persona.service';
import { Habitacion } from '../../../core/models/Habitacion';
import { Persona } from '../../../core/models/persona.model';
import { VentaService } from '../../../core/services/venta.service';

// Interfaz para el manejo de errores detallados
interface ErrorVentas {
  mensaje: string;
  status?: number;
  detalle?: string;
}

@Component({
  selector: 'app-recepciondetalle',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './recepciondetalle.html'
})
export class RecepcionDetalleComponent implements OnInit {
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly habService = inject(HabitacionService);
  private readonly recService = inject(RecepcionService);
  private readonly personaService = inject(PersonaService);
  private readonly cdr = inject(ChangeDetectorRef);
  private readonly platformId = inject(PLATFORM_ID);
  private readonly ventaService = inject(VentaService);

  habitacion: Habitacion | null = null;
  recepcion: any = {};
  servicios: any[] = [];

  // Lista unificada para clientes (idTipoPersona === 3)
  listaPersonas: Persona[] = [];

  esOcupada = false;
  isLoading = false;
  dniBusqueda = '';
  precioBasePorDia = 0;
  listaVentasCliente: any[] = [];
  isLoadingVentas: boolean = false;

  // Variable agregada para capturar el error exacto y mostrarlo en el HTML
  errorVentas: ErrorVentas | null = null;

  private readonly CATEGORIAS_MAP: Record<number, string> = { 1: 'Simple', 2: 'Doble', 3: 'Matrimonial', 4: 'Suite' };
  private readonly PISOS_MAP: Record<number, string> = { 1: 'PRIMERO', 2: 'SEGUNDO', 3: 'TERCERO', 4: 'CUARTO' };

  ngOnInit(): void {
    // 1. Cargar clientes al iniciar para asegurar disponibilidad
    this.cargarClientes();

    this.route.paramMap.subscribe(params => {
      const id = Number(params.get('id'));
      if (id) this.cargarDatos(id);
    });
  }

  // Método centralizado para cargar clientes tipo 3
  private cargarClientes(): void {
    this.personaService.listar().subscribe({
      next: (res) => {
        if (res?.data) {
          this.listaPersonas = res.data.filter(p => Number(p.idTipoPersona) === 3);
          this.cdr.markForCheck();
        }
      },
      error: () => console.error('Error cargando clientes')
    });
  }

  cargarDatos(idHabitacion: number): void {
    this.isLoading = true;
    this.recepcion = this.inicializarModelo(idHabitacion);

    this.habService.buscarPorId(idHabitacion).subscribe({
      next: (res) => {
        this.habitacion = res.data;
        if (this.habitacion) this.procesarEstadoHabitacion(this.habitacion);
        this.isLoading = false;
        this.cdr.markForCheck();
      }
    });
  }

  private procesarEstadoHabitacion(hab: Habitacion): void {
    this.precioBasePorDia = Number(hab.precio || 0);
    this.esOcupada = Number(hab.idEstadoHabitacion) === 2;

    if (this.esOcupada) {
      this.obtenerHospedajeActivo(Number(hab.idHabitacion));
    } else {
      this.recepcion.precioInicial = this.precioBasePorDia;
      this.recepcion.precioRestante = this.precioBasePorDia;
    }
  }

  private obtenerHospedajeActivo(idHabitacion: number): void {
    this.recService.buscarActivaPorHabitacion(idHabitacion).subscribe({
      next: (res) => {
        if (res?.data) {
          this.recepcion = { ...res.data };
          this.servicios = res.data.listaConsumos || res.data.servicios || [];

          // Sincronización: Al obtener la recepción activa, cargamos sus ventas de inmediato
          if (this.recepcion.idRecepcion) {
            this.cargarVentasConsumo(this.recepcion.idRecepcion);
          }
        }
        this.cdr.markForCheck();
      }
    });
  }

  cargarVentasConsumo(idRecepcion: number): void {
    this.isLoadingVentas = true;
    this.errorVentas = null;

    this.ventaService.buscarPorRecepcion(idRecepcion).subscribe({
      next: (res) => {
        const listaVentas = res?.data || res || [];
        const serviciosMapeados: any[] = [];

        listaVentas.forEach((v: any) => {
          // Normalización para tolerar mayúsculas o minúsculas de la BD (idrecepcion vs idRecepcion)
          const vIdRecepcion = v.idRecepcion ?? v.idrecepcion;

          if (vIdRecepcion === idRecepcion) {
            if (v.detalles && Array.isArray(v.detalles) && v.detalles.length > 0) {
              v.detalles.forEach((d: any) => {
                serviciosMapeados.push({
                  producto: d.nombreProducto || d.producto?.nombre || 'Producto',
                  cantidad: d.cantidad,
                  precioUnitario: d.precioUnitario || d.precio,
                  estadoVenta: v.estado,
                  subTotal: d.cantidad * (d.precioUnitario || d.precio)
                });
              });
            } else {
              // MAPEADO SEGURO: Si no hay array de detalles, renderizamos la fila en base a su total monetario
              serviciosMapeados.push({
                producto: `Consumo Registrado (Venta N° ${v.idventa || v.idVenta})`,
                cantidad: 1,
                precioUnitario: v.total,
                estadoVenta: v.estado,
                subTotal: v.total
              });
            }
          }
        });

        this.listaVentasCliente = serviciosMapeados;
        this.isLoadingVentas = false;
        this.cdr.markForCheck();
      },
      error: (err) => {
        this.isLoadingVentas = false;
        console.error('Error completo capturado:', err);

        let mensajeError = 'No se pudieron recuperar los consumos de la recepción activa.';
        let detalleBackend = 'Error desconocido o sin respuesta del servidor de Spring Boot.';

        if (err.error) {
          if (typeof err.error === 'object' && err.error.message) {
            detalleBackend = err.error.message;
          } else if (typeof err.error === 'string') {
            detalleBackend = err.error;
          }
        } else if (err.message) {
          detalleBackend = err.message;
        }

        this.errorVentas = {
          mensaje: mensajeError,
          status: err.status,
          detalle: detalleBackend
        };

        this.cdr.markForCheck();
      }
    });
  }

  // Usa la lista ya cargada, no llama al API nuevamente
  abrirModalClientes(): void {
    if (this.esOcupada || this.listaPersonas.length === 0) return;

    if (isPlatformBrowser(this.platformId)) {
      const modalElement = document.getElementById('modalClientes');
      if (modalElement) {
        const modal = new (window as any).bootstrap.Modal(modalElement);
        modal.show();
      }
    }
  }

  // Búsqueda en la lista ya cargada
  buscarCliente(): void {
    if (!this.dniBusqueda || this.esOcupada) return;

    const cliente = this.listaPersonas.find(p => p.documento === this.dniBusqueda);
    if (cliente) {
      this.seleccionarPersona(cliente);
    } else {
      Swal.fire('Atención', 'Cliente no encontrado o no corresponde a tipo Cliente', 'warning');
    }
  }

  seleccionarPersona(cliente: Persona): void {
    this.recepcion.idCliente = cliente.idPersona;
    this.recepcion.nombre = cliente.nombre;
    this.recepcion.apellido = cliente.apellido;
    this.recepcion.documento = cliente.documento;
    this.recepcion.correo = cliente.correo;
    this.recepcion.tipoDocumento = cliente.tipoDocumento;

    this.cdr.markForCheck();
  }

  guardar(): void {
    if (this.esOcupada) return;

    console.log('Datos enviados al registro:', JSON.stringify(this.recepcion, null, 2));

    if (!this.recepcion.documento || !this.recepcion.nombre) {
      Swal.fire('Error', 'Faltan datos obligatorios del cliente.', 'error');
      return;
    }

    this.recService.registrar(this.recepcion).subscribe({
      next: (res) => {
        Swal.fire('Éxito', 'Registrado correctamente', 'success');
        this.router.navigate(['/admin/recepcion']);
      },
      error: (err) => {
        console.error('Error detallado del backend:', err);
        Swal.fire('Error', err.error?.message || 'No se pudo procesar el registro.', 'error');
      }
    });
  }

  calcularPrecio(): void {
    if (this.esOcupada || !this.recepcion.fechaEntrada || !this.recepcion.fechaSalida) return;
    const diff = new Date(this.recepcion.fechaSalida).getTime() - new Date(this.recepcion.fechaEntrada).getTime();
    const dias = Math.max(1, Math.round(diff / (24 * 60 * 60 * 1000)));
    this.recepcion.precioInicial = this.precioBasePorDia * dias;
    this.recepcion.precioRestante = this.recepcion.precioInicial - (this.recepcion.adelanto || 0);
  }

  private inicializarModelo(idHab: number): any {
    return {
      idHabitacion: idHab,
      precioInicial: 0,
      adelanto: 0,
      precioRestante: 0,
      fechaEntrada: new Date().toISOString().split('T')[0]
    };
  }

  get nombreCategoria(): string { return this.CATEGORIAS_MAP[Number(this.habitacion?.idCategoria)] || 'Simple'; }
  get nombrePiso(): string { return this.PISOS_MAP[Number(this.habitacion?.idPiso)] || 'PRIMERO'; }
}
