package com.taurus.racingTiming.pojo;

import com.taurus.racingTiming.entidade.corrida.Prova;

/**
 *
 * @author Diego
 */
public class ProvaNumeracaoRepetida {

    private Prova prova;
    private Integer numeroAtleta;
    private Integer repeticoes;

    public ProvaNumeracaoRepetida(Prova prova, Integer numeroAtleta, Integer repeticoes) {
        this.prova = prova;
        this.numeroAtleta = numeroAtleta;
        this.repeticoes = repeticoes;
    }

    public Prova getProva() {
        return prova;
    }

    public void setProva(Prova prova) {
        this.prova = prova;
    }

    public Integer getNumeroAtleta() {
        return numeroAtleta;
    }

    public void setNumeroAtleta(Integer numeroAtleta) {
        this.numeroAtleta = numeroAtleta;
    }

    public Integer getRepeticoes() {
        return repeticoes;
    }

    public void setRepeticoes(Integer repeticoes) {
        this.repeticoes = repeticoes;
    }

}
