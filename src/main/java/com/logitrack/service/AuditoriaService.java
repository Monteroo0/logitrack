package com.logitrack.service;

import com.logitrack.model.Auditoria;
import com.logitrack.repository.AuditoriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuditoriaService {

    @Autowired
    private AuditoriaRepository auditoriaRepository;

    public List<Auditoria> findAll() {
        return auditoriaRepository.findAll();
    }

    public List<Auditoria> findByUsuarioUsername(String username) {
        return auditoriaRepository.findByUsuarioUsername(username);
    }

    public List<Auditoria> findByTipoOperacion(String tipoOperacion) {
        return auditoriaRepository.findByTipoOperacion(tipoOperacion);
    }
}