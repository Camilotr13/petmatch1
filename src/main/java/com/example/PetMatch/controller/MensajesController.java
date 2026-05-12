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

import com.example.PetMatch.model.dto.MensajesDto;
import com.example.PetMatch.model.entity.Mensajes;
import com.example.PetMatch.model.payload.MensajeResponse;
import com.example.PetMatch.service.IMensajesService;

@RestController
@RequestMapping("/api/v1")
public class MensajesController {

    @Autowired
    private IMensajesService mensajesService;

    @GetMapping("mensajes")
    public ResponseEntity<?> showAll() {
        try {
            List<Mensajes> getList = mensajesService.listAll();
            if (getList == null || getList.isEmpty()) {
                return new ResponseEntity<>(
                        MensajeResponse.builder()
                                .mensaje("No hay registros")
                                .object(null)
                                .build(),
                        HttpStatus.OK);
            }
            List<MensajesDto> dtoList = getList.stream()
                    .map(mensaje -> MensajesDto.builder()
                            .id(mensaje.getId())
                            .emisorId(mensaje.getEmisor().getId().longValue())
                            .receptorId(mensaje.getReceptor().getId().longValue())
                            .contenido(mensaje.getContenido())
                            .fecha(mensaje.getFecha())
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

    @PostMapping("mensaje")
    public ResponseEntity<?> create(@RequestBody MensajesDto mensajesDto) {
        try {
            Mensajes mensajeSave = mensajesService.save(mensajesDto);
            return new ResponseEntity<>(
                    MensajeResponse.builder()
                            .mensaje("Guardado Correctamente")
                            .object(MensajesDto.builder()
                                    .id(mensajeSave.getId())
                                    .emisorId(mensajeSave.getEmisor().getId().longValue())
                                    .receptorId(mensajeSave.getReceptor().getId().longValue())
                                    .contenido(mensajeSave.getContenido())
                                    .fecha(mensajeSave.getFecha())
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

    @GetMapping("mensaje/{id}")
    public ResponseEntity<?> showById(@PathVariable Long id) {
        try {
            Mensajes mensaje = mensajesService.findById(id);
            if (mensaje == null) {
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
                            .object(MensajesDto.builder()
                                    .id(mensaje.getId())
                                    .emisorId(mensaje.getEmisor().getId().longValue())
                                    .receptorId(mensaje.getReceptor().getId().longValue())
                                    .contenido(mensaje.getContenido())
                                    .fecha(mensaje.getFecha())
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

    @PutMapping("mensaje/{id}")
    public ResponseEntity<?> update(@RequestBody MensajesDto mensajesDto, @PathVariable Long id) {
        try {
            if (mensajesService.existsById(id)) {
                mensajesDto.setId(id);
                Mensajes mensajeUpdate = mensajesService.save(mensajesDto);
                return new ResponseEntity<>(
                        MensajeResponse.builder()
                                .mensaje("Actualizado Correctamente")
                                .object(MensajesDto.builder()
                                        .id(mensajeUpdate.getId())
                                        .emisorId(mensajeUpdate.getEmisor().getId().longValue())
                                        .receptorId(mensajeUpdate.getReceptor().getId().longValue())
                                        .contenido(mensajeUpdate.getContenido())
                                        .fecha(mensajeUpdate.getFecha())
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

    @DeleteMapping("mensaje/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            if (!mensajesService.existsById(id)) {
                return new ResponseEntity<>(
                        MensajeResponse.builder()
                                .mensaje("El registro que intenta eliminar no existe")
                                .object(null)
                                .build(),
                        HttpStatus.NOT_FOUND);
            }
            Mensajes mensajeDelete = mensajesService.findById(id);
            mensajesService.delete(mensajeDelete);
            return new ResponseEntity<>(
                    MensajeResponse.builder()
                            .mensaje("El mensaje fue eliminado correctamente")
                            .object(null)
                            .build(),
                    HttpStatus.OK);
        } catch (DataAccessException exDT) {
            return new ResponseEntity<>(
                    MensajeResponse.builder()
                            .mensaje("No se puede eliminar el mensaje porque tiene registros asociados")
                            .object(null)
                            .build(),
                    HttpStatus.CONFLICT);
        }
    }
}
