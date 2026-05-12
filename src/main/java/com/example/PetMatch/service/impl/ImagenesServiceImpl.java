package com.example.PetMatch.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.PetMatch.model.dao.ImagenesDao;
import com.example.PetMatch.model.dao.MascotasDao;
import com.example.PetMatch.model.dto.ImagenesDto;
import com.example.PetMatch.model.entity.Imagenes;
import com.example.PetMatch.model.entity.Mascotas;
import com.example.PetMatch.service.IImagenesService;

@Service
public class ImagenesServiceImpl implements IImagenesService {

    @Autowired
    private ImagenesDao imagenesDao;

    @Autowired
    private MascotasDao mascotasDao;

    @Override
    public List<Imagenes> listAll() {
        return (List<Imagenes>) imagenesDao.findAll();
    }

    @Transactional
    @Override
    public Imagenes save(ImagenesDto imagenesDto) {

        if (imagenesDto.getMascotaId() == null) {
            throw new RuntimeException("El campo mascotaId es obligatorio");
        }

        Mascotas mascota = mascotasDao.findById(imagenesDto.getMascotaId())
                .orElse(null);

        if (mascota == null) {
            throw new RuntimeException("La mascota asociada no existe");
        }

        Imagenes imagenes = Imagenes.builder()
                .id(imagenesDto.getId())
                .url(imagenesDto.getUrl())
                .mascota(mascota)
                .build();

        return imagenesDao.save(imagenes);
    }

    @Transactional(readOnly = true)
    @Override
    public Imagenes findById(Long id) {
        return imagenesDao.findById(id).orElse(null);
    }

    @Transactional
    @Override
    public void delete(Imagenes imagenes) {
        imagenesDao.delete(imagenes);
    }

    @Override
    public boolean existsById(Long id) {
        return imagenesDao.existsById(id);
    }
}
