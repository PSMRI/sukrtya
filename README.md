# Sukrtya on DIGIT
 spring boot demo with postgress or SQL Server database.
 In this application perform CRUD operations.

** Pre requisite **
  1. Installed Postgress or SQL Server
  2. Create a database name "spring_boot_demo_db"
  3. Installed Java and set environment path 
  4. Installed Maven and set environment path
  5. Installed Postman for check APIs


For check java installed or not
    `java -version`
   
For check maven installed or not
   ` mvn -v`

For compile spring boot application
    `mvn clean package`

For run spring boot application
`    mvn spring-boot:run `

Swagger URL
`    http://localhost:9090/swagger-ui/index.html `

POST Request 
`    http://localhost:9090/api/states`
```json
{
    "stateNameEn": "Assam",
    "stateID": "S03",
    "isActive": true,
    "stateNameHn": "असम"
}
```
Get Request
 `   http://localhost:9090/api/states`
	
Output
```json
[{
    "stateNameEn": "Assam",
    "stateID": "S03",
    "isActive": true,
    "stateNameHn": "असम"
}]
```

