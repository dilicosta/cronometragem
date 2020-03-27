package com.taurus.racingTiming.ui.ds.corrida;

import com.taurus.javafx.ds.GenericDataSourceTableView;
import com.taurus.javafx.ds.GenericLinhaView;
import com.taurus.racingTiming.entidade.corrida.CategoriaDaProva;
import java.util.List;

/**
 *
 * @author Diego Lima
 */
public class CategoriaProvaDataSource extends GenericDataSourceTableView<GenericLinhaView<CategoriaDaProva>, CategoriaDaProva> {

    public CategoriaProvaDataSource(List<CategoriaDaProva> listaBean) {
        super(listaBean);
    }

    public CategoriaProvaDataSource(CategoriaDaProva categoriaDaProva) {
        super(categoriaDaProva);
    }

    @Override
    protected GenericLinhaView<CategoriaDaProva> criarLinhaView(CategoriaDaProva categoriaDaProva) {
        GenericLinhaView<CategoriaDaProva> linhaView = new GenericLinhaView(categoriaDaProva);
        linhaView.setColuna1(categoriaDaProva.getCategoriaAtleta().getNome());
        linhaView.setColuna2(categoriaDaProva.getPercurso().getNome());
        linhaView.setColuna3(categoriaDaProva.getLargada().getNome());
        return linhaView;
    }

    @Override
    public CategoriaDaProva getBean(GenericLinhaView<CategoriaDaProva> linhaView) {
        return linhaView.getBean();
    }
}
