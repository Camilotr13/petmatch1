package com.example.PetMatch.controller;


import com.example.PetMatch.model.dao.UsuariosDao;
import com.example.PetMatch.model.dto.UsuariosDto;
import com.example.PetMatch.model.entity.Usuarios;
import com.example.PetMatch.model.payload.MensajeResponse;
import com.example.PetMatch.service.IUsuariosService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class UsuariosController {

    @Autowired
    private IUsuariosService usuariosService;
    @Autowired
    private UsuariosDao usuariosDao;


    @GetMapping("usuarios")
    public ResponseEntity<?> showAll(){

        List<Usuarios> getList = usuariosService.listAll();
        if (getList == null) {
            return new ResponseEntity<>(
                    MensajeResponse.builder()
                            .mensaje("No hay registros")
                            .object(null)
                            .build()
                    ,HttpStatus.OK);
        }

        return new ResponseEntity<>(
                MensajeResponse.builder()
                        .mensaje("")
                        .object(getList)
                        .build()
                ,HttpStatus.OK);
    }


    @PostMapping("usuario")
    public ResponseEntity<?> create(@RequestBody UsuariosDto usuariosDto) {
        Usuarios  usuariosSave = null;

        try {
             usuariosSave = usuariosService.save(usuariosDto);
            return new ResponseEntity<>(MensajeResponse.builder()
                    .mensaje("Guardado Correctamente")
                    .object(UsuariosDto.builder()
                        .id(usuariosSave.getId())
                        .nombre(usuariosSave.getNombre())
                        .email(usuariosSave.getEmail())
                        .password(usuariosSave.getPassword())
                        .rol(usuariosSave.getRol())
                        .created_at(usuariosSave.getCreated_at())
                        .build())
            .build()
            , HttpStatus.CREATED);
        }catch (DataAccessException exDT) {
            return new ResponseEntity<>(
                    MensajeResponse.builder()
                            .mensaje(exDT.getMessage())
                            .object(null)
                            .build()
                        , HttpStatus.METHOD_NOT_ALLOWED);
        }
    }


    @PutMapping("usuario/{id}")
    public ResponseEntity<?> update(@RequestBody UsuariosDto usuariosDto, @PathVariable Integer id){
        Usuarios usuariosUpdate = null;

        try {

            if (usuariosService.existsById(id)) {
                usuariosDto.setId(id);
                usuariosUpdate = usuariosService.save(usuariosDto);
                return new ResponseEntity<>(MensajeResponse.builder()
                        .mensaje("Guardado Correctamente")
                        .object( UsuariosDto.builder()
                                .id(usuariosUpdate.getId())
                                .nombre(usuariosUpdate.getNombre())
                                .email(usuariosUpdate.getEmail())
                                .password(usuariosUpdate.getPassword())
                                .rol(usuariosUpdate.getRol())
                                .created_at(usuariosUpdate.getCreated_at())
                                .build())
                        .build()
                        , HttpStatus.CREATED);

            }else {
                return new ResponseEntity<>(
                        MensajeResponse.builder()
                                .mensaje("El registro que intenta actualizar no se encuantra en la base de datos")
                                .object(null)
                                .build()
                        , HttpStatus.NOT_FOUND);
            }

        }catch (DataAccessException exDT) {
            return new ResponseEntity<>(
                    MensajeResponse.builder()
                            .mensaje(exDT.getMessage())
                            .object(null)
                            .build()
                        , HttpStatus.METHOD_NOT_ALLOWED);
        }
    }


    @DeleteMapping("usuario/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id){

        Usuarios usuariosDelete = usuariosService.findById(id);

        if (usuariosDelete == null) {
            return new ResponseEntity<>(
                    MensajeResponse.builder()
                            .mensaje("El registro que intenta eliminar no existe")
                            .object(null)
                            .build(),
                    HttpStatus.NOT_FOUND
            );
        }

        usuariosService.delete(usuariosDelete);

        return new ResponseEntity<>(
                MensajeResponse.builder()
                        .mensaje("El usuario fue eliminado correctamente")
                        .object(null)
                        .build(),
                HttpStatus.OK
        );
    }

    @GetMapping("usuario/{id}")
    public ResponseEntity<?> showById(@PathVariable Integer id){
        Usuarios usuario =  usuariosService.findById(id);
        if (usuario == null) {
            return new ResponseEntity<>(
                    MensajeResponse.builder()
                            .mensaje("El Registro que intenta buscar no existe")
                            .object(null)
                            .build()
                    ,HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(
                MensajeResponse.builder()
                        .mensaje("Consulta Exitosa")
                        .object(UsuariosDto.builder()
                                .id(usuario.getId())
                                .nombre(usuario.getNombre())
                                .email(usuario.getEmail())
                                .password(usuario.getPassword())
                                .rol(usuario.getRol())
                                .created_at(usuario.getCreated_at())
                                .build())
                        .build()
                ,HttpStatus.OK);
    }
}
