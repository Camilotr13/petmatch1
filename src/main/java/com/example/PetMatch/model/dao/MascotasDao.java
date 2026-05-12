package com.example.PetMatch.model.dao;

import org.springframework.data.repository.CrudRepository;

import com.example.PetMatch.model.entity.Mascotas;

public interface MascotasDao extends CrudRepository<Mascotas, Long> {
}
