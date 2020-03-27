package com.taurus.racingTiming.controller.atleta;

import com.taurus.racingTiming.entidade.atleta.Atleta;

/**
 *
 * @author Diego
 */
public interface IPopupAtletaController {

    public Atleta getAtletaSelecionado();

    public void setNomeAtleta(String nomeAtleta);

}
