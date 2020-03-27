package com.taurus.racingTiming.dao.corrida;

import com.taurus.dao.GenericDAO;
import com.taurus.racingTiming.entidade.corrida.Largada;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Diego Lima
 */
@Repository
public class LargadaDAO extends GenericDAO<Largada> implements ILargadaDAO<Largada> {

    public LargadaDAO() {
        super(Largada.class);
    }
}
