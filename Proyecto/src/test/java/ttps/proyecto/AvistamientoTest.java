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
import ttps.proyecto.dto.AvistamientoDto;
import ttps.proyecto.dto.UbicacionDto;

import org.junit.jupiter.api.extension.ExtendWith;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { AppConfig.class, WebConfig.class })
@WebAppConfiguration
@Transactional
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AvistamientoTest {

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
    @DisplayName("POST /api/avistamientos - Crear avistamiento exitosamente")
    void deberiaCrearNuevoAvistamiento() throws Exception {
        // Given
        AvistamientoDto avistamientoDto = new AvistamientoDto();
        avistamientoDto.setMascotaId(1L); // Fido (mascota perdida)
        avistamientoDto.setComentario("Vi a este perro cerca del parque Saavedra");
        avistamientoDto.setFecha(LocalDate.now());
        
        UbicacionDto ubicacion = new UbicacionDto();
        ubicacion.setBarrio("Centro");
        ubicacion.setLatitud(-34.9215);
        ubicacion.setLongitud(-57.9555);
        avistamientoDto.setUbicacion(ubicacion);

        // When & Then
        mockMvc.perform(post("/api/avistamientos")
                .param("reportadorId", "2") // María reporta
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(avistamientoDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.comentario").value("Vi a este perro cerca del parque Saavedra"))
                .andExpect(jsonPath("$.mascotaId").value(1))
                .andExpect(jsonPath("$.mascotaNombre").value("Fido"))
                .andExpect(jsonPath("$.reportadorId").value(2))
                .andExpect(jsonPath("$.reportadorNombre").value("María González"))
                .andExpect(jsonPath("$.ubicacion.barrio").value("Centro"))
                .andExpect(jsonPath("$.ubicacion.latitud").value(-34.9215))
                .andExpect(jsonPath("$.ubicacion.longitud").value(-57.9555));
    }

    @Test
    @Order(2)
    @DisplayName("POST /api/avistamientos - Crear avistamiento sin ubicación")
    void deberiaCrearAvistamientoSinUbicacion() throws Exception {
        // Given
        AvistamientoDto avistamientoDto = new AvistamientoDto();
        avistamientoDto.setMascotaId(1L);
        avistamientoDto.setComentario("Lo vi pero no sé exactamente dónde");
        avistamientoDto.setFecha(LocalDate.now());
        // Sin ubicación

        // When & Then
        mockMvc.perform(post("/api/avistamientos")
                .param("reportadorId", "3") // Carlos reporta
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(avistamientoDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.comentario").value("Lo vi pero no sé exactamente dónde"))
                .andExpect(jsonPath("$.mascotaNombre").value("Fido"))
                .andExpect(jsonPath("$.reportadorNombre").value("Carlos López"));
    }

    @Test
    @Order(3)
    @DisplayName("POST /api/avistamientos - Reportador no existe")
    void noDeberiaCrearAvistamientoConReportadorInexistente() throws Exception {
        // Given
        AvistamientoDto avistamientoDto = new AvistamientoDto();
        avistamientoDto.setMascotaId(1L);
        avistamientoDto.setComentario("Vi al perro");
        avistamientoDto.setFecha(LocalDate.now());

        // When & Then
        mockMvc.perform(post("/api/avistamientos")
                .param("reportadorId", "999") // Usuario no existe
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(avistamientoDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(containsString("Usuario no encontrado")));
    }

    @Test
    @Order(4)
    @DisplayName("POST /api/avistamientos - Mascota no existe")
    void noDeberiaCrearAvistamientoConMascotaInexistente() throws Exception {
        // Given
        AvistamientoDto avistamientoDto = new AvistamientoDto();
        avistamientoDto.setMascotaId(999L); // Mascota no existe
        avistamientoDto.setComentario("Vi una mascota");
        avistamientoDto.setFecha(LocalDate.now());

        // When & Then
        mockMvc.perform(post("/api/avistamientos")
                .param("reportadorId", "2")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(avistamientoDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(containsString("Mascota no encontrada")));
    }

    @Test
    @Order(5)
    @DisplayName("POST /api/avistamientos - Fecha por defecto si no se proporciona")
    void deberiaUsarFechaActualSiNoSeProporciona() throws Exception {
        // Given
        AvistamientoDto avistamientoDto = new AvistamientoDto();
        avistamientoDto.setMascotaId(2L); // Luna
        avistamientoDto.setComentario("Vi a esta gata hoy");
        // Sin fecha - debería usar LocalDate.now()

        // When & Then
        mockMvc.perform(post("/api/avistamientos")
                .param("reportadorId", "1") // Juan reporta
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(avistamientoDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.fecha").value(LocalDate.now().toString()));
    }

    @Test
    @Order(6)
    @DisplayName("GET /api/avistamientos - Listar todos los avistamientos")
    void deberiaListarTodosLosAvistamientos() throws Exception {
        mockMvc.perform(get("/api/avistamientos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(3)))) // 3 avistamientos iniciales
                .andExpect(jsonPath("$[0].id").exists())
                .andExpect(jsonPath("$[0].mascotaNombre").exists())
                .andExpect(jsonPath("$[0].reportadorNombre").exists());
    }

    @Test
    @Order(7)
    @DisplayName("GET /api/avistamientos/{id} - Obtener avistamiento por ID")
    void deberiaObtenerAvistamientoPorId() throws Exception {
        mockMvc.perform(get("/api/avistamientos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.comentario").value("Vi a este perro cerca de la plaza San Martín"))
                .andExpect(jsonPath("$.mascotaId").value(1))
                .andExpect(jsonPath("$.mascotaNombre").value("Fido"))
                .andExpect(jsonPath("$.reportadorId").value(2))
                .andExpect(jsonPath("$.reportadorNombre").value("María González"))
                .andExpect(jsonPath("$.ubicacion.barrio").value("Centro"))
                .andExpect(jsonPath("$.fecha").exists());
    }

    @Test
    @Order(8)
    @DisplayName("GET /api/avistamientos/{id} - Avistamiento no existe")
    void noDeberiaEncontrarAvistamientoInexistente() throws Exception {
        mockMvc.perform(get("/api/avistamientos/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(9)
    @DisplayName("GET /api/avistamientos/mascota/{mascotaId} - Listar avistamientos de una mascota")
    void deberiaListarAvistamientosPorMascota() throws Exception {
        mockMvc.perform(get("/api/avistamientos/mascota/1")) // Avistamientos de Fido
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(2)))) // Al menos 2 avistamientos de Fido
                .andExpect(jsonPath("$[0].mascotaId").value(1))
                .andExpect(jsonPath("$[0].mascotaNombre").value("Fido"))
                .andExpect(jsonPath("$[1].mascotaId").value(1))
                .andExpect(jsonPath("$[1].mascotaNombre").value("Fido"));
    }

    @Test
    @Order(10)
    @DisplayName("GET /api/avistamientos/mascota/{mascotaId} - Mascota sin avistamientos")
    void deberiaRetornarListaVaciaSiMascotaSinAvistamientos() throws Exception {
        mockMvc.perform(get("/api/avistamientos/mascota/3")) // Max (recuperado, sin avistamientos)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @Order(11)
    @DisplayName("GET /api/avistamientos/mascota/{mascotaId} - Verificar ordenamiento por fecha")
    void deberiaListarAvistamientosConInformacionCompleta() throws Exception {
        mockMvc.perform(get("/api/avistamientos/mascota/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").exists())
                .andExpect(jsonPath("$[0].fecha").exists())
                .andExpect(jsonPath("$[0].comentario").exists())
                .andExpect(jsonPath("$[0].mascotaId").value(1))
                .andExpect(jsonPath("$[0].mascotaNombre").isNotEmpty())
                .andExpect(jsonPath("$[0].reportadorId").exists())
                .andExpect(jsonPath("$[0].reportadorNombre").isNotEmpty());
    }

    @Test
    @Order(12)
    @DisplayName("POST /api/avistamientos - Múltiples avistamientos de diferentes reportadores")
    void deberiaPermitirMultiplesAvistamientosDeDiferentesUsuarios() throws Exception {
        // Given - Avistamiento 1 de María
        AvistamientoDto avistamiento1 = new AvistamientoDto();
        avistamiento1.setMascotaId(2L); // Luna
        avistamiento1.setComentario("Vi a Luna en el parque");
        avistamiento1.setFecha(LocalDate.now().minusDays(1));

        mockMvc.perform(post("/api/avistamientos")
                .param("reportadorId", "2") // María
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(avistamiento1)))
                .andExpect(status().isCreated());

        // Given - Avistamiento 2 de Carlos
        AvistamientoDto avistamiento2 = new AvistamientoDto();
        avistamiento2.setMascotaId(2L); // Luna
        avistamiento2.setComentario("También la vi yo");
        avistamiento2.setFecha(LocalDate.now());

        mockMvc.perform(post("/api/avistamientos")
                .param("reportadorId", "3") // Carlos
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(avistamiento2)))
                .andExpect(status().isCreated());

        // When & Then - Verificar que ambos avistamientos existen
        mockMvc.perform(get("/api/avistamientos/mascota/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(3)))) // 1 inicial + 2 nuevos
                .andExpect(jsonPath("$[*].reportadorNombre", hasItems(
                    "María González",
                    "Carlos López"
                )));
    }

    @Test
    @Order(13)
    @DisplayName("POST /api/avistamientos - Validar que comentario no esté vacío")
    void noDeberiaCrearAvistamientoSinComentario() throws Exception {
        // Given
        AvistamientoDto avistamientoDto = new AvistamientoDto();
        avistamientoDto.setMascotaId(1L);
        // Sin comentario
        avistamientoDto.setFecha(LocalDate.now());

        // When & Then
        mockMvc.perform(post("/api/avistamientos")
                .param("reportadorId", "2")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(avistamientoDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(14)
    @DisplayName("POST /api/avistamientos - Crear con fecha pasada")
    void deberiaCrearAvistamientoConFechaPasada() throws Exception {
        // Given
        AvistamientoDto avistamientoDto = new AvistamientoDto();
        avistamientoDto.setMascotaId(1L);
        avistamientoDto.setComentario("Vi a Fido hace 5 días");
        avistamientoDto.setFecha(LocalDate.now().minusDays(5));
        
        UbicacionDto ubicacion = new UbicacionDto();
        ubicacion.setBarrio("Tolosa");
        ubicacion.setLatitud(-34.8990);
        ubicacion.setLongitud(-57.9700);
        avistamientoDto.setUbicacion(ubicacion);

        // When & Then
        mockMvc.perform(post("/api/avistamientos")
                .param("reportadorId", "3")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(avistamientoDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.fecha").value(LocalDate.now().minusDays(5).toString()))
                .andExpect(jsonPath("$.comentario").value("Vi a Fido hace 5 días"));
    }

    @Test
    @Order(15)
    @DisplayName("GET /api/avistamientos - Verificar estructura completa de respuesta")
    void deberiaRetornarEstructuraCompletaDeAvistamientos() throws Exception {
        mockMvc.perform(get("/api/avistamientos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").isNumber())
                .andExpect(jsonPath("$[0].fecha").isString())
                .andExpect(jsonPath("$[0].comentario").isString())
                .andExpect(jsonPath("$[0].mascotaId").isNumber())
                .andExpect(jsonPath("$[0].mascotaNombre").isString())
                .andExpect(jsonPath("$[0].reportadorId").isNumber())
                .andExpect(jsonPath("$[0].reportadorNombre").isString());
    }

    @Test
    @Order(16)
    @DisplayName("POST /api/avistamientos - Crear avistamiento con ubicación completa")
    void deberiaCrearAvistamientoConUbicacionCompleta() throws Exception {
        // Given
        AvistamientoDto avistamientoDto = new AvistamientoDto();
        avistamientoDto.setMascotaId(2L); // Luna
        avistamientoDto.setComentario("Vi a esta gata en City Bell");
        avistamientoDto.setFecha(LocalDate.now());
        
        UbicacionDto ubicacion = new UbicacionDto();
        ubicacion.setBarrio("City Bell");
        ubicacion.setLatitud(-34.8700);
        ubicacion.setLongitud(-58.0500);
        avistamientoDto.setUbicacion(ubicacion);

        // When & Then
        mockMvc.perform(post("/api/avistamientos")
                .param("reportadorId", "1") // Juan reporta
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(avistamientoDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.ubicacion").exists())
                .andExpect(jsonPath("$.ubicacion.barrio").value("City Bell"))
                .andExpect(jsonPath("$.ubicacion.latitud").value(-34.8700))
                .andExpect(jsonPath("$.ubicacion.longitud").value(-58.0500))
                .andExpect(jsonPath("$.mascotaNombre").value("Luna"))
                .andExpect(jsonPath("$.reportadorNombre").value("Juan Pérez"));
    }
}