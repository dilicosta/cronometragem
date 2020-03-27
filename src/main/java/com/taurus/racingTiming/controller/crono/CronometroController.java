package com.taurus.racingTiming.controller.crono;

import com.github.sarxos.webcam.Webcam;
import com.taurus.exception.AvisoNegocioException;
import com.taurus.exception.NegocioException;
import com.taurus.javafx.componente.TableViewDuploClique;
import com.taurus.javafx.converter.GenericComboBoxConverter;
import com.taurus.javafx.util.ControleJanela;
import com.taurus.javafx.util.JavafxUtil;
import com.taurus.javafx.util.ValidarCampoUtil;
import com.taurus.racingTiming.controller.IImagemViewController;
import com.taurus.racingTiming.controller.administracao.CameraBaseController;
import com.taurus.racingTiming.entidade.Configuracao;
import com.taurus.racingTiming.entidade.corrida.AtletaInscricao;
import com.taurus.racingTiming.entidade.corrida.Cronometragem;
import com.taurus.racingTiming.entidade.corrida.CronometragemImagem;
import com.taurus.racingTiming.entidade.corrida.Largada;
import com.taurus.racingTiming.entidade.corrida.Prova;
import com.taurus.racingTiming.negocio.IAdministrador;
import com.taurus.racingTiming.negocio.IJuiz;
import com.taurus.racingTiming.negocio.IRolex;
import com.taurus.racingTiming.negocio.ISecretario;
import com.taurus.racingTiming.pojo.CategoriaProvaResumo;
import com.taurus.racingTiming.util.ListaConstantes;
import com.taurus.racingTiming.util.ListaConstantes.Stages;
import com.taurus.racingTiming.util.ListaConstantes.StatusProva;
import com.taurus.racingTiming.util.Util;
import com.taurus.util.FormatarUtil;
import com.taurus.util.GeralUtil;
import com.taurus.util.ListaConstantesBase;
import com.taurus.util.ListaConstantesBase.EstiloCss;
import com.taurus.util.MascaraClasse;
import com.taurus.util.Pagina;
import com.taurus.util.annotation.Mascara;
import com.taurus.util.annotation.Parametro;
import java.io.ByteArrayInputStream;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.WindowEvent;
import javafx.util.Duration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.validator.GenericValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class CronometroController extends CameraBaseController implements ICronometroController {

    private static final Log LOG = LogFactory.getLog(CronometroController.class);
    private static int POS_X_NUMERO = 10;
    private static int POS_X_TEMPO = 85;
    private static int POS_X_BTN_VIEW_FOTO = 171;
    private static int POS_X_DUVIDA = 205;
    private static int POS_X_BTN_CONF = 235;
    private static int POS_X_BTN_EXCL = 260;

    private static final int DISTANCIA_Y = 32;
    private static final int LINHA_Y_INICIAL = 50;

    private static final int NUMERO_LARGURA = 70;
    private static final int NUMERO_ALTURA = 25;
    private static final int TEMPO_LARGURA = 81;
    private static final int TEMPO_ALTURA = 25;

    private static final String TXT_NUMERO = "txtNumero_";
    private static final String TXT_TEMPO = "txtTempo_";
    private static final String CHECK_DUVIDA = "checkDuvida_";
    private static final String BTN_VIEW_FOTO = "btnViewFoto_";
    private static final String BTN_CONF = "btnConfirmar_";
    private static final String BTN_EXCL = "btnExcluir_";

    private static final int ITENS_POR_PAGINA = 20;

    private Map<Integer, byte[]> mapFoto = new HashMap<>();

    @Autowired
    private ICronometragemEdicaoController cronometragemEdicaoController;
    @Autowired
    private IImagemViewController cronometragemImagemController;
    @Autowired
    private IClassificacaoTemporariaController classificacaoTemporariaController;
    @Autowired
    private IRolex rolex;
    @Autowired
    private ISecretario secretario;
    @Autowired
    private IJuiz juiz;
    @Autowired
    private IAdministrador administrador;

    private int contador_total = 0;
    private int contador_visual = 0;

    @Parametro
    private Prova prova;

    //private CronometragemDataSource dataSource;
    public CronometroController() {
        super();
    }

    Timeline timeline;

    @FXML
    ScrollPane scrollPane;
    @FXML
    private AnchorPane paneMarcacao;
    @FXML
    private TextField txtNomeProva;
    @FXML
    private ComboBox<Largada> comboLargada;
    @FXML
    private Button btnIniciarLargada;
    @FXML
    private TextField txtHoraPrevista;
    @FXML
    @Mascara(tipo = Mascara.TipoMascara.TEXT_FIELD_CUSTOM, custom = "99:99:99")
    private TextField txtHoraInicio;
    @FXML
    private Label lblHora;
    @FXML
    private Button btnAdicionarMarcacao;
    @FXML
    private Button btnConcluir;

    @FXML
    private TableView<Cronometragem> tabCrono;
    @FXML
    private TableColumn<Cronometragem, Integer> colNumero;
    @FXML
    private TableColumn<Cronometragem, String> colAtleta;
    @FXML
    private TableColumn<Cronometragem, String> colCategoria;
    //@FXML
    //private TableColumn<GenericLinhaView, String> colVolta;
    @FXML
    private TableColumn<Cronometragem, String> colHora;
    @FXML
    private TableColumn<Cronometragem, Integer> colPosicao;
    @FXML
    private TableColumn<Cronometragem, String> colTempo;

    @FXML
    private TableView<CategoriaProvaResumo> tabCategoriaResumo;
    @FXML
    private TableColumn<CategoriaProvaResumo, String> colCatResumo;
    @FXML
    private TableColumn<CategoriaProvaResumo, Long> colTotalCat;
    @FXML
    private TableColumn<CategoriaProvaResumo, Long> colConcluidoCat;
    @FXML
    private TableColumn<CategoriaProvaResumo, Long> colFaltamCat;

    @FXML
    Button btnClassificacaoTemporaria;

    @Value("${fxml.cronometragem.view}")
    @Override
    public void setFxmlFilePath(String filePath) {
        this.fxmlFilePath = filePath;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);

        this.timeline = new Timeline(new KeyFrame(Duration.millis(100), (ActionEvent arg0) -> {
            CronometroController.this.atualizarCronometro();
        }));
        this.timeline.setCycleCount(Timeline.INDEFINITE);

        this.configurarComboLargada();

        this.configurarTabelaColunas();
        this.configurarTabelaColunasResumoCategoria();

        this.btnClassificacaoTemporaria.getStyleClass().add(ListaConstantes.EstiloCss.BOTAO_RELATORIO.getValor());
        this.btnClassificacaoTemporaria.setTooltip(new Tooltip("exibir classificação temporária"));
    }

    @Override
    public void reinicializarJanelaEspecifico() {
        this.contador_total = 0;
        this.tabCrono.getItems().clear();
        this.comboLargada.getItems().clear();
        this.mapFoto.clear();
    }

    @Override
    public void aoFecharJanelaEspecifico() {
        this.timeline.stop();
    }

    @Override
    public void aoAbrirJanela() {
        try {
            this.prova = this.secretario.pesquisarProvaPorId(this.prova.getId());
        } catch (NegocioException ex) {
            super.cj.exibirErro(cm.getMensagem("erro_control_pesquisar", "prova"), GeralUtil.getMensagemOriginalErro(ex));
        }
        switch (this.prova.getStatus()) {
            case INSCRICAO_FECHADA:
            case CRONOMETRANDO_PARCIAL_LARGADA:
            case CRONOMETRANDO_TODAS_LARGADAS:
                this.carregarConfiguracaoCamera();

                try {
                    this.prova.setListaLargada(new LinkedHashSet<>(this.secretario.pesquisarLargada(this.prova)));
                    this.txtNomeProva.setText(this.prova.getNome());
                    this.comboLargada.getItems().addAll(this.prova.getListaLargada());
                    this.comboLargada.setValue(this.comboLargada.getItems().get(0));
                } catch (NegocioException ex) {
                    super.cj.exibirErro(cm.getMensagem("erro_control_pesquisar", "Largada"), GeralUtil.getMensagemOriginalErro(ex));
                }

                if (this.prova.getStatus() == StatusProva.CRONOMETRANDO_PARCIAL_LARGADA || this.prova.getStatus() == StatusProva.CRONOMETRANDO_TODAS_LARGADAS) {
                    this.btnAdicionarMarcacao.setVisible(true);
                    this.btnConcluir.setDisable(false);
                    this.btnIniciarLargada.setDisable(false);

                    try {
                        this.juiz.carregarClassificacaoTemporiaria(prova);
                    } catch (NegocioException ex) {
                        super.cj.exibirErro(cm.getMensagem("erro_control_pesquisar", "Atleta Inscrição"), GeralUtil.getMensagemOriginalErro(ex));
                        return;
                    }

                    Pagina pagina = new Pagina(1, ITENS_POR_PAGINA, null);
                    try {
                        List<Cronometragem> ultimasCronometragens = this.rolex.pesquisarUltimasCronometragensProva(this.prova, pagina);
                        this.tabCrono.getItems().addAll(ultimasCronometragens);
                    } catch (NegocioException ex) {
                        super.cj.exibirErro(cm.getMensagem("erro_control_pesquisar", "Conometragem"), GeralUtil.getMensagemOriginalErro(ex));
                        return;
                    }

                    this.atualizarTabelaCategoriaResumo();
                } else {
                    this.btnAdicionarMarcacao.setVisible(false);
                    this.btnConcluir.setDisable(true);

                    super.cj.exibirInformacao("info_iniciar_largada");
                }
                this.configurarValidacaoFecharJanela();
                break;
            default:
                super.cj.exibirAviso("Esta prova não está disponível para cronometragem: " + this.prova.getStatus().getDescricao());
                this.btnAdicionarMarcacao.setVisible(false);
                this.btnIniciarLargada.setDisable(true);
                this.btnConcluir.setDisable(true);
        }
    }

    @Override
    public void setProva(Prova prova) {
        this.prova = prova;
    }

    private void atualizarCronometro() {
        Largada largada = this.comboLargada.getValue();
        if (largada.getHoraInicio() != null) {
            LocalDateTime horaInicio = largada.getHoraInicio();
            LocalDateTime horaAtual = LocalDateTime.now();

            this.lblHora.setText(GeralUtil.calcularDiferencaTempo(horaInicio, horaAtual));
        }
    }

    @FXML
    public void iniciarAtualizarLargada(ActionEvent actionEvent) {
        Largada largada = this.comboLargada.getValue();
        LocalDateTime horaLargada = LocalDateTime.now();
        if (!this.txtHoraInicio.getText().isEmpty()) {
            try {
                int hora = Integer.valueOf(txtHoraInicio.getText().substring(0, 2));
                int min = Integer.valueOf(txtHoraInicio.getText().substring(3, 5));
                int seg = Integer.valueOf(txtHoraInicio.getText().substring(6, 8));
                horaLargada = horaLargada.withHour(hora).withMinute(min).withSecond(seg);
            } catch (Exception ex) {
                super.cj.exibirAviso("aviso_hora_invalida");
                return;
            }
            if (horaLargada.isAfter(LocalDateTime.now())) {
                super.cj.exibirAviso("aviso_hora_largada_futura");
                return;
            }
        } else {
            this.txtHoraInicio.setText(FormatarUtil.localDateTimeToString(horaLargada, FormatarUtil.FORMATO_HORA_SEG));
        }
        largada.setHoraInicio(horaLargada);
        try {
            this.rolex.atualizarHorarioLargada(this.prova, largada);
        } catch (NegocioException ex) {
            super.cj.exibirErro(cm.getMensagem("erro_control_atualizar", "largada"), GeralUtil.getMensagemOriginalErro(ex));
            return;
        }
        this.timeline.play();
        this.btnAdicionarMarcacao.setVisible(true);
        this.btnConcluir.setDisable(false);

    }

    @FXML
    public void visualizarFoto(ActionEvent actionEvent) {
        Button btnViewFoto = (Button) actionEvent.getSource();
        Integer indexFoto = Integer.valueOf(((String[]) btnViewFoto.getId().split("_"))[1]);
        this.cronometragemImagemController.setImagem(new Image(new ByteArrayInputStream(this.mapFoto.get(indexFoto))));
        super.cj.abrirPopup(Stages.CRONOMETRAGEM.ordinal(), IImagemViewController.class, btnViewFoto, ControleJanela.PosicaoReferenciaPopup.DIREITA_ACIMA);
    }

    @FXML
    public void adicionarMarcacao(ActionEvent actionEvent) {
        double y = LINHA_Y_INICIAL + (DISTANCIA_Y * this.contador_visual);
        this.criarCampoNumeroAtleta(y);
        this.criarCampoTempo(y);
        this.criarBotaoViewFoto(y);
        this.criarCampoDuvida(y);
        this.criarBotaoConfirmar(y);
        this.criarBotaoExcluir(y);

        byte[] foto = super.capturarImagem();
        if (foto != null) {
            this.mapFoto.put(this.contador_total, foto);
        }

        this.contador_total++;
        this.contador_visual++;
    }

    @FXML
    public void adicionarCronometragemAtleta(ActionEvent actionEvent) {
        Button btnConf = (Button) actionEvent.getSource();
        Integer indexMarcacao = Integer.valueOf(((String[]) btnConf.getId().split("_"))[1]);
        int index = this.paneMarcacao.getChildren().indexOf(btnConf);
        TextField txtNumero = (TextField) this.paneMarcacao.getChildren().get(index - 4);
        TextField txtTempo = (TextField) this.paneMarcacao.getChildren().get(index - 3);
        CheckBox checkDuvida = (CheckBox) this.paneMarcacao.getChildren().get(index - 1);
        byte[] imagem = this.mapFoto.get(indexMarcacao);

        if (ValidarCampoUtil.validarCampos(txtNumero, txtTempo)) {
            LocalDateTime dataHora = GeralUtil.adicionarTempoNaDataAtual(txtTempo.getText());
            if (dataHora == null) {
                super.cj.exibirAviso("aviso_hora_invalida");
                return;
            }

            Integer numeroAtleta = JavafxUtil.getInteger(txtNumero);
            AtletaInscricao atletaInscricao;

            try {
                atletaInscricao = this.getAtletaInscricao(numeroAtleta);
            } catch (NegocioException ex) {
                super.cj.exibirErro(cm.getMensagem("erro_control_pesquisar", "inscricao"), GeralUtil.getMensagemOriginalErro(ex));
                return;
            }

            if (atletaInscricao == null && this.prova.isDemo()) {
                try {
                    atletaInscricao = this.secretario.criarAtletaDemo(this.prova, numeroAtleta);
                } catch (NegocioException ex) {
                    super.cj.exibirErro(cm.getMensagem("erro_control_salvar", "inscricao"), GeralUtil.getMensagemOriginalErro(ex));
                    return;
                } catch (AvisoNegocioException ex) {
                    super.cj.exibirAviso(ex.getMessage());
                    return;
                }
            } else if (atletaInscricao == null && !super.cj.exibirDialogSimNao("", "aviso_atleta_numero_nao_encontrado", "aviso_registrar_crono_sem_atleta")) {
                return;
            }

            Cronometragem cronometragem = new Cronometragem();
            cronometragem.setNumeroAtleta(numeroAtleta);
            cronometragem.setDuvida(checkDuvida.isSelected());
            cronometragem.setHoraRegistro(dataHora);
            cronometragem.setAtletaInscricao(atletaInscricao);
            cronometragem.setProva(this.prova);

            if (imagem != null) {
                cronometragem.setCronometragemImagem(new CronometragemImagem(imagem));
            }
            try {
                cronometragem = this.rolex.criarNovoCronometragemComVerificacaoTempoRelacionado(cronometragem);
                if (atletaInscricao != null) {
                    this.juiz.adicionarAtletaClassificacaoTemporaria(cronometragem.getAtletaInscricao());
                    this.atualizarTabelaCategoriaResumo();
                }
            } catch (NegocioException ex) {
                super.cj.exibirErro(cm.getMensagem("erro_control_salvar", "cronometragem"), GeralUtil.getMensagemOriginalErro(ex));
                return;
            }

            this.tabCrono.getItems().add(0, cronometragem);

            while (this.tabCrono.getItems().size() > ITENS_POR_PAGINA) {
                this.tabCrono.getItems().remove(ITENS_POR_PAGINA);
            }
            this.excluirCamposMarcacao(index + 1);

            if (paneMarcacao.getChildren().size() > 1) {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        Node node = paneMarcacao.getChildren().get(1);
                        if (node != null) {
                            node.requestFocus();
                        }
                        scrollPane.setVvalue(0);
                    }
                });
            }
        }
    }

    @FXML
    public void removerMarcacao(ActionEvent actionEvent) {
        Button btnExcluir = (Button) actionEvent.getSource();
        int index = this.paneMarcacao.getChildren().indexOf(btnExcluir);
        String tempo = ((TextField) this.paneMarcacao.getChildren().get(index - 4)).getText();
        if (super.cj.exibirDialogSimNao("titulo_apagar", "aviso_pergunta_excluir", null, "marcação de cronometragem " + tempo)) {
            this.excluirCamposMarcacao(index);
        }
    }

    private void excluirCamposMarcacao(int indexBotaoExcluir) {
        for (int i = 0; i < 6; i++) {
            this.paneMarcacao.getChildren().remove(indexBotaoExcluir - i);
        }
        this.contador_visual--;

        if (this.paneMarcacao.getChildren().size() >= indexBotaoExcluir) {
            for (int i = indexBotaoExcluir - 5; i < this.paneMarcacao.getChildren().size(); i++) {
                Node node = this.paneMarcacao.getChildren().get(i);
                double newY = node.getLayoutY() - DISTANCIA_Y;
                node.setLayoutY(newY);
            }
        }
    }

    private void criarCampoNumeroAtleta(double y) {
        TextField txtNumero = new TextField();
        txtNumero.setPromptText("num. atleta");
        txtNumero.setId(TXT_NUMERO + this.contador_total);
        txtNumero.setPrefWidth(NUMERO_LARGURA);
        txtNumero.setPrefHeight(NUMERO_ALTURA);
        txtNumero.setLayoutX(POS_X_NUMERO);
        txtNumero.setLayoutY(y);
        Mascara m = new MascaraClasse(Mascara.TipoMascara.TEXT_FIELD_APENAS_NUMERO, "8", null);
        ValidarCampoUtil.adicionarListener(txtNumero, m);
        this.paneMarcacao.getChildren().add(txtNumero);
    }

    private void criarCampoTempo(double y) {
        TextField txtTempo = new TextField();
        txtTempo.setText(FormatarUtil.localDateTimeToString(LocalDateTime.now(), FormatarUtil.FORMATO_HORA_MILI));
        txtTempo.setId(TXT_TEMPO + this.contador_total);
        txtTempo.setPrefWidth(TEMPO_LARGURA);
        txtTempo.setPrefHeight(TEMPO_ALTURA);
        txtTempo.setLayoutX(POS_X_TEMPO);
        txtTempo.setLayoutY(y);
        txtTempo.setFocusTraversable(false);
        Mascara m = new MascaraClasse(Mascara.TipoMascara.TEXT_FIELD_CUSTOM, null, "99:99:99:999");
        ValidarCampoUtil.adicionarListener(txtTempo, m);
        this.paneMarcacao.getChildren().add(txtTempo);
    }

    private void criarCampoDuvida(double y) {
        CheckBox check = new CheckBox("??");
        check.setId(CHECK_DUVIDA + this.contador_total);
        check.setLayoutX(POS_X_DUVIDA);
        check.setLayoutY(y + 3);
        check.setFocusTraversable(false);
        this.paneMarcacao.getChildren().add(check);
        check.setTooltip(new Tooltip("Marque em caso de dúvida"));
    }

    private void criarBotaoViewFoto(double y) {
        Button btnViewFoto = new Button();
        btnViewFoto.setId(BTN_VIEW_FOTO + this.contador_total);
        btnViewFoto.setPrefWidth(30);
        btnViewFoto.setPrefHeight(30);
        btnViewFoto.setLayoutX(POS_X_BTN_VIEW_FOTO);
        btnViewFoto.setLayoutY(y - 2);
        btnViewFoto.setFocusTraversable(false);
        btnViewFoto.setOnAction(CronometroController.this::visualizarFoto);
        btnViewFoto.getStyleClass().add(EstiloCss.BOTAO_VIEW.getValor());
        this.paneMarcacao.getChildren().add(btnViewFoto);
        btnViewFoto.setTooltip(new Tooltip("Visualizar foto da chegada"));
        btnViewFoto.setVisible(super.cameraLigadaProperty.get());
    }

    private void criarBotaoConfirmar(double y) {
        Button btnConf = new Button();
        btnConf.setId(BTN_CONF + this.contador_total);
        btnConf.setPrefWidth(30);
        btnConf.setPrefHeight(30);
        btnConf.setLayoutX(POS_X_BTN_CONF);
        btnConf.setLayoutY(y - 2);
        btnConf.setFocusTraversable(false);
        btnConf.setOnAction(CronometroController.this::adicionarCronometragemAtleta);
        btnConf.getStyleClass().add(EstiloCss.BOTAO_ADICIONAR.getValor());
        this.paneMarcacao.getChildren().add(btnConf);
        btnConf.setTooltip(new Tooltip("Confirmar cronometragem do atleta"));
    }

    private void criarBotaoExcluir(double y) {
        Button btnExcluir = new Button();
        btnExcluir.setId(BTN_EXCL + this.contador_total);
        btnExcluir.setPrefWidth(30);
        btnExcluir.setPrefHeight(30);
        btnExcluir.setLayoutX(POS_X_BTN_EXCL);
        btnExcluir.setLayoutY(y - 2);
        btnExcluir.setFocusTraversable(false);
        btnExcluir.setOnAction(CronometroController.this::removerMarcacao);
        btnExcluir.getStyleClass().add(EstiloCss.BOTAO_EXCLUIR.getValor());
        this.paneMarcacao.getChildren().add(btnExcluir);
        btnExcluir.setTooltip(new Tooltip("Excluir marcação"));
    }

    private AtletaInscricao getAtletaInscricao(Integer numeroAtleta) throws NegocioException {
        return this.secretario.pesquisarAtletaInscricaoPorNumeroAtleta(this.prova, numeroAtleta);
    }

    private void configurarComboLargada() {
        this.comboLargada.setConverter(new GenericComboBoxConverter<>("nome"));
        this.comboLargada.valueProperty().addListener((ObservableValue<? extends Largada> observable, Largada oldValue, Largada newValue) -> {
            if (newValue != null && newValue != oldValue) {
                this.txtHoraPrevista.setText(FormatarUtil.localDateTimeToString(newValue.getHoraPrevista(), FormatarUtil.FORMATO_HORA_MIN));
                if (newValue.getHoraInicio() != null) {
                    this.txtHoraInicio.setText(FormatarUtil.localDateTimeToString(newValue.getHoraInicio(), FormatarUtil.FORMATO_HORA_SEG));
                    this.btnIniciarLargada.setText("atualizar");
                    this.timeline.play();
                } else {
                    this.txtHoraInicio.setText("");
                    this.btnIniciarLargada.setText("iniciar");
                    this.lblHora.setText("");
                    this.timeline.pause();
                }
            }
        });
    }

    @FXML
    public void finalizarCronometragem(ActionEvent actionEvent) {
        if (this.contador_visual > 0) {
            super.cj.exibirAviso("Existem marcações de cronometragem não confirmadas.", "Confirme ou exclua estas marcações antes de concluir a prova.");
        } else if (super.cj.exibirDialogSimNao("Finalizar cronometragem", "Você tem certeza que deseja encerrar a prova?", "Novas cronometragens não poderão ser realizadas após este procedimento.")) {
            try {
                prova = this.rolex.encerrarCronometragem(this.prova);
                this.desligarCamera();
                if (GenericValidator.isBlankOrNull(this.prova.getMotivoPendencia())) {
                    super.cj.exibirInformacao("info_crono_sem_pendencia");
                } else {
                    super.cj.exibirAviso("aviso_crono_fechada_pendencia", this.prova.getMotivoPendencia().replaceAll("\\|", "\n"));
                }
            } catch (NegocioException ex) {
                super.cj.exibirErro(cm.getMensagem("erro_control_atualizar", "prova"), GeralUtil.getMensagemOriginalErro(ex));
                return;
            } catch (AvisoNegocioException ex) {
                super.cj.exibirAviso(ex.getMessage());
                return;
            }
            super.aoFecharJanela();
            super.getStage().close();
        }
    }

    private void configurarValidacaoFecharJanela() {
        super.getStage().setOnCloseRequest((WindowEvent event) -> {
            if (CronometroController.this.contador_visual > 0) {
                CronometroController.super.cj.exibirAviso("Existem marcações de cronometragem não confirmadas.", "Confirme ou exclua estas marcações antes de sair.");
                event.consume();
            } else {
                CronometroController.super.aoFecharJanela();
                CronometroController.super.getStage().close();
            }
        });
    }

    private void configurarTabelaColunas() {
        this.colNumero.setCellValueFactory((TableColumn.CellDataFeatures<Cronometragem, Integer> linha) -> new ReadOnlyObjectWrapper<>(linha.getValue().getNumeroAtleta()));
        this.colAtleta.setCellValueFactory((TableColumn.CellDataFeatures<Cronometragem, String> linha) -> new ReadOnlyObjectWrapper<>(linha.getValue().getAtletaInscricao() == null ? "" : linha.getValue().getAtletaInscricao().getAtleta().getNome()));
        this.colCategoria.setCellValueFactory((TableColumn.CellDataFeatures<Cronometragem, String> linha) -> new ReadOnlyObjectWrapper<>(linha.getValue().getAtletaInscricao() == null ? "" : linha.getValue().getAtletaInscricao().getCategoria().getCategoriaAtleta().getNome()));
        this.colHora.setCellValueFactory((TableColumn.CellDataFeatures<Cronometragem, String> linha) -> new ReadOnlyObjectWrapper<>(FormatarUtil.localDateTimeToString(linha.getValue().getHoraRegistro(), FormatarUtil.FORMATO_DATA_HORA_MILI)));
        this.colPosicao.setCellValueFactory((TableColumn.CellDataFeatures<Cronometragem, Integer> linha) -> new ReadOnlyObjectWrapper<>(linha.getValue().getAtletaInscricao() == null ? null : juiz.pesquisarPosicaoTemporariaAtleta(linha.getValue().getAtletaInscricao())));
        this.colTempo.setCellValueFactory((TableColumn.CellDataFeatures<Cronometragem, String> linha) -> new ReadOnlyObjectWrapper<>(linha.getValue().getTempoVolta() == null ? null : FormatarUtil.formatarTempoMilisegundos(linha.getValue().getTempoVolta())));

        this.colNumero.getStyleClass().add(ListaConstantesBase.EstiloCss.TAB_COL_CENTRO.getValor());
        this.colAtleta.getStyleClass().add(ListaConstantesBase.EstiloCss.TAB_COL_ESQUERDO.getValor());
        this.colCategoria.getStyleClass().add(ListaConstantesBase.EstiloCss.TAB_COL_ESQUERDO.getValor());
        this.colHora.getStyleClass().add(ListaConstantesBase.EstiloCss.TAB_COL_CENTRO.getValor());
        this.colPosicao.getStyleClass().add(ListaConstantesBase.EstiloCss.TAB_COL_CENTRO.getValor());
        this.colTempo.getStyleClass().add(ListaConstantesBase.EstiloCss.TAB_COL_CENTRO.getValor());

        this.paneMarcacao.heightProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
            this.scrollPane.setVvalue(1.0);
        });

        this.tabCrono.setRowFactory(new TableViewDuploClique<Cronometragem>(true) {
            @Override
            public Cronometragem eventoDuploClique(Cronometragem cronometragem) {
                CronometroController.this.cronometragemEdicaoController.setOperacao(ListaConstantesBase.Operacao.EDITAR);
                CronometroController.this.cronometragemEdicaoController.setCronometragemEdicao(cronometragem);
                CronometroController.super.cj.abrirJanela(ListaConstantes.Stages.CRONOMETRAGEM_EDICAO.ordinal(), ListaConstantes.Stages.CRONOMETRAGEM.ordinal(), ICronometragemEdicaoController.class, "cronometragem", true);
                //CronometroController.this.dataSource.atualizar(cronometragem, CronometroController.this.cronometragemEdicaoController.getCronometragemEdicao());
                cronometragem = CronometroController.this.cronometragemEdicaoController.getCronometragemEdicao();
                CronometroController.this.cronometragemEdicaoController.setCronometragemEdicao(null);
                return cronometragem;
            }
        });
    }

    private void configurarTabelaColunasResumoCategoria() {
        this.colCatResumo.setCellValueFactory((TableColumn.CellDataFeatures<CategoriaProvaResumo, String> linha) -> new ReadOnlyObjectWrapper<>(linha.getValue().getCategoriaDaProva().getCategoriaAtleta().getNome()));
        this.colTotalCat.setCellValueFactory((TableColumn.CellDataFeatures<CategoriaProvaResumo, Long> linha) -> new ReadOnlyObjectWrapper<>(linha.getValue().getInscritos()));
        this.colConcluidoCat.setCellValueFactory((TableColumn.CellDataFeatures<CategoriaProvaResumo, Long> linha) -> new ReadOnlyObjectWrapper<>(linha.getValue().getConcluiramProva()));
        this.colFaltamCat.setCellValueFactory((TableColumn.CellDataFeatures<CategoriaProvaResumo, Long> linha) -> new ReadOnlyObjectWrapper<>(linha.getValue().getNaoConcluiramProva()));

        this.colCatResumo.getStyleClass().add(ListaConstantesBase.EstiloCss.TAB_COL_ESQUERDO.getValor());
        this.colTotalCat.getStyleClass().add(ListaConstantesBase.EstiloCss.TAB_COL_CENTRO.getValor());
        this.colConcluidoCat.getStyleClass().add(ListaConstantesBase.EstiloCss.TAB_COL_CENTRO.getValor());
        this.colFaltamCat.getStyleClass().add(ListaConstantesBase.EstiloCss.TAB_COL_CENTRO.getValor());
    }

    @FXML
    public void abrirClassificacaoTemporaria() {
        this.classificacaoTemporariaController.setProva(this.prova);
        super.cj.abrirJanelaComRedimensionamento(Stages.CLASSIFICACAO_TEMPORARIA.ordinal(), null, ClassificacaoTemporariaController.class, "Classificação temporária dos atletas", novaJanela);

    }

    @Override
    protected String getPath() {
        return "/Taurus/corridas/" + this.prova.getNome() + "/media/video/";
    }

    private void carregarConfiguracaoCamera() {
        try {
            Configuracao c = this.administrador.pesquisarConfiguracao();
            super.setWebcam(Webcam.getWebcamByName(c.getNomeCamera()));
            super.setResolucao(Util.stringToDimension(c.getResolucaoCamera()));
        } catch (NegocioException ex) {
            this.cj.exibirErro(cm.getMensagem("erro_control_pesquisar", "Configuracao"), GeralUtil.getMensagemOriginalErro(ex));
        }
    }

    private void atualizarTabelaCategoriaResumo() {
        this.tabCategoriaResumo.getItems().clear();
        try {
            this.tabCategoriaResumo.getItems().addAll(this.rolex.getListaAtletasCorrendo(this.prova));
        } catch (NegocioException ex) {
            super.cj.exibirErro(cm.getMensagem("erro_control_pesquisar", "Atleta Inscrição"), GeralUtil.getMensagemOriginalErro(ex));
        }
    }
}
