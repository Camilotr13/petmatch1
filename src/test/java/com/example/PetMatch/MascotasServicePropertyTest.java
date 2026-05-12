// Feature: mascotas-module, Property 2: Create round-trip preserves all DTO fields
package com.example.PetMatch;

import com.example.PetMatch.model.dao.MascotasDao;
import com.example.PetMatch.model.dao.UsuariosDao;
import com.example.PetMatch.model.dto.MascotasDto;
import com.example.PetMatch.model.entity.Mascotas;
import com.example.PetMatch.model.entity.Usuarios;
import com.example.PetMatch.service.impl.MascotasServiceImpl;
import net.jqwik.api.*;
import net.jqwik.api.lifecycle.BeforeProperty;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Property-based tests for MascotasServiceImpl.
 * Validates: Requirements 2.1, 2.3, 2.4
 */
public class MascotasServicePropertyTest {

    private MascotasDao mascotasDao;
    private UsuariosDao usuariosDao;
    private MascotasServiceImpl mascotasService;

    private static final AtomicLong idGenerator = new AtomicLong(1L);

    @BeforeProperty
    void setUp() {
        mascotasDao = mock(MascotasDao.class);
        usuariosDao = mock(UsuariosDao.class);
        mascotasService = new MascotasServiceImpl();

        // Inject mocks via reflection since @Autowired is used
        try {
            java.lang.reflect.Field mascotasDaoField = MascotasServiceImpl.class.getDeclaredField("mascotasDao");
            mascotasDaoField.setAccessible(true);
            mascotasDaoField.set(mascotasService, mascotasDao);

            java.lang.reflect.Field usuariosDaoField = MascotasServiceImpl.class.getDeclaredField("usuariosDao");
            usuariosDaoField.setAccessible(true);
            usuariosDaoField.set(mascotasService, usuariosDao);
        } catch (Exception e) {
            throw new RuntimeException("Failed to inject mock DAOs", e);
        }
    }

    // Feature: mascotas-module, Property 2: Create round-trip preserves all DTO fields
    // Validates: Requirements 2.1, 2.3, 2.4
    @Property(tries = 100)
    void createRoundTripPreservesFields(@ForAll("validMascotasDtos") MascotasDto dto) {
        // Arrange: mock UsuariosDao to return a valid Usuarios entity
        Usuarios mockUsuario = Usuarios.builder()
                .id(dto.getUsuarioId().intValue())
                .nombre("TestUser")
                .email("test@test.com")
                .build();
        when(usuariosDao.findById(anyInt())).thenReturn(Optional.of(mockUsuario));

        // Arrange: mock MascotasDao.save() to return a Mascotas entity built from the DTO with a generated non-null id
        long generatedId = idGenerator.getAndIncrement();
        Mascotas savedMascota = Mascotas.builder()
                .id(generatedId)
                .nombre(dto.getNombre())
                .tipo(dto.getTipo())
                .edad(dto.getEdad())
                .descripcion(dto.getDescripcion())
                .estado(dto.getEstado())
                .usuario(mockUsuario)
                .created_at(dto.getCreated_at())
                .build();
        when(mascotasDao.save(any(Mascotas.class))).thenReturn(savedMascota);

        // Act
        Mascotas result = mascotasService.save(dto);

        // Assert: returned entity has all fields matching the input DTO
        assertThat(result.getId()).isNotNull();
        assertThat(result.getNombre()).isEqualTo(dto.getNombre());
        assertThat(result.getTipo()).isEqualTo(dto.getTipo());
        assertThat(result.getEdad()).isEqualTo(dto.getEdad());
        assertThat(result.getDescripcion()).isEqualTo(dto.getDescripcion());
        assertThat(result.getEstado()).isEqualTo(dto.getEstado());
        // usuarioId: compare via mascota.getUsuario().getId() converted to Long
        assertThat(result.getUsuario().getId().longValue()).isEqualTo(dto.getUsuarioId());
    }

