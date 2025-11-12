package ttps.proyecto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import ttps.proyecto.config.AppConfig;
import ttps.proyecto.config.WebConfig;
import ttps.proyecto.dto.MascotaDto;
import ttps.proyecto.dto.UbicacionDto;
import ttps.proyecto.models.enums.EstadoMascota;

import org.junit.jupiter.api.extension.ExtendWith;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { AppConfig.class, WebConfig.class })
@WebAppConfiguration
@Transactional
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class MascotaIntegrationTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    @Order(1)
    @DisplayName("POST /api/mascotas - Crear mascota exitosamente")
    void deberiaCrearNuevaMascota() throws Exception {
        // Given
        MascotaDto mascotaDto = new MascotaDto();
        mascotaDto.setNombre("Rex");
        mascotaDto.setColor("Negro");
        mascotaDto.setDescripcion("Pastor alemán muy amigable");
        mascotaDto.setEstado(EstadoMascota.PERDIDO_PROPIO);
        mascotaDto.setTamanioNombre("GRANDE");
        
        UbicacionDto ubicacion = new UbicacionDto();
        ubicacion.setBarrio("Tolosa");
        ubicacion.setLatitud(-34.8990);
        ubicacion.setLongitud(-57.9700);
        mascotaDto.setUbicacion(ubicacion);

        // When & Then
        mockMvc.perform(post("/api/mascotas")
                .param("usuarioId", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(mascotaDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombre").value("Rex"))
                .andExpect(jsonPath("$.color").value("Negro"))
                .andExpect(jsonPath("$.estado").value("PERDIDO_PROPIO"))
                .andExpect(jsonPath("$.tamanioNombre").value("GRANDE"))
                .andExpect(jsonPath("$.ubicacion.barrio").value("Tolosa"));
    }

    @Test
    @Order(2)
    @DisplayName("POST /api/mascotas - Usuario no existe")
    void noDeberiaCrearMascotaConUsuarioInexistente() throws Exception {
        // Given
        MascotaDto mascotaDto = new MascotaDto();
        mascotaDto.setNombre("Rex");
        mascotaDto.setColor("Negro");
        mascotaDto.setEstado(EstadoMascota.PERDIDO_PROPIO);
        mascotaDto.setTamanioNombre("GRANDE");

        // When & Then
        mockMvc.perform(post("/api/mascotas")
                .param("usuarioId", "999") // Usuario no existe
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(mascotaDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(3)
    @DisplayName("GET /api/mascotas/{id} - Obtener mascota existente")
    void deberiaObtenerMascotaPorId() throws Exception {
        mockMvc.perform(get("/api/mascotas/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Fido"))
                .andExpect(jsonPath("$.color").value("Marrón"))
                .andExpect(jsonPath("$.estado").value("PERDIDO_PROPIO"))
                .andExpect(jsonPath("$.publicadorId").value(1))
                .andExpect(jsonPath("$.publicadorNombre").value("Juan Pérez"));
    }

    @Test
    @Order(4)
    @DisplayName("GET /api/mascotas/perdidas - Listar mascotas perdidas")
    void deberiaListarMascotasPerdidas() throws Exception {
        mockMvc.perform(get("/api/mascotas/perdidas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(2))))
                .andExpect(jsonPath("$[0].estado", anyOf(
                    is("PERDIDO_PROPIO"),
                    is("PERDIDO_AJENO")
                )));
    }

    @Test
    @Order(5)
    @DisplayName("GET /api/mascotas/usuario/{id} - Listar mascotas de un usuario")
    void deberiaListarMascotasDelUsuario() throws Exception {
        mockMvc.perform(get("/api/mascotas/usuario/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[0].publicadorId").value(1))
                .andExpect(jsonPath("$[0].publicadorNombre").value("Juan Pérez"));
    }

    @Test
    @Order(6)
    @DisplayName("PUT /api/mascotas/{id} - Actualizar mascota")
    void deberiaActualizarMascota() throws Exception {
        // Given
        MascotaDto updateDto = new MascotaDto();
        updateDto.setNombre("Fido");
        updateDto.setColor("Marrón claro");
        updateDto.setDescripcion("Perro labrador con collar rojo");
        updateDto.setEstado(EstadoMascota.RECUPERADO);
        updateDto.setTamanioNombre("GRANDE");

        // When & Then
        mockMvc.perform(put("/api/mascotas/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.color").value("Marrón claro"))
                .andExpect(jsonPath("$.estado").value("RECUPERADO"))
                .andExpect(jsonPath("$.descripcion").value(containsString("collar rojo")));
    }

    @Test
    @Order(7)
    @DisplayName("DELETE /api/mascotas/{id} - Eliminar mascota")
    void deberiaEliminarMascota() throws Exception {
        mockMvc.perform(delete("/api/mascotas/1"))
                .andExpect(status().isNoContent());

        // Verificar que ya no existe
        mockMvc.perform(get("/api/mascotas/1"))
                .andExpect(status().isNotFound());
    }
}