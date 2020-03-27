package com.taurus.racingTiming.ui.ds.responsavel;

import com.taurus.javafx.ds.GenericDataSourceTableView;
import com.taurus.javafx.ds.GenericLinhaView;
import com.taurus.racingTiming.entidade.responsavel.OrganizacaoProva;
import java.util.List;

/**
 *
 * @author Diego Lima
 */
public class OrganizacaoProvaDataSource extends GenericDataSourceTableView<GenericLinhaView<OrganizacaoProva>, OrganizacaoProva> {

    public OrganizacaoProvaDataSource(List<OrganizacaoProva> listaBean) {
        super(listaBean);
    }

    public OrganizacaoProvaDataSource(OrganizacaoProva federacao) {
        super(federacao);
    }

    @Override
    protected GenericLinhaView criarLinhaView(OrganizacaoProva federacao) {
        GenericLinhaView<OrganizacaoProva> linhaView = new GenericLinhaView(federacao);
        linhaView.setColuna1(federacao.getNome());
        return linhaView;
    }

    @Override
    public OrganizacaoProva getBean(GenericLinhaView<OrganizacaoProva> linhaView) {
        return linhaView.getBean();
    }
}
