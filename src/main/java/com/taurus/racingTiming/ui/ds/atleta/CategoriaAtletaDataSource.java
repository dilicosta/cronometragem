package com.taurus.racingTiming.ui.ds.atleta;

import com.taurus.javafx.ds.GenericDataSourceTableView;
import com.taurus.javafx.ds.GenericLinhaView;
import com.taurus.racingTiming.entidade.atleta.CategoriaAtleta;
import java.util.List;

/**
 *
 * @author Diego Lima
 */
public class CategoriaAtletaDataSource extends GenericDataSourceTableView<GenericLinhaView<CategoriaAtleta>, CategoriaAtleta> {

    public CategoriaAtletaDataSource(List<CategoriaAtleta> listaBean) {
        super(listaBean);
    }

    public CategoriaAtletaDataSource(CategoriaAtleta categoriaAtleta) {
        super(categoriaAtleta);
    }

    @Override
    protected GenericLinhaView criarLinhaView(CategoriaAtleta categoriaAtleta) {
        GenericLinhaView<CategoriaAtleta> linhaView = new GenericLinhaView(categoriaAtleta);
        linhaView.setColuna1(categoriaAtleta.getNome());
        linhaView.setColuna2(categoriaAtleta.getDescricao());
        linhaView.setColuna3(categoriaAtleta.getSexo().getDescricao());
        linhaView.setColuna4(categoriaAtleta.getIdadeMinima() != null ? categoriaAtleta.getIdadeMinima().toString() : "");
        linhaView.setColuna5(categoriaAtleta.getIdadeMaxima() != null ? categoriaAtleta.getIdadeMaxima().toString() : "");
        return linhaView;
    }

    @Override
    public CategoriaAtleta getBean(GenericLinhaView<CategoriaAtleta> linhaView) {
        return linhaView.getBean();
    }
}
