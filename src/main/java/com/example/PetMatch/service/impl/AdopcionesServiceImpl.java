package com.example.PetMatch.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.PetMatch.model.dao.AdopcionesDao;
import com.example.PetMatch.model.dao.MascotasDao;
import com.example.PetMatch.model.dao.UsuariosDao;
import com.example.PetMatch.model.dto.AdopcionesDto;
import com.example.PetMatch.model.entity.Adopciones;
import com.example.PetMatch.model.entity.Mascotas;
import com.example.PetMatch.model.entity.Usuarios;
import com.example.PetMatch.service.IAdopcionesService;

@Service
public class AdopcionesServiceImpl implements IAdopcionesService {

    @Autowired
    private AdopcionesDao adopcionesDao;

    @Autowired
    private UsuariosDao usuariosDao;

    @Autowired
    private MascotasDao mascotasDao;

    @Override
    public List<Adopciones> listAll() {
        return (List<Adopciones>) adopcionesDao.findAll();
    }

    @Transactional
    @Override
    public Adopciones save(AdopcionesDto dto) {

        if (dto.getUsuarioId() == null) {
            throw new RuntimeException("El campo usuarioId es obligatorio");
        }

        if (dto.getMascotaId() == null) {
            throw new RuntimeException("El campo mascotaId es obligatorio");
        }

        Usuarios usuario = usuariosDao.findById(dto.getUsuarioId().intValue())
                .orElse(null);

        if (usuario == null) {
            throw new RuntimeException("El usuario asociado no existe");
        }

        Mascotas mascota = mascotasDao.findById(dto.getMascotaId())
                .orElse(null);

        if (mascota == null) {
            throw new RuntimeException("La mascota asociada no existe");
        }

        if ("adoptada".equals(mascota.getEstado())) {
            throw new RuntimeException("La mascota ya ha sido adoptada");
        }

        Adopciones adopciones = Adopciones.builder()
                .id(dto.getId())
                .estado(dto.getEstado() != null ? dto.getEstado() : "pendiente")
                .usuario(usuario)
                .mascota(mascota)
                .build();

        Adopciones saved = adopcionesDao.save(adopciones);

        if (dto.getId() == null) {
            mascota.setEstado("adoptada");
            mascotasDao.save(mascota);
        }

        return saved;
    }

    @Transactional(readOnly = true)
    @Override
    public Adopciones findById(Long id) {
        return adopcionesDao.findById(id).orElse(null);
    }

    @Transactional
    @Override
    public void delete(Adopciones adopciones) {
        adopcionesDao.delete(adopciones);
    }

    @Override
    public boolean existsById(Long id) {
        return adopcionesDao.existsById(id);
    }
}
