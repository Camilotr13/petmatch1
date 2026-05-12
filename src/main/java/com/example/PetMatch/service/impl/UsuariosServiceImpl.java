package com.example.PetMatch.service.impl;

import com.example.PetMatch.model.dao.UsuariosDao;
import com.example.PetMatch.model.dto.UsuariosDto;
import com.example.PetMatch.model.entity.Usuarios;
import com.example.PetMatch.service.IUsuariosService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UsuariosServiceImpl implements IUsuariosService {

    @Autowired

    private UsuariosDao usuariosDao;

    @Override
    public List<Usuarios> listAll() {
        return (List) usuariosDao.findAll();
    }

    @Transactional
    @Override
    public Usuarios save(UsuariosDto usuariosDto) {
        Usuarios usuarios = Usuarios.builder()
                .id(usuariosDto.getId())
                .nombre(usuariosDto.getNombre())
                .email(usuariosDto.getEmail())
                .password(usuariosDto.getPassword())
                .rol(usuariosDto.getRol())
                .created_at(usuariosDto.getCreated_at())

                .build();
        return usuariosDao.save(usuarios);
    }

    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    @Override
    public Usuarios findById(Integer id) {

        return usuariosDao.findById(id).orElse(null);
    }

    @org.springframework.transaction.annotation.Transactional
    @Override
    public void delete(Usuarios usuarios) {

        usuariosDao.delete(usuarios);
    }

    @Override
    public boolean existsById(Integer id) {
        return usuariosDao.existsById(id);
    }
}
