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
import ttps.proyecto.dto.UsuarioDto;

import org.junit.jupiter.api.extension.ExtendWith;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { AppConfig.class, WebConfig.class })
@WebAppConfiguration
@Transactional
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UsuarioTest {

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
    @DisplayName("GET /api/usuarios/{id} - Obtener usuario existente")
    void deberiaObtenerUsuarioPorId() throws Exception {
        mockMvc.perform(get("/api/usuarios/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Juan"))
                .andExpect(jsonPath("$.apellido").value("Pérez"))
                .andExpect(jsonPath("$.email").value("juan@example.com"))
                .andExpect(jsonPath("$.telefono").value("221-4567890"))
                .andExpect(jsonPath("$.ciudad").value("La Plata"))
                .andExpect(jsonPath("$.puntos").value(0))
                .andExpect(jsonPath("$.rolNombre").value("USUARIO"))
                .andExpect(jsonPath("$.estadoNombre").value("HABILITADO"));
    }

    @Test
    @Order(2)
    @DisplayName("GET /api/usuarios/{id} - Usuario no existe")
    void noDeberiaEncontrarUsuarioInexistente() throws Exception {
        mockMvc.perform(get("/api/usuarios/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(3)
    @DisplayName("PUT /api/usuarios/{id} - Actualizar perfil exitosamente")
    void deberiaActualizarPerfilUsuario() throws Exception {
        // Given
        UsuarioDto updateRequest = new UsuarioDto();
        updateRequest.setNombre("Juan Carlos");
        updateRequest.setApellido("Pérez García");
        updateRequest.setEmail("juan@example.com");
        updateRequest.setTelefono("221-9999999");
        updateRequest.setCiudad("Buenos Aires");

        // When & Then
        mockMvc.perform(put("/api/usuarios/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Juan Carlos"))
                .andExpect(jsonPath("$.apellido").value("Pérez García"))
                .andExpect(jsonPath("$.telefono").value("221-9999999"))
                .andExpect(jsonPath("$.ciudad").value("Buenos Aires"));
    }
}
