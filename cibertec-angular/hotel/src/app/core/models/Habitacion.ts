export interface Habitacion {
  // Identificadores principales
  idHabitacion?: number;
  numero: string;

  // Datos básicos
  detalle?: string;
  precio?: number;

  // IDs para relaciones (FK)
  idEstadoHabitacion: number;
  idPiso: number;
  idCategoria: number;

  // Estados y auditoría
  estado?: boolean;
  fechaCreacion?: string;

  // Propiedades descriptivas (Mapeadas desde el backend)
  categoriaNombre?: string; // Antes tenías categoriaDescripcion, unificado a 'Nombre'
  estadoDescripcion?: string;
  pisoNombre?: string;
}
