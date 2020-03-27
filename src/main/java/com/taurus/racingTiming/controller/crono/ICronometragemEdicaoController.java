package com.taurus.racingTiming.controller.crono;

import com.taurus.racingTiming.entidade.corrida.AtletaInscricao;
import com.taurus.racingTiming.entidade.corrida.Cronometragem;
import com.taurus.util.ListaConstantesBase.Operacao;

public interface ICronometragemEdicaoController {

    public void setOperacao(Operacao operacao);

    public Cronometragem getCronometragemEdicao();

    public void setCronometragemEdicao(Cronometragem cronometragem);

    public void setAtletaInscricao(AtletaInscricao atletaInscricao);

    public Cronometragem getCronometragemNovo();

    public void setCronometragemNovo(Cronometragem cronometragem);

}
