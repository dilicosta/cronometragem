package com.taurus.racingTiming.negocio;

import com.taurus.dao.ParametroPesquisa;
import com.taurus.dao.ParametroPesquisa.Operador;
import com.taurus.exception.AvisoNegocioException;
import com.taurus.exception.NegocioException;
import com.taurus.exception.PersistenciaException;
import com.taurus.racingTiming.dao.atleta.IAtletaDAO;
import com.taurus.racingTiming.dao.corrida.IAtletaInscricaoDAO;
import com.taurus.racingTiming.dao.corrida.ICategoriaDaProvaDAO;
import com.taurus.racingTiming.dao.corrida.ILargadaDAO;
import com.taurus.racingTiming.dao.corrida.IPercursoDAO;
import com.taurus.racingTiming.dao.corrida.IProvaDAO;
import com.taurus.racingTiming.dao.responsavel.IRepresentanteFederacaoDAO;
import com.taurus.racingTiming.dao.responsavel.IRepresentanteOrganizacaoProvaDAO;
import com.taurus.racingTiming.entidade.Endereco;
import com.taurus.racingTiming.entidade.atleta.Atleta;
import com.taurus.racingTiming.entidade.atleta.CategoriaAtleta;
import com.taurus.racingTiming.entidade.corrida.AtletaInscricao;
import com.taurus.racingTiming.entidade.corrida.CategoriaDaProva;
import com.taurus.racingTiming.entidade.corrida.Cronometragem;
import com.taurus.racingTiming.entidade.corrida.EnderecoAtletaInscricao;
import com.taurus.racingTiming.entidade.corrida.Largada;
import com.taurus.racingTiming.entidade.corrida.Percurso;
import com.taurus.racingTiming.entidade.corrida.Prova;
import com.taurus.racingTiming.entidade.responsavel.OrganizacaoProva;
import com.taurus.racingTiming.entidade.responsavel.RepresentanteFederacao;
import com.taurus.racingTiming.entidade.responsavel.RepresentanteOrganizacaoProva;
import com.taurus.racingTiming.pojo.ImportacaoInscricao;
import com.taurus.racingTiming.pojo.ProvaNumeracaoRepetida;
import com.taurus.racingTiming.util.ListaConstantes;
import com.taurus.racingTiming.util.ListaConstantes.StatusProva;
import com.taurus.util.ControleMensagem;
import com.taurus.util.FormatarUtil;
import com.taurus.util.GeralUtil;
import com.taurus.util.ListaConstantesBase.Sexo;
import com.taurus.util.Pagina;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Random;
import javax.persistence.PersistenceException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.validator.GenericValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Classe de negocio responsavel pelos cadastros de atletas, inscricoes, provas,
 * percursos e largadas
 *
 * @author Diego Lima
 */
@Component
public class Secretario implements ISecretario {

    private static final Log LOG = LogFactory.getLog(Secretario.class);

    @Autowired
    private IEstagiario estagiario;
    @Autowired
    private IRolex rolex;
    @Autowired
    private IAtletaDAO<Atleta> atletaDAO;
    @Autowired
    private IAtletaInscricaoDAO<AtletaInscricao> atletaInscricaoDAO;
    @Autowired
    private IProvaDAO<Prova> provaDAO;
    @Autowired
    private ICategoriaDaProvaDAO<CategoriaDaProva> catProvaDAO;
    @Autowired
    private ILargadaDAO<Largada> largadaDAO;
    @Autowired
    private IPercursoDAO<Percurso> percursoDAO;
    @Autowired
    private IRepresentanteFederacaoDAO<RepresentanteFederacao> representanteFederacaoDAO;
    @Autowired
    private IRepresentanteOrganizacaoProvaDAO<RepresentanteOrganizacaoProva> representanteOrganizacaoProvaDAO;

    private ControleMensagem cm = ControleMensagem.getInstance();

