# ---------- STAGE 1: build del WAR ----------
FROM maven:3.9-amazoncorretto-17 AS builder

WORKDIR /build

# copiar pom y dependencias primero para cachear
COPY ./pom.xml .
RUN mvn -B dependency:go-offline

# copiar código fuente
COPY src ./src

# compilar sin tests
RUN mvn clean package -DskipTests


# ---------- STAGE 2: Tomcat con WAR ----------
FROM tomcat:10.1-jre17

# limpiar apps por defecto
RUN rm -rf /usr/local/tomcat/webapps/*

# copiar WAR generado
COPY --from=builder /build/target/Proyecto.war /usr/local/tomcat/webapps/Proyecto.war

EXPOSE 8080

CMD ["catalina.sh", "run"]

