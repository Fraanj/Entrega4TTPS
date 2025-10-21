package ttps.proyecto.persistence;

import ttps.proyecto.models.Usuario;
import ttps.proyecto.models.Rol;
import ttps.proyecto.models.EstadoUsuario;
import ttps.proyecto.persistence.dao.UsuarioDAO;
import ttps.proyecto.persistence.dao.RolDAO;
import ttps.proyecto.persistence.dao.EstadoUsuarioDAO;
import ttps.proyecto.persistence.util.FactoryDAO;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class UsuarioDAOTest {

    private static UsuarioDAO usuarioDAO;
    private static RolDAO rolDAO;
    private static EstadoUsuarioDAO estadoDAO;

    private static Rol rolUsuario;
    private static EstadoUsuario estadoHabilitado;

    @BeforeAll
    public static void setUpAll() {
        usuarioDAO = FactoryDAO.getUsuarioDAO();
        rolDAO = FactoryDAO.getRolDAO();
        estadoDAO = FactoryDAO.getEstadoUsuarioDAO();

        rolUsuario = new Rol();
        rolUsuario.setNombre("USUARIO_TEST");
        rolDAO.persistir(rolUsuario);

        estadoHabilitado = new EstadoUsuario();
        estadoHabilitado.setNombre("HABILITADO_TEST");
        estadoDAO.persistir(estadoHabilitado);
    }

    @AfterAll
    public static void tearDownAll() {
        try {
            if (rolUsuario != null && rolUsuario.getId() != null) rolDAO.eliminar(rolUsuario.getId());
            if (estadoHabilitado != null && estadoHabilitado.getId() != null) stateSafeDelete();
        } catch (Exception e) {
            System.err.println("Error limpiando catálogo Usuario: " + e.getMessage());
        }
    }

    private static void stateSafeDelete() {
        // envuelve en try/catch por si el delete falla por FK
        try {
            estadoDAO.eliminar(estadoHabilitado.getId());
        } catch (Exception e) {
            System.err.println("No se pudo borrar estado: " + e.getMessage());
        }
    }

    @Test
    public void testCRUDUsuario() {
        Long usuarioId = null;
        try {
            // --- ALTA ---
            Usuario usuario = new Usuario();
            usuario.setNombre("Test");
            usuario.setApellido("Usuario");
            usuario.setEmail("usuario_test@local");
            usuario.setPassword("pass1234");
            usuario.setRol(rolUsuario);
            usuario.setEstado(estadoHabilitado);

            usuarioDAO.persistir(usuario);
            usuarioId = usuario.getId();
            assertNotNull(usuarioId, "No se persistió el usuario (ALTA)");

            // --- RECUPERACIÓN ---
            Usuario recuperado = usuarioDAO.recuperarPorId(usuarioId);
            assertNotNull(recuperado, "No se recuperó el usuario (RECUP)");
            assertEquals("Test", recuperado.getNombre());
            assertEquals("usuario_test@local", recuperado.getEmail());

            // --- MODIFICACIÓN ---
            recuperado.setEmail("usuario_modificado@local");
            usuarioDAO.actualizar(recuperado);

            Usuario modificado = usuarioDAO.recuperarPorId(usuarioId);
            assertEquals("usuario_modificado@local", modificado.getEmail(), "No se aplicó la modificación (MODIF)");

        } finally {
            // --- BAJA ---
            if (usuarioId != null) {
                usuarioDAO.eliminar(usuarioId);
                assertNull(usuarioDAO.recuperarPorId(usuarioId), "El usuario no fue borrado (BAJA)");
            }
        }
    }
}