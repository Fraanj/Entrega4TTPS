#!/bin/bash
echo "Principio Run"
sudo mvn clean package -DskipTests 
sudo mv ./target/Proyecto.war /opt/tomcat/webapps
echo "archivo Movido"
sudo /opt/tomcat/bin/startup.sh 
echo "tomcat startup"
cd ../Front/DondeEstas/
ng serve
echo "Fin run"
