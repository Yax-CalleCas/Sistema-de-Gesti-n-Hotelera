import { DetalleVenta } from './detalleventa';

export interface Venta {
  idVenta?: number;
  idRecepcion: number;
  total?: number;
  estado: string;
  detalles: DetalleVenta[];
}

