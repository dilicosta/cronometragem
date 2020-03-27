package com.taurus.racingTiming.controller.corrida;

import com.taurus.exception.NegocioException;
import com.taurus.javafx.componente.TableViewDuploClique;
import com.taurus.javafx.controller.FXMLBaseController;
import com.taurus.javafx.converter.GenericComboBoxConverter;
import com.taurus.javafx.ds.GenericLinhaView;
import com.taurus.racingTiming.entidade.corrida.Prova;
import com.taurus.racingTiming.entidade.responsavel.OrganizacaoProva;
import com.taurus.racingTiming.negocio.ISecretario;
import com.taurus.racingTiming.pojo.SituacaoProva;
import com.taurus.racingTiming.ui.ds.corrida.SituacaoProvaDataSource;
import com.taurus.racingTiming.util.ListaConstantes.Stages;
import com.taurus.racingTiming.util.ListaConstantes.StatusProva;
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
public class SituacaoProvaPesquisaController extends FXMLBaseController {

    private static final Log LOG = LogFactory.getLog(SituacaoProvaPesquisaController.class);
    private static final int ITENS_POR_PAGINA = 15;

    @Autowired
    private ISituacaoProvaController situacaoProvaController;

    private SituacaoProvaDataSource dataSource;

    public SituacaoProvaPesquisaController() {
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
    private StatusProva filtroStatusProva;

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
    private ComboBox<StatusProva> comboStatus;
    @FXML
    private Pagination paginacao;
    @FXML
    private TableView<GenericLinhaView<SituacaoProva>> tabProva;
    @FXML
    private TableColumn<GenericLinhaView, String> colNome;
    @FXML
    private TableColumn<GenericLinhaView, String> colData;
    @FXML
    private TableColumn<GenericLinhaView, String> colOrganizacao;
    @FXML
    private TableColumn<GenericLinhaView, String> colStatus;
    @FXML
    private TableColumn<GenericLinhaView, String> colInscricoes;

    @Value("${fxml.statusProvaPesquisa.view}")
    @Override
    public void setFxmlFilePath(String filePath) {
        this.fxmlFilePath = filePath;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.paginacao.setPageFactory(this::paginar);

        this.comboOrganizacao.setConverter(new GenericComboBoxConverter<>("nome"));
        this.comboStatus.setConverter(new GenericComboBoxConverter<>("descricao"));

        this.comboStatus.getItems().add(0, null);
        this.comboStatus.getItems().addAll(StatusProva.values());

        colNome.setCellValueFactory(new PropertyValueFactory("coluna1"));
        colData.setCellValueFactory(new PropertyValueFactory("coluna2"));
        colOrganizacao.setCellValueFactory(new PropertyValueFactory("coluna3"));
        colStatus.setCellValueFactory(new PropertyValueFactory("coluna4"));
        colInscricoes.setCellValueFactory(new PropertyValueFactory("coluna5"));

        colNome.getStyleClass().add(ListaConstantesBase.EstiloCss.TAB_COL_ESQUERDO.getValor());
        colData.getStyleClass().add(ListaConstantesBase.EstiloCss.TAB_COL_CENTRO.getValor());
        colOrganizacao.getStyleClass().add(ListaConstantesBase.EstiloCss.TAB_COL_ESQUERDO.getValor());
        colStatus.getStyleClass().add(ListaConstantesBase.EstiloCss.TAB_COL_CENTRO.getValor());
        colInscricoes.getStyleClass().add(ListaConstantesBase.EstiloCss.TAB_COL_CENTRO.getValor());

        // Evento duplo clique na tabela
        // Abre a Janela de Prova para edicao
        tabProva.setRowFactory(new TableViewDuploClique<GenericLinhaView<SituacaoProva>>() {
            @Override
            public GenericLinhaView eventoDuploClique(GenericLinhaView<SituacaoProva> linhaView) {
                abrirSituacaoProva(linhaView); return linhaView;
            }
        });
    }

    @Override
    public void reinicializarJanelaEspecifico() {
        this.filtroNome = null;
        this.filtroDataInicio = null;
        this.filtroDataFim = null;
        this.filtroOrganizacaoProva = null;
        this.filtroStatusProva = null;
        this.dataSource = null;
        this.pagina = new Pagina(1, ITENS_POR_PAGINA, null);

    }

    @Override
    public void aoAbrirJanela() {
        this.comboOrganizacao.getItems().clear();
        this.comboOrganizacao.getItems().add(0, null);
        this.comboOrganizacao.getItems().addAll(this.listaSistema.getListaOrganizacaoProva());

        this.filtrar();
        this.txtNome.requestFocus();
    }

    @Override
    public void aoFecharJanelaEspecifico() {
    }

    private void abrirSituacaoProva(GenericLinhaView<SituacaoProva> linhaView) {
        SituacaoProva sp = linhaView.getBean();
        this.situacaoProvaController.setSituacaoProva(sp);
        super.cj.abrirJanela(Stages.STATUS_PROVA.ordinal(), Stages.STATUS_PROVA_PESQUISA.ordinal(), ISituacaoProvaController.class, "prova", true);
        this.dataSource.atualizar(linhaView, sp);
    }

    @FXML
    public void filtrar() {
        this.filtroNome = this.txtNome.getText();
        this.filtroDataInicio = FormatarUtil.stringToLocalDate(this.dtInicial.getEditor().getText());
        this.filtroDataFim = FormatarUtil.stringToLocalDate(this.dtFinal.getEditor().getText());
        this.filtroOrganizacaoProva = this.comboOrganizacao.getValue();
        this.filtroStatusProva = this.comboStatus.getValue();

        this.pagina = new Pagina(1, ITENS_POR_PAGINA, null);
        this.paginacao.getPageFactory().call(0);
    }

    private TableView paginar(int indexPagina) {
        try {
            this.pagina.setNumeroPagina(indexPagina + 1);
            List<Prova> listaProvas = this.secretario.pesquisarProva(this.filtroNome, this.filtroDataInicio, this.filtroDataFim, this.filtroOrganizacaoProva, this.filtroStatusProva, this.pagina);
            List<SituacaoProva> listaSituacaoProva = new ArrayList<>();
            for (Prova prova : listaProvas) {
                Long numeroInscricoes = this.secretario.pesquisarQuantidadeInscricoes(prova);
                listaSituacaoProva.add(new SituacaoProva(prova, numeroInscricoes));
            }
            this.dataSource = new SituacaoProvaDataSource(listaSituacaoProva);
            this.tabProva.setItems(dataSource.getData());
            this.paginacao.setPageCount(pagina.getNumeroPaginas());
        } catch (NegocioException ex) {
            this.cj.exibirErro(cm.getMensagem("erro_control_pesquisar", "prova"), GeralUtil.getMensagemOriginalErro(ex));
        }
        return this.tabProva;
    }
}
