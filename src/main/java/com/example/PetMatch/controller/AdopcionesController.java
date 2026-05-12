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
import org.springframework.web.bind.annotation.RestController;

import com.example.PetMatch.model.dto.AdopcionesDto;
import com.example.PetMatch.model.entity.Adopciones;
import com.example.PetMatch.model.payload.MensajeResponse;
import com.example.PetMatch.service.IAdopcionesService;

@RestController
@RequestMapping("/api/v1")
public class AdopcionesController {

    @Autowired
    private IAdopcionesService adopcionesService;

    @GetMapping("adopciones")
    public ResponseEntity<?> showAll() {
        try {
            List<Adopciones> getList = adopcionesService.listAll();
            if (getList == null || getList.isEmpty()) {
                return new ResponseEntity<>(
                        MensajeResponse.builder()
                                .mensaje("No hay registros")
                                .object(null)
                                .build(),
                        HttpStatus.OK);
            }
            List<AdopcionesDto> dtoList = getList.stream()
                    .map(adopcion -> AdopcionesDto.builder()
                            .id(adopcion.getId())
                            .usuarioId(adopcion.getUsuario().getId().longValue())
                            .mascotaId(adopcion.getMascota().getId())
                            .estado(adopcion.getEstado())
                            .fecha(adopcion.getFecha())
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

    @PostMapping("adopcion")
    public ResponseEntity<?> create(@RequestBody AdopcionesDto adopcionesDto) {
        Adopciones adopcionSave = null;
        try {
            adopcionSave = adopcionesService.save(adopcionesDto);
            return new ResponseEntity<>(
                    MensajeResponse.builder()
                            .mensaje("Guardado Correctamente")
                            .object(AdopcionesDto.builder()
                                    .id(adopcionSave.getId())
                                    .usuarioId(adopcionSave.getUsuario().getId().longValue())
                                    .mascotaId(adopcionSave.getMascota().getId())
                                    .estado(adopcionSave.getEstado())
                                    .fecha(adopcionSave.getFecha())
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
            } else if (message.contains("ya ha sido adoptada")) {
                return new ResponseEntity<>(
                        MensajeResponse.builder()
                                .mensaje(message)
                                .object(null)
                                .build(),
                        HttpStatus.CONFLICT);
            }
            return new ResponseEntity<>(
                    MensajeResponse.builder()
                            .mensaje(message)
                            .object(null)
                            .build(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("adopcion/{id}")
    public ResponseEntity<?> showById(@PathVariable Long id) {
        try {
            Adopciones adopcion = adopcionesService.findById(id);
            if (adopcion == null) {
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
                            .object(AdopcionesDto.builder()
                                    .id(adopcion.getId())
                                    .usuarioId(adopcion.getUsuario().getId().longValue())
                                    .mascotaId(adopcion.getMascota().getId())
                                    .estado(adopcion.getEstado())
                                    .fecha(adopcion.getFecha())
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

    @PutMapping("adopcion/{id}")
    public ResponseEntity<?> update(@RequestBody AdopcionesDto adopcionesDto, @PathVariable Long id) {
        Adopciones adopcionUpdate = null;
        try {
            if (adopcionesService.existsById(id)) {
                adopcionesDto.setId(id);
                adopcionUpdate = adopcionesService.save(adopcionesDto);
                return new ResponseEntity<>(
                        MensajeResponse.builder()
                                .mensaje("Actualizado Correctamente")
                                .object(AdopcionesDto.builder()
                                        .id(adopcionUpdate.getId())
                                        .usuarioId(adopcionUpdate.getUsuario().getId().longValue())
                                        .mascotaId(adopcionUpdate.getMascota().getId())
                                        .estado(adopcionUpdate.getEstado())
                                        .fecha(adopcionUpdate.getFecha())
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
            } else if (message.contains("ya ha sido adoptada")) {
                return new ResponseEntity<>(
                        MensajeResponse.builder()
                                .mensaje(message)
                                .object(null)
                                .build(),
                        HttpStatus.CONFLICT);
            }
            return new ResponseEntity<>(
                    MensajeResponse.builder()
                            .mensaje(message)
                            .object(null)
                            .build(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("adopcion/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            if (!adopcionesService.existsById(id)) {
                return new ResponseEntity<>(
                        MensajeResponse.builder()
                                .mensaje("El registro que intenta eliminar no existe")
                                .object(null)
                                .build(),
                        HttpStatus.NOT_FOUND);
            }
            Adopciones adopcionDelete = adopcionesService.findById(id);
            adopcionesService.delete(adopcionDelete);
            return new ResponseEntity<>(
                    MensajeResponse.builder()
                            .mensaje("La adopción fue eliminada correctamente")
                            .object(null)
                            .build(),
                    HttpStatus.OK);
        } catch (DataAccessException exDT) {
            return new ResponseEntity<>(
                    MensajeResponse.builder()
                            .mensaje("No se puede eliminar la adopción porque tiene registros asociados")
                            .object(null)
                            .build(),
                    HttpStatus.CONFLICT);
        }
    }
}
