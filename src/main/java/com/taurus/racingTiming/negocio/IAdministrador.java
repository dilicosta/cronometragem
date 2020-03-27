package com.taurus.racingTiming.negocio;

import com.taurus.exception.NegocioException;
import com.taurus.racingTiming.entidade.Configuracao;

/**
 *
 * @author Diego
 */
public interface IAdministrador {

    public Configuracao atualizarConfiguracao(Configuracao configuracao) throws NegocioException;

    public Configuracao pesquisarConfiguracao() throws NegocioException;
}
