export interface Producto {
  idProducto?: number;
  nombre: string;
  detalle?: string;
  precio?: number;
  cantidad?: number;
  estado?: boolean;
  fechaCreacion?: string;
  // Agregamos el campo de imagen
  imagenUrl?: string;
}
