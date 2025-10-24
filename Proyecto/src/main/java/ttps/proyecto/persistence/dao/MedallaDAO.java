package ttps.proyecto.persistence.dao;

import ttps.proyecto.models.Medalla;
import ttps.proyecto.models.Usuario;

import java.util.Set;

public interface MedallaDAO extends GenericDAO<Medalla>{
    public Set<Medalla> getMedallasByUsuario(Usuario usuario);
}
