package com.taurus.racingTiming.ui.ds.corrida;

import com.taurus.javafx.ds.GenericDataSourceTableView;
import com.taurus.javafx.ds.GenericLinhaView;
import com.taurus.racingTiming.entidade.corrida.Percurso;
import java.util.List;

/**
 *
 * @author Diego Lima
 */
public class PercursoDataSource extends GenericDataSourceTableView<GenericLinhaView<Percurso>, Percurso> {

    public PercursoDataSource(List<Percurso> listaBean) {
        super(listaBean);
    }

    public PercursoDataSource(Percurso percurso) {
        super(percurso);
    }

    @Override
    protected GenericLinhaView<Percurso> criarLinhaView(Percurso percurso) {
        GenericLinhaView<Percurso> linhaView = new GenericLinhaView(percurso);
        linhaView.setColuna1(percurso.getNome());
        linhaView.setColuna2(percurso.getDistancia().toString().replace(".", ","));
        linhaView.setColuna3(percurso.getNumeroVolta().toString());
        linhaView.setColuna4(percurso.getGrauDificuldade().getDescricao());
        return linhaView;
    }

    @Override
    public Percurso getBean(GenericLinhaView<Percurso> linhaView) {
        return linhaView.getBean();
    }
}
