package com.taurus.racingTiming.controller.corrida.inscricao.it;

import com.taurus.racingTiming.entidade.corrida.CategoriaDaProva;
import com.taurus.racingTiming.entidade.corrida.Prova;
import java.io.File;
import java.util.Map;

public interface IInscricaoImportarAssociarCategoriaController {

    public void setProva(Prova prova);

    public void setFile(File file);

    public void setIndiceColunaCategoriaXls(Integer indiceColunaCategoriaXls);

    public void setLinhasCabecalho(Integer linhasCabecalho);
    
    public Map<String, CategoriaDaProva> getMapCategoriaAssociada();
    
    public Integer getTotalCategoriaXls();

}
