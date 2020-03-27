package com.taurus.racingTiming.dao.corrida;

import com.taurus.dao.GenericDAO;
import com.taurus.exception.PersistenciaException;
import com.taurus.racingTiming.entidade.corrida.Prova;
import com.taurus.racingTiming.entidade.responsavel.OrganizacaoProva;
import com.taurus.racingTiming.util.ListaConstantes.StatusProva;
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
public class ProvaDAO extends GenericDAO<Prova> implements IProvaDAO<Prova> {

    public ProvaDAO() {
        super(Prova.class);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<Prova> pesquisarProva(String nomeProva, LocalDate dataInicial, LocalDate dataFinal, OrganizacaoProva organizacaoProva, List<StatusProva> listaStatusProva, int pagina, int registrosPorPagina) throws PersistenciaException {
        Query query = super.getEntityManager()
                .createQuery("SELECT p" + this.criarQueryBasicaPesquisarProva(nomeProva, dataInicial, dataFinal, organizacaoProva, listaStatusProva)
                        + " ORDER BY p.data DESC");
        query.setFirstResult((pagina - 1) * registrosPorPagina);
        query.setMaxResults(registrosPorPagina);
        this.preencherParametrosQuery(nomeProva, dataInicial, dataFinal, organizacaoProva, listaStatusProva, query);

        try {
            return query.getResultList();
        } catch (Exception ex) {
            throw new PersistenciaException(ex);
        }
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public Long pesquisarTotalProva(String nomeProva, LocalDate dataInicial, LocalDate dataFinal, OrganizacaoProva organizacaoProva, List<StatusProva> listaStatusProva) throws PersistenciaException {
        Query query = super.getEntityManager()
                .createQuery("SELECT count(p.id)" + this.criarQueryBasicaPesquisarProva(nomeProva, dataInicial, dataFinal, organizacaoProva, listaStatusProva).replaceAll("FETCH", ""));

        this.preencherParametrosQuery(nomeProva, dataInicial, dataFinal, organizacaoProva, listaStatusProva, query);

        try {
            return (Long) query.getSingleResult();
        } catch (Exception ex) {
            throw new PersistenciaException(ex);
        }
    }

    private String criarQueryBasicaPesquisarProva(String nomeProva, LocalDate dataInicial, LocalDate dataFinal, OrganizacaoProva organizacaoProva, List<StatusProva> listaStatusProva) {
        boolean whereInserido = false;
        StringBuilder queryStr = new StringBuilder();
        queryStr.append(" from Prova p JOIN FETCH p.listaRepresentanteOrganizacao ro")
                .append(" JOIN FETCH ro.organizacaoProva op")
                .append(" JOIN FETCH p.endereco");

        if (!GenericValidator.isBlankOrNull(nomeProva)) {
            whereInserido = this.auxiliarWhereAnd(queryStr, whereInserido);
            queryStr.append(" p.nome LIKE :nomeProva");
        }
        if (dataInicial != null) {
            whereInserido = this.auxiliarWhereAnd(queryStr, whereInserido);
            queryStr.append(" p.data >= :dataInicial");
        }
        if (dataFinal != null) {
            whereInserido = this.auxiliarWhereAnd(queryStr, whereInserido);
            queryStr.append(" p.data <= :dataFinal");
        }

        if (organizacaoProva != null) {
            whereInserido = this.auxiliarWhereAnd(queryStr, whereInserido);
            queryStr.append(" ro.organizacaoProva = :organizacaoProva");
        }

        if (listaStatusProva != null) {
            whereInserido = this.auxiliarWhereAnd(queryStr, whereInserido);
            queryStr.append(" (");
            for (int i = 1; i <= listaStatusProva.size(); i++) {
                if (i > 1) {
                    queryStr.append(" OR");
                }
                queryStr.append(" p.status = :statusProva").append(i);
            }
            queryStr.append(")");
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

    private void preencherParametrosQuery(String nomeProva, LocalDate dataInicial, LocalDate dataFinal, OrganizacaoProva organizacaoProva, List<StatusProva> listaStatusProva, Query query) {
        if (!GenericValidator.isBlankOrNull(nomeProva)) {
            query.setParameter("nomeProva", "%" + nomeProva + "%");
        }
        if (dataInicial != null) {
            query.setParameter("dataInicial", dataInicial);
        }
        if (dataFinal != null) {
            query.setParameter("dataFinal", dataFinal);
        }
        if (organizacaoProva != null) {
            query.setParameter("organizacaoProva", organizacaoProva);
        }
        if (listaStatusProva != null) {
            for (int i = 1; i <= listaStatusProva.size(); i++) {
                query.setParameter("statusProva" + i, listaStatusProva.get(i - 1));
            }
        }
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public Prova pesquisarTudoProva(Prova prova) throws PersistenciaException {
        StringBuilder queryStr = new StringBuilder();
        queryStr.append("SELECT p from Prova p ")
                .append("JOIN FETCH p.endereco ")
                .append("JOIN FETCH p.listaRepresentanteOrganizacao ro ")
                .append("JOIN FETCH ro.organizacaoProva ")
                .append("JOIN FETCH p.listaRepresentanteFederacao rf ")
                .append("JOIN FETCH rf.federacao ")
                .append("JOIN FETCH p.listaPercurso ")
                .append("JOIN FETCH p.listaLargada ")
                .append("JOIN FETCH p.listaCategoriaDaProva cp ")
                .append("JOIN FETCH cp.categoriaAtleta ca ")
                .append("WHERE p = :prova ")
                .append("ORDER BY ca.nome ");

        TypedQuery<Prova> query = super.getEntityManager().createQuery(queryStr.toString(), Prova.class);
        query.setParameter("prova", prova);
        try {
            return query.getSingleResult();
        } catch (Exception ex) {
            throw new PersistenciaException(ex);
        }
    }
}
