package com.taurus.racingTiming.dao.corrida;

import com.taurus.dao.GenericDAO;
import com.taurus.exception.PersistenciaException;
import com.taurus.racingTiming.entidade.atleta.Atleta;
import com.taurus.racingTiming.entidade.corrida.Prova;
import java.time.LocalDate;
import java.util.List;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import org.apache.commons.validator.GenericValidator;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Diego Lima
 */
@Repository
public class InscricaoDAO extends GenericDAO  {

    public InscricaoDAO() {
        super(Object.class);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public List pesquisarInscricao(String nomeAtleta, String cpfAtleta, LocalDate dataInicialProva, LocalDate dataFinalProva, int pagina, int registrosPorPagina) throws PersistenciaException {
        Query query = super.getEntityManager()
                .createQuery("SELECT i" + this.criarQueryBasicaPesquisarInscricao(nomeAtleta, cpfAtleta, dataInicialProva, dataFinalProva)
                        + " ORDER BY p.data DESC, a.nome");
        query.setFirstResult((pagina - 1) * registrosPorPagina);
        query.setMaxResults(registrosPorPagina);
        this.preencherParametrosQuery(nomeAtleta, cpfAtleta, dataInicialProva, dataFinalProva, query);

        try {
            return query.getResultList();
        } catch (Exception ex) {
            throw new PersistenciaException(ex);
        }
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public Long pesquisarTotalInscricao(String nomeAtleta, String cpfAtleta, LocalDate dataInicialProva, LocalDate dataFinalProva) throws PersistenciaException {
        Query query = super.getEntityManager()
                .createQuery("SELECT count(i.id)" + this.criarQueryBasicaPesquisarInscricao(nomeAtleta, cpfAtleta, dataInicialProva, dataFinalProva).replaceAll("FETCH", ""));

        this.preencherParametrosQuery(nomeAtleta, cpfAtleta, dataInicialProva, dataFinalProva, query);

        try {
            return (Long) query.getSingleResult();
        } catch (Exception ex) {
            throw new PersistenciaException(ex);
        }
    }

    private String criarQueryBasicaPesquisarInscricao(String nomeAtleta, String cpfAtleta, LocalDate dataInicialProva, LocalDate dataFinalProva) {
        boolean whereInserido = false;
        StringBuilder queryStr = new StringBuilder();
        queryStr.append(" from Inscricao i JOIN FETCH i.atleta a")
                .append(" JOIN FETCH i.categoriaDaProva cp")
                .append(" JOIN FETCH cp.prova p")
                .append(" JOIN FETCH cp.categoriaAtleta ca");

        if (!GenericValidator.isBlankOrNull(nomeAtleta)) {
            whereInserido = this.auxiliarWhereAnd(queryStr, whereInserido);
            queryStr.append(" a.nome LIKE :nomeAtleta");
        }
        if (!GenericValidator.isBlankOrNull(cpfAtleta)) {
            whereInserido = this.auxiliarWhereAnd(queryStr, whereInserido);
            queryStr.append(" a.nome = :cpfAtleta");
        }
        if (dataInicialProva != null) {
            whereInserido = this.auxiliarWhereAnd(queryStr, whereInserido);
            queryStr.append(" p.data >= :dataInicial");
        }
        if (dataFinalProva != null) {
            whereInserido = this.auxiliarWhereAnd(queryStr, whereInserido);
            queryStr.append(" p.data <= :dataFinal");
        }

        return queryStr.toString();
    }

    private boolean auxiliarWhereAnd(StringBuilder query, boolean whereInserido) {
        if (!whereInserido) {
            query.append(" WHERE");
            whereInserido = true;
        } else {
            query.append(" AND");
        }
        return whereInserido;
    }

    private void preencherParametrosQuery(String nomeAtleta, String cpfAtleta, LocalDate dataInicialProva, LocalDate dataFinalProva, Query query) {
        if (!GenericValidator.isBlankOrNull(nomeAtleta)) {
            query.setParameter("nomeAtleta", "%" + nomeAtleta + "%");
        }
        if (!GenericValidator.isBlankOrNull(cpfAtleta)) {
            query.setParameter("nomeAtleta", cpfAtleta);
        }
        if (dataInicialProva != null) {
            query.setParameter("dataInicial", dataInicialProva);
        }
        if (dataFinalProva != null) {
            query.setParameter("dataFinal", dataFinalProva);
        }
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public Object pesquisarInscricao(Prova prova, Atleta atleta) throws PersistenciaException {
        StringBuilder queryStr = new StringBuilder();
        queryStr.append("SELECT i from Inscricao i ")
                .append("JOIN FETCH i.atleta a ")
                .append("JOIN FETCH i.categoriaDaProva cp ")
                .append("JOIN FETCH cp.categoriaAtleta ca ")
                .append("JOIN FETCH cp.prova p ")
                .append("WHERE p = :prova ")
                .append("AND a = :atleta");

        TypedQuery query = super.getEntityManager().createQuery(queryStr.toString(), Object.class);
        query.setParameter("prova", prova);
        query.setParameter("atleta", atleta);
        try {
            return query.getResultList().isEmpty() ? null : query.getSingleResult();
        } catch (Exception ex) {
            throw new PersistenciaException(ex);
        }
    }
}
