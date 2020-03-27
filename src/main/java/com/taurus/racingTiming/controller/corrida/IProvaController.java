package com.taurus.racingTiming.controller.corrida;

import com.taurus.racingTiming.entidade.corrida.Prova;
import com.taurus.util.ListaConstantesBase.Operacao;

public interface IProvaController {

    public void setOperacao(Operacao operacao);

    public void setProvaEdicao(Prova provaEdicao);

    public Prova getProvaEdicao();

}
