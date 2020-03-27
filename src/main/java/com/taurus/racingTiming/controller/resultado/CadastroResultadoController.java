package com.taurus.racingTiming.controller.resultado;

import com.taurus.exception.NegocioException;
import com.taurus.javafx.componente.BotaoCelulaTableValueFactory;
import com.taurus.javafx.componente.BotaoCondicionalTableColumnFactory;
import com.taurus.javafx.componente.BotaoTableColumnFactory;
import com.taurus.javafx.controller.FXMLBaseController;
import com.taurus.javafx.converter.GenericComboBoxConverter;
import com.taurus.javafx.ds.GenericLinhaView;
import com.taurus.racingTiming.entidade.corrida.Prova;
import com.taurus.racingTiming.entidade.responsavel.OrganizacaoProva;
import com.taurus.racingTiming.negocio.ISecretario;
import com.taurus.racingTiming.ui.ds.corrida.ProvaDataSource;
import com.taurus.racingTiming.util.ListaConstantes;
import com.taurus.racingTiming.util.ListaSistema;
import com.taurus.util.FormatarUtil;
import com.taurus.util.GeralUtil;
import com.taurus.util.ListaConstantesBase;
import com.taurus.util.Pagina;
import com.taurus.util.annotation.Mascara;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Pagination;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class CadastroResultadoController extends FXMLBaseController {

    private static final Log LOG = LogFactory.getLog(CadastroResultadoController.class);
    private static final int ITENS_POR_PAGINA = 15;

    @Autowired
    private IResultadoCronometragemController resultadoCronometragemController;
    @Autowired
    private IResultadoClassificacaoController resultadoClassificacaoController;

    private ProvaDataSource dataSource;

    public CadastroResultadoController() {
        super();
    }

    @Autowired
    private ListaSistema listaSistema;
    @Autowired
    private ISecretario secretario;

    private Pagina pagina = null;

    private String filtroNome;
    private LocalDate filtroDataInicio;
    private LocalDate filtroDataFim;
    private OrganizacaoProva filtroOrganizacaoProva;

    @FXML
    @Mascara(tipo = Mascara.TipoMascara.TEXT_FIELD_MAXIMO_CARACTER, tamanhoMax = "30")
    private TextField txtNome;
    @FXML
    @Mascara(tipo = Mascara.TipoMascara.DATE_PICKER)
    private DatePicker dtInicial;
    @FXML
    @Mascara(tipo = Mascara.TipoMascara.DATE_PICKER)
    private DatePicker dtFinal;
    @FXML
    private ComboBox<OrganizacaoProva> comboOrganizacao;
    @FXML
    private Pagination paginacao;
    @FXML
    private TableView<GenericLinhaView<Prova>> tabProva;
    @FXML
    private TableColumn<GenericLinhaView, String> colNome;
    @FXML
    private TableColumn<GenericLinhaView, String> colData;
    @FXML
    private TableColumn<GenericLinhaView, String> colOrganizacao;
    @FXML
    private TableColumn<GenericLinhaView, String> colSituacao;
    @FXML
    private TableColumn colCronometragem;
    @FXML
    private TableColumn colClassificacao;
    @FXML
    private TableColumn colRelatorio;

    @Value("${fxml.cadastroResultado.view}")
    @Override
    public void setFxmlFilePath(String filePath) {
        this.fxmlFilePath = filePath;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.paginacao.setPageFactory(this::paginar);

        this.comboOrganizacao.setConverter(new GenericComboBoxConverter<>("nome"));

        this.configurarTabelaColunas();
    }

    @Override
    public void reinicializarJanelaEspecifico() {
        this.filtroNome = null;
        this.filtroDataInicio = null;
        this.filtroDataFim = null;
        this.filtroOrganizacaoProva = null;
        this.dataSource = null;
    }

    @Override
    public void aoAbrirJanela() {
        this.pagina = new Pagina(1, ITENS_POR_PAGINA, null);

        this.comboOrganizacao.getItems().clear();
        this.comboOrganizacao.getItems().add(0, null);
        this.comboOrganizacao.getItems().addAll(this.listaSistema.getListaOrganizacaoProva());

        this.filtrar();
        this.txtNome.requestFocus();
    }

    @Override
    public void aoFecharJanelaEspecifico() {
    }

    @FXML
    public void filtrar() {
        this.filtroNome = this.txtNome.getText();
        this.filtroDataInicio = FormatarUtil.stringToLocalDate(this.dtInicial.getEditor().getText());
        this.filtroDataFim = FormatarUtil.stringToLocalDate(this.dtFinal.getEditor().getText());
        this.filtroOrganizacaoProva = this.comboOrganizacao.getValue();

        this.pagina = new Pagina(1, ITENS_POR_PAGINA, null);
        this.paginacao.getPageFactory().call(0);
    }

    private TableView paginar(int indexPagina) {
        try {
            this.pagina.setNumeroPagina(indexPagina + 1);
            List<ListaConstantes.StatusProva> listaStatusProva = new ArrayList();
            listaStatusProva.add(ListaConstantes.StatusProva.ENCERRADA_APURANDO_RESULTADOS);
            listaStatusProva.add(ListaConstantes.StatusProva.ENCERRADA_RESULTADOS_CONCLUIDOS);
            listaStatusProva.add(ListaConstantes.StatusProva.FINALIZADA);
            List<Prova> lista = this.secretario.pesquisarProva(this.filtroNome, this.filtroDataInicio, this.filtroDataFim, this.filtroOrganizacaoProva, listaStatusProva, this.pagina);
            this.dataSource = new ProvaDataSource(lista);
            this.tabProva.setItems(dataSource.getData());
            this.paginacao.setPageCount(pagina.getNumeroPaginas());
        } catch (NegocioException ex) {
            this.cj.exibirErro(cm.getMensagem("erro_control_pesquisar", "prova"), GeralUtil.getMensagemOriginalErro(ex));
        }
        return this.tabProva;
    }

    private void configurarTabelaColunas() {
        colNome.setCellValueFactory(new PropertyValueFactory("coluna1"));
        colData.setCellValueFactory(new PropertyValueFactory("coluna2"));
        colOrganizacao.setCellValueFactory(new PropertyValueFactory("coluna4"));
        colSituacao.setCellValueFactory(new PropertyValueFactory("coluna5"));

        colNome.getStyleClass().add(ListaConstantesBase.EstiloCss.TAB_COL_ESQUERDO.getValor());
        colData.getStyleClass().add(ListaConstantesBase.EstiloCss.TAB_COL_CENTRO.getValor());
        colOrganizacao.getStyleClass().add(ListaConstantesBase.EstiloCss.TAB_COL_ESQUERDO.getValor());
        colSituacao.getStyleClass().add(ListaConstantesBase.EstiloCss.TAB_COL_CENTRO.getValor());

        colCronometragem.getStyleClass().add(ListaConstantesBase.EstiloCss.TAB_COL_CENTRO.getValor());
        colCronometragem.setCellValueFactory(new BotaoCelulaTableValueFactory());
        colCronometragem.setCellFactory(new BotaoTableColumnFactory<GenericLinhaView<Prova>>("abrir cronometragens", ListaConstantes.EstiloCss.BOTAO_CRONOMETRO.getValor()) {
            @Override
            public void eventoBotao(ActionEvent actionEvent, GenericLinhaView<Prova> linhaView) {
                CadastroResultadoController.this.resultadoCronometragemController.setProva(linhaView.getBean());
                CadastroResultadoController.super.cj.abrirJanela(ListaConstantes.Stages.RESULTADO_CRONOMETRAGEM.ordinal(), ListaConstantes.Stages.CADASTRO_RESULTADO.ordinal(), IResultadoCronometragemController.class, "t_result_crono", true);
            }
        });

        colClassificacao.getStyleClass().add(ListaConstantesBase.EstiloCss.TAB_COL_CENTRO.getValor());
        colClassificacao.setCellValueFactory(new BotaoCelulaTableValueFactory());
        colClassificacao.setCellFactory(new BotaoCondicionalTableColumnFactory<GenericLinhaView<Prova>>("abrir classificação", ListaConstantes.EstiloCss.BOTAO_CLASSIFICACAO.getValor()) {
            @Override
            public void eventoBotao(ActionEvent actionEvent, GenericLinhaView<Prova> linhaView) {
                CadastroResultadoController.this.resultadoClassificacaoController.setProva(linhaView.getBean());
                CadastroResultadoController.super.cj.abrirJanelaComRedimensionamento(ListaConstantes.Stages.RESULTADO_CLASSIFICACAO.ordinal(), ListaConstantes.Stages.CADASTRO_RESULTADO.ordinal(), IResultadoClassificacaoController.class, "t_result_class", true);
            }

            @Override
            protected boolean exibirItem(GenericLinhaView<Prova> linhaView) {
                return linhaView.getBean().getStatus() == ListaConstantes.StatusProva.ENCERRADA_RESULTADOS_CONCLUIDOS
                        || linhaView.getBean().getStatus() == ListaConstantes.StatusProva.FINALIZADA;
            }
        });
    }
}
