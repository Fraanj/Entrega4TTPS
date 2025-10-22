package ttps.proyecto.persistence;

import ttps.proyecto.models.Avistamiento;
import ttps.proyecto.models.Mascota;
import ttps.proyecto.models.Usuario;
import ttps.proyecto.models.Ubicacion;
import ttps.proyecto.models.Rol;
import ttps.proyecto.models.EstadoUsuario;
import ttps.proyecto.models.TamanioMascota;
import ttps.proyecto.models.enums.EstadoMascota;
import ttps.proyecto.persistence.dao.AvistamientoDAO;
import ttps.proyecto.persistence.dao.MascotaDAO;
import ttps.proyecto.persistence.dao.UsuarioDAO;
import ttps.proyecto.persistence.dao.RolDAO;
import ttps.proyecto.persistence.dao.EstadoUsuarioDAO;
import ttps.proyecto.persistence.dao.TamanioMascotaDAO;
import ttps.proyecto.persistence.util.FactoryDAO;
import ttps.proyecto.persistence.util.EMF;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test CRUD para Avistamiento. Usamos JOIN FETCH en el test para inicializar relaciones y evitar LazyInitializationException.
 */
public class AvistamientoDAOTest {

    private static AvistamientoDAO avistamientoDAO;
    private static MascotaDAO mascotaDAO;
    private static UsuarioDAO usuarioDAO;
    private static RolDAO rolDAO;
    private static EstadoUsuarioDAO estadoDAO;
    private static TamanioMascotaDAO tamanioDAO;

    private static Rol rolUsuario;
    private static EstadoUsuario estadoHabilitado;
    private static TamanioMascota tamanioMediano;

    @BeforeAll
    public static void setUpAll() {
        avistamientoDAO = FactoryDAO.getAvistamientoDAO();
        mascotaDAO = FactoryDAO.getMascotaDAO();
        usuarioDAO = FactoryDAO.getUsuarioDAO();
        rolDAO = FactoryDAO.getRolDAO();
        estadoDAO = FactoryDAO.getEstadoUsuarioDAO();
        tamanioDAO = FactoryDAO.getTamanioMascotaDAO();

        rolUsuario = new Rol();
        rolUsuario.setNombre("REPORTADOR_TEST");
        rolDAO.persistir(rolUsuario);

        estadoHabilitado = new EstadoUsuario();
        estadoHabilitado.setNombre("HABILITADO_TEST");
        estadoDAO.persistir(estadoHabilitado);

        tamanioMediano = new TamanioMascota();
        tamanioMediano.setNombre("MEDIANO_TEST");
        tamanioDAO.persistir(tamanioMediano);
    }

    @AfterAll
    public static void tearDownAll() {
        try {
            if (tamanioMediano != null && tamanioMediano.getId() != null) tamanioDAO.eliminar(tamanioMediano.getId());
            if (rolUsuario != null && rolUsuario.getId() != null) rolDAO.eliminar(rolUsuario.getId());
            if (estadoHabilitado != null && estadoHabilitado.getId() != null) stateSafeDelete();
        } catch (Exception e) {
            System.err.println("Error limpiando catálogos Avistamiento: " + e.getMessage());
        }
    }

    private static void stateSafeDelete() {
        try {
            estadoDAO.eliminar(estadoHabilitado.getId());
        } catch (Exception e) {
            System.err.println("No se pudo borrar estado: " + e.getMessage());
        }
    }

    @Test
    public void testCRUDAvistamiento() {
        Long usuarioId = null;
        Long mascotaId = null;
        Long avistamientoId = null;

        try {
            // Crear y persistir Usuario
            Usuario usuario = new Usuario();
            usuario.setNombre("Reportador");
            usuario.setApellido("Test");
            usuario.setEmail("reportador@test");
            usuario.setPassword("pwd");
            usuario.setRol(rolUsuario);
            usuario.setEstado(estadoHabilitado);
            usuarioDAO.persistir(usuario);
            usuarioId = usuario.getId();
            assertNotNull(usuarioId, "No se persistió usuario requisito");

            // Crear y persistir Mascota
            Mascota mascota = new Mascota();
            mascota.setNombre("MascotaParaAvistamiento");
            mascota.setEstado(EstadoMascota.PERDIDO_PROPIO);
            mascota.setPublicador(usuario);
            mascota.setTamanio(tamanioMediano);
            mascota.setUltimaUbicacion(new Ubicacion());
            mascota.getUltimaUbicacion().setBarrio("BarrioTest");
            mascotaDAO.persistir(mascota);
            mascotaId = mascota.getId();
            assertNotNull(mascotaId, "No se persistió mascota requisito");

            // ALTA Avistamiento (vía DAO)
            Avistamiento av = new Avistamiento();
            av.setFecha(LocalDate.now());
            av.setComentario("Primera observación");
            av.setReportador(usuario);
            av.setMascota(mascota);
            Ubicacion ub = new Ubicacion();
            ub.setBarrio("BarrioAvistamiento");
            av.setUbicacion(ub);
            avistamientoDAO.persistir(av);
            avistamientoId = av.getId();
            assertNotNull(avistamientoId, "No se persistió avistamiento (ALTA)");

            // RECUPERACIÓN con JOIN FETCH (para inicializar relaciones)
            EntityManager em = EMF.getEMF().createEntityManager();
            try {
                TypedQuery<Avistamiento> q = em.createQuery(
                        "SELECT a FROM Avistamiento a " +
                                "LEFT JOIN FETCH a.mascota m " +
                                "LEFT JOIN FETCH a.reportador r " +
                                "LEFT JOIN FETCH a.ubicacion u " +
                                "LEFT JOIN FETCH a.foto f " +
                                "WHERE a.id = :id", Avistamiento.class);
                q.setParameter("id", avistamientoId);
                Avistamiento recuperado = q.getSingleResult();

                assertNotNull(recuperado, "No se recuperó avistamiento (RECUP)");
                assertEquals("Primera observación", recuperado.getComentario());
                assertEquals("MascotaParaAvistamiento", recuperado.getMascota().getNombre());
                assertEquals(usuarioId, recuperado.getReportador().getId());

                // MODIFICACIÓN
                recuperado.setComentario("Comentario modificado");
                // actualizar vía DAO (nota: esto hace merge en otro EM)
                avistamientoDAO.actualizar(recuperado);

                // Volvemos a recuperar con JOIN FETCH para verificar el cambio
                TypedQuery<Avistamiento> q2 = em.createQuery(
                        "SELECT a FROM Avistamiento a " +
                                "LEFT JOIN FETCH a.mascota m " +
                                "LEFT JOIN FETCH a.reportador r " +
                                "LEFT JOIN FETCH a.ubicacion u " +
                                "LEFT JOIN FETCH a.foto f " +
                                "WHERE a.id = :id", Avistamiento.class);
                q2.setParameter("id", avistamientoId);
                Avistamiento mod = q2.getSingleResult();
                assertEquals("Comentario modificado", mod.getComentario(), "No se aplicó la modificación (MODIF)");

            } finally {
                em.close();
            }

        } finally {
            // BAJA
            if (avistamientoId != null) {
                try { avistamientoDAO.eliminar(avistamientoId); } catch (Exception ignored) {}
            }
            if (mascotaId != null) {
                try { mascotaDAO.eliminar(mascotaId); } catch (Exception ignored) {}
            }
            if (usuarioId != null) {
                try { usuarioDAO.eliminar(usuarioId); } catch (Exception ignored) {}
            }
        }
    }
}