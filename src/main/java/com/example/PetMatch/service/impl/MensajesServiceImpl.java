package com.example.PetMatch.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.PetMatch.model.dao.MensajesDao;
import com.example.PetMatch.model.dao.UsuariosDao;
import com.example.PetMatch.model.dto.MensajesDto;
import com.example.PetMatch.model.entity.Mensajes;
import com.example.PetMatch.model.entity.Usuarios;
import com.example.PetMatch.service.IMensajesService;

@Service
public class MensajesServiceImpl implements IMensajesService {

    @Autowired
    private MensajesDao mensajesDao;

    @Autowired
    private UsuariosDao usuariosDao;

    @Override
    public List<Mensajes> listAll() {
        return (List<Mensajes>) mensajesDao.findAll();
    }

    @Transactional
    @Override
    public Mensajes save(MensajesDto dto) {

        if (dto.getEmisorId() == null) {
            throw new RuntimeException("El campo emisorId es obligatorio");
        }

        if (dto.getReceptorId() == null) {
            throw new RuntimeException("El campo receptorId es obligatorio");
        }

        Usuarios emisor = usuariosDao.findById(dto.getEmisorId().intValue())
                .orElse(null);

        if (emisor == null) {
            throw new RuntimeException("El emisor asociado no existe");
        }

        Usuarios receptor = usuariosDao.findById(dto.getReceptorId().intValue())
                .orElse(null);

        if (receptor == null) {
            throw new RuntimeException("El receptor asociado no existe");
        }

        Mensajes mensajes = Mensajes.builder()
                .id(dto.getId())
                .contenido(dto.getContenido())
                .emisor(emisor)
                .receptor(receptor)
                .build();

        return mensajesDao.save(mensajes);
    }

    @Transactional(readOnly = true)
    @Override
    public Mensajes findById(Long id) {
        return mensajesDao.findById(id).orElse(null);
    }

    @Transactional
    @Override
    public void delete(Mensajes mensajes) {
        mensajesDao.delete(mensajes);
    }

    @Override
    public boolean existsById(Long id) {
        return mensajesDao.existsById(id);
    }
}
