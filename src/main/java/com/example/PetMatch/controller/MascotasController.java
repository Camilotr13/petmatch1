package com.example.PetMatch.controller;

import java.util.List;

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

import com.example.PetMatch.model.dto.MascotasDto;
import com.example.PetMatch.model.entity.Mascotas;
import com.example.PetMatch.model.payload.MensajeResponse;
import com.example.PetMatch.service.IMascotasService;

@RestController
@RequestMapping("/api/v1")
public class MascotasController {

    @Autowired
    private IMascotasService mascotasService;

    @GetMapping("mascotas")
    public ResponseEntity<?> showAll() {
        try {
            List<Mascotas> getList = mascotasService.listAll();
            if (getList == null || getList.isEmpty()) {
                return new ResponseEntity<>(
                        MensajeResponse.builder()
                                .mensaje("No hay registros")
                                .object(null)
                                .build(),
                        HttpStatus.OK);
            }
            return new ResponseEntity<>(
                    MensajeResponse.builder()
                            .mensaje("")
                            .object(getList)
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

    @PostMapping("mascota")
    public ResponseEntity<?> create(@RequestBody MascotasDto mascotasDto) {
        Mascotas mascotaSave = null;
        try {
            mascotaSave = mascotasService.save(mascotasDto);
            return new ResponseEntity<>(
                    MensajeResponse.builder()
                            .mensaje("Guardado Correctamente")
                            .object(MascotasDto.builder()
                                    .id(mascotaSave.getId())
                                    .nombre(mascotaSave.getNombre())
                                    .tipo(mascotaSave.getTipo())
                                    .edad(mascotaSave.getEdad())
                                    .descripcion(mascotaSave.getDescripcion())
                                    .estado(mascotaSave.getEstado())
                                    .usuarioId(mascotaSave.getUsuario() != null ? mascotaSave.getUsuario().getId().longValue() : null)
                                    .created_at(mascotaSave.getCreated_at())
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
        }
    }

    @PutMapping("mascota/{id}")
    public ResponseEntity<?> update(@RequestBody MascotasDto mascotasDto, @PathVariable Long id) {
        Mascotas mascotaUpdate = null;
        try {
            if (mascotasService.existsById(id)) {
                mascotasDto.setId(id);
                mascotaUpdate = mascotasService.save(mascotasDto);
                return new ResponseEntity<>(
                        MensajeResponse.builder()
                                .mensaje("Guardado Correctamente")
                                .object(MascotasDto.builder()
                                        .id(mascotaUpdate.getId())
                                        .nombre(mascotaUpdate.getNombre())
                                        .tipo(mascotaUpdate.getTipo())
                                        .edad(mascotaUpdate.getEdad())
                                        .descripcion(mascotaUpdate.getDescripcion())
                                        .estado(mascotaUpdate.getEstado())
                                        .usuarioId(mascotaUpdate.getUsuario() != null ? mascotaUpdate.getUsuario().getId().longValue() : null)
                                        .created_at(mascotaUpdate.getCreated_at())
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
        }
    }

    @DeleteMapping("mascota/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            Mascotas mascotaDelete = mascotasService.findById(id);
            if (mascotaDelete == null) {
                return new ResponseEntity<>(
                        MensajeResponse.builder()
                                .mensaje("El registro que intenta eliminar no existe")
                                .object(null)
                                .build(),
                        HttpStatus.NOT_FOUND);
            }
            mascotasService.delete(mascotaDelete);
            return new ResponseEntity<>(
                    MensajeResponse.builder()
                            .mensaje("La mascota fue eliminada correctamente")
                            .object(null)
                            .build(),
                    HttpStatus.OK);
        } catch (DataAccessException exDT) {
            return new ResponseEntity<>(
                    MensajeResponse.builder()
                            .mensaje("No se puede eliminar la mascota porque tiene registros asociados")
                            .object(null)
                            .build(),
                    HttpStatus.CONFLICT);
        }
    }

    @GetMapping("mascota/{id}")
    public ResponseEntity<?> showById(@PathVariable Long id) {
        Mascotas mascota = mascotasService.findById(id);
        if (mascota == null) {
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
                        .object(MascotasDto.builder()
                                .id(mascota.getId())
                                .nombre(mascota.getNombre())
                                .tipo(mascota.getTipo())
                                .edad(mascota.getEdad())
                                .descripcion(mascota.getDescripcion())
                                .estado(mascota.getEstado())
                                .usuarioId(mascota.getUsuario() != null ? mascota.getUsuario().getId().longValue() : null)
                                .created_at(mascota.getCreated_at())
                                .build())
                        .build(),
                HttpStatus.OK);
    }
}
