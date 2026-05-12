package com.example.PetMatch.model.dto;

import java.io.Serializable;
import java.util.Date;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@Builder
public class MascotasDto implements Serializable {

    private Long id;
    private String nombre;
    private String tipo;
    private Integer edad;
    private String descripcion;
    private String estado;
    private Long usuarioId;
    private Date created_at;
}
