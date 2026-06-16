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

  // Propiedades descriptivas
  categoriaNombre?: string;
  estadoDescripcion?: string;
  pisoNombre?: string;

  // --- CAMPO NUEVO ---
  // Representa la lista de URLs que vienen del backend
  urlsImagenes?: string[];
}
