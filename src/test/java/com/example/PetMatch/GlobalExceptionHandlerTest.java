package com.example.PetMatch;

import com.example.PetMatch.controller.MascotasController;
import com.example.PetMatch.service.IMascotasService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for GlobalExceptionHandler.
 * Satisfies: Requirements 3.3, 5.3
 *
 * Uses @WebMvcTest(MascotasController.class) so that the full Spring MVC
 * dispatch pipeline is active, including the @RestControllerAdvice
 * GlobalExceptionHandler.
 */
@WebMvcTest(MascotasController.class)
class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IMascotasService mascotasService;

    // -------------------------------------------------------------------------
    // 11.1 nonNumericIdReturns400
    // Validates: Requirement 3.3
    // When a non-numeric value is supplied as the {id} path variable on GET,
    // Spring raises MethodArgumentTypeMismatchException which the
    // GlobalExceptionHandler maps to 400 with the standard message.
    // -------------------------------------------------------------------------
    @Test
    void nonNumericIdReturns400() throws Exception {
        mockMvc.perform(get("/api/v1/mascota/abc"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje").value("El ID debe ser un número válido"));
    }

    // -------------------------------------------------------------------------
    // 11.2 nonNumericIdDeleteReturns400
    // Validates: Requirement 5.3
    // Same as 11.1 but for the DELETE endpoint.
    // -------------------------------------------------------------------------
    @Test
    void nonNumericIdDeleteReturns400() throws Exception {
        mockMvc.perform(delete("/api/v1/mascota/abc"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje").value("El ID debe ser un número válido"));
    }

    // -------------------------------------------------------------------------
    // 11.3 unknownEndpointReturns404
    // The application.properties already sets:
    //   spring.mvc.throw-exception-if-no-handler-found=true
    //   spring.web.resources.add-mappings=false
    // so Spring raises NoHandlerFoundException for unknown paths, which the
    // GlobalExceptionHandler maps to 404.
    // -------------------------------------------------------------------------
    @Test
    void unknownEndpointReturns404() throws Exception {
        mockMvc.perform(get("/api/v1/nonexistent"))
                .andExpect(status().isNotFound());
    }

    // -------------------------------------------------------------------------
    // 11.4 wrongHttpMethodReturns405
    // GET /api/v1/mascotas is the only mapped method for that path.
    // Sending DELETE raises HttpRequestMethodNotSupportedException which the
    // GlobalExceptionHandler maps to 405.
    // -------------------------------------------------------------------------
    @Test
    void wrongHttpMethodReturns405() throws Exception {
        mockMvc.perform(delete("/api/v1/mascotas"))
                .andExpect(status().isMethodNotAllowed());
    }
}
