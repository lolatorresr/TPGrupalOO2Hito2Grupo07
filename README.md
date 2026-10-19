# TPGrupal OO2Hito1 Grupo07
Hito 2: Spring Boot - Sistema de Gestión Epicentro Gourmet

## Información del Grupo
* **Grupo 07**

## Integrantes
| Nombre y apellido | Usuario GitHub |
| :--- | :--- |
| Lola Torres Rodriguez | @lolatorresr |
| Juan Pablo Lombisano Bentos | @Juanplombisano |
| Lucia Daniela Ruiz | @LDR16 |

| Método | Responsable |
| :--- | :--- |
| ABM Unidad de Venta (Food Truck / Puesto Desarmable), cálculo de canon y validación de código único | Lucia Daniela Ruiz - PR: _pendiente_ |

## Pasos para correr la aplicación
1. Requisitos: Java 17, Maven 3.9+ y MySQL 8.
2. Crear la base de datos (las tablas las genera Hibernate al arrancar):
   ```sql
   CREATE DATABASE TPGrupalOO2Hito2Grupo07;
   ```
3. Credenciales de MySQL en `src/main/resources/application.yml` (por defecto `root` / `root`).
4. Ejecutar `mvn spring-boot:run` y abrir http://localhost:8080
5. Los datos de prueba se cargan automáticamente desde `src/main/resources/data.sql` en cada arranque.

### Usuarios de prueba
| Usuario | Contraseña | Rol |
| :--- | :--- | :--- |
| admin | admin123 | ROLE_ADMIN |
| responsable1 | admin123 | ROLE_RESPONSABLE |
