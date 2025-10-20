package ttps.proyecto.persistence.dao;
import java.util.List;

/**
 * Interfaz de DAO Genérico (idéntica a Teoria 4.4, pag 4).
 * @param <T> El tipo de la entidad del modelo.
 */
public interface GenericDAO<T> {

    void persistir(T entity); // En la teoría es persist(T)
    T actualizar(T entity); // En la teoría es update(T)
    T recuperarPorId(Long id); // En la teoría es get(Long)
    List<T> recuperarTodos(); // En la teoría es getAll(String)
    void eliminar(Long id); // En la teoría es delete(Long)
    void eliminar(T entity); // En la teoría es delete(T)
}