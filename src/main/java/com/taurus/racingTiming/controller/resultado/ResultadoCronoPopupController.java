package com.taurus.racingTiming.controller.resultado;

import com.taurus.exception.NegocioException;
import com.taurus.javafx.controller.FXMLBaseController;
import com.taurus.racingTiming.entidade.corrida.AtletaInscricao;
import com.taurus.racingTiming.entidade.corrida.Cronometragem;
import com.taurus.racingTiming.negocio.IRolex;
import com.taurus.racingTiming.util.ListaConstantes;
import com.taurus.util.FormatarUtil;
import com.taurus.util.GeralUtil;
import com.taurus.util.ListaConstantesBase;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ResultadoCronoPopupController extends FXMLBaseController implements IResultadoCronoPopupController {

    private static final Log LOG = LogFactory.getLog(ResultadoCronoPopupController.class);

    public ResultadoCronoPopupController() {
        super();
    }

    @Autowired
    private IRolex rolex;

    private AtletaInscricao atletaInscricao;

    @FXML
    private AnchorPane pane;
    @FXML
    private TableView<Cronometragem> tabCrono;
    @FXML
    private TableColumn<Cronometragem, Integer> colNumero;
    @FXML
    private TableColumn<Cronometragem, String> colAtleta;
    @FXML
    private TableColumn<Cronometragem, Integer> colVolta;
    @FXML
    private TableColumn<Cronometragem, String> colTempo;
    @FXML
    private TableColumn<Cronometragem, String> colHora;

    @Value("${fxml.resultadoCronoPopup.view}")
    @Override
    public void setFxmlFilePath(String filePath) {
        this.fxmlFilePath = filePath;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.configurarTabelaColunas();
    }

    @Override
    public void reinicializarJanelaEspecifico() {
        this.tabCrono.getItems().clear();
    }

    @Override
    public void aoAbrirJanela() {
        try {
            List<Cronometragem> cronometragens = this.rolex.pesquisarCronometragemPorAtletaStatus(this.atletaInscricao, ListaConstantes.StatusCronometragem.ATIVA);
            this.tabCrono.setItems(FXCollections.observableArrayList(cronometragens));
            pane.setPrefHeight(25 + (25 * cronometragens.size()));
        } catch (NegocioException ex) {
            this.cj.exibirErro(cm.getMensagem("erro_control_pesquisar", "cronometragem"), GeralUtil.getMensagemOriginalErro(ex));
        }
    }

    @Override
    public void aoFecharJanelaEspecifico() {
    }

    private void configurarTabelaColunas() {
        this.colNumero.setCellValueFactory(linha -> new ReadOnlyObjectWrapper(linha.getValue().getNumeroAtleta()));
        this.colAtleta.setCellValueFactory(linha -> new ReadOnlyObjectWrapper(linha.getValue().getAtletaInscricao().getAtleta().getNome()));
        this.colVolta.setCellValueFactory(linha -> new ReadOnlyObjectWrapper(linha.getValue().getVolta()));
        this.colTempo.setCellValueFactory(linha -> new ReadOnlyObjectWrapper(linha.getValue().getTempoVolta() == null ? null : FormatarUtil.formatarTempoMilisegundos(linha.getValue().getTempoVolta())));
        this.colHora.setCellValueFactory(linha -> new ReadOnlyObjectWrapper(linha.getValue().getHoraRegistro() == null ? null : FormatarUtil.localDateTimeToString(linha.getValue().getHoraRegistro(), FormatarUtil.FORMATO_DATA_HORA_MILI)));

        this.colNumero.getStyleClass().add(ListaConstantesBase.EstiloCss.TAB_COL_CENTRO.getValor());
        this.colAtleta.getStyleClass().add(ListaConstantesBase.EstiloCss.TAB_COL_ESQUERDO.getValor());
        this.colVolta.getStyleClass().add(ListaConstantesBase.EstiloCss.TAB_COL_CENTRO.getValor());
        this.colTempo.getStyleClass().add(ListaConstantesBase.EstiloCss.TAB_COL_CENTRO.getValor());
        this.colHora.getStyleClass().add(ListaConstantesBase.EstiloCss.TAB_COL_CENTRO.getValor());
    }

    @Override
    public void setAtletaInscricao(AtletaInscricao atletaInscricao) {
        this.atletaInscricao = atletaInscricao;
    }
}
