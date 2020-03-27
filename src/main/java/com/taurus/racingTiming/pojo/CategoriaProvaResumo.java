package com.taurus.racingTiming.pojo;

import com.taurus.javafx.tableView.LinhaViewComCheckBox;
import com.taurus.racingTiming.entidade.corrida.CategoriaDaProva;

/**
 *
 * @author Diego
 */
public class CategoriaProvaResumo extends LinhaViewComCheckBox {

    private CategoriaDaProva categoriaDaProva;
    private Long inscritos;
    private Long semNumeracao;
    private Long concluiramProva = 0L;

    public CategoriaProvaResumo(CategoriaDaProva categoriaDaProva, Long inscritos) {
        this.categoriaDaProva = categoriaDaProva;
        this.inscritos = inscritos;
    }

    public CategoriaProvaResumo(CategoriaDaProva categoriaDaProva, Long inscritos, Long semNumeracao) {
        this.categoriaDaProva = categoriaDaProva;
        this.inscritos = inscritos;
        this.semNumeracao = semNumeracao;
    }

    public CategoriaDaProva getCategoriaDaProva() {
        return categoriaDaProva;
    }

    public Long getInscritos() {
        return inscritos;
    }

    public void setInscritos(Long inscritos) {
        this.inscritos = inscritos;
    }

    public Long getSemNumeracao() {
        return semNumeracao;
    }

    public void setSemNumeracao(Long semNumeracao) {
        this.semNumeracao = semNumeracao;
    }

    public Long getConcluiramProva() {
        return concluiramProva;
    }

    public void setConcluiramProva(Long concluiramProva) {
        this.concluiramProva = concluiramProva;
    }

    public Long getNaoConcluiramProva() {
        if (this.inscritos != null) {
            return this.inscritos - this.concluiramProva;
        } else {
            return null;
        }
    }
}
