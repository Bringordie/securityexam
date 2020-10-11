## SEM4 Security - Backend


**Opsætningning af pipeline**

1) Clone dette projekt.

2) I netbeans gå ind i .pom filen og skift linjen med remote.server til at peje på dit domæne + /manager/text. 

3) På din droplet gå ind i opt/tomcat/bin/setenv.sh og skift CONNECTION_STR til at peje på dit nuværende projekt.

4) Husk at restarte tomcat efter ændringer, brug commanden "sudo service tomcat restart".

5) På Travis find dit repository, gå ind i settings og lav to environment variables REMOTE_USER og REMOTE_PW (du kan se hvilke værdier de skal have på din droplet i filen /opt/tomcat/conf/tomcat-users.xml).

6) På travis tryk på knappen der viser status på dit build, vælg formatet markdown, kopier det der står i result og paste det ind øverst i din readme fil på github.

**Deployment af frontend med Surge**

1) I en terminal i roden af projektet skriv "npm run build" for at generate en build folder.

2) Stadig i roden af projektet skriv "surge --project ./build --domain A_DOMAIN_NAME.surge.sh".

**Brug af frontend**

1) Husk at skifte url'er i "settings.js"

2) Sikrer dig at "settings.js" bruger PROD urls hvis du ikke allerede har downloaded backend og sat dette op.

**Brug af backend**

1) Rename projektet ved at højreklikke på projektnavnet i netbeans og vælg rename. Husk at vælge at den skal ændre ArtifactID også.

2) Lav dto'er til det du skal fetche fra andre api'er. Se ChuckDTO for et eksempel med hvor man modtager et simpelt JSON object. Se WeatherDTO samt WeatherDataObjectDTO for et eksempel på hvordan man kan håndtere det data man modtager hvis det er et mere komplekst JSON object.

3) Husk at tjekke at SharedSecret i security packagen står til at generate en random secret. Denne secret bliver brugt til hashing af signaturen (den sidste af de tre dele) af jwt'erne. Problemet ved at generate en random er at den generator en ny secret hvis serveren bliver nødt til at reboote, så alle jwt'er der er lavet før dette reboot, bliver så pludselig ubruglige.

4) for at lave users i database på serveren, lav først den db du har tænkt dig at bruge, gå så ind i config.properties og ændre følgende information så det passer med din database på dropletten. 

db.server=""


db.port=""


db.user=""


db.password=""


db.database=""



5) Gå så ind i SetupTestUsers i utils packagen og udfyld username og password for de users der skal oprettes, run SetupTestUsers, tjek at userne er blevet oprettet via workbench, og HUSK SÅ AT SÆTTE USERNAME OG PASSWORD TILBAGE TIL TOMME STRINGS FØR DU PUSHER!!



