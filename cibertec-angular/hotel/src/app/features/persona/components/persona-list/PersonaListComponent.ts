import { Component, OnInit, inject, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import Swal from 'sweetalert2';
import { PersonaService } from '../../../../core/services/persona.service';
import { Persona } from '../../../../core/models/persona.model';

const ROLES: Record<number, { nombre: string, badge: string }> = {
  1: { nombre: 'Administrador', badge: 'bg-danger' },
  2: { nombre: 'Empleado', badge: 'bg-primary' },
  3: { nombre: 'Cliente', badge: 'bg-info text-dark' }
};

@Component({
  selector: 'app-persona-list',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './persona-list.html',
})
export class PersonaListComponent implements OnInit {
  private readonly service = inject(PersonaService);

  personas = signal<Persona[]>([]);
  personaSeleccionada = signal<Persona>(this.crearPersonaVacia());
  isProcessing = signal(false);
  currentPage = signal(1);
  itemsPerPage = 10;

  roles = Object.entries(ROLES).map(([id, val]) => ({ id: Number(id), ...val }));

  // Computados reactivos
  paginatedPersonas = computed(() => {
    const start = (this.currentPage() - 1) * this.itemsPerPage;
    return this.personas().slice(start, start + this.itemsPerPage);
  });

  ngOnInit(): void {
    this.cargarPersonas();
  }

  cargarPersonas(): void {
    this.service.listar().subscribe({
      next: (res) => this.personas.set(res?.data || []),
      error: () => Swal.fire('Error', 'No se pudieron cargar los datos', 'error')
    });
  }

  guardar(): void {
    if (this.isProcessing()) return;
    this.isProcessing.set(true);

    const data = this.personaSeleccionada();
    const esEdicion = !!data.idPersona;

    // Payload limpio
    const payload: any = { ...data, idTipoPersona: Number(data.idTipoPersona) };
    if (esEdicion && !payload.clave?.trim()) delete payload.clave;

    const operacion = esEdicion
      ? this.service.actualizar(data.idPersona!, payload)
      : this.service.crear(payload);

    operacion.subscribe({
      next: () => {
        Swal.fire('Éxito', `Persona ${esEdicion ? 'actualizada' : 'registrada'} correctamente`, 'success');
        this.limpiarFormulario();
        this.cargarPersonas();
      },
      error: (err) => Swal.fire('Error', err?.error?.message || 'Error al guardar', 'error'),
      complete: () => this.isProcessing.set(false)
    });
  }

  eliminar(id: number | undefined): void {
    if (!id) return;
    Swal.fire({
      title: '¿Estás seguro?',
      icon: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#d33',
      confirmButtonText: 'Sí, eliminar'
    }).then((res) => {
      if (res.isConfirmed) {
        this.service.eliminar(id).subscribe(() => {
          this.cargarPersonas();
          Swal.fire('Eliminado', 'Registro eliminado.', 'success');
        });
      }
    });
  }

  cambiarEstado(p: Persona): void {
    const updated = { ...p, estado: !p.estado, idTipoPersona: Number(p.idTipoPersona) };
    this.service.actualizar(p.idPersona!, updated).subscribe({
      next: () => this.cargarPersonas(),
      error: () => Swal.fire('Error', 'No se pudo cambiar el estado', 'error')
    });
  }

  editar(persona: Persona): void {
    this.personaSeleccionada.set({ ...persona, clave: '' });
    window.scrollTo({ top: 0, behavior: 'smooth' });
  }

  limpiarFormulario(): void {
    this.personaSeleccionada.set(this.crearPersonaVacia());
  }

  getRolInfo(id: number) {
    return ROLES[id] || { nombre: 'Desconocido', badge: 'bg-secondary' };
  }

  // Añade este método dentro de la clase PersonaListComponent
  trackByPersona(index: number, p: Persona): number | undefined {
    return p.idPersona;
  }

  private crearPersonaVacia(): Persona {
    return { tipoDocumento: '', documento: '', nombre: '', apellido: '', correo: '', clave: '', idTipoPersona: 0, estado: true };
  }
}
