export interface Recepcion {
  idRecepcion?: number;
  idCliente?: number;
  idHabitacion?: number;
  numero?: string;
  categoriaNombre?: string;
  pisoNombre?: string;
  detalleHabitacion?: string;
  precioHabitacion?: number;
  estadoHabitacion?: string;
  tipoDocumento?: string;
  documento?: string;
  nombre?: string;
  apellido?: string;
  correo?: string;
  precioInicial?: number;
  adelanto?: number;
  precioRestante?: number;
  totalPagado?: number;
  costoPenalidad?: number;
  fechaEntrada?: string;
  fechaSalida?: string;
  fechaSalidaConfirmacion?: string;
  observacion?: string | null;
  estado?: boolean;
}
