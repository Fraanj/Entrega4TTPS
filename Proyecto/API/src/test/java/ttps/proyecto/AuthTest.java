package ttps.proyecto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import ttps.proyecto.config.AppConfig;
import ttps.proyecto.config.WebConfig;
import ttps.proyecto.dto.LoginRequest;
import ttps.proyecto.dto.RegisterRequest;

import org.junit.jupiter.api.extension.ExtendWith;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { AppConfig.class, WebConfig.class })
@WebAppConfiguration
@Transactional
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AuthTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    @Order(1)
    @DisplayName("POST /api/auth/register - Registro exitoso")
    void deberiaRegistrarNuevoUsuario() throws Exception {
        // Given
        RegisterRequest request = new RegisterRequest();
        request.setNombre("Pedro");
        request.setApellido("García");
        request.setEmail("pedro@test.com");
        request.setPassword("123456");
        request.setTelefono("221-5555555");
        request.setCiudad("La Plata");

        // When & Then
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombre").value("Pedro"))
                .andExpect(jsonPath("$.email").value("pedro@test.com"))
                .andExpect(jsonPath("$.rolNombre").value("USUARIO"))
                .andExpect(jsonPath("$.puntos").value(0));
    }

    @Test
    @Order(2)
    @DisplayName("POST /api/auth/register - Email duplicado")
    void noDeberiaRegistrarEmailDuplicado() throws Exception {
        // Given (usuario ya existe en test-data.sql)
        RegisterRequest request = new RegisterRequest();
        request.setNombre("Juan");
        request.setApellido("Pérez");
        request.setEmail("juan@example.com"); // Ya existe
        request.setPassword("123456");
        request.setTelefono("221-4567890");
        request.setCiudad("La Plata");

        // When & Then
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(containsString("El email ya está registrado")));
    }

    @Test
    @Order(3)
    @DisplayName("POST /api/auth/register - Validación de campos obligatorios")
    void noDeberiaRegistrarConCamposVacios() throws Exception {
        // Given
        RegisterRequest request = new RegisterRequest();
        request.setEmail("invalido@test.com");
        // Faltan nombre, apellido, password

        // When & Then
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(4)
    @DisplayName("POST /api/auth/login - Login exitoso")
    void deberiaLoginConCredencialesCorrectas() throws Exception {
        // Given
        LoginRequest request = new LoginRequest();
        request.setEmail("juan@example.com");
        request.setPassword("123456");

        // When & Then
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Juan"))
                .andExpect(jsonPath("$.email").value("juan@example.com"));
    }

    @Test
    @Order(5)
    @DisplayName("POST /api/auth/login - Credenciales incorrectas")
    void noDeberiaLoginConPasswordIncorrecto() throws Exception {
        // Given
        LoginRequest request = new LoginRequest();
        request.setEmail("juan@example.com");
        request.setPassword("incorrecta");

        // When & Then
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value(containsString("Credenciales inválidas")));
    }

    @Test
    @Order(6)
    @DisplayName("POST /api/auth/login - Usuario no existe")
    void noDeberiaLoginConUsuarioInexistente() throws Exception {
        // Given
        LoginRequest request = new LoginRequest();
        request.setEmail("noexiste@test.com");
        request.setPassword("123456");

        // When & Then
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }
}