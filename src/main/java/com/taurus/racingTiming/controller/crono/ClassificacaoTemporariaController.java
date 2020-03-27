package com.taurus.racingTiming.controller.crono;

import com.taurus.exception.NegocioException;
import com.taurus.javafx.controller.FXMLBaseController;
import com.taurus.racingTiming.entidade.corrida.AtletaInscricao;
import com.taurus.racingTiming.entidade.corrida.Prova;
import com.taurus.racingTiming.negocio.IJuiz;
import com.taurus.racingTiming.negocio.comparador.AtletaInscricaoComparadorClassificacao;
import com.taurus.racingTiming.util.ListaConstantes;
import com.taurus.util.FormatarUtil;
import com.taurus.util.GeralUtil;
import com.taurus.util.ListaConstantesBase;
import com.taurus.util.Pagina;
import com.taurus.util.annotation.Parametro;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Pagination;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ClassificacaoTemporariaController extends FXMLBaseController implements IClassificacaoTemporariaController {

    private static final Log LOG = LogFactory.getLog(ClassificacaoTemporariaController.class);
    private static int ITENS_POR_PAGINA = 18;

    public ClassificacaoTemporariaController() {
        super();
    }

    @Autowired
    private IJuiz juiz;

    @Parametro
    private Prova prova;

    private Pagina pagina = new Pagina(1, ITENS_POR_PAGINA, null);

    @Value("${img.logo}")
    private String resourceLogo;

    private Map<String, Map<AtletaInscricaoComparadorClassificacao, AtletaInscricao>> mapClassificacaoTemporaria;
    private List<AtletaInscricao> listaClassificacao = null;
    private List<String> listaCategoria = null;
    private Iterator<String> itCategoria;
    private StringProperty nomeCategoriaProperty = new SimpleStringProperty();
    private boolean exibirListaVazia = true;
    private boolean exibir = false;

    private DoubleProperty tempoProgressoProperty = new SimpleDoubleProperty();

    private Timeline timeline;

    @FXML
    private Label lblNomeProva;
    @FXML
    private Label lblCategoria;
    @FXML
    private Label lblHoraAtualizacao;
    @FXML
    private ImageView imgTaurus;
    @FXML
    private Button btnPlayPause;

    @FXML
    private Pagination paginacao;
    @FXML
    private TableView<AtletaInscricao> tabClassificacao;
    @FXML
    private TableColumn<AtletaInscricao, Integer> colPosicao;
    @FXML
    private TableColumn<AtletaInscricao, String> colNumero;
    @FXML
    private TableColumn<AtletaInscricao, String> colAtleta;
    @FXML
    private TableColumn<AtletaInscricao, String> colEquipe;
    @FXML
    private TableColumn<AtletaInscricao, String> colCategoria;
    @FXML
    private TableColumn<AtletaInscricao, Integer> colVolta;
    @FXML
    private TableColumn<AtletaInscricao, String> colTempo;
    @FXML
    private TableColumn<AtletaInscricao, String> colDifCat;

    @FXML
    private ProgressBar progressBar;

    @Value("${fxml.classificacaoTemporaria.view}")
    @Override
    public void setFxmlFilePath(String filePath) {
        this.fxmlFilePath = filePath;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.timeline = new Timeline(new KeyFrame(Duration.millis(10000), (ActionEvent arg0) -> {
            ClassificacaoTemporariaController.this.atualizarTabelaClassificacao();
        }));
        this.timeline.setCycleCount(Timeline.INDEFINITE);

        this.lblCategoria.textProperty().bind(this.nomeCategoriaProperty);
        this.progressBar.progressProperty().bind(this.tempoProgressoProperty);

        this.paginacao.setPageFactory(this::paginar);
        this.imgTaurus.setImage(new Image(ClassificacaoTemporariaController.class.getResourceAsStream(resourceLogo)));
        this.configurarTabelaColunas();
    }

    @Override
    public void reinicializarJanelaEspecifico() {
        this.tabClassificacao.getItems().clear();
    }

    @Override
    public void aoAbrirJanela() {
        try {
            this.juiz.carregarClassificacaoTemporiaria(this.prova);
        } catch (NegocioException ex) {
            super.cj.exibirErro(cm.getMensagem("erro_control_pesquisar", "Atleta Inscrição"), GeralUtil.getMensagemOriginalErro(ex));
            return;
        }

        this.lblNomeProva.setText(this.prova.getNome());
        this.atualizarListasClassificacoes();
        this.timeline.play();
        this.tempoProgressoProperty.setValue(0);

        this.removerTooltipEstiloBotao(this.btnPlayPause);
        this.btnPlayPause.getStyleClass().add(ListaConstantes.EstiloCss.BOTAO_PAUSE.getValor());
        this.btnPlayPause.setTooltip(new Tooltip("pausar"));

        Runnable r = new Runnable() {
            @Override
            public void run() {
                while (timeline.getStatus() == Animation.Status.RUNNING || timeline.getStatus() == Animation.Status.PAUSED) {
                    try {
                        double segundo = timeline.getCurrentTime().toSeconds();
                        tempoProgressoProperty.set(segundo / 10);
                        Thread.sleep(10L);
                    } catch (InterruptedException ex) {
                    }
                }
            }
        };
        new Thread(r).start();
    }

    @Override
    public void aoFecharJanelaEspecifico() {
        this.timeline.stop();
    }

    @FXML
    public void proximaPagina() {
        this.atualizarTabelaClassificacao();
    }

    private void atualizarTabelaClassificacao() {
        exibir = true;
        int indexPagina = this.pagina.getNumeroPagina() - 1;
        if (indexPagina == this.paginacao.getCurrentPageIndex()
                || indexPagina + 1 > this.paginacao.getPageCount()) {
            this.paginacao.getPageFactory().call(indexPagina);
        } else { //chama a paginacao do componente
            this.paginacao.setCurrentPageIndex(indexPagina);
        }

    }

    private TableView paginar(int indexPagina) {
        this.tabClassificacao.getItems().clear();
        if (this.exibir) {
            boolean sair = false;
            while (!sair) {
                int indiceInicio = (this.pagina.getNumeroPagina() - 1) * this.pagina.getItensPorPagina();
                int indiceFim = indiceInicio + this.pagina.getItensPorPagina();
                if (this.listaClassificacao.size() > indiceInicio) {
                    this.pagina.setTotalItens(Integer.valueOf(this.listaClassificacao.size()).longValue());

                    // Sempre que o total de paginas eh atualizado,  o paginador Javafx inicia automaticamente na primeira pagina
                    if (this.paginacao.getPageCount() != this.pagina.getNumeroPaginas()) {
                        this.paginacao.setPageCount(pagina.getNumeroPaginas());
                        return this.tabClassificacao;
                    } else {
                        // marca a proxima pagina a ser exibida
                        this.pagina.setNumeroPagina(this.pagina.getNumeroPagina() + 1);
                    }
                    if (this.listaClassificacao.size() < indiceFim) {
                        indiceFim = this.listaClassificacao.size();
                    }
                    for (int i = indiceInicio; i < indiceFim; i++) {
                        int indiceAnteiror = i == 0 ? 0 : i-1;
                        this.adicionarPosicao(this.listaClassificacao.get(i), this.listaClassificacao.get(indiceAnteiror), i + 1);
                        this.tabClassificacao.getItems().add(this.listaClassificacao.get(i));
                    }
                    exibirListaVazia = false;
                    sair = true;
                } else if (this.exibirListaVazia) {
                    this.exibirListaVazia = false;
                    sair = true;
                } else {
                    this.exibirListaVazia = true;
                    this.posicionarProximaCategoria();
                }
            }
        }
        return this.tabClassificacao;
    }

    private void configurarTabelaColunas() {
        this.colPosicao.setCellValueFactory(linha -> new ReadOnlyObjectWrapper(linha.getValue().getPosicaoCategoria()));
        this.colNumero.setCellValueFactory(linha -> new ReadOnlyObjectWrapper(linha.getValue().getNumeroAtleta()));
        this.colAtleta.setCellValueFactory(linha -> new ReadOnlyObjectWrapper(linha.getValue().getAtleta().getNome()));
        this.colEquipe.setCellValueFactory(linha -> new ReadOnlyObjectWrapper(linha.getValue().getEquipe()));
        this.colCategoria.setCellValueFactory(linha -> new ReadOnlyObjectWrapper(linha.getValue().getCategoria().getCategoriaAtleta().getNome()));
        this.colVolta.setCellValueFactory(linha -> new ReadOnlyObjectWrapper(linha.getValue().getNumeroVolta()));
        this.colTempo.setCellValueFactory(linha -> new ReadOnlyObjectWrapper(linha.getValue().getTempo() == null ? null : FormatarUtil.formatarTempoMilisegundos(linha.getValue().getTempo())));
        this.colDifCat.setCellValueFactory(linha -> new ReadOnlyObjectWrapper(linha.getValue().getDiferencaCategoria() == null ? null : FormatarUtil.formatarTempoMilisegundos(linha.getValue().getDiferencaCategoria())));

        this.colPosicao.getStyleClass().add(ListaConstantesBase.EstiloCss.TAB_COL_CENTRO.getValor());
        this.colNumero.getStyleClass().add(ListaConstantesBase.EstiloCss.TAB_COL_CENTRO.getValor());
        this.colAtleta.getStyleClass().add(ListaConstantesBase.EstiloCss.TAB_COL_ESQUERDO.getValor());
        this.colEquipe.getStyleClass().add(ListaConstantesBase.EstiloCss.TAB_COL_ESQUERDO.getValor());
        this.colCategoria.getStyleClass().add(ListaConstantesBase.EstiloCss.TAB_COL_ESQUERDO.getValor());
        this.colVolta.getStyleClass().add(ListaConstantesBase.EstiloCss.TAB_COL_CENTRO.getValor());
        this.colTempo.getStyleClass().add(ListaConstantesBase.EstiloCss.TAB_COL_CENTRO.getValor());
        this.colDifCat.getStyleClass().add(ListaConstantesBase.EstiloCss.TAB_COL_CENTRO.getValor());

        this.tabClassificacao.heightProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
            // altura da tabela - altura do cabecalho; divido pela altura de uma linha;
            ITENS_POR_PAGINA = (newValue.intValue() - 25) / 24;
        });
        this.tabClassificacao.widthProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
            this.colAtleta.setPrefWidth(newValue.intValue() - 802);
        });
    }

    @Override
    public void setProva(Prova prova) {
        this.prova = prova;
    }

    private void atualizarListasClassificacoes() {
        this.lblHoraAtualizacao.setText("atualizado ás " + FormatarUtil.localDateTimeToString(LocalDateTime.now(), FormatarUtil.FORMATO_HORA_SEG));
        this.mapClassificacaoTemporaria = this.juiz.getMapClassificacaoTemporaria();
        this.listaCategoria = new ArrayList(this.mapClassificacaoTemporaria.keySet());
        this.itCategoria = this.listaCategoria.iterator();
        this.pagina = new Pagina(1, ITENS_POR_PAGINA, null);
        if (this.itCategoria.hasNext()) {
            this.posicionarProximaCategoria();
        }
    }

    private void posicionarProximaCategoria() {
        if (itCategoria.hasNext()) {
            this.nomeCategoriaProperty.set(itCategoria.next());
            this.listaClassificacao = new ArrayList(this.mapClassificacaoTemporaria.get(this.nomeCategoriaProperty.get()).values());
            this.pagina = new Pagina(1, ITENS_POR_PAGINA, null);
        } else {
            this.atualizarListasClassificacoes();
        }
    }

    private void adicionarPosicao(AtletaInscricao atleta, AtletaInscricao atletaPrimeiro, int posicao) {
        atleta.setPosicaoCategoria(posicao);
        atleta.setDiferencaCategoria(atleta.getTempo() - atletaPrimeiro.getTempo());
    }

    @FXML
    public void ligarPausar(ActionEvent actionEvent) {
        if (this.timeline.getStatus() == Animation.Status.RUNNING) {
            this.timeline.pause();
            this.removerTooltipEstiloBotao(this.btnPlayPause);
            this.btnPlayPause.getStyleClass().add(ListaConstantes.EstiloCss.BOTAO_PLAY.getValor());
            this.btnPlayPause.setTooltip(new Tooltip("ligar"));
        } else if (this.timeline.getStatus() == Animation.Status.PAUSED) {
            this.timeline.play();
            this.removerTooltipEstiloBotao(this.btnPlayPause);
            this.btnPlayPause.getStyleClass().add(ListaConstantes.EstiloCss.BOTAO_PAUSE.getValor());
            this.btnPlayPause.setTooltip(new Tooltip("pausar"));
        }
    }

    private void removerTooltipEstiloBotao(Button botao) {
        botao.getStyleClass().clear();
        botao.setTooltip(null);
    }
}
