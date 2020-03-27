package com.taurus.racingTiming.ui.ds.atleta;

import com.taurus.javafx.ds.GenericDataSourceTableView;
import com.taurus.javafx.ds.GenericLinhaView;
import com.taurus.racingTiming.entidade.atleta.Atleta;
import com.taurus.util.FormatarUtil;
import java.util.List;

/**
 *
 * @author Diego Lima
 */
public class AtletaDataSource extends GenericDataSourceTableView<GenericLinhaView<Atleta>, Atleta> {

    public AtletaDataSource(List<Atleta> listaBean) {
        super(listaBean);
    }

    public AtletaDataSource(Atleta atleta) {
        super(atleta);
    }

    @Override
    protected GenericLinhaView criarLinhaView(Atleta atleta) {
        GenericLinhaView<Atleta> linhaView = new GenericLinhaView(atleta);
        linhaView.setColuna1(atleta.getNome());
        linhaView.setColuna2(FormatarUtil.formatarCpf(atleta.getCpf()));
        linhaView.setColuna3(FormatarUtil.localDateToString(atleta.getDataNascimento()));
        linhaView.setColuna4(atleta.getSexo().getDescricao());
        return linhaView;
    }

    @Override
    public Atleta getBean(GenericLinhaView<Atleta> linhaView) {
        return  linhaView.getBean();
    }
}
