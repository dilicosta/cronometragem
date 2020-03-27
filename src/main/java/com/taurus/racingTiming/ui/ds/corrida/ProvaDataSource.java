package com.taurus.racingTiming.ui.ds.corrida;

import com.taurus.javafx.ds.GenericDataSourceTableView;
import com.taurus.javafx.ds.GenericLinhaView;
import com.taurus.racingTiming.entidade.corrida.Prova;
import com.taurus.racingTiming.entidade.responsavel.RepresentanteOrganizacaoProva;
import com.taurus.util.FormatarUtil;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author Diego Lima
 */
public class ProvaDataSource extends GenericDataSourceTableView<GenericLinhaView<Prova>, Prova> {

    public ProvaDataSource(List<Prova> listaBean) {
        super(listaBean);
    }

    public ProvaDataSource(Prova prova) {
        super(prova);
    }

    @Override
    protected GenericLinhaView criarLinhaView(Prova prova) {
        GenericLinhaView<Prova> linhaView = new GenericLinhaView(prova);
        linhaView.setColuna1(prova.getNome());
        linhaView.setColuna2(FormatarUtil.localDateToString(prova.getData()));
        linhaView.setColuna3(prova.getEndereco().getCidade());

        StringBuilder organizador = new StringBuilder();
        Iterator<RepresentanteOrganizacaoProva> it = prova.getListaRepresentanteOrganizacao().iterator();
        RepresentanteOrganizacaoProva representante = it.next();
        organizador.append(representante.getOrganizacaoProva().getNome());
        organizador.append(" / ").append(representante.getNome());

        if (prova.getListaRepresentanteOrganizacao().size() > 1) {
            organizador.append(" (+)");
        }
        linhaView.setColuna4(organizador.toString());
        
        linhaView.setColuna5(prova.getStatus().getDescricao()); 

        return linhaView;
    }

    @Override
    public Prova getBean(GenericLinhaView<Prova> linhaView) {
        return linhaView.getBean();
    }
}
