# 🚀 Sukrtya Application

Sukrtya is a modern Java web application built with Spring Boot, designed to run on **Java 17 or higher**, packaged via Maven, and using a PostgreSQL database. After successful deployment, explore the API via Swagger UI.

---

## 🛠 Prerequisites

- **Java 17 or higher** installed  
  _Check with:_ `java -version`
- **Maven** installed  
  _Check with:_ `mvn -v`
- **PostgreSQL** database instance running

---

## ⚙️ Required Environment Variables

Before building and running, set the following environment variables:

| Variable                | Purpose                                       |
|-------------------------|-----------------------------------------------|
| SPRING_APPLICATION_NAME | Spring application name                       |
| SERVER_PORT             | Port on which server will run                 |
| DB_IP                   | Database server IP address                    |
| DB_PORT                 | Database server port                          |
| DB_NAME                 | Database name                                 |
| DB_USERNAME             | Database username                             |
| DB_PASSWORD             | Database password                             |
| FRUNTEND_URL            | Frontend application URL                      |
| BACKED_URL              | Backend application URL                       |

**Example (Linux/macOS):**

export SPRING_APPLICATION_NAME="Sukrtya"
export SERVER_PORT=8081
export DB_IP="localhost"
export DB_PORT="5432"
export DB_NAME="sukrtya_vhsnd_v2"
export DB_USERNAME="postgres"
export DB_PASSWORD="postgres"
export FRUNTEND_URL="http://localhost:3000"
export BACKED_URL="http://localhost:8081"


**Example (Windows):**

set SPRING_APPLICATION_NAME=Sukrtya
set SERVER_PORT=8081
set DB_IP=localhost
set DB_PORT=5432
set DB_NAME=sukrtya_vhsnd_v2
set DB_USERNAME=postgres
set DB_PASSWORD=postgres
set FRUNTEND_URL=http://localhost:3000
set BACKED_URL=http://localhost:8081
---

## 🧩 Build and Run

1. **Clean and Package the Application**
    ```bash
   mvn clean package -DisipTest
      ```
2. **Run the Application**
   ```bash 
    java -jar target/sukrtya.jar
   ```


### Alternatively: Pass Parameters Directly
You can also pass configuration as command-line parameters:
```bash
   SPRING_APPLICATION_NAME="Sukrtya" SERVER_PORT=8081 DB_IP="localhost" DB_PORT="5432" DB_NAME="sukrtya_vhsnd_v2" DB_USERNAME="postgres" DB_PASSWORD="postgres" FRUNTEND_URL="http://localhost:3000" BACKED_URL="http://localhost:8081" java -jar sukrtya.jar
```
*(Update `target/sukrtya.jar` if your JAR file has a different name.)*

---

## 🗄️ PostgreSQL Setup

Make sure your PostgreSQL database is running and matches the above environment variable configuration.

---

## 📖 API Documentation

Once the application is running, explore the API docs at:
[BACKED_URL]/swagger-ui/index.html

- Replace `[SERVER_PORT]` with your configured port (default: 8081).

---

## 🌐 Frontend/Backend URLs

Set `FRUNTEND_URL` and `BACKED_URL` environment variables as appropriate for your deployment.

---

## 💡 Tips

- Check application logs for successful startup and database connection.
- See [Swagger UI]([BACKED_URL]/swagger-ui/index.html) for all endpoints and schemas.

---

## 🆘 Help

If you face issues, verify all environment variables and database credentials.

