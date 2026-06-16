// reporte.models.ts

export interface ReporteProductoDto {
  idProducto: number;
  nombre: string;
  cantidad: number;
  precio: number; // En TypeScript usamos 'number' para BigDecimal
  estado: boolean;
}

export interface ReporteVentaDto {
  nombreProducto: string;
  cantidadTotalVendida: number; // Long en Java se mapea a number en TS
  totalIngresado: number;
}

export interface ReporteHabitacionDto {
  numeroHabitacion: string;
  categoria: string;
  vecesAlquilada: number;
}

export interface ReporteCobroDto {
  numeroHabitacion: string;
  nombreCliente: string;
  totalPagado: number;
  fechaPago: string; // En JSON, las fechas LocalDateTime llegan como string ISO
}
