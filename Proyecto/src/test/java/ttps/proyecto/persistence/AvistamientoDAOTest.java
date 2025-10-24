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
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unary tests for Avistamiento CRUD operations.
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
            if (estadoHabilitado != null && estadoHabilitado.getId() != null) safeDeleteEstado();
        } catch (Exception e) {
            System.err.println("Error cleaning catalogs: " + e.getMessage());
        }
    }

    private static void safeDeleteEstado() {
        try {
            estadoDAO.eliminar(estadoHabilitado.getId());
        } catch (Exception e) {
            System.err.println("Could not delete estado: " + e.getMessage());
        }
    }

    @Test
    public void testCreateAvistamiento() {
        Long usuarioId = null;
        Long mascotaId = null;
        Long avistamientoId = null;

        try {
            Usuario usuario = new Usuario();
            usuario.setNombre("Creator");
            usuario.setApellido("Test");
            usuario.setEmail("creator@test");
            usuario.setPassword("pwd");
            usuario.setRol(rolUsuario);
            usuario.setEstado(estadoHabilitado);
            usuarioDAO.persistir(usuario);
            usuarioId = usuario.getId();
            assertNotNull(usuarioId);

            Mascota mascota = new Mascota();
            mascota.setNombre("MascotaCreate");
            mascota.setEstado(EstadoMascota.PERDIDO_PROPIO);
            mascota.setPublicador(usuario);
            mascota.setTamanio(tamanioMediano);
            mascota.setUltimaUbicacion(new Ubicacion());
            mascota.getUltimaUbicacion().setBarrio("BarrioCreate");
            mascotaDAO.persistir(mascota);
            mascotaId = mascota.getId();
            assertNotNull(mascotaId);

            Avistamiento av = new Avistamiento();
            av.setFecha(LocalDate.now());
            av.setComentario("Create observation");
            av.setReportador(usuario);
            av.setMascota(mascota);
            Ubicacion ub = new Ubicacion();
            ub.setBarrio("BarrioAvCreate");
            av.setUbicacion(ub);
            avistamientoDAO.persistir(av);
            avistamientoId = av.getId();
            assertNotNull(avistamientoId);

            Avistamiento rec = avistamientoDAO.recuperarPorIdConRelaciones(avistamientoId);
            assertNotNull(rec);
            assertEquals("Create observation", rec.getComentario());
            assertEquals("MascotaCreate", rec.getMascota().getNombre());
            assertEquals(usuarioId, rec.getReportador().getId());
        } finally {
            if (avistamientoId != null) try { avistamientoDAO.eliminar(avistamientoId); } catch (Exception ignored) {}
            if (mascotaId != null) try { mascotaDAO.eliminar(mascotaId); } catch (Exception ignored) {}
            if (usuarioId != null) try { usuarioDAO.eliminar(usuarioId); } catch (Exception ignored) {}
        }
    }

    @Test
    public void testReadAvistamiento() {
        Long usuarioId = null;
        Long mascotaId = null;
        Long avistamientoId = null;

        try {
            Usuario usuario = new Usuario();
            usuario.setNombre("Reader");
            usuario.setApellido("Test");
            usuario.setEmail("reader@test");
            usuario.setPassword("pwd");
            usuario.setRol(rolUsuario);
            usuario.setEstado(estadoHabilitado);
            usuarioDAO.persistir(usuario);
            usuarioId = usuario.getId();
            assertNotNull(usuarioId);

            Mascota mascota = new Mascota();
            mascota.setNombre("MascotaRead");
            mascota.setEstado(EstadoMascota.PERDIDO_PROPIO);
            mascota.setPublicador(usuario);
            mascota.setTamanio(tamanioMediano);
            mascota.setUltimaUbicacion(new Ubicacion());
            mascota.getUltimaUbicacion().setBarrio("BarrioRead");
            mascotaDAO.persistir(mascota);
            mascotaId = mascota.getId();
            assertNotNull(mascotaId);

            Avistamiento av = new Avistamiento();
            av.setFecha(LocalDate.now());
            av.setComentario("Read observation");
            av.setReportador(usuario);
            av.setMascota(mascota);
            Ubicacion ub = new Ubicacion();
            ub.setBarrio("BarrioAvRead");
            av.setUbicacion(ub);
            avistamientoDAO.persistir(av);
            avistamientoId = av.getId();
            assertNotNull(avistamientoId);

            Avistamiento rec = avistamientoDAO.recuperarPorIdConRelaciones(avistamientoId);
            assertNotNull(rec);
            assertEquals("Read observation", rec.getComentario());
            assertEquals("MascotaRead", rec.getMascota().getNombre());
            assertEquals("BarrioAvRead", rec.getUbicacion().getBarrio());
        } finally {
            if (avistamientoId != null) try { avistamientoDAO.eliminar(avistamientoId); } catch (Exception ignored) {}
            if (mascotaId != null) try { mascotaDAO.eliminar(mascotaId); } catch (Exception ignored) {}
            if (usuarioId != null) try { usuarioDAO.eliminar(usuarioId); } catch (Exception ignored) {}
        }
    }

    @Test
    public void testUpdateAvistamiento() {
        Long usuarioId = null;
        Long mascotaId = null;
        Long avistamientoId = null;

        try {
            Usuario usuario = new Usuario();
            usuario.setNombre("Updater");
            usuario.setApellido("Test");
            usuario.setEmail("updater@test");
            usuario.setPassword("pwd");
            usuario.setRol(rolUsuario);
            usuario.setEstado(estadoHabilitado);
            usuarioDAO.persistir(usuario);
            usuarioId = usuario.getId();
            assertNotNull(usuarioId);

            Mascota mascota = new Mascota();
            mascota.setNombre("MascotaUpdate");
            mascota.setEstado(EstadoMascota.PERDIDO_PROPIO);
            mascota.setPublicador(usuario);
            mascota.setTamanio(tamanioMediano);
            mascota.setUltimaUbicacion(new Ubicacion());
            mascota.getUltimaUbicacion().setBarrio("BarrioBefore");
            mascotaDAO.persistir(mascota);
            mascotaId = mascota.getId();
            assertNotNull(mascotaId);

            Avistamiento av = new Avistamiento();
            av.setFecha(LocalDate.now());
            av.setComentario("Before update");
            av.setReportador(usuario);
            av.setMascota(mascota);
            Ubicacion ub = new Ubicacion();
            ub.setBarrio("BarrioAvBefore");
            av.setUbicacion(ub);
            avistamientoDAO.persistir(av);
            avistamientoId = av.getId();
            assertNotNull(avistamientoId);

            Avistamiento toUpdate = avistamientoDAO.recuperarPorIdConRelaciones(avistamientoId);
            toUpdate.setComentario("After update");
            toUpdate.getUbicacion().setBarrio("BarrioAvAfter");
            avistamientoDAO.actualizar(toUpdate);

            Avistamiento updated = avistamientoDAO.recuperarPorIdConRelaciones(avistamientoId);
            assertNotNull(updated);
            assertEquals("After update", updated.getComentario());
            assertEquals("BarrioAvAfter", updated.getUbicacion().getBarrio());
        } finally {
            if (avistamientoId != null) try { avistamientoDAO.eliminar(avistamientoId); } catch (Exception ignored) {}
            if (mascotaId != null) try { mascotaDAO.eliminar(mascotaId); } catch (Exception ignored) {}
            if (usuarioId != null) try { usuarioDAO.eliminar(usuarioId); } catch (Exception ignored) {}
        }
    }

    @Test
    public void testDeleteAvistamiento() {
        Long usuarioId = null;
        Long mascotaId = null;
        Long avistamientoId = null;

        try {
            Usuario usuario = new Usuario();
            usuario.setNombre("Deleter");
            usuario.setApellido("Test");
            usuario.setEmail("deleter@test");
            usuario.setPassword("pwd");
            usuario.setRol(rolUsuario);
            usuario.setEstado(estadoHabilitado);
            usuarioDAO.persistir(usuario);
            usuarioId = usuario.getId();
            assertNotNull(usuarioId);

            Mascota mascota = new Mascota();
            mascota.setNombre("MascotaDelete");
            mascota.setEstado(EstadoMascota.PERDIDO_PROPIO);
            mascota.setPublicador(usuario);
            mascota.setTamanio(tamanioMediano);
            mascota.setUltimaUbicacion(new Ubicacion());
            mascota.getUltimaUbicacion().setBarrio("BarrioDelete");
            mascotaDAO.persistir(mascota);
            mascotaId = mascota.getId();
            assertNotNull(mascotaId);

            Avistamiento av = new Avistamiento();
            av.setFecha(LocalDate.now());
            av.setComentario("To be deleted");
            av.setReportador(usuario);
            av.setMascota(mascota);
            Ubicacion ub = new Ubicacion();
            ub.setBarrio("BarrioAvDelete");
            av.setUbicacion(ub);
            avistamientoDAO.persistir(av);
            avistamientoId = av.getId();
            assertNotNull(avistamientoId);

            avistamientoDAO.eliminar(avistamientoId);

            try {
                Avistamiento rec = avistamientoDAO.recuperarPorIdConRelaciones(avistamientoId);
                assertNull(rec, "Avistamiento should be null after deletion");
            } catch (Exception expected) {
                // If DAO throws when not found, accept as successful delete
            }
        } finally {
            if (avistamientoId != null) try { avistamientoDAO.eliminar(avistamientoId); } catch (Exception ignored) {}
            if (mascotaId != null) try { mascotaDAO.eliminar(mascotaId); } catch (Exception ignored) {}
            if (usuarioId != null) try { usuarioDAO.eliminar(usuarioId); } catch (Exception ignored) {}
        }
    }
}
