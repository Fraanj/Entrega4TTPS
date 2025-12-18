# Detener Tomcat
sudo systemctl stop tomcat

# Limpiar TODO el webapps
sudo rm -rf /opt/tomcat/webapps/Proyecto*
sudo rm -rf /opt/tomcat/work/Catalina/localhost/Proyecto*
sudo rm -rf /opt/tomcat/logs/*

# Reconstruir el WAR
cd ~/IdeaProjects/Proyecto/API
mvn clean package -DskipTests

# Verificar que se creó
ls -lh target/Proyecto.war

# Copiar al webapps
sudo cp target/Proyecto.war /opt/tomcat/webapps/

# Dar permisos
sudo chown tomcat:tomcat /opt/tomcat/webapps/Proyecto.war

# Iniciar Tomcat
sudo systemctl start tomcat

# Monitorear el despliegue
sudo tail -f /opt/tomcat/logs/catalina.out > log.txt
