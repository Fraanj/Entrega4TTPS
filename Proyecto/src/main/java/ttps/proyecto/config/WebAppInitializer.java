package ttps.proyecto.config;

import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

/**
 * Clase que inicializa la aplicación web basada en configuraciones Java.
 * 
 * Reemplaza al antiguo archivo "web.xml" y configura el DispatcherServlet
 * (el núcleo de Spring MVC que recibe todas las peticiones HTTP).
 * 
 * Extiende de AbstractAnnotationConfigDispatcherServletInitializer,
 * que facilita registrar las clases de configuración y definir los mappings.
 */
public class WebAppInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {

    /**
     * Define las clases de configuración raíz de la aplicación.
     * 
     * Estas clases contienen la configuración general (base de datos, JPA, servicios, etc.).
     * En este caso, AppConfig configura:
     *   - DataSource (conexión a la base de datos)
     *   - EntityManager (JPA)
     *   - Transacciones
     */
    @Override
    protected Class<?>[] getRootConfigClasses() {
        return new Class[] { AppConfig.class };
    }

    /**
     * Define las clases de configuración específicas del contexto web (MVC).
     * 
     * Aquí se registran los controladores, convertidores JSON, vistas, etc.
     * En este caso, WebConfig configura:
     *   - Spring MVC
     *   - Conversión de JSON (Jackson)
     *   - Escaneo de controladores
     */
    @Override
    protected Class<?>[] getServletConfigClasses() {
        return new Class[] { WebConfig.class };
    }

    /**
     * Define las URL que manejará el DispatcherServlet.
     * 
     * En este caso, todas las rutas que empiecen con "/api/" serán dirigidas
     * al contexto de Spring MVC configurado por WebConfig.
     * 
     * Ejemplo: 
     *   /api/usuarios
     *   /api/productos
     *   /api/login
     */
    @Override
    protected String[] getServletMappings() {
        return new String[] { "/api/*" };
    }

    /**
     * Nombre del servlet dispatcher (opcional, para logs)
    */
    @Override
    protected String getServletName() {
        return "dispatcher";
    }
}
