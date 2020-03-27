package com.taurus.racingTiming.util;

import com.taurus.util.ExpressaoRegularUtil;

/**
 * Classe con listas de constantes
 *
 * @author Diego Lima
 */
public class ListaConstantes {

    public enum Stages {
        PRIMARY_STAGE, CONF_CAMERA, TESTE_CAMERA,
        FEDERACAO, REPRESENTANTE_FEDERACAO, ORGANIZACAO_PROVA, REPRESENTANTE_ORGANIZACAO_PROVA,
        CADASTRO_ATLETA, ATLETA, POPUP_ATLETA, POPUP_ATLETA_INSCRICAO,
        CADASTRO_PROVA, PROVA, POPUP_PROVA,
        STATUS_PROVA_PESQUISA, STATUS_PROVA, PROVA_CATEGORIA_ATLETA,
        STATUS_ATLETA_PROVA, CADASTRO_IMPORTAR_INSCRICAO,
        CADASTRO_INSCRICAO, INSCRICAO, IMPORTAR_INSCRICAO, ASSOCIAR_CATEGORIA_XLS,
        CADASTRO_CATEGORIA_ATLETA, CATEGORIA_ATLETA, PENDENCIA_INSCRICAO, INSCRICAO_NUM_AUTOMATICA,
        INSCRICAO_NUM_MANUAL, PROVA_DEMO,
        CADASTRO_CRONOMETRAGEM, CRONOMETRAGEM, CADASTRO_PROV_CRONO_PEND, PROVA_CRONO_PENDENCIA,
        CRONOMETRAGEM_EDICAO, CRONOMETRAGEM_ADD_ATLETA, CRONOMETRAGEM_PUPUP, CLASSIFICACAO_TEMPORARIA,
        CADASTRO_RESULTADO, RESULTADO_CRONOMETRAGEM, RESULTADO_CLASSIFICACAO, RESULTADO_RELATORIO;
    }

    public enum TipoSanguineo {
        A_POS("A+"), A_NEG("A-"), B_POS("B+"), B_NEG("B-"), AB_POS("AB+"), AB_NEG("AB-"), O_POS("O+"), O_NEG("O-");

        private final String nome;

        private TipoSanguineo(String nome) {
            this.nome = nome;
        }

        public String getNome() {
            return nome;
        }

    }

    public enum GrauDificuldade {
        FACIL("Fácil"), MEDIO("Médio"), DIFICIL("Difícil"), MUITO_DIFICIL("Muito Difícil");

        private final String descricao;

        private GrauDificuldade(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }
    }

    public enum StatusProva {
        FORNO("Em construção"),
        INSCRICAO_ABERTA("Inscrições abertas"),
        INSCRICAO_ENCERRADA_PENDENTE("Inscrições encerradas. Cadastro(s) pendente(s)"),
        INSCRICAO_FECHADA("Inscrições encerradas"),
        CRONOMETRANDO_PARCIAL_LARGADA("Cronometragem em andamento. Largada(s) pendente(s)"),
        CRONOMETRANDO_TODAS_LARGADAS("Cronometragem em andamento"),
        ENCERRADA_APURANDO_RESULTADOS("Encerrada: apurando resultados"),
        ENCERRADA_RESULTADOS_CONCLUIDOS("Encerrada: resultados concluídos"),
        FINALIZADA("Finalizada"),
        CANCELADA("Cancelada");

        private final String descricao;

        private StatusProva(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }
    }

    public enum StatusAtletaCorrida {
        AGUARDANDO("Aguardando"),
        COMPLETOU("Completou"),
        DNF("Não compareceu ou não completou"),
        VOLTA_A_MAIS("Com voltas a mais"),
        DESCLASSIFICADO("Desclassificado"),
        DNF_CONFIRMADO("D.N.F. confirmado");

        private final String descricao;

        private StatusAtletaCorrida(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }
    }

    public enum StatusCronometragem {
        ATIVA, EXCLUIDA, DUVIDA;
    }

    public enum PendenciaInscricao {
        ATLETA_SEM_NUMERO("Atleta(s) sem numeração"),
        ATLETA_NUMERACAO_REPETIDA("Atletas com numerações repetidas"),
        ATLETA_SEM_DUPLA("Atleta(s) sem dupla definida");

        private final String descricao;

        private PendenciaInscricao(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }
    }

    public enum PendenciaProvaCrono {
        ATLETA_NAO_COMPLETOU("Atleta não completou ou não compareceu"),
        ATLETA_VOLTA_MAIS("Atleta com voltas a mais"),
        ATLETA_SEM_INSCRICAO("Atleta sem inscrição"),
        CRONO_ANTERIOR_LARGADA("Cronometragem antes da largada"),
        CRONO_DUVIDA("Cronometragem marcada como dúvida");

        private final String descricao;

        private PendenciaProvaCrono(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }
    }

    public enum EstiloCss {
        BOTAO_CRONOMETRO("button-cronometro"),
        BOTAO_CRONOMETRO_ADICIONAR("button-cronometro"),
        BOTAO_CRONOMETRO_REMOVER("button-remove-cronometro"),
        BOTAO_CLASSIFICACAO("button-classificacao"),
        BOTAO_DESCLASSIFICACAO("button-desclassificacao"),
        BOTAO_PESQUISAR_COMPUTADOR("button-pesquisar-computador"),
        BOTAO_BANDEIRA_CHEGADA("button-bandeira-chegada"),
        BOTAO_NAO_BANDEIRA_CHEGADA("button-nao-bandeira-chegada"),
        BOTAO_NAO_BANDEIRA_CHEGADA_VERDE("button-nao-bandeira-chegada-verde"),
        BOTAO_ON("button-controle-on"), BOTAO_OFF("button-controle-off"), BOTAO_REC("button-controle-rec"),
        BOTAO_PAUSE("button-controle-pause"), BOTAO_STOP("button-controle-stop"),
        BOTAO_PLAY("button-controle-play"),
        BOTAO_RELATORIO("button-relatorio");

        private final String valor;

        private EstiloCss(String valor) {
            this.valor = valor;
        }

        public String getValor() {
            return valor;
        }
    }

    public enum NumeracaoAutomatica {
        NA("Não se aplica"),
        SEQUENCIAL("Sequencial"),
        POR_FAIXA("Por faixa");

        private String descricao;

        private NumeracaoAutomatica(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }
    }

    public static void main(String args[]) {
        ExpressaoRegularUtil er = new ExpressaoRegularUtil("99:99");
        System.out.println(er.getExpressaoRegularValidacaoParcial());
    }
}
