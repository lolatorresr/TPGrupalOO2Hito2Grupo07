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
| Entidades base: Festival, Plato, Pedido e ItemPlato | Lola Torres Rodriguez - [PR #1](https://github.com/lolatorresr/TPGrupalOO2Hito2Grupo07/pull/1) |
| Entidad Persona y su ABM base | Juan Pablo Lombisano Bentos - [PR #2](https://github.com/lolatorresr/TPGrupalOO2Hito2Grupo07/pull/2) |
| Alta de Platos (ABM de plato con precio de venta y costo de producción) | Lola Torres Rodriguez - [PR #3](https://github.com/lolatorresr/TPGrupalOO2Hito2Grupo07/pull/3) |
| ABM Unidad de Venta (Food Truck / Puesto Desarmable), cálculo de canon y validación de código único | Lucia Daniela Ruiz - [PR #4](https://github.com/lolatorresr/TPGrupalOO2Hito2Grupo07/pull/4) |
| Alta de Empleados (cajero / cocinero con categoría) con envío de mail de credenciales y baja lógica | Juan Pablo Lombisano Bentos - [PR #5](https://github.com/lolatorresr/TPGrupalOO2Hito2Grupo07/pull/5) y [PR #6](https://github.com/lolatorresr/TPGrupalOO2Hito2Grupo07/pull/6) |
| Login de empleado: redirección por rol y página del empleado (festivales y unidades donde trabaja, si trabaja hoy) | Juan Pablo Lombisano Bentos - [PR #7](https://github.com/lolatorresr/TPGrupalOO2Hito2Grupo07/pull/7) |
| Registro de Responsable con Unidad de Venta (genera el usuario del responsable) | Lucia Daniela Ruiz - [PR #8](https://github.com/lolatorresr/TPGrupalOO2Hito2Grupo07/pull/8) |
| Página de inicio: festivales, registro e inicio de sesión, accesos por rol | Lucia Daniela Ruiz - [PR #9](https://github.com/lolatorresr/TPGrupalOO2Hito2Grupo07/pull/9) |
| Alta de Pedido con sus ítems (validación de fecha del día) y pedidos precargados | Lucia Daniela Ruiz - [PR #10](https://github.com/lolatorresr/TPGrupalOO2Hito2Grupo07/pull/10) |
| Plato Estrella: plato con más pedidos de una unidad en un festival | Lola Torres Rodriguez - [PR #11](https://github.com/lolatorresr/TPGrupalOO2Hito2Grupo07/pull/11) |
| Integración de ramas y arreglos | Juan Pablo Lombisano Bentos - [PR #12](https://github.com/lolatorresr/TPGrupalOO2Hito2Grupo07/pull/12) y [PR #13](https://github.com/lolatorresr/TPGrupalOO2Hito2Grupo07/pull/13) |
| Inicio por festival: página del festival con sus unidades, registro de unidad desde un festival activo (validación: festival no finalizado) y filtro de unidades por festival | Lucia Daniela Ruiz - [PR #14](https://github.com/lolatorresr/TPGrupalOO2Hito2Grupo07/pull/14) |
| Pedidos de empleados: el empleado carga pedidos desde su página, solo para las unidades donde trabaja (validación: unidad propia y festival en curso) | Lucia Daniela Ruiz - [PR #14](https://github.com/lolatorresr/TPGrupalOO2Hito2Grupo07/pull/14) |
| Reportes del administrador: ranking de unidades por recaudación de un festival y unidades con mayor canon (ReporteMayoresCanonDTO) | Lucia Daniela Ruiz - PR: [PR #15](https://github.com/lolatorresr/TPGrupalOO2Hito2Grupo07/pull/15) |
| Liquidación de haberes del empleado y recibo de sueldo en PDF | Lola Torres Rodriguez - [PR #16](https://github.com/lolatorresr/TPGrupalOO2Hito2Grupo07/pull/16) |
| Ajustes de la liquidación: acceso con sesión (cada empleado ve solo su recibo), sueldos por festival en la página del empleado y estilo del sitio | Lucia Daniela Ruiz - PR: [PR #17](https://github.com/lolatorresr/TPGrupalOO2Hito2Grupo07/pull/17) |
| Plato estrella con la barra del sitio y acceso desde la gestión de platos | Lucia Daniela Ruiz - PR: [PR #18](https://github.com/lolatorresr/TPGrupalOO2Hito2Grupo07/pull/18) |
| Reactivar empleado dado de baja (método de actualización específico), data.sql sin la columna activo y ajustes de email y usuario | Juan Pablo Lombisano Bentos - [PR #19](https://github.com/lolatorresr/TPGrupalOO2Hito2Grupo07/pull/19) |
| Alta de pedido: entre los empleados, solo los cajeros pueden cargar pedidos | Juan Pablo Lombisano Bentos - [PR #20](https://github.com/lolatorresr/TPGrupalOO2Hito2Grupo07/pull/20) |
| Cierre de caja del cajero al finalizar el día (CierreCaja, historial de cierres y bloqueo de nuevos pedidos con la caja cerrada) | Juan Pablo Lombisano Bentos - [PR #21](https://github.com/lolatorresr/TPGrupalOO2Hito2Grupo07/pull/21) |
| Reportes del responsable: recaudación de un festival (ReporteVentaDTO), rentabilidad neta de una unidad en un festival y rentabilidad neta entre dos fechas | Lucia Daniela Ruiz - PR: [PR #22](https://github.com/lolatorresr/TPGrupalOO2Hito2Grupo07/pull/22) |

## Pasos para correr la aplicación
1. Requisitos: Java 17, Maven 3.9+ y MySQL 8.
2. Crear la base de datos (las tablas las genera Hibernate al arrancar):
   ```sql
   CREATE DATABASE TPGrupalOO2Hito2Grupo07;
   ```
3. Configurar la conexión y el mail con variables de entorno (opcional: si no se definen se usan los valores por defecto de `src/main/resources/application.yml`):

   | Variable | Para qué | Por defecto |
   | :--- | :--- | :--- |
   | `DB_HOST` | Host de MySQL | `localhost` |
   | `DB_PORT` | Puerto de MySQL | `3306` |
   | `DB_NAME` | Nombre de la base | `TPGrupalOO2Hito2Grupo07` |
   | `DB_USER` | Usuario de MySQL | `root` |
   | `DB_PASSWORD` | Contraseña de MySQL | `root` |
   | `EMAIL_USER` | Cuenta de Gmail desde la que se envían los mails a los empleados | vacío (no se envía) |
   | `EMAIL_PASSWORD` | Contraseña de aplicación de esa cuenta | vacío |

   Ejemplo en Linux / macOS: `export DB_USER=miusuario DB_PASSWORD=miclave` antes de ejecutar. En Windows (PowerShell): `$env:DB_USER="miusuario"`.
4. Ejecutar `mvn spring-boot:run` y abrir http://localhost:8080
5. Los datos de prueba se cargan automáticamente desde `src/main/resources/data.sql` en cada arranque.

### Usuarios de prueba
| Usuario | Contraseña | Rol |
| :--- | :--- | :--- |
| admin | admin123 | ROLE_ADMIN |
| responsable1 | admin123 | ROLE_RESPONSABLE (Martín García, unidades Burger Truck, Taco Móvil y Choripanes del Sur) |
| responsable2 | admin123 | ROLE_RESPONSABLE (Ana Martínez, unidades Pizza Nostra y Empanadas del Norte) |
| responsable3 | admin123 | ROLE_RESPONSABLE (Julián Pereyra, unidades Pizza Truck y Cositas Dulces) |
| empleado1 | admin123 | ROLE_EMPLEADO (Diego Fernández, trabaja en Choripanes del Sur) |
| cajero1 | admin123 | ROLE_EMPLEADO (Carlos López, cajero de Burger Truck y Pizza Nostra; puede cerrar caja) |
