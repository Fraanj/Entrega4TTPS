package ttps.proyecto.persistence.impl;

import ttps.proyecto.models.Medalla;
import ttps.proyecto.persistence.dao.MedallaDAO;

public class MedallaDAOHibernateJPA extends GenericDAOHibernateJPA<Medalla> implements MedallaDAO {
    public MedallaDAOHibernateJPA() {
        super(Medalla.class);
    }
    //defeniremos mas adelante consultas personalizadas
}
