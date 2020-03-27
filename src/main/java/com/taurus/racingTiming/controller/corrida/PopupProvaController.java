package com.taurus.racingTiming.controller.corrida;

import com.taurus.exception.NegocioException;
import com.taurus.javafx.componente.GenericListCell;
import com.taurus.javafx.controller.PopupBaseController;
import com.taurus.javafx.ds.GenericLinhaView;
import com.taurus.racingTiming.entidade.corrida.Prova;
import com.taurus.racingTiming.negocio.ISecretario;
import com.taurus.racingTiming.ui.ds.corrida.PopupProvaDataSource;
import com.taurus.racingTiming.util.ListaConstantes;
import com.taurus.racingTiming.util.ListaConstantes.StatusProva;
import com.taurus.util.GeralUtil;
import com.taurus.util.Pagina;
import com.taurus.util.annotation.Parametro;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Pagination;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class PopupProvaController extends PopupBaseController implements IPopupProvaController {

    private static final Log LOG = LogFactory.getLog(PopupProvaController.class);
    private static final int ITENS_POR_PAGINA = 10;
    private Prova provaSelecionado;
    private PopupProvaDataSource dataSource;
    private Pagina pagina = null;

    @Parametro
    private String nomeProva;

    public PopupProvaController() {
        super();
    }

    @Autowired
    private ISecretario secretario;

    @FXML
    Label lblTitulo;

    @FXML
    ListView<GenericLinhaView<Prova>> listView;
    @FXML
    private Pagination paginacao;

    @Value("${fxml.genericListaPopup.view}")
    @Override
    public void setFxmlFilePath(String filePath) {
        this.fxmlFilePath = filePath;
    }

    @Override
    protected void inicializar(URL location, ResourceBundle resources) {
        this.paginacao.setPageFactory(this::paginar);
        this.lblTitulo.setText(cm.getMensagem("prova"));
        this.listView.setCellFactory((ListView<GenericLinhaView<Prova>> param) -> {
            return new GenericListCell<Prova>() {
                @Override
                protected void eventoDuploClique(GenericLinhaView<Prova> item) {
                    PopupProvaController.this.provaSelecionado = dataSource.getBean(item);
                    PopupProvaController.this.aoFecharJanela();
                    PopupProvaController.this.getStage().close();
                }
            };
        });
    }

    @Override
    public void reinicializarJanelaEspecifico() {
        this.dataSource = null;
    }

    @Override
    public void aoAbrirJanela() {
        this.pagina = new Pagina(1, ITENS_POR_PAGINA, null);
        this.paginacao.getPageFactory().call(0);
        this.provaSelecionado = null;
    }

    @Override
    public void aoFecharJanelaEspecifico() {
    }

    @Override
    public Prova getProvaSelecionado() {
        return this.provaSelecionado;
    }

    @Override
    public void setNomeProva(String nomeProva) {
        this.nomeProva = nomeProva;
    }

    private ListView paginar(int indexPagina) {
        this.pagina.setNumeroPagina(indexPagina + 1);
        try {
            // Vamos permitir temporariamente inscricao independente do status da prova
            //List<Prova> lista = this.secretario.pesquisarProva(nomeProva, null, null, null, ListaConstantes.StatusProva.INSCRICAO_ABERTA, pagina);
            List<Prova> lista = this.secretario.pesquisarProva(nomeProva, null, null, null, (StatusProva) null, pagina);
            this.dataSource = new PopupProvaDataSource(lista);
            this.listView.setItems(dataSource.getData());
            this.paginacao.setPageCount(pagina.getNumeroPaginas());
        } catch (NegocioException ex) {
            cj.exibirErro(cm.getMensagem("erro_control_pesquisar", "prova"), GeralUtil.getMensagemOriginalErro(ex));
        }
        return this.listView;
    }
}
