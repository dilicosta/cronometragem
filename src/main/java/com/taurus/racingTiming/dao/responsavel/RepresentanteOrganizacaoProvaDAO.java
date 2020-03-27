package com.taurus.racingTiming.dao.responsavel;

import com.taurus.dao.GenericDAO;
import com.taurus.racingTiming.entidade.responsavel.RepresentanteOrganizacaoProva;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Diego Lima
 */
@Repository
public class RepresentanteOrganizacaoProvaDAO extends GenericDAO<RepresentanteOrganizacaoProva> implements IRepresentanteOrganizacaoProvaDAO<RepresentanteOrganizacaoProva>{

    public RepresentanteOrganizacaoProvaDAO() {
        super(RepresentanteOrganizacaoProva.class);
    }
}
