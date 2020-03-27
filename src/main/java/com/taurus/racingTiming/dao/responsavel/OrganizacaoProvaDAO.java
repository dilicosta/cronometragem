package com.taurus.racingTiming.dao.responsavel;

import com.taurus.dao.GenericDAO;
import com.taurus.racingTiming.entidade.responsavel.OrganizacaoProva;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Diego Lima
 */
@Repository
public class OrganizacaoProvaDAO extends GenericDAO<OrganizacaoProva> implements IOrganizacaoProvaDAO<OrganizacaoProva>{

    public OrganizacaoProvaDAO() {
        super(OrganizacaoProva.class);
    }
}
