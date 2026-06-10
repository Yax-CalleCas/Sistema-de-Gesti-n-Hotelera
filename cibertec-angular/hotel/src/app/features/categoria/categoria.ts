import { Component, OnInit, inject, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import Swal from 'sweetalert2';
import { CategoriaService } from '../../core/services/categoria.service';
import { Categoria } from '../../core/models/categoria.model';

@Component({
  selector: 'app-categoria',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './categoria.html',
})
export class CategoriaComponent implements OnInit {
  private readonly service = inject(CategoriaService);
  private readonly cdr = inject(ChangeDetectorRef);

  categorias: Categoria[] = [];
  categoria: Categoria = this.nuevoModelo();
  isProcessing = false;

  ngOnInit(): void {
    this.cargar();
  }

  cargar(): void {
    this.service.listar().subscribe({
      next: (res) => {
        this.categorias = res.data || [];
        this.cdr.detectChanges();
      }
    });
  }

  guardar(): void {
    this.isProcessing = true;
    const operacion$ = this.categoria.idCategoria
      ? this.service.actualizar(this.categoria.idCategoria, this.categoria)
      : this.service.registrar(this.categoria);

    operacion$.subscribe({
      next: () => {
        Swal.fire('Éxito', 'Categoría guardada correctamente', 'success');
        this.cargar();
        this.cerrarModal();
      },
      error: (err) => Swal.fire('Error', err.error?.message || 'Error al guardar', 'error'),
      complete: () => (this.isProcessing = false)
    });
  }

  eliminar(id: number): void {
    Swal.fire({
      title: '¿Estás seguro?',
      text: "La categoría será desactivada",
      icon: 'warning',
      showCancelButton: true,
      confirmButtonText: 'Sí, eliminar'
    }).then((result) => {
      if (result.isConfirmed) {
        this.service.eliminar(id).subscribe(() => {
          this.cargar();
          Swal.fire('Eliminado', 'Categoría dada de baja', 'success');
        });
      }
    });
  }

  nuevoModelo(): Categoria {
    return { idCategoria: 0, descripcion: '', estado: true };
  }

  abrir(c?: Categoria): void {
    this.categoria = c ? { ...c } : this.nuevoModelo();
    // Lógica para abrir tu modal (Bootstrap)
    const modalEl = document.getElementById('modalCat');
    if (modalEl) (window as any).bootstrap.Modal.getOrCreateInstance(modalEl).show();
  }

  cerrarModal(): void {
    const modalEl = document.getElementById('modalCat');
    (window as any).bootstrap.Modal.getInstance(modalEl)?.hide();
  }
}
