package ttps.proyecto.persistence.impl;

import ttps.proyecto.models.Avistamiento;
import ttps.proyecto.persistence.dao.AvistamientoDAO;

public class AvistamientoDAOHibernateJPA extends  GenericDAOHibernateJPA<Avistamiento> implements AvistamientoDAO {

    public AvistamientoDAOHibernateJPA() {
        super(Avistamiento.class);
    }

}
