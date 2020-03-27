package com.taurus.racingTiming.ui.ds.responsavel;

import com.taurus.javafx.ds.GenericDataSourceTableView;
import com.taurus.javafx.ds.GenericLinhaView;
import com.taurus.racingTiming.entidade.responsavel.RepresentanteFederacao;
import java.util.List;

/**
 *
 * @author Diego Lima
 */
public class RepresentanteFederacaoDataSource extends GenericDataSourceTableView<GenericLinhaView<RepresentanteFederacao>, RepresentanteFederacao> {

    public RepresentanteFederacaoDataSource(List<RepresentanteFederacao> listaBean) {
        super(listaBean);
    }

    public RepresentanteFederacaoDataSource(RepresentanteFederacao rf) {
        super(rf);
    }

    @Override
    protected GenericLinhaView<RepresentanteFederacao> criarLinhaView(RepresentanteFederacao rf) {
        GenericLinhaView<RepresentanteFederacao> linhaView = new GenericLinhaView(rf);
        linhaView.setColuna1(rf.getNome());
        linhaView.setColuna2(rf.getFederacao().getNome());
        return linhaView;
    }

    @Override
    public RepresentanteFederacao getBean(GenericLinhaView<RepresentanteFederacao> linhaView) {
        return  linhaView.getBean();
    }
}
