package com.taurus.racingTiming.dao.corrida;

import com.taurus.dao.GenericDAO;
import com.taurus.racingTiming.entidade.corrida.Cronometragem;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Diego Lima
 */
@Repository
public class CronometragemDAO extends GenericDAO<Cronometragem> implements ICronometragemDAO<Cronometragem> {

    public CronometragemDAO() {
        super(Cronometragem.class);
    }
}
