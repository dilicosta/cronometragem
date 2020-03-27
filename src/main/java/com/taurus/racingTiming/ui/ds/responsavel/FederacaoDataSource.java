package com.taurus.racingTiming.ui.ds.responsavel;

import com.taurus.javafx.ds.GenericDataSourceTableView;
import com.taurus.javafx.ds.GenericLinhaView;
import com.taurus.racingTiming.entidade.responsavel.Federacao;
import java.util.List;

/**
 *
 * @author Diego Lima
 */
public class FederacaoDataSource extends GenericDataSourceTableView<GenericLinhaView<Federacao>, Federacao> {

    public FederacaoDataSource(List<Federacao> listaBean) {
        super(listaBean);
    }

    public FederacaoDataSource(Federacao federacao) {
        super(federacao);
    }

    @Override
    protected GenericLinhaView criarLinhaView(Federacao federacao) {
        GenericLinhaView<Federacao> linhaView = new GenericLinhaView(federacao);
        linhaView.setColuna1(federacao.getNome());
        linhaView.setColuna2(federacao.getSigla());
        return linhaView;
    }

    @Override
    public Federacao getBean(GenericLinhaView<Federacao> linhaView) {
        return  linhaView.getBean();
    }
}
