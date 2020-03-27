package com.taurus.racingTiming.dao.corrida;

import com.taurus.dao.IGenericDAO;
import com.taurus.exception.PersistenciaException;
import com.taurus.racingTiming.entidade.corrida.Prova;
import com.taurus.racingTiming.entidade.responsavel.OrganizacaoProva;
import com.taurus.racingTiming.util.ListaConstantes;
import com.taurus.racingTiming.util.ListaConstantes.StatusProva;
import java.time.LocalDate;
import java.util.List;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Diego
 * @param <T>
 */
public interface IProvaDAO<T extends Prova> extends IGenericDAO<T> {

    @Transactional(propagation = Propagation.SUPPORTS)
    List<Prova> pesquisarProva(String nomeProva, LocalDate dataInicial, LocalDate dataFinal, OrganizacaoProva organizacaoProva, List<StatusProva> listaStatusProva, int pagina, int registrosPorPagina) throws PersistenciaException;

    @Transactional(propagation = Propagation.SUPPORTS)
    Long pesquisarTotalProva(String nomeProva, LocalDate dataInicial, LocalDate dataFinal, OrganizacaoProva organizacaoProva, List<StatusProva> listaStatusProva) throws PersistenciaException;

    @Transactional(propagation = Propagation.SUPPORTS)
    Prova pesquisarTudoProva(Prova prova) throws PersistenciaException;
}
