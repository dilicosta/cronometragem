package com.taurus.racingTiming.dao.corrida;

import com.taurus.dao.GenericDAO;
import com.taurus.racingTiming.entidade.corrida.Percurso;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Diego Lima
 */
@Repository
public class PercursoDAO extends GenericDAO<Percurso> implements IPercursoDAO<Percurso> {

    public PercursoDAO() {
        super(Percurso.class);
    }
}
