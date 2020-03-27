package com.taurus.racingTiming.dao.responsavel;

import com.taurus.dao.GenericDAO;
import com.taurus.racingTiming.entidade.responsavel.Federacao;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Diego Lima
 */
@Repository
public class FederacaoDAO extends GenericDAO<Federacao>  implements IFederacaoDAO<Federacao>{

    public FederacaoDAO() {
        super(Federacao.class);
    }
}
