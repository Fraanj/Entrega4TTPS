package ttps.proyecto.persistence;

import ttps.proyecto.models.Mascota;
import ttps.proyecto.models.Usuario;
import ttps.proyecto.models.Ubicacion;
import ttps.proyecto.models.EstadoUsuario;
import ttps.proyecto.models.Rol;
import ttps.proyecto.models.TamanioMascota;
import ttps.proyecto.models.enums.EstadoMascota;
import ttps.proyecto.persistence.dao.*;
import ttps.proyecto.persistence.util.FactoryDAO;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas CRUD y búsquedas para Mascota.
 */
public class MascotaDAOTest {

    private static MascotaDAO mascotaDAO;
    private static UsuarioDAO usuarioDAO;

    // DAOs de Catálogo
    private static RolDAO rolDAO;
    private static EstadoUsuarioDAO estadoUsuarioDAO;
    private static TamanioMascotaDAO tamanioDAO;

    // Entidades de Catálogo
    private static Rol rolUsuario;
    private static EstadoUsuario estadoHabilitado;
    private static TamanioMascota tamanioMediano;

    @BeforeAll
    public static void setUpAll() {
        mascotaDAO = FactoryDAO.getMascotaDAO();
        usuarioDAO = FactoryDAO.getUsuarioDAO();
        rolDAO = FactoryDAO.getRolDAO();
        estadoUsuarioDAO = FactoryDAO.getEstadoUsuarioDAO();
        tamanioDAO = FactoryDAO.getTamanioMascotaDAO();

        rolUsuario = new Rol();
        rolUsuario.setNombre("USUARIO_TEST");
        rolDAO.persistir(rolUsuario);

        estadoHabilitado = new EstadoUsuario();
        estadoHabilitado.setNombre("HABILITADO_TEST");
        estadoUsuarioDAO.persistir(estadoHabilitado);

        tamanioMediano = new TamanioMascota();
        tamanioMediano.setNombre("MEDIANO_TEST");
        tamanioDAO.persistir(tamanioMediano);
    }

    @AfterAll
    public static void tearDownAll() {
        try {
            if (rolUsuario != null && rolUsuario.getId() != null) rolDAO.eliminar(rolUsuario.getId());
            if (estadoHabilitado != null && estadoHabilitado.getId() != null) estadoUsuarioDAO.eliminar(estadoHabilitado.getId());
            if (tamanioMediano != null && tamanioMediano.getId() != null) tamanioDAO.eliminar(tamanioMediano.getId());
        } catch (Exception e) {
            System.err.println("Error limpiando datos de catálogo: " + e.getMessage());
        }
    }

    @Test
    public void testCreateMascota() {
        Long usuarioId = null;
        Long mascotaId = null;

        try {
            // Create and persist user
            Usuario usuario = new Usuario();
            usuario.setNombre("CreateUser");
            usuario.setApellido("Test");
            usuario.setEmail("create@test.com");
            usuario.setPassword("pwd");
            usuario.setRol(rolUsuario);
            usuario.setEstado(estadoHabilitado);
            usuarioDAO.persistir(usuario);
            usuarioId = usuario.getId();
            assertNotNull(usuarioId, "Usuario no persistido");

            // Create mascot with all fields
            Mascota mascota = new Mascota();
            mascota.setNombre("CreateFido");
            mascota.setEstado(EstadoMascota.PERDIDO_PROPIO);
            mascota.setPublicador(usuario);
            mascota.setTamanio(tamanioMediano);
            Ubicacion ubic = new Ubicacion();
            ubic.setBarrio("BarrioCreate");
            mascota.setUltimaUbicacion(ubic);
            mascota.setColor("Marron");
            mascota.setDescripcion("Descripcion Create");
            mascota.setFechaPublicacion(LocalDate.of(2021, 5, 20));

            mascotaDAO.persistir(mascota);
            mascotaId = mascota.getId();
            assertNotNull(mascotaId, "Mascota no persistida");

            // Retrieve to verify persisted values
            Mascota rec = mascotaDAO.recuperarPorId(mascotaId);
            assertNotNull(rec);
            assertEquals("CreateFido", rec.getNombre());
            assertEquals(EstadoMascota.PERDIDO_PROPIO, rec.getEstado());
            assertEquals(usuarioId, rec.getPublicador().getId());
            assertEquals("BarrioCreate", rec.getUltimaUbicacion().getBarrio());
            assertEquals("Marron", rec.getColor());
            assertEquals("Descripcion Create", rec.getDescripcion());
            assertEquals(LocalDate.of(2021, 5, 20), rec.getFechaPublicacion());
            assertEquals(tamanioMediano.getId(), rec.getTamanio().getId());
        } finally {
            if (mascotaId != null) try { mascotaDAO.eliminar(mascotaId); } catch (Exception ignored) {}
            if (usuarioId != null) try { usuarioDAO.eliminar(usuarioId); } catch (Exception ignored) {}
        }
    }

