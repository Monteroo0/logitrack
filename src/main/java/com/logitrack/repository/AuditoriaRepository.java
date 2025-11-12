package com.logitrack.repository;

import com.logitrack.model.Auditoria;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AuditoriaRepository extends JpaRepository<Auditoria, Long> {
    List<Auditoria> findByUsuarioUsername(String username);
    List<Auditoria> findByTipoOperacion(String tipoOperacion);
}