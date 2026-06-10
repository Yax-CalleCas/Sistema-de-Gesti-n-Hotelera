import { Component, OnInit, inject, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import Swal from 'sweetalert2';
import { RecepcionService } from '../../../core/services/recepcion.service';
import { ProductoService } from '../../../core/services/producto.service';
import { VentaService } from '../../../core/services/venta.service';

@Component({
  selector: 'app-ventaproductos',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './ventaproductos.html'
})
export class Ventaproductos implements OnInit {
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private recepcionService = inject(RecepcionService);
  private productoService = inject(ProductoService);
  private ventaService = inject(VentaService);
  private cdr = inject(ChangeDetectorRef);

  datosRecepcion: any = null;
  listaProductos: any[] = [];
  carrito: any[] = [];
  errorMessage: string | undefined | null;
  productoSeleccionadoId: number = 0;
  cantidad: number = 1;
  isProcessing: boolean = false;

  // VARIABLE AGREGADA: Controla si la venta se cancela de inmediato o se va a la cuenta
  estadoVenta: string = 'PAGADO';

  ngOnInit(): void {
    const idHab = this.route.snapshot.paramMap.get('id');
    if (idHab) this.cargarTodo(Number(idHab));
  }

  cargarTodo(idHabitacion: number) {
    this.recepcionService.buscarActivaPorHabitacion(idHabitacion).subscribe(res => {
      this.datosRecepcion = res?.data || res;
      this.cdr.detectChanges();
    });

    this.productoService.listar().subscribe(res => {
      this.listaProductos = res?.data || [];
      this.cdr.detectChanges();
    });
  }

  agregarAlCarrito() {
    const prod = this.listaProductos.find(p => p.idProducto == this.productoSeleccionadoId);
    if (!prod) return;

    const itemExistente = this.carrito.find(item => item.idProducto === prod.idProducto);

    if (itemExistente) {
      itemExistente.cantidad += this.cantidad;
      itemExistente.subTotal = itemExistente.cantidad * itemExistente.precio;
    } else {
      this.carrito.push({
        idProducto: prod.idProducto,
        nombre: prod.nombre,
        precio: prod.precio,
        cantidad: this.cantidad,
        subTotal: prod.precio * this.cantidad
      });
    }

    // Limpiar selección después de agregar para evitar duplicaciones accidentales
    this.productoSeleccionadoId = 0;
    this.cantidad = 1;
    this.cdr.detectChanges();
  }

  getTotal(): number {
    return this.carrito.reduce((acc, item) => acc + item.subTotal, 0);
  }

  registrarVenta() {
    if (!this.carrito || this.carrito.length === 0) {
      this.errorMessage = "El carrito está vacío.";
      Swal.fire('Advertencia', this.errorMessage, 'warning');
      return;
    }

    if (!this.datosRecepcion || !this.datosRecepcion.idRecepcion) {
      this.errorMessage = "No se encontraron datos válidos de la recepción activa.";
      Swal.fire('Error', this.errorMessage, 'error');
      return;
    }

    // Activamos el estado de carga
    this.isProcessing = true;

    // MODIFICADO: Ahora el campo 'estado' recibe dinámicamente el valor del select de tu HTML (PAGADO o DEBE)
    const ventaData = {
      idRecepcion: this.datosRecepcion.idRecepcion,
      estado: this.estadoVenta,
      detalles: this.carrito.map(item => ({
        idProducto: item.idProducto,
        cantidad: item.cantidad,
        precioUnitario: item.precio
      }))
    };

    this.ventaService.guardar(ventaData).subscribe({
      next: () => {
        this.isProcessing = false; // Desbloqueamos el botón
        Swal.fire('Éxito', 'Venta registrada correctamente', 'success');
        this.router.navigate(['/admin/recepcion']);
      },
      error: (err) => {
        this.isProcessing = false; // Desbloqueamos el botón ante error
        console.error('Error:', err);
        this.errorMessage = err.error?.message || 'Error al procesar la venta en el servidor.';

        Swal.fire({
          icon: 'error',
          title: 'Error al registrar',
          text: this.errorMessage!,
          footer: 'Verifique stock disponible en el inventario'
        });
        this.cdr.detectChanges();
      }
    });
  }
  obtenerPrecioSeleccionado(): number {
  if (!this.productoSeleccionadoId || this.productoSeleccionadoId == 0) {
    return 0;
  }
  const prod = this.listaProductos.find(p => p.idProducto == this.productoSeleccionadoId);
  return prod ? prod.precio : 0;
}
  get precioProductoSeleccionado(): number {
  const prod = this.listaProductos.find(p => p.idProducto == this.productoSeleccionadoId);
  return prod ? prod.precio : 0;
}
}
