package com.taurus.racingTiming.controller.crono;

import com.taurus.racingTiming.entidade.corrida.AtletaInscricao;
import com.taurus.racingTiming.entidade.corrida.Prova;

public interface ICronometragemAdicionarAtletaController {

    public void setProva(Prova prova);

    public void setNumeroAtleta(Integer numeroAtleta);

    public AtletaInscricao getAtletaInscricao();

}
