// java
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
            if (estadoHabilitado != null && estadoHabilitado.getId() != null) estadoDAO.eliminar(estadoHabilitado.getId());
        } catch (Exception e) {
            // kept minimal to avoid test noise
        }
    }

    @Test
    public void testCreateUsuario() {
        Long usuarioId = null;
        try {
            Usuario usuario = new Usuario();
            usuario.setNombre("Create");
            usuario.setApellido("User");
            usuario.setEmail("create_user@local");
            usuario.setPassword("pwdCreate");
            usuario.setRol(rolUsuario);
            usuario.setEstado(estadoHabilitado);

            usuarioDAO.persistir(usuario);
            usuarioId = usuario.getId();
            assertNotNull(usuarioId, "User should be persisted (create)");

            Usuario rec = usuarioDAO.recuperarPorId(usuarioId);
            assertNotNull(rec);
            assertEquals("Create", rec.getNombre());
            assertEquals("create_user@local", rec.getEmail());
        } finally {
            if (usuarioId != null) try { usuarioDAO.eliminar(usuarioId); } catch (Exception ignored) {}
        }
    }

    @Test
    public void testReadUsuario() {
        Long usuarioId = null;
        try {
            Usuario usuario = new Usuario();
            usuario.setNombre("Read");
            usuario.setApellido("User");
            usuario.setEmail("read_user@local");
            usuario.setPassword("pwdRead");
            usuario.setRol(rolUsuario);
            usuario.setEstado(estadoHabilitado);

            usuarioDAO.persistir(usuario);
            usuarioId = usuario.getId();
            assertNotNull(usuarioId);

            // read by id
            Usuario recById = usuarioDAO.recuperarPorId(usuarioId);
            assertNotNull(recById);
            assertEquals("Read", recById.getNombre());
            assertEquals("read_user@local", recById.getEmail());

            // if DAO supports recuperarPorEmail verify it
            try {
                Usuario recByEmail = usuarioDAO.recuperarPorEmail("read_user@local");
                if (recByEmail != null) {
                    assertEquals(usuarioId, recByEmail.getId());
                }
            } catch (AbstractMethodError | UnsupportedOperationException ignored) {
                // ignore if not implemented
            }
        } finally {
            if (usuarioId != null) try { usuarioDAO.eliminar(usuarioId); } catch (Exception ignored) {}
        }
    }

    @Test
    public void testUpdateUsuario() {
        Long usuarioId = null;
        try {
            Usuario usuario = new Usuario();
            usuario.setNombre("Update");
            usuario.setApellido("User");
            usuario.setEmail("update_user@local");
            usuario.setPassword("pwdUpdate");
            usuario.setRol(rolUsuario);
            usuario.setEstado(estadoHabilitado);

            usuarioDAO.persistir(usuario);
            usuarioId = usuario.getId();
            assertNotNull(usuarioId);

            Usuario toUpdate = usuarioDAO.recuperarPorId(usuarioId);
            toUpdate.setEmail("updated_user@local");
            toUpdate.setNombre("UpdatedName");
            usuarioDAO.actualizar(toUpdate);

            Usuario updated = usuarioDAO.recuperarPorId(usuarioId);
            assertNotNull(updated);
            assertEquals("updated_user@local", updated.getEmail(), "Email should be updated");
            assertEquals("UpdatedName", updated.getNombre(), "Name should be updated");
        } finally {
            if (usuarioId != null) try { usuarioDAO.eliminar(usuarioId); } catch (Exception ignored) {}
        }
    }

    @Test
    public void testDeleteUsuario() {
        Long usuarioId = null;
        try {
            Usuario usuario = new Usuario();
            usuario.setNombre("Delete");
            usuario.setApellido("User");
            usuario.setEmail("delete_user@local");
            usuario.setPassword("pwdDelete");
            usuario.setRol(rolUsuario);
            usuario.setEstado(estadoHabilitado);

            usuarioDAO.persistir(usuario);
            usuarioId = usuario.getId();
            assertNotNull(usuarioId);

            Usuario exists = usuarioDAO.recuperarPorId(usuarioId);
            assertNotNull(exists);

            usuarioDAO.eliminar(usuarioId);

            Usuario after = usuarioDAO.recuperarPorId(usuarioId);
            assertNull(after, "User should be null after deletion");
        } finally {
            if (usuarioId != null) try { usuarioDAO.eliminar(usuarioId); } catch (Exception ignored) {}
        }
    }
}
