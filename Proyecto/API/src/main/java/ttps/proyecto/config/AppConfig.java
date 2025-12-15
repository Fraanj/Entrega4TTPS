package ttps.proyecto.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import jakarta.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import java.util.Properties;

/**
 * Clase de configuración principal del contexto raíz (no web) de Spring.
 *
 * Se encarga de:
 *  - Configurar la conexión a la base de datos (DataSource)
 *  - Configurar JPA/Hibernate (EntityManagerFactory)
 *  - Habilitar y configurar el manejo de transacciones
 *  - Escanear los paquetes de servicios, repositorios, etc.
 */
@Configuration // Indica que esta clase define beans gestionados por Spring
@EnableTransactionManagement // Habilita el manejo automático de transacciones (con @Transactional)
@ComponentScan(basePackages = "ttps.proyecto") // Spring buscará componentes en este paquete (DAO, servicios, etc.)
@EnableJpaRepositories(basePackages = "ttps.proyecto.repositories")
public class AppConfig {

    /**
     * Define el bean DataSource, que contiene la configuración de conexión
     * a la base de datos: driver, URL, usuario y contraseña.
     */
    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        // Driver JDBC que usará Hibernate (para MySQL 8)
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        // URL de conexión a la base de datos
        dataSource.setUrl("jdbc:mysql://localhost:3306/mibd?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC");
        // Credenciales de acceso
        dataSource.setUsername("root");
        dataSource.setPassword("1234567!");
        return dataSource;
    }

    /**
     * Define el bean de tipo EntityManagerFactory, que crea y gestiona
     * los EntityManager de JPA (responsables de las operaciones con entidades).
     */
    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource) {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource);

        // Indica a Spring en qué paquete buscar las entidades (@Entity)
        em.setPackagesToScan("ttps.proyecto.models");

        // Usa Hibernate como proveedor de JPA
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);
        em.setJpaProperties(hibernateProperties());

        return em;
    }

    /**
     * Define propiedades específicas de Hibernate (motor JPA que estamos usando).
     */
    private Properties hibernateProperties() {
        Properties properties = new Properties();
        // Dialecto SQL de MySQL (traduce las consultas JPA a SQL nativo)
        properties.put("hibernate.dialect", "org.hibernate.dialect.MySQL8Dialect");
        // Muestra las consultas SQL por consola
        properties.put("hibernate.show_sql", "true");
        /* Control del esquema:
             - update: actualiza las tablas sin borrar datos (modo desarrollo)
             - create-drop: crea las tablas al inicio y las elimina al cerrar (modo test)
             - create: crea las tablas nuevas (borra las anteriores)
             - none: no toca la base de datos */
        properties.put("hibernate.hbm2ddl.auto", "update");
        // Formatea el SQL mostrado para hacerlo más legible
        properties.put("hibernate.format_sql", "true");
        return properties;
    }

    /**
     * Define el bean para manejar transacciones JPA.
     * Spring lo usa para iniciar, confirmar o revertir transacciones automáticamente
     * cuando se usa la anotación @Transactional.
     */
    @Bean
    public PlatformTransactionManager transactionManager(EntityManagerFactory emf) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        // Se asocia al EntityManagerFactory definido arriba
        transactionManager.setEntityManagerFactory(emf);
        return transactionManager;
    }
}