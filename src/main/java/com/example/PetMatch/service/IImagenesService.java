package com.example.PetMatch.service;

import java.util.List;

import com.example.PetMatch.model.dto.ImagenesDto;
import com.example.PetMatch.model.entity.Imagenes;

public interface IImagenesService {

    List<Imagenes> listAll();

    Imagenes save(ImagenesDto imagenesDto);

    Imagenes findById(Long id);

    void delete(Imagenes imagenes);

    boolean existsById(Long id);
}
