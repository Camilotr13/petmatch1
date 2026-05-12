package com.example.PetMatch.model.dto;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.Date;


@Data
@ToString
@Builder


public class UsuariosDto implements Serializable {


    private Integer id;
    private String nombre;
    private String email;
    private String password;
    private String rol;
    private Date created_at;
}




