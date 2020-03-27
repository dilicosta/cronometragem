package com.taurus.racingTiming.controller.administracao;

import com.github.sarxos.webcam.Webcam;
import com.taurus.exception.NegocioException;
import com.taurus.javafx.converter.GenericComboBoxConverter;
import com.taurus.javafx.util.ControleJanela;
import com.taurus.racingTiming.controller.IImagemViewController;
import com.taurus.racingTiming.entidade.Configuracao;
import com.taurus.racingTiming.negocio.IAdministrador;
import com.taurus.racingTiming.util.ListaConstantes.Stages;
import com.taurus.racingTiming.util.Util;
import com.taurus.util.GeralUtil;
import java.awt.Dimension;
import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.validator.GenericValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ConfiguracaoCameraController extends CameraBaseController {

    private static final Log LOG = LogFactory.getLog(ConfiguracaoCameraController.class);

    @Autowired
    private IAdministrador administrador;

    private Configuracao configuracao;
    private Webcam cameraSelecionada;

    @FXML
    private TextField txtNome;
    @FXML
    private TextField txtResolucao;
    @FXML
    private ChoiceBox<Webcam> choiceCamera;
    @FXML
    private ChoiceBox<Dimension> choiceResolucao;

    public ConfiguracaoCameraController() {
        super();
    }

    @Value("${fxml.configuracaoCamera.view}")
    @Override
    public void setFxmlFilePath(String filePath) {
        this.fxmlFilePath = filePath;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);

        this.configurarChoiceCamera();
        this.configurarChoiceResolucao();

    }

    @Override
    public void reinicializarJanelaEspecifico() {
    }

    @Override
    public void aoFecharJanelaEspecifico() {
        super.desligarCamera();
        this.configuracao.setNomeCamera(this.cameraSelecionada == null ? null : this.cameraSelecionada.getName());
        this.configuracao.setResolucaoCamera(Util.dimensionToString(this.choiceResolucao.getValue()));

        try {
            this.administrador.atualizarConfiguracao(this.configuracao);
        } catch (NegocioException ex) {
            this.cj.exibirErro(cm.getMensagem("erro_control_salvar", "Configuracao"), GeralUtil.getMensagemOriginalErro(ex));
        }
    }

    @Override
    public void aoAbrirJanela() {
        try {
            this.configuracao = this.administrador.pesquisarConfiguracao();
            this.txtNome.setText(this.configuracao.getNomeCamera());
            this.txtResolucao.setText(this.configuracao.getResolucaoCamera());
        } catch (NegocioException ex) {
            this.cj.exibirErro(cm.getMensagem("erro_control_pesquisar", "Configuracao"), GeralUtil.getMensagemOriginalErro(ex));
        }
        this.carregarCameras();
    }

    @FXML
    public void visualizarFoto(ActionEvent actionEvent) {
        // super.cj.exibirAviso("", "Calma Bete, calma.");
        Button btnViewFoto = (Button) actionEvent.getSource();
        //this.imagemViewController.setImagem(new Image(new ByteArrayInputStream(this.mapFoto.get(indexFoto))));
        super.cj.abrirPopup(Stages.CRONOMETRAGEM.ordinal(), IImagemViewController.class, btnViewFoto, ControleJanela.PosicaoReferenciaPopup.DIREITA_ACIMA);
    }

    private void carregarCameras() {
        this.choiceCamera.getItems().addAll(Webcam.getWebcams());
        if (!GenericValidator.isBlankOrNull(this.configuracao.getNomeCamera())) {
            Webcam webcam = Webcam.getWebcamByName(this.configuracao.getNomeCamera());
            if (webcam != null) {
                this.choiceCamera.setValue(webcam);
            }
        }
    }

    private void exibirResolucoes() {
        this.choiceResolucao.getItems().clear();
        this.choiceResolucao.getItems().addAll(Arrays.asList(this.cameraSelecionada.getViewSizes()));
        /*
        if (!GenericValidator.isBlankOrNull(this.configuracao.getResolucaoCamera())) {
            this.choiceResolucao.setValue(Util.stringToDimension(this.configuracao.getResolucaoCamera()));
        }
         */
    }

    private void configurarChoiceCamera() {
        this.choiceCamera.setConverter(new GenericComboBoxConverter<>("name"));
        this.choiceCamera.valueProperty().addListener((ObservableValue<? extends Webcam> observable, Webcam oldValue, Webcam newValue) -> {
            if (newValue != null) {
                this.cameraSelecionada = newValue;
                super.setWebcam(this.cameraSelecionada);
                this.exibirResolucoes();
            }
        });
        this.choiceCamera.disableProperty().bind(super.cameraLigadaProperty);
    }

    private void configurarChoiceResolucao() {
        this.choiceResolucao.setConverter(new StringConverter<Dimension>() {
            @Override
            public String toString(Dimension dimensao) {
                return Util.dimensionToString(dimensao);
            }

            @Override
            public Dimension fromString(String label) {
                return Util.stringToDimension(label);
            }
        });

        this.choiceResolucao.valueProperty().addListener((ObservableValue<? extends Dimension> observable, Dimension oldValue, Dimension newValue) -> {
            //if (newValue != null) {
                super.setResolucao(newValue);
            //}
        });

        this.choiceResolucao.disableProperty().bind(super.cameraLigadaProperty);

    }

    @Override
    protected String getPath() {
        return "/Taurus/Sistema/media/video/";
    }
}
