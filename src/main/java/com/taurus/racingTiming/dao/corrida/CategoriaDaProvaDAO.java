package com.taurus.racingTiming.dao.corrida;

import com.taurus.dao.GenericDAO;
import com.taurus.exception.PersistenciaException;
import com.taurus.racingTiming.entidade.corrida.CategoriaDaProva;
import com.taurus.racingTiming.entidade.corrida.Prova;
import java.util.List;
import javax.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Diego Lima
 */
@Repository
public class CategoriaDaProvaDAO extends GenericDAO<CategoriaDaProva> implements ICategoriaDaProvaDAO<CategoriaDaProva> {

    public CategoriaDaProvaDAO() {
        super(CategoriaDaProva.class);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<CategoriaDaProva> pesquisarCategoria(Prova prova) throws PersistenciaException {
        StringBuilder queryStr = new StringBuilder();
        queryStr.append("SELECT cp from CategoriaDaProva cp ")
                .append("JOIN FETCH cp.categoriaAtleta ca ")
                .append("WHERE cp.prova = :prova ")
                .append("ORDER BY ca.nome ");

        TypedQuery<CategoriaDaProva> query = super.getEntityManager().createQuery(queryStr.toString(), CategoriaDaProva.class);
        query.setParameter("prova", prova);
        try {
            return query.getResultList();
        } catch (Exception ex) {
            throw new PersistenciaException(ex);
        }
    }
}
