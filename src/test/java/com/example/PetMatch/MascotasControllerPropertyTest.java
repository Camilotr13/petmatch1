// Feature: mascotas-module, Property 1: List response structure is always valid
package com.example.PetMatch;

import com.example.PetMatch.controller.MascotasController;
import com.example.PetMatch.model.dto.MascotasDto;
import com.example.PetMatch.model.entity.Mascotas;
import com.example.PetMatch.model.payload.MensajeResponse;
import com.example.PetMatch.service.IMascotasService;
import net.jqwik.api.*;
import net.jqwik.api.lifecycle.BeforeProperty;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Property-based tests for MascotasController.
 * Validates: Requirements 1.1, 1.2
 */
public class MascotasControllerPropertyTest {

    private IMascotasService mascotasService;
    private MascotasController mascotasController;

    @BeforeProperty
    void setUp() {
        mascotasService = org.mockito.Mockito.mock(IMascotasService.class);
        mascotasController = new MascotasController();
        // Inject mock via reflection since @Autowired is used
        try {
            java.lang.reflect.Field field = MascotasController.class.getDeclaredField("mascotasService");
            field.setAccessible(true);
            field.set(mascotasController, mascotasService);
        } catch (Exception e) {
            throw new RuntimeException("Failed to inject mock service", e);
        }
    }

    // Feature: mascotas-module, Property 1: List response structure is always valid
    @Property(tries = 100)
    void listResponseIsAlwaysValid(@ForAll("mascotasLists") List<Mascotas> mascotasList) {
        // Arrange
        when(mascotasService.listAll()).thenReturn(mascotasList);

        // Act
        ResponseEntity<?> response = mascotasController.showAll();

        // Assert: HTTP status is always 200
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        MensajeResponse body = (MensajeResponse) response.getBody();
        assertThat(body).isNotNull();

        if (mascotasList == null || mascotasList.isEmpty()) {
            // When list is null or empty: mensaje = "No hay registros", object = null
            assertThat(body.getMensaje()).isEqualTo("No hay registros");
            assertThat(body.getObject()).isNull();
        } else {
            // When list has elements: object contains the list
            assertThat(body.getObject()).isEqualTo(mascotasList);
        }
    }

    /**
     * Generates arbitrary lists of Mascotas, including:
     * - null (edge case)
     * - empty list (edge case)
     * - lists with 1 to 10 elements
     */
    @Provide
    Arbitrary<List<Mascotas>> mascotasLists() {
        Arbitrary<Mascotas> mascotaArbitrary = Combinators.combine(
                Arbitraries.longs().between(1L, 10000L),
                Arbitraries.strings().alpha().ofMinLength(1).ofMaxLength(50),
                Arbitraries.strings().alpha().ofMinLength(1).ofMaxLength(30),
                Arbitraries.integers().between(0, 30),
                Arbitraries.strings().ofMaxLength(200),
                Arbitraries.of("disponible", "adoptado", "en_proceso")
        ).as((id, nombre, tipo, edad, descripcion, estado) ->
                Mascotas.builder()
                        .id(id)
                        .nombre(nombre)
                        .tipo(tipo)
                        .edad(edad)
                        .descripcion(descripcion)
                        .estado(estado)
                        .build()
        );

        // Non-empty lists (1 to 10 elements)
        Arbitrary<List<Mascotas>> nonEmptyLists = mascotaArbitrary.list().ofMinSize(1).ofMaxSize(10);

        // Empty list edge case
        Arbitrary<List<Mascotas>> emptyList = Arbitraries.just(new ArrayList<>());

        // null edge case
        Arbitrary<List<Mascotas>> nullList = Arbitraries.just(null);

        return Arbitraries.oneOf(nonEmptyLists, emptyList, nullList);
    }

    // Feature: mascotas-module, Property 4: Update overwrites fields and preserves path ID
    // Validates: Requirements 4.1, 4.4
    @Property(tries = 100)
    void updatePreservesPathId(
            @ForAll("pathIds") Long pathId,
            @ForAll("validMascotasDtos") MascotasDto updatePayload) {

        // Arrange: existsById(pathId) returns true
        when(mascotasService.existsById(pathId)).thenReturn(true);

        // Arrange: save() returns a Mascotas entity built from the DTO with id set to the path variable
        Mascotas savedMascota = Mascotas.builder()
                .id(pathId)
                .nombre(updatePayload.getNombre())
                .tipo(updatePayload.getTipo())
                .edad(updatePayload.getEdad())
                .descripcion(updatePayload.getDescripcion())
                .estado(updatePayload.getEstado())
                .usuario(null)
                .created_at(updatePayload.getCreated_at())
                .build();
        when(mascotasService.save(any(MascotasDto.class))).thenReturn(savedMascota);

        // Act
        ResponseEntity<?> response = mascotasController.update(updatePayload, pathId);

        // Assert: HTTP status is 200
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        MensajeResponse body = (MensajeResponse) response.getBody();
        assertThat(body).isNotNull();

        // Assert: object in response is a MascotasDto with id equal to the path variable
        MascotasDto responseDto = (MascotasDto) body.getObject();
        assertThat(responseDto).isNotNull();
        assertThat(responseDto.getId()).isEqualTo(pathId);

        // Assert: nombre, tipo, edad, descripcion, estado equal the update payload fields
        assertThat(responseDto.getNombre()).isEqualTo(updatePayload.getNombre());
        assertThat(responseDto.getTipo()).isEqualTo(updatePayload.getTipo());
        assertThat(responseDto.getEdad()).isEqualTo(updatePayload.getEdad());
        assertThat(responseDto.getDescripcion()).isEqualTo(updatePayload.getDescripcion());
        assertThat(responseDto.getEstado()).isEqualTo(updatePayload.getEstado());
    }

    /**
     * Generates arbitrary path IDs in the range 1–1000.
     */
    @Provide
    Arbitrary<Long> pathIds() {
        return Arbitraries.longs().between(1L, 1000L);
    }

    /**
     * Generates arbitrary valid MascotasDto update payloads.
     * The DTO may include a different id in the body (the path variable should override it).
     */
    @Provide
    Arbitrary<MascotasDto> validMascotasDtos() {
        return Combinators.combine(
                Arbitraries.longs().between(1L, 2000L).optional(),              // id (may differ from path)
                Arbitraries.strings().alpha().ofMinLength(1).ofMaxLength(50),   // nombre
                Arbitraries.strings().alpha().ofMinLength(1).ofMaxLength(30),   // tipo
                Arbitraries.integers().between(0, 30),                           // edad
                Arbitraries.strings().ofMaxLength(200),                          // descripcion
                Arbitraries.of("disponible", "adoptado", "en_proceso")           // estado
        ).as((optId, nombre, tipo, edad, desc, estado) ->
                MascotasDto.builder()
                        .id(optId.orElse(null))
                        .nombre(nombre)
                        .tipo(tipo)
                        .edad(edad)
                        .descripcion(desc)
                        .estado(estado)
                        .usuarioId(1L)
                        .created_at(new Date())
                        .build()
        );
    }
}
