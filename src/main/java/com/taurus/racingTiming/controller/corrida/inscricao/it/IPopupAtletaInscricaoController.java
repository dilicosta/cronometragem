package com.taurus.racingTiming.controller.corrida.inscricao.it;

import com.taurus.racingTiming.entidade.corrida.AtletaInscricao;
import com.taurus.racingTiming.entidade.corrida.CategoriaDaProva;

/**
 *
 * @author Diego
 */
public interface IPopupAtletaInscricaoController {

    public AtletaInscricao getAtletaInscricaoSelecionado();

    public void setNomeAtleta(String nomeAtleta);
    
    public void setCategoriaDaProva(CategoriaDaProva categoriaDaProva);

}
