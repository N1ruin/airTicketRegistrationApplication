@echo off
set TOMCAT_HOME=G:\tomcat
set WAR_SOURCE=C:\airTicketRegistrationApp\airTicketRegistrationApplication\target\ticket-app.war

call "%TOMCAT_HOME%\bin\shutdown.bat"
rd /q "%TOMCAT_HOME%\webapps\ticket-app"
copy /Y "%WAR_SOURCE%" "%TOMCAT_HOME%\webapps\"

call "%TOMCAT_HOME%\bin\startup.bat"