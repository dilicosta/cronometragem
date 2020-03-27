package com.taurus.racingTiming.dao.atleta;

import com.taurus.dao.GenericDAO;
import com.taurus.racingTiming.entidade.atleta.CategoriaAtleta;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Diego Lima
 */
@Repository
public class CategoriaAtletaDAO extends GenericDAO<CategoriaAtleta> implements ICategoriaAtletaDAO<CategoriaAtleta> {

    public CategoriaAtletaDAO() {
        super(CategoriaAtleta.class);
    }
}
