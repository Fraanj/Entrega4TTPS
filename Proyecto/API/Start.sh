#!/bin/bash
echo "Principio Run"
sudo mvn clean package -DskipTests 
sudo mv ./target/Proyecto.war /opt/tomcat/webapps
sudo /opt/tomcat/bin/startup.sh 
cd ../Front/DondeEstas/
ng serve
echo "Fin run"
