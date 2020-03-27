package com.taurus.racingTiming.controller.atleta;

import com.taurus.racingTiming.entidade.atleta.CategoriaAtleta;
import com.taurus.util.ListaConstantesBase.Operacao;

/**
 *
 * @author Diego
 */
public interface ICategoriaAtletaController {

    void setCategoriaAtletaEdicao(CategoriaAtleta categoriaAtletaEdicao);

    void setOperacao(Operacao op);
    
}
