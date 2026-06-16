import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import Swal from 'sweetalert2';
import { PersonaService } from '../../../../core/services/persona.service';
import { Persona } from '../../../../core/models/persona.model';

@Component({
  selector: 'app-cliente-list',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './cliente-list.html'
})
export class ClienteListComponent implements OnInit {
  private readonly service = inject(PersonaService);

  personas: Persona[] = [];
  personaSeleccionada = this.crearPersonaVacia();
  terminoBusqueda = '';
  itemsPerPage = 10;
  currentPage = 1;
  guardando = false;

  ngOnInit(): void { this.cargarPersonas(); }

  cargarPersonas(): void {
    this.service.listar().subscribe({
      next: (res) => this.personas = res?.data.filter(p => p.idTipoPersona === 3) || [],
      error: () => Swal.fire('Error', 'No se pudieron cargar los clientes', 'error')
    });
  }

  guardar(): void {
    if (this.guardando) return;
    this.guardando = true;
    const esEdicion = !!this.personaSeleccionada.idPersona;

    const obs = esEdicion
      ? this.service.actualizar(this.personaSeleccionada.idPersona!, this.personaSeleccionada)
      : this.service.crear(this.personaSeleccionada);

    obs.subscribe({
      next: () => {
        Swal.fire('Éxito', 'Guardado correctamente', 'success');
        this.cerrarModal();
        this.cargarPersonas();
      },
      error: () => Swal.fire('Error', 'Problema al guardar', 'error'),
      complete: () => this.guardando = false
    });
  }

  eliminar(id: number | undefined): void {
    if (!id) return;
    Swal.fire({ title: '¿Seguro?', icon: 'warning', showCancelButton: true, confirmButtonText: 'Sí, eliminar' })
      .then(res => { if (res.isConfirmed) this.service.eliminar(id).subscribe(() => this.cargarPersonas()); });
  }

  editarCliente(p: Persona): void {
    this.personaSeleccionada = { ...p };
    this.toggleModal('show');
  }

  nuevoCliente(): void {
    this.personaSeleccionada = this.crearPersonaVacia();
    this.toggleModal('show');
  }

  private toggleModal(action: 'show' | 'hide'): void {
    const el = document.getElementById('modalPersona');
    const modal = (window as any).bootstrap?.Modal.getInstance(el) || new (window as any).bootstrap.Modal(el);
    modal[action]();
  }

  private cerrarModal = () => this.toggleModal('hide');
  private crearPersonaVacia(): Persona { return { tipoDocumento: '', documento: '', nombre: '', apellido: '', correo: '', clave: '', idTipoPersona: 3, estado: true }; }

  // Métodos necesarios para el HTML
  trackByPersona = (_: number, p: Persona) => p.idPersona;

get filteredPersonas() {
    const busqueda = this.terminoBusqueda?.toLowerCase().trim() || '';

    return this.personas.filter(p => {
      // Usamos el operador || '' para asegurar que si el valor es null/undefined,
      // se convierta en una cadena vacía y no rompa la ejecución.
      const nombre = (p.nombre || '').toLowerCase();
      const apellido = (p.apellido || '').toLowerCase();
      const documento = (p.documento || '').toLowerCase();

      return nombre.includes(busqueda) ||
             apellido.includes(busqueda) ||
             documento.includes(busqueda);
    });
  }

  get paginatedPersonas() {
    const start = (this.currentPage - 1) * this.itemsPerPage;
    return this.filteredPersonas.slice(start, start + this.itemsPerPage);
  }

  get totalPages() { return Math.ceil(this.filteredPersonas.length / this.itemsPerPage); }
  changePage(p: number) { if (p >= 1 && p <= this.totalPages) this.currentPage = p; }
}
