package com.taurus.racingTiming.dao.corrida;

import com.taurus.dao.GenericDAO;
import com.taurus.exception.PersistenciaException;
import com.taurus.racingTiming.entidade.corrida.AtletaInscricao;
import com.taurus.racingTiming.entidade.corrida.CategoriaDaProva;
import com.taurus.racingTiming.entidade.corrida.Percurso;
import com.taurus.racingTiming.entidade.corrida.Prova;
import com.taurus.racingTiming.pojo.ImportacaoInscricao;
import com.taurus.racingTiming.pojo.ProvaNumeracaoRepetida;
import com.taurus.racingTiming.util.ListaConstantes;
import com.taurus.util.ListaConstantesBase;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Diego Lima
 */
@Repository
public class AtletaInscricaoDAO extends GenericDAO<AtletaInscricao> implements IAtletaInscricaoDAO<AtletaInscricao> {

    public AtletaInscricaoDAO() {
        super(AtletaInscricao.class);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<AtletaInscricao> pesquisarAtletasClassificadosSemCronometragem(Prova prova) throws PersistenciaException {
        StringBuilder queryStr = new StringBuilder();
        queryStr.append("SELECT a from AtletaInscricao a ")
                .append("JOIN a.categoria cp ")
                .append("LEFT JOIN a.listaCronometragem c ")
                .append("WHERE cp.prova = :prova ")
                .append("AND a.statusCorrida <> ").append(ListaConstantes.StatusAtletaCorrida.DESCLASSIFICADO.ordinal())
                .append(" AND c IS null ");

        TypedQuery<AtletaInscricao> query = super.getEntityManager().createQuery(queryStr.toString(), AtletaInscricao.class);
        query.setParameter("prova", prova);
        try {
            return query.getResultList();
        } catch (Exception ex) {
            throw new PersistenciaException(ex);
        }
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<AtletaInscricao> pesquisarAtletasComCronometragemAtivaAnteriorLargada(Prova prova) throws PersistenciaException {
        StringBuilder queryStr = new StringBuilder();
        queryStr.append("SELECT a from AtletaInscricao a ")
                .append("JOIN FETCH a.categoria cp ")
                .append("JOIN FETCH cp.largada l ")
                .append("LEFT JOIN a.listaCronometragem c ")
                .append("WHERE cp.prova = :prova ")
                .append("AND a.statusCorrida <> ").append(ListaConstantes.StatusAtletaCorrida.DESCLASSIFICADO.ordinal())
                .append(" AND c.excluida = 0 ")
                .append("AND c.horaRegistro < l.horaInicio ")
                .append("GROUP BY a ")
                .append("ORDER BY a.numeroAtleta");

        TypedQuery<AtletaInscricao> query = super.getEntityManager().createQuery(queryStr.toString(), AtletaInscricao.class);
        query.setParameter("prova", prova);
        try {
            return query.getResultList();
        } catch (Exception ex) {
            throw new PersistenciaException(ex);
        }
    }

    /**
     * A classificação deve estar com os dados consolidados inseridos em
     * atleta_inscricao, nao eh necessario calcular sobre o tempo de
     * cronometragem
     *
     * @param percurso
     * @param sexo
     * @return
     * @throws PersistenciaException
     * @deprecated
     */
    @Override
    @Deprecated
    public AtletaInscricao pesquisarPrimeiroGeral(Percurso percurso, ListaConstantesBase.Sexo sexo) throws PersistenciaException {
        StringBuilder queryStr = new StringBuilder();
        queryStr.append("SELECT a from AtletaInscricao a ")
                .append("JOIN a.inscricao i ")
                .append("JOIN i.listaCronometragem c ")
                .append("WHERE c.horaRegistro = (")
                .append("   SELECT MIN(c2.horaRegistro) ")
                .append("   FROM Cronometragem c2 ")
                .append("   WHERE c2.horaRegistro IN ( ")
                .append("       SELECT MAX(c3.horaRegistro) ")
                .append("       FROM Cronometragem c3 ")
                .append("       JOIN c3.inscricao i2 ")
                .append("       JOIN i2.atletaInscricao a2 ")
                .append("       JOIN i2.categoriaDaProva cp ")
                .append("       JOIN cp.categoriaAtleta ca ")
                .append("       WHERE a2.statusCorrida <> ").append(ListaConstantes.StatusAtletaCorrida.DESCLASSIFICADO.ordinal())
                .append("       AND c3.excluida = 0 ")
                .append("       AND cp.percurso = :percurso ")
                .append("       AND ca.sexo = :sexo ")
                .append("       GROUP BY c3.inscricao)) ");

        TypedQuery<AtletaInscricao> query = super.getEntityManager().createQuery(queryStr.toString(), AtletaInscricao.class);
        query.setParameter("percurso", percurso);
        query.setParameter("sexo", sexo);
        try {
            return query.getSingleResult();
        } catch (Exception ex) {
            throw new PersistenciaException(ex);
        }
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<ImportacaoInscricao> pesquisarImportacaoInscricao(Prova prova) throws PersistenciaException {
        StringBuilder queryStr = new StringBuilder();
        queryStr.append("SELECT a.dataImportacao, count(a) from AtletaInscricao a ")
                .append("JOIN a.categoria cp ")
                .append("WHERE cp.prova = :prova ")
                .append("AND a.dataImportacao is not null ")
                .append("GROUP BY a.dataImportacao ")
                .append("ORDER BY a.dataImportacao ");

        Query query = super.getEntityManager().createQuery(queryStr.toString());
        query.setParameter("prova", prova);
        try {
            List resultado = query.getResultList();
            List<ImportacaoInscricao> importacoes = new ArrayList();
            Iterator it = resultado.iterator();
            while (it.hasNext()) {
                Object[] objetos = (Object[]) it.next();
                importacoes.add(new ImportacaoInscricao(prova, (LocalDateTime) objetos[0], (Long) objetos[1]));
            }
            return importacoes;
        } catch (Exception ex) {
            throw new PersistenciaException(ex);
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public int excluirImportacaoInscricao(ImportacaoInscricao importacaoInscricao) throws PersistenciaException {
        StringBuilder queryStr = new StringBuilder();
        queryStr.append("DELETE FROM AtletaInscricao a ")
                .append("WHERE a.dataImportacao = :data ")
                .append("AND a.categoria in ( ")
                .append("SELECT c FROM CategoriaDaProva c WHERE c.prova = :prova) ");

        Query query = super.getEntityManager().createQuery(queryStr.toString());
        query.setParameter("prova", importacaoInscricao.getProva());
        query.setParameter("data", importacaoInscricao.getDataImportacao());
        try {
            return query.executeUpdate();
        } catch (Exception ex) {
            throw new PersistenciaException(ex);
        }
    }

    @Override
    public Integer getMaiorNumeracaoCategoria(CategoriaDaProva categoriaDaProva) throws PersistenciaException {
        StringBuilder queryStr = new StringBuilder();
        queryStr.append("SELECT MAX(a.numeroAtleta) from AtletaInscricao a ")
                .append("WHERE a.categoria = :categoria");

        Query query = super.getEntityManager().createQuery(queryStr.toString());
        query.setParameter("categoria", categoriaDaProva);
        try {
            return (Integer) query.getSingleResult();
        } catch (Exception ex) {
            throw new PersistenciaException(ex);
        }
    }

    @Override
    public Integer getMaiorNumeracaoSequencialProva(Prova prova) throws PersistenciaException {
        StringBuilder queryStr = new StringBuilder();
        queryStr.append("SELECT MAX(a.numeroAtleta) from AtletaInscricao a ")
                .append("JOIN a.categoria cp ")
                .append("WHERE cp.prova = :prova ")
                .append("AND cp.numeracaoAutomatica = ").append(ListaConstantes.NumeracaoAutomatica.SEQUENCIAL.ordinal());

        Query query = super.getEntityManager().createQuery(queryStr.toString());
        query.setParameter("prova", prova);
        try {
            return (Integer) query.getSingleResult();
        } catch (Exception ex) {
            throw new PersistenciaException(ex);
        }
    }

    @Override
    public void atualizarNumeroAtleta(AtletaInscricao atletaInscricao) throws PersistenciaException {
        StringBuilder queryStr = new StringBuilder();
        queryStr.append("UPDATE AtletaInscricao ")
                .append("SET numeroAtleta = :numero ")
                .append("WHERE id = :id ");

        Query query = super.getEntityManager().createQuery(queryStr.toString());
        query.setParameter("numero", atletaInscricao.getNumeroAtleta());
        query.setParameter("id", atletaInscricao.getId());
        try {
            query.executeUpdate();
        } catch (Exception ex) {
            throw new PersistenciaException(ex);
        }
    }

    @Override
    public List<ProvaNumeracaoRepetida> pesquisarNumeracaoRepetidaProva(Prova prova) throws PersistenciaException {
        StringBuilder queryStr = new StringBuilder();
        queryStr.append("SELECT a.numeroAtleta, COUNT(a.id) AS repeticoes from AtletaInscricao a ")
                .append("JOIN a.categoria cp ")
                .append("WHERE cp.prova = :prova AND a.numeroAtleta IS NOT NULL ")
                .append("GROUP BY a.numeroAtleta ")
                .append("HAVING COUNT(a.id) > 1 ");

        Query query = super.getEntityManager().createQuery(queryStr.toString());
        query.setParameter("prova", prova);
        try {
            List resultados = query.getResultList();
            if (resultados.isEmpty()) {
                return resultados;
            } else {
                List<ProvaNumeracaoRepetida> listaNumeracaoRepetida = new ArrayList<>();
                Iterator it = resultados.iterator();
                while (it.hasNext()) {
                    Object[] objetos = (Object[]) it.next();
                    listaNumeracaoRepetida.add(new ProvaNumeracaoRepetida(prova, (Integer) objetos[0], ((Long) objetos[1]).intValue()));
                }
                return listaNumeracaoRepetida;
            }
        } catch (Exception ex) {
            throw new PersistenciaException(ex);
        }
    }

    @Override
    public Long pesquisarTotalAtletaCompletouProva(CategoriaDaProva cdp) throws PersistenciaException {
        StringBuilder queryStr = new StringBuilder();
        queryStr.append("SELECT COUNT(a) from AtletaInscricao a ")
                .append("WHERE a.categoria = :categoria ")
                .append("AND (a.statusCorrida = ").append(ListaConstantes.StatusAtletaCorrida.COMPLETOU.ordinal())
                .append(" OR a.statusCorrida = ").append(ListaConstantes.StatusAtletaCorrida.VOLTA_A_MAIS.ordinal())
                .append(")");

        Query query = super.getEntityManager().createQuery(queryStr.toString());
        query.setParameter("categoria", cdp);
        try {
            return (Long) query.getSingleResult();
        } catch (Exception ex) {
            throw new PersistenciaException(ex);
        }
    }
}
