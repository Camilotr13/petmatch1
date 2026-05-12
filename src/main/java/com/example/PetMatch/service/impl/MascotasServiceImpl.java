package com.example.PetMatch.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.PetMatch.model.dao.MascotasDao;
import com.example.PetMatch.model.dao.UsuariosDao;
import com.example.PetMatch.model.dto.MascotasDto;
import com.example.PetMatch.model.entity.Mascotas;
import com.example.PetMatch.model.entity.Usuarios;
import com.example.PetMatch.service.IMascotasService;

@Service
public class MascotasServiceImpl implements IMascotasService {

    @Autowired
    private MascotasDao mascotasDao;

    @Autowired
    private UsuariosDao usuariosDao;

    @Override
    public List<Mascotas> listAll() {
        return (List<Mascotas>) mascotasDao.findAll();
    }

    @Transactional
    @Override
    public Mascotas save(MascotasDto mascotasDto) {

        Usuarios usuario = usuariosDao.findById(
                mascotasDto.getUsuarioId().intValue()
        ).orElse(null);

        if (usuario == null) {
            throw new RuntimeException("El usuario asociado no existe");
        }

        Mascotas mascotas = Mascotas.builder()
                .id(mascotasDto.getId())
                .nombre(mascotasDto.getNombre())
                .tipo(mascotasDto.getTipo())
                .edad(mascotasDto.getEdad())
                .descripcion(mascotasDto.getDescripcion())
                .estado(mascotasDto.getEstado())
                .usuario(usuario)
                .created_at(mascotasDto.getCreated_at())
                .build();

        return mascotasDao.save(mascotas);
    }

    @Transactional(readOnly = true)
    @Override
    public Mascotas findById(Long id) {
        return mascotasDao.findById(id).orElse(null);
    }

    @Transactional
    @Override
    public void delete(Mascotas mascotas) {
        mascotasDao.delete(mascotas);
    }

    @Override
    public boolean existsById(Long id) {
        return mascotasDao.existsById(id);
    }
}
