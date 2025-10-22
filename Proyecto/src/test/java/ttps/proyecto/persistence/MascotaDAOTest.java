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
    public void testCRUDCompleto() {
        Long usuarioId = null;
        Long mascotaId = null;

        try {
            // --- ALTA (Usuario) ---
            Usuario usuario = new Usuario();
            usuario.setNombre("Usuario");
            usuario.setApellido("DePrueba");
            usuario.setEmail("test_crud@test.com");
            usuario.setPassword("1234");
            usuario.setRol(rolUsuario);
            usuario.setEstado(estadoHabilitado);
            usuarioDAO.persistir(usuario);
            usuarioId = usuario.getId();
            assertNotNull(usuarioId, "El usuario no se pudo persistir");

            // --- ALTA (Mascota) ---
            Mascota mascota = new Mascota();
            mascota.setNombre("Fido");
            mascota.setEstado(EstadoMascota.PERDIDO_PROPIO);
            mascota.setPublicador(usuario);
            mascota.setTamanio(tamanioMediano);

            Ubicacion ubicacion = new Ubicacion();
            ubicacion.setBarrio("La Plata");
            mascota.setUltimaUbicacion(ubicacion);

            mascotaDAO.persistir(mascota);
            mascotaId = mascota.getId();
            assertNotNull(mascotaId, "La mascota no se pudo persistir");

            // --- RECUPERACIÓN ---
            Mascota mascotaRecuperada = mascotaDAO.recuperarPorId(mascotaId);
            assertNotNull(mascotaRecuperada, "La mascota no se pudo recuperar");
            assertEquals("Fido", mascotaRecuperada.getNombre());
            assertEquals("La Plata", mascotaRecuperada.getUltimaUbicacion().getBarrio());
            assertEquals(usuarioId, mascotaRecuperada.getPublicador().getId());

            // --- MODIFICACIÓN ---
            mascotaRecuperada.setNombre("FidoModificado");
            mascotaRecuperada.setEstado(EstadoMascota.RECUPERADO);
            mascotaDAO.actualizar(mascotaRecuperada);

            Mascota mascotaModificada = mascotaDAO.recuperarPorId(mascotaId);
            assertEquals("FidoModificado", mascotaModificada.getNombre());
            assertEquals(EstadoMascota.RECUPERADO, mascotaModificada.getEstado());

        } finally {
            // --- BAJA ---
            if (usuarioId != null) {
                usuarioDAO.eliminar(usuarioId);
            }
            if (mascotaId != null) {
                assertNull(mascotaDAO.recuperarPorId(mascotaId), "La mascota no fue borrada en cascada");
            }
        }
    }

    @Test
    public void testBuscarPorEstadoYBarrio() {
        Long usuarioId = null;
        Long m1 = null;
        Long m2 = null;

        try {
            Usuario usuario = new Usuario();
            usuario.setNombre("Buscador");
            usuario.setApellido("Test");
            usuario.setEmail("buscador@test");
            usuario.setPassword("pwd");
            usuario.setRol(rolUsuario);
            usuario.setEstado(estadoHabilitado);
            usuarioDAO.persistir(usuario);
            usuarioId = usuario.getId();
            assertNotNull(usuarioId);

            Mascota mascota1 = new Mascota();
            mascota1.setNombre("Perdida1");
            mascota1.setEstado(EstadoMascota.PERDIDO_PROPIO);
            mascota1.setPublicador(usuario);
            mascota1.setTamanio(tamanioMediano);
            mascota1.setUltimaUbicacion(new Ubicacion());
            mascota1.getUltimaUbicacion().setBarrio("BarrioX");
            mascotaDAO.persistir(mascota1);
            m1 = mascota1.getId();

            Mascota mascota2 = new Mascota();
            mascota2.setNombre("Encontrada1");
            mascota2.setEstado(EstadoMascota.RECUPERADO);
            mascota2.setPublicador(usuario);
            mascota2.setTamanio(tamanioMediano);
            mascota2.setUltimaUbicacion(new Ubicacion());
            mascota2.getUltimaUbicacion().setBarrio("BarrioY");
            mascotaDAO.persistir(mascota2);
            m2 = mascota2.getId();

            // buscar por estado
            List<Mascota> perdidas = mascotaDAO.buscarPorEstado(EstadoMascota.PERDIDO_PROPIO);
            assertNotNull(perdidas);
            Long finalM = m1;
            assertTrue(perdidas.stream().anyMatch(m -> m.getId().equals(finalM)));

            // buscar por barrio
            List<Mascota> barrioX = mascotaDAO.buscarPorBarrio("BarrioX");
            assertNotNull(barrioX);
            Long finalM1 = m1;
            assertTrue(barrioX.stream().anyMatch(m -> m.getId().equals(finalM1)));
            Long finalM2 = m2;
            assertFalse(barrioX.stream().anyMatch(m -> m.getId().equals(finalM2)));

        } finally {
            if (m1 != null) try { mascotaDAO.eliminar(m1); } catch (Exception ignored) {}
            if (m2 != null) try { mascotaDAO.eliminar(m2); } catch (Exception ignored) {}
            if (usuarioId != null) try { usuarioDAO.eliminar(usuarioId); } catch (Exception ignored) {}
        }
    }
}