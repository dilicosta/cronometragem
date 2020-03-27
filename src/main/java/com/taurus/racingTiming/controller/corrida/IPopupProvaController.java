package com.taurus.racingTiming.controller.corrida;

import com.taurus.racingTiming.entidade.corrida.Prova;

/**
 *
 * @author Diego
 */
public interface IPopupProvaController {

    public Prova getProvaSelecionado();

    public void setNomeProva(String nomeProva);

}
