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
public class MensajesDto implements Serializable {

    private Long id;
    private Long emisorId;
    private Long receptorId;
    private String contenido;
    private Timestamp fecha;
}
