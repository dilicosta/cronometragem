package com.taurus.racingTiming.dao.corrida;

import com.taurus.dao.GenericDAO;
import com.taurus.racingTiming.entidade.corrida.CronometragemImagem;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Diego Lima
 */
@Repository
public class CronometragemImagemDAO extends GenericDAO<CronometragemImagem> implements ICronometragemImagemDAO<CronometragemImagem> {

    public CronometragemImagemDAO() {
        super(CronometragemImagem.class);
    }
}
