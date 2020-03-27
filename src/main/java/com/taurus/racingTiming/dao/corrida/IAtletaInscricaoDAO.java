package com.taurus.racingTiming.dao.corrida;

import com.taurus.dao.IGenericDAO;
import com.taurus.exception.PersistenciaException;
import com.taurus.racingTiming.entidade.corrida.AtletaInscricao;
import com.taurus.racingTiming.entidade.corrida.CategoriaDaProva;
import com.taurus.racingTiming.entidade.corrida.Percurso;
import com.taurus.racingTiming.entidade.corrida.Prova;
import com.taurus.racingTiming.pojo.ImportacaoInscricao;
import com.taurus.racingTiming.pojo.ProvaNumeracaoRepetida;
import com.taurus.util.ListaConstantesBase;
import java.util.List;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Diego
 * @param <T>
 */
public interface IAtletaInscricaoDAO<T extends AtletaInscricao> extends IGenericDAO<T> {

    @Transactional(propagation = Propagation.SUPPORTS)
    public List<AtletaInscricao> pesquisarAtletasClassificadosSemCronometragem(Prova prova) throws PersistenciaException;

    @Transactional(propagation = Propagation.SUPPORTS)
    public List<AtletaInscricao> pesquisarAtletasComCronometragemAtivaAnteriorLargada(Prova prova) throws PersistenciaException;

    @Transactional(propagation = Propagation.SUPPORTS)
    public AtletaInscricao pesquisarPrimeiroGeral(Percurso percurso, ListaConstantesBase.Sexo sexo) throws PersistenciaException;

    @Transactional(propagation = Propagation.SUPPORTS)
    public List pesquisarImportacaoInscricao(Prova prova) throws PersistenciaException;

    @Transactional(propagation = Propagation.REQUIRED)
    public int excluirImportacaoInscricao(ImportacaoInscricao importacaoInscricao) throws PersistenciaException;

    @Transactional(propagation = Propagation.SUPPORTS)
    public Integer getMaiorNumeracaoCategoria(CategoriaDaProva categoriaDaProva) throws PersistenciaException;

    @Transactional(propagation = Propagation.SUPPORTS)
    public Integer getMaiorNumeracaoSequencialProva(Prova prova) throws PersistenciaException;

    @Transactional(propagation = Propagation.REQUIRED)
    public void atualizarNumeroAtleta(AtletaInscricao atletaInscricao) throws PersistenciaException;

    @Transactional(propagation = Propagation.SUPPORTS)
    public List<ProvaNumeracaoRepetida> pesquisarNumeracaoRepetidaProva(Prova prova) throws PersistenciaException;

    @Transactional(propagation = Propagation.SUPPORTS)
    public Long pesquisarTotalAtletaCompletouProva(CategoriaDaProva cdp) throws PersistenciaException;
}
