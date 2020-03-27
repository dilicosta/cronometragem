package com.taurus.racingTiming.controller.corrida.inscricao;

import com.taurus.racingTiming.controller.corrida.inscricao.it.IPopupAtletaInscricaoController;
import com.taurus.exception.NegocioException;
import com.taurus.javafx.controller.PopupBaseController;
import com.taurus.racingTiming.entidade.corrida.AtletaInscricao;
import com.taurus.racingTiming.entidade.corrida.CategoriaDaProva;
import com.taurus.racingTiming.negocio.ISecretario;
import com.taurus.util.GeralUtil;
import com.taurus.util.Pagina;
import com.taurus.util.annotation.Parametro;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Pagination;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class PopupAtletaInscricaoController extends PopupBaseController implements IPopupAtletaInscricaoController {

    private static final Log LOG = LogFactory.getLog(PopupAtletaInscricaoController.class);
    private static final int ITENS_POR_PAGINA = 10;
    private AtletaInscricao AtletaInscricaoSelecionado;
    private Pagina pagina = new Pagina(1, ITENS_POR_PAGINA, null);

    @Parametro
    private String nomeAtleta;
    @Parametro
    private CategoriaDaProva categoriaDaProva;

    public PopupAtletaInscricaoController() {
        super();
    }

    @Autowired
    private ISecretario secretario;

    @FXML
    Label lblTitulo;

    @FXML
    ListView<AtletaInscricao> listView;
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
        this.listView.setCellFactory((ListView<AtletaInscricao> param) -> {
            return new ListCell<AtletaInscricao>() {
                @Override
                public void updateItem(AtletaInscricao ai, boolean empty) {
                    super.updateItem(ai, empty);
                    if (!empty) {
                        this.setText(ai.getAtleta().getNome());
                        this.setOnMouseClicked(event -> {
                            if (event.getClickCount() == 2 && (!this.isEmpty())) {
                                PopupAtletaInscricaoController.this.AtletaInscricaoSelecionado = ai;
                                PopupAtletaInscricaoController.this.aoFecharJanela();
                                PopupAtletaInscricaoController.this.getStage().close();
                            }
                        });
                    } else {
                        this.setText(null);
                    }
                }
            };
        });
    }

    @Override
    public void reinicializarJanelaEspecifico() {
        this.listView.getItems().clear();
    }

    @Override
    public void aoAbrirJanela() {
        this.paginacao.getPageFactory().call(0);
        this.AtletaInscricaoSelecionado = null;
    }

    @Override
    public void aoFecharJanelaEspecifico() {
    }

    @Override
    public AtletaInscricao getAtletaInscricaoSelecionado() {
        return this.AtletaInscricaoSelecionado;
    }

    @Override
    public void setNomeAtleta(String nomeAtleta) {
        this.nomeAtleta = nomeAtleta;
    }

    @Override
    public void setCategoriaDaProva(CategoriaDaProva categoriaDaProva) {
        this.categoriaDaProva = categoriaDaProva;
    }

    private ListView paginar(int indexPagina) {
        this.pagina.setNumeroPagina(indexPagina + 1);
        try {
            List<AtletaInscricao> lista = this.secretario.pesquisarAtletaInscricao(this.nomeAtleta, this.categoriaDaProva, pagina);
            this.listView.getItems().clear();
            this.listView.getItems().addAll(lista);
            this.paginacao.setPageCount(pagina.getNumeroPaginas());
        } catch (NegocioException ex) {
            cj.exibirErro(cm.getMensagem("erro_control_pesquisar", "atleta"), GeralUtil.getMensagemOriginalErro(ex));
        }
        return this.listView;
    }
}
