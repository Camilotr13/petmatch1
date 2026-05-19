package com.example.PetMatch.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.example.PetMatch.model.dto.ImagenesDto;
import com.example.PetMatch.model.entity.Imagenes;

public interface IImagenesService {

    List<Imagenes> listAll();

    Imagenes save(ImagenesDto imagenesDto);

    Imagenes upload(MultipartFile file, Long mascotaId);

    Imagenes findById(Long id);

    void delete(Imagenes imagenes);

    boolean existsById(Long id);
}
