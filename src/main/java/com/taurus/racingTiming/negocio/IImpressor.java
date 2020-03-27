package com.taurus.racingTiming.negocio;

import com.taurus.exception.NegocioException;
import com.taurus.racingTiming.entidade.corrida.CategoriaDaProva;
import com.taurus.racingTiming.entidade.corrida.Percurso;
import com.taurus.racingTiming.entidade.corrida.Prova;
import com.taurus.util.ListaConstantesBase;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Diego
 */
public interface IImpressor {

    @Transactional(propagation = Propagation.SUPPORTS)
    public void gerarRelatorioClassificacaoProvaTodasCategorias(Prova prova) throws NegocioException;

    @Transactional(propagation = Propagation.SUPPORTS)
    public byte[] gerarRelatorioClassificacaoProvaCategoria(CategoriaDaProva categoriaDaProva) throws NegocioException;

    @Transactional(propagation = Propagation.SUPPORTS)
    public byte[] gerarRelatorioClassificacaoProvaGeral(Percurso percurso, ListaConstantesBase.Sexo sexo) throws NegocioException;

    @Transactional(propagation = Propagation.SUPPORTS)
    public byte[] gerarRelatorioInscritosPorCategoria(Prova prova, CategoriaDaProva categoriaDaProva, Boolean numeracao, Impressor.OrdenacaoInscritos ordenacaoInscritos) throws NegocioException;

    @Transactional(propagation = Propagation.SUPPORTS)
    public byte[] gerarRelatorioInscritosGeral(Prova prova, Boolean numeracao, Impressor.OrdenacaoInscritos ordenacaoInscritos) throws NegocioException;
}
