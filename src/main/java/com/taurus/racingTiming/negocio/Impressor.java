package com.taurus.racingTiming.negocio;

import com.taurus.dao.ParametroPesquisa;
import com.taurus.exception.NegocioException;
import com.taurus.racingTiming.entidade.corrida.AtletaInscricao;
import com.taurus.racingTiming.entidade.corrida.CategoriaDaProva;
import com.taurus.racingTiming.entidade.corrida.Percurso;
import com.taurus.racingTiming.entidade.corrida.Prova;
import com.taurus.util.ContextUtil;
import com.taurus.util.ListaConstantesBase.Sexo;
import com.taurus.util.arquivo.ByteManager;
import com.taurus.util.jrp.ReportJasper;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
public class Impressor implements IImpressor {

    private static final Log LOG = LogFactory.getLog(Impressor.class);

    @Autowired
    private IJuiz juiz;
    @Autowired
    private ISecretario secretario;

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public void gerarRelatorioClassificacaoProvaTodasCategorias(Prova prova) throws NegocioException {
        for (CategoriaDaProva cat : this.secretario.pesquisarCategoriaDaProva(prova)) {
            this.gerarRelatorioClassificacaoProvaCategoria(cat);
        }
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public byte[] gerarRelatorioClassificacaoProvaCategoria(CategoriaDaProva categoriaDaProva) throws NegocioException {
        Map parametros = this.criarParametrosBasicos(categoriaDaProva.getProva());
        parametros.put("categoria", categoriaDaProva.getCategoriaAtleta().getNome());

        String nomeRelatorio = "Classficacao_" + categoriaDaProva.getCategoriaAtleta().getNome();
        String pathFile = ContextUtil.getAppContext().getEnvironment().getProperty("rpt.classificacaoCategoria");

        List<AtletaInscricao> atletas = this.juiz.pesquisarClassificacaoProvaCategoria(categoriaDaProva);

        try {
            InputStream jasperInputStream = Impressor.class.getResourceAsStream(pathFile);
            byte[] relatorio = ReportJasper.runPdf(jasperInputStream, parametros, atletas);
            ByteManager.writeFile(this.obterCaminhoRelatorio(categoriaDaProva.getProva()), nomeRelatorio + ".pdf", relatorio);
            return relatorio;
        } catch (IOException ex) {
            throw new NegocioException("", ex);
        }
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public byte[] gerarRelatorioClassificacaoProvaGeral(Percurso percurso, Sexo sexo) throws NegocioException {
        Map parametros = this.criarParametrosBasicos(percurso.getProva());
        parametros.put("percurso", percurso.getNome() + " / " + sexo.getDescricao());

        String nomeRelatorio = "Classificacao geral_" + percurso.getNome() + "_" + sexo.getSigla();
        String pathFile = ContextUtil.getAppContext().getEnvironment().getProperty("rpt.classificacaoGeral");

        List<AtletaInscricao> atletas = this.juiz.pesquisarClassificacaoProvaGeral(percurso, sexo);
        try {
            InputStream jasperInputStream = Impressor.class.getResourceAsStream(pathFile);
            byte[] relatorio = ReportJasper.runPdf(jasperInputStream, parametros, atletas);
            ByteManager.writeFile(this.obterCaminhoRelatorio(percurso.getProva()), nomeRelatorio + ".pdf", relatorio);
            return relatorio;
        } catch (IOException ex) {
            throw new NegocioException("", ex);
        }
    }

    @Override
    public byte[] gerarRelatorioInscritosPorCategoria(Prova prova, CategoriaDaProva categoriaDaProva, Boolean numeracao, OrdenacaoInscritos ordenacaoInscritos) throws NegocioException {
        return this.gerarRelatorioInscritos(prova, categoriaDaProva, true, numeracao, ordenacaoInscritos);
    }

    @Override
    public byte[] gerarRelatorioInscritosGeral(Prova prova, Boolean numeracao, OrdenacaoInscritos ordenacaoInscritos) throws NegocioException {
        return this.gerarRelatorioInscritos(prova, null, false, numeracao, ordenacaoInscritos);
    }

    private byte[] gerarRelatorioInscritos(Prova prova, CategoriaDaProva categoriaDaProva, boolean agruparCategoria, Boolean numeracao, OrdenacaoInscritos ordenacaoInscritos) throws NegocioException {
        Map parametros = this.criarParametrosBasicos(prova);

        List<ParametroPesquisa> parametrosPesquisa = new ArrayList<>();
        if (categoriaDaProva == null) {
            parametrosPesquisa.add(new ParametroPesquisa("categoria.prova", prova, ParametroPesquisa.Operador.IGUAL));
        } else {
            parametrosPesquisa.add(new ParametroPesquisa("categoria", categoriaDaProva, ParametroPesquisa.Operador.IGUAL));
        }
        if (numeracao != null && numeracao) {
            parametrosPesquisa.add(new ParametroPesquisa("numeroAtleta", null, ParametroPesquisa.Operador.DIFERENTE));
        } else if (numeracao != null && !numeracao) {
            parametrosPesquisa.add(new ParametroPesquisa("numeroAtleta", null, ParametroPesquisa.Operador.IGUAL));
        }

        List<AtletaInscricao> atletas;
        String pathFile;
        String nomeRelatorio;
        if (agruparCategoria) {
            atletas = this.secretario.pesquisarAtletaInscricaoPorParametros(parametrosPesquisa, true, "categoria.categoriaAtleta.nome", ordenacaoInscritos == OrdenacaoInscritos.NOME_ATLETA ? "atleta.nome" : "numeroAtleta");
            pathFile = ContextUtil.getAppContext().getEnvironment().getProperty("rpt.inscritosCategoria");
            nomeRelatorio = "Inscricoes por categoria";
        } else {
            atletas = this.secretario.pesquisarAtletaInscricaoPorParametros(parametrosPesquisa, true, ordenacaoInscritos == OrdenacaoInscritos.NOME_ATLETA ? "atleta.nome" : "numeroAtleta");
            pathFile = ContextUtil.getAppContext().getEnvironment().getProperty("rpt.inscritosGeral");
            nomeRelatorio = "Inscricoes geral";
        }
        try {
            InputStream jasperInputStream = Impressor.class.getResourceAsStream(pathFile);
            byte[] relatorio = ReportJasper.runPdf(jasperInputStream, parametros, atletas);
            ByteManager.writeFile(this.obterCaminhoRelatorio(prova), nomeRelatorio + ".pdf", relatorio);
            return relatorio;
        } catch (IOException ex) {
            LOG.error("Erro ao gerar o relatório de inscrições geral", ex);
            throw new NegocioException("", ex);
        }
    }

    private String obterCaminhoRelatorio(Prova prova) {
        return "/Taurus/Corridas/" + prova.getNome() + "/Relatorios/";
    }

    private Map<String, Object> criarParametrosBasicos(Prova prova) {
        Map<String, Object> parametros = new HashMap();
        parametros.put("logo", Impressor.class.getResource(ContextUtil.getAppContext().getEnvironment().getProperty("img.logo")).getFile());
        parametros.put("nomeProva", prova.getNome());
        return parametros;
    }

    public static enum OrdenacaoInscritos {
        NUMERO_ATLETA, NOME_ATLETA;
    }

}