    // Feature: mascotas-module, Property 3: FindById round-trip returns the saved mascota
    // Validates: Requirements 3.1, 3.5
    @Property(tries = 100)
    void findByIdRoundTrip(@ForAll("validMascotasDtos") MascotasDto dto) {
        // Arrange: build the Mascotas entity that would have been saved from this DTO
        Usuarios mockUsuario = Usuarios.builder()
                .id(dto.getUsuarioId().intValue())
                .nombre("TestUser")
                .email("test@test.com")
                .build();

        long generatedId = idGenerator.getAndIncrement();
        Mascotas savedMascota = Mascotas.builder()
                .id(generatedId)
                .nombre(dto.getNombre())
                .tipo(dto.getTipo())
                .edad(dto.getEdad())
                .descripcion(dto.getDescripcion())
                .estado(dto.getEstado())
                .usuario(mockUsuario)
                .created_at(dto.getCreated_at())
                .build();

        // Arrange: mock MascotasDao.findById() to return the saved entity
        when(mascotasDao.findById(generatedId)).thenReturn(Optional.of(savedMascota));

        // Act
        Mascotas result = mascotasService.findById(generatedId);

        // Assert: returned entity fields match the original DTO fields
        assertThat(result).isNotNull();
        assertThat(result.getNombre()).isEqualTo(dto.getNombre());
        assertThat(result.getTipo()).isEqualTo(dto.getTipo());
        assertThat(result.getEdad()).isEqualTo(dto.getEdad());
        assertThat(result.getDescripcion()).isEqualTo(dto.getDescripcion());
        assertThat(result.getEstado()).isEqualTo(dto.getEstado());
        assertThat(result.getUsuario().getId().longValue()).isEqualTo(dto.getUsuarioId());
    }

    // Feature: mascotas-module, Property 5: Delete removes the mascota from the system
    // Validates: Requirements 5.1, 5.5
    @Property(tries = 100)
    void deleteRemovesEntity(@ForAll("validMascotas") Mascotas mascota) {
        // Arrange: in-memory store with the mascota pre-populated
        Map<Long, Mascotas> store = new HashMap<>();
        store.put(mascota.getId(), mascota);

        // Mock findById to look up from the in-memory store
        when(mascotasDao.findById(anyLong())).thenAnswer(invocation -> {
            Long id = invocation.getArgument(0);
            return Optional.ofNullable(store.get(id));
        });

        // Mock delete to remove from the in-memory store
        doAnswer(invocation -> {
            Mascotas m = invocation.getArgument(0);
            store.remove(m.getId());
            return null;
        }).when(mascotasDao).delete(any(Mascotas.class));

        // Act: delete the mascota
        mascotasService.delete(mascota);

        // Assert: findById now returns null (entity no longer in the system)
        Mascotas result = mascotasService.findById(mascota.getId());
        assertThat(result).isNull();
    }

    @Provide
    Arbitrary<MascotasDto> validMascotasDtos() {
        return Combinators.combine(
                Arbitraries.strings().alpha().ofMinLength(1).ofMaxLength(50),  // nombre
                Arbitraries.strings().alpha().ofMinLength(1).ofMaxLength(30),  // tipo
                Arbitraries.integers().between(0, 30),                          // edad
                Arbitraries.strings().ofMaxLength(200),                         // descripcion
                Arbitraries.of("disponible", "adoptado", "en_proceso"),         // estado
                Arbitraries.longs().between(1L, 1000L)                          // usuarioId
        ).as((nombre, tipo, edad, desc, estado, uid) ->
                MascotasDto.builder()
                        .nombre(nombre).tipo(tipo).edad(edad)
                        .descripcion(desc).estado(estado).usuarioId(uid)
                        .created_at(new Date())
                        .build()
        );
    }

    @Provide
    Arbitrary<Mascotas> validMascotas() {
        return Combinators.combine(
                Arbitraries.longs().between(1L, 1000L),                         // id
                Arbitraries.strings().alpha().ofMinLength(1).ofMaxLength(50),   // nombre
                Arbitraries.strings().alpha().ofMinLength(1).ofMaxLength(30),   // tipo
                Arbitraries.integers().between(0, 30),                           // edad
                Arbitraries.strings().ofMaxLength(200),                          // descripcion
                Arbitraries.of("disponible", "adoptado", "en_proceso")           // estado
        ).as((id, nombre, tipo, edad, desc, estado) ->
                Mascotas.builder()
                        .id(id)
                        .nombre(nombre)
                        .tipo(tipo)
                        .edad(edad)
                        .descripcion(desc)
                        .estado(estado)
                        .created_at(new Date())
                        .build()
        );
    }
}
