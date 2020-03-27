package com.taurus.racingTiming.negocio;

import com.taurus.dao.ParametroPesquisa;
import com.taurus.exception.AvisoNegocioException;
import com.taurus.exception.NegocioException;
import com.taurus.racingTiming.entidade.corrida.AtletaInscricao;
import com.taurus.racingTiming.entidade.corrida.CategoriaDaProva;
import com.taurus.racingTiming.entidade.corrida.Cronometragem;
import com.taurus.racingTiming.entidade.corrida.Largada;
import com.taurus.racingTiming.entidade.corrida.Percurso;
import com.taurus.racingTiming.entidade.corrida.Prova;
import com.taurus.racingTiming.pojo.CategoriaProvaResumo;
import com.taurus.racingTiming.util.ListaConstantes.PendenciaProvaCrono;
import com.taurus.racingTiming.util.ListaConstantes.StatusAtletaCorrida;
import com.taurus.racingTiming.util.ListaConstantes.StatusCronometragem;
import com.taurus.util.ListaConstantesBase;
import com.taurus.util.Pagina;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Classe responsavel pela cronometragem da prova
 *
 * @author Diego
 */
public interface IRolex {

    @Transactional(propagation = Propagation.SUPPORTS)
    public List<CategoriaProvaResumo> getListaAtletasCorrendo(Prova prova) throws NegocioException;

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Largada atualizarHorarioLargada(Prova prova, Largada largada) throws NegocioException;

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Cronometragem criarNovoCronometragemComVerificacaoTempoRelacionado(Cronometragem cronometragem) throws NegocioException;

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Cronometragem atualizarCronometragemComVerificacaoTempoRelacionado(Cronometragem cronometragem) throws NegocioException;

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Cronometragem ativarCronometragem(Cronometragem cronometragem) throws NegocioException;

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void excluirCronometragemFisica(Cronometragem cronometragem) throws NegocioException;

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Cronometragem excluirCronometragem(Cronometragem cronometragem) throws NegocioException;

    @Transactional(propagation = Propagation.SUPPORTS, rollbackFor = Exception.class)
    public List<Cronometragem> pesquisarUltimasCronometragensProva(Prova prova, Pagina pagina) throws NegocioException;

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Prova encerrarCronometragem(Prova prova) throws NegocioException, AvisoNegocioException;

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Prova verificarInconsitenciasCronometragem(Prova prova) throws NegocioException, AvisoNegocioException;

    @Transactional(propagation = Propagation.SUPPORTS, rollbackFor = Exception.class)
    public List<Cronometragem> pesquisarCronometragemPorPercursoSexoStatus(Prova prova, Percurso percurso, ListaConstantesBase.Sexo sexo, StatusCronometragem statusCrono, Pagina pagina) throws NegocioException;

    @Transactional(propagation = Propagation.SUPPORTS, rollbackFor = Exception.class)
    public List<Cronometragem> pesquisarCronometragemPorCategoriaStatus(CategoriaDaProva categoriaDaProva, StatusCronometragem statusCrono, Pagina pagina) throws NegocioException;

    @Transactional(propagation = Propagation.SUPPORTS, rollbackFor = Exception.class)
    public List<Cronometragem> pesquisarCronometragemPorNumeroAtletaPeriodoStatus(Prova prova, Integer numeroAtleta, LocalDateTime horaInicio, LocalDateTime horaFim, StatusCronometragem statusCrono, Pagina pagina) throws NegocioException;

    @Transactional(propagation = Propagation.SUPPORTS)
    public List<Cronometragem> pesquisarCronometragemPorParametro(List<ParametroPesquisa> parametros, boolean ordemCrescente, String... atributosOrdenacao) throws NegocioException;

    @Transactional(propagation = Propagation.SUPPORTS)
    public List<Cronometragem> pesquisarCronometragemPorAtletaStatus(AtletaInscricao atletaInscricao, StatusCronometragem... listaStatusCronometragem) throws NegocioException;

    @Transactional(propagation = Propagation.SUPPORTS)
    public List<Cronometragem> pesquisarCronometragemPorNumeroAtleta(Prova prova, Integer numeroAtleta) throws NegocioException;

    @Transactional(propagation = Propagation.SUPPORTS)
    public List<Cronometragem> pesquisarCronometragemPorNumeroAtletaStatus(Prova prova, Integer numeroAtleta, StatusCronometragem statusCronometragem) throws NegocioException;

    @Transactional(propagation = Propagation.SUPPORTS, rollbackFor = Exception.class)
    public Set<PendenciaProvaCrono> calcularTempoVoltaProvaEInconsistencias(Prova prova) throws NegocioException;

    @Transactional(propagation = Propagation.SUPPORTS, rollbackFor = Exception.class)
    public Set<PendenciaProvaCrono> calcularTempoVoltaAtletaEInconsistencias(AtletaInscricao atletaInscricao) throws NegocioException;

    @Transactional(propagation = Propagation.SUPPORTS, rollbackFor = Exception.class)
    public Set<PendenciaProvaCrono> calcularTempoVoltaAtletaEInconsistencias(List<Cronometragem> listaCronoPorAtleta) throws NegocioException;

    @Transactional(propagation = Propagation.SUPPORTS)
    public List<AtletaInscricao> pesquisarAtletaProva(Prova prova, StatusAtletaCorrida statusAtleta) throws NegocioException;

    @Transactional(propagation = Propagation.SUPPORTS)
    public List<AtletaInscricao> pesquisarAtletaProvaComCronometragemAtivaAnteriorLargada(Prova prova) throws NegocioException;

    @Transactional(propagation = Propagation.SUPPORTS)
    public List<Cronometragem> pesquisarCronometragemSemInscricaoStatus(Prova prova, StatusCronometragem statusCronometragem) throws NegocioException;

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void excluirTodasCronometragemAtleta(AtletaInscricao atletaInscricao) throws NegocioException;

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void adicionarAtletaCronometragem(AtletaInscricao atletaInscricao, Prova prova, Integer numeroAtletaDaCronometragem) throws NegocioException;

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Cronometragem trocarCronometragemAtleta(AtletaInscricao atletaInscricaoAntiga, Cronometragem crono) throws NegocioException;

    @Transactional(propagation = Propagation.SUPPORTS)
    public boolean carregarImagemCronometragem(Cronometragem cronometragem) throws NegocioException;

}
