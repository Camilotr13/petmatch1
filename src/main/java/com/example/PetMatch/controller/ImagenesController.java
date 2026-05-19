package com.example.PetMatch.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.PetMatch.model.dto.ImagenesDto;
import com.example.PetMatch.model.entity.Imagenes;
import com.example.PetMatch.model.payload.MensajeResponse;
import com.example.PetMatch.service.IImagenesService;

@RestController
@RequestMapping("/api/v1")
public class ImagenesController {

    @Autowired
    private IImagenesService imagenesService;

    @GetMapping("imagenes")
    public ResponseEntity<?> showAll() {
        try {
            List<Imagenes> getList = imagenesService.listAll();
            if (getList == null || getList.isEmpty()) {
                return new ResponseEntity<>(
                        MensajeResponse.builder()
                                .mensaje("No hay registros")
                                .object(null)
                                .build(),
                        HttpStatus.OK);
            }
            List<ImagenesDto> dtoList = getList.stream()
                    .map(imagen -> ImagenesDto.builder()
                            .id(imagen.getId())
                            .url(imagen.getUrl())
                            .mascotaId(imagen.getMascota() != null ? imagen.getMascota().getId() : null)
                            .build())
                    .collect(Collectors.toList());
            return new ResponseEntity<>(
                    MensajeResponse.builder()
                            .mensaje("")
                            .object(dtoList)
                            .build(),
                    HttpStatus.OK);
        } catch (DataAccessException exDT) {
            return new ResponseEntity<>(
                    MensajeResponse.builder()
                            .mensaje(exDT.getMessage())
                            .object(null)
                            .build(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("imagen")
    public ResponseEntity<?> create(@RequestBody ImagenesDto imagenesDto) {
        Imagenes imagenSave = null;
        try {
            imagenSave = imagenesService.save(imagenesDto);
            return new ResponseEntity<>(
                    MensajeResponse.builder()
                            .mensaje("Guardado Correctamente")
                            .object(ImagenesDto.builder()
                                    .id(imagenSave.getId())
                                    .url(imagenSave.getUrl())
                                    .mascotaId(imagenSave.getMascota() != null ? imagenSave.getMascota().getId() : null)
                                    .build())
                            .build(),
                    HttpStatus.CREATED);
        } catch (DataAccessException exDT) {
            return new ResponseEntity<>(
                    MensajeResponse.builder()
                            .mensaje(exDT.getMessage())
                            .object(null)
                            .build(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (RuntimeException ex) {
            String message = ex.getMessage();
            if (message.contains("obligatorio")) {
                return new ResponseEntity<>(
                        MensajeResponse.builder()
                                .mensaje(message)
                                .object(null)
                                .build(),
                        HttpStatus.BAD_REQUEST);
            } else if (message.contains("no existe")) {
                return new ResponseEntity<>(
                        MensajeResponse.builder()
                                .mensaje(message)
                                .object(null)
                                .build(),
                        HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(
                    MensajeResponse.builder()
                            .mensaje(message)
                            .object(null)
                            .build(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("imagen/upload")
    public ResponseEntity<?> upload(
            @RequestParam(value = "file", required = false) MultipartFile file,
            @RequestParam(value = "mascotaId", required = false) Long mascotaId) {
        try {
            Imagenes imagenSave = imagenesService.upload(file, mascotaId);
            return new ResponseEntity<>(
                    MensajeResponse.builder()
                            .mensaje("Guardado Correctamente")
                            .object(ImagenesDto.builder()
                                    .id(imagenSave.getId())
                                    .url(imagenSave.getUrl())
                                    .mascotaId(imagenSave.getMascota() != null ? imagenSave.getMascota().getId() : null)
                                    .build())
                            .build(),
                    HttpStatus.CREATED);
        } catch (DataAccessException exDT) {
            return new ResponseEntity<>(
                    MensajeResponse.builder()
                            .mensaje(exDT.getMessage())
                            .object(null)
                            .build(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (RuntimeException ex) {
            String message = ex.getMessage();
            HttpStatus status;
            if (message.contains("obligatorio") || message.contains("imagen válida") || message.contains("5 MB")) {
                status = HttpStatus.BAD_REQUEST;
            } else if (message.contains("no existe")) {
                status = HttpStatus.NOT_FOUND;
            } else {
                status = HttpStatus.INTERNAL_SERVER_ERROR;
            }
            return new ResponseEntity<>(
                    MensajeResponse.builder()
                            .mensaje(message)
                            .object(null)
                            .build(),
                    status);
        }
    }

    @GetMapping("imagen/{id}")
    public ResponseEntity<?> showById(@PathVariable Long id) {
        try {
            Imagenes imagen = imagenesService.findById(id);
            if (imagen == null) {
                return new ResponseEntity<>(
                        MensajeResponse.builder()
                                .mensaje("El Registro que intenta buscar no existe")
                                .object(null)
                                .build(),
                        HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(
                    MensajeResponse.builder()
                            .mensaje("Consulta Exitosa")
                            .object(ImagenesDto.builder()
                                    .id(imagen.getId())
                                    .url(imagen.getUrl())
                                    .mascotaId(imagen.getMascota() != null ? imagen.getMascota().getId() : null)
                                    .build())
                            .build(),
                    HttpStatus.OK);
        } catch (DataAccessException exDT) {
            return new ResponseEntity<>(
                    MensajeResponse.builder()
                            .mensaje(exDT.getMessage())
                            .object(null)
                            .build(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("imagen/{id}")
    public ResponseEntity<?> update(@RequestBody ImagenesDto imagenesDto, @PathVariable Long id) {
        Imagenes imagenUpdate = null;
        try {
            if (imagenesService.existsById(id)) {
                imagenesDto.setId(id);
                imagenUpdate = imagenesService.save(imagenesDto);
                return new ResponseEntity<>(
                        MensajeResponse.builder()
                                .mensaje("Actualizado Correctamente")
                                .object(ImagenesDto.builder()
                                        .id(imagenUpdate.getId())
                                        .url(imagenUpdate.getUrl())
                                        .mascotaId(imagenUpdate.getMascota() != null ? imagenUpdate.getMascota().getId() : null)
                                        .build())
                                .build(),
                        HttpStatus.OK);
            } else {
                return new ResponseEntity<>(
                        MensajeResponse.builder()
                                .mensaje("El registro que intenta actualizar no se encuentra en la base de datos")
                                .object(null)
                                .build(),
                        HttpStatus.NOT_FOUND);
            }
        } catch (DataAccessException exDT) {
            return new ResponseEntity<>(
                    MensajeResponse.builder()
                            .mensaje(exDT.getMessage())
                            .object(null)
                            .build(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (RuntimeException ex) {
            String message = ex.getMessage();
            if (message.contains("obligatorio")) {
                return new ResponseEntity<>(
                        MensajeResponse.builder()
                                .mensaje(message)
                                .object(null)
                                .build(),
                        HttpStatus.BAD_REQUEST);
            } else if (message.contains("no existe")) {
                return new ResponseEntity<>(
                        MensajeResponse.builder()
                                .mensaje(message)
                                .object(null)
                                .build(),
                        HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(
                    MensajeResponse.builder()
                            .mensaje(message)
                            .object(null)
                            .build(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("imagen/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            Imagenes imagenDelete = imagenesService.findById(id);
            if (imagenDelete == null) {
                return new ResponseEntity<>(
                        MensajeResponse.builder()
                                .mensaje("El registro que intenta eliminar no existe")
                                .object(null)
                                .build(),
                        HttpStatus.NOT_FOUND);
            }
            imagenesService.delete(imagenDelete);
            return new ResponseEntity<>(
                    MensajeResponse.builder()
                            .mensaje("La imagen fue eliminada correctamente")
                            .object(null)
                            .build(),
                    HttpStatus.OK);
        } catch (DataAccessException exDT) {
            return new ResponseEntity<>(
                    MensajeResponse.builder()
                            .mensaje("No se puede eliminar la imagen porque tiene registros asociados")
                            .object(null)
                            .build(),
                    HttpStatus.CONFLICT);
        }
    }
}
