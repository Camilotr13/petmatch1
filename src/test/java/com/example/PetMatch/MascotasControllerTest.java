package com.example.PetMatch;

import com.example.PetMatch.controller.MascotasController;
import com.example.PetMatch.model.dto.MascotasDto;
import com.example.PetMatch.model.entity.Mascotas;
import com.example.PetMatch.service.IMascotasService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataAccessException;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for MascotasController — error and edge cases.
 * Satisfies: Requirements 1.2, 1.4, 2.2, 3.2, 3.4, 4.2, 4.3, 5.2, 5.4
 */
@WebMvcTest(MascotasController.class)
class MascotasControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IMascotasService mascotasService;

    @Autowired
    private ObjectMapper objectMapper;

    // -------------------------------------------------------------------------
    // Helper: a minimal valid MascotasDto JSON body
    // -------------------------------------------------------------------------
    private String minimalMascotaDtoJson() throws Exception {
        MascotasDto dto = MascotasDto.builder()
                .nombre("Firulais")
                .tipo("Perro")
                .edad(3)
                .descripcion("Labrador amigable")
                .estado("disponible")
                .usuarioId(1L)
                .build();
        return objectMapper.writeValueAsString(dto);
    }

    // -------------------------------------------------------------------------
    // Helper: a DataAccessException anonymous subclass (abstract class)
    // -------------------------------------------------------------------------
    private DataAccessException dbError() {
        return new DataAccessException("DB error") {};
    }

    // -------------------------------------------------------------------------
    // 10.1 createThrowsDataAccessException
    // Validates: Requirement 2.2
    // -------------------------------------------------------------------------
    @Test
    void createThrowsDataAccessException() throws Exception {
        when(mascotasService.save(any(MascotasDto.class))).thenThrow(dbError());

        mockMvc.perform(post("/api/v1/mascota")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(minimalMascotaDtoJson()))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.mensaje").value(notNullValue()));
    }

    // -------------------------------------------------------------------------
    // 10.2 updateThrowsDataAccessException
    // Validates: Requirement 4.3
    // -------------------------------------------------------------------------
    @Test
    void updateThrowsDataAccessException() throws Exception {
        when(mascotasService.existsById(1L)).thenReturn(true);
        when(mascotasService.save(any(MascotasDto.class))).thenThrow(dbError());

        mockMvc.perform(put("/api/v1/mascota/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(minimalMascotaDtoJson()))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.mensaje").value(notNullValue()));
    }

    // -------------------------------------------------------------------------
    // 10.3 deleteThrowsDataAccessException
    // Validates: Requirement 5.4
    // -------------------------------------------------------------------------
    @Test
    void deleteThrowsDataAccessException() throws Exception {
        Mascotas existingMascota = Mascotas.builder()
                .id(1L)
                .nombre("Firulais")
                .tipo("Perro")
                .edad(3)
                .descripcion("Labrador amigable")
                .estado("disponible")
                .build();
        when(mascotasService.findById(1L)).thenReturn(existingMascota);
        doThrow(dbError()).when(mascotasService).delete(any(Mascotas.class));

        mockMvc.perform(delete("/api/v1/mascota/1"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.mensaje").value(notNullValue()));
    }

    // -------------------------------------------------------------------------
    // 10.4 listThrowsDataAccessException
    // Validates: Requirement 1.4
    // -------------------------------------------------------------------------
    @Test
    void listThrowsDataAccessException() throws Exception {
        when(mascotasService.listAll()).thenThrow(dbError());

        mockMvc.perform(get("/api/v1/mascotas"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.mensaje").value(notNullValue()));
    }

    // -------------------------------------------------------------------------
    // 10.5 listAllEmptyReturnsNoRegistros
    // Validates: Requirement 1.2
    // -------------------------------------------------------------------------
    @Test
    void listAllEmptyReturnsNoRegistros() throws Exception {
        when(mascotasService.listAll()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/v1/mascotas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensaje").value("No hay registros"));
    }

    // -------------------------------------------------------------------------
    // 10.6 findByIdNotFoundReturns404
    // Validates: Requirement 3.2
    // -------------------------------------------------------------------------
    @Test
    void findByIdNotFoundReturns404() throws Exception {
        when(mascotasService.findById(999L)).thenReturn(null);

        mockMvc.perform(get("/api/v1/mascota/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensaje").value("El Registro que intenta buscar no existe"));
    }

    // -------------------------------------------------------------------------
    // 10.7 deleteNotFoundReturns404
    // Validates: Requirement 5.2
    // -------------------------------------------------------------------------
    @Test
    void deleteNotFoundReturns404() throws Exception {
        when(mascotasService.findById(999L)).thenReturn(null);

        mockMvc.perform(delete("/api/v1/mascota/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensaje").value("El registro que intenta eliminar no existe"));
    }

    // -------------------------------------------------------------------------
    // 10.8 updateNotFoundReturns404
    // Validates: Requirement 4.2
    // -------------------------------------------------------------------------
    @Test
    void updateNotFoundReturns404() throws Exception {
        when(mascotasService.existsById(999L)).thenReturn(false);

        mockMvc.perform(put("/api/v1/mascota/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(minimalMascotaDtoJson()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensaje").value("El registro que intenta actualizar no se encuentra en la base de datos"));
    }
}
