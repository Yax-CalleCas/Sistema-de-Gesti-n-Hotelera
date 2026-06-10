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
  personaSeleccionada: Persona = this.crearPersonaVacia();
  terminoBusqueda: string = '';
  itemsPerPage: number = 10;
  currentPage: number = 1;
  guardando: boolean = false;

  ngOnInit(): void {
    this.cargarPersonas();
  }


private crearPersonaVacia(): Persona {
  return {
    tipoDocumento: '',
    documento: '',
    nombre: '',
    apellido: '',
    correo: '',
    clave: '',
    idTipoPersona: 3,
    estado: true
  };
}

  cargarPersonas(): void {
    this.service.listar().subscribe({
      next: (res) => {
        if (res?.data) {
          this.personas = res.data.filter(p => p.idTipoPersona === 3);
        }
      },
      error: () => {
        Swal.fire('Error', 'No se pudieron cargar los datos de los clientes', 'error');
      }
    });
  }

  guardar(): void {
    if (this.guardando) return;
    this.guardando = true;

    const esEdicion = !!this.personaSeleccionada.idPersona;
    const operacion = esEdicion
      ? this.service.actualizar(this.personaSeleccionada.idPersona!, this.personaSeleccionada)
      : this.service.crear(this.personaSeleccionada);

    operacion.subscribe({
      next: () => {
        Swal.fire('Éxito', `Cliente ${esEdicion ? 'actualizado' : 'registrado'} correctamente`, 'success');
        this.cerrarModal();
        this.cargarPersonas();
        this.guardando = false;
      },
      error: () => {
        Swal.fire('Error', 'Ocurrió un problema al guardar los datos', 'error');
        this.guardando = false;
      }
    });
  }

  eliminar(id: number | undefined): void {
    if (!id) return;
    Swal.fire({
      title: '¿Estás seguro?',
      text: "¡Esta acción no se puede revertir!",
      icon: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#d33',
      confirmButtonText: 'Sí, eliminar'
    }).then((result) => {
      if (result.isConfirmed) {
        this.service.eliminar(id).subscribe(() => {
          this.cargarPersonas();
          Swal.fire('Eliminado', 'Cliente eliminado con éxito', 'success');
        });
      }
    });
  }

  editarCliente(persona: Persona): void {
    this.personaSeleccionada = { ...persona };
    this.abrirModal();
  }

  nuevoCliente(): void {
    this.personaSeleccionada = this.crearPersonaVacia();
    this.abrirModal();
  }

  private abrirModal(): void {
    const modalElement = document.getElementById('modalPersona');
    if (modalElement) {
      const modal = new (window as any).bootstrap.Modal(modalElement);
      modal.show();
    }
  }

  private cerrarModal(): void {
    const modalElement = document.getElementById('modalPersona');
    if (modalElement) {
      const modal = (window as any).bootstrap.Modal.getInstance(modalElement);
      modal?.hide();
    }
  }

  get filteredPersonas(): Persona[] {
    const busqueda = this.terminoBusqueda.toLowerCase().trim();
    if (!busqueda) return this.personas;
    return this.personas.filter(p =>
      p.nombre.toLowerCase().includes(busqueda) ||
      p.apellido.toLowerCase().includes(busqueda) ||
      p.documento.includes(busqueda)
    );
  }

  get paginatedPersonas(): Persona[] {
    const start = (this.currentPage - 1) * this.itemsPerPage;
    return this.filteredPersonas.slice(start, start + this.itemsPerPage);
  }

  get totalPages(): number {
    return Math.ceil(this.filteredPersonas.length / this.itemsPerPage);
  }

  changePage(page: number): void {
    if (page >= 1 && page <= this.totalPages) {
      this.currentPage = page;
    }
  }
}
