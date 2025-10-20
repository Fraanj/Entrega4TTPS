package ttps.proyecto.persistence.dao;

import ttps.proyecto.models.Usuario;

public interface UsuarioDAO extends GenericDAO<Usuario> {

    // Ejemplo de método específico [cite: 529, 569]
    Usuario recuperarPorEmail(String email);
}