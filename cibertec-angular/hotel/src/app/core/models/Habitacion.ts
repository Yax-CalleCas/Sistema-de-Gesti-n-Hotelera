export interface Habitacion {
  idHabitacion?: number;
  numero: string;
  detalle?: string;
  precio?: number;
  idEstadoHabitacion: number;
  idPiso: number;
  idCategoria: number;
  estado?: boolean;
  urlsImagenes?: string[];

  estadoHabitacion?: {
    idEstadoHabitacion: number;
    descripcion: string;
  };
  descripcionEstado?: string;
  estadoDescripcion?: string;
  categoriaNombre?: string;
}
