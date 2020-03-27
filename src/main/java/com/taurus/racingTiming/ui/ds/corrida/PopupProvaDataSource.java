package com.taurus.racingTiming.ui.ds.corrida;

import com.taurus.javafx.ds.GenericDataSourceTableView;
import com.taurus.javafx.ds.GenericLinhaView;
import com.taurus.racingTiming.entidade.corrida.Prova;
import java.util.List;

/**
 *
 * @author Diego Lima
 */
public class PopupProvaDataSource extends GenericDataSourceTableView<GenericLinhaView<Prova>, Prova> {

    public PopupProvaDataSource(List<Prova> listaBean) {
        super(listaBean);
    }

    public PopupProvaDataSource(Prova prova) {
        super(prova);
    }

    @Override
    protected GenericLinhaView criarLinhaView(Prova prova) {
        GenericLinhaView<Prova> linhaView = new GenericLinhaView(prova);
        linhaView.setColuna1(prova.getNome());
        return linhaView;
    }

    @Override
    public Prova getBean(GenericLinhaView<Prova> linhaView) {
        return linhaView.getBean();
    }
}
