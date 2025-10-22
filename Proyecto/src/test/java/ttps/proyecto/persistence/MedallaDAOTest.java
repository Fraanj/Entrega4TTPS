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
import ttps.proyecto.persistence.util.EMF;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test CRUD para Medalla. Para verificar la asociación ManyToMany usamos una consulta JOIN FETCH
 * en el test para inicializar la colección usuarios.
 */
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
            if (estadoHabilitado != null && estadoHabilitado.getId() != null) estadoDAO.eliminar(estadoHabilitado.getId());
        } catch (Exception e) {
            System.err.println("Error limpiando catálogos Medalla: " + e.getMessage());
        }
    }

    @Test
    public void testCRUDMedallaYAsignacionAUsuario() {
        Long medallaId = null;
        Long usuarioId = null;

        try {
            // --- ALTA (Medalla) ---
            Medalla medalla = new Medalla();
            medalla.setNombre("MEDALLA_TEST");
            medalla.setDescripcion("Descripcion inicial");
            medallaDAO.persistir(medalla);
            medallaId = medalla.getId();
            assertNotNull(medallaId, "No se persistió la medalla (ALTA)");

            // --- RECUP (simple) ---
            Medalla recuperada = medallaDAO.recuperarPorId(medallaId);
            assertNotNull(recuperada, "No se recuperó la medalla (RECUP)");
            assertEquals("MEDALLA_TEST", recuperada.getNombre());

            // --- MODIFICACIÓN ---
            recuperada.setDescripcion("Descripcion modificada");
            medallaDAO.actualizar(recuperada);
            Medalla modificada = medallaDAO.recuperarPorId(medallaId);
            assertEquals("Descripcion modificada", modificada.getDescripcion(), "No se aplicó la modificación (MODIF)");

            // --- ASIGNACIÓN A USUARIO (ManyToMany) ---
            Usuario usuario = new Usuario();
            usuario.setNombre("UsuarioMedalla");
            usuario.setApellido("Test");
            usuario.setEmail("usuario_medalla@test");
            usuario.setPassword("pwd");
            usuario.setRol(rolUsuario);
            usuario.setEstado(estadoHabilitado);

            // agregar la medalla en la colección propietaria (Usuario.medallas)
            usuario.getMedallas().add(modificada);
            usuarioDAO.persistir(usuario);
            usuarioId = usuario.getId();
            assertNotNull(usuarioId, "No se persistió el usuario con medalla");

            // Recuperar medalla con usuarios mediante JOIN FETCH para inicializar la colección
            EntityManager em = EMF.getEMF().createEntityManager();
            try {
                TypedQuery<Medalla> q = em.createQuery(
                        "SELECT DISTINCT m FROM Medalla m LEFT JOIN FETCH m.usuarios u WHERE m.id = :id", Medalla.class);
                q.setParameter("id", medallaId);
                Medalla medWithUsers = q.getSingleResult();
                assertNotNull(medWithUsers);
                Long finalUsuarioId = usuarioId;
                assertTrue(medWithUsers.getUsuarios().stream().anyMatch(u -> u.getId().equals(finalUsuarioId)),
                        "La medalla no refleja la asociación con el usuario");
            } finally {
                em.close();
            }

        } finally {
            // --- BAJA ---
            if (usuarioId != null) {
                try { usuarioDAO.eliminar(usuarioId); } catch (Exception ignored) {}
            }
            if (medallaId != null) {
                try { medallaDAO.eliminar(medallaId); } catch (Exception ignored) {}
            }
        }
    }
}