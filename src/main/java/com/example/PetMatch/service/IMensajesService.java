package com.example.PetMatch.service;

import java.util.List;

import com.example.PetMatch.model.dto.MensajesDto;
import com.example.PetMatch.model.entity.Mensajes;

public interface IMensajesService {

    List<Mensajes> listAll();

    Mensajes save(MensajesDto mensajesDto);

    Mensajes findById(Long id);

    void delete(Mensajes mensajes);

    boolean existsById(Long id);
}
