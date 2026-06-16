import { Component, OnInit, inject, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import Swal from 'sweetalert2';

import { HabitacionService } from '../../../core/services/habitacion.service';
import { RecepcionService } from '../../../core/services/recepcion.service';
import { PersonaService } from '../../../core/services/persona.service';
import { VentaService } from '../../../core/services/venta.service';
import { Habitacion } from '../../../core/models/Habitacion';
import { Persona } from '../../../core/models/persona.model';

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
  private readonly ventaService = inject(VentaService);

  // Signals para el estado
  habitacion = signal<Habitacion | null>(null);
  recepcion = signal<any>(this.inicializarModelo(0));
  listaPersonas = signal<Persona[]>([]);
  listaVentasCliente = signal<any[]>([]);
  dniBusqueda = signal('');

  isLoading = signal(false);
  isLoadingVentas = signal(false);
  errorVentas = signal<any | null>(null);

  // Computados para rendimiento (se actualizan solos)
  esOcupada = computed(() => Number(this.habitacion()?.idEstadoHabitacion) === 2);
  nombreCategoria = computed(() => ({ 1: 'Simple', 2: 'Doble', 3: 'Matrimonial', 4: 'Suite' }[Number(this.habitacion()?.idCategoria)] ?? 'Simple'));
  nombrePiso = computed(() => ({ 1: 'PRIMERO', 2: 'SEGUNDO', 3: 'TERCERO', 4: 'CUARTO' }[Number(this.habitacion()?.idPiso)] ?? 'PRIMERO'));

  ngOnInit(): void {
    this.cargarClientes();
    this.route.paramMap.subscribe(params => {
      const id = Number(params.get('id'));
      if (id) this.cargarDatos(id);
    });
  }



  cargarDatos(idHabitacion: number): void {
    this.isLoading.set(true);
    this.recepcion.set(this.inicializarModelo(idHabitacion));

    this.habService.buscarPorId(idHabitacion).subscribe(res => {
      this.habitacion.set(res.data);
      if (res.data && this.esOcupada()) this.obtenerHospedajeActivo(idHabitacion);
      this.isLoading.set(false);
    });
  }

  private obtenerHospedajeActivo(idHabitacion: number): void {
    this.recService.buscarActivaPorHabitacion(idHabitacion).subscribe(res => {
      if (res?.data) {
        this.recepcion.set({ ...res.data });
        if (res.data.idRecepcion) this.cargarVentasConsumo(res.data.idRecepcion);
      }
    });
  }

  cargarVentasConsumo(idRecepcion: number): void {
    this.isLoadingVentas.set(true);
    this.ventaService.buscarPorRecepcion(idRecepcion).subscribe({
      next: (res) => {
        const listaVentas = res?.data ?? [];
        const servicios = listaVentas.flatMap((v: any) =>
          (v.detalles ?? []).map((d: any) => ({
            producto: d.nombreProducto ?? 'Producto',
            cantidad: d.cantidad,
            subTotal: d.cantidad * (d.precioUnitario ?? d.precio)
          }))
        );
        this.listaVentasCliente.set(servicios);
        this.isLoadingVentas.set(false);
      },
      error: () => { this.isLoadingVentas.set(false); this.errorVentas.set('Error cargando consumos'); }
    });
  }

  abrirModalClientes(): void {
    const modalElement = document.getElementById('modalClientes');
    if (modalElement) new (window as any).bootstrap.Modal(modalElement).show();
  }

  buscarCliente(): void {
    const cliente = this.listaPersonas().find(p => p.documento === this.dniBusqueda());
    if (cliente) {
      this.seleccionarPersona(cliente);
      const modal = (window as any).bootstrap.Modal.getInstance(document.getElementById('modalClientes'));
      modal?.hide();
    } else {
      Swal.fire('Atención', 'Cliente no encontrado', 'warning');
    }
  }

seleccionarPersona(c: Persona): void {
  this.recepcion.update(r => ({
    ...r,
    idCliente: c.idPersona,
    nombre: c.nombre,
    apellido: c.apellido,
    documento: c.documento,
    correo: c.correo,
    tipoDocumento: c.tipoDocumento // Asegúrate de incluir esto
  }));
}
  guardar(): void {
    if (this.esOcupada()) return;
    this.recService.registrar(this.recepcion()).subscribe({
      next: () => {
        Swal.fire('Éxito', 'Registrado correctamente', 'success');
        this.router.navigate(['/admin/recepcion']);
      },
      error: (err) => Swal.fire('Error', err.error?.message ?? 'Error al registrar', 'error')
    });
  }

  // En tu .ts, verifica que al cargar los clientes, los guardes así:
private cargarClientes(): void {
  this.personaService.listar().subscribe(res => {
    if (res?.data) {
      // ESTO ES LO QUE LLENA EL MODAL
      this.listaPersonas.set(res.data.filter(p => Number(p.idTipoPersona) === 3));
    }
  });
}

  private inicializarModelo(idHab: number) {
    return { idHabitacion: idHab, precioInicial: 0, fechaEntrada: new Date().toISOString().split('T')[0] };
  }
}
