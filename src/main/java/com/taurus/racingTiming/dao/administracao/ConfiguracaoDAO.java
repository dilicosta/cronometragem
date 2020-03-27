package com.taurus.racingTiming.dao.administracao;

import com.taurus.dao.GenericDAO;
import com.taurus.racingTiming.entidade.Configuracao;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Diego Lima
 */
@Repository
public class ConfiguracaoDAO extends GenericDAO<Configuracao> implements IConfiguacaoDAO<Configuracao>{

    public ConfiguracaoDAO() {
        super(Configuracao.class);
    }
}
