// java
package ttps.proyecto.persistence;

import ttps.proyecto.models.Medalla;
import ttps.proyecto.models.Usuario;
import ttps.proyecto.models.Rol;
import ttps.proyecto.models.EstadoUsuario;
import ttps.proyecto.persistence.dao.MedallaDAO;
import ttps.proyecto.persistence.dao.UsuarioDAO;
import ttps.proyecto.persistence.dao.RolDAO;
import ttps.proyecto.persistence.dao.EstadoUsuarioDAO;
import ttps.proyecto.persistence.util.FactoryDAO;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class MedallaDAOTest {

    private static MedallaDAO medallaDAO;
    private static UsuarioDAO usuarioDAO;
    private static RolDAO rolDAO;
    private static EstadoUsuarioDAO estadoDAO;

    private static Rol rolUsuario;
    private static EstadoUsuario estadoHabilitado;

    @BeforeAll
    public static void setUpAll() {
        medallaDAO = FactoryDAO.getMedallaDAO();
        usuarioDAO = FactoryDAO.getUsuarioDAO();
        rolDAO = FactoryDAO.getRolDAO();
        estadoDAO = FactoryDAO.getEstadoUsuarioDAO();

        rolUsuario = new Rol();
        rolUsuario.setNombre("USUARIO_MEDALLA_TEST");
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
            System.err.println("Error cleaning catalogs Medalla: " + e.getMessage());
        }
    }

    private static void stateSafeDelete() {
        try {
            stateSafeDeleteInner();
        } catch (Exception e) {
            System.err.println("Could not delete estado: " + e.getMessage());
        }
    }

    private static void stateSafeDeleteInner() {
        estadoDAO.eliminar(estadoHabilitado.getId());
    }

    @Test
    public void testCreateMedalla() {
        Long medallaId = null;
        try {
            Medalla medalla = new Medalla();
            medalla.setNombre("MEDALLA_CREATE");
            medalla.setDescripcion("Descripcion create");
            medallaDAO.persistir(medalla);
            medallaId = medalla.getId();
            assertNotNull(medallaId, "Medalla should be persisted (create)");

            Medalla rec = medallaDAO.recuperarPorId(medallaId);
            assertNotNull(rec);
            assertEquals("MEDALLA_CREATE", rec.getNombre());
            assertEquals("Descripcion create", rec.getDescripcion());
        } finally {
            if (medallaId != null) try { medallaDAO.eliminar(medallaId); } catch (Exception ignored) {}
        }
    }

    @Test
    public void testReadMedalla() {
        Long medallaId = null;
        try {
            Medalla medalla = new Medalla();
            medalla.setNombre("MEDALLA_READ");
            medalla.setDescripcion("Descripcion read");
            medallaDAO.persistir(medalla);
            medallaId = medalla.getId();
            assertNotNull(medallaId);

            Medalla rec = medallaDAO.recuperarPorId(medallaId);
            assertNotNull(rec, "Recovered medalla should not be null");
            assertEquals("MEDALLA_READ", rec.getNombre());
            assertEquals("Descripcion read", rec.getDescripcion());
        } finally {
            if (medallaId != null) try { medallaDAO.eliminar(medallaId); } catch (Exception ignored) {}
        }
    }

    @Test
    public void testUpdateMedalla() {
        Long medallaId = null;
        try {
            Medalla medalla = new Medalla();
            medalla.setNombre("MEDALLA_UPDATE");
            medalla.setDescripcion("Descripcion before");
            medallaDAO.persistir(medalla);
            medallaId = medalla.getId();
            assertNotNull(medallaId);

            Medalla toUpdate = medallaDAO.recuperarPorId(medallaId);
            toUpdate.setDescripcion("Descripcion after");
            medallaDAO.actualizar(toUpdate);

            Medalla updated = medallaDAO.recuperarPorId(medallaId);
            assertNotNull(updated);
            assertEquals("Descripcion after", updated.getDescripcion(), "Description should be updated");
            assertEquals("MEDALLA_UPDATE", updated.getNombre(), "Name should remain unchanged");
        } finally {
            if (medallaId != null) try { medallaDAO.eliminar(medallaId); } catch (Exception ignored) {}
        }
    }

    @Test
    public void testDeleteMedalla() {
        Long medallaId = null;
        try {
            Medalla medalla = new Medalla();
            medalla.setNombre("MEDALLA_DELETE");
            medalla.setDescripcion("Descripcion delete");
            medallaDAO.persistir(medalla);
            medallaId = medalla.getId();
            assertNotNull(medallaId);

            // ensure exists
            Medalla exists = medallaDAO.recuperarPorId(medallaId);
            assertNotNull(exists);

            medallaDAO.eliminar(medallaId);

            Medalla afterDelete = medallaDAO.recuperarPorId(medallaId);
            assertNull(afterDelete, "Medalla should be null after deletion");
        } finally {
            if (medallaId != null) try { medallaDAO.eliminar(medallaId); } catch (Exception ignored) {}
        }
    }

    @Test
    public void testMedallaUsuarioRelation() {
        Long medallaId = null;
        Long usuarioId = null;
        try {
            // create usuario
            Usuario usuario = new Usuario();
            usuario.setNombre("UserMedalla");
            usuario.setApellido("Test");
            usuario.setEmail("usermedalla@test");
            usuario.setPassword("pwd");
            usuario.setRol(rolUsuario);
            usuario.setEstado(estadoHabilitado);
            usuarioDAO.persistir(usuario);
            usuarioId = usuario.getId();
            assertNotNull(usuarioId);

            // create medalla
            Medalla medalla = new Medalla();
            medalla.setNombre("MEDALLA_REL");
            medalla.setDescripcion("Descripcion relation");
            medallaDAO.persistir(medalla);
            medallaId = medalla.getId();
            assertNotNull(medallaId);

            // establish relation from the user side if helper exists
            // try user.addMedalla(...) pattern; if not present, attempt to update association via DAO persistence
            try {
                usuario.addMedalla(medalla);
                usuarioDAO.actualizar(usuario);
            } catch (NoSuchMethodError | UnsupportedOperationException e) {
                // fallback: try medalla.setUsuario(...) then update medalla
                try {
                    medalla.setUsuario(usuario);
                    medallaDAO.actualizar(medalla);
                } catch (Exception ignored) {
                    // if neither pattern exists, still attempt to query DAO to show empty result is handled
                }
            } catch (Exception ignored) {
                // ignore unexpected and continue
            }

            // retrieve medallas for usuario
            Set<Medalla> medallas = medallaDAO.getMedallasByUsuario(usuario);
            assertNotNull(medallas, "Result set should not be null");
            Long finalMedallaId = medallaId;
            boolean contains = medallas.stream().anyMatch(m -> m.getId() != null && m.getId().equals(finalMedallaId));
            assertTrue(contains, "Medallas for usuario should contain the created medalla");
        } finally {
            if (medallaId != null) try { medallaDAO.eliminar(medallaId); } catch (Exception ignored) {}
            if (usuarioId != null) try { usuarioDAO.eliminar(usuarioId); } catch (Exception ignored) {}
        }
    }
}
