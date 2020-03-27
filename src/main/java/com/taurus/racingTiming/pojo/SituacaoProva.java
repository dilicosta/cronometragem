package com.taurus.racingTiming.pojo;

import com.taurus.racingTiming.entidade.corrida.Prova;

/**
 *
 * @author Diego
 */
public class SituacaoProva {

    private Prova prova;
    private Long numeroInscritos;

    public SituacaoProva() {

    }

    public SituacaoProva(Prova prova, Long numeroInscritos) {
        this.prova = prova;
        this.numeroInscritos = numeroInscritos;
    }

    public Prova getProva() {
        return prova;
    }

    public void setProva(Prova nomeProva) {
        this.prova = nomeProva;
    }

    public Long getNumeroInscritos() {
        return numeroInscritos;
    }

    public void setNumeroInscritos(Long numeroInscritos) {
        this.numeroInscritos = numeroInscritos;
    }
}
