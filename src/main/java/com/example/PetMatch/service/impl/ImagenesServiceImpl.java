package com.example.PetMatch.service.impl;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.PetMatch.model.dao.ImagenesDao;
import com.example.PetMatch.model.dao.MascotasDao;
import com.example.PetMatch.model.dto.ImagenesDto;
import com.example.PetMatch.model.entity.Imagenes;
import com.example.PetMatch.model.entity.Mascotas;
import com.example.PetMatch.service.CloudinaryService;
import com.example.PetMatch.service.IImagenesService;

@Service
public class ImagenesServiceImpl implements IImagenesService {

    @Autowired
    private ImagenesDao imagenesDao;

    @Autowired
    private MascotasDao mascotasDao;

    @Autowired
    private CloudinaryService cloudinaryService;

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

    @Transactional
    @Override
    public Imagenes upload(MultipartFile file, Long mascotaId) {
        // Validar archivo no vacío
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("El archivo de imagen es obligatorio");
        }

        // Validar content-type
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new RuntimeException("El archivo debe ser una imagen válida");
        }

        // Validar tamaño (5 MB = 5 * 1024 * 1024 bytes)
        long maxSize = 5 * 1024 * 1024;
        if (file.getSize() > maxSize) {
            throw new RuntimeException("El archivo no debe superar los 5 MB");
        }

        // Validar mascotaId
        if (mascotaId == null) {
            throw new RuntimeException("El campo mascotaId es obligatorio");
        }

        // Validar existencia de mascota
        Mascotas mascota = mascotasDao.findById(mascotaId).orElse(null);
        if (mascota == null) {
            throw new RuntimeException("La mascota asociada no existe");
        }

        // Subir a Cloudinary
        String secureUrl;
        try {
            secureUrl = cloudinaryService.uploadImage(file);
        } catch (IOException e) {
            throw new RuntimeException("Error al subir la imagen a Cloudinary");
        }

        // Persistir registro
        Imagenes imagen = Imagenes.builder()
                .url(secureUrl)
                .mascota(mascota)
                .build();

        return imagenesDao.save(imagen);
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
