package com.taurus.racingTiming.negocio;

import com.taurus.dao.ParametroPesquisa;
import com.taurus.exception.AvisoNegocioException;
import com.taurus.exception.NegocioException;
import com.taurus.exception.PersistenciaException;
import com.taurus.racingTiming.dao.corrida.IAtletaInscricaoDAO;
import com.taurus.racingTiming.entidade.corrida.AtletaInscricao;
import com.taurus.racingTiming.entidade.corrida.CategoriaDaProva;
import com.taurus.racingTiming.entidade.corrida.Percurso;
import com.taurus.racingTiming.entidade.corrida.Prova;
import com.taurus.racingTiming.negocio.comparador.AtletaInscricaoComparadorClassificacao;
import com.taurus.racingTiming.util.ListaConstantes;
import com.taurus.util.ListaConstantesBase;
import com.taurus.util.ListaConstantesBase.Sexo;
import com.taurus.util.Pagina;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.validator.GenericValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Classe responsavel pela apresentacao das classificacoes e resultados
 *
 * @author Diego Lima
 */
@Component
public class Juiz implements IJuiz {

    private static final Log LOG = LogFactory.getLog(Juiz.class);

    @Autowired
    private ISecretario secretario;
    @Autowired
    private IRolex rolex;

    @Autowired
    private IAtletaInscricaoDAO<AtletaInscricao> atletaInscricaoDAO;

    private Map<String, Map<AtletaInscricaoComparadorClassificacao, AtletaInscricao>> mapClassificacaoTemporaria = new HashMap<>();

    @Override
    public Map<String, Map<AtletaInscricaoComparadorClassificacao, AtletaInscricao>> getMapClassificacaoTemporaria() {
        return mapClassificacaoTemporaria;
    }

