 PetMatch Backend

Backend oficial de PetMatch, una plataforma enfocada en conectar mascotas y personas mediante perfiles, chat en tiempo real e imágenes en la nube.

* Tecnologías utilizadas
* Java 17
* Spring Boot
* MySQL
*  WebSocket + STOMP
* Cloudinary
* Maven
* Docker
* Railway
* Swagger OpenAPI
* Lombok
* JPA / Hibernate
* Backend en producción
API Base URL
https://petmatch1-production.up.railway.app


   Documentación Swagger
https://petmatch1-production.up.railway.app/swagger-ui/index.html
   Funcionalidades principales
👤 Usuarios
Crear usuarios
Consultar usuarios
Actualizar usuarios
Eliminar usuarios
🐶 Mascotas
Registro de mascotas
Asociación con usuarios
CRUD completo
💬 Chat en tiempo real

Implementado con WebSocket + STOMP.

Endpoint WebSocket
wss://petmatch1-production.up.railway.app/chat
Suscripción
/topic/messages
Envío de mensajes
/app/send
Formato de mensaje
{
  "sender": "camilo",
  "receiver": "juan",
  "content": "Hola!"
}
🖼️ Imágenes con Cloudinary

Las imágenes de mascotas se almacenan en la nube mediante Cloudinary.

Upload endpoint
POST /api/v1/imagen/upload
Form-data
Key	Type
file	File
mascotaId	Text
⚙️ Configuración local
1 Clonar repositorio
git clone https://github.com/Camilotr13/petmatch1.git
2️ Entrar al proyecto
cd petmatch1
3️ Configurar variables de entorno

Crear variables:

SPRING_DATASOURCE_URL=
SPRING_DATASOURCE_USERNAME=
SPRING_DATASOURCE_PASSWORD=

CLOUDINARY_CLOUD_NAME=
CLOUDINARY_API_KEY=
CLOUDINARY_API_SECRET=
4️⃣ Ejecutar aplicación
./mvnw spring-boot:run
🐳 Docker
Construcción
docker build -t petmatch .
Ejecución
docker run -p 8080:8080 petmatch
📂 Estructura del proyecto
src/
 ├── controller/
 ├── service/
 ├── repository/
 ├── model/
 ├── config/
 └── resources/
🔐 Seguridad

Las credenciales sensibles fueron movidas a variables de entorno usando:

${VARIABLE_NAME}
🚀 Deploy

Proyecto desplegado en:

Railway
MySQL Cloud
Cloudinary
👨‍💻 Equipo de desarrollo
Backend Developer
Camilo Torres

Proyecto académico - PetMatch © 2026
