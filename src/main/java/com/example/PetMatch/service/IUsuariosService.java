package com.example.PetMatch.service;

import com.example.PetMatch.model.dto.UsuariosDto;
import com.example.PetMatch.model.entity.Usuarios;

import java.util.List;

public interface IUsuariosService {

    List<Usuarios> listAll();

    Usuarios save(UsuariosDto usuarios);

    Usuarios findById(Integer id);

    void delete(Usuarios usuarios);

    boolean existsById(Integer id);

}

