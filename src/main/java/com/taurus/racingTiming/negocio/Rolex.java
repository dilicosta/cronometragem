package com.taurus.racingTiming.negocio;

import com.taurus.dao.ParametroPesquisa;
import com.taurus.exception.AvisoNegocioException;
import com.taurus.exception.NegocioException;
import com.taurus.exception.PersistenciaException;
import com.taurus.racingTiming.dao.corrida.IAtletaInscricaoDAO;
import com.taurus.racingTiming.dao.corrida.ICronometragemDAO;
import com.taurus.racingTiming.dao.corrida.ICronometragemImagemDAO;
import com.taurus.racingTiming.dao.corrida.ILargadaDAO;
import com.taurus.racingTiming.entidade.corrida.AtletaInscricao;
import com.taurus.racingTiming.entidade.corrida.CategoriaDaProva;
import com.taurus.racingTiming.entidade.corrida.Cronometragem;
import com.taurus.racingTiming.entidade.corrida.CronometragemImagem;
import com.taurus.racingTiming.entidade.corrida.Largada;
import com.taurus.racingTiming.entidade.corrida.Percurso;
import com.taurus.racingTiming.entidade.corrida.Prova;
import com.taurus.racingTiming.negocio.comparador.CronometragemHoraRegistroComparator;
import com.taurus.racingTiming.pojo.CategoriaProvaResumo;
import com.taurus.racingTiming.util.ListaConstantes;
import com.taurus.racingTiming.util.ListaConstantes.PendenciaProvaCrono;
import com.taurus.racingTiming.util.ListaConstantes.StatusAtletaCorrida;
import com.taurus.racingTiming.util.ListaConstantes.StatusCronometragem;
import com.taurus.racingTiming.util.ListaConstantes.StatusProva;
import com.taurus.util.GeralUtil;
import com.taurus.util.ListaConstantesBase;
import com.taurus.util.Pagina;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Diego
 */
@Component
public class Rolex implements IRolex {

    private static final Log LOG = LogFactory.getLog(Rolex.class);

    @Autowired
    private ISecretario secretario;

    @Autowired
    private IAtletaInscricaoDAO<AtletaInscricao> atletaInscricaoDAO;
    @Autowired
    private ILargadaDAO<Largada> largadaDAO;
    @Autowired
    private ICronometragemDAO<Cronometragem> cronometragemDAO;
    @Autowired
    private ICronometragemImagemDAO<CronometragemImagem> cronometragemImagemDAO;

