export interface Usuario {
  id: number;
  nombre: string;
  apellido: string;
  email: string;
  telefono: string;
  ciudad: string;
  puntos: number;
  rolNombre: string;
  estadoNombre: string;
}

export interface UsuarioLogged {
  accessToken: string;
  usuario: Usuario;
  expiresIn: number;
}