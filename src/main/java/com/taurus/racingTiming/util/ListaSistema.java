package com.taurus.racingTiming.util;

import com.taurus.exception.NegocioException;
import com.taurus.racingTiming.entidade.atleta.CategoriaAtleta;
import com.taurus.racingTiming.entidade.responsavel.Federacao;
import com.taurus.racingTiming.entidade.responsavel.OrganizacaoProva;
import com.taurus.racingTiming.negocio.IEstagiario;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Classe com as listas do Sistema que sao utilizadas em varias operacoes
 *
 * @author Diego Lima
 */
@Component
public class ListaSistema {

    @Autowired
    private IEstagiario estagiario;

    private List<OrganizacaoProva> listaOrganizacaoProva;
    private List<Federacao> listaFederacao;
    private List<CategoriaAtleta> listaCategoriaAtleta;

    public List<OrganizacaoProva> getListaOrganizacaoProva() {
        return listaOrganizacaoProva;
    }

    public List<Federacao> getListaFederacao() {
        return listaFederacao;
    }

    public List<CategoriaAtleta> getListaCategoriaAtleta() {
        return listaCategoriaAtleta;
    }

    /**
     * Atualiza todas as listas do sistema
     *
     * @throws com.taurus.exception.NegocioException
     */
    public void atualizarTodas() throws NegocioException {
        this.atualizarListaOrganizacaoProva();
        this.atualizarListaFederacao();
        this.atualizarListaCategoriaAtleta();
    }

    public void atualizarListaOrganizacaoProva() throws NegocioException {
        this.listaOrganizacaoProva = estagiario.pesquisarOrganizacaoProvaTodos();
    }

    public void atualizarListaFederacao() throws NegocioException {
        this.listaFederacao = estagiario.pesquisarFederacaoTodos();
    }

    public void atualizarListaCategoriaAtleta() throws NegocioException {
        this.listaCategoriaAtleta = estagiario.pesquisarCategoriaAtletaTodos();
    }
}
