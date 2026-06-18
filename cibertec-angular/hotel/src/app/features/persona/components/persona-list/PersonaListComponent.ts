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
  styleUrls:['./persona-list.css']
})
export class PersonaListComponent implements OnInit {
  private readonly service = inject(PersonaService);

  personas = signal<Persona[]>([]);
  personaSeleccionada = signal<Persona>(this.crearPersonaVacia());
  isProcessing = signal(false);
  currentPage = signal(1);
  itemsPerPage = 10;

  roles = Object.entries(ROLES).map(([id, val]) => ({ id: Number(id), ...val }));

  // Señal computada para obtener la porción de personas por página
  paginatedPersonas = computed(() => {
    const start = (this.currentPage() - 1) * this.itemsPerPage;
    return this.personas().slice(start, start + this.itemsPerPage);
  });

  // Señal computada para calcular el total de páginas
  totalPaginas = computed(() => {
    return Math.ceil(this.personas().length / this.itemsPerPage);
  });

  ngOnInit(): void { this.cargarPersonas(); }

  // Cambiar de página de forma segura validando límites
  cambiarPagina(pagina: number): void {
    if (pagina >= 1 && pagina <= this.totalPaginas()) {
      this.currentPage.set(pagina);
    }
  }

  cargarPersonas(): void {
    this.service.listar().subscribe({
      next: (res) => {
        this.personas.set(res?.data || []);

        // Corrección de visualización: si al eliminar nos quedamos en una página sin registros, retrocedemos
        if (this.currentPage() > this.totalPaginas() && this.totalPaginas() > 0) {
          this.currentPage.set(this.totalPaginas());
        }
      },
      error: () => Swal.fire('Error', 'No se pudieron cargar los datos', 'error')
    });
  }

  guardar(): void {
    if (this.isProcessing()) return;
    this.isProcessing.set(true);

    const data = this.personaSeleccionada();
    const esEdicion = !!data.idPersona;
    const payload: Persona = {
      ...data,
      idTipoPersona: Number(data.idTipoPersona),
      clave: data.clave || (esEdicion ? '**********' : ''),
      fotoUrl: data.fotoUrl || ''
    };

    const operacion = esEdicion
      ? this.service.actualizar(data.idPersona!, payload)
      : this.service.crear(payload);

    operacion.subscribe({
      next: (res) => {
        Swal.fire('Éxito', res.message || 'Operación realizada', 'success');
        this.limpiarFormulario();
        this.cargarPersonas();
      },
      error: (err) => Swal.fire('Error', err?.error?.message || 'Error de validación', 'error'),
      complete: () => this.isProcessing.set(false)
    });
  }

  onImgError(event: any): void {
    event.target.src = 'https://goo.su/V78kA';
  }

  cambiarEstado(p: Persona): void {
    const nuevoEstado = !p.estado;
    const payload: Persona = {
        ...p,
        estado: nuevoEstado,
        idTipoPersona: Number(p.idTipoPersona),
        clave: p.clave && p.clave.trim() !== '' ? p.clave : '**********',
    };

    this.service.actualizar(p.idPersona!, payload).subscribe({
      next: () => this.cargarPersonas(),
      error: (err) => {
        console.error("Detalle del error 400:", err.error);
        Swal.fire('Error', 'No se pudo cambiar el estado', 'error');
      }
    });
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
      estado: true,
      fotoUrl: ''
    };
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

  editar(persona: Persona): void {
    this.personaSeleccionada.set({ ...persona, clave: '' });
    window.scrollTo({ top: 0, behavior: 'smooth' });
  }

  limpiarFormulario(): void {
    this.personaSeleccionada.set(this.crearPersonaVacia());
  }

  getRolInfo(id: number) { return ROLES[id] || { nombre: 'Desconocido', badge: 'bg-secondary' }; }

  trackByPersona = (_: number, p: Persona) => p.idPersona;
}
