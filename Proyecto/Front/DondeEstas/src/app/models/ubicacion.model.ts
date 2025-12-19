export interface Ubicacion {
    latitud: number;
    longitud: number;
    barrio: string; // Se obtiene de la API de GeoRef
  }
  
export interface Ciudad {
  id: string;
  nombre: string;
  provincia: {
    id: string;
    nombre: string;
  };
}

export interface GeorefResponse {
  localidades: Ciudad[];
  cantidad: number;
  total: number;
  inicio: number;
  parametros: any;
}