    private List<CategoriaProvaResumo> listaAtletasCorrendo = null;

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<CategoriaProvaResumo> getListaAtletasCorrendo(Prova prova) throws NegocioException {
        if (this.listaAtletasCorrendo == null) {
            this.listaAtletasCorrendo = new ArrayList<>();
            for (CategoriaDaProva cdp : this.secretario.pesquisarCategoriaDaProva(prova)) {
                Long totalInscritos = this.secretario.pesquisarTotalInscricoes(cdp);
                CategoriaProvaResumo categoriaProvaResumo = new CategoriaProvaResumo(cdp, totalInscritos);
                categoriaProvaResumo.setConcluiramProva(this.pesquisarTotalAtletaCompletou(cdp));
                this.listaAtletasCorrendo.add(categoriaProvaResumo);
            }
        }
        return this.listaAtletasCorrendo;
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public Largada atualizarHorarioLargada(Prova prova, Largada largada) throws NegocioException {
        try {
            boolean todasLargadasIniciadas = true;
            for (Largada largadaTmp : prova.getListaLargada()) {
                if (largadaTmp.getHoraInicio() == null) {
                    todasLargadasIniciadas = false;
                }
            }

            prova.setStatus(todasLargadasIniciadas ? StatusProva.CRONOMETRANDO_TODAS_LARGADAS : StatusProva.CRONOMETRANDO_PARCIAL_LARGADA);
            this.secretario.atualizarProva(prova);
            return this.largadaDAO.merge(largada);
        } catch (PersistenciaException ex) {
            LOG.error("", ex);
            throw new NegocioException(ex);
        }
    }

    /**
     * Cria um novo registro de Cronometragem
     *
     * @param cronometragem objeto que representa uma Cronometragem
     * @return
     * @throws NegocioException
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    private Cronometragem criarNovoCronometragem(Cronometragem cronometragem) throws NegocioException {
        try {
            cronometragem.setExcluida(false);
            this.cronometragemDAO.persist(cronometragem);
            return cronometragem;
        } catch (PersistenciaException ex) {
            LOG.error("", ex);
            throw new NegocioException(ex);
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public Cronometragem criarNovoCronometragemComVerificacaoTempoRelacionado(Cronometragem cronometragem) throws NegocioException {
        cronometragem = this.criarNovoCronometragem(cronometragem);
        this.calcularTempoVoltaAtletaEInconsistencias(this.pesquisarCronometragemPorNumeroAtletaStatus(cronometragem.getProva(), cronometragem.getNumeroAtleta(), StatusCronometragem.ATIVA));
        if (cronometragem.getAtletaInscricao() != null) {
            this.atualizarListaAtletaCorrendo(cronometragem.getAtletaInscricao());
        }
        return cronometragem;
    }

    /**
     * Atualiza um registro de Cronometragem
     *
     * @param cronometragem objeto que representa uma Cronometragem
     * @return Atleta
     * @throws NegocioException
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    private Cronometragem atualizarCronometragem(Cronometragem cronometragem) throws NegocioException {
        try {
            return this.cronometragemDAO.merge(cronometragem);
        } catch (PersistenciaException ex) {
            LOG.error("", ex);
            throw new NegocioException(ex);
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public Cronometragem atualizarCronometragemComVerificacaoTempoRelacionado(Cronometragem cronometragem) throws NegocioException {
        if (cronometragem.isExcluida()) {
            cronometragem.setDuvida(false);
            cronometragem.setVolta(null);
            cronometragem.setTempoVolta(null);
        }
        cronometragem = this.atualizarCronometragem(cronometragem);
        this.verificarCronometragemAtleta(cronometragem.getProva(), cronometragem.getNumeroAtleta(), cronometragem.getAtletaInscricao());
        return cronometragem;
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public Cronometragem ativarCronometragem(Cronometragem cronometragem) throws NegocioException {
        cronometragem.setExcluida(false);
        cronometragem.setDuvida(false);
        cronometragem.setVolta(null);
        cronometragem.setTempoVolta(null);
        this.atualizarCronometragemComVerificacaoTempoRelacionado(cronometragem);
        return cronometragem;
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public void excluirTodasCronometragemAtleta(AtletaInscricao atletaInscricao) throws NegocioException {
        List<Cronometragem> listaCrono = this.pesquisarCronometragemPorNumeroAtleta(atletaInscricao.getCategoria().getProva(), atletaInscricao.getNumeroAtleta());
        for (Cronometragem cronometragem : listaCrono) {
            cronometragem.setExcluida(true);
            cronometragem.setDuvida(false);
            cronometragem.setVolta(null);
            cronometragem.setTempoVolta(null);
            this.atualizarCronometragem(cronometragem);
        }

        if (atletaInscricao.getId() != null) {
            this.removerTempoAtleta(atletaInscricao);
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public void excluirCronometragemFisica(Cronometragem cronometragem) throws NegocioException {
        try {
            this.cronometragemDAO.excluir(cronometragem);
        } catch (PersistenciaException ex) {
            LOG.error("", ex);
            throw new NegocioException(ex);
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public Cronometragem excluirCronometragem(Cronometragem cronometragem) throws NegocioException {
        cronometragem.setExcluida(true);
        cronometragem.setDuvida(false);
        cronometragem.setVolta(null);
        cronometragem.setTempoVolta(null);
        this.atualizarCronometragemComVerificacaoTempoRelacionado(cronometragem);
        return cronometragem;
    }

    @Override
    public void adicionarAtletaCronometragem(AtletaInscricao atletaInscricao, Prova prova, Integer numeroAtletaDaCronometragem) throws NegocioException {
        List<Cronometragem> cronometragens = this.pesquisarCronometragemPorNumeroAtletaStatus(prova, numeroAtletaDaCronometragem, StatusCronometragem.ATIVA);
        for (Cronometragem crono : cronometragens) {
            crono.setNumeroAtleta(atletaInscricao.getNumeroAtleta());
            crono.setAtletaInscricao(atletaInscricao);
            this.atualizarCronometragem(crono);
        }
        this.calcularTempoVoltaAtletaEInconsistencias(atletaInscricao);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<Cronometragem> pesquisarUltimasCronometragensProva(Prova prova, Pagina pagina) throws NegocioException {
        List<ParametroPesquisa> parametros = GeralUtil.criarLista(new ParametroPesquisa("prova", prova, ParametroPesquisa.Operador.IGUAL));
        return this.pesquisarCronometragemPorParametroPaginada(parametros, pagina, false, "horaRegistro");
    }

    @Override
    public Cronometragem trocarCronometragemAtleta(AtletaInscricao atletaInscricaoAntiga, Cronometragem crono) throws NegocioException {
        crono = this.atualizarCronometragemComVerificacaoTempoRelacionado(crono);
        if (atletaInscricaoAntiga != null) {
            this.verificarCronometragemAtleta(atletaInscricaoAntiga.getCategoria().getProva(), atletaInscricaoAntiga.getNumeroAtleta(), atletaInscricaoAntiga);
        }
        return crono;
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<Cronometragem> pesquisarCronometragemPorAtletaStatus(AtletaInscricao atletaInscricao, StatusCronometragem... listaStatusCronometragem) throws NegocioException {
        List<ParametroPesquisa> parametros = GeralUtil.criarLista(new ParametroPesquisa("atletaInscricao", atletaInscricao, ParametroPesquisa.Operador.IGUAL));
        for (StatusCronometragem status : listaStatusCronometragem) {
            this.adicionarParametroStatusCronometragem(parametros, status);
        }
        return this.pesquisarCronometragemPorParametro(parametros, true, "horaRegistro");
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<Cronometragem> pesquisarCronometragemPorNumeroAtletaStatus(Prova prova, Integer numeroAtleta, StatusCronometragem statusCronometragem) throws NegocioException {
        return this.pesquisarCronometragemPorNumeroAtletaPeriodoStatus(prova, numeroAtleta, null, null, statusCronometragem, null);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<Cronometragem> pesquisarCronometragemPorNumeroAtleta(Prova prova, Integer numeroAtleta) throws NegocioException {
        return this.pesquisarCronometragemPorNumeroAtletaPeriodoStatus(prova, numeroAtleta, null, null, null, null);
    }

    @Override
    public List<Cronometragem> pesquisarCronometragemPorPercursoSexoStatus(Prova prova, Percurso percurso, ListaConstantesBase.Sexo sexo, ListaConstantes.StatusCronometragem statusCrono, Pagina pagina) throws NegocioException {
        List<ParametroPesquisa> parametros = new ArrayList();
        parametros.add(new ParametroPesquisa("atletaInscricao.categoria.prova", prova, ParametroPesquisa.Operador.IGUAL));
        if (percurso != null) {
            parametros.add(new ParametroPesquisa("atletaInscricao.categoria.percurso", percurso, ParametroPesquisa.Operador.IGUAL));
        }
        if (sexo != null) {
            parametros.add(new ParametroPesquisa("atletaInscricao.categoria.categoriaAtleta.sexo", sexo, ParametroPesquisa.Operador.IGUAL));
        }
        this.adicionarParametroStatusCronometragem(parametros, statusCrono);
        return this.pesquisarCronometragemPorParametroPaginada(parametros, pagina, true, "horaRegistro");
    }

    @Override
    public List<Cronometragem> pesquisarCronometragemPorCategoriaStatus(CategoriaDaProva categoriaDaProva, ListaConstantes.StatusCronometragem statusCrono, Pagina pagina) throws NegocioException {
        List<ParametroPesquisa> parametros = new ArrayList();
        parametros.add(new ParametroPesquisa("atletaInscricao.categoria", categoriaDaProva, ParametroPesquisa.Operador.IGUAL));
        this.adicionarParametroStatusCronometragem(parametros, statusCrono);
        return this.pesquisarCronometragemPorParametroPaginada(parametros, pagina, true, "horaRegistro");
    }

    @Override
    public List<Cronometragem> pesquisarCronometragemPorNumeroAtletaPeriodoStatus(Prova prova, Integer numeroAtleta, LocalDateTime horaInicio, LocalDateTime horaFim, StatusCronometragem statusCrono, Pagina pagina) throws NegocioException {
        List<ParametroPesquisa> parametros = GeralUtil.criarLista(new ParametroPesquisa("prova", prova, ParametroPesquisa.Operador.IGUAL));
        if (numeroAtleta != null) {
            parametros.add(new ParametroPesquisa("numeroAtleta", numeroAtleta, ParametroPesquisa.Operador.IGUAL));
        }
        if (horaInicio != null) {
            parametros.add(new ParametroPesquisa("horaRegistro", horaInicio, ParametroPesquisa.Operador.MAIOR_IGUAL));
        }
        if (horaFim != null) {
            parametros.add(new ParametroPesquisa("horaRegistro", horaFim, ParametroPesquisa.Operador.MENOR_IGUAL));
        }
        this.adicionarParametroStatusCronometragem(parametros, statusCrono);
        if (pagina == null) {
            return this.pesquisarCronometragemPorParametro(parametros, true, "horaRegistro");
        } else {
            return this.pesquisarCronometragemPorParametroPaginada(parametros, pagina, true, "horaRegistro");
        }
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    private List<Cronometragem> pesquisarCronometragemAtiva(Prova prova) throws NegocioException {
        return this.pesquisarCronometragemPorNumeroAtletaStatus(prova, null, StatusCronometragem.ATIVA);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<Cronometragem> pesquisarCronometragemSemInscricaoStatus(Prova prova, StatusCronometragem statusCronometragem) throws NegocioException {
        List<ParametroPesquisa> parametros = GeralUtil.criarLista(new ParametroPesquisa("prova", prova, ParametroPesquisa.Operador.IGUAL));
        parametros.add(new ParametroPesquisa("atletaInscricao", null, ParametroPesquisa.Operador.IGUAL));
        this.adicionarParametroStatusCronometragem(parametros, statusCronometragem);
        return this.pesquisarCronometragemPorParametro(parametros, true, "numeroAtleta", "horaRegistro");
    }

    @Transactional(propagation = Propagation.SUPPORTS, rollbackFor = Exception.class)
    private List<Cronometragem> pesquisarCronometragemPorParametroPaginada(List<ParametroPesquisa> parametros, Pagina pagina, boolean ordemCrescente, String... atributosOrdenacao) throws NegocioException {
        try {
            if (pagina.getTotalItens() == null) {
                pagina.setTotalItens(this.cronometragemDAO.pesquisarTotalRegistrosPesquisaPorParametros(parametros));
            }
            return this.cronometragemDAO.pesquisarPorParametrosPaginada(parametros, pagina.getNumeroPagina(), pagina.getItensPorPagina(), ordemCrescente, atributosOrdenacao);
        } catch (PersistenciaException ex) {
            LOG.error("", ex);
            throw new NegocioException(ex);
        }
    }

    @Transactional(propagation = Propagation.SUPPORTS, rollbackFor = Exception.class)
    @Override
    public List<Cronometragem> pesquisarCronometragemPorParametro(List<ParametroPesquisa> parametros, boolean ordemCrescente, String... atributosOrdenacao) throws NegocioException {
        try {
            return this.cronometragemDAO.pesquisarPorParametros(parametros, ordemCrescente, atributosOrdenacao);
        } catch (PersistenciaException ex) {
            LOG.error("", ex);
            throw new NegocioException(ex);
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public Prova encerrarCronometragem(Prova prova) throws NegocioException, AvisoNegocioException {
        if (prova.getStatus() == StatusProva.CRONOMETRANDO_TODAS_LARGADAS) {
            prova = verificarInconsitenciasCronometragem(prova);
            prova.setStatus(StatusProva.ENCERRADA_APURANDO_RESULTADOS);
            return this.secretario.atualizarProva(prova);
        } else {
            throw new AvisoNegocioException("Não foram iniciadas todas as largadas, a cronometragem ainda não pode ser fechada.");
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public Prova verificarInconsitenciasCronometragem(Prova prova) throws NegocioException, AvisoNegocioException {
        Set<PendenciaProvaCrono> pendencias = this.calcularTempoVoltaProvaEInconsistencias(prova);

        if (this.verificarAtletaSemCronometragem(prova)) {
            pendencias.add(PendenciaProvaCrono.ATLETA_NAO_COMPLETOU);
        }
        prova.setListaMotivoPendencia(pendencias);
        return this.secretario.atualizarProva(prova);
    }

    /**
     * Busca por todas as cronometragens da prova e calcula o tempo de cada
     * volta, assim como o numero da volta para cada atleta
     *
     * @param prova
     * @return
     * @throws com.taurus.exception.NegocioException
     */
    @Transactional(propagation = Propagation.SUPPORTS, rollbackFor = Exception.class)
    @Override
    public Set<PendenciaProvaCrono> calcularTempoVoltaProvaEInconsistencias(Prova prova) throws NegocioException {
        Set<PendenciaProvaCrono> pendencias = new HashSet<>();
        List<Cronometragem> listaCronometragem = this.pesquisarCronometragemAtiva(prova);
        Map<Integer, List<Cronometragem>> cronoPorAtletaMap = new HashMap<>();

        // Organiza uma lista de cronometragem para cada numero de atleta
        for (Cronometragem crono : listaCronometragem) {
            List<Cronometragem> listaCronoPorAtleta;
            if (cronoPorAtletaMap.containsKey(crono.getNumeroAtleta())) {
                listaCronoPorAtleta = cronoPorAtletaMap.get(crono.getNumeroAtleta());
            } else {
                listaCronoPorAtleta = new ArrayList();
                cronoPorAtletaMap.put(crono.getNumeroAtleta(), listaCronoPorAtleta);
            }
            listaCronoPorAtleta.add(crono);
        }

        for (List<Cronometragem> listaCronoPorAtleta : cronoPorAtletaMap.values()) {
            pendencias.addAll(Rolex.this.calcularTempoVoltaAtletaEInconsistencias(listaCronoPorAtleta));
        }
        return pendencias;
    }

    @Transactional(propagation = Propagation.SUPPORTS, rollbackFor = Exception.class)
    @Override
    public Set<PendenciaProvaCrono> calcularTempoVoltaAtletaEInconsistencias(AtletaInscricao atletaInscricao) throws NegocioException {
        return this.calcularTempoVoltaAtletaEInconsistencias(this.pesquisarCronometragemPorAtletaStatus(atletaInscricao, StatusCronometragem.ATIVA));
    }

    @Transactional(propagation = Propagation.SUPPORTS, rollbackFor = Exception.class)
    @Override
    public Set<PendenciaProvaCrono> calcularTempoVoltaAtletaEInconsistencias(List<Cronometragem> listaCronoPorAtleta) throws NegocioException {
        Set<PendenciaProvaCrono> pendencias = new HashSet<>();
        if (listaCronoPorAtleta != null && !listaCronoPorAtleta.isEmpty()) {
            listaCronoPorAtleta.sort(new CronometragemHoraRegistroComparator());
            AtletaInscricao atletaInscricao = listaCronoPorAtleta.get(0).getAtletaInscricao();
            if (atletaInscricao == null) {
                pendencias.add(PendenciaProvaCrono.ATLETA_SEM_INSCRICAO);
            } else {
                atletaInscricao.setNumeroVolta(listaCronoPorAtleta.size());
                if (atletaInscricao.getCategoria().getPercurso().getNumeroVolta() > listaCronoPorAtleta.size()) {
                    if (atletaInscricao.getStatusCorrida() != StatusAtletaCorrida.DNF_CONFIRMADO) {
                        this.atualizarStatusAtletaProva(atletaInscricao, StatusAtletaCorrida.DNF);
                        pendencias.add(PendenciaProvaCrono.ATLETA_NAO_COMPLETOU);
                    }
                } else if (atletaInscricao.getCategoria().getPercurso().getNumeroVolta() < listaCronoPorAtleta.size()) {
                    this.atualizarStatusAtletaProva(atletaInscricao, StatusAtletaCorrida.VOLTA_A_MAIS);
                    pendencias.add(PendenciaProvaCrono.ATLETA_VOLTA_MAIS);
                } else if (atletaInscricao.getStatusCorrida() != StatusAtletaCorrida.COMPLETOU) {
                    this.atualizarStatusAtletaProva(atletaInscricao, StatusAtletaCorrida.COMPLETOU);
                }
                this.calcularTempoProvaAtleta(atletaInscricao, listaCronoPorAtleta.get(listaCronoPorAtleta.size() - 1));
            }

            for (int i = 0; i < listaCronoPorAtleta.size(); i++) {
                Cronometragem crono = listaCronoPorAtleta.get(i);
                LocalDateTime horaInicioVolta;
                if (i == 0) {
                    horaInicioVolta = crono.getAtletaInscricao() == null ? null : crono.getAtletaInscricao().getCategoria().getLargada().getHoraInicio();
                } else {
                    horaInicioVolta = listaCronoPorAtleta.get(i - 1).getHoraRegistro();
                }

                if (horaInicioVolta != null) {
                    if (horaInicioVolta.isAfter(crono.getHoraRegistro())) {
                        pendencias.add(PendenciaProvaCrono.CRONO_ANTERIOR_LARGADA);
                        crono.setTempoVolta(null);
                    } else {
                        crono.setTempoVolta(ChronoUnit.MILLIS.between(horaInicioVolta, crono.getHoraRegistro()));
                        //crono.setTempoVolta(GeralUtil.calcularDiferencaTempo(horaInicioVolta, crono.getHoraRegistro()));
                    }
                }
                crono.setVolta(i + 1);
                crono.setAtletaInscricao(atletaInscricao);
                this.atualizarCronometragem(crono);
            }
        }
        return pendencias;
    }

    @Transactional(propagation = Propagation.SUPPORTS, rollbackFor = Exception.class)
    private boolean verificarAtletaSemCronometragem(Prova prova) throws NegocioException {
        List<AtletaInscricao> atletas = this.pesquisarAtletasSemCronometragem(prova);
        boolean atletaSemDnfConfirmado = false;
        for (AtletaInscricao atletaInscricao : atletas) {
            if (atletaInscricao.getStatusCorrida() != StatusAtletaCorrida.DNF_CONFIRMADO) {
                atletaInscricao = this.atualizarStatusAtletaProva(atletaInscricao, StatusAtletaCorrida.DNF);
                atletaSemDnfConfirmado = true;
            }
        }
        return atletaSemDnfConfirmado;
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    private List<AtletaInscricao> pesquisarAtletasSemCronometragem(Prova prova) throws NegocioException {
        try {
            return this.atletaInscricaoDAO.pesquisarAtletasClassificadosSemCronometragem(prova);
        } catch (PersistenciaException ex) {
            LOG.error("", ex);
            throw new NegocioException(ex);
        }
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<AtletaInscricao> pesquisarAtletaProva(Prova prova, StatusAtletaCorrida statusAtleta) throws NegocioException {
        List<ParametroPesquisa> parametros = GeralUtil.criarLista(new ParametroPesquisa("statusCorrida", statusAtleta, ParametroPesquisa.Operador.IGUAL));
        parametros.add(new ParametroPesquisa("categoria.prova", prova, ParametroPesquisa.Operador.IGUAL));
        try {
            return this.atletaInscricaoDAO.pesquisarPorParametros(parametros, true, "atleta.nome");
        } catch (PersistenciaException ex) {
            LOG.error("", ex);
            throw new NegocioException(ex);
        }
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<AtletaInscricao> pesquisarAtletaProvaComCronometragemAtivaAnteriorLargada(Prova prova) throws NegocioException {
        try {
            return this.atletaInscricaoDAO.pesquisarAtletasComCronometragemAtivaAnteriorLargada(prova);
        } catch (PersistenciaException ex) {
            LOG.error("", ex);
            throw new NegocioException(ex);
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    private AtletaInscricao atualizarStatusAtletaProva(AtletaInscricao atletaInscricao, StatusAtletaCorrida statusAtleta) throws NegocioException {
        atletaInscricao.setStatusCorrida(statusAtleta);
        return this.secretario.atualizarAtletaInscricao(atletaInscricao);
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    private void calcularTempoProvaAtleta(AtletaInscricao atletaInscricao, Cronometragem ultimaCronometragemAtleta) throws NegocioException {
        long milisegundos = ChronoUnit.MILLIS.between(atletaInscricao.getCategoria().getLargada().getHoraInicio(), ultimaCronometragemAtleta.getHoraRegistro());
        atletaInscricao.setTempo(milisegundos);

        if (atletaInscricao.getDupla() != null) {
            if (atletaInscricao.getDupla().getTempo() == null) {
                atletaInscricao.setTempoDupla(milisegundos);
            } else {
                long tempoDupla = atletaInscricao.getDupla().getTempo();
                if (tempoDupla > milisegundos) {
                    atletaInscricao.setTempoDupla(tempoDupla);
                } else {
                    atletaInscricao.setTempoDupla(milisegundos);
                    atletaInscricao.getDupla().setTempoDupla(milisegundos);
                    this.secretario.atualizarAtletaInscricao(atletaInscricao.getDupla());
                }
            }
        }

        this.secretario.atualizarAtletaInscricao(atletaInscricao);
    }

    private void adicionarParametroStatusCronometragem(List<ParametroPesquisa> parametros, StatusCronometragem statusCrono) {
        if (parametros != null && statusCrono != null) {
            switch (statusCrono) {
                case ATIVA:
                    parametros.add(new ParametroPesquisa("excluida", false, ParametroPesquisa.Operador.IGUAL));

                    break;
                case DUVIDA:
                    parametros.add(new ParametroPesquisa("duvida", true, ParametroPesquisa.Operador.IGUAL));

                    break;
                case EXCLUIDA:
                    parametros.add(new ParametroPesquisa("excluida", true, ParametroPesquisa.Operador.IGUAL));
                    break;
            }
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    private void removerTempoAtleta(AtletaInscricao atletaInscricao) throws NegocioException {
        atletaInscricao.setStatusCorrida(StatusAtletaCorrida.DNF);
        atletaInscricao.setTempo(null);
        atletaInscricao.setPosicaoCategoria(null);
        atletaInscricao.setPosicaoGeral(null);
        this.secretario.atualizarAtletaInscricao(atletaInscricao);
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    private void verificarCronometragemAtleta(Prova prova, Integer numeroAtleta, AtletaInscricao atletaInscricao) throws NegocioException {
        List<Cronometragem> cronometragensAtivas = this.pesquisarCronometragemPorNumeroAtletaStatus(prova, numeroAtleta, StatusCronometragem.ATIVA);
        if (cronometragensAtivas.isEmpty()) {
            if (atletaInscricao != null) {
                this.removerTempoAtleta(atletaInscricao);
            }
        } else {
            this.calcularTempoVoltaAtletaEInconsistencias(cronometragensAtivas);
        }
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public boolean carregarImagemCronometragem(Cronometragem cronometragem) throws NegocioException {
        try {
            this.cronometragemDAO.atacharEntidade(cronometragem);

            if (cronometragem.getCronometragemImagem() == null) {
                return false;
            } else {
                CronometragemImagem ci = this.cronometragemImagemDAO.pesquisarPorId(cronometragem.getCronometragemImagem().getId());
                cronometragem.setCronometragemImagem(ci);
                return true;
            }
        } catch (PersistenciaException ex) {
            LOG.error("", ex);
            throw new NegocioException(ex);
        }
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    private Long pesquisarTotalAtletaCompletou(CategoriaDaProva cdp) throws NegocioException {
        try {
            return this.atletaInscricaoDAO.pesquisarTotalAtletaCompletouProva(cdp);
        } catch (PersistenciaException ex) {
            LOG.error("", ex);
            throw new NegocioException(ex);
        }
    }

    // TODO
    // Essa regra nao contempla a mudanca de cronometragem de atleta, troca de atleta, exclusao de cronometragem, etc...
    private void atualizarListaAtletaCorrendo(AtletaInscricao atletaInscricao) throws NegocioException {
        switch (atletaInscricao.getStatusCorrida()) {
            case COMPLETOU:
            case VOLTA_A_MAIS:
                for (CategoriaProvaResumo categoriaProvaResumo : this.getListaAtletasCorrendo(atletaInscricao.getCategoria().getProva())) {
                    if (categoriaProvaResumo.getCategoriaDaProva().equals(atletaInscricao.getCategoria())) {
                        categoriaProvaResumo.setConcluiramProva(categoriaProvaResumo.getConcluiramProva() + 1);
                    }
                }
                break;
        }
    }

}
