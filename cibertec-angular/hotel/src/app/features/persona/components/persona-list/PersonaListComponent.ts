// src/app/features/persona/components/persona-list/persona-list.component.ts
import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import Swal from 'sweetalert2';
import { PersonaService } from '../../../../core/services/persona.service';
import { Persona } from '../../../../core/models/persona.model';

@Component({
  selector: 'app-persona-list',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './persona-list.html',
})
export class PersonaListComponent implements OnInit {
  private readonly service = inject(PersonaService);

  personas: Persona[] = [];
  personaSeleccionada: Persona = this.crearPersonaVacia();
  isProcessing = false;
  currentPage = 1;
  itemsPerPage = 10;

  ngOnInit(): void {
    this.cargarPersonas();
  }

  cargarPersonas(): void {
    this.service.listar().subscribe({
      next: (res) => {
        // Mapeo defensivo para asegurar que idTipoPersona siempre sea un número reconocible por el HTML
        this.personas = (res?.data || []).map((p: any) => ({
          ...p,
          idTipoPersona: p.idTipoPersona ? Number(p.idTipoPersona) : 0
        }));
      },
      error: (err) => {
        console.error('Error al recuperar el listado:', err);
        Swal.fire('Error', 'No se pudieron cargar los datos desde el servidor', 'error');
      }
    });
  }

  guardar(): void {
    if (this.isProcessing) return;

    // Forzamos que el ID del tipo de persona sea numérico puro antes de enviarlo a Spring Boot
    this.personaSeleccionada.idTipoPersona = Number(this.personaSeleccionada.idTipoPersona);

    if (this.personaSeleccionada.idTipoPersona === 0) {
      Swal.fire('Atención', 'Por favor, seleccione un rol válido.', 'warning');
      return;
    }

    this.isProcessing = true;
    const esEdicion = !!this.personaSeleccionada.idPersona;

    // Si es edición y el usuario dejó la contraseña vacía, enviamos una cadena vacía o nula
    // para que el backend reconozca que debe preservar la contraseña de la BD.
    const datosEnviar = { ...this.personaSeleccionada };
    if (esEdicion && (!datosEnviar.clave || datosEnviar.clave.trim() === '')) {
      datosEnviar.clave = '';
    }

    const operacion = esEdicion
      ? this.service.actualizar(datosEnviar.idPersona!, datosEnviar)
      : this.service.crear(datosEnviar);

    operacion.subscribe({
      next: () => {
        Swal.fire('Éxito', `Persona ${esEdicion ? 'actualizada' : 'registrada'} correctamente`, 'success');
        this.limpiarFormulario();
        this.cargarPersonas();
      },
      error: (err) => {
        this.isProcessing = false;
        const errorMsg = err?.error?.message || 'Ocurrió un problema al guardar';
        Swal.fire('Error', errorMsg, 'error');
      },
      complete: () => (this.isProcessing = false)
    });
  }

  eliminar(id: number | undefined): void {
    if (!id) return;

    Swal.fire({
      title: '¿Estás seguro?',
      text: "¡Esta acción desactivará o eliminará el registro de la persona!",
      icon: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#d33',
      confirmButtonText: 'Sí, eliminar',
      cancelButtonText: 'Cancelar'
    }).then((result) => {
      if (result.isConfirmed) {
        this.service.eliminar(id).subscribe({
          next: () => {
            this.cargarPersonas();
            Swal.fire('Eliminado', 'El registro ha sido eliminado.', 'success');
          },
          error: (err) => {
            const errorMsg = err?.error?.message || 'No se pudo eliminar el registro';
            Swal.fire('Error', errorMsg, 'error');
          }
        });
      }
    });
  }

  cambiarEstado(p: Persona): void {
    const nuevoEstado = !p.estado;

    // Clonamos el objeto de manera segura para la petición asíncrona de actualización de estado
    const copiaPersona = { ...p, estado: nuevoEstado };
    copiaPersona.idTipoPersona = Number(copiaPersona.idTipoPersona);

    this.service.actualizar(p.idPersona!, copiaPersona).subscribe({
      next: () => {
        p.estado = nuevoEstado;
      },
      error: () => Swal.fire('Error', 'No se pudo cambiar el estado de la persona', 'error')
    });
  }

  editar(persona: Persona): void {
    // Clonamos el objeto para evitar mutaciones directas en la fila de la tabla mientras escribes
    this.personaSeleccionada = {
      ...persona,
      idTipoPersona: Number(persona.idTipoPersona),
      clave: '' // Limpiamos el campo visualmente para que se entienda que es opcional modificarla
    };
    window.scrollTo({ top: 0, behavior: 'smooth' });
  }

  limpiarFormulario(): void {
    this.personaSeleccionada = this.crearPersonaVacia();
  }

  private crearPersonaVacia(): Persona {
    return {
      tipoDocumento: '',
      documento: '',
      nombre: '',
      apellido: '',
      correo: '',
      clave: '',
      idTipoPersona: 0,
      estado: true
    };
  }

  // ===================================================================
  // LÓGICA DE PAGINACIÓN Y BADGES
  // ===================================================================
  get totalPages(): number {
    return Math.ceil(this.personas.length / this.itemsPerPage);
  }

  get paginatedPersonas(): Persona[] {
    const start = (this.currentPage - 1) * this.itemsPerPage;
    return this.personas.slice(start, start + this.itemsPerPage);
  }

  changePage(page: number): void {
    if (page >= 1 && page <= this.totalPages) {
      this.currentPage = page;
    }
  }

  getRolBadgeClass(id: number): string {
    const roles: Record<number, string> = { 1: 'bg-danger', 2: 'bg-primary', 3: 'bg-info text-dark' };
    return roles[id] || 'bg-secondary';
  }

  getRolNombre(id: number): string {
    const roles: Record<number, string> = { 1: 'Administrador', 2: 'Empleado', 3: 'Cliente' };
    return roles[id] || 'Desconocido';
  }
}
