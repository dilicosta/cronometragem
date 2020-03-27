package com.taurus.racingTiming.negocio;

import com.taurus.exception.AvisoNegocioException;
import com.taurus.exception.NegocioException;
import com.taurus.racingTiming.entidade.atleta.CategoriaAtleta;
import com.taurus.racingTiming.entidade.responsavel.Federacao;
import com.taurus.racingTiming.entidade.responsavel.OrganizacaoProva;
import com.taurus.racingTiming.entidade.responsavel.RepresentanteFederacao;
import com.taurus.racingTiming.entidade.responsavel.RepresentanteOrganizacaoProva;
import com.taurus.util.ListaConstantesBase;
import com.taurus.util.Pagina;
import java.util.List;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Diego
 */
public interface IEstagiario {

    /**
     * Atualiza um registro de Categoria de Atleta
     *
     * @param categoriaAtleta objeto que representa uma Categoria de Atleta
     * @return Categoria de Atleta
     * @throws NegocioException
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public CategoriaAtleta atualizarCategoriaAtleta(CategoriaAtleta categoriaAtleta) throws NegocioException;

    /**
     * Atualiza um registro de Federacao
     *
     * @param federacao objeto que representa uma Federacao
     * @return Federacao
     * @throws NegocioException
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Federacao atualizarFederacao(Federacao federacao) throws NegocioException;

    /**
     * Atualiza um registro de Federacao
     *
     * @param organizacaoProva objeto que representa uma Organizacao de Prova
     * @return Organizacao de Prova
     * @throws NegocioException
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public OrganizacaoProva atualizarOrganizacaoProva(OrganizacaoProva organizacaoProva) throws NegocioException;

    /**
     * Atualiza um registro de Representante da Federacao
     *
     * @param representanteFederacao objeto que representa um Representante da
     * Federacao
     * @return Representante da Federacao
     * @throws NegocioException
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public RepresentanteFederacao atualizarRepresentanteFederacao(RepresentanteFederacao representanteFederacao) throws NegocioException;

    /**
     * Atualiza um registro de Representante da Organizacao de Prova
     *
     * @param representanteOrganizacaoProva objeto que representa um
     * Representante da Federacao
     * @return Representante da Organizacao de Prova
     * @throws NegocioException
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public RepresentanteOrganizacaoProva atualizarRepresentanteOrganizacaoProva(RepresentanteOrganizacaoProva representanteOrganizacaoProva) throws NegocioException;

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
    public CategoriaAtleta criarNovoCategoriaAtleta(CategoriaAtleta categoriaAtleta) throws NegocioException;

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
    public Federacao criarNovoFederacao(Federacao federacao) throws NegocioException;

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
    public OrganizacaoProva criarNovoOrganizacaoProva(OrganizacaoProva organizacaoProva) throws NegocioException;

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
    public RepresentanteFederacao criarNovoRepresentanteFederacao(RepresentanteFederacao representanteFederacao) throws NegocioException;

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
    public RepresentanteOrganizacaoProva criarNovoRepresentanteOrganizacaoProva(RepresentanteOrganizacaoProva representanteOrganizacaoProva) throws NegocioException;

    /**
     * Exclui o registro da Federacao e seus representantes caso nao exista
     * nenhum impedimento
     *
     * @param categoriaAtleta Objeto que representa a Categoria de Atleta
     * @throws NegocioException
     * @throws AvisoNegocioException Caso exista algum impedimento para exclusao
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void excluirCategoriaAtleta(CategoriaAtleta categoriaAtleta) throws NegocioException, AvisoNegocioException;

    /**
     * Exclui o registro da Federacao e seus representantes caso nao exista
     * nenhum impedimento
     *
     * @param federacao Objeto que representa a Federacao
     * @throws NegocioException
     * @throws AvisoNegocioException Caso exista algum impedimento para exclusao
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void excluirFederacao(Federacao federacao) throws NegocioException, AvisoNegocioException;

    /**
     * Exclui o registro da Organizacao de Prova e seus representantes caso nao
     * exista nenhum impedimento
     *
     * @param organizacaoProva Objeto que representa a Organizacao de Prova
     * @throws NegocioException
     * @throws AvisoNegocioException Caso exista algum impedimento para exclusao
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void excluirOrganizacaoProva(OrganizacaoProva organizacaoProva) throws NegocioException, AvisoNegocioException;

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
    public void excluirRepresentanteFederacao(RepresentanteFederacao representanteFederacao) throws NegocioException, AvisoNegocioException;

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
    public void excluirRepresentanteOrganizacaoProva(RepresentanteOrganizacaoProva representanteOrganizacaoProva) throws NegocioException, AvisoNegocioException;

    /**
     *
     * @param pagina
     * @return Lista de Categoria de Atleta ordenada por nome
     * @throws NegocioException
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    public List<CategoriaAtleta> pesquisarCategoriaAtletaTodos(Pagina pagina) throws NegocioException;

    @Transactional(propagation = Propagation.SUPPORTS)
    List<CategoriaAtleta> pesquisarCategoriaAtletaTodos() throws NegocioException;

    /**
     *
     * @return Lista de Federacao ordenada por nome
     * @throws NegocioException
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    public List<Federacao> pesquisarFederacaoTodos() throws NegocioException;

    /**
     * Perquisa os registros em Organizacao Prova pelo nome
     *
     * @param nome
     * @return Lista de Organizacao de Prova ordenada por nome
     * @throws NegocioException
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    public List<OrganizacaoProva> pesquisarOrganizacaoProvaPorNome(String nome) throws NegocioException;

    /**
     *
     * @return Lista de Organizacao de Prova ordenada por nome
     * @throws NegocioException
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    public List<OrganizacaoProva> pesquisarOrganizacaoProvaTodos() throws NegocioException;

    /**
     *
     * @param federacao Objeto que representa a Federacao
     * @return Lista de Representante da Federacao ordenada por nome
     * @throws NegocioException
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    public List<RepresentanteFederacao> pesquisarRepresentanteFederacaoPorFederacao(Federacao federacao) throws NegocioException;

    /**
     * Perquisa os registros em Representante Organizacao Prova pelo nome
     *
     * @param nome
     * @return Lista de Representante Organizacao de Prova ordenada por nome
     * @throws NegocioException
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    public List<RepresentanteOrganizacaoProva> pesquisarRepresentanteOrganizacaoProvaPorNome(String nome) throws NegocioException;

    /**
     *
     * @param organizacaoProva Objeto que representa a Organizacao de Prova
     * @return Lista de Representante da Organizacao de Prova ordenada por nome
     * @throws NegocioException
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    public List<RepresentanteOrganizacaoProva> pesquisarRepresentanteOrganizacaoProvaPorOrganizacaoProva(OrganizacaoProva organizacaoProva) throws NegocioException;

    public List<CategoriaAtleta> pesquisarCategorias(int totalCategoria, ListaConstantesBase.Sexo sexo, boolean dupla) throws NegocioException;

}
