package com.example.PetMatch.service;

import java.util.List;

import com.example.PetMatch.model.dto.AdopcionesDto;
import com.example.PetMatch.model.entity.Adopciones;

public interface IAdopcionesService {

    List<Adopciones> listAll();

    Adopciones save(AdopcionesDto adopcionesDto);

    Adopciones findById(Long id);

    void delete(Adopciones adopciones);

    boolean existsById(Long id);
}
