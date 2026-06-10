export interface Recepcion {
  idRecepcion?: number;
  idCliente: number;
  idHabitacion: number;

  // Campos del cliente (necesarios para el SP si el cliente es nuevo)
  tipoDocumento?: string;
  documento?: string;
  nombre?: string;
  apellido?: string;
  correo?: string;

  // Campos de reserva
  fechaEntrada?: string;
  fechaSalida?: string;
  fechaSalidaConfirmacion?: string;
  precioInicial: number;
  adelanto: number;
  precioRestante: number;
  totalPagado?: number;
  costoPenalidad?: number;
  observacion?: string;
  estado?: boolean;
  categoriaNombre?: string;
}
