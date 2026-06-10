export interface ItemServicio {
  producto: string;
  cantidad: number;
  precioUnitario: number;
  estadoVenta: string; // <-- Cambia '"Pendiente" | "Pagado"' por 'string'
  subTotal: number;
}
