export enum Rol {
  ESTUDIANTE = 'ESTUDIANTE',
  RESPONSABLE = 'RESPONSABLE',
  ADMINISTRADOR = 'ADMINISTRADOR',
}

export interface Usuario {
  id: number;
  nombre: string;
  correo: string;
  rol: Rol;
  activo: boolean;
}

export interface LoginRequest {
  correo: string;
  password: string;
}

export interface LoginResponse {
  token: string;
  tipo: string;
  rol: Rol;
  nombre: string;
  id?: number;
}

export interface RegistroRequest {
  nombre: string;
  correo: string;
  password: string;
  rol: Rol;
}

export interface UserSession {
  id: number;
  nombre: string;
  rol: Rol;
  token: string;
}
