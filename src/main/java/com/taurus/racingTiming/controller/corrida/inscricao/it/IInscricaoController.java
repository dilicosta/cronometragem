package com.taurus.racingTiming.controller.corrida.inscricao.it;

import com.taurus.racingTiming.entidade.corrida.AtletaInscricao;
import com.taurus.racingTiming.entidade.corrida.Prova;
import com.taurus.util.ListaConstantesBase.Operacao;

public interface IInscricaoController {

    public void setOperacao(Operacao operacao);

    public void setProva(Prova prova);

    public void setAtletaInscricaoEdicao(AtletaInscricao atletaInscricaoEdicao);

    public void setSalvarFechar(Boolean salvarFechar);

    public AtletaInscricao getAtletaInscricaoNovo();

    public void setAtletaInscricaoNovo(AtletaInscricao inscricao);
    
    public void setEdicaoApuracao(Boolean edicaoApuracao);

}
