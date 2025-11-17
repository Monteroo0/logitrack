package com.logitrack.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.logitrack.audit.AuditoriaListener;
import com.logitrack.repository.AuditoriaRepository;
import com.logitrack.repository.UsuarioRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AuditConfig {

    @Bean
    public AuditoriaListener auditoriaListener(AuditoriaRepository auditoriaRepository, 
                                             UsuarioRepository usuarioRepository,
                                             ObjectMapper objectMapper) {
        AuditoriaListener listener = new AuditoriaListener();
        listener.setAuditoriaRepository(auditoriaRepository);
        listener.setUsuarioRepository(usuarioRepository);
        listener.setObjectMapper(objectMapper);
        return listener;
    }

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return mapper;
    }
}