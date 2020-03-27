package com.taurus.racingTiming.ui.ds.corrida;

import com.taurus.javafx.ds.GenericDataSourceTableView;
import com.taurus.javafx.ds.GenericLinhaView;
import com.taurus.racingTiming.entidade.corrida.AtletaInscricao;
import com.taurus.util.FormatarUtil;
import java.time.LocalDateTime;
import java.util.List;

/**
 *
 * @author Diego Lima
 */
public class AtletaInscricaoDataSource extends GenericDataSourceTableView<GenericLinhaView<AtletaInscricao>, AtletaInscricao> {

    public AtletaInscricaoDataSource(List<AtletaInscricao> listaBean) {
        super(listaBean);
    }

    public AtletaInscricaoDataSource(AtletaInscricao atleta) {
        super(atleta);
    }

    @Override
    protected GenericLinhaView criarLinhaView(AtletaInscricao atletaInscricao) {
        GenericLinhaView<AtletaInscricao> linhaView = new GenericLinhaView(atletaInscricao);
        linhaView.setColuna1("" + atletaInscricao.getNumeroAtleta());
        linhaView.setColuna2(atletaInscricao.getAtleta().getNome());
        linhaView.setColuna3(atletaInscricao.getCategoria().getCategoriaAtleta().getNome());
        Integer voltas = atletaInscricao.getCategoria().getPercurso().getNumeroVolta();
        linhaView.setColuna4(voltas == null ? "?" : voltas.toString());
        LocalDateTime horaInicio = atletaInscricao.getCategoria().getLargada().getHoraInicio();
        linhaView.setColuna5(horaInicio == null ? "?" : FormatarUtil.localDateTimeToString(horaInicio, FormatarUtil.FORMATO_DATA_HORA_SEG));
        return linhaView;
    }

    @Override
    public AtletaInscricao getBean(GenericLinhaView<AtletaInscricao> linhaView) {
        return linhaView.getBean();
    }
}
