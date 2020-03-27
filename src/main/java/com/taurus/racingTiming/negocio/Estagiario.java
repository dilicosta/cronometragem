package com.taurus.racingTiming.negocio;

import com.taurus.dao.ParametroPesquisa;
import com.taurus.exception.AvisoNegocioException;
import com.taurus.exception.NegocioException;
import com.taurus.exception.PersistenciaException;
import com.taurus.racingTiming.dao.atleta.ICategoriaAtletaDAO;
import com.taurus.racingTiming.dao.corrida.ICategoriaDaProvaDAO;
import com.taurus.racingTiming.dao.corrida.IProvaDAO;
import com.taurus.racingTiming.dao.responsavel.IFederacaoDAO;
import com.taurus.racingTiming.dao.responsavel.IOrganizacaoProvaDAO;
import com.taurus.racingTiming.dao.responsavel.IRepresentanteFederacaoDAO;
import com.taurus.racingTiming.dao.responsavel.IRepresentanteOrganizacaoProvaDAO;
import com.taurus.racingTiming.entidade.atleta.CategoriaAtleta;
import com.taurus.racingTiming.entidade.corrida.CategoriaDaProva;
import com.taurus.racingTiming.entidade.corrida.Prova;
import com.taurus.racingTiming.entidade.responsavel.Federacao;
import com.taurus.racingTiming.entidade.responsavel.OrganizacaoProva;
import com.taurus.racingTiming.entidade.responsavel.RepresentanteFederacao;
import com.taurus.racingTiming.entidade.responsavel.RepresentanteOrganizacaoProva;
import com.taurus.util.ControleMensagem;
import com.taurus.util.GeralUtil;
import com.taurus.util.ListaConstantesBase.Sexo;
import com.taurus.util.Pagina;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Classe de negocio responsavel pelos cadastros basicos do sistema Ex:
 * Federacao, categoria de atleta, organizacao de prova
 *
 * @author Diego Lima
 */
@Component
public class Estagiario implements IEstagiario {

    private static final Log LOG = LogFactory.getLog(Estagiario.class);

    @Autowired
    private IFederacaoDAO<Federacao> federacaoDAO;
    @Autowired
    private IRepresentanteFederacaoDAO<RepresentanteFederacao> representanteFederacaoDAO;
    @Autowired
    private IOrganizacaoProvaDAO<OrganizacaoProva> organizacaoProvaDAO;
    @Autowired
    private IRepresentanteOrganizacaoProvaDAO<RepresentanteOrganizacaoProva> representanteOrganizacaoProvaDAO;
    @Autowired
    private ICategoriaAtletaDAO<CategoriaAtleta> categoriaAtletaDAO;
    @Autowired
    private ICategoriaDaProvaDAO<CategoriaDaProva> categoriaDaProvaDAO;
    @Autowired
    private IProvaDAO<Prova> provaDAO;

    private ControleMensagem cm = ControleMensagem.getInstance();

