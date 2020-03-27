package com.taurus.racingTiming.controller.resultado;

import com.taurus.exception.NegocioException;
import com.taurus.javafx.controller.FXMLBaseController;
import com.taurus.javafx.converter.GenericComboBoxConverter;
import com.taurus.javafx.util.ControleJanela;
import com.taurus.racingTiming.entidade.corrida.AtletaInscricao;
import com.taurus.racingTiming.entidade.corrida.CategoriaDaProva;
import com.taurus.racingTiming.entidade.corrida.Percurso;
import com.taurus.racingTiming.entidade.corrida.Prova;
import com.taurus.racingTiming.negocio.IImpressor;
import com.taurus.racingTiming.negocio.IJuiz;
import com.taurus.racingTiming.negocio.ISecretario;
import com.taurus.racingTiming.util.ListaConstantes;
import com.taurus.racingTiming.util.TarefaThread;
import com.taurus.util.FormatarUtil;
import com.taurus.util.GeralUtil;
import com.taurus.util.ListaConstantesBase;
import com.taurus.util.ListaConstantesBase.Sexo;
import com.taurus.util.Pagina;
import com.taurus.util.annotation.Mascara;
import com.taurus.util.annotation.Parametro;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Pagination;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ResultadoClassificacaoController extends FXMLBaseController implements IResultadoClassificacaoController {

    private static final Log LOG = LogFactory.getLog(ResultadoClassificacaoController.class);
    private static final int ITENS_POR_PAGINA = 21;

    public ResultadoClassificacaoController() {
        super();
    }

    @Autowired
    private IResultadoCronoPopupController cronoPopup;
    @Autowired
    private ISecretario secretario;

    @Autowired
    private IImpressor impressor;

    @Autowired
    private IJuiz juiz;

    @Parametro
    private Prova prova;

    private Pagina pagina = new Pagina(1, ITENS_POR_PAGINA, null);
    //private AtletaInscricaoDataSource dataSource;
    private Filtro filtro = Filtro.GERAL;

    private Percurso filtroPercurso;
    private ListaConstantesBase.Sexo filtroSexo;
    private CategoriaDaProva filtroCategoria;
    private String filtroNumeroAtleta;

    @FXML
    private TextField txtNomeProva;
    @FXML
    private ComboBox<Percurso> comboPercurso;
    @FXML
    private Button btnPesquisarGeral;
    @FXML
    private Button btnReportGeral;
    @FXML
    private RadioButton radioMasculino;
    @FXML
    private RadioButton radioFeminino;
    @FXML
    private ComboBox<CategoriaDaProva> comboCategoria;
    @FXML
    private Button btnPesquisarCategoria;
    @FXML
    private Button btnReportCategoria;
    @FXML
    @Mascara(tipo = Mascara.TipoMascara.TEXT_FIELD_APENAS_NUMERO, tamanhoMax = "8")
    private TextField txtNumeroAtleta;
    @FXML
    private Button btnPesquisarAtleta;
    @FXML
    private CheckBox checkDuvida;
    @FXML
    @Mascara(tipo = Mascara.TipoMascara.TEXT_FIELD_CUSTOM, custom = "99:99:99")
    private TextField txtHoraInicio;
    @FXML
    @Mascara(tipo = Mascara.TipoMascara.TEXT_FIELD_CUSTOM, custom = "99:99:99")
    private TextField txtHoraFim;

    @FXML
    private Pagination paginacao;
    @FXML
    private TableView<AtletaInscricao> tabClassificacao;
    @FXML
    private TableColumn<AtletaInscricao, Integer> colPosicao;
    @FXML
    private TableColumn<AtletaInscricao, Integer> colPosGeral;
    @FXML
    private TableColumn<AtletaInscricao, String> colNumero;
    @FXML
    private TableColumn<AtletaInscricao, String> colAtleta;
    @FXML
    private TableColumn<AtletaInscricao, String> colEquipe;
    @FXML
    private TableColumn<AtletaInscricao, String> colCategoria;
    @FXML
    private TableColumn<AtletaInscricao, String> colTempo;
    @FXML
    private TableColumn<AtletaInscricao, String> colDifCat;
    @FXML
    private TableColumn<AtletaInscricao, String> colDifGeral;

    @FXML
    ProgressIndicator indicadorProgresso;

    private enum Filtro {
        GERAL, CATEGORIA, ATLETA;
    }

    @Value("${fxml.resultadoClassificacao.view}")
    @Override
    public void setFxmlFilePath(String filePath) {
        this.fxmlFilePath = filePath;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.paginacao.setPageFactory(this::paginar);

        this.comboPercurso.setConverter(new GenericComboBoxConverter<>("nome"));
        this.comboCategoria.setConverter(new GenericComboBoxConverter<>("categoriaAtleta.nome"));

        this.btnPesquisarGeral.getStyleClass().add(ListaConstantesBase.EstiloCss.BOTAO_PESQUISAR.getValor());
        this.btnPesquisarGeral.setTooltip(new Tooltip("classificação geral"));
        this.btnReportGeral.getStyleClass().add(ListaConstantesBase.EstiloCss.BOTAO_PRINT.getValor());
        this.btnReportGeral.setTooltip(new Tooltip("relatório geral"));

        this.btnPesquisarCategoria.getStyleClass().add(ListaConstantesBase.EstiloCss.BOTAO_PESQUISAR.getValor());
        this.btnPesquisarCategoria.setTooltip(new Tooltip("classificação por categoria"));
        this.btnReportCategoria.getStyleClass().add(ListaConstantesBase.EstiloCss.BOTAO_PRINT.getValor());
        this.btnReportCategoria.setTooltip(new Tooltip("relatório por categoria"));

        this.btnPesquisarAtleta.getStyleClass().add(ListaConstantesBase.EstiloCss.BOTAO_PESQUISAR.getValor());
        this.btnPesquisarAtleta.setTooltip(new Tooltip("classificação do atleta"));
        this.configurarTabelaColunas();

        this.indicadorProgresso.setVisible(false);
    }

    @Override
    public void reinicializarJanelaEspecifico() {
        this.filtro = null;
        this.filtroPercurso = null;
        this.filtroSexo = null;
        this.filtroCategoria = null;
        this.filtroNumeroAtleta = null;

        this.comboPercurso.getItems().clear();
        this.comboCategoria.getItems().clear();

        this.radioMasculino.setSelected(true);
        this.tabClassificacao.getItems().clear();
    }

    @Override
    public void aoAbrirJanela() {
        try {
            this.comboPercurso.getItems().addAll(this.secretario.pesquisarPercurso(this.prova));
        } catch (NegocioException ex) {
            this.cj.exibirErro(cm.getMensagem("erro_control_pesquisar", "percurso"), GeralUtil.getMensagemOriginalErro(ex));
        }

        try {
            this.comboCategoria.getItems().addAll(this.secretario.pesquisarCategoriaDaProva(this.prova));
        } catch (NegocioException ex) {
            this.cj.exibirErro(cm.getMensagem("erro_control_pesquisar", "categoria_atleta"), GeralUtil.getMensagemOriginalErro(ex));
        }

        this.txtNomeProva.setText(this.prova.getNome());
        //this.reiniciarPaginacao();
        this.comboPercurso.requestFocus();
    }

    @Override
    public void aoFecharJanelaEspecifico() {
    }

    @FXML
    public void filtrarGeral() {
        this.filtro = Filtro.GERAL;
        this.filtroPercurso = this.comboPercurso.getValue();
        this.filtroSexo = this.radioMasculino.isSelected() ? Sexo.MASCULINO : Sexo.FEMININO;
        this.reiniciarPaginacao();
    }

    @FXML
    public void filtrarCategoria() {
        this.filtro = Filtro.CATEGORIA;
        this.filtroCategoria = this.comboCategoria.getValue();
        this.reiniciarPaginacao();
    }

    @FXML
    public void filtrarAtleta() {
        this.filtro = Filtro.ATLETA;
        this.filtroNumeroAtleta = this.txtNumeroAtleta.getText();
        this.reiniciarPaginacao();
    }

    private TableView paginar(int indexPagina) {
        try {
            this.pagina.setNumeroPagina(indexPagina + 1);
            List<AtletaInscricao> atletas = new ArrayList();
            if (filtro != null) {
                switch (this.filtro) {
                    case GERAL:
                        atletas = this.juiz.pesquisarClassificacaoProvaGeral(this.filtroPercurso, this.filtroSexo, pagina);
                        break;
                    case CATEGORIA:
                        atletas = this.juiz.pesquisarClassificacaoProvaCategoria(this.filtroCategoria, pagina);
                        break;
                    case ATLETA:
                        AtletaInscricao atleta = this.juiz.pesquisarClassificacaoAtleta(this.prova, this.filtroNumeroAtleta);
                        if (atleta != null) {
                            atletas.add(atleta);
                        }
                        break;
                }
            }

            this.tabClassificacao.setItems(FXCollections.observableArrayList(atletas));
            this.paginacao.setPageCount(pagina.getNumeroPaginas());
        } catch (NegocioException ex) {
            this.cj.exibirErro(cm.getMensagem("erro_control_pesquisar", "cronometragem"), GeralUtil.getMensagemOriginalErro(ex));
        }
        return this.tabClassificacao;
    }

    private void configurarTabelaColunas() {
        this.colPosicao.setCellValueFactory(linha -> new ReadOnlyObjectWrapper(linha.getValue().getPosicaoCategoria()));
        this.colPosGeral.setCellValueFactory(linha -> new ReadOnlyObjectWrapper(linha.getValue().getPosicaoGeral()));
        this.colNumero.setCellValueFactory(linha -> new ReadOnlyObjectWrapper(linha.getValue().getNumeroAtleta()));
        this.colAtleta.setCellValueFactory(linha -> new ReadOnlyObjectWrapper(linha.getValue().getAtleta().getNome()));
        this.colEquipe.setCellValueFactory(linha -> new ReadOnlyObjectWrapper(linha.getValue().getEquipe()));
        this.colCategoria.setCellValueFactory(linha -> new ReadOnlyObjectWrapper(linha.getValue().getCategoria().getCategoriaAtleta().getNome()));
        this.colTempo.setCellValueFactory(linha -> new ReadOnlyObjectWrapper(linha.getValue().getTempo() == null ? null : FormatarUtil.formatarTempoMilisegundos(linha.getValue().getTempo())));
        this.colDifCat.setCellValueFactory(linha -> new ReadOnlyObjectWrapper(linha.getValue().getDiferencaCategoria() == null ? null : FormatarUtil.formatarTempoMilisegundos(linha.getValue().getDiferencaCategoria())));
        this.colDifGeral.setCellValueFactory(linha -> new ReadOnlyObjectWrapper(linha.getValue().getDiferencaGeral() == null ? null : FormatarUtil.formatarTempoMilisegundos(linha.getValue().getDiferencaGeral())));

        this.colPosicao.getStyleClass().add(ListaConstantesBase.EstiloCss.TAB_COL_CENTRO.getValor());
        this.colPosGeral.getStyleClass().add(ListaConstantesBase.EstiloCss.TAB_COL_CENTRO.getValor());
        this.colNumero.getStyleClass().add(ListaConstantesBase.EstiloCss.TAB_COL_CENTRO.getValor());
        this.colAtleta.getStyleClass().add(ListaConstantesBase.EstiloCss.TAB_COL_ESQUERDO.getValor());
        this.colEquipe.getStyleClass().add(ListaConstantesBase.EstiloCss.TAB_COL_ESQUERDO.getValor());
        this.colCategoria.getStyleClass().add(ListaConstantesBase.EstiloCss.TAB_COL_ESQUERDO.getValor());
        this.colTempo.getStyleClass().add(ListaConstantesBase.EstiloCss.TAB_COL_CENTRO.getValor());
        this.colDifCat.getStyleClass().add(ListaConstantesBase.EstiloCss.TAB_COL_CENTRO.getValor());
        this.colDifGeral.getStyleClass().add(ListaConstantesBase.EstiloCss.TAB_COL_CENTRO.getValor());

        // Evento clique na tabela.
        // Nao foi utilizado a classe que implmenta a factory de um clique pois precisou do objeto "row" 
        // dentro do evento de clique para posicionar o popup
        this.tabClassificacao.setRowFactory((TableView<AtletaInscricao> param) -> {
            TableRow<AtletaInscricao> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 1 && (!row.isEmpty())) {
                    cronoPopup.setAtletaInscricao(row.getItem());
                    ResultadoClassificacaoController.super.cj.abrirPopup(ListaConstantes.Stages.RESULTADO_CLASSIFICACAO.ordinal(), IResultadoCronoPopupController.class, row, ControleJanela.PosicaoReferenciaPopup.CENTRO_ABAIXO);
                }
            });
            return row;
        });
    }

    private void reiniciarPaginacao() {
        this.pagina.setTotalItens(null);
        this.paginacao.setCurrentPageIndex(0);
        this.paginacao.getPageFactory().call(0);

    }

    @FXML
    public void relatorioGeral(ActionEvent actionEvent) {
        this.filtroSexo = this.radioMasculino.isSelected() ? Sexo.MASCULINO : Sexo.FEMININO;
        if (this.comboPercurso.getValue() != null) {
            String msgErro = cm.getMensagem("erro_rpt_geral", this.filtroSexo.getDescricao(), this.comboPercurso.getValue().getNome());
            Task<byte[]> tarefa = new TarefaThread<byte[]>("info_rpt_concluido", msgErro, super.cj) {
                @Override
                protected byte[] call() throws Exception {
                    return ResultadoClassificacaoController.this.impressor.gerarRelatorioClassificacaoProvaGeral(ResultadoClassificacaoController.this.comboPercurso.getValue(), ResultadoClassificacaoController.this.filtroSexo);
                }
            };
            this.indicadorProgresso.visibleProperty().unbind();
            this.indicadorProgresso.visibleProperty().bind(tarefa.runningProperty());
            new Thread(tarefa).start();
        }
    }

    @FXML
    public void relatorioCategoria(ActionEvent actionEvent) {
        if (this.comboCategoria.getValue() != null) {
            String msgErro = cm.getMensagem("erro_rpt_classificacao_cat", this.comboCategoria.getValue().getCategoriaAtleta().getNome());
            Task<byte[]> tarefa = new TarefaThread<byte[]>("info_rpt_concluido", msgErro, super.cj) {
                @Override
                protected byte[] call() throws Exception {
                    if (ResultadoClassificacaoController.this.comboCategoria.getValue().getId() == null) {
                        ResultadoClassificacaoController.this.impressor.gerarRelatorioClassificacaoProvaTodasCategorias(ResultadoClassificacaoController.this.prova);
                        return null;
                    } else {
                        return ResultadoClassificacaoController.this.impressor.gerarRelatorioClassificacaoProvaCategoria(ResultadoClassificacaoController.this.comboCategoria.getValue());
                    }
                }
            };
            this.indicadorProgresso.visibleProperty().unbind();
            this.indicadorProgresso.visibleProperty().bind(tarefa.runningProperty());
            new Thread(tarefa).start();
        }
    }

    @Override
    public void setProva(Prova prova) {
        this.prova = prova;
    }
}
