package com.taurus.racingTiming.controller.atleta;

import com.taurus.racingTiming.entidade.atleta.Atleta;
import com.taurus.util.ListaConstantesBase.Operacao;

public interface IAtletaController {

    public void setOperacao(Operacao op);

    public void setAtletaEdicao(Atleta atletaEdicao);

    public void setSalvarFechar(Boolean salvarFechar);

    public Atleta getNovoAtleta();

    public void setNovoAtleta(Atleta atleta);
}
