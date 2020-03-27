package com.taurus.racingTiming.ui.ds.corrida;

import com.taurus.javafx.ds.GenericDataSourceTableView;
import com.taurus.javafx.ds.GenericLinhaView;
import com.taurus.racingTiming.pojo.CategoriaProvaResumo;
import java.util.List;

/**
 *
 * @author Diego Lima
 */
public class CategoriaProvaInscritosDataSource extends GenericDataSourceTableView<GenericLinhaView<CategoriaProvaResumo>, CategoriaProvaResumo> {

    public CategoriaProvaInscritosDataSource(List<CategoriaProvaResumo> listaBean) {
        super(listaBean);
    }

    public CategoriaProvaInscritosDataSource(CategoriaProvaResumo categoriaProvaInscritos) {
        super(categoriaProvaInscritos);
    }

    @Override
    protected GenericLinhaView criarLinhaView(CategoriaProvaResumo categoriaProvaInscritos) {
        GenericLinhaView<CategoriaProvaResumo> linhaView = new GenericLinhaView(categoriaProvaInscritos);
        linhaView.setColuna1(categoriaProvaInscritos.getCategoriaDaProva().getCategoriaAtleta().getNome());
        linhaView.setColuna2(categoriaProvaInscritos.getCategoriaDaProva().getCategoriaAtleta().getDescricao());
        linhaView.setColuna3(categoriaProvaInscritos.getInscritos().toString());
        return linhaView;
    }

    @Override
    public CategoriaProvaResumo getBean(GenericLinhaView<CategoriaProvaResumo> linhaView) {
        return linhaView.getBean();
    }
}
