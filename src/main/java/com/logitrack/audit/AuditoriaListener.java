package com.logitrack.audit;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.logitrack.model.Auditoria;
import com.logitrack.model.Usuario;
import com.logitrack.repository.AuditoriaRepository;
import com.logitrack.repository.UsuarioRepository;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreRemove;
import jakarta.persistence.PreUpdate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;

@Component
public class AuditoriaListener {

    private static AuditoriaRepository auditoriaRepository;
    private static UsuarioRepository usuarioRepository;
    private static ObjectMapper objectMapper;

    @Autowired
    @Lazy
    public void setAuditoriaRepository(AuditoriaRepository auditoriaRepository) {
        AuditoriaListener.auditoriaRepository = auditoriaRepository;
    }

    @Autowired
    @Lazy
    public void setUsuarioRepository(UsuarioRepository usuarioRepository) {
        AuditoriaListener.usuarioRepository = usuarioRepository;
    }

    @Autowired
    @Lazy
    public void setObjectMapper(ObjectMapper objectMapper) {
        AuditoriaListener.objectMapper = objectMapper;
    }

    @PrePersist
    public void prePersist(Object entity) {
        registrarAuditoria("INSERT", entity, null);
    }

    @PreUpdate
    public void preUpdate(Object entity) {
        // For UPDATE operations, we need to get the old values
        // Since we can't get the old state in PreUpdate, we'll store the current state
        // and handle the comparison logic in the service layer if needed
        registrarAuditoria("UPDATE", entity, null);
    }

    @PreRemove
    public void preRemove(Object entity) {
        registrarAuditoria("DELETE", entity, null);
    }

    private void registrarAuditoria(String operacion, Object entity, String valoresAnteriores) {
        try {
            if (auditoriaRepository == null) {
                System.out.println("AuditoriaRepository no está disponible - operación no registrada: " + operacion);
                return;
            }

            Auditoria auditoria = new Auditoria();
            auditoria.setTipoOperacion(operacion);
            auditoria.setFecha(LocalDateTime.now());
            auditoria.setEntidad(entity.getClass().getSimpleName());
            
            // Try to get the current user - for now use "system" as fallback
            String currentUser = "system";
            try {
                if (usuarioRepository != null) {
                    // In a real application, you would get the current user from Spring Security context
                    Usuario usuario = usuarioRepository.findByUsername("admin");
                    if (usuario != null) {
                        auditoria.setUsuario(usuario);
                    }
                }
            } catch (Exception e) {
                // If user lookup fails, continue without user
            }
            
            // Serialize the entity to JSON for the new values
            if (objectMapper != null) {
                String valoresNuevos = objectMapper.writeValueAsString(entity);
                auditoria.setValoresNuevos(valoresNuevos);
            } else {
                auditoria.setValoresNuevos(entity.toString());
            }
            
            if (valoresAnteriores != null) {
                auditoria.setValoresAnteriores(valoresAnteriores);
            }
            
            auditoriaRepository.save(auditoria);
            System.out.println("Auditoría registrada: " + operacion + " en " + entity.getClass().getSimpleName());
            
        } catch (Exception e) {
            System.err.println("Error al registrar auditoría: " + e.getMessage());
            e.printStackTrace();
        }
    }
}