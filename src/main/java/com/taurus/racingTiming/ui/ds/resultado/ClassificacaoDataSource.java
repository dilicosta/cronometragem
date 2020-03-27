package com.taurus.racingTiming.ui.ds.resultado;

import com.taurus.javafx.ds.GenericDataSourceTableView;
import com.taurus.javafx.ds.GenericLinhaView;
import com.taurus.racingTiming.pojo.relatorio.Classificacao;
import java.util.List;

/**
 *
 * @author Diego Lima
 */
public class ClassificacaoDataSource extends GenericDataSourceTableView<GenericLinhaView<Classificacao>, Classificacao> {

    public ClassificacaoDataSource(List<Classificacao> listaBean) {
        super(listaBean);
    }

    public ClassificacaoDataSource(Classificacao cronometragem) {
        super(cronometragem);
    }

    @Override
    protected GenericLinhaView<Classificacao> criarLinhaView(Classificacao clas) {
        GenericLinhaView<Classificacao> linhaView = new GenericLinhaView(clas);
        linhaView.setColuna1(clas.getPosicao().toString());
        linhaView.setColuna2("" + clas.getNumeroAtleta());
        linhaView.setColuna3(clas.getNomeAtleta());
        linhaView.setColuna4(clas.getSexo().getDescricao());
        linhaView.setColuna5(clas.getEquipe());
        linhaView.setColuna6(clas.getCidadeAtleta());
        linhaView.setColuna7(clas.getCategoriaAtleta());
        linhaView.setColuna8(clas.getNomePercurso());
        linhaView.setColuna9(clas.getKm().toString());
        linhaView.setColuna10(clas.getTempo());
        return linhaView;
    }

    @Override
    public Classificacao getBean(GenericLinhaView<Classificacao> linhaView) {
        return linhaView.getBean();
    }
}
