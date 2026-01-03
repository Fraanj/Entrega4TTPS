# -----------------------------
# 🚀 LEVANTAR SERVICIOS
# -----------------------------

# Levantar todos los servicios (DB + Backend + Frontend)
docker compose up -d

# Levantar solo un servicio
docker compose up -d frontend
docker compose up -d backend
docker compose up -d db

# -----------------------------
# 🔍 VER ESTADO DE CONTENEDORES
# -----------------------------
docker compose ps

# -----------------------------
# ⏹ DETENER SERVICIOS
# -----------------------------

# Detener todos los servicios
docker compose stop

# Detener solo un servicio
docker compose stop frontend
docker compose stop backend
docker compose stop db

# -----------------------------
# 🗑 ELIMINAR CONTENEDORES
# -----------------------------

# Eliminar todos los contenedores parados
docker compose rm -f

# Eliminar solo un contenedor
docker compose rm -f frontend

# -----------------------------
# 🛠 RECONSTRUIR SERVICIOS
# -----------------------------

# Reconstruir frontend y levantarlo
docker compose build frontend
docker compose up -d frontend

# Reconstruir backend y levantarlo
docker compose build backend
docker compose up -d backend

# -----------------------------
# 💾 ACCEDER A LA BASE DE DATOS MySQL
# -----------------------------

# Directo al cliente MySQL
docker exec -it mysql-db mysql -u root -p

# Abrir bash dentro del contenedor y luego MySQL
docker exec -it mysql-db bash
mysql -u root -p

# -----------------------------
# 🧹 LIMPIEZA Y RESET COMPLETO
# -----------------------------

# Detener y eliminar todos los contenedores, redes y volúmenes
docker compose down -v --remove-orphans

# Limpiar imágenes no usadas
docker image prune -a

# -----------------------------
# ✅ RESUMEN RÁPIDO
# -----------------------------
# Levantar todos:           docker compose up -d
# Levantar frontend solo:    docker compose up -d frontend
# Parar todos:               docker compose stop
# Parar frontend:            docker compose stop frontend
# Eliminar frontend:         docker compose rm -f frontend
# Reconstruir frontend:      docker compose build frontend && docker compose up -d frontend
# Entrar MySQL CLI:          docker exec -it mysql-db mysql -u root -p
# Levantar todo limpio:      docker compose down -v --remove-orphans && docker compose up -d --build

