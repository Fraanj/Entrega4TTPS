# Dockerfile para Angular dev
FROM node:20.19.0

# Carpeta de trabajo dentro del contenedor
WORKDIR /app

# Copiamos package.json y package-lock.json (si existe)
COPY package*.json ./

# Instalamos dependencias
RUN npm install

# Copiamos todo el proyecto
COPY . .

# Exponemos el puerto de Angular
EXPOSE 4200

# Comando para levantar Angular en modo desarrollo
CMD ["npm", "start", "--", "--host", "0.0.0.0", "--port", "4200"]

