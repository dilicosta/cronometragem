package com.taurus.racingTiming.negocio;

import com.taurus.dao.ParametroPesquisa;
import com.taurus.exception.AvisoNegocioException;
import com.taurus.exception.NegocioException;
import com.taurus.racingTiming.entidade.atleta.Atleta;
import com.taurus.racingTiming.entidade.corrida.AtletaInscricao;
import com.taurus.racingTiming.entidade.corrida.CategoriaDaProva;
import com.taurus.racingTiming.entidade.corrida.Largada;
import com.taurus.racingTiming.entidade.corrida.Percurso;
import com.taurus.racingTiming.entidade.corrida.Prova;
import com.taurus.racingTiming.entidade.responsavel.OrganizacaoProva;
import com.taurus.racingTiming.entidade.responsavel.RepresentanteOrganizacaoProva;
import com.taurus.racingTiming.pojo.ImportacaoInscricao;
import com.taurus.racingTiming.util.ListaConstantes;
import com.taurus.racingTiming.util.ListaConstantes.StatusProva;
import com.taurus.util.Pagina;
import java.time.LocalDate;
import java.util.List;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Classe de negocio responsavel pelos cadastros de atletas, inscricoes, provas,
 * percursos e largadas
 *
 * @author Diego Lima
 */
public interface ISecretario {