    @Override
    public void adicionarAtletaClassificacaoTemporaria(AtletaInscricao atletaInscricao) {
        if (atletaInscricao != null) {
//            Sexo sexoCategoria = atletaInscricao.getCategoria().getCategoriaAtleta().getSexo();
//            
//            if (sexoCategoria != Sexo.NAO_SE_APLICA) {
//                String keyGeral = "Geral " + atletaInscricao.getCategoria().getPercurso().getNome() + " " + sexoCategoria.toString();
//                Map<Integer, AtletaInscricao> classificacaoGeral = this.mapClassificacaoTemporaria.get(keyGeral);
//                if (classificacaoGeral == null) {
//                    classificacaoGeral = new TreeMap<>();
//                    this.mapClassificacaoTemporaria.put(keyGeral, classificacaoGeral);
//                }
//                classificacaoGeral.put(atletaInscricao.getNumeroAtleta(), atletaInscricao);
//            }

            String keyCategoria = atletaInscricao.getCategoria().getCategoriaAtleta().getNome();
            Map<AtletaInscricaoComparadorClassificacao, AtletaInscricao> classificacaoCategoria = this.mapClassificacaoTemporaria.get(keyCategoria);
            if (classificacaoCategoria == null) {
                classificacaoCategoria = new TreeMap<>();
                this.mapClassificacaoTemporaria.put(keyCategoria, classificacaoCategoria);
            }

            // Nao identifiquei o pq, mesmo implementando regra no equal e no compareTo da chave
            // as vezes o map nao encontra a chave.
            AtletaInscricaoComparadorClassificacao aicc = new AtletaInscricaoComparadorClassificacao(atletaInscricao);
            AtletaInscricaoComparadorClassificacao chaveExistente = null;
            for (AtletaInscricaoComparadorClassificacao ai : classificacaoCategoria.keySet()) {
                if (aicc.equals(ai)) {
                    chaveExistente = ai;
                }
            }
            if (chaveExistente != null) {
                classificacaoCategoria.remove(chaveExistente);
            }
            classificacaoCategoria.put(aicc, atletaInscricao);
        }
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public void carregarClassificacaoTemporiaria(Prova prova) throws NegocioException {
        List<ParametroPesquisa> parametros = new ArrayList();
        parametros.add(new ParametroPesquisa("categoria.prova", prova, ParametroPesquisa.Operador.IGUAL));
        parametros.add(new ParametroPesquisa("tempo", null, ParametroPesquisa.Operador.DIFERENTE));
        List<AtletaInscricao> atletas = this.secretario.pesquisarAtletaInscricaoPorParametros(parametros, false);
        for (AtletaInscricao atletaInscricao : atletas) {
            this.adicionarAtletaClassificacaoTemporaria(atletaInscricao);
        }
    }

    @Override
    public Integer pesquisarPosicaoTemporariaAtleta(AtletaInscricao atletaInscricao
    ) {
        String keyCategoria = atletaInscricao.getCategoria().getCategoriaAtleta().getNome();
        Map<AtletaInscricaoComparadorClassificacao, AtletaInscricao> mapCategoria = this.mapClassificacaoTemporaria.get(keyCategoria);
        if (mapCategoria != null) {
            int posicao = 1;
            for (AtletaInscricao ai : mapCategoria.values()) {
                if (Objects.equals(ai.getNumeroAtleta(), atletaInscricao.getNumeroAtleta())) {
                    return posicao;
                } else {
                    posicao++;
                }
            }
        }
        return null;
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public AtletaInscricao desclassificarAtleta(AtletaInscricao atletaInscricao, String motivoDesclassificacao) throws NegocioException {
        atletaInscricao.setStatusCorrida(ListaConstantes.StatusAtletaCorrida.DESCLASSIFICADO);
        atletaInscricao.setMotivoDesclassificacao(motivoDesclassificacao);
        return this.secretario.atualizarAtletaInscricao(atletaInscricao);
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public AtletaInscricao desfazerDesclassificacaoAtleta(AtletaInscricao atletaInscricao) throws NegocioException {
        atletaInscricao.setStatusCorrida(ListaConstantes.StatusAtletaCorrida.COMPLETOU);
        atletaInscricao.setMotivoDesclassificacao(null);
        atletaInscricao = this.secretario.atualizarAtletaInscricao(atletaInscricao);
        this.rolex.calcularTempoVoltaAtletaEInconsistencias(atletaInscricao);
        return atletaInscricao;
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public AtletaInscricao confirmarDnfAtleta(AtletaInscricao atletaInscricao) throws NegocioException {
        atletaInscricao.setStatusCorrida(ListaConstantes.StatusAtletaCorrida.DNF_CONFIRMADO);
        return this.secretario.atualizarAtletaInscricao(atletaInscricao);
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public AtletaInscricao desfazerConfirmarDnfAtleta(AtletaInscricao atletaInscricao) throws NegocioException {
        atletaInscricao.setStatusCorrida(ListaConstantes.StatusAtletaCorrida.DNF);
        atletaInscricao = this.secretario.atualizarAtletaInscricao(atletaInscricao);
        this.rolex.calcularTempoVoltaAtletaEInconsistencias(atletaInscricao);
        return atletaInscricao;
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public Prova reabrirCronometragem(Prova prova) throws NegocioException, AvisoNegocioException {
        switch (prova.getStatus()) {
            case ENCERRADA_APURANDO_RESULTADOS:
                prova.setStatus(ListaConstantes.StatusProva.CRONOMETRANDO_TODAS_LARGADAS);
                return this.secretario.atualizarProva(prova);
            default:
                throw new AvisoNegocioException("A cronometragem da prova não pode ser reaberta, pois a mesma não foi finalizada.");
        }
    }

    @Override
    public Prova reabrirApuracao(Prova prova) throws NegocioException, AvisoNegocioException {
        switch (prova.getStatus()) {
            case ENCERRADA_RESULTADOS_CONCLUIDOS:
                prova.setStatus(ListaConstantes.StatusProva.ENCERRADA_APURANDO_RESULTADOS);
                return this.secretario.atualizarProva(prova);
            default:
                throw new AvisoNegocioException("A apuração da prova não pode ser reaberta, pois a mesma não foi finalizada.");
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public void criarClassificacao(Prova prova) throws NegocioException {
        List<Percurso> listaPercurso = this.secretario.pesquisarPercurso(prova);
        for (Percurso percurso : listaPercurso) {
            for (Sexo sexo : Sexo.values()) {
                List<AtletaInscricao> listaAtletas = this.pesquisarClassificacaoProvaGeral(percurso, sexo);
                if (!listaAtletas.isEmpty()) {
                    AtletaInscricao primeiroGeral = null;
                    for (AtletaInscricao atletaInscricao : listaAtletas) {
                        if (atletaInscricao.getStatusCorrida() == ListaConstantes.StatusAtletaCorrida.COMPLETOU) {
                            primeiroGeral = atletaInscricao;
                            break;
                        }
                    }
                    if (primeiroGeral != null) {
                        this.criarClassificacaoPorCategoriaEGeral(primeiroGeral, listaAtletas);
                    }
                }
            }
        }

        List<CategoriaDaProva> listaCategoriaDupla = this.secretario.pesquisarCategoriaDupla(prova);
        for (CategoriaDaProva categoria : listaCategoriaDupla) {
            List<AtletaInscricao> listaAtletas = this.pesquisarClassificacaoProvaCategoria(categoria);
            this.criarClassificacaoPorCategoriaDeDupla(listaAtletas);
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public Prova finalizarApuracao(Prova prova) throws NegocioException, AvisoNegocioException {
        if (prova.getStatus() != ListaConstantes.StatusProva.ENCERRADA_APURANDO_RESULTADOS) {
            throw new AvisoNegocioException("A apuração de resultados não está aberta para ser finalizada.");
        }
        prova = this.rolex.verificarInconsitenciasCronometragem(prova);
        if (!GenericValidator.isBlankOrNull(prova.getMotivoPendencia())) {
            throw new AvisoNegocioException(("A apuração não pode ser finalizada, pois existem inconsistências na cronometragem."));
        }
        this.criarClassificacao(prova);
        prova.setStatus(ListaConstantes.StatusProva.ENCERRADA_RESULTADOS_CONCLUIDOS);
        return this.secretario.atualizarProva(prova);
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public Prova finalizarProva(Prova prova) throws NegocioException, AvisoNegocioException {
        if (prova.getStatus() != ListaConstantes.StatusProva.ENCERRADA_RESULTADOS_CONCLUIDOS) {
            throw new AvisoNegocioException("A prova não pode ser finaliza. Verifique se não existe pendências.");
        }
        prova.setStatus(ListaConstantes.StatusProva.FINALIZADA);
        return this.secretario.atualizarProva(prova);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<AtletaInscricao> pesquisarClassificacaoProvaGeral(Percurso percurso, Sexo sexo) throws NegocioException {
        return this.pesquisarClassificacaoProvaGeral(percurso, sexo, null);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<AtletaInscricao> pesquisarClassificacaoProvaGeral(Percurso percurso, Sexo sexo,
            Pagina pagina) throws NegocioException {
        List<ParametroPesquisa> parametros = new ArrayList();
        parametros.add(new ParametroPesquisa("categoria.percurso", percurso, ParametroPesquisa.Operador.IGUAL));
        parametros.add(new ParametroPesquisa("categoria.categoriaAtleta.sexo", sexo, ParametroPesquisa.Operador.IGUAL));
        parametros.add(new ParametroPesquisa("categoria.categoriaAtleta.categoriaDupla", false, ParametroPesquisa.Operador.IGUAL));
        if (pagina == null) {
            return this.secretario.pesquisarAtletaInscricaoPorParametros(parametros, true, "statusCorrida", "tempo");
        } else {
            return this.secretario.pesquisarAtletaInscricaoPorParametrosPaginada(parametros, pagina, true, "statusCorrida", "tempo");
        }
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<AtletaInscricao> pesquisarClassificacaoProvaCategoria(CategoriaDaProva categoriaDaProva) throws NegocioException {
        return this.pesquisarClassificacaoProvaCategoria(categoriaDaProva, null);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<AtletaInscricao> pesquisarClassificacaoProvaCategoria(CategoriaDaProva categoriaDaProva, Pagina pagina) throws NegocioException {
        List<ParametroPesquisa> parametros = new ArrayList();
        parametros.add(new ParametroPesquisa("categoria", categoriaDaProva, ParametroPesquisa.Operador.IGUAL));
        boolean dupla = categoriaDaProva.getCategoriaAtleta().isCategoriaDupla();
        if (pagina == null) {
            return this.secretario.pesquisarAtletaInscricaoPorParametros(parametros, true, "statusCorrida", (dupla ? "tempo_dupla, tempo" : "tempo"));
        } else {
            return this.secretario.pesquisarAtletaInscricaoPorParametrosPaginada(parametros, pagina, true, "statusCorrida", (dupla ? "tempo_dupla, tempo" : "tempo"));
        }
    }

    @Override
    public List<AtletaInscricao> pesquisarClassificacaoAtleta(Prova prova, String nomeAtleta,
            Pagina pagina) throws NegocioException {
        List<ParametroPesquisa> parametros = new ArrayList<>();
        parametros.add(new ParametroPesquisa("categoria.prova", prova, ParametroPesquisa.Operador.IGUAL));
        parametros.add(new ParametroPesquisa("atleta.nome", nomeAtleta, ParametroPesquisa.Operador.LIKE));
        return this.secretario.pesquisarAtletaInscricaoPorParametrosPaginada(parametros, pagina, true, "statusCorrida", "tempo");
    }

    @Override
    public AtletaInscricao pesquisarClassificacaoAtleta(Prova prova, String numeroAtleta) throws NegocioException {
        List<ParametroPesquisa> parametros = new ArrayList<>();
        parametros.add(new ParametroPesquisa("categoria.prova", prova, ParametroPesquisa.Operador.IGUAL));
        parametros.add(new ParametroPesquisa("numeroAtleta", numeroAtleta, ParametroPesquisa.Operador.IGUAL));
        List<AtletaInscricao> resultado = this.secretario.pesquisarAtletaInscricaoPorParametros(parametros, true, "statusCorrida", "tempo");
        if (!resultado.isEmpty()) {
            return resultado.get(0);
        } else {
            return null;
        }
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    private AtletaInscricao pesquisarPrimeiroGeral(Percurso percurso, ListaConstantesBase.Sexo sexo) throws NegocioException {
        try {
            return this.atletaInscricaoDAO.pesquisarPrimeiroGeral(percurso, sexo);
        } catch (PersistenciaException ex) {
            LOG.error("", ex);
            throw new NegocioException(ex);
        }
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    private void criarClassificacaoPorCategoriaEGeral(AtletaInscricao primeiroGeral, List<AtletaInscricao> listaAtletasDeMesmoPercursoESexo) throws NegocioException {

        Map<CategoriaDaProva, AtletaInscricao> mapAnteriorCategoria = new HashMap<>();
        Map<CategoriaDaProva, Integer> mapPosicaoCategoria = new HashMap<>();

        int posicaoGeral = 1;
        AtletaInscricao anteriorGeral = null;
        for (AtletaInscricao atletaInscricao : listaAtletasDeMesmoPercursoESexo) {
            if (atletaInscricao.getStatusCorrida() == ListaConstantes.StatusAtletaCorrida.COMPLETOU) {
                AtletaInscricao anteriorCategoria;
                CategoriaDaProva categoria = atletaInscricao.getCategoria();
                if (mapAnteriorCategoria.containsKey(categoria)) {
                    anteriorCategoria = mapAnteriorCategoria.get(categoria);
                } else {
                    anteriorCategoria = atletaInscricao;
                    mapPosicaoCategoria.put(categoria, 1);
                }
                Integer posicaoCategoria = mapPosicaoCategoria.get(categoria);
                atletaInscricao.setPosicaoCategoria(posicaoCategoria++);

                atletaInscricao.setPosicaoGeral(posicaoGeral++);

                atletaInscricao.setDiferencaCategoria(atletaInscricao.getTempo() - anteriorCategoria.getTempo());
                if (atletaInscricao.getDiferencaCategoria() < 0) {
                    throw new NegocioException("Diferença negativa de tempo entre o atleta anterior da categoria e o atleta.");
                }

                if (anteriorGeral == null) {
                    atletaInscricao.setDiferencaGeral(0L);
                } else {
                    atletaInscricao.setDiferencaGeral(atletaInscricao.getTempo() - anteriorGeral.getTempo());
                }
                if (atletaInscricao.getDiferencaGeral() < 0) {
                    throw new NegocioException("Diferença negativa de tempo entre o atleta anterior geral e o atleta.");
                }
                this.secretario.atualizarAtletaInscricao(atletaInscricao);

                mapPosicaoCategoria.replace(categoria, posicaoCategoria);
                mapAnteriorCategoria.put(categoria, atletaInscricao);
                anteriorGeral = atletaInscricao;
            }
        }
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    private void criarClassificacaoPorCategoriaDeDupla(List<AtletaInscricao> listaAtletas) throws NegocioException {
        if (listaAtletas != null && !listaAtletas.isEmpty()) {
            int posicao = 1;
            for (int i = 0; i < listaAtletas.size(); i += 2) {
                AtletaInscricao atleta1 = listaAtletas.get(i);
                AtletaInscricao atleta2 = atleta1.getDupla();
                atleta1.setPosicaoGeral(null);
                atleta2.setPosicaoGeral(null);
                atleta1.setPosicaoCategoria(posicao);
                atleta2.setPosicaoCategoria(posicao);
                this.secretario.atualizarAtletaInscricao(atleta1);
                this.secretario.atualizarAtletaInscricao(atleta2);
                posicao++;
            }
        }
    }
}
