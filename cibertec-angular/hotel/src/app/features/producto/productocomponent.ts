import { Component, OnInit, inject, ChangeDetectorRef } from '@angular/core'; // <-- Importamos ChangeDetectorRef
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { ProductoService } from '../../core/services/producto.service';
import { Producto } from '../../core/models/producto';
import { finalize } from 'rxjs/operators'; // <-- Importamos finalize para asegurar el flujo
import Swal from 'sweetalert2';

@Component({
  selector: 'app-producto',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './producto.html',
  styleUrls:['./productos.css']
})
export class ProductoComponent implements OnInit {
  private fb = inject(FormBuilder);
  private prodService = inject(ProductoService);
  private router = inject(Router);
  private cdr = inject(ChangeDetectorRef); // <-- Inyectamos el detector de cambios de Angular

  productos: Producto[] = [];
  form: FormGroup;
  isEdit = false;
  isLoading = false;

  // Variables para la paginación simple de 10 registros
  paginaActual = 1;
  readonly elementosPorPagina = 10;

  constructor() {
    this.form = this.fb.group({
      idProducto: [null],
      nombre: ['', Validators.required],
      detalle: [''],
      precio: [0, [Validators.required, Validators.min(0)]],
      cantidad: [0, [Validators.required, Validators.min(0)]],
      estado: [true],
      imagenUrl: ['']
    });
  }

  ngOnInit(): void {
    this.cargar();
  }

  // Getter para obtener la porción de datos de la página actual
  get productosPaginados(): Producto[] {
    const inicio = (this.paginaActual - 1) * this.elementosPorPagina;
    const fin = inicio + this.elementosPorPagina;
    return this.productos.slice(inicio, fin);
  }

  // Getter para calcular el total de páginas
  get totalPaginas(): number {
    return Math.ceil(this.productos.length / this.elementosPorPagina);
  }

  // Cambiar de página validando límites básicos
  cambiarPagina(pagina: number): void {
    if (pagina >= 1 && pagina <= this.totalPaginas) {
      this.paginaActual = pagina;
    }
  }

  cargar(): void {
    // Forzamos el estado de carga desde el inicio exacto del ciclo de vida
    this.isLoading = true;
    this.cdr.detectChanges(); // <-- Obliga al HTML a renderizar el Spinner inmediatamente

    this.prodService.listar()
      .pipe(
        finalize(() => {
          this.isLoading = false;
          this.cdr.detectChanges(); // <-- Obliga al HTML a ocultar el Spinner y liberar la tabla
        })
      )
      .subscribe({
        next: (res) => {
          this.productos = res.data ?? [];
          // Si por una eliminación la página actual se queda sin registros, retrocedemos una página
          if (this.paginaActual > this.totalPaginas && this.totalPaginas > 0) {
            this.paginaActual = this.totalPaginas;
          }
        },
        error: (err) => {
          this.manejarError(err);
        }
      });
  }

  guardar(): void {
    if (this.form.invalid) return;

    const producto = this.form.value;
    const obs = this.isEdit
      ? this.prodService.actualizar(producto.idProducto, producto)
      : this.prodService.registrar(producto);

    obs.subscribe({
      next: () => {
        Swal.fire('Éxito', 'Guardado correctamente', 'success');
        this.cancelar();
        this.cargar(); // Ejecuta la recarga de datos automática
      },
      error: (err) => this.manejarError(err)
    });
  }

  editar(p: Producto): void {
    this.isEdit = true;
    this.form.patchValue(p);
  }

  eliminar(id: number | undefined): void {
    if (id === undefined || id === null) {
      Swal.fire('Error', 'No se puede eliminar un registro sin ID válido.', 'error');
      return;
    }

    Swal.fire({
      title: '¿Eliminar?',
      text: "Esta acción no se puede revertir",
      icon: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#d33',
      cancelButtonColor: '#3085d6',
      confirmButtonText: 'Sí, eliminar',
      cancelButtonText: 'Cancelar'
    }).then((r) => {
      if (r.isConfirmed) {
        this.prodService.eliminar(id).subscribe({
          next: () => {
            Swal.fire('Eliminado', 'El producto ha sido removido.', 'success');
            this.cargar(); // Ejecuta la recarga de datos automática
          },
          error: (err) => this.manejarError(err)
        });
      }
    });
  }

  cancelar(): void {
    this.isEdit = false;
    this.form.reset({ precio: 0, cantidad: 0, estado: true ,imagenUrl: ''});
  }

  private manejarError(err: any): void {
    console.error('Error en ProductoComponent:', err);
    const mensajeAmigable = err?.error?.message || 'Ocurrió un error inesperado.';

    if (err.status === 403 || err.status === 401) {
      Swal.fire('Sesión Caducada', 'No tienes permisos o tu sesión expiró.', 'error')
        .then(() => this.router.navigate(['/login']));
    } else {
      Swal.fire('Error', mensajeAmigable, 'error');
    }
  }
}