    /**
     * **********************************************************************
     * *************************** ATLETA ***********************************
     * **********************************************************************
     */
    /**
     * Cria um novo registro de Atleta
     *
     * @param atleta objeto que representa um Atleta
     * @return objeto Atleta instanciado
     * @throws NegocioException
     * @throws com.taurus.exception.AvisoNegocioException
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Atleta criarNovoAtleta(Atleta atleta) throws NegocioException, AvisoNegocioException;

    /**
     * Atualiza um registro de Atleta
     *
     * @param atleta objeto que representa um Atleta
     * @return Atleta
     * @throws NegocioException
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Atleta atualizarAtleta(Atleta atleta) throws NegocioException;

    /**
     * Exclui o registro do Atleta caso nao exista nenhum impedimento
     *
     * @param atleta Objeto que representa o Atleta
     * @throws NegocioException
     * @throws AvisoNegocioException Caso exista algum impedimento para exclusao
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void excluirAtleta(Atleta atleta) throws NegocioException, AvisoNegocioException;

    @Transactional(propagation = Propagation.SUPPORTS)
    public List<AtletaInscricao> pesquisarAtletaPorStatusCategoria(Prova prova, ListaConstantes.StatusAtletaCorrida statusAtletaInscricao, CategoriaDaProva categoriaDaProva, Pagina pagina) throws NegocioException;

    /**
     * Pesquisa paginada por Atleta
     *
     * @param filtroNome nome ou parte do nome do atleta
     * @param filtroCpf cpf do atleta
     * @param pagina
     * @return Lista de atletas correspondentes aos filtros
     * @throws NegocioException
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    public List<Atleta> pesquisarAtleta(String filtroNome, String filtroCpf, Pagina pagina) throws NegocioException;

    @Transactional(propagation = Propagation.SUPPORTS)
    public List<AtletaInscricao> pesquisarAtletaInscricaoPorNome(Prova prova, String nome, Pagina pagina) throws NegocioException;

    @Transactional(propagation = Propagation.SUPPORTS)
    public List<AtletaInscricao> pesquisarAtletaInscricaoPorParametros(List<ParametroPesquisa> parametros, boolean ordemCrescente, String... atributosOrdenacao) throws NegocioException;

    @Transactional(propagation = Propagation.SUPPORTS)
    public List<AtletaInscricao> pesquisarAtletaInscricaoPorParametrosPaginada(List<ParametroPesquisa> parametros, Pagina pagina, boolean ordemCrescente, String... atributosOrdenacao) throws NegocioException;

    @Transactional(propagation = Propagation.SUPPORTS)
    public List<AtletaInscricao> pesquisarAtletaInscricaoSemDupla(Prova prova, Pagina pagina) throws NegocioException;

    public List<AtletaInscricao> pesquisarAtletaInscricaoSemNumero(Prova prova, Pagina pagina) throws NegocioException;

    @Transactional(propagation = Propagation.SUPPORTS)
    public List<AtletaInscricao> pesquisarAtletaInscricaoSemNumero(CategoriaDaProva categoriaDaProva) throws NegocioException;

    @Transactional(propagation = Propagation.SUPPORTS)
    public Atleta pesquisarAtletaPorCpf(String cpf) throws NegocioException;

    /**
     * **********************************************************************
     * *************************** PROVA ***********************************
     * **********************************************************************
     */
    /**
     * Cria um novo registro de Prova
     *
     * @param prova objeto que representa uma Prova
     * @return objeto Prova instanciado
     * @throws NegocioException
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Prova criarNovoProva(Prova prova) throws NegocioException;

    /**
     * Atualiza um registro de Prova
     *
     * @param prova objeto que representa uma Prova
     * @return Prova
     * @throws NegocioException
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Prova atualizarProva(Prova prova) throws NegocioException;

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Prova atualizarProvaComExclusao(Prova prova, List<RepresentanteOrganizacaoProva> listaApagarRepresentanteProva) throws NegocioException;

    /**
     * Exclui o registro da Prova caso nao exista nenhum impedimento
     *
     * @param prova Objeto que representa a Prova
     * @throws NegocioException
     * @throws AvisoNegocioException Caso exista algum impedimento para exclusao
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void excluirProva(Prova prova) throws NegocioException, AvisoNegocioException;

    /**
     * Pesquisa paginada das Provas
     *
     * @param nomeProva
     * @param dataInicial
     * @param dataFinal
     * @param organizacaoProva
     * @param statusProva
     * @param pagina
     * @return Lista de provas correspondentes aos filtros
     * @throws NegocioException
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    public List<Prova> pesquisarProva(String nomeProva, LocalDate dataInicial, LocalDate dataFinal, OrganizacaoProva organizacaoProva, StatusProva statusProva, Pagina pagina) throws NegocioException;

    @Transactional(propagation = Propagation.SUPPORTS)
    public List<Prova> pesquisarProva(String nomeProva, LocalDate dataInicial, LocalDate dataFinal, OrganizacaoProva organizacaoProva, List<StatusProva> listaStatusProva, Pagina pagina) throws NegocioException;

    @Transactional(propagation = Propagation.SUPPORTS)
    public Prova pesquisarProvaPorId(Long id) throws NegocioException;

    @Transactional(propagation = Propagation.SUPPORTS)
    public Prova carregarTudoProva(Prova provaEdicao) throws NegocioException;

    /**
     * Cria um novo registro de Inscricao
     *
     * @param atletaInscricao objeto que representa uma Inscricao
     * @return objeto Inscricao instanciado
     * @throws NegocioException
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public AtletaInscricao criarNovoAtletaInscricao(AtletaInscricao atletaInscricao) throws NegocioException;

    /**
     * Exclui o registro da Inscricao caso nao exista nenhum impedimento
     *
     * @param atletaInscricao Objeto que representa a Inscricao
     * @throws NegocioException
     * @throws AvisoNegocioException Caso exista algum impedimento para exclusao
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void excluirAtletaInscricao(AtletaInscricao atletaInscricao) throws NegocioException, AvisoNegocioException;

    /**
     * Pesquisa paginada das Inscricaos
     *
     * @param nomeAtleta
     * @param cpfAtleta
     * @param nomeProva
     * @param dataInicialProva
     * @param dataFinalProva
     * @param pagina
     * @return Lista de inscricaos correspondentes aos filtros
     * @throws NegocioException
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    public List<AtletaInscricao> pesquisarAtletaInscricao(String nomeAtleta, String cpfAtleta, String nomeProva, LocalDate dataInicialProva, LocalDate dataFinalProva, Pagina pagina) throws NegocioException;

    /**
     * Carrega todos os dados do Bean Inscricao
     *
     * @param atletaInscricao
     * @return Bena com todos os atributos preenchidos
     * @throws NegocioException
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public AtletaInscricao carregarTudoAtletaInscricao(AtletaInscricao atletaInscricao) throws NegocioException;

    @Transactional(propagation = Propagation.SUPPORTS)
    public List<CategoriaDaProva> pesquisarCategoriaDaProva(Prova prova) throws NegocioException;

    @Transactional(propagation = Propagation.SUPPORTS)
    public void inserirNumeracaoAutomatica(Prova prova, boolean substituirNumeracaoExistente) throws NegocioException, AvisoNegocioException;

    @Transactional(propagation = Propagation.SUPPORTS)
    public void inserirNumeracaoAutomatica(List<CategoriaDaProva> listaCategoriaDaProva, boolean substituirNumeracaoExistente) throws NegocioException, AvisoNegocioException;

    @Transactional(propagation = Propagation.SUPPORTS)
    public Integer obterProximaNumeracaoInscricao(CategoriaDaProva categoriaDaProva) throws AvisoNegocioException, NegocioException;

    @Transactional(propagation = Propagation.SUPPORTS)
    public AtletaInscricao pesquisarAtletaInscricao(Prova prova, Atleta atleta) throws NegocioException;

    @Transactional(propagation = Propagation.SUPPORTS)
    public AtletaInscricao pesquisarAtletaInscricaoPorNumeroAtleta(Prova prova, Integer numeroAtleta) throws NegocioException;

    @Transactional(propagation = Propagation.SUPPORTS)
    public List<AtletaInscricao> pesquisarAtletaInscricao(String nomeAtleta, CategoriaDaProva categoriaDaProva, Pagina pagina) throws NegocioException;

    @Transactional(propagation = Propagation.SUPPORTS)
    public List<AtletaInscricao> pesquisarAtletaInscricao(CategoriaDaProva categoriaDaProva) throws NegocioException;

    @Transactional(propagation = Propagation.SUPPORTS)
    public List<AtletaInscricao> pesquisarAtletaInscricao(CategoriaDaProva categoriaDaProva, Pagina pagina) throws NegocioException;

    @Transactional(propagation = Propagation.SUPPORTS)
    public List<AtletaInscricao> pesquisarAtletaInscricaoOrdenadoPorNumero(CategoriaDaProva categoriaDaProva, Pagina pagina) throws NegocioException;

    @Transactional(propagation = Propagation.SUPPORTS)
    public AtletaInscricao pesquisarAtletaInscricao(CategoriaDaProva categoriaDaProva, String cpf) throws NegocioException;

    @Transactional(propagation = Propagation.SUPPORTS)
    public Long pesquisarQuantidadeInscricoes(Prova prova) throws NegocioException;

    @Transactional(propagation = Propagation.SUPPORTS)
    public Long pesquisarTotalInscricoes(CategoriaDaProva categoriaDaProva) throws NegocioException;

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public boolean finalizarInscricaoProva(Prova prova) throws NegocioException, AvisoNegocioException;

    @Transactional(propagation = Propagation.REQUIRED)
    public boolean verificarPendenciasInscricoesProva(Prova prova) throws NegocioException;

    @Transactional(propagation = Propagation.SUPPORTS)
    public List<Largada> pesquisarLargada(Prova prova) throws NegocioException;

    @Transactional(propagation = Propagation.SUPPORTS)
    public List<Percurso> pesquisarPercurso(Prova prova) throws NegocioException;

    @Transactional(propagation = Propagation.REQUIRED)
    public AtletaInscricao atualizarAtletaInscricao(AtletaInscricao atletaInscricao) throws NegocioException;

    @Transactional(propagation = Propagation.SUPPORTS)
    public CategoriaDaProva pesquisarCategoriaDaProvaPorNome(Prova prova, String nomeCategoria) throws NegocioException;

    @Transactional(propagation = Propagation.REQUIRED)
    public CategoriaDaProva criarNovoCategoriaDaProva(CategoriaDaProva cdp) throws NegocioException;

    @Transactional(propagation = Propagation.REQUIRED)
    public CategoriaDaProva atualizarCategoriaDaProva(CategoriaDaProva cdp) throws NegocioException;

    @Transactional(propagation = Propagation.SUPPORTS)
    public List<ImportacaoInscricao> pesquisarImportacaoInscricaoProva(Prova prova) throws NegocioException;

    @Transactional(propagation = Propagation.REQUIRED)
    public int excluirImportacaoInscricaoProva(ImportacaoInscricao importacaoInscricao) throws NegocioException, AvisoNegocioException;

    @Transactional(propagation = Propagation.SUPPORTS)
    public List<CategoriaDaProva> pesquisarCategoriaDupla(Prova prova) throws NegocioException;

    @Transactional(propagation = Propagation.SUPPORTS)
    public Long pesquisarTotalAtletaSemNumero(CategoriaDaProva categoriaDaProva) throws NegocioException;

    @Transactional(propagation = Propagation.SUPPORTS)
    public List<AtletaInscricao> pesquisarAtletaInscricaoNumeracaoRepetida(Prova prova, Pagina pagina) throws NegocioException;

    @Transactional(propagation = Propagation.SUPPORTS)
    public Prova criarProvaDemo(int totalPercurso, int totalVolta, int totalCategoriaMasculina, int totalCategoriaFeminina, int totalCategoriaDupla) throws AvisoNegocioException, NegocioException;

    @Transactional(propagation = Propagation.REQUIRED)
    public AtletaInscricao criarAtletaDemo(Prova prova, Integer numeroAtleta) throws NegocioException, AvisoNegocioException;

}
