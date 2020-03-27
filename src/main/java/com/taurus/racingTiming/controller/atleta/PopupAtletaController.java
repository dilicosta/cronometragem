package com.taurus.racingTiming.controller.atleta;

import com.taurus.exception.NegocioException;
import com.taurus.javafx.componente.GenericListCell;
import com.taurus.javafx.controller.PopupBaseController;
import com.taurus.javafx.ds.GenericLinhaView;
import com.taurus.racingTiming.entidade.atleta.Atleta;
import com.taurus.racingTiming.negocio.ISecretario;
import com.taurus.racingTiming.ui.ds.atleta.AtletaDataSource;
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
public class PopupAtletaController extends PopupBaseController implements IPopupAtletaController {

    private static final Log LOG = LogFactory.getLog(PopupAtletaController.class);
    private static final int ITENS_POR_PAGINA = 10;
    private Atleta AtletaSelecionado;
    private AtletaDataSource dataSource;
    private Pagina pagina = new Pagina(1, ITENS_POR_PAGINA, null);

    @Parametro
    private String nomeAtleta;

    public PopupAtletaController() {
        super();
    }

    @Autowired
    private ISecretario secretario;

    @FXML
    Label lblTitulo;

    @FXML
    ListView<GenericLinhaView<Atleta>> listView;
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
        this.lblTitulo.setText(cm.getMensagem("atleta"));
        this.listView.setCellFactory((ListView<GenericLinhaView<Atleta>> param) -> {
            return new GenericListCell<Atleta>() {
                @Override
                protected void eventoDuploClique(GenericLinhaView<Atleta> item) {
                    PopupAtletaController.this.AtletaSelecionado = dataSource.getBean(item);
                    PopupAtletaController.this.aoFecharJanela();
                    PopupAtletaController.this.getStage().close();
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
        this.paginacao.getPageFactory().call(0);
        this.AtletaSelecionado = null;
    }

    @Override
    public void aoFecharJanelaEspecifico() {
    }

    @Override
    public Atleta getAtletaSelecionado() {
        return this.AtletaSelecionado;
    }

    @Override
    public void setNomeAtleta(String nomeAtleta) {
        this.nomeAtleta = nomeAtleta;
    }

    private ListView paginar(int indexPagina) {
        this.pagina.setNumeroPagina(indexPagina + 1);
        try {
            List<Atleta> lista = this.secretario.pesquisarAtleta(nomeAtleta, null, pagina);
            this.dataSource = new AtletaDataSource(lista);
            this.listView.setItems(dataSource.getData());
            this.paginacao.setPageCount(pagina.getNumeroPaginas());
        } catch (NegocioException ex) {
            cj.exibirErro(cm.getMensagem("erro_control_pesquisar", "atleta"), GeralUtil.getMensagemOriginalErro(ex));
        }
        return this.listView;
    }
}
