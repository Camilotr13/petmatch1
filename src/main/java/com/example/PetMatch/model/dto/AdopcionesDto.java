package com.example.PetMatch.model.dto;

import java.io.Serializable;
import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdopcionesDto implements Serializable {

    private Long id;
    private Long usuarioId;
    private Long mascotaId;
    private String estado;
    private Timestamp fecha;
}
