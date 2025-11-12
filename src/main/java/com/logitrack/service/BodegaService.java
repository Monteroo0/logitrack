package com.logitrack.service;

import com.logitrack.model.Bodega;
import com.logitrack.repository.BodegaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BodegaService {

    @Autowired
    private BodegaRepository bodegaRepository;

    public List<Bodega> findAll() {
        return bodegaRepository.findAll();
    }

    public Optional<Bodega> findById(Long id) {
        return bodegaRepository.findById(id);
    }

    public Bodega save(Bodega bodega) {
        return bodegaRepository.save(bodega);
    }

    public void deleteById(Long id) {
        bodegaRepository.deleteById(id);
    }
}