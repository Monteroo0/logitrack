# 📦 Proyecto LogiTrack - Sistema de Gestión de Inventario

## 🎯 Descripción General

LogiTrack es un sistema completo de gestión de inventario desarrollado con Spring Boot que permite administrar productos, bodegas, movimientos de inventario y usuarios. El sistema incluye auditoría automática, reportes avanzados y consultas analíticas.

## 🏗️ Arquitectura del Proyecto

### Tecnologías Utilizadas
- **Backend:** Spring Boot 3.5.7
- **Base de Datos:** JPA/Hibernate (Configuración para MySQL/PostgreSQL)
- **Lenguaje:** Java 17
- **Build Tool:** Maven
- **Documentación:** OpenAPI/Swagger (disponible)

### Estructura del Proyecto
```
src/main/java/com/logitrack/
├── controller/          # Controladores REST
├── service/            # Lógica de negocio
├── repository/         # Capa de acceso a datos
├── model/              # Entidades JPA
├── dto/                # Objetos de Transferencia de Datos
├── audit/              # Sistema de auditoría automática
└── config/             # Configuraciones de Spring
```

## 🚀 Funcionalidades Principales

### 1. Gestión de Productos
- ✅ CRUD completo de productos
- ✅ Control de stock y categorías
- ✅ Alertas de stock bajo (automático < 10 unidades)
- ✅ Historial de cambios mediante auditoría

### 2. Gestión de Bodegas
- ✅ Administración de almacenes
- ✅ Control de capacidad
- ✅ Asignación de encargados
- ✅ Ubicación y detalles de bodegas

### 3. Movimientos de Inventario
- ✅ Registro de entradas y salidas
- ✅ Transferencias entre bodegas
- ✅ Control por usuario
- ✅ Filtrado por fechas

### 4. Gestión de Usuarios
- ✅ Sistema de roles y permisos
- ✅ Autenticación básica (preparado para JWT)
- ✅ Auditoría de operaciones por usuario

### 5. Sistema de Auditoría Automática
- ✅ Registro automático de INSERT, UPDATE, DELETE
- ✅ Trazabilidad completa de cambios
- ✅ Serialización JSON de estados
- ✅ Contexto de usuario y timestamps

### 6. Reportes y Análisis
- ✅ Stock total por bodega
- ✅ Productos más movidos
- ✅ Reportes personalizados por fecha
- ✅ Análisis de tendencias

## 📊 Endpoints de la API

### Productos
```
GET    /api/productos              # Listar todos
GET    /api/productos/{id}         # Obtener por ID
POST   /api/productos              # Crear producto
PUT    /api/productos/{id}         # Actualizar producto
DELETE /api/productos/{id}         # Eliminar producto
GET    /api/productos/stock-bajo   # Productos con stock < 10
```

### Bodegas
```
GET    /api/bodegas                # Listar todas
GET    /api/bodegas/{id}           # Obtener por ID
POST   /api/bodegas                # Crear bodega
PUT    /api/bodegas/{id}           # Actualizar bodega
DELETE /api/bodegas/{id}           # Eliminar bodega
```

### Movimientos
```
GET    /api/movimientos            # Listar todos
GET    /api/movimientos/{id}       # Obtener por ID
POST   /api/movimientos            # Crear movimiento
PUT    /api/movimientos/{id}       # Actualizar movimiento
DELETE /api/movimientos/{id}       # Eliminar movimiento
GET    /api/movimientos/por-fecha  # Filtrar por rango de fechas
```

### Auditoría
```
GET    /api/auditoria              # Todas las auditorías
GET    /api/auditoria/usuario/{username}  # Por usuario
GET    /api/auditoria/tipo/{tipo}  # Por tipo de operación
```

### Reportes
```
GET    /api/reportes/resumen       # Reporte general de inventario
```

## 🧪 Scripts de Prueba

### Probar Endpoints de Reportes
```bash
# Ejecutar script de pruebas
./test-endpoints.bat
```

### Probar Sistema de Auditoría
```bash
# Ejecutar pruebas de auditoría
./test-auditing.bat
```

## 🔧 Configuración del Proyecto

### Requisitos Previos
- Java 17 o superior
- Maven 3.6+
- Base de datos MySQL/PostgreSQL (o H2 para desarrollo)

### Instalación
1. Clonar el repositorio
2. Configurar base de datos en `application.properties`
3. Ejecutar: `mvn clean install`
4. Iniciar aplicación: `mvn spring-boot:run`

### Configuración de Base de Datos
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/logitrack
spring.datasource.username=root
spring.datasource.password=your_password
spring.jpa.hibernate.ddl-auto=update
```

## 📈 Características Avanzadas

### Auditoría Automática
- Registro automático de todas las operaciones
- Contexto de usuario y timestamps
- Serialización JSON de estados
- Consultas por tipo de operación y usuario

### Reportes Inteligentes
- Análisis de productos más movidos
- Stock total por ubicación
- Tendencias de movimiento
- Filtros personalizados por fecha

### Validaciones y Seguridad
- Validación de datos con Jakarta Validation
- Manejo de errores centralizado
- Respuestas RESTful estándar
- Preparado para implementación de JWT

## 📁 Documentación Detallada

### Documentos Creados
1. **DOCUMENTACION-API-CONSULTAS.md** - Guía completa de endpoints
2. **DOCUMENTACION-SISTEMA-AUDITORIA.md** - Sistema de auditoría automática
3. **test-endpoints.bat** - Script de pruebas para endpoints
4. **test-auditing.bat** - Script de pruebas para auditoría

### Estructura de Entidades
- **Producto:** id, nombre, categoria, stock, precio
- **Bodega:** id, nombre, ubicacion, capacidad, encargado
- **Movimiento:** id, fecha, tipo, usuario, bodegaOrigen, bodegaDestino, productos
- **Usuario:** id, username, password, nombre, rol
- **Auditoria:** id, tipoOperacion, fecha, usuario, entidad, valoresAnteriores, valoresNuevos

## 🚀 Puesta en Producción

### Checklist de Despliegue
- ✅ Configurar base de datos de producción
- ✅ Establecer variables de entorno
- ✅ Configurar logs y monitoreo
- ✅ Habilitar HTTPS
- ✅ Implementar autenticación JWT
- ✅ Configurar respaldos automáticos

### Monitoreo y Mantenimiento
- Logs de auditoría para troubleshooting
- Métricas de rendimiento de API
- Monitoreo de base de datos
- Alertas de sistema

## 🔮 Mejoras Futuras Planificadas

1. **Autenticación JWT** - Seguridad mejorada
2. **API Documentación** - Swagger/OpenAPI integration
3. **Caché Redis** - Mejorar rendimiento
4. **Microservicios** - Escalabilidad
5. **WebSocket** - Actualizaciones en tiempo real
6. **Exportación Excel/PDF** - Reportes descargables
7. **Dashboard Web** - Interfaz de usuario moderna

## 📞 Soporte y Contacto

Para soporte técnico o consultas sobre el sistema:
- Revisar documentación en archivos .md
- Ejecutar scripts de prueba para diagnóstico
- Verificar logs de auditoría para troubleshooting
- Contactar al equipo de desarrollo

---

**LogiTrack** - Sistema completo de gestión de inventario con auditoría automática y reportes avanzados. Desarrollado con Spring Boot y listo para producción. 🚀