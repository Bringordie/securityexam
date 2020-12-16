## SEM4 Security - Backend

Link til [Frontend](https://github.com/Bringordie/securityexam_frontend)

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

1) Tilføjelse af filer:
ved security_backend\src\main\resources skal der tilføjes 3 filer og tilføjes diverse ting.

mongo.properties

Denne skal indholde:
mongouri=**mongodb+srv://USERNAME:PASSWORD@CLUSTERNAME.601hh.mongodb.net/COLLECTIONNAME?retryWrites=true&w=majority**
mongoDB = **database name**
mongoLoginAttemptsCollection = **collection name**
mongoLoggerCollection = **collection name**

picture.properties

Denne skal indholde:
picturepath= **stien til der hvor billeder skal gemmes.**
picturepathdemo= **en sti lokalt hvis man selv skal kunne prøve at køre dette.**

salt.properties

Denne skal indholde:
salt = **noget som er meget sikkert og er kompleks og langt.**


2) for at lave users i database på serveren eller lokalt, lav først den db du har tænkt dig at bruge, gå så ind i config.properties og ændre følgende information så det passer med din database på dropletten. 

db.server=""


db.port=""


db.user=""


db.password=""


db.database=""


