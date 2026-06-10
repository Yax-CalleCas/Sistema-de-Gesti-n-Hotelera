
export interface LoginRequest {
  correo: string;
  clave: string;
}

export interface LoginResponse {
  idPersona: number;
  nombre: string;
  apellido: string;
  correo: string;
  tipoPersona: string;
  token: string;
}


