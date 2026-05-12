package com.example.PetMatch.model.dto;

import java.io.Serializable;
import java.util.Date;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@Builder
public class ImagenesDto implements Serializable {

    private Long id;
    private String url;
    private Long mascotaId;
}
