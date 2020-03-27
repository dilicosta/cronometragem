package com.taurus.racingTiming.controller.crono;

import com.taurus.racingTiming.entidade.corrida.Prova;

/**
 *
 * @author Diego
 */
public interface ICronometroController {

    public void setProva(Prova prova);

    public void desligarCamera();

}
