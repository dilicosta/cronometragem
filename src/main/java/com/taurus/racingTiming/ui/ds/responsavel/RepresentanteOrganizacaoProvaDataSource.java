package com.taurus.racingTiming.ui.ds.responsavel;

import com.taurus.javafx.ds.GenericDataSourceTableView;
import com.taurus.javafx.ds.GenericLinhaView;
import com.taurus.racingTiming.entidade.responsavel.RepresentanteOrganizacaoProva;
import java.util.List;

/**
 *
 * @author Diego Lima
 */
public class RepresentanteOrganizacaoProvaDataSource extends GenericDataSourceTableView<GenericLinhaView<RepresentanteOrganizacaoProva>, RepresentanteOrganizacaoProva> {

    public RepresentanteOrganizacaoProvaDataSource(List<RepresentanteOrganizacaoProva> listaBean) {
        super(listaBean);
    }

    public RepresentanteOrganizacaoProvaDataSource(RepresentanteOrganizacaoProva rop) {
        super(rop);
    }

    @Override
    protected GenericLinhaView<RepresentanteOrganizacaoProva> criarLinhaView(RepresentanteOrganizacaoProva rop) {
        GenericLinhaView<RepresentanteOrganizacaoProva> linhaView = new GenericLinhaView(rop);
        linhaView.setColuna1(rop.getNome());
        linhaView.setColuna2(rop.getOrganizacaoProva().getNome());
        return linhaView;
    }

    @Override
    public RepresentanteOrganizacaoProva getBean(GenericLinhaView<RepresentanteOrganizacaoProva> linhaView) {
        return linhaView.getBean();
    }
}
