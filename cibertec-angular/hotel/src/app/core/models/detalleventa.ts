export interface DetalleVenta {
  idDetalleVenta?: number;
  idVenta?: number;
  idProducto: number;
  cantidad: number;
  precioUnitario: number; // Coincide con (elem->>'precioUnitario') del SP de PostgreSQL
}
