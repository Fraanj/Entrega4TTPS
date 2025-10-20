package ttps.proyecto.persistence;

import ttps.proyecto.models.Mascota;
import ttps.proyecto.models.Usuario;
import ttps.proyecto.models.Ubicacion;
import ttps.proyecto.models.EstadoUsuario;
import ttps.proyecto.models.Rol;
import ttps.proyecto.models.TamanioMascota;
import ttps.proyecto.models.enums.EstadoMascota; // El Enum
import ttps.proyecto.persistence.dao.*;
import ttps.proyecto.persistence.util.FactoryDAO;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Prueba C.R.U.D.
 * Usamos @BeforeAll y @AfterAll para crear los datos de catálogo
 * una sola vez para todas las pruebas.
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
        // Cargar DAOs
        mascotaDAO = FactoryDAO.getMascotaDAO();
        usuarioDAO = FactoryDAO.getUsuarioDAO();
        rolDAO = FactoryDAO.getRolDAO();
        estadoUsuarioDAO = FactoryDAO.getEstadoUsuarioDAO();
        tamanioDAO = FactoryDAO.getTamanioMascotaDAO();

        // --- 1. Crear datos de Catálogo ---
        // (En una app real, esto se haría con un script SQL (seeding))

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
        // Limpiar datos de catálogo
        // (En una BD con FKs, deberíamos borrar usuarios y mascotas primero)
        // Por simplicidad del test, si hbm2ddl es "create-drop", esto es opcional.
        try {
            rolDAO.eliminar(rolUsuario.getId());
            estadoUsuarioDAO.eliminar(estadoHabilitado.getId());
            tamanioDAO.eliminar(tamanioMediano.getId());
        } catch (Exception e) {
            System.err.println("Error limpiando datos de catálogo: " + e.getMessage());
        }
    }

    @Test
    public void testCRUDCompleto() {
        Long usuarioId = null;
        Long mascotaId = null;

        try {
            // --- 2. ALTA (Usuario) ---
            Usuario usuario = new Usuario();
            usuario.setNombre("Usuario");
            usuario.setApellido("DePrueba");
            usuario.setEmail("test_crud@test.com");
            usuario.setPassword("1234");
            // Asignamos las entidades de catálogo
            usuario.setRol(rolUsuario);
            usuario.setEstado(estadoHabilitado);
            usuarioDAO.persistir(usuario);
            usuarioId = usuario.getId();
            assertNotNull(usuarioId, "El usuario no se pudo persistir");

            // --- 3. ALTA (Mascota) ---
            Mascota mascota = new Mascota();
            mascota.setNombre("Fido");
            mascota.setEstado(EstadoMascota.PERDIDO_PROPIO); // Usamos el Enum
            mascota.setPublicador(usuario); // Asociamos al usuario
            mascota.setTamanio(tamanioMediano); // Asignamos la entidad de catálogo

            Ubicacion ubicacion = new Ubicacion();
            ubicacion.setBarrio("La Plata");
            mascota.setUltimaUbicacion(ubicacion);

            mascotaDAO.persistir(mascota);
            mascotaId = mascota.getId();
            assertNotNull(mascotaId, "La mascota no se pudo persistir");

            // --- 4. RECUPERACIÓN (Mascota) ---
            Mascota mascotaRecuperada = mascotaDAO.recuperarPorId(mascotaId);
            assertNotNull(mascotaRecuperada, "La mascota no se pudo recuperar");
            assertEquals("Fido", mascotaRecuperada.getNombre());
            assertEquals("La Plata", mascotaRecuperada.getUltimaUbicacion().getBarrio());
            assertEquals(usuarioId, mascotaRecuperada.getPublicador().getId());

            // --- 5. MODIFICACIÓN (Mascota) ---
            mascotaRecuperada.setNombre("FidoModificado");
            mascotaRecuperada.setEstado(EstadoMascota.RECUPERADO); // Cambiamos el Enum
            mascotaDAO.actualizar(mascotaRecuperada);

            Mascota mascotaModificada = mascotaDAO.recuperarPorId(mascotaId);
            assertEquals("FidoModificado", mascotaModificada.getNombre());
            assertEquals(EstadoMascota.RECUPERADO, mascotaModificada.getEstado());

        } finally {
            // --- 6. BAJA (Limpieza) ---
            // Gracias a CascadeType.ALL, al borrar el usuario se borra su mascota.
            if (usuarioId != null) {
                usuarioDAO.eliminar(usuarioId);
            }

            // Verificamos que la mascota también se haya borrado (por cascada)
            if (mascotaId != null) {
                assertNull(mascotaDAO.recuperarPorId(mascotaId), "La mascota no fue borrada en cascada");
            }
        }
    }
}