    @Test
    public void testReadMascota() {
        Long usuarioId = null;
        Long mascotaId = null;

        try {
            // Setup
            Usuario usuario = new Usuario();
            usuario.setNombre("ReadUser");
            usuario.setApellido("Test");
            usuario.setEmail("read@test.com");
            usuario.setPassword("pwd");
            usuario.setRol(rolUsuario);
            usuario.setEstado(estadoHabilitado);
            usuarioDAO.persistir(usuario);
            usuarioId = usuario.getId();
            assertNotNull(usuarioId);

            Mascota mascota = new Mascota();
            mascota.setNombre("ReadFido");
            mascota.setEstado(EstadoMascota.PERDIDO_PROPIO);
            mascota.setPublicador(usuario);
            mascota.setTamanio(tamanioMediano);
            Ubicacion ubic = new Ubicacion();
            ubic.setBarrio("BarrioRead");
            mascota.setUltimaUbicacion(ubic);
            mascota.setColor("Negro");
            mascota.setDescripcion("Descripcion Read");
            mascota.setFechaPublicacion(LocalDate.of(2022, 1, 15));

            mascotaDAO.persistir(mascota);
            mascotaId = mascota.getId();

            // Act - read
            Mascota rec = mascotaDAO.recuperarPorId(mascotaId);
            assertNotNull(rec);
            assertEquals("ReadFido", rec.getNombre());
            assertEquals("Negro", rec.getColor());
            assertEquals("Descripcion Read", rec.getDescripcion());
            assertEquals(LocalDate.of(2022, 1, 15), rec.getFechaPublicacion());
            assertEquals("BarrioRead", rec.getUltimaUbicacion().getBarrio());
            assertEquals(EstadoMascota.PERDIDO_PROPIO, rec.getEstado());
            assertEquals(usuarioId, rec.getPublicador().getId());
        } finally {
            if (mascotaId != null) try { mascotaDAO.eliminar(mascotaId); } catch (Exception ignored) {}
            if (usuarioId != null) try { usuarioDAO.eliminar(usuarioId); } catch (Exception ignored) {}
        }
    }

    @Test
    public void testUpdateMascota() {
        Long usuarioId = null;
        Long mascotaId = null;

        try {
            // Setup user and mascota
            Usuario usuario = new Usuario();
            usuario.setNombre("UpdateUser");
            usuario.setApellido("Test");
            usuario.setEmail("update@test.com");
            usuario.setPassword("pwd");
            usuario.setRol(rolUsuario);
            usuario.setEstado(estadoHabilitado);
            usuarioDAO.persistir(usuario);
            usuarioId = usuario.getId();

            Mascota mascota = new Mascota();
            mascota.setNombre("UpdateFido");
            mascota.setEstado(EstadoMascota.PERDIDO_PROPIO);
            mascota.setPublicador(usuario);
            mascota.setTamanio(tamanioMediano);
            Ubicacion ubic = new Ubicacion();
            ubic.setBarrio("BarrioBefore");
            mascota.setUltimaUbicacion(ubic);
            mascota.setColor("Blanco");
            mascota.setDescripcion("Before update");
            mascota.setFechaPublicacion(LocalDate.of(2020, 3, 10));

            mascotaDAO.persistir(mascota);
            mascotaId = mascota.getId();

            //Modificamos datos
            Mascota toUpdate = mascotaDAO.recuperarPorId(mascotaId);
            toUpdate.setNombre("UpdatedFido");
            toUpdate.setEstado(EstadoMascota.RECUPERADO);
            toUpdate.setColor("Gris");
            toUpdate.setDescripcion("After update");
            toUpdate.getUltimaUbicacion().setBarrio("BarrioAfter");
            toUpdate.setFechaPublicacion(LocalDate.of(2023, 7, 1));

            mascotaDAO.actualizar(toUpdate);

            // Verify update
            Mascota updated = mascotaDAO.recuperarPorId(mascotaId);
            assertNotNull(updated);
            assertEquals("UpdatedFido", updated.getNombre());
            assertEquals(EstadoMascota.RECUPERADO, updated.getEstado());
            assertEquals("Gris", updated.getColor());
            assertEquals("After update", updated.getDescripcion());
            assertEquals("BarrioAfter", updated.getUltimaUbicacion().getBarrio());
            assertEquals(LocalDate.of(2023, 7, 1), updated.getFechaPublicacion());
        } finally {
            if (mascotaId != null) try { mascotaDAO.eliminar(mascotaId); } catch (Exception ignored) {}
            if (usuarioId != null) try { usuarioDAO.eliminar(usuarioId); } catch (Exception ignored) {}
        }
    }

    @Test
    public void testDeleteMascota() {
        Long usuarioId = null;
        Long mascotaId = null;

        try {
            // Setup
            Usuario usuario = new Usuario();
            usuario.setNombre("DeleteUser");
            usuario.setApellido("Test");
            usuario.setEmail("delete@test.com");
            usuario.setPassword("pwd");
            usuario.setRol(rolUsuario);
            usuario.setEstado(estadoHabilitado);
            usuarioDAO.persistir(usuario);
            usuarioId = usuario.getId();

            Mascota mascota = new Mascota();
            mascota.setNombre("DeleteFido");
            mascota.setEstado(EstadoMascota.PERDIDO_PROPIO);
            mascota.setPublicador(usuario);
            mascota.setTamanio(tamanioMediano);
            Ubicacion ubic = new Ubicacion();
            ubic.setBarrio("BarrioDelete");
            mascota.setUltimaUbicacion(ubic);
            mascota.setColor("Cafe");
            mascota.setDescripcion("To be deleted");
            mascota.setFechaPublicacion(LocalDate.of(2019, 11, 11));

            mascotaDAO.persistir(mascota);
            mascotaId = mascota.getId();
            assertNotNull(mascotaDAO.recuperarPorId(mascotaId));

            // Delete
            mascotaDAO.eliminar(mascotaId);

            // Verify deletion
            assertNull(mascotaDAO.recuperarPorId(mascotaId), "Mascota debería ser nula después de eliminar");
        } finally {
            if (mascotaId != null) try { mascotaDAO.eliminar(mascotaId); } catch (Exception ignored) {}
            if (usuarioId != null) try { usuarioDAO.eliminar(usuarioId); } catch (Exception ignored) {}
        }
    }
}
