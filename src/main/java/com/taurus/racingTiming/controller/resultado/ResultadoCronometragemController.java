package com.taurus.racingTiming.controller.resultado;

import com.taurus.exception.NegocioException;
import com.taurus.javafx.componente.BotaoCelulaTableValueFactory;
import com.taurus.javafx.componente.BotaoVariavelTableColumnFactory;
import com.taurus.javafx.componente.TableViewDuploClique;
import com.taurus.javafx.controller.FXMLBaseController;
import com.taurus.javafx.converter.GenericComboBoxConverter;
import com.taurus.javafx.ds.GenericLinhaView;
import com.taurus.javafx.util.JavafxUtil;
import com.taurus.racingTiming.controller.crono.ICronometragemEdicaoController;
import com.taurus.racingTiming.entidade.corrida.CategoriaDaProva;
import com.taurus.racingTiming.entidade.corrida.Cronometragem;
import com.taurus.racingTiming.entidade.corrida.Percurso;
import com.taurus.racingTiming.entidade.corrida.Prova;
import com.taurus.racingTiming.negocio.IRolex;
import com.taurus.racingTiming.negocio.ISecretario;
import com.taurus.racingTiming.ui.ds.corrida.CronometragemDataSource;
import com.taurus.racingTiming.util.ListaConstantes;
import com.taurus.racingTiming.util.ListaConstantes.StatusCronometragem;
import com.taurus.racingTiming.util.ListaConstantes.StatusProva;
import com.taurus.util.GeralUtil;
import com.taurus.util.ListaConstantesBase;
import com.taurus.util.ListaConstantesBase.Sexo;
import com.taurus.util.Pagina;
import com.taurus.util.annotation.Mascara;
import com.taurus.util.annotation.Parametro;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Pagination;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ResultadoCronometragemController extends FXMLBaseController implements IResultadoCronometragemController {

    private static final Log LOG = LogFactory.getLog(ResultadoCronometragemController.class);
    private static final int ITENS_POR_PAGINA = 18;

    public ResultadoCronometragemController() {
        super();
    }

    @Autowired
    private ICronometragemEdicaoController cronometragemEdicaoController;

    @Autowired
    private ISecretario secretario;

    @Autowired
    private IRolex rolex;

    @Parametro
    private Prova prova;

    private Pagina pagina = new Pagina(1, ITENS_POR_PAGINA, null);
    private CronometragemDataSource dataSource;
    private Filtro filtro = Filtro.PERCURSO;

    private Percurso filtroPercurso;
    private ListaConstantesBase.Sexo filtroSexo;
    private CategoriaDaProva filtroCategoria;
    private Integer filtroNumeroAtleta;
    private StatusCronometragem statusCrono;
    private LocalDateTime filtroHoraInicio;
    private LocalDateTime filtroHoraFim;

    @FXML
    private TextField txtNomeProva;
    @FXML
    private RadioButton radioStatusTodas;
    @FXML
    private RadioButton radioStatusAtiva;
    @FXML
    private RadioButton radioStatusExcluida;
    @FXML
    private RadioButton radioStatusDuvida;

    @FXML
    private ComboBox<Percurso> comboPercurso;
    @FXML
    private Button btnPesquisarPercurso;
    @FXML
    private RadioButton radioSexoTodos;
    @FXML
    private RadioButton radioMasculino;
    @FXML
    private RadioButton radioFeminino;
    @FXML
    private ComboBox<CategoriaDaProva> comboCategoria;
    @FXML
    private Button btnPesquisarCategoria;
    @FXML
    @Mascara(tipo = Mascara.TipoMascara.TEXT_FIELD_APENAS_NUMERO, tamanhoMax = "8")
    private TextField txtNumeroAtleta;
    @FXML
    private Button btnPesquisarOutro;
    @FXML
    @Mascara(tipo = Mascara.TipoMascara.TEXT_FIELD_CUSTOM, custom = "99:99:99")
    private TextField txtHoraInicio;
    @FXML
    @Mascara(tipo = Mascara.TipoMascara.TEXT_FIELD_CUSTOM, custom = "99:99:99")
    private TextField txtHoraFim;

    @FXML
    private Pagination paginacao;
    @FXML
    private TableView<GenericLinhaView<Cronometragem>> tabCrono;
    @FXML
    private TableColumn<GenericLinhaView, String> colNumero;
    @FXML
    private TableColumn<GenericLinhaView, String> colAtleta;
    @FXML
    private TableColumn<GenericLinhaView, String> colCategoria;
    @FXML
    private TableColumn<GenericLinhaView, String> colHora;
    @FXML
    private TableColumn<GenericLinhaView, String> colTempo;
    @FXML
    private TableColumn<GenericLinhaView, String> colVolta;
    @FXML
    private TableColumn colStatus;

    private enum Filtro {
        CATEGORIA, PERCURSO, OUTRO;
    }

    @Value("${fxml.resultadoCronometragem.view}")
    @Override
    public void setFxmlFilePath(String filePath) {
        this.fxmlFilePath = filePath;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.paginacao.setPageFactory(this::paginar);

        this.comboPercurso.setConverter(new GenericComboBoxConverter<>("nome"));
        this.comboCategoria.setConverter(new GenericComboBoxConverter<>("categoriaAtleta.nome"));

        this.btnPesquisarPercurso.getStyleClass().add(ListaConstantesBase.EstiloCss.BOTAO_PESQUISAR.getValor());
        this.btnPesquisarPercurso.setTooltip(new Tooltip("filtrar"));
        this.btnPesquisarCategoria.getStyleClass().add(ListaConstantesBase.EstiloCss.BOTAO_PESQUISAR.getValor());
        this.btnPesquisarCategoria.setTooltip(new Tooltip("filtrar"));
        this.btnPesquisarOutro.getStyleClass().add(ListaConstantesBase.EstiloCss.BOTAO_PESQUISAR.getValor());
        this.btnPesquisarOutro.setTooltip(new Tooltip("filtrar"));
        this.configurarTabelaColunas();
    }

    @Override
    public void reinicializarJanelaEspecifico() {
        this.filtro = Filtro.PERCURSO;
        this.filtroPercurso = null;
        this.filtroSexo = null;
        this.filtroCategoria = null;
        this.filtroNumeroAtleta = null;
        this.statusCrono = null;
        this.filtroHoraInicio = null;
        this.filtroHoraFim = null;

        this.comboPercurso.getItems().clear();
        this.comboCategoria.getItems().clear();

        this.dataSource = null;
    }

    @Override
    public void aoAbrirJanela() {
        this.radioStatusTodas.setSelected(true);
        this.comboPercurso.getItems().add(0, null);
        try {
            this.comboPercurso.getItems().addAll(this.secretario.pesquisarPercurso(this.prova));
        } catch (NegocioException ex) {
            this.cj.exibirErro(cm.getMensagem("erro_control_pesquisar", "percurso"), GeralUtil.getMensagemOriginalErro(ex));
        }
        this.comboCategoria.getItems().add(0, null);
        try {
            this.comboCategoria.getItems().addAll(this.secretario.pesquisarCategoriaDaProva(this.prova));
        } catch (NegocioException ex) {
            this.cj.exibirErro(cm.getMensagem("erro_control_pesquisar", "categoria_atleta"), GeralUtil.getMensagemOriginalErro(ex));
        }

        this.txtNomeProva.setText(this.prova.getNome());
        this.reiniciarPaginacao();
        this.comboPercurso.requestFocus();
    }

    @Override
    public void aoFecharJanelaEspecifico() {
    }

    @FXML
    public void filtrarPercurso() {
        this.filtro = Filtro.PERCURSO;
        this.filtroPercurso = this.comboPercurso.getValue();
        this.filtroSexo = this.radioSexoTodos.isSelected() ? null : this.radioMasculino.isSelected() ? Sexo.MASCULINO : this.radioFeminino.isSelected() ? Sexo.FEMININO : null;
        this.reiniciarPaginacao();
    }

    @FXML
    public void filtrarCategoria() {
        this.filtro = Filtro.CATEGORIA;
        this.filtroCategoria = this.comboCategoria.getValue();
        this.reiniciarPaginacao();
    }

    @FXML
    public void filtrarOutro() {
        this.filtro = Filtro.OUTRO;
        this.filtroNumeroAtleta = JavafxUtil.getInteger(this.txtNumeroAtleta);
        try {
            this.filtroHoraInicio = this.txtHoraInicio.getText().isEmpty() ? null : LocalDateTime.of(this.prova.getData(), LocalTime.parse(this.txtHoraInicio.getText(), DateTimeFormatter.ISO_LOCAL_TIME));
            this.filtroHoraFim = this.txtHoraFim.getText().isEmpty() ? null : LocalDateTime.of(this.prova.getData(), LocalTime.parse(this.txtHoraFim.getText(), DateTimeFormatter.ISO_LOCAL_TIME));
        } catch (DateTimeParseException ex) {
            super.cj.exibirAviso("aviso_hora_invalida");
            return;
        }
        this.reiniciarPaginacao();
    }

    private TableView paginar(int indexPagina) {
        try {
            this.pagina.setNumeroPagina(indexPagina + 1);
            List<Cronometragem> cronometragens = null;

            switch (this.filtro) {
                case PERCURSO:
                    cronometragens = this.rolex.pesquisarCronometragemPorPercursoSexoStatus(this.prova, this.filtroPercurso, this.filtroSexo, this.statusCrono, this.pagina);
                    break;
                case CATEGORIA:
                    cronometragens = this.rolex.pesquisarCronometragemPorCategoriaStatus(this.filtroCategoria, this.statusCrono, this.pagina);
                    break;
                case OUTRO:
                    cronometragens = this.rolex.pesquisarCronometragemPorNumeroAtletaPeriodoStatus(this.prova, this.filtroNumeroAtleta, this.filtroHoraInicio, this.filtroHoraFim, this.statusCrono, this.pagina);
                    break;
            }

            this.dataSource = new CronometragemDataSource(cronometragens);
            this.tabCrono.setItems(dataSource.getData());
            this.paginacao.setPageCount(pagina.getNumeroPaginas());
        } catch (NegocioException ex) {
            this.cj.exibirErro(cm.getMensagem("erro_control_pesquisar", "cronometragem"), GeralUtil.getMensagemOriginalErro(ex));
        }
        return this.tabCrono;
    }

    private void configurarTabelaColunas() {
        this.colNumero.setCellValueFactory(new PropertyValueFactory("coluna1"));
        this.colAtleta.setCellValueFactory(new PropertyValueFactory("coluna2"));
        this.colCategoria.setCellValueFactory(new PropertyValueFactory("coluna3"));
        this.colHora.setCellValueFactory(new PropertyValueFactory("coluna4"));
        this.colTempo.setCellValueFactory(new PropertyValueFactory("coluna5"));
        this.colVolta.setCellValueFactory(new PropertyValueFactory("coluna6"));

        this.colNumero.getStyleClass().add(ListaConstantesBase.EstiloCss.TAB_COL_CENTRO.getValor());
        this.colAtleta.getStyleClass().add(ListaConstantesBase.EstiloCss.TAB_COL_ESQUERDO.getValor());
        this.colCategoria.getStyleClass().add(ListaConstantesBase.EstiloCss.TAB_COL_ESQUERDO.getValor());
        this.colHora.getStyleClass().add(ListaConstantesBase.EstiloCss.TAB_COL_CENTRO.getValor());
        this.colTempo.getStyleClass().add(ListaConstantesBase.EstiloCss.TAB_COL_CENTRO.getValor());
        this.colVolta.getStyleClass().add(ListaConstantesBase.EstiloCss.TAB_COL_CENTRO.getValor());

        this.colStatus.getStyleClass().add(ListaConstantesBase.EstiloCss.TAB_COL_CENTRO.getValor());
        this.colStatus.setCellValueFactory(new BotaoCelulaTableValueFactory());
        this.colStatus.setCellFactory(new BotaoVariavelTableColumnFactory<GenericLinhaView<Cronometragem>>() {
            @Override
            public BotaoVariavelTableColumnFactory.DadosBotao getDadosBotao(GenericLinhaView<Cronometragem> linhaView) {
                if (linhaView.getBean().isExcluida()) {
                    return new BotaoVariavelTableColumnFactory.DadosBotao(ListaConstantesBase.EstiloCss.BOTAO_FALHA.getValor(), "Cronometragem desconsiderada");
                } else if (linhaView.getBean().isDuvida()) {
                    return new BotaoVariavelTableColumnFactory.DadosBotao(ListaConstantesBase.EstiloCss.BOTAO_DUVIDA.getValor(), "Cronometragem com marcação de dúvida");
                } else {
                    return new BotaoVariavelTableColumnFactory.DadosBotao(ListaConstantesBase.EstiloCss.BOTAO_OK.getValor(), "Cronometragem válida");
                }
            }

            @Override
            public void eventoBotao(ActionEvent actionEvent, GenericLinhaView<Cronometragem> linhaView) {
            }

            @Override
            protected boolean exibirItem(GenericLinhaView<Cronometragem> linhaView) {
                return true;
            }
        });

        // Evento duplo clique na tabela
        this.tabCrono.setRowFactory(new TableViewDuploClique<GenericLinhaView<Cronometragem>>() {
            @Override
            public GenericLinhaView eventoDuploClique(GenericLinhaView<Cronometragem> linhaView) {
                if (ResultadoCronometragemController.this.prova.getStatus() == StatusProva.ENCERRADA_APURANDO_RESULTADOS) {
                    ResultadoCronometragemController.this.cronometragemEdicaoController.setOperacao(ListaConstantesBase.Operacao.EDITAR);
                    ResultadoCronometragemController.this.cronometragemEdicaoController.setCronometragemEdicao(linhaView.getBean());
                    ResultadoCronometragemController.super.cj.abrirJanela(ListaConstantes.Stages.CRONOMETRAGEM_EDICAO.ordinal(), ListaConstantes.Stages.RESULTADO_CRONOMETRAGEM.ordinal(), ICronometragemEdicaoController.class, "cronometragem", true);
                    ResultadoCronometragemController.this.dataSource.atualizar(linhaView, ResultadoCronometragemController.this.cronometragemEdicaoController.getCronometragemEdicao());
                    ResultadoCronometragemController.this.cronometragemEdicaoController.setCronometragemEdicao(null);
                }
                return linhaView;
            }
        });
    }

    private void reiniciarPaginacao() {
        this.statusCrono = this.radioStatusTodas.isSelected() ? null : this.radioStatusAtiva.isSelected() ? StatusCronometragem.ATIVA : this.radioStatusExcluida.isSelected() ? StatusCronometragem.EXCLUIDA : StatusCronometragem.DUVIDA;
        this.pagina.setTotalItens(null);
        this.paginacao.setCurrentPageIndex(0);
        this.paginacao.getPageFactory().call(0);

    }

    @Override
    public void setProva(Prova prova) {
        this.prova = prova;
    }
}
