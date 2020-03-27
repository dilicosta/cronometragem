package com.taurus.racingTiming.ui.ds.corrida;

import com.taurus.javafx.ds.GenericDataSourceTableView;
import com.taurus.javafx.ds.GenericLinhaView;
import com.taurus.racingTiming.entidade.corrida.Prova;
import com.taurus.racingTiming.entidade.responsavel.RepresentanteOrganizacaoProva;
import com.taurus.racingTiming.pojo.SituacaoProva;
import com.taurus.util.FormatarUtil;
import java.util.List;

/**
 *
 * @author Diego Lima
 */
public class SituacaoProvaDataSource extends GenericDataSourceTableView<GenericLinhaView<SituacaoProva>, SituacaoProva> {

    public SituacaoProvaDataSource(List<SituacaoProva> listaBean) {
        super(listaBean);
    }

    public SituacaoProvaDataSource(SituacaoProva prova) {
        super(prova);
    }

    @Override
    protected GenericLinhaView criarLinhaView(SituacaoProva situacaoProva) {
        GenericLinhaView<Prova> linhaView = new GenericLinhaView(situacaoProva);
        linhaView.setColuna1(situacaoProva.getProva().getNome());
        linhaView.setColuna2(FormatarUtil.localDateToString(situacaoProva.getProva().getData()));
        String nomeOrganizador = ((RepresentanteOrganizacaoProva) situacaoProva.getProva().getListaRepresentanteOrganizacao().toArray()[0]).getOrganizacaoProva().getNome();
        if (situacaoProva.getProva().getListaRepresentanteOrganizacao().size() > 1) {
            nomeOrganizador += "(+)";
        }
        linhaView.setColuna3(nomeOrganizador);
        linhaView.setColuna4(situacaoProva.getProva().getStatus().getDescricao());
        linhaView.setColuna5(situacaoProva.getNumeroInscritos().toString());
        return linhaView;
    }

    @Override
    public SituacaoProva getBean(GenericLinhaView<SituacaoProva> linhaView) {
        return linhaView.getBean();
    }
}
