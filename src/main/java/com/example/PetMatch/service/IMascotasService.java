package com.example.PetMatch.service;

import java.util.List;

import com.example.PetMatch.model.dto.MascotasDto;
import com.example.PetMatch.model.entity.Mascotas;

public interface IMascotasService {

    List<Mascotas> listAll();

    Mascotas save(MascotasDto mascotasDto);

    Mascotas findById(Long id);

    void delete(Mascotas mascotas);

    boolean existsById(Long id);
}
