package com.taurus.racingTiming.dao.atleta;

import com.taurus.dao.GenericDAO;
import com.taurus.racingTiming.entidade.atleta.Atleta;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Diego Lima
 */
@Repository
public class AtletaDAO extends GenericDAO<Atleta> implements IAtletaDAO<Atleta>{

    public AtletaDAO() {
        super(Atleta.class);
    }
}
