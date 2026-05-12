package com.example.PetMatch.model.dao;

import org.springframework.data.repository.CrudRepository;

import com.example.PetMatch.model.entity.Adopciones;

public interface AdopcionesDao extends CrudRepository<Adopciones, Long> {
}
