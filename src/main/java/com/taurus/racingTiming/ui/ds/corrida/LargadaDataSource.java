package com.taurus.racingTiming.ui.ds.corrida;

import com.taurus.javafx.ds.GenericDataSourceTableView;
import com.taurus.javafx.ds.GenericLinhaView;
import com.taurus.racingTiming.entidade.corrida.Largada;
import com.taurus.util.FormatarUtil;
import java.util.List;

/**
 *
 * @author Diego Lima
 */
public class LargadaDataSource extends GenericDataSourceTableView<GenericLinhaView<Largada>, Largada> {

    public LargadaDataSource(List<Largada> listaBean) {
        super(listaBean);
    }

    public LargadaDataSource(Largada largada) {
        super(largada);
    }

    @Override
    protected GenericLinhaView<Largada> criarLinhaView(Largada largada) {
        GenericLinhaView<Largada> linhaView = new GenericLinhaView(largada);
        linhaView.setColuna1(largada.getNome());
        linhaView.setColuna2(FormatarUtil.localDateTimeToString(largada.getHoraPrevista(), FormatarUtil.FORMATO_HORA_MIN));
        return linhaView;
    }

    @Override
    public Largada getBean(GenericLinhaView<Largada> linhaView) {
        return linhaView.getBean();
    }
}
