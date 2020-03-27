package com.taurus.racingTiming.dao.responsavel;

import com.taurus.dao.GenericDAO;
import com.taurus.racingTiming.entidade.responsavel.RepresentanteFederacao;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Diego Lima
 */
@Repository
public class RepresentanteFederacaoDAO extends GenericDAO<RepresentanteFederacao> implements IRepresentanteFederacaoDAO<RepresentanteFederacao>{

    public RepresentanteFederacaoDAO() {
        super(RepresentanteFederacao.class);
    }
}
