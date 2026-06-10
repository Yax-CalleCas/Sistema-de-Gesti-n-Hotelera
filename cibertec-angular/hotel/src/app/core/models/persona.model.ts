export interface Persona {
  idPersona?: number;
  tipoDocumento: string;
  documento: string;
  nombre: string;
  apellido: string;
  correo: string;
  clave: string;
  idTipoPersona: number;
  estado?: boolean;
  fechaCreacion?: string;
}