    /**
     * **********************************************************************
     * *************************** ATLETA ***********************************
     * **********************************************************************
     */
    /**
     * Cria um novo registro de Atleta
     *
     * @param atleta objeto que representa um Atleta
     * @throws NegocioException
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public Atleta criarNovoAtleta(Atleta atleta) throws NegocioException, AvisoNegocioException {
        try {
            if (!GenericValidator.isBlankOrNull(atleta.getCpf())) {
                Atleta atletaExistente = this.pesquisarAtletaPorCpf(atleta.getCpf());
                if (atletaExistente != null) {
                    throw new AvisoNegocioException("Já existe um atleta cadastrado para o cpf informado: atleta \"" + atletaExistente.getNome() + "\"");
                }
            }
            this.atletaDAO.persist(atleta);
            return atleta;
        } catch (PersistenciaException ex) {
            LOG.error("", ex);
            throw new NegocioException(ex);
        }
    }

    /**
     * Atualiza um registro de Atleta
     *
     * @param atleta objeto que representa um Atleta
     * @return Atleta
     * @throws NegocioException
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public Atleta atualizarAtleta(Atleta atleta) throws NegocioException {
        try {
            Atleta at = this.atletaDAO.merge(atleta);
            return at;
        } catch (PersistenciaException ex) {
            LOG.error("", ex);
            throw new NegocioException(ex);
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public AtletaInscricao atualizarAtletaInscricao(AtletaInscricao atletaInscricao) throws NegocioException {
        try {
            return this.atletaInscricaoDAO.merge(atletaInscricao);
        } catch (PersistenciaException ex) {
            LOG.error("", ex);
            throw new NegocioException(ex);
        }
    }

    /**
     * Exclui o registro do Atleta caso nao exista nenhum impedimento
     *
     * @param atleta Objeto que representa o Atleta
     * @throws NegocioException
     * @throws AvisoNegocioException Caso exista algum impedimento para exclusao
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public void excluirAtleta(Atleta atleta) throws NegocioException, AvisoNegocioException {
        try {
            List<ParametroPesquisa> parametros = GeralUtil.criarLista(new ParametroPesquisa("atleta", atleta, Operador.IGUAL));
            if (this.atletaInscricaoDAO.pesquisarTotalRegistrosPesquisaPorParametros(parametros) > 0) {
                throw new AvisoNegocioException(cm.getMensagem("aviso_exclusao_negada_item_associado_item", "atleta", "inscricao"));
            }

            this.atletaDAO.excluir(atleta);
        } catch (PersistenciaException ex) {
            LOG.error("", ex);
            throw new NegocioException(ex);
        }
    }

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
    @Override
    public List<Atleta> pesquisarAtleta(String filtroNome, String filtroCpf, Pagina pagina) throws NegocioException {
        List<ParametroPesquisa> listaParametroPesquisa = new ArrayList();
        if (!GenericValidator.isBlankOrNull(filtroNome)) {
            listaParametroPesquisa.add(new ParametroPesquisa("nome", filtroNome, ParametroPesquisa.Operador.LIKE));
        }
        if (!GenericValidator.isBlankOrNull(filtroCpf)) {
            listaParametroPesquisa.add(new ParametroPesquisa("cpf", filtroCpf, ParametroPesquisa.Operador.IGUAL));
        }
        try {
            if (pagina.getTotalItens() == null) {
                pagina.setTotalItens(this.atletaDAO.pesquisarTotalRegistrosPesquisaPorParametros(listaParametroPesquisa));
            }
            return this.atletaDAO.pesquisarPorParametrosPaginada(listaParametroPesquisa, pagina.getNumeroPagina(), pagina.getItensPorPagina(), true, "nome");
        } catch (PersistenciaException ex) {
            LOG.error("", ex);
            throw new NegocioException(ex);
        }
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<AtletaInscricao> pesquisarAtletaInscricaoPorNome(Prova prova, String nome, Pagina pagina) throws NegocioException {
        List<ParametroPesquisa> listaParametroPesquisa = new ArrayList();
        listaParametroPesquisa.add(new ParametroPesquisa("categoria.prova", prova, ParametroPesquisa.Operador.IGUAL));

        if (!GenericValidator.isBlankOrNull(nome)) {
            listaParametroPesquisa.add(new ParametroPesquisa("atleta.nome", nome, ParametroPesquisa.Operador.LIKE));
        }
        return this.pesquisarAtletaInscricaoPorParametrosPaginada(listaParametroPesquisa, pagina, true, "atleta.nome");
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<AtletaInscricao> pesquisarAtletaInscricaoPorParametros(List<ParametroPesquisa> parametros, boolean ordemCrescente, String... atributosOrdenacao) throws NegocioException {
        try {
            return this.atletaInscricaoDAO.pesquisarPorParametros(parametros, ordemCrescente, atributosOrdenacao);
        } catch (PersistenciaException ex) {
            LOG.error("", ex);
            throw new NegocioException(ex);
        }
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<AtletaInscricao> pesquisarAtletaInscricaoPorParametrosPaginada(List<ParametroPesquisa> parametros, Pagina pagina, boolean ordemCrescente, String... atributosOrdenacao) throws NegocioException {
        try {
            if (pagina.getTotalItens() == null) {
                pagina.setTotalItens(this.atletaInscricaoDAO.pesquisarTotalRegistrosPesquisaPorParametros(parametros));
            }
            return this.atletaInscricaoDAO.pesquisarPorParametrosPaginada(parametros, pagina.getNumeroPagina(), pagina.getItensPorPagina(), ordemCrescente, atributosOrdenacao);
        } catch (PersistenciaException ex) {
            LOG.error("", ex);
            throw new NegocioException(ex);
        }
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public Atleta pesquisarAtletaPorCpf(String cpf) throws NegocioException {
        List<ParametroPesquisa> parametros = GeralUtil.criarLista(new ParametroPesquisa("cpf", cpf, ParametroPesquisa.Operador.IGUAL));
        try {
            List<Atleta> atletas = this.atletaDAO.pesquisarPorParametros(parametros, true);
            if (!atletas.isEmpty()) {
                return atletas.get(0);
            } else {
                return null;
            }
        } catch (PersistenciaException ex) {
            LOG.error("", ex);
            throw new NegocioException(ex);
        }
    }

    /**
     * **********************************************************************
     * *************************** PROVA ***********************************
     * **********************************************************************
     */
    /**
     * Carrega todos os dados do Bean Prova
     *
     * @param prova
     * @return Bena com todos os atributos preenchidos
     * @throws NegocioException
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public Prova carregarTudoProva(Prova prova) throws NegocioException {
        try {
            return this.provaDAO.pesquisarTudoProva(prova);
        } catch (PersistenciaException ex) {
            LOG.error("", ex);
            throw new NegocioException(ex);
        }
    }

    /**
     * Cria um novo registro de Prova
     *
     * @param prova objeto que representa uma Prova
     * @return objeto Prova instanciado
     * @throws NegocioException
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public Prova criarNovoProva(Prova prova) throws NegocioException {
        try {
            List<CategoriaDaProva> listaCategoriaDaProva = new ArrayList<>();
            listaCategoriaDaProva.addAll(prova.getListaCategoriaDaProva());
            prova.getListaCategoriaDaProva().clear();

            Prova p = this.provaDAO.merge(prova);

            for (CategoriaDaProva cdp : listaCategoriaDaProva) {
                cdp.setProva(p);
                for (Largada largada : p.getListaLargada()) {
                    if (largada.equals(cdp.getLargada())) {
                        cdp.setLargada(largada);
                    }
                }
                for (Percurso percurso : p.getListaPercurso()) {
                    if (percurso.equals(cdp.getPercurso())) {
                        cdp.setPercurso(percurso);
                    }
                }
                p.getListaCategoriaDaProva().add(cdp);
            }
            return this.provaDAO.merge(p);
        } catch (PersistenciaException ex) {
            LOG.error("", ex);
            throw new NegocioException(ex);
        }
    }

    /**
     * Atualiza um registro de Prova
     *
     * @param prova objeto que representa uma Prova
     * @return Prova
     * @throws NegocioException
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public Prova atualizarProva(Prova prova) throws NegocioException {
        try {
            return this.provaDAO.merge(prova);
        } catch (PersistenciaException ex) {
            LOG.error("", ex);
            throw new NegocioException(ex);
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public Prova atualizarProvaComExclusao(Prova prova, List<RepresentanteOrganizacaoProva> listaApagarRepresentanteProva) throws NegocioException {
        try {
            Prova p = this.provaDAO.merge(prova);
            return p;
        } catch (PersistenciaException ex) {
            LOG.error("", ex);
            throw new NegocioException(ex);
        }
    }

    /**
     * Exclui o registro da Prova caso nao exista nenhum impedimento
     *
     * @param prova Objeto que representa a Prova
     * @throws NegocioException
     * @throws AvisoNegocioException Caso exista algum impedimento para exclusao
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public void excluirProva(Prova prova) throws NegocioException, AvisoNegocioException {
        if (!prova.isDemo()) {
            switch (prova.getStatus()) {
                case CRONOMETRANDO_PARCIAL_LARGADA:
                case CRONOMETRANDO_TODAS_LARGADAS:
                    throw new AvisoNegocioException(cm.getMensagem("aviso_exclusao_negada_prova_cronometrando"));
                case ENCERRADA_APURANDO_RESULTADOS:
                case ENCERRADA_RESULTADOS_CONCLUIDOS:
                case FINALIZADA:
                    throw new AvisoNegocioException(cm.getMensagem("aviso_exclusao_negada_prova_concluida"));
            }
            try {
                List<ParametroPesquisa> parametros = GeralUtil.criarLista(new ParametroPesquisa("categoria.prova", prova, Operador.IGUAL));
                if (this.atletaInscricaoDAO.pesquisarTotalRegistrosPesquisaPorParametros(parametros) > 0) {
                    throw new AvisoNegocioException(cm.getMensagem("aviso_exclusao_negada_item_associado_item", "prova", "inscricao"));
                }
                //prova = this.provaDAO.atacharEntidade(prova);
                //this.excluirCategoriaDaProva(prova);
                this.provaDAO.excluir(prova);
            } catch (PersistenciaException ex) {
                LOG.error("", ex);
                throw new NegocioException(ex);
            }
        } else {
            this.excluirProvaDemo(prova);
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    private void excluirProvaDemo(Prova prova) throws NegocioException, AvisoNegocioException {
        if (prova.isDemo()) {
            try {
                List<ParametroPesquisa> listaParametros = new ArrayList<>();
                listaParametros.add(new ParametroPesquisa("prova", prova, Operador.IGUAL));
                for (Cronometragem crono : this.rolex.pesquisarCronometragemPorParametro(listaParametros, false)) {
                    this.rolex.excluirCronometragemFisica(crono);
                }

                listaParametros.clear();
                listaParametros.add(new ParametroPesquisa("categoria.prova", prova, Operador.IGUAL));
                for (AtletaInscricao atletaInscricao : this.pesquisarAtletaInscricaoPorParametros(listaParametros, false)) {
                    this.atletaInscricaoDAO.excluir(atletaInscricao);
                    this.atletaDAO.excluir(atletaInscricao.getAtleta());

                }
                this.provaDAO.excluir(prova);
            } catch (PersistenciaException ex) {
                LOG.error("", ex);
                throw new NegocioException(ex);
            }
        }
    }

    /**
     * Pesquisa paginada das Provas
     *
     * @param nomeProva
     * @param dataInicial
     * @param dataFinal
     * @param organizacaoProva
     * @param pagina
     * @return Lista de provas correspondentes aos filtros
     * @throws NegocioException
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<Prova> pesquisarProva(String nomeProva, LocalDate dataInicial, LocalDate dataFinal, OrganizacaoProva organizacaoProva, StatusProva statusProva, Pagina pagina) throws NegocioException {

        List<StatusProva> listaStatusProva = null;
        if (statusProva != null) {
            listaStatusProva = GeralUtil.criarLista(statusProva);
        }
        return this.pesquisarProva(nomeProva, dataInicial, dataFinal, organizacaoProva, listaStatusProva, pagina);
    }

    /**
     * Pesquisa paginada das Provas
     *
     * @param nomeProva
     * @param dataInicial
     * @param dataFinal
     * @param organizacaoProva
     * @param listaStatusProva
     * @param pagina
     * @return
     * @throws NegocioException
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<Prova> pesquisarProva(String nomeProva, LocalDate dataInicial, LocalDate dataFinal, OrganizacaoProva organizacaoProva, List<StatusProva> listaStatusProva, Pagina pagina) throws NegocioException {
        try {
            if (pagina.getTotalItens() == null) {
                pagina.setTotalItens(this.provaDAO.pesquisarTotalProva(nomeProva, dataInicial, dataFinal, organizacaoProva, listaStatusProva));
            }
            if (pagina.getTotalItens() > 0) {
                return this.provaDAO.pesquisarProva(nomeProva, dataInicial, dataFinal, organizacaoProva, listaStatusProva, pagina.getNumeroPagina(), pagina.getItensPorPagina());
            } else {
                return new ArrayList();
            }
        } catch (PersistenciaException ex) {
            LOG.error("", ex);
            throw new NegocioException(ex);
        }
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public Prova pesquisarProvaPorId(Long id) throws NegocioException {
        try {
            return this.provaDAO.pesquisarPorId(id);
        } catch (PersistenciaException ex) {
            LOG.error("", ex);
            throw new NegocioException(ex);
        }
    }

    /**
     * Finaliza as inscricoes para a Prova e verifica as pendencias para a
     * realizacao da corrida
     *
     * @param prova
     * @return true se a prova possui pendencias de cadastros, false caso
     * contrario
     * @throws NegocioException
     * @throws com.taurus.exception.AvisoNegocioException Excecao lancada caso o
     * status da prova nao permita a finalizacao das inscricoes
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public boolean finalizarInscricaoProva(Prova prova) throws NegocioException, AvisoNegocioException {
        if (prova.getStatus().equals(StatusProva.INSCRICAO_ABERTA) || prova.getStatus().equals(StatusProva.INSCRICAO_ENCERRADA_PENDENTE)) {
            boolean possuiPendencias = this.verificarPendenciasInscricoesProva(prova);
            if (possuiPendencias) {
                prova.setStatus(StatusProva.INSCRICAO_ENCERRADA_PENDENTE);
            } else {
                prova.setStatus(StatusProva.INSCRICAO_FECHADA);
            }
            try {
                this.provaDAO.merge(prova);
            } catch (PersistenciaException ex) {
                LOG.error("", ex);
                throw new NegocioException(ex);
            }
            return possuiPendencias;
        } else {
            throw new AvisoNegocioException("As inscrições da prova já foram finalizadas!");
        }
    }

    /**
     * Verifica se o cadastro da prova possui pendencias
     *
     * @param prova
     * @return true se tiver pendencias, false caso contrario
     * @throws NegocioException
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public boolean verificarPendenciasInscricoesProva(Prova prova) throws NegocioException {
        List<ListaConstantes.PendenciaInscricao> pendencias = new ArrayList();

        if (this.pesquisarTotalAtletaSemNumero(prova) > 0) {
            pendencias.add(ListaConstantes.PendenciaInscricao.ATLETA_SEM_NUMERO);
        }
        if (!this.pesquisarNumeracaoRepetida(prova).isEmpty()) {
            pendencias.add(ListaConstantes.PendenciaInscricao.ATLETA_NUMERACAO_REPETIDA);
        }
        if (this.pesquisarTotalAtletaDuplaSemDupla(prova) > 0) {
            pendencias.add(ListaConstantes.PendenciaInscricao.ATLETA_SEM_DUPLA);
        }

        try {
            prova.setListaMotivoPendencia(pendencias);
            prova = this.provaDAO.merge(prova);
            return prova.getMotivoPendencia() != null;
        } catch (PersistenciaException ex) {
            LOG.error("", ex);
            throw new NegocioException(ex);
        }
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    private Long pesquisarTotalAtletaSemNumero(Prova prova) throws NegocioException {
        List<ParametroPesquisa> parametros = new ArrayList();
        parametros.add(new ParametroPesquisa("categoria.prova", prova, Operador.IGUAL));
        parametros.add(new ParametroPesquisa("numeroAtleta", null, Operador.IGUAL));
        try {
            return this.atletaInscricaoDAO.pesquisarTotalRegistrosPesquisaPorParametros(parametros);
        } catch (PersistenciaException ex) {
            LOG.error("", ex);
            throw new NegocioException(ex);
        }
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public Long pesquisarTotalAtletaSemNumero(CategoriaDaProva categoriaDaProva) throws NegocioException {
        List<ParametroPesquisa> parametros = new ArrayList();
        parametros.add(new ParametroPesquisa("categoria", categoriaDaProva, Operador.IGUAL));
        parametros.add(new ParametroPesquisa("numeroAtleta", null, Operador.IGUAL));
        try {
            return this.atletaInscricaoDAO.pesquisarTotalRegistrosPesquisaPorParametros(parametros);
        } catch (PersistenciaException ex) {
            LOG.error("", ex);
            throw new NegocioException(ex);
        }
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    private List<ProvaNumeracaoRepetida> pesquisarNumeracaoRepetida(Prova prova) throws NegocioException {
        try {
            return this.atletaInscricaoDAO.pesquisarNumeracaoRepetidaProva(prova);
        } catch (PersistenciaException ex) {
            LOG.error("", ex);
            throw new NegocioException(ex);
        }
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    private Long pesquisarTotalAtletaDuplaSemDupla(Prova prova) throws NegocioException {
        List<ParametroPesquisa> parametros = new ArrayList();
        parametros.add(new ParametroPesquisa("categoria.prova", prova, Operador.IGUAL));
        parametros.add(new ParametroPesquisa("categoria.categoriaAtleta.categoriaDupla", true, Operador.IGUAL));
        parametros.add(new ParametroPesquisa("dupla", null, Operador.IGUAL));
        try {
            return this.atletaInscricaoDAO.pesquisarTotalRegistrosPesquisaPorParametros(parametros);
        } catch (PersistenciaException ex) {
            LOG.error("", ex);
            throw new NegocioException(ex);
        }
    }

    /**
     * **********************************************************************
     * *************************** LARGADA **********************************
     * **********************************************************************
     */
    /**
     * Retorna uma lista das largadas associadas a prova
     *
     * @param prova
     * @return
     * @throws NegocioException
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<Largada> pesquisarLargada(Prova prova) throws NegocioException {
        List<ParametroPesquisa> parametros = GeralUtil.criarLista(new ParametroPesquisa("prova", prova, Operador.IGUAL));
        try {
            return this.largadaDAO.pesquisarPorParametros(parametros, true, "nome");
        } catch (PersistenciaException ex) {
            LOG.error("", ex);
            throw new NegocioException(ex);
        }
    }

    /**
     * Carrega todos os dados do Bean AtletaInscricao
     *
     * @param atletaInscricao
     * @return Bena com todos os atributos preenchidos
     * @throws NegocioException
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public AtletaInscricao carregarTudoAtletaInscricao(AtletaInscricao atletaInscricao) throws NegocioException {
        //try {
        //return this.inscricaoDAO.pesquisarTudoInscricao(inscricao);
        return null;
        //} catch (PersistenciaException ex) {
        //  LOG.error("", ex);
        //   throw new NegocioException(ex);
        // }
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<AtletaInscricao> pesquisarAtletaPorStatusCategoria(Prova prova, ListaConstantes.StatusAtletaCorrida statusAtletaInscricao, CategoriaDaProva categoriaDaProva, Pagina pagina) throws NegocioException {
        List<ParametroPesquisa> parametros = new ArrayList();
        if (statusAtletaInscricao != null) {
            parametros.add(new ParametroPesquisa("statusCorrida", statusAtletaInscricao, Operador.IGUAL));
        }
        if (categoriaDaProva != null) {
            parametros.add(new ParametroPesquisa("categoria", categoriaDaProva, Operador.IGUAL));
        } else {
            parametros.add(new ParametroPesquisa("categoria.prova", prova, Operador.IGUAL));
        }
        return this.pesquisarAtletaInscricaoPorParametrosPaginada(parametros, pagina, true, "statusCorrida", "numeroAtleta");
    }

    /**
     * Pesquisa uma inscricao a partir do atleta e da prova
     *
     * @param prova
     * @param atleta
     * @return AtletaInscricao instanciada
     * @throws com.taurus.exception.NegocioException
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public AtletaInscricao pesquisarAtletaInscricao(Prova prova, Atleta atleta) throws NegocioException {
        List<ParametroPesquisa> parametros = new ArrayList<>();
        parametros.add(new ParametroPesquisa("categoria.prova", prova, Operador.IGUAL));
        parametros.add(new ParametroPesquisa("atleta", atleta, Operador.IGUAL));
        List<AtletaInscricao> resultado = this.pesquisarAtletaInscricaoPorParametros(parametros, false);
        if (resultado.isEmpty()) {
            return null;
        } else {
            return resultado.get(0);
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public Long pesquisarQuantidadeInscricoes(Prova prova) throws NegocioException {
        try {
            List<ParametroPesquisa> parametros = GeralUtil.criarLista(new ParametroPesquisa("categoria.prova", prova, Operador.IGUAL));
            return this.atletaInscricaoDAO.pesquisarTotalRegistrosPesquisaPorParametros(parametros);
        } catch (PersistenciaException ex) {
            LOG.error("", ex);
            throw new NegocioException(ex);
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public Long pesquisarTotalInscricoes(CategoriaDaProva categoriaDaProva) throws NegocioException {
        try {
            List<ParametroPesquisa> parametros = GeralUtil.criarLista(new ParametroPesquisa("categoria", categoriaDaProva, Operador.IGUAL));
            return this.atletaInscricaoDAO.pesquisarTotalRegistrosPesquisaPorParametros(parametros);
        } catch (PersistenciaException ex) {
            LOG.error("", ex);
            throw new NegocioException(ex);
        }
    }

    /**
     * Cria um novo registro de Inscricao
     *
     * @param atletaInscricao objeto que representa uma Inscricao
     * @throws NegocioException
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public AtletaInscricao criarNovoAtletaInscricao(AtletaInscricao atletaInscricao) throws NegocioException {
        try {
            if (atletaInscricao.getDataInscricao() == null) {
                atletaInscricao.setDataInscricao(LocalDateTime.now());
            }
            atletaInscricao.setEnderecoAtletaInscricao(new EnderecoAtletaInscricao(atletaInscricao.getAtleta().getEndereco()));
            atletaInscricao.setStatusCorrida(ListaConstantes.StatusAtletaCorrida.AGUARDANDO);
            this.atletaInscricaoDAO.persist(atletaInscricao);

            if (atletaInscricao.getDupla() != null) {
                atletaInscricao.getDupla().setDupla(atletaInscricao);
                this.atualizarAtletaInscricao(atletaInscricao.getDupla());
            }

            return atletaInscricao;
        } catch (PersistenciaException ex) {
            LOG.error("", ex);
            throw new NegocioException(ex);
        }
    }

    /**
     * Exclui o registro da Inscricao caso nao exista nenhum impedimento
     *
     * @param atletaInscricao Objeto que representa a Inscricao
     * @throws NegocioException
     * @throws AvisoNegocioException Caso exista algum impedimento para exclusao
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public void excluirAtletaInscricao(AtletaInscricao atletaInscricao) throws NegocioException, AvisoNegocioException {
        try {
            switch (atletaInscricao.getCategoria().getProva().getStatus()) {
                case INSCRICAO_ABERTA:
                    if (atletaInscricao.getDupla() != null) {
                        atletaInscricao.getDupla().setDupla(null);
                        this.atualizarAtletaInscricao(atletaInscricao.getDupla());
                    }
                    this.atletaInscricaoDAO.excluir(atletaInscricao);
                    break;
                default:
                    throw new AvisoNegocioException(this.cm.getMensagem("aviso_exclusao_negada_insc_prova_concluida"));
            }
        } catch (PersistenciaException ex) {
            LOG.error("", ex);
            throw new NegocioException(ex);
        }
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<AtletaInscricao> pesquisarAtletaInscricao(String nomeAtleta, String cpfAtleta, String nomeProva,
            LocalDate dataInicialProva, LocalDate dataFinalProva, Pagina pagina) throws NegocioException {

        List<ParametroPesquisa> parametros = new ArrayList();
        if (!GenericValidator.isBlankOrNull(nomeAtleta)) {
            parametros.add(new ParametroPesquisa("atleta.nome", nomeAtleta, Operador.LIKE));
        }
        if (!GenericValidator.isBlankOrNull(cpfAtleta)) {
            parametros.add(new ParametroPesquisa("atleta.cpf", cpfAtleta, Operador.IGUAL));
        }
        if (!GenericValidator.isBlankOrNull(nomeProva)) {
            parametros.add(new ParametroPesquisa("categoria.prova.nome", nomeProva, Operador.LIKE));
        }
        if (dataInicialProva != null) {
            parametros.add(new ParametroPesquisa("categoria.prova.data", dataInicialProva, Operador.MAIOR_IGUAL));
        }
        if (dataFinalProva != null) {
            parametros.add(new ParametroPesquisa("categoria.prova.data", dataFinalProva, Operador.MENOR_IGUAL));
        }

        return this.pesquisarAtletaInscricaoPorParametrosPaginada(parametros, pagina, false, "dataInscricao");
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public AtletaInscricao pesquisarAtletaInscricaoPorNumeroAtleta(Prova prova, Integer numeroAtleta) throws NegocioException {
        List<AtletaInscricao> resultado = this.pesquisarListaAtletaInscricaoPorNumeroAtleta(prova, numeroAtleta);
        return resultado.isEmpty() ? null : resultado.get(0);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    private List<AtletaInscricao> pesquisarListaAtletaInscricaoPorNumeroAtleta(Prova prova, Integer numeroAtleta) throws NegocioException {
        List<ParametroPesquisa> parametros = GeralUtil.criarLista(new ParametroPesquisa("numeroAtleta", numeroAtleta, Operador.IGUAL));
        parametros.add(new ParametroPesquisa("categoria.prova", prova, Operador.IGUAL));
        return this.pesquisarAtletaInscricaoPorParametros(parametros, false);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<AtletaInscricao> pesquisarAtletaInscricao(String nomeAtleta, CategoriaDaProva categoriaDaProva, Pagina pagina) throws NegocioException {
        List<ParametroPesquisa> parametros = GeralUtil.criarLista(new ParametroPesquisa("atleta.nome", nomeAtleta, Operador.LIKE));
        parametros.add(new ParametroPesquisa("categoria", categoriaDaProva, Operador.IGUAL));
        return this.pesquisarAtletaInscricaoPorParametrosPaginada(parametros, pagina, true, "atleta.nome");
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public AtletaInscricao pesquisarAtletaInscricao(CategoriaDaProva categoriaDaProva, String cpf) throws NegocioException {
        List<ParametroPesquisa> parametros = GeralUtil.criarLista(new ParametroPesquisa("atleta.cpf", cpf, Operador.IGUAL));
        parametros.add(new ParametroPesquisa("categoria", categoriaDaProva, Operador.IGUAL));
        List<AtletaInscricao> resultado = this.pesquisarAtletaInscricaoPorParametros(parametros, false);
        if (resultado.isEmpty()) {
            return null;
        } else {
            return resultado.get(0);
        }
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<AtletaInscricao> pesquisarAtletaInscricao(CategoriaDaProva categoriaDaProva) throws NegocioException {
        return this.pesquisarAtletaInscricao(categoriaDaProva, (Pagina) null);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<AtletaInscricao> pesquisarAtletaInscricao(CategoriaDaProva categoriaDaProva, Pagina pagina) throws NegocioException {
        return this.pesquisarAtletaInscricaoOrdenacaoCustomizada(categoriaDaProva, "atleta.nome", pagina);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<AtletaInscricao> pesquisarAtletaInscricaoOrdenadoPorNumero(CategoriaDaProva categoriaDaProva, Pagina pagina) throws NegocioException {
        return this.pesquisarAtletaInscricaoOrdenacaoCustomizada(categoriaDaProva, "numeroAtleta", pagina);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    private List<AtletaInscricao> pesquisarAtletaInscricaoOrdenacaoCustomizada(CategoriaDaProva categoriaDaProva, String ordenacao, Pagina pagina) throws NegocioException {
        List<ParametroPesquisa> parametros = GeralUtil.criarLista(new ParametroPesquisa("categoria", categoriaDaProva, Operador.IGUAL));
        if (pagina == null) {
            return this.pesquisarAtletaInscricaoPorParametros(parametros, true, ordenacao);
        } else {
            return this.pesquisarAtletaInscricaoPorParametrosPaginada(parametros, pagina, true, ordenacao);
        }
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<CategoriaDaProva> pesquisarCategoriaDaProva(Prova prova) throws NegocioException {
        try {
            return this.catProvaDAO.pesquisarCategoria(prova);
        } catch (PersistenciaException ex) {
            LOG.error("", ex);
            throw new NegocioException(ex);
        }
    }

    @Override
    public List<CategoriaDaProva> pesquisarCategoriaDupla(Prova prova) throws NegocioException {
        List<ParametroPesquisa> parametros = new ArrayList();
        parametros.add(new ParametroPesquisa("prova", prova, Operador.IGUAL));
        parametros.add(new ParametroPesquisa("categoriaAtleta.categoriaAtleta.categoriaDupla", true, Operador.IGUAL));
        return this.pesquisarCategoriaDaProvaPorParametros(parametros, true);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public CategoriaDaProva pesquisarCategoriaDaProvaPorNome(Prova prova, String nomeCategoria) throws NegocioException {
        List<ParametroPesquisa> parametros = new ArrayList();
        parametros.add(new ParametroPesquisa("prova", prova, Operador.IGUAL));
        parametros.add(new ParametroPesquisa("categoriaAtleta.nome", nomeCategoria, Operador.IGUAL));
        List<CategoriaDaProva> resultado = this.pesquisarCategoriaDaProvaPorParametros(parametros, true);
        if (resultado.isEmpty()) {
            return null;
        } else {
            return resultado.get(0);
        }
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    private List<CategoriaDaProva> pesquisarCategoriaDaProvaPorParametros(List<ParametroPesquisa> parametros, boolean ordemCrescente, String... atributosOrdenacao) throws NegocioException {
        try {
            return this.catProvaDAO.pesquisarPorParametros(parametros, ordemCrescente, atributosOrdenacao);
        } catch (PersistenciaException ex) {
            LOG.error("", ex);
            throw new NegocioException(ex);
        }
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<Percurso> pesquisarPercurso(Prova prova) throws NegocioException {
        try {
            List<ParametroPesquisa> parametros = GeralUtil.criarLista(new ParametroPesquisa("prova", prova, Operador.IGUAL));
            return this.percursoDAO.pesquisarPorParametros(parametros, true, "nome");
        } catch (PersistenciaException ex) {
            LOG.error("", ex);
            throw new NegocioException(ex);
        }
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public void inserirNumeracaoAutomatica(Prova prova, boolean substituirNumeracaoExistente) throws NegocioException, AvisoNegocioException {
        List<CategoriaDaProva> listaCategoriaDaProva = this.pesquisarCategoriaDaProva(prova);
        this.inserirNumeracaoAutomatica(listaCategoriaDaProva, substituirNumeracaoExistente);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public void inserirNumeracaoAutomatica(List<CategoriaDaProva> categoriasParaNumeracao, boolean substituirNumeracaoExistente) throws NegocioException, AvisoNegocioException {
        if (categoriasParaNumeracao == null || categoriasParaNumeracao.isEmpty()) {
            return;
        }
        Integer numeroAtletaSequencial = substituirNumeracaoExistente ? 1 : null;

        List<CategoriaDaProva> listaTodasCategoriasProva = this.pesquisarCategoriaDaProva(categoriasParaNumeracao.get(0).getProva());

        for (CategoriaDaProva categoriaDaProva : categoriasParaNumeracao) {
            if (categoriaDaProva.getNumeracaoAutomatica() != ListaConstantes.NumeracaoAutomatica.NA) {
                List<AtletaInscricao> atletas;
                if (substituirNumeracaoExistente) {
                    atletas = this.pesquisarAtletaInscricao(categoriaDaProva);
                } else {
                    atletas = this.pesquisarAtletaInscricaoSemNumero(categoriaDaProva);
                }
                if (atletas.size() > 0) {
                    Integer numeroAtleta = null;
                    switch (categoriaDaProva.getNumeracaoAutomatica()) {
                        case POR_FAIXA:
                            numeroAtleta = substituirNumeracaoExistente ? categoriaDaProva.getInicioNumeracao() : this.obterProximaNumeracaoInscricao(categoriaDaProva);
                            break;
                        case SEQUENCIAL:
                            if (numeroAtletaSequencial == null) {
                                numeroAtletaSequencial = this.obterProximaNumeracaoInscricao(categoriaDaProva);
                            }
                            numeroAtleta = numeroAtletaSequencial;
                            break;
                    }
                    for (AtletaInscricao atletaInscricao : atletas) {
                        numeroAtleta = this.validarObterNumeracaoAutomatica(numeroAtleta, categoriaDaProva, listaTodasCategoriasProva);
                        atletaInscricao.setNumeroAtleta(numeroAtleta);
                        try {
                            //this.atualizarAtletaInscricao(atletaInscricao);
                            this.atletaInscricaoDAO.atualizarNumeroAtleta(atletaInscricao); // desempenho 4x mais rapido
                        } catch (PersistenciaException ex) {
                            LOG.error("", ex);
                            throw new NegocioException(ex);
                        }
                        numeroAtleta++;
                    }
                    if (categoriaDaProva.getNumeracaoAutomatica() == ListaConstantes.NumeracaoAutomatica.SEQUENCIAL) {
                        numeroAtletaSequencial = numeroAtleta;
                    }
                }
            }
        }
    }

    @Override
    public Integer obterProximaNumeracaoInscricao(CategoriaDaProva categoriaDaProva) throws NegocioException, AvisoNegocioException {
        try {
            Integer ultimaNumeracao;
            Integer proximaNumeracao = null;

            switch (categoriaDaProva.getNumeracaoAutomatica()) {
                case POR_FAIXA:
                    ultimaNumeracao = this.atletaInscricaoDAO.getMaiorNumeracaoCategoria(categoriaDaProva);
                    proximaNumeracao = ultimaNumeracao == null ? categoriaDaProva.getInicioNumeracao() : ultimaNumeracao + 1;
                    break;
                case SEQUENCIAL:
                    ultimaNumeracao = this.atletaInscricaoDAO.getMaiorNumeracaoSequencialProva(categoriaDaProva.getProva());
                    proximaNumeracao = ultimaNumeracao == null ? 1 : ultimaNumeracao + 1;
                    break;
            }
            return this.validarObterNumeracaoAutomatica(proximaNumeracao, categoriaDaProva, null);
        } catch (PersistenciaException ex) {
            LOG.error("", ex);
            throw new NegocioException(ex);
        }
    }

    /**
     * Valida a numeracao de acordo com a categoria e caso seja uma numeracao
     * sequencial, verifica se a numeracao esta dentro da faixa de numeracao de
     * uma outra categoria da corrida e retorna uma proxima numeracao fora da
     * faixa caso necessario.
     *
     * @param proximaNumeracao numeracao a ser validada
     * @param categoriaDaProva categoria correspondente a numeracao informada
     * @param todasCategoriasDaProva lista das categorias da prova, se null o
     * proprio metodo carrega as categorias.
     * @return numeracao valida.
     * @throws AvisoNegocioException
     * @throws NegocioException
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    private Integer validarObterNumeracaoAutomatica(Integer proximaNumeracao, CategoriaDaProva categoriaDaProva, List<CategoriaDaProva> todasCategoriasDaProva) throws AvisoNegocioException, NegocioException {
        switch (categoriaDaProva.getNumeracaoAutomatica()) {
            case NA:
                throw new AvisoNegocioException("A categoria não está configurada com numeração automática.");
            case POR_FAIXA:
                if (proximaNumeracao > categoriaDaProva.getFimNumeracao()) {
                    throw new AvisoNegocioException("A categoria " + categoriaDaProva.getCategoriaAtleta().getNome() + "estourou o limite da numeração.");
                }
                break;
            case SEQUENCIAL:
                boolean numeracaoDentroDaFaixaDeCategoria;
                do {
                    numeracaoDentroDaFaixaDeCategoria = false;
                    if (todasCategoriasDaProva == null) {
                        todasCategoriasDaProva = this.pesquisarCategoriaDaProva(categoriaDaProva.getProva());
                    }

                    for (CategoriaDaProva categoria : todasCategoriasDaProva) {
                        if (categoria.getNumeracaoAutomatica() == ListaConstantes.NumeracaoAutomatica.POR_FAIXA) {
                            if (proximaNumeracao >= categoria.getInicioNumeracao() && proximaNumeracao <= categoria.getFimNumeracao()) {
                                proximaNumeracao = categoria.getFimNumeracao() + 1;
                                numeracaoDentroDaFaixaDeCategoria = true;
                            }
                        }
                    }
                } while (numeracaoDentroDaFaixaDeCategoria);
                break;
        }
        if (categoriaDaProva.getDigitosNumeracao() != null && proximaNumeracao >= (Math.pow(10, categoriaDaProva.getDigitosNumeracao()))) {
            throw new AvisoNegocioException("A numeração automática rompeu o limite de digitos informado: " + categoriaDaProva.getDigitosNumeracao() + " dígitos.");
        } else {
            return proximaNumeracao;
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public CategoriaDaProva criarNovoCategoriaDaProva(CategoriaDaProva cdp) throws NegocioException {
        try {
            this.catProvaDAO.persist(cdp);
        } catch (PersistenciaException ex) {
            LOG.error("", ex);
            throw new NegocioException(ex);
        }
        return cdp;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public CategoriaDaProva atualizarCategoriaDaProva(CategoriaDaProva cdp) throws NegocioException {
        try {
            this.catProvaDAO.merge(cdp);
        } catch (PersistenciaException ex) {
            LOG.error("", ex);
            throw new NegocioException(ex);
        }
        return cdp;
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<ImportacaoInscricao> pesquisarImportacaoInscricaoProva(Prova prova) throws NegocioException {
        try {
            return this.atletaInscricaoDAO.pesquisarImportacaoInscricao(prova);
        } catch (PersistenciaException ex) {
            LOG.error("", ex);
            throw new NegocioException(ex);
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public int excluirImportacaoInscricaoProva(ImportacaoInscricao importacaoInscricao) throws NegocioException, AvisoNegocioException {
        List<ParametroPesquisa> parametros = new ArrayList();
        parametros.add(new ParametroPesquisa("categoria.prova", importacaoInscricao.getProva(), Operador.IGUAL));
        parametros.add(new ParametroPesquisa("dataImportacao", importacaoInscricao.getDataImportacao(), Operador.IGUAL));
        List<AtletaInscricao> inscricoes = this.pesquisarAtletaInscricaoPorParametros(parametros, false);
        int i = 0;
        for (AtletaInscricao atletaInscricao : inscricoes) {
            this.excluirAtletaInscricao(atletaInscricao);
            i++;
        }
        return i;
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<AtletaInscricao> pesquisarAtletaInscricaoSemDupla(Prova prova, Pagina pagina) throws NegocioException {
        List<ParametroPesquisa> parametros = new ArrayList();
        parametros.add(new ParametroPesquisa("categoria.prova", prova, Operador.IGUAL));
        parametros.add(new ParametroPesquisa("categoria.categoriaAtleta.categoriaDupla", true, Operador.IGUAL));
        parametros.add(new ParametroPesquisa("dupla", null, Operador.IGUAL));
        return this.pesquisarAtletaInscricaoPorParametrosPaginada(parametros, pagina, true, "atleta.nome");
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<AtletaInscricao> pesquisarAtletaInscricaoSemNumero(Prova prova, Pagina pagina) throws NegocioException {
        List<ParametroPesquisa> parametros = new ArrayList<>();
        parametros.add(new ParametroPesquisa("numeroAtleta", null, Operador.IGUAL));
        parametros.add(new ParametroPesquisa("categoria.prova", prova, Operador.IGUAL));
        return this.pesquisarAtletaInscricaoPorParametrosPaginada(parametros, pagina, false);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<AtletaInscricao> pesquisarAtletaInscricaoNumeracaoRepetida(Prova prova, Pagina pagina) throws NegocioException {
        List<AtletaInscricao> atletas = new ArrayList();
        List<ProvaNumeracaoRepetida> numeracaoRepetida = this.pesquisarNumeracaoRepetida(prova);

        for (ProvaNumeracaoRepetida provaNumeracaoRepetida : numeracaoRepetida) {
            atletas.addAll(this.pesquisarListaAtletaInscricaoPorNumeroAtleta(prova, provaNumeracaoRepetida.getNumeroAtleta()));
        }
        return atletas;
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<AtletaInscricao> pesquisarAtletaInscricaoSemNumero(CategoriaDaProva categoriaDaProva) throws NegocioException {
        List<ParametroPesquisa> parametros = new ArrayList<>();
        parametros.add(new ParametroPesquisa("numeroAtleta", null, Operador.IGUAL));
        parametros.add(new ParametroPesquisa("categoria", categoriaDaProva, Operador.IGUAL));
        return this.pesquisarAtletaInscricaoPorParametros(parametros, false);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    private void excluirCategoriaDaProva(Prova prova) throws NegocioException {
        try {
            for (CategoriaDaProva cdp : this.pesquisarCategoriaDaProva(prova)) {
                this.catProvaDAO.excluir(cdp);
            }
            this.catProvaDAO.sincronizar();
        } catch (PersistenciaException ex) {
            LOG.error("", ex);
            throw new NegocioException(ex);
        }
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public Prova criarProvaDemo(int totalPercurso, int totalVolta, int totalCategoriaMasculina, int totalCategoriaFeminina, int totalCategoriaDupla) throws AvisoNegocioException, NegocioException {
        int totalCategoria = totalCategoriaMasculina + totalCategoriaFeminina + totalCategoriaDupla;
        if (totalCategoria < totalPercurso) {
            throw new AvisoNegocioException("O total de categorias deve ser maior que o número total de percursos.");
        }

        Prova prova = new Prova();
        prova.setData(LocalDate.now());
        prova.setNome("Prova Demo " + FormatarUtil.localDateToString(prova.getData()));
        prova.setDemo(true);
        prova.setStatus(StatusProva.INSCRICAO_FECHADA);

        prova.setListaRepresentanteFederacao(new LinkedHashSet<>());
        prova.getListaRepresentanteFederacao().add(this.pesquisarUmRepresentanteFederacao());
        prova.setListaRepresentanteOrganizacao(new LinkedHashSet<>());
        prova.getListaRepresentanteOrganizacao().add(this.pesquisarUmRepresentanteOrganizacao());

        Endereco ende = new Endereco();
        ende.setCidade("Vespasiano");
        ende.setUf("MG");
        prova.setEndereco(ende);

        prova.setListaPercurso(new LinkedHashSet<>());
        prova.setListaLargada(new LinkedHashSet<>());
        for (int i = 1; i <= totalPercurso; i++) {
            Percurso p = new Percurso();
            p.setNome("Percurso " + i);
            p.setNumeroVolta(totalVolta);
            p.setDistancia(10f * i);
            p.setGrauDificuldade(ListaConstantes.GrauDificuldade.MEDIO);
            p.setProva(prova);
            prova.getListaPercurso().add(p);

            Largada l = new Largada();
            l.setNome("Largada " + i);
            l.setHoraPrevista(LocalDateTime.now());
            l.setProva(prova);
            prova.getListaLargada().add(l);
        }

        List<CategoriaAtleta> categorias = new ArrayList<>();
        categorias.addAll(this.estagiario.pesquisarCategorias(totalCategoriaMasculina, Sexo.MASCULINO, false));
        categorias.addAll(this.estagiario.pesquisarCategorias(totalCategoriaFeminina, Sexo.FEMININO, false));
        categorias.addAll(this.estagiario.pesquisarCategorias(totalCategoriaDupla, null, true));

        Iterator<Percurso> itPercurso = prova.getListaPercurso().iterator();
        Iterator<Largada> itLargada = prova.getListaLargada().iterator();
        Percurso percurso = null;
        Largada largada = null;
        int categoriaPorPercurso = totalCategoria / totalPercurso;
        int contadorCategoria = 0;

        prova.setListaCategoriaDaProva(new LinkedHashSet<>());
        for (CategoriaAtleta ca : categorias) {
            if (contadorCategoria % categoriaPorPercurso == 0 && contadorCategoria + categoriaPorPercurso <= totalCategoria) {
                percurso = itPercurso.next();
                largada = itLargada.next();
            }
            CategoriaDaProva cdp = new CategoriaDaProva();
            cdp.setProva(prova);
            cdp.setLargada(largada);
            cdp.setPercurso(percurso);
            cdp.setCategoriaAtleta(ca);
            cdp.setNumeracaoAutomatica(ListaConstantes.NumeracaoAutomatica.NA);
            prova.getListaCategoriaDaProva().add(cdp);
            contadorCategoria++;
        }

        return this.criarNovoProva(prova);
    }

    private RepresentanteFederacao pesquisarUmRepresentanteFederacao() throws NegocioException {
        try {
            return (RepresentanteFederacao) this.representanteFederacaoDAO.pesquisarPorParametrosPaginada(new ArrayList(), 1, 1, false).get(0);
        } catch (PersistenciaException ex) {
            throw new NegocioException(ex);
        }
    }

    private RepresentanteOrganizacaoProva pesquisarUmRepresentanteOrganizacao() throws NegocioException {
        try {
            return (RepresentanteOrganizacaoProva) this.representanteOrganizacaoProvaDAO.pesquisarPorParametrosPaginada(new ArrayList(), 1, 1, false).get(0);
        } catch (PersistenciaException ex) {
            throw new NegocioException(ex);
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public AtletaInscricao criarAtletaDemo(Prova prova, Integer numeroAtleta) throws NegocioException, AvisoNegocioException {
        List<CategoriaDaProva> categorias = this.pesquisarCategoriaDaProva(prova);
        Random random = new Random();
        CategoriaDaProva categoriaDaProva = categorias.get(random.nextInt(categorias.size()));

        Atleta atleta = new Atleta();
        atleta.setNome("Atleta " + numeroAtleta);
        atleta.setDataNascimento(LocalDate.now());
        atleta.setSexo(categoriaDaProva.getCategoriaAtleta().getSexo() == Sexo.NAO_SE_APLICA ? Sexo.MASCULINO : categoriaDaProva.getCategoriaAtleta().getSexo());

        Endereco ende = new Endereco();
        ende.setCidade("Vespasiano");
        ende.setUf("MG");

        atleta.setEndereco(ende);

        atleta = this.criarNovoAtleta(atleta);

        AtletaInscricao atletaInscricao = new AtletaInscricao();
        atletaInscricao.setAtleta(atleta);
        atletaInscricao.setNumeroAtleta(numeroAtleta);
        atletaInscricao.setCategoria(categoriaDaProva);
        return this.criarNovoAtletaInscricao(atletaInscricao);
    }
}
