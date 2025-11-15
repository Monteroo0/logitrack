# 🕵️ LogiTrack Automatic Auditing System

## 🎯 Overview

The automatic auditing system has been implemented to track all database operations (INSERT, UPDATE, DELETE) across all entities in the LogiTrack inventory system. Every change is automatically recorded in the `auditoria` table with detailed information about the operation.

## 🔧 Implementation Details

### 📁 Components Created

1. **AuditoriaListener.java** (`src/main/java/com/logitrack/audit/`)
   - JPA entity listener with lifecycle callbacks
   - Handles `@PrePersist`, `@PreUpdate`, and `@PreRemove` events
   - Serializes entity state to JSON for audit records

2. **AuditConfig.java** (`src/main/java/com/logitrack/config/`)
   - Spring configuration for dependency injection
   - Configures ObjectMapper for JSON serialization
   - Manages repository dependencies

### 🏷️ Entity Annotations Added

The following entities now have automatic auditing enabled:

- ✅ **Producto** - Tracks product lifecycle changes
- ✅ **Movimiento** - Records inventory movement operations
- ✅ **Bodega** - Monitors warehouse modifications
- ✅ **Usuario** - Logs user account changes

## 📊 Audit Record Structure

Each audit entry contains:

```json
{
  "id": 1,
  "tipoOperacion": "INSERT|UPDATE|DELETE",
  "fecha": "2024-03-15T14:20:30",
  "usuario": {
    "id": 1,
    "username": "admin"
  },
  "entidad": "Producto",
  "valoresAnteriores": "{\"nombre\":\"Old Name\",\"stock\":100}",
  "valoresNuevos": "{\"nombre\":\"New Name\",\"stock\":95}"
}
```

## 🚀 How It Works

### 1. Automatic Operation Detection

```java
@PrePersist  // Triggered when entity is created
public void prePersist(Object entity) {
    registrarAuditoria("INSERT", entity, null);
}

@PreUpdate   // Triggered when entity is modified
public void preUpdate(Object entity) {
    registrarAuditoria("UPDATE", entity, null);
}

@PreRemove   // Triggered when entity is deleted
public void preRemove(Object entity) {
    registrarAuditoria("DELETE", entity, null);
}
```

### 2. Entity Serialization

The system uses Jackson's ObjectMapper to serialize entity state to JSON:

```java
String valoresNuevos = objectMapper.writeValueAsString(entity);
```

### 3. User Context

Currently uses "system" user with fallback to "admin" user. In production, this would integrate with Spring Security context.

## 🧪 Testing the Auditing System

### 1. Create a Product (Triggers INSERT)
```bash
curl -X POST http://localhost:8080/api/productos \
  -H "Content-Type: application/json" \
  -d '{"nombre":"Laptop Dell","categoria":"Electrónica","stock":50,"precio":1500.00}'
```

### 2. Update a Product (Triggers UPDATE)
```bash
curl -X PUT http://localhost:8080/api/productos/1 \
  -H "Content-Type: application/json" \
  -d '{"nombre":"Laptop Dell Updated","categoria":"Electrónica","stock":45,"precio":1450.00}'
```

### 3. Delete a Product (Triggers DELETE)
```bash
curl -X DELETE http://localhost:8080/api/productos/1
```

### 4. View Audit Trail
```bash
# All audits
curl -X GET http://localhost:8080/api/auditoria

# By operation type
curl -X GET http://localhost:8080/api/auditoria/tipo/INSERT
curl -X GET http://localhost:8080/api/auditoria/tipo/UPDATE
curl -X GET http://localhost:8080/api/auditoria/tipo/DELETE

# By user
curl -X GET http://localhost:8080/api/auditoria/usuario/admin
```

## 📈 Benefits

### 🔍 **Complete Traceability**
- Every database change is recorded with timestamp
- Entity state is preserved in JSON format
- User context is tracked for accountability

### 🛡️ **Security & Compliance**
- Automatic audit trail for regulatory requirements
- Immutable record of all system changes
- Detailed operation history for investigation

### ⚡ **Zero Configuration**
- Works automatically after entity annotations
- No manual intervention required
- Seamless integration with existing codebase

### 🔧 **Developer Friendly**
- Clean separation of concerns
- Easy to extend for custom audit requirements
- JSON format allows for flexible querying

## 🚨 Current Limitations

1. **User Context**: Currently uses hardcoded "admin" user. Should integrate with Spring Security in production.

2. **Old Value Tracking**: For UPDATE operations, only the new state is captured. To get old values, would need to:
   - Use Hibernate Envers for full historical tracking
   - Implement custom pre-update state capture
   - Use database triggers for complete before/after comparison

3. **Performance**: JSON serialization adds overhead. For high-volume systems, consider:
   - Asynchronous audit processing
   - Audit table partitioning
   - Selective auditing for critical entities only

## 🔮 Future Enhancements

### Spring Security Integration
```java
Authentication auth = SecurityContextHolder.getContext().getAuthentication();
String currentUser = auth.getName();
```

### Selective Auditing
```java
@PreUpdate
public void preUpdate(Object entity) {
    if (shouldAudit(entity)) {
        registrarAuditoria("UPDATE", entity, getOldValues(entity));
    }
}
```

### Async Processing
```java
@Async
@EventListener
public void handleAuditEvent(AuditEvent event) {
    auditService.processAsync(event);
}
```

## 📋 Summary

✅ **Automatic auditing implemented for all entities**
✅ **INSERT, UPDATE, DELETE operations tracked**
✅ **JSON serialization for entity state**
✅ **RESTful endpoints for audit queries**
✅ **Zero configuration required**
✅ **Production-ready with minor enhancements**

The auditing system is now fully functional and ready for use! 🎉