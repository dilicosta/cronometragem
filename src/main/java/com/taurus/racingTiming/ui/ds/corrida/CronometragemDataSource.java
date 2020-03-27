package com.taurus.racingTiming.ui.ds.corrida;

import com.taurus.javafx.ds.GenericDataSourceTableView;
import com.taurus.javafx.ds.GenericLinhaView;
import com.taurus.racingTiming.entidade.corrida.Cronometragem;
import com.taurus.util.FormatarUtil;
import java.util.List;

/**
 *
 * @author Diego Lima
 */
public class CronometragemDataSource extends GenericDataSourceTableView<GenericLinhaView<Cronometragem>, Cronometragem> {

    public CronometragemDataSource(List<Cronometragem> listaBean) {
        super(listaBean);
    }

    public CronometragemDataSource(Cronometragem cronometragem) {
        super(cronometragem);
    }

    @Override
    protected GenericLinhaView<Cronometragem> criarLinhaView(Cronometragem cronometragem) {
        GenericLinhaView<Cronometragem> linhaView = new GenericLinhaView(cronometragem);
        linhaView.setColuna1("" + cronometragem.getNumeroAtleta());
        if (cronometragem.getAtletaInscricao() != null) {
            linhaView.setColuna2(cronometragem.getAtletaInscricao().getAtleta().getNome());
            linhaView.setColuna3(cronometragem.getAtletaInscricao().getCategoria().getCategoriaAtleta().getNome());
        } else {
            linhaView.setColuna2("");
            linhaView.setColuna3("");
        }
        linhaView.setColuna4(FormatarUtil.localDateTimeToString(cronometragem.getHoraRegistro(), FormatarUtil.FORMATO_DATA_HORA_MILI));
        linhaView.setColuna5(cronometragem.getTempoVolta() == null ? null : FormatarUtil.formatarTempoMilisegundos(cronometragem.getTempoVolta()));
        linhaView.setColuna6(cronometragem.getVolta() == null ? null : cronometragem.getVolta().toString());
        return linhaView;
    }

    @Override
    public Cronometragem getBean(GenericLinhaView<Cronometragem> linhaView) {
        return linhaView.getBean();
    }
}
