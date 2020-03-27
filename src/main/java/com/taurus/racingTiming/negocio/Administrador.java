package com.taurus.racingTiming.negocio;

import com.taurus.exception.NegocioException;
import com.taurus.exception.PersistenciaException;
import com.taurus.racingTiming.dao.administracao.IConfiguacaoDAO;
import com.taurus.racingTiming.entidade.Configuracao;
import com.taurus.util.ControleMensagem;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Classe de negocio responsavel pelas configurações do sistema.
 *
 * @author Diego Lima
 */
@Component
public class Administrador implements IAdministrador {

    private static final Log LOG = LogFactory.getLog(Administrador.class);
    private Configuracao configuracao = null;
    @Autowired
    private IConfiguacaoDAO<Configuracao> configuracaoDAO;

    private ControleMensagem cm = ControleMensagem.getInstance();

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    private Configuracao criarNovoConfiguracao(Configuracao configuracao) throws NegocioException {
        try {
            this.configuracaoDAO.persist(configuracao);
            return configuracao;
        } catch (PersistenciaException ex) {
            LOG.error("", ex);
            throw new NegocioException(ex);
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public Configuracao atualizarConfiguracao(Configuracao configuracao) throws NegocioException {
        try {
            Configuracao c = this.configuracaoDAO.merge(configuracao);
            this.configuracao = c;
            return c;
        } catch (PersistenciaException ex) {
            LOG.error("", ex);
            throw new NegocioException(ex);
        }
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public Configuracao pesquisarConfiguracao() throws NegocioException {
        if (this.configuracao != null) {
            return this.configuracao;
        } else {
            try {
                List<Configuracao> lista = this.configuracaoDAO.pesquisarTodos();
                if (lista.isEmpty()) {
                    return this.criarNovoConfiguracao(new Configuracao());
                } else {
                    return lista.get(0);
                }
            } catch (PersistenciaException ex) {
                LOG.error("", ex);
                throw new NegocioException(ex);
            }
        }
    }
}