    /**
     * **********************************************************************
     * *************************** FEDERACAO*********************************
     * **********************************************************************
     */
    /**
     * Cria um novo registro de Federacao
     *
     * @param federacao objeto que representa uma Federacao
     * @return objeto Federacao instanciado
     * @throws NegocioException
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public Federacao criarNovoFederacao(Federacao federacao) throws NegocioException {
        try {
            this.federacaoDAO.persist(federacao);
            return federacao;
        } catch (PersistenciaException ex) {
            LOG.error("", ex);
            throw new NegocioException(ex);
        }
    }

    /**
     * Atualiza um registro de Federacao
     *
     * @param federacao objeto que representa uma Federacao
     * @return Federacao
     * @throws NegocioException
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public Federacao atualizarFederacao(Federacao federacao) throws NegocioException {
        try {
            Federacao f = this.federacaoDAO.merge(federacao);
            return f;
        } catch (PersistenciaException ex) {
            LOG.error("", ex);
            throw new NegocioException(ex);
        }
    }

    /**
     * Exclui o registro da Federacao e seus representantes caso nao exista
     * nenhum impedimento
     *
     * @param federacao Objeto que representa a Federacao
     * @throws NegocioException
     * @throws AvisoNegocioException Caso exista algum impedimento para exclusao
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public void excluirFederacao(Federacao federacao) throws NegocioException, AvisoNegocioException {
        try {
            List<ParametroPesquisa> parametros = GeralUtil.criarLista(new ParametroPesquisa("listaRepresentanteFederacao.federacao", federacao, ParametroPesquisa.Operador.IGUAL));
            if (this.provaDAO.pesquisarTotalRegistrosPesquisaPorParametros(parametros) > 0) {
                throw new AvisoNegocioException(cm.getMensagem("aviso_exclusao_negada_item_associado_item", "federacao", "prova"));
            }
            federacao = this.federacaoDAO.atacharEntidade(federacao);
            this.federacaoDAO.excluir(federacao);
        } catch (PersistenciaException ex) {
            LOG.error("", ex);
            throw new NegocioException(ex);
        }
    }

    /**
     *
     * @return Lista de Federacao ordenada por nome
     * @throws NegocioException
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<Federacao> pesquisarFederacaoTodos() throws NegocioException {
        try {
            return this.federacaoDAO.pesquisarTodos(true, "nome");
        } catch (PersistenciaException ex) {
            LOG.error("", ex);
            throw new NegocioException(ex);
        }
    }

    /**
     * **********************************************************************
     * ******************** REPRESENTANTE FEDERACAO**************************
     * **********************************************************************
     */
    /**
     * Cria um novo registro de Representante da Federacao
     *
     * @param representanteFederacao objeto que representa um Representante da
     * Federacao
     * @return objeto Representante da Federacao instanciado
     * @throws NegocioException
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public RepresentanteFederacao criarNovoRepresentanteFederacao(RepresentanteFederacao representanteFederacao) throws NegocioException {
        try {
            this.representanteFederacaoDAO.persist(representanteFederacao);
            return representanteFederacao;
        } catch (PersistenciaException ex) {
            LOG.error("", ex);
            throw new NegocioException(ex);
        }
    }

    /**
     * Atualiza um registro de Representante da Federacao
     *
     * @param representanteFederacao objeto que representa um Representante da
     * Federacao
     * @return Representante da Federacao
     * @throws NegocioException
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public RepresentanteFederacao atualizarRepresentanteFederacao(RepresentanteFederacao representanteFederacao) throws NegocioException {
        try {
            RepresentanteFederacao rf = this.representanteFederacaoDAO.merge(representanteFederacao);
            return rf;
        } catch (PersistenciaException ex) {
            LOG.error("", ex);
            throw new NegocioException(ex);
        }
    }

    /**
     * Exclui o registro do Representante da Federacao e seus representantes
     * caso nao exista nenhum impedimento
     *
     * @param representanteFederacao Objeto que representa o Representante da
     * Federacao
     * @throws NegocioException
     * @throws AvisoNegocioException Caso exista algum impedimento para exclusao
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public void excluirRepresentanteFederacao(RepresentanteFederacao representanteFederacao) throws NegocioException, AvisoNegocioException {
        try {
            List<ParametroPesquisa> parametros = GeralUtil.criarLista(new ParametroPesquisa("listaRepresentanteFederacao.id", representanteFederacao.getId(), ParametroPesquisa.Operador.IGUAL));
            if (this.provaDAO.pesquisarTotalRegistrosPesquisaPorParametros(parametros) > 0) {
                throw new AvisoNegocioException(cm.getMensagem("aviso_exclusao_negada_item_associado_item", "representante_federacao", "prova"));
            }
            representanteFederacao = this.representanteFederacaoDAO.atacharEntidade(representanteFederacao);
            this.representanteFederacaoDAO.excluir(representanteFederacao);
        } catch (PersistenciaException ex) {
            LOG.error("", ex);
            throw new NegocioException(ex);
        }
    }

    /**
     *
     * @param federacao Objeto que representa a Federacao
     * @return Lista de Representante da Federacao ordenada por nome
     * @throws NegocioException
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<RepresentanteFederacao> pesquisarRepresentanteFederacaoPorFederacao(Federacao federacao) throws NegocioException {
        List<ParametroPesquisa> parametros = GeralUtil.criarLista(new ParametroPesquisa("federacao", federacao, ParametroPesquisa.Operador.IGUAL));

        try {
            return this.representanteFederacaoDAO.pesquisarPorParametros(parametros, true, "nome");
        } catch (PersistenciaException ex) {
            LOG.error("", ex);
            throw new NegocioException(ex);
        }
    }

    /**
     * **********************************************************************
     * ************************* ORGANIZACAO PROVA***************************
     * **********************************************************************
     */
    /**
     * Cria um novo registro de Organizacao de Prova
     *
     * @param organizacaoProva objeto que representa uma Organizacao de Prova
     * @return objeto Organizacao de Prova instanciado
     * @throws NegocioException
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public OrganizacaoProva criarNovoOrganizacaoProva(OrganizacaoProva organizacaoProva) throws NegocioException {
        try {
            this.organizacaoProvaDAO.persist(organizacaoProva);
            return organizacaoProva;
        } catch (PersistenciaException ex) {
            LOG.error("", ex);
            throw new NegocioException(ex);
        }
    }

    /**
     * Atualiza um registro de Federacao
     *
     * @param organizacaoProva objeto que representa uma Organizacao de Prova
     * @return Organizacao de Prova
     * @throws NegocioException
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public OrganizacaoProva atualizarOrganizacaoProva(OrganizacaoProva organizacaoProva) throws NegocioException {
        try {
            OrganizacaoProva op = this.organizacaoProvaDAO.merge(organizacaoProva);
            return op;
        } catch (PersistenciaException ex) {
            LOG.error("", ex);
            throw new NegocioException(ex);
        }
    }

    /**
     * Exclui o registro da Organizacao de Prova e seus representantes caso nao
     * exista nenhum impedimento
     *
     * @param organizacaoProva Objeto que representa a Organizacao de Prova
     * @throws NegocioException
     * @throws AvisoNegocioException Caso exista algum impedimento para exclusao
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public void excluirOrganizacaoProva(OrganizacaoProva organizacaoProva) throws NegocioException, AvisoNegocioException {
        try {
            List<ParametroPesquisa> parametros = GeralUtil.criarLista(new ParametroPesquisa("listaRepresentanteOrganizacao.organizacaoProva", organizacaoProva, ParametroPesquisa.Operador.IGUAL));
            if (this.provaDAO.pesquisarTotalRegistrosPesquisaPorParametros(parametros) > 0) {
                throw new AvisoNegocioException(cm.getMensagem("aviso_exclusao_negada_item_associado_item", "organizacao_prova", "prova"));
            }
            organizacaoProva = this.organizacaoProvaDAO.atacharEntidade(organizacaoProva);
            this.organizacaoProvaDAO.excluir(organizacaoProva);
        } catch (PersistenciaException ex) {
            LOG.error("", ex);
            throw new NegocioException(ex);
        }
    }

    /**
     * Perquisa os registros em Organizacao Prova pelo nome
     *
     * @param nome
     * @return Lista de Organizacao de Prova ordenada por nome
     * @throws NegocioException
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<OrganizacaoProva> pesquisarOrganizacaoProvaPorNome(String nome) throws NegocioException {
        List<ParametroPesquisa> parametros = GeralUtil.criarLista(new ParametroPesquisa("nome", nome, ParametroPesquisa.Operador.IGUAL));
        try {
            return this.organizacaoProvaDAO.pesquisarPorParametros(parametros, true, "nome");
        } catch (PersistenciaException ex) {
            LOG.error("", ex);
            throw new NegocioException(ex);
        }
    }

    /**
     *
     * @return Lista de Organizacao de Prova ordenada por nome
     * @throws NegocioException
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<OrganizacaoProva> pesquisarOrganizacaoProvaTodos() throws NegocioException {
        try {
            return this.organizacaoProvaDAO.pesquisarTodos(true, "nome");
        } catch (PersistenciaException ex) {
            LOG.error("", ex);
            throw new NegocioException(ex);
        }
    }

    /**
     * **********************************************************************
     * ******************* REPRESENTANTE ORGANIZACAO PROVA ******************
     * **********************************************************************
     */
    /**
     * Cria um novo registro de Representante da Organizacao de Prova
     *
     * @param representanteOrganizacaoProva objeto que representa um
     * Representante da Organizacao de Prova
     * @return objeto Representante da Organizacao de Prova instanciado
     * @throws NegocioException
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public RepresentanteOrganizacaoProva criarNovoRepresentanteOrganizacaoProva(RepresentanteOrganizacaoProva representanteOrganizacaoProva) throws NegocioException {
        try {
            this.representanteOrganizacaoProvaDAO.persist(representanteOrganizacaoProva);
            return representanteOrganizacaoProva;
        } catch (PersistenciaException ex) {
            LOG.error("", ex);
            throw new NegocioException(ex);
        }
    }

    /**
     * Atualiza um registro de Representante da Organizacao de Prova
     *
     * @param representanteOrganizacaoProva objeto que representa um
     * Representante da Federacao
     * @return Representante da Organizacao de Prova
     * @throws NegocioException
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public RepresentanteOrganizacaoProva atualizarRepresentanteOrganizacaoProva(RepresentanteOrganizacaoProva representanteOrganizacaoProva) throws NegocioException {
        try {
            RepresentanteOrganizacaoProva rf = this.representanteOrganizacaoProvaDAO.merge(representanteOrganizacaoProva);
            return rf;
        } catch (PersistenciaException ex) {
            LOG.error("", ex);
            throw new NegocioException(ex);
        }
    }

    /**
     * Exclui o registro do Representante da Organizacao de Prova e seus
     * representantes caso nao exista nenhum impedimento
     *
     * @param representanteOrganizacaoProva Objeto que representa o
     * Representante da Organizacao de Prova
     * @throws NegocioException
     * @throws AvisoNegocioException Caso exista algum impedimento para exclusao
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public void excluirRepresentanteOrganizacaoProva(RepresentanteOrganizacaoProva representanteOrganizacaoProva) throws NegocioException, AvisoNegocioException {
        try {
            List<ParametroPesquisa> parametros = GeralUtil.criarLista(new ParametroPesquisa("listaRepresentanteOrganizacao.id", representanteOrganizacaoProva.getId(), ParametroPesquisa.Operador.IGUAL));
            if (this.provaDAO.pesquisarTotalRegistrosPesquisaPorParametros(parametros) > 0) {
                throw new AvisoNegocioException(cm.getMensagem("aviso_exclusao_negada_item_associado_item", "representante_organizacao_prova", "prova"));
            }
            representanteOrganizacaoProva = this.representanteOrganizacaoProvaDAO.atacharEntidade(representanteOrganizacaoProva);
            this.representanteOrganizacaoProvaDAO.excluir(representanteOrganizacaoProva);
        } catch (PersistenciaException ex) {
            LOG.error("", ex);
            throw new NegocioException(ex);
        }
    }

    /**
     *
     * @param organizacaoProva Objeto que representa a Organizacao de Prova
     * @return Lista de Representante da Organizacao de Prova ordenada por nome
     * @throws NegocioException
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<RepresentanteOrganizacaoProva> pesquisarRepresentanteOrganizacaoProvaPorOrganizacaoProva(OrganizacaoProva organizacaoProva) throws NegocioException {
        List<ParametroPesquisa> parametros = GeralUtil.criarLista(new ParametroPesquisa("organizacaoProva", organizacaoProva, ParametroPesquisa.Operador.IGUAL));

        try {
            return this.representanteOrganizacaoProvaDAO.pesquisarPorParametros(parametros, true, "nome");
        } catch (PersistenciaException ex) {
            LOG.error("", ex);
            throw new NegocioException(ex);
        }
    }

    /**
     * Perquisa os registros em Representante Organizacao Prova pelo nome
     *
     * @param nome
     * @return Lista de Representante Organizacao de Prova ordenada por nome
     * @throws NegocioException
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<RepresentanteOrganizacaoProva> pesquisarRepresentanteOrganizacaoProvaPorNome(String nome) throws NegocioException {
        List<ParametroPesquisa> parametros = GeralUtil.criarLista(new ParametroPesquisa("nome", nome, ParametroPesquisa.Operador.IGUAL));
        try {
            return this.representanteOrganizacaoProvaDAO.pesquisarPorParametros(parametros, true, "nome");
        } catch (PersistenciaException ex) {
            LOG.error("", ex);
            throw new NegocioException(ex);
        }
    }

    /**
     * **********************************************************************
     * ********************** CATEGORIA ATLETA ******************************
     * **********************************************************************
     */
    /**
     * Cria um novo registro de Categoria de Atleta
     *
     * @param categoriaAtleta objeto que representa uma Categoria de Atleta
     * @return objeto Categoria de Atleta instanciado
     * @throws NegocioException
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public CategoriaAtleta criarNovoCategoriaAtleta(CategoriaAtleta categoriaAtleta) throws NegocioException {
        try {
            this.categoriaAtletaDAO.persist(categoriaAtleta);
            return categoriaAtleta;
        } catch (PersistenciaException ex) {
            LOG.error("", ex);
            throw new NegocioException(ex);
        }
    }

    /**
     * Atualiza um registro de Categoria de Atleta
     *
     * @param categoriaAtleta objeto que representa uma Categoria de Atleta
     * @return Categoria de Atleta
     * @throws NegocioException
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public CategoriaAtleta atualizarCategoriaAtleta(CategoriaAtleta categoriaAtleta) throws NegocioException {
        try {
            CategoriaAtleta f = this.categoriaAtletaDAO.merge(categoriaAtleta);
            return f;
        } catch (PersistenciaException ex) {
            LOG.error("", ex);
            throw new NegocioException(ex);
        }
    }

    /**
     * Exclui o registro da Categoria do Atleta caso nao exista nenhum
     * impedimento
     *
     * @param categoriaAtleta Objeto que representa a Categoria de Atleta
     * @throws NegocioException
     * @throws AvisoNegocioException Caso exista algum impedimento para exclusao
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public void excluirCategoriaAtleta(CategoriaAtleta categoriaAtleta) throws NegocioException, AvisoNegocioException {
        try {
            List<ParametroPesquisa> parametros = GeralUtil.criarLista(new ParametroPesquisa("categoriaAtleta", categoriaAtleta, ParametroPesquisa.Operador.IGUAL));
            if (this.categoriaDaProvaDAO.pesquisarTotalRegistrosPesquisaPorParametros(parametros) > 0) {
                throw new AvisoNegocioException(cm.getMensagem("aviso_exclusao_negada_item_associado_item", "categoria_atleta", "prova"));
            }

            categoriaAtleta = this.categoriaAtletaDAO.atacharEntidade(categoriaAtleta);

            this.categoriaAtletaDAO.excluir(categoriaAtleta);
        } catch (PersistenciaException ex) {
            LOG.error("", ex);
            throw new NegocioException(ex);
        }
    }

    /**
     *
     * @param pagina
     * @return Lista de Categoria de Atleta ordenada por nome
     * @throws NegocioException
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<CategoriaAtleta> pesquisarCategoriaAtletaTodos(Pagina pagina) throws NegocioException {
        List<ParametroPesquisa> listaParametroPesquisas = new ArrayList();
        try {
            pagina.setTotalItens(this.categoriaAtletaDAO.pesquisarTotalRegistrosPesquisaPorParametros(listaParametroPesquisas));
            return this.categoriaAtletaDAO.pesquisarPorParametrosPaginada(listaParametroPesquisas, pagina.getNumeroPagina(), pagina.getItensPorPagina(), true, "nome");
        } catch (PersistenciaException ex) {
            LOG.error("", ex);
            throw new NegocioException(ex);
        }
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<CategoriaAtleta> pesquisarCategoriaAtletaTodos() throws NegocioException {
        try {
            return this.categoriaAtletaDAO.pesquisarTodos(true, "nome");
        } catch (PersistenciaException ex) {
            LOG.error("", ex);
            throw new NegocioException(ex);
        }
    }

    @Override
    public List<CategoriaAtleta> pesquisarCategorias(int totalCategoria, Sexo sexo, boolean dupla) throws NegocioException {
        List<ParametroPesquisa> parametros = new ArrayList();
        if (sexo != null) {
            parametros.add(new ParametroPesquisa("sexo", sexo, ParametroPesquisa.Operador.IGUAL));
        }
        if (dupla) {
            parametros.add(new ParametroPesquisa("categoriaDupla", true, ParametroPesquisa.Operador.IGUAL));
        }

        try {
            return this.categoriaAtletaDAO.pesquisarPorParametrosPaginada(parametros, 1, totalCategoria, false);
        } catch (PersistenciaException ex) {
            LOG.error("", ex);
            throw new NegocioException(ex);
        }
    }
}
