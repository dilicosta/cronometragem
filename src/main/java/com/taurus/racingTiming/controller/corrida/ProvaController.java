package com.taurus.racingTiming.controller.corrida;

import com.taurus.exception.NegocioException;
import com.taurus.javafx.componente.BotaoCelulaTableValueFactory;
import com.taurus.javafx.componente.BotaoTableColumnFactory;
import com.taurus.javafx.componente.TableViewDuploClique;
import com.taurus.javafx.controller.FXMLMultiCenasBaseController;
import com.taurus.javafx.converter.EstadoBrasilConverter;
import com.taurus.javafx.converter.GenericComboBoxConverter;
import com.taurus.javafx.ds.GenericLinhaView;
import com.taurus.javafx.util.ValidarCampoUtil;
import com.taurus.racingTiming.controller.atleta.ICategoriaAtletaController;
import com.taurus.racingTiming.entidade.Endereco;
import com.taurus.racingTiming.entidade.atleta.CategoriaAtleta;
import com.taurus.racingTiming.entidade.corrida.CategoriaDaProva;
import com.taurus.racingTiming.entidade.corrida.Largada;
import com.taurus.racingTiming.entidade.corrida.Percurso;
import com.taurus.racingTiming.entidade.corrida.Prova;
import com.taurus.racingTiming.entidade.responsavel.Federacao;
import com.taurus.racingTiming.entidade.responsavel.OrganizacaoProva;
import com.taurus.racingTiming.entidade.responsavel.RepresentanteFederacao;
import com.taurus.racingTiming.entidade.responsavel.RepresentanteOrganizacaoProva;
import com.taurus.racingTiming.negocio.IEstagiario;
import com.taurus.racingTiming.negocio.ISecretario;
import com.taurus.racingTiming.ui.ds.corrida.CategoriaProvaDataSource;
import com.taurus.racingTiming.ui.ds.corrida.LargadaDataSource;
import com.taurus.racingTiming.ui.ds.corrida.PercursoDataSource;
import com.taurus.racingTiming.ui.ds.responsavel.RepresentanteFederacaoDataSource;
import com.taurus.racingTiming.ui.ds.responsavel.RepresentanteOrganizacaoProvaDataSource;
import com.taurus.racingTiming.util.ListaConstantes;
import com.taurus.racingTiming.util.ListaConstantes.GrauDificuldade;
import com.taurus.racingTiming.util.ListaConstantes.StatusProva;
import com.taurus.racingTiming.util.ListaSistema;
import com.taurus.util.FormatarUtil;
import com.taurus.util.GeralUtil;
import com.taurus.util.ListaConstantesBase;
import com.taurus.util.ListaConstantesBase.EstadoBrasil;
import com.taurus.util.ListaConstantesBase.Operacao;
import com.taurus.util.annotation.Mascara;
import com.taurus.util.annotation.Parametro;
import com.taurus.util.annotation.ValidarCampo;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.validator.GenericValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ProvaController extends FXMLMultiCenasBaseController implements IProvaController {

    private static final Log LOG = LogFactory.getLog(ProvaController.class);

    @Autowired
    private ISecretario secretario;
    @Autowired
    private IEstagiario estagiario;
    @Autowired
    private ICategoriaAtletaController categoriaAtletaController;
    @Autowired
    private ListaSistema listaSistema;

    @Parametro
    private Operacao operacao;
    private Prova provaEdicao;

    private GenericLinhaView<Percurso> linhaViewPercursoEdicao = null;
    private GenericLinhaView<Largada> linhaViewLargadaEdicao = null;
    private GenericLinhaView<CategoriaDaProva> linhaViewCategoriaEdicao = null;

    private RepresentanteOrganizacaoProvaDataSource representanteProvaDS;
    private List<RepresentanteOrganizacaoProva> listaRepresentanteProvaParaExcluir = new ArrayList<>();
    private PercursoDataSource percursoDS;
    private List<Percurso> listaPercursoParaExcluir = new ArrayList<>();
    private LargadaDataSource largadaDS;
    private List<Largada> listaLargadaParaExcluir = new ArrayList<>();
    private CategoriaProvaDataSource categoriaDS;
    private List<CategoriaDaProva> listaCategoriaParaExcluir = new ArrayList<>();
    private RepresentanteFederacaoDataSource representanteFederacaoDS;
    private List<RepresentanteFederacao> listaRepresentanteFederacaoParaExcluir = new ArrayList<>();

    /**
     * ************************ Informacoes Basicas ***************************
     */
    @FXML
    @ValidarCampo
    @Mascara(tipo = Mascara.TipoMascara.TEXT_FIELD_MAXIMO_CARACTER, tamanhoMax = "50")
    private TextField txtNomeProva;
    @FXML
    @Mascara(tipo = Mascara.TipoMascara.DATE_PICKER)
    @ValidarCampo
    private DatePicker dpData;
    @FXML
    private ComboBox<OrganizacaoProva> comboOrganizacaoProva;
    @FXML
    private ComboBox<RepresentanteOrganizacaoProva> comboRepresentanteProva;
    @FXML
    private TableView<GenericLinhaView<RepresentanteOrganizacaoProva>> tabOrganizacao;
    @FXML
    private TableColumn<GenericLinhaView, String> colOrganizacaoProva;
    @FXML
    private TableColumn<GenericLinhaView, String> colRepresentanteProva;
    @FXML
    private TableColumn colExcluirRepresentanteOrganizacao;

    /**
     * ************************ Informacoes Endereco **************************
     */
    @FXML
    @Mascara(tipo = Mascara.TipoMascara.TEXT_FIELD_MAXIMO_CARACTER, tamanhoMax = "50")
    private TextField txtLogradouro;
    @FXML
    @Mascara(tipo = Mascara.TipoMascara.TEXT_FIELD_APENAS_NUMERO, tamanhoMax = "6")
    private TextField txtNumero;
    @FXML
    @Mascara(tipo = Mascara.TipoMascara.TEXT_FIELD_MAXIMO_CARACTER, tamanhoMax = "20")
    private TextField txtComplemento;
    @FXML
    @Mascara(tipo = Mascara.TipoMascara.TEXT_FIELD_MAXIMO_CARACTER, tamanhoMax = "40")
    private TextField txtBairro;
    @FXML
    @Mascara(tipo = Mascara.TipoMascara.TEXT_FIELD_MAXIMO_CARACTER, tamanhoMax = "40")
    @ValidarCampo
    private TextField txtCidade;
    @FXML
    private ComboBox<ListaConstantesBase.EstadoBrasil> comboUf;
    @FXML
    @Mascara(tipo = Mascara.TipoMascara.TEXT_FIELD_CEP)
    private TextField txtCep;

    /**
     * ************************ Informacoes Percurso **************************
     */
    @FXML
    @ValidarCampo
    @Mascara(tipo = Mascara.TipoMascara.TEXT_FIELD_MAXIMO_CARACTER, tamanhoMax = "40")
    private TextField txtNomePercurso;
    @FXML
    @ValidarCampo
    @Mascara(tipo = Mascara.TipoMascara.TEXT_FIELD_VALOR, tamanhoMax = "6")
    private TextField txtDistancia;
    @FXML
    @ValidarCampo
    @Mascara(tipo = Mascara.TipoMascara.TEXT_FIELD_APENAS_NUMERO, tamanhoMax = "3")
    private TextField txtNumeroVolta;
    @FXML
    @ValidarCampo
    private ComboBox<GrauDificuldade> comboGrauDificuldade;
    @FXML
    private Button btnAddPercurso;
    @FXML
    private TableView<GenericLinhaView<Percurso>> tabPercurso;
    @FXML
    private TableColumn<GenericLinhaView, String> colNomePercurso;
    @FXML
    private TableColumn<GenericLinhaView, String> colKm;
    @FXML
    private TableColumn<GenericLinhaView, String> colVolta;
    @FXML
    private TableColumn<GenericLinhaView, String> colDificuldade;
    @FXML
    private TableColumn colExcluirPercurso;
    @FXML
    private Button btnCancelarEditarPercurso;

    /**
     * ************************ Informacoes Largada **************************
     */
    @FXML
    @Mascara(tipo = Mascara.TipoMascara.TEXT_FIELD_MAXIMO_CARACTER, tamanhoMax = "40")
    private TextField txtNomeLargada;
    @FXML
    @Mascara(tipo = Mascara.TipoMascara.TEXT_FIELD_CUSTOM, custom = "99:99")
    private TextField txtHoraPrevista;
    @FXML
    private Button btnAddLargada;
    @FXML
    private TableView<GenericLinhaView<Largada>> tabLargada;
    @FXML
    private TableColumn<GenericLinhaView, String> colNomeLargada;
    @FXML
    private TableColumn<GenericLinhaView, String> colHoraPrevista;
    @FXML
    private TableColumn colExcluirLargada;
    @FXML
    private Button btnCancelarEditarLargada;

    /**
     * ********************** Informacoes Categorias **************************
     */
    @FXML
    private Button btnNovoCategoriaAtleta;
    @FXML
    private ComboBox<CategoriaAtleta> comboCategoria;
    @FXML
    private ComboBox<Percurso> comboPercurso;
    @FXML
    private ComboBox<Largada> comboLargada;
    @FXML
    private ComboBox<ListaConstantes.NumeracaoAutomatica> comboNumeracaoAutomatica;
    @FXML
    private Label lblDigitoNumeracao;
    @FXML
    @Mascara(tipo = Mascara.TipoMascara.TEXT_FIELD_APENAS_NUMERO, tamanhoMax = "1")
    private TextField txtDigitoNumeracao;
    @FXML
    private Label lblInicioNumeracao;
    @FXML
    @Mascara(tipo = Mascara.TipoMascara.TEXT_FIELD_APENAS_NUMERO)
    private TextField txtInicioNumeracao;
    @FXML
    private Label lblFimNumeracao;
    @FXML
    @Mascara(tipo = Mascara.TipoMascara.TEXT_FIELD_APENAS_NUMERO)
    private TextField txtFimNumeracao;
    @FXML
    private Button btnAddCategoria;
    @FXML
    private TableView<GenericLinhaView<CategoriaDaProva>> tabCategoria;
    @FXML
    private TableColumn<GenericLinhaView, String> colCategoria;
    @FXML
    private TableColumn<GenericLinhaView, String> colPercurso;
    @FXML
    private TableColumn<GenericLinhaView, String> colLargada;
    @FXML
    private TableColumn colExcluirCategoria;
    @FXML
    private Button btnCancelarEditarCategoria;

    /**
     * ********************** Informacoes Federacao **************************
     */
    @FXML
    private ComboBox<Federacao> comboFederacao;
    @FXML
    private ComboBox<RepresentanteFederacao> comboRepresentanteFederacao;
    @FXML
    private TableView<GenericLinhaView<RepresentanteFederacao>> tabFederacao;
    @FXML
    private TableColumn<GenericLinhaView, String> colFederacao;
    @FXML
    private TableColumn<GenericLinhaView, String> colRepresentanteFederacao;
    @FXML
    private TableColumn colExcluirRepresentanteFederacao;

    @Value("${fxml.prova.view}")
    @Override
    public void setFxmlFilePath(String filePath) {
        this.fxmlFilePath = filePath;
    }

    @Value("${fxml.prova.view.cenas}")
    @Override
    public void setFxmlFilePathCenas(String fxmlFilePathCenas) {
        super.fxmlFilePathCenas = fxmlFilePathCenas;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        switch (super.getIndiceCena()) {
            case 0:
                this.comboOrganizacaoProva.setConverter(new GenericComboBoxConverter<>("nome"));
                this.comboOrganizacaoProva.valueProperty().addListener((ObservableValue<? extends OrganizacaoProva> observable, OrganizacaoProva oldValue, OrganizacaoProva newValue) -> {
                    if (newValue != oldValue) {
                        ProvaController.this.carregarComboRepresentanteProva();
                    }
                });
                this.comboRepresentanteProva.setConverter(new GenericComboBoxConverter("nome"));
                this.comboUf.setConverter(new EstadoBrasilConverter());
                this.configurarTabelaOrganizacao();

                break;
            case 1:
                this.comboGrauDificuldade.setConverter(new GenericComboBoxConverter<>("descricao"));
                this.btnCancelarEditarPercurso.getStyleClass().add(ListaConstantesBase.EstiloCss.BOTAO_CANCELAR_EDITAR.getValor());
                this.btnCancelarEditarPercurso.setTooltip(new Tooltip("cancelar edição de percurso"));
                this.btnCancelarEditarPercurso.setVisible(false);
                this.btnCancelarEditarLargada.getStyleClass().add(ListaConstantesBase.EstiloCss.BOTAO_CANCELAR_EDITAR.getValor());
                this.btnCancelarEditarLargada.setTooltip(new Tooltip("cancelar edição de largada"));
                this.btnCancelarEditarLargada.setVisible(false);
                this.configurarTabelaPercurso();
                this.configurarTabelaLargada();
                break;
            case 2:
                this.comboCategoria.setConverter(new GenericComboBoxConverter<>("nome"));
                this.comboPercurso.setConverter(new GenericComboBoxConverter<>("nome"));
                this.comboLargada.setConverter(new GenericComboBoxConverter<>("nome"));

                this.comboNumeracaoAutomatica.setConverter(new GenericComboBoxConverter<>("descricao"));
                this.comboNumeracaoAutomatica.valueProperty().addListener((ObservableValue<? extends ListaConstantes.NumeracaoAutomatica> observable, ListaConstantes.NumeracaoAutomatica oldValue, ListaConstantes.NumeracaoAutomatica newValue) -> {
                    if (newValue != oldValue) {
                        ProvaController.this.atualizarVisualizacaoNumeracaoAutomatica();
                    }
                });

                this.btnNovoCategoriaAtleta.getStyleClass().add(ListaConstantesBase.EstiloCss.BOTAO_ADICIONAR.getValor());
                this.btnNovoCategoriaAtleta.setTooltip(new Tooltip("nova categoria"));

                this.btnCancelarEditarCategoria.getStyleClass().add(ListaConstantesBase.EstiloCss.BOTAO_CANCELAR_EDITAR.getValor());
                this.btnCancelarEditarCategoria.setTooltip(new Tooltip("cancelar edição de categoria"));
                this.btnCancelarEditarCategoria.setVisible(false);

                this.comboFederacao.setConverter(new GenericComboBoxConverter<>("nome"));
                this.comboFederacao.valueProperty().addListener((ObservableValue<? extends Federacao> observable, Federacao oldValue, Federacao newValue) -> {
                    if (newValue != oldValue) {
                        ProvaController.this.carregarComboRepresentanteFederacao();
                    }
                });

                this.comboRepresentanteFederacao.setConverter(new GenericComboBoxConverter("nome"));

                this.configurarTabelaCategoria();
                this.configurarTabelaRepresentanteFederacao();
                break;
        }
    }

    @Override
    public void reinicializarJanelaMultiCenas() {
        this.representanteProvaDS = null;
        this.listaRepresentanteProvaParaExcluir.clear();
        this.percursoDS = null;
        this.listaPercursoParaExcluir.clear();
        this.largadaDS = null;
        this.listaLargadaParaExcluir.clear();
        this.categoriaDS = null;
        this.listaCategoriaParaExcluir.clear();
        this.representanteFederacaoDS = null;
        this.listaRepresentanteFederacaoParaExcluir.clear();
        this.comboUf.setValue(EstadoBrasil.MINAS_GERAIS);
        this.btnCancelarEditarPercurso.setVisible(false);
        this.btnCancelarEditarLargada.setVisible(false);
        this.btnCancelarEditarCategoria.setVisible(false);
    }

    @Override
    public void aoAbrirJanela() {
        this.comboUf.getItems().addAll(ListaConstantesBase.EstadoBrasil.values());
        this.comboUf.setValue(ListaConstantesBase.EstadoBrasil.MINAS_GERAIS);
        this.comboOrganizacaoProva.getItems().addAll(this.listaSistema.getListaOrganizacaoProva());
        this.comboFederacao.getItems().addAll(this.listaSistema.getListaFederacao());
        this.comboGrauDificuldade.getItems().addAll(GrauDificuldade.values());
        this.comboNumeracaoAutomatica.getItems().addAll(ListaConstantes.NumeracaoAutomatica.values());

        this.carregarCategoriaAtleta();

        if (operacao == Operacao.EDITAR) {
            this.exibirProva();
        }
    }

    @Override
    public void aoAbrirCena(int indiceCena) {
        switch (indiceCena) {
            case 0:
                this.txtNomeProva.requestFocus();
                break;
            case 1:
                this.txtNomePercurso.requestFocus();
                break;
            case 2:
                this.comboPercurso.getItems().clear();
                this.comboPercurso.getItems().addAll(this.percursoDS.getListaBean());
                this.comboLargada.getItems().clear();
                this.comboLargada.getItems().addAll(this.largadaDS.getListaBean());
                this.comboCategoria.requestFocus();
                break;
            default:
                break;
        }
    }

    @Override
    public void aoFecharJanelaEspecifico() {
    }

    @Override
    public boolean validarAvancar() {

        switch (super.getIndiceCena()) {
            case 0:
                LocalDate dataProva = FormatarUtil.stringToLocalDate(this.dpData.getEditor().getText());
                if (!ValidarCampoUtil.validarCampos(this.txtNomeProva, this.dpData, this.txtCidade)) {
                    return false;
                } else if (this.representanteProvaDS == null || this.representanteProvaDS.getData().isEmpty()) {
                    super.cj.exibirAviso(super.cm.getMensagem("aviso_campo_adicionar", "representante_organizacao_prova"));
                    return false;
                } else if (dataProva == null || dataProva.isBefore(LocalDate.now())) {
                    super.cj.exibirAviso("aviso_data_prova_invalida");
                    return false;
                }
                break;
            case 1:
                if (this.percursoDS == null || this.percursoDS.getData().isEmpty()) {
                    super.cj.exibirAviso(super.cm.getMensagem("aviso_campo_adicionar", "percurso"));
                    return false;
                } else if (this.largadaDS == null || this.largadaDS.getData().isEmpty()) {
                    super.cj.exibirAviso(super.cm.getMensagem("aviso_campo_adicionar", "largada"));
                    return false;
                }
                break;
        }
        return true;
    }

    @Override
    public boolean validarVoltar() {
        return true;
    }

    @FXML
    public void adicionarRepresentanteProva(ActionEvent actionEvent) {
        if (this.comboRepresentanteProva != null) {
            if (this.representanteProvaDS == null) {
                this.representanteProvaDS = new RepresentanteOrganizacaoProvaDataSource(this.comboRepresentanteProva.getValue());
                this.tabOrganizacao.setItems(this.representanteProvaDS.getData());
            } else {
                if (!this.representanteProvaDS.contem(this.comboRepresentanteProva.getValue())) {
                    this.representanteProvaDS.adicionar(this.comboRepresentanteProva.getValue());
                }
            }
        }
    }

    @FXML
    public void adicionarPercurso(ActionEvent actionEvent) {
        boolean atualizar = this.linhaViewPercursoEdicao != null;
        if (ValidarCampoUtil.validarCampos(this.txtNomePercurso, this.txtDistancia, this.txtNumeroVolta, this.comboGrauDificuldade)) {
            Percurso percurso = new Percurso();
            percurso.setNome(this.txtNomePercurso.getText());
            percurso.setDistancia(FormatarUtil.getNumero(this.txtDistancia.getText()));
            percurso.setNumeroVolta(Integer.valueOf(this.txtNumeroVolta.getText()));
            percurso.setGrauDificuldade(this.comboGrauDificuldade.getValue());

            if (this.percursoDS == null) {
                this.percursoDS = new PercursoDataSource(percurso);
                this.tabPercurso.setItems(this.percursoDS.getData());
            } else {
                if (atualizar) {
                    Percurso percursoAntigo = this.percursoDS.getBean(this.linhaViewPercursoEdicao);
                    percurso.setId(percursoAntigo.getId());
                    this.percursoDS.atualizar(this.linhaViewPercursoEdicao, percurso);
                    this.linhaViewPercursoEdicao = null;
                    this.btnAddPercurso.setText("adicionar");
                    this.atualizarCategoriaPercurso(percurso, percursoAntigo);
                    this.btnCancelarEditarPercurso.setVisible(false);
                } else {
                    if (!this.percursoDS.contem(percurso)) {
                        this.percursoDS.adicionar(percurso);
                    }
                }
            }
            this.limparCamposPercurso();
        }
    }

    @FXML
    public void cancelarEditarPercurso(ActionEvent actionEvent) {
        this.linhaViewPercursoEdicao = null;
        this.btnAddPercurso.setText("adicionar");
        this.limparCamposPercurso();
        this.btnCancelarEditarPercurso.setVisible(false);
    }

    @FXML
    public void adicionarLargada(ActionEvent actionEvent) {
        boolean atualizar = this.linhaViewLargadaEdicao != null;

        if (ValidarCampoUtil.validarCampos(this.txtNomeLargada, this.txtHoraPrevista)) {
            Largada largada = new Largada();
            largada.setNome(this.txtNomeLargada.getText());
            LocalDateTime horaPrevista = FormatarUtil.stringToLocalDateTime(this.dpData.getEditor().getText() + " " + this.txtHoraPrevista.getText(), FormatarUtil.FORMATO_DATA_HORA);
            if (horaPrevista == null) {
                super.cj.exibirErro("Horário previsto", "Formato de hora inválido!");
                this.txtHoraPrevista.requestFocus();
                return;
            }
            largada.setHoraPrevista(horaPrevista);
            if (this.largadaDS == null) {
                this.largadaDS = new LargadaDataSource(largada);
                this.tabLargada.setItems(this.largadaDS.getData());
            } else {
                if (atualizar) {
                    Largada largadaAntiga = this.largadaDS.getBean(this.linhaViewLargadaEdicao);
                    largada.setId(largadaAntiga.getId());
                    this.largadaDS.atualizar(this.linhaViewLargadaEdicao, largada);
                    this.linhaViewLargadaEdicao = null;
                    this.btnAddLargada.setText("adicionar");
                    this.atualizarCategoriaLargada(largada, largadaAntiga);
                    this.btnCancelarEditarLargada.setVisible(false);
                } else {
                    if (!this.largadaDS.contem(largada)) {
                        this.largadaDS.adicionar(largada);
                    }
                }
            }
            this.limparCamposLargada();
        }
    }

    @FXML
    public void cancelarEditarLargada(ActionEvent actionEvent) {
        this.linhaViewLargadaEdicao = null;
        this.btnAddLargada.setText("adicionar");
        this.limparCamposLargada();
        this.btnCancelarEditarLargada.setVisible(false);
    }

    @FXML
    public void adicionarCategoria(ActionEvent actionEvent) {
        boolean atualizar = this.linhaViewCategoriaEdicao != null;

        if (ValidarCampoUtil.validarCampos(this.comboCategoria, this.comboPercurso, this.comboLargada)) {
            CategoriaDaProva catProva = atualizar ? this.categoriaDS.getBean(this.linhaViewCategoriaEdicao) : new CategoriaDaProva();
            CategoriaAtleta categoriaAtleta = this.comboCategoria.getValue();
            if (!atualizar && this.categoriaExistenteNaProva(categoriaAtleta)) {
                super.cj.exibirAviso("aviso_categoria_exitente_prova");
            } else {
                catProva.setCategoriaAtleta(this.comboCategoria.getValue());
                catProva.setPercurso(this.comboPercurso.getValue());
                catProva.setLargada(this.comboLargada.getValue());
                catProva.setNumeracaoAutomatica(this.comboNumeracaoAutomatica.getValue());
                if (!GenericValidator.isBlankOrNull(this.txtDigitoNumeracao.getText())) {
                    catProva.setDigitosNumeracao(Integer.valueOf(this.txtDigitoNumeracao.getText()));
                }
                if (this.comboNumeracaoAutomatica.getValue() == ListaConstantes.NumeracaoAutomatica.POR_FAIXA) {
                    if (ValidarCampoUtil.validarCampos(this.txtInicioNumeracao, this.txtFimNumeracao)) {
                        catProva.setInicioNumeracao(Integer.valueOf(this.txtInicioNumeracao.getText()));
                        catProva.setFimNumeracao(Integer.valueOf(this.txtFimNumeracao.getText()));
                        if (!validarFaixaNumeracao(catProva)) {
                            return;
                        }
                    } else {
                        return;
                    }
                }

                if (this.categoriaDS == null) {
                    this.categoriaDS = new CategoriaProvaDataSource(catProva);
                    this.tabCategoria.setItems(this.categoriaDS.getData());
                } else {
                    if (atualizar) {
                        this.categoriaDS.atualizar(this.linhaViewCategoriaEdicao, catProva);
                        this.linhaViewCategoriaEdicao = null;
                        this.btnAddCategoria.setText("adicionar");
                        this.btnCancelarEditarCategoria.setVisible(false);
                    } else {
                        if (!this.categoriaDS.contem(catProva)) {
                            this.categoriaDS.adicionar(catProva);
                        }
                    }
                }
                this.limparCamposCategoria();
            }
        }
    }

    @FXML
    public void cancelarEditarCategoria(ActionEvent actionEvent) {
        this.linhaViewCategoriaEdicao = null;
        this.btnAddCategoria.setText("adicionar");
        this.limparCamposCategoria();
        this.btnCancelarEditarCategoria.setVisible(false);
    }

    @FXML
    public void adicionarRepresentanteFederacao(ActionEvent actionEvent) {
        if (this.comboRepresentanteFederacao != null) {
            if (this.representanteFederacaoDS == null) {
                this.representanteFederacaoDS = new RepresentanteFederacaoDataSource(this.comboRepresentanteFederacao.getValue());
                this.tabFederacao.setItems(this.representanteFederacaoDS.getData());
            } else {
                if (!this.representanteFederacaoDS.contem(this.comboRepresentanteFederacao.getValue())) {
                    this.representanteFederacaoDS.adicionar(this.comboRepresentanteFederacao.getValue());
                }
            }
        }
    }

    @FXML
    public void concluir(ActionEvent actionEvent) {
        if (this.categoriaDS == null || this.categoriaDS.getData().isEmpty()) {
            super.cj.exibirAviso(super.cm.getMensagem("aviso_campo_adicionar", "categoria_atleta"));
            return;
        } else if (this.representanteFederacaoDS == null || this.representanteFederacaoDS.getData().isEmpty()) {
            super.cj.exibirAviso(super.cm.getMensagem("aviso_campo_adicionar", "representante_federacao"));
            return;
        }

        Prova prova = this.preencherBeanProva();
        if (this.operacao == Operacao.NOVO) {
            try {
                this.secretario.criarNovoProva(prova);
                super.cj.exibirInformacao(super.cm.getMensagem("sucesso_control_salvar", "prova"));
                super.resetJanela();
            } catch (NegocioException ex) {
                super.cj.exibirErro(cm.getMensagem("erro_control_salvar", "prova"), GeralUtil.getMensagemOriginalErro(ex));
            }
        } else {
            try {
                this.provaEdicao = this.secretario.criarNovoProva(prova);
                this.exibirProva();
            } catch (NegocioException ex) {
                super.cj.exibirErro(cm.getMensagem("erro_control_atualizar", "prova"), GeralUtil.getMensagemOriginalErro(ex));
            }
            super.cj.exibirInformacao(super.cm.getMensagem("sucesso_control_atualizar", "prova"));
        }
    }

    private void configurarTabelaOrganizacao() {
        this.colOrganizacaoProva.getStyleClass().add(ListaConstantesBase.EstiloCss.TAB_COL_ESQUERDO.getValor());
        this.colOrganizacaoProva.setCellValueFactory(new PropertyValueFactory("coluna2"));
        this.colRepresentanteProva.getStyleClass().add(ListaConstantesBase.EstiloCss.TAB_COL_ESQUERDO.getValor());
        this.colRepresentanteProva.setCellValueFactory(new PropertyValueFactory("coluna1"));
        configurarColunaRemoverRepresentanteProva();
    }

    private void configurarColunaRemoverRepresentanteProva() {
        this.colExcluirRepresentanteOrganizacao.getStyleClass().add(ListaConstantesBase.EstiloCss.TAB_COL_CENTRO.getValor());
        this.colExcluirRepresentanteOrganizacao.setCellValueFactory(new BotaoCelulaTableValueFactory());
        this.colExcluirRepresentanteOrganizacao.setCellFactory(new BotaoTableColumnFactory<GenericLinhaView<RepresentanteOrganizacaoProva>>("remover representante", ListaConstantesBase.EstiloCss.BOTAO_EXCLUIR.getValor()) {
            @Override
            public void eventoBotao(ActionEvent actionEvent, GenericLinhaView<RepresentanteOrganizacaoProva> linhaView) {
                removerRepresentanteProva(linhaView);
            }
        });
    }

    private void removerRepresentanteProva(GenericLinhaView linhaView) {
        if (cj.exibirDialogSimNao("titulo_apagar", "aviso_pergunta_excluir", null, "representante_organizacao_prova")) {
            try {
                RepresentanteOrganizacaoProva rop = this.representanteProvaDS.remover(linhaView);
                this.tabOrganizacao.refresh();
                if (this.operacao == Operacao.EDITAR && rop.getId() != null) {
                    this.listaRepresentanteProvaParaExcluir.add(rop);
                }
            } catch (Exception ex) {
                super.cj.exibirErro(cm.getMensagem("erro_control_excluir", "representante_organizacao_prova"), GeralUtil.getMensagemOriginalErro(ex));
            }
        }
    }

    private void configurarTabelaPercurso() {
        this.colNomePercurso.getStyleClass().add(ListaConstantesBase.EstiloCss.TAB_COL_ESQUERDO.getValor());
        this.colKm.getStyleClass().add(ListaConstantesBase.EstiloCss.TAB_COL_CENTRO.getValor());
        this.colVolta.getStyleClass().add(ListaConstantesBase.EstiloCss.TAB_COL_CENTRO.getValor());
        this.colDificuldade.getStyleClass().add(ListaConstantesBase.EstiloCss.TAB_COL_CENTRO.getValor());
        this.colNomePercurso.setCellValueFactory(new PropertyValueFactory("coluna1"));
        this.colKm.setCellValueFactory(new PropertyValueFactory("coluna2"));
        this.colVolta.setCellValueFactory(new PropertyValueFactory("coluna3"));
        this.colDificuldade.setCellValueFactory(new PropertyValueFactory("coluna4"));
        this.configurarColunaRemoverPercurso();
        this.configurarDuploCliquePercurso();
    }

    private void configurarColunaRemoverPercurso() {
        this.colExcluirPercurso.getStyleClass().add(ListaConstantesBase.EstiloCss.TAB_COL_CENTRO.getValor());
        this.colExcluirPercurso.setCellValueFactory(new BotaoCelulaTableValueFactory());
        this.colExcluirPercurso.setCellFactory(new BotaoTableColumnFactory<GenericLinhaView<Percurso>>("remover percurso", ListaConstantesBase.EstiloCss.BOTAO_EXCLUIR.getValor()) {
            @Override
            public void eventoBotao(ActionEvent actionEvent, GenericLinhaView<Percurso> linhaView) {
                removerPercurso(linhaView);
            }
        });
    }

    private void removerPercurso(GenericLinhaView linhaView) {
        Percurso percurso = this.percursoDS.getBean(linhaView);
        for (CategoriaDaProva categoriaProva : this.categoriaDS.getListaBean()) {
            if (categoriaProva.getPercurso().equals(percurso)) {
                super.cj.exibirAviso(cm.getMensagem("aviso_percurso_associada_categoria", categoriaProva.getCategoriaAtleta().getNome()));
                return;
            }
        }
        if (cj.exibirDialogSimNao("titulo_apagar", "aviso_pergunta_excluir", null, "percurso")) {
            try {
                Percurso p = this.percursoDS.remover(linhaView);
                tabPercurso.refresh();
                if (this.operacao == Operacao.EDITAR && p.getId() != null) {
                    this.listaPercursoParaExcluir.add(p);
                }
                this.cancelarEditarPercurso(null);
            } catch (Exception ex) {
                super.cj.exibirErro(cm.getMensagem("erro_control_excluir", "percurso"), GeralUtil.getMensagemOriginalErro(ex));
            }
        }
    }

    private void configurarDuploCliquePercurso() {
        // Evento duplo clique na tabela
        tabPercurso.setRowFactory(new TableViewDuploClique<GenericLinhaView<Percurso>>() {
            @Override
            public GenericLinhaView eventoDuploClique(GenericLinhaView<Percurso> linhaView) {
                ProvaController.this.linhaViewPercursoEdicao = linhaView;
                Percurso percurso = ProvaController.this.percursoDS.getBean(linhaView);
                ProvaController.this.txtNomePercurso.setText(percurso.getNome());
                ProvaController.this.txtDistancia.setText(percurso.getDistancia().toString().replace(".", ","));
                ProvaController.this.txtNumeroVolta.setText(percurso.getNumeroVolta().toString());
                ProvaController.this.comboGrauDificuldade.setValue(percurso.getGrauDificuldade());
                ProvaController.this.btnAddPercurso.setText("atualizar");
                ProvaController.this.btnCancelarEditarPercurso.setVisible(true);
                return linhaView;
            }
        });
    }

    private void configurarTabelaLargada() {
        this.colNomeLargada.getStyleClass().add(ListaConstantesBase.EstiloCss.TAB_COL_ESQUERDO.getValor());
        this.colHoraPrevista.getStyleClass().add(ListaConstantesBase.EstiloCss.TAB_COL_CENTRO.getValor());
        this.colNomeLargada.setCellValueFactory(new PropertyValueFactory("coluna1"));
        this.colHoraPrevista.setCellValueFactory(new PropertyValueFactory("coluna2"));
        this.configurarColunaRemoverLargada();
        this.configurarDuploCliqueLargada();
    }

    private void configurarColunaRemoverLargada() {
        this.colExcluirLargada.getStyleClass().add(ListaConstantesBase.EstiloCss.TAB_COL_CENTRO.getValor());
        this.colExcluirLargada.setCellValueFactory(new BotaoCelulaTableValueFactory());
        this.colExcluirLargada.setCellFactory(new BotaoTableColumnFactory<GenericLinhaView<Largada>>("remover largada", ListaConstantesBase.EstiloCss.BOTAO_EXCLUIR.getValor()) {
            @Override
            public void eventoBotao(ActionEvent actionEvent, GenericLinhaView<Largada> linhaView) {
                removerLargada(linhaView);
            }
        });
    }

    private void removerLargada(GenericLinhaView linhaView) {
        Largada largada = this.largadaDS.getBean(linhaView);
        for (CategoriaDaProva categoriaProva : this.categoriaDS.getListaBean()) {
            if (categoriaProva.getLargada().equals(largada)) {
                super.cj.exibirAviso(cm.getMensagem("aviso_largada_associada_categoria", categoriaProva.getCategoriaAtleta().getNome()));
                return;
            }
        }

        if (cj.exibirDialogSimNao("titulo_apagar", "aviso_pergunta_excluir", null, "largada")) {
            try {
                Largada largadaExcluida = this.largadaDS.remover(linhaView);
                this.tabLargada.refresh();
                if (this.operacao == Operacao.EDITAR && largadaExcluida.getId() != null) {
                    this.listaLargadaParaExcluir.add(largadaExcluida);
                }
                this.cancelarEditarLargada(null);
            } catch (Exception ex) {
                super.cj.exibirErro(cm.getMensagem("erro_control_excluir", "largada"), GeralUtil.getMensagemOriginalErro(ex));
            }
        }
    }

    private void configurarDuploCliqueLargada() {
        // Evento duplo clique na tabela
        tabLargada.setRowFactory(new TableViewDuploClique<GenericLinhaView<Largada>>() {
            @Override
            public GenericLinhaView eventoDuploClique(GenericLinhaView<Largada> linhaView) {
                ProvaController.this.linhaViewLargadaEdicao = linhaView;
                Largada largada = ProvaController.this.largadaDS.getBean(linhaView);
                ProvaController.this.txtNomeLargada.setText(largada.getNome());
                ProvaController.this.txtHoraPrevista.setText(FormatarUtil.localDateTimeToString(largada.getHoraPrevista(), FormatarUtil.FORMATO_HORA_MIN));
                ProvaController.this.btnAddLargada.setText("atualizar");
                ProvaController.this.btnCancelarEditarLargada.setVisible(true);
                return linhaView;
            }
        });
    }

    private void configurarTabelaCategoria() {
        this.colCategoria.getStyleClass().add(ListaConstantesBase.EstiloCss.TAB_COL_ESQUERDO.getValor());
        this.colPercurso.getStyleClass().add(ListaConstantesBase.EstiloCss.TAB_COL_ESQUERDO.getValor());
        this.colLargada.getStyleClass().add(ListaConstantesBase.EstiloCss.TAB_COL_ESQUERDO.getValor());

        this.colCategoria.setCellValueFactory(new PropertyValueFactory("coluna1"));
        this.colPercurso.setCellValueFactory(new PropertyValueFactory("coluna2"));
        this.colLargada.setCellValueFactory(new PropertyValueFactory("coluna3"));
        this.configurarColunaRemoverCategoria();
        this.configurarDuploCliqueCategoria();
    }

    private void configurarColunaRemoverCategoria() {
        this.colExcluirCategoria.getStyleClass().add(ListaConstantesBase.EstiloCss.TAB_COL_CENTRO.getValor());
        this.colExcluirCategoria.setCellValueFactory(new BotaoCelulaTableValueFactory());
        this.colExcluirCategoria.setCellFactory(new BotaoTableColumnFactory<GenericLinhaView<CategoriaDaProva>>("remover categoria", ListaConstantesBase.EstiloCss.BOTAO_EXCLUIR.getValor()) {
            @Override
            public void eventoBotao(ActionEvent actionEvent, GenericLinhaView<CategoriaDaProva> linhaView) {
                removerCategoria(linhaView);
            }
        });
    }

    private void removerCategoria(GenericLinhaView linhaView) {
        if (cj.exibirDialogSimNao("titulo_apagar", "aviso_pergunta_excluir", null, "categoria_atleta")) {
            try {
                CategoriaDaProva categoriaDaProva = this.categoriaDS.remover(linhaView);
                this.tabCategoria.refresh();
                if (this.operacao == Operacao.EDITAR && categoriaDaProva.getId() != null) {
                    this.listaCategoriaParaExcluir.add(categoriaDaProva);
                }
                this.cancelarEditarCategoria(null);
            } catch (Exception ex) {
                super.cj.exibirErro(cm.getMensagem("erro_control_excluir", "categoria_atleta"), GeralUtil.getMensagemOriginalErro(ex));
            }
        }
    }

    private void configurarDuploCliqueCategoria() {
        // Evento duplo clique na tabela
        tabCategoria.setRowFactory(new TableViewDuploClique<GenericLinhaView<CategoriaDaProva>>() {
            @Override
            public GenericLinhaView eventoDuploClique(GenericLinhaView<CategoriaDaProva> linhaView) {
                ProvaController.this.linhaViewCategoriaEdicao = linhaView;
                CategoriaDaProva categoria = ProvaController.this.categoriaDS.getBean(linhaView);
                ProvaController.this.comboCategoria.setValue(categoria.getCategoriaAtleta());
                ProvaController.this.comboPercurso.setValue(categoria.getPercurso());
                ProvaController.this.comboLargada.setValue(categoria.getLargada());
                ProvaController.this.comboNumeracaoAutomatica.setValue(categoria.getNumeracaoAutomatica());
                ProvaController.this.txtDigitoNumeracao.setText(categoria.getDigitosNumeracao() == null ? null : categoria.getDigitosNumeracao().toString());
                ProvaController.this.txtInicioNumeracao.setText(categoria.getInicioNumeracao() == null ? null : categoria.getInicioNumeracao().toString());
                ProvaController.this.txtFimNumeracao.setText(categoria.getFimNumeracao() == null ? null : categoria.getFimNumeracao().toString());

                ProvaController.this.btnAddCategoria.setText("atualizar");
                ProvaController.this.btnCancelarEditarCategoria.setVisible(true);
                return linhaView;
            }
        });
    }

    private void configurarTabelaRepresentanteFederacao() {
        this.colFederacao.getStyleClass().add(ListaConstantesBase.EstiloCss.TAB_COL_ESQUERDO.getValor());
        this.colRepresentanteFederacao.getStyleClass().add(ListaConstantesBase.EstiloCss.TAB_COL_ESQUERDO.getValor());

        this.colFederacao.setCellValueFactory(new PropertyValueFactory("coluna2"));
        this.colRepresentanteFederacao.setCellValueFactory(new PropertyValueFactory("coluna1"));
        configurarColunaRemoverRepresentanteFederacao();
    }

    private void configurarColunaRemoverRepresentanteFederacao() {
        this.colExcluirRepresentanteFederacao.getStyleClass().add(ListaConstantesBase.EstiloCss.TAB_COL_CENTRO.getValor());
        this.colExcluirRepresentanteFederacao.setCellValueFactory(new BotaoCelulaTableValueFactory());
        this.colExcluirRepresentanteFederacao.setCellFactory(new BotaoTableColumnFactory<GenericLinhaView<RepresentanteFederacao>>("remover representante da federação", ListaConstantesBase.EstiloCss.BOTAO_EXCLUIR.getValor()) {
            @Override
            public void eventoBotao(ActionEvent actionEvent, GenericLinhaView<RepresentanteFederacao> linhaView) {
                removerRepresentanteFederacao(linhaView);
            }
        });
    }

    private void removerRepresentanteFederacao(GenericLinhaView linhaView) {
        if (cj.exibirDialogSimNao("titulo_apagar", "aviso_pergunta_excluir", null, "representante_federacao")) {
            try {
                RepresentanteFederacao representanteFederacao = this.representanteFederacaoDS.remover(linhaView);
                this.tabFederacao.refresh();
                if (this.operacao == Operacao.EDITAR && representanteFederacao.getId() != null) {
                    this.listaRepresentanteFederacaoParaExcluir.add(representanteFederacao);
                }
            } catch (Exception ex) {
                super.cj.exibirErro(cm.getMensagem("erro_control_excluir", "representante_federacao"), GeralUtil.getMensagemOriginalErro(ex));
            }
        }
    }

    private void carregarComboRepresentanteProva() {
        this.comboRepresentanteProva.getItems().clear();
        OrganizacaoProva op = this.comboOrganizacaoProva.getValue();
        if (op != null) {
            try {
                List<RepresentanteOrganizacaoProva> listaRepresentanteOrganizacaoProva = this.estagiario.pesquisarRepresentanteOrganizacaoProvaPorOrganizacaoProva(op);
                this.comboRepresentanteProva.getItems().addAll(listaRepresentanteOrganizacaoProva);
            } catch (NegocioException ex) {
                super.cj.exibirErro(this.cm.getMensagem("erro_pesquisa", "representante_organizacao_prova"), GeralUtil.getMensagemOriginalErro(ex));
            }
        }
    }

    private void carregarComboRepresentanteFederacao() {
        this.comboRepresentanteFederacao.getItems().clear();
        Federacao federacao = this.comboFederacao.getValue();
        if (federacao != null) {
            try {
                List<RepresentanteFederacao> listaRepresentante = this.estagiario.pesquisarRepresentanteFederacaoPorFederacao(federacao);
                this.comboRepresentanteFederacao.getItems().addAll(listaRepresentante);
            } catch (NegocioException ex) {
                super.cj.exibirErro(this.cm.getMensagem("erro_pesquisa", "representante_federacao"), GeralUtil.getMensagemOriginalErro(ex));
            }
        }
    }

    @Override
    public void setOperacao(Operacao operacao) {
        this.operacao = operacao;
    }

    @Override
    public Prova getProvaEdicao() {
        return provaEdicao;
    }

    @Override
    public void setProvaEdicao(Prova provaEdicao) {
        this.provaEdicao = provaEdicao;
    }

    private void limparCamposPercurso() {
        this.txtNomePercurso.setText("");
        this.txtDistancia.setText("");
        this.txtNumeroVolta.setText("");
        this.comboGrauDificuldade.setValue(null);
    }

    private void limparCamposLargada() {
        this.txtNomeLargada.setText("");
        this.txtHoraPrevista.setText("");
    }

    private void limparCamposCategoria() {
        this.comboCategoria.setValue(null);
        this.comboPercurso.setValue(null);
        this.comboLargada.setValue(null);
        this.txtDigitoNumeracao.setText("");
        this.txtInicioNumeracao.setText("");
        this.txtFimNumeracao.setText("");
    }

    private Prova preencherBeanProva() {
        Prova prova = this.operacao == Operacao.EDITAR ? this.provaEdicao : new Prova();

        prova.setNome(this.txtNomeProva.getText());
        prova.setData(FormatarUtil.stringToLocalDate(this.dpData.getEditor().getText()));
        prova.setStatus(prova.getStatus() == null ? StatusProva.INSCRICAO_ABERTA : prova.getStatus());

        if (prova.getEndereco() == null) {
            prova.setEndereco(new Endereco());
        }
        prova.getEndereco().setLogradouro(this.txtLogradouro.getText());
        prova.getEndereco().setNumero(GenericValidator.isInt(this.txtNumero.getText()) ? Integer.valueOf(this.txtNumero.getText()) : null);
        prova.getEndereco().setComplemento(this.txtComplemento.getText());
        prova.getEndereco().setBairro(this.txtBairro.getText());
        prova.getEndereco().setCidade(this.txtCidade.getText());
        prova.getEndereco().setUf(this.comboUf.getValue().getSigla());
        prova.getEndereco().setCep(FormatarUtil.removerMascaraNumero(this.txtCep.getText()));

        // Representante Organizacao de Prova
        if (prova.getListaRepresentanteOrganizacao() == null) {
            prova.setListaRepresentanteOrganizacao(new LinkedHashSet<>());
        } else {
            prova.getListaRepresentanteOrganizacao().clear();
        }
        prova.getListaRepresentanteOrganizacao().addAll(this.representanteProvaDS.getListaBean());

        // Percurso
        if (prova.getListaPercurso() == null) {
            prova.setListaPercurso(new LinkedHashSet<>());
        } else {
            prova.getListaPercurso().clear();
        }
        this.percursoDS.getListaBean().forEach((percurso) -> {
            percurso.setProva(prova);
            prova.getListaPercurso().add(percurso);
        });

        // Largada
        if (prova.getListaLargada() == null) {
            prova.setListaLargada(new LinkedHashSet<>());
        } else {
            prova.getListaLargada().clear();
        }
        this.largadaDS.getListaBean().forEach((largada) -> {
            largada.setProva(prova);
            prova.getListaLargada().add(largada);
        });

        // Categoria
        if (prova.getListaCategoriaDaProva() == null) {
            prova.setListaCategoriaDaProva(new LinkedHashSet<>());
        } else {
            prova.getListaCategoriaDaProva().clear();
        }
        prova.getListaCategoriaDaProva().addAll(this.categoriaDS.getListaBean());

        // Representante da Federacao
        if (prova.getListaRepresentanteFederacao() == null) {
            prova.setListaRepresentanteFederacao(new LinkedHashSet<>());
        } else {
            prova.getListaRepresentanteFederacao().clear();
        }
        prova.getListaRepresentanteFederacao().addAll(this.representanteFederacaoDS.getListaBean());
        return prova;
    }

    private void exibirProva() {
        try {
            this.provaEdicao = this.secretario.carregarTudoProva(this.provaEdicao);
        } catch (NegocioException ex) {
            cj.exibirErro(cm.getMensagem("erro_control_pesquisar", "prova"), GeralUtil.getMensagemOriginalErro(ex));
            return;
        }
        this.txtNomeProva.setText(this.provaEdicao.getNome());
        this.dpData.getEditor().setText(FormatarUtil.localDateToString(this.provaEdicao.getData()));

        this.txtLogradouro.setText(this.provaEdicao.getEndereco().getLogradouro());
        this.txtNumero.setText(this.provaEdicao.getEndereco().getNumero() == null ? "" : this.provaEdicao.getEndereco().getNumero().toString());
        this.txtComplemento.setText(this.provaEdicao.getEndereco().getComplemento());
        this.txtBairro.setText(this.provaEdicao.getEndereco().getBairro());
        this.txtCidade.setText(this.provaEdicao.getEndereco().getCidade());
        this.comboUf.setValue(EstadoBrasil.getEstado(this.provaEdicao.getEndereco().getUf()));
        this.txtCep.setText(FormatarUtil.formatarCep(this.provaEdicao.getEndereco().getCep()));

        // Representante Organizacao de Prova
        this.representanteProvaDS = new RepresentanteOrganizacaoProvaDataSource(new ArrayList(this.provaEdicao.getListaRepresentanteOrganizacao()));
        this.tabOrganizacao.setItems(this.representanteProvaDS.getData());

        // Percurso
        this.percursoDS = new PercursoDataSource(new ArrayList(this.provaEdicao.getListaPercurso()));
        this.tabPercurso.setItems(this.percursoDS.getData());

        // Largada
        this.largadaDS = new LargadaDataSource(new ArrayList(this.provaEdicao.getListaLargada()));
        this.tabLargada.setItems(this.largadaDS.getData());

        // Categoria
        this.categoriaDS = new CategoriaProvaDataSource(new ArrayList(this.provaEdicao.getListaCategoriaDaProva()));
        this.tabCategoria.setItems(this.categoriaDS.getData());

        // Representante da Federacao
        this.representanteFederacaoDS = new RepresentanteFederacaoDataSource(new ArrayList(this.provaEdicao.getListaRepresentanteFederacao()));
        this.tabFederacao.setItems(this.representanteFederacaoDS.getData());
    }

    private void atualizarCategoriaPercurso(Percurso percursoNovo, Percurso percursoAntigo) {
        this.categoriaDS.getData().forEach((linhaView) -> {
            if (linhaView.getBean().getPercurso().equals(percursoAntigo)) {
                linhaView.getBean().setPercurso(percursoNovo);
            }
        });
        this.categoriaDS.recarregar();
    }

    private void atualizarCategoriaLargada(Largada largadaNovo, Largada largadaAntigo) {
        this.categoriaDS.getData().forEach((linhaView) -> {
            if (linhaView.getBean().getLargada().equals(largadaAntigo)) {
                linhaView.getBean().setLargada(largadaNovo);
            }
        });
        this.categoriaDS.recarregar();
    }

    @FXML
    public void abrirNovoCategoriaAtleta() {
        this.categoriaAtletaController.setOperacao(ListaConstantesBase.Operacao.NOVO);
        super.cj.abrirJanela(ListaConstantes.Stages.CATEGORIA_ATLETA.ordinal(), ListaConstantes.Stages.PROVA.ordinal(), ICategoriaAtletaController.class, "categoria_atleta", true);
        this.carregarCategoriaAtleta();
    }

    private void carregarCategoriaAtleta() {
        this.comboCategoria.getItems().clear();
        this.comboCategoria.getItems().addAll(this.listaSistema.getListaCategoriaAtleta());
    }

    private boolean categoriaExistenteNaProva(CategoriaAtleta categoriaAtleta) {
        if (this.categoriaDS == null) {
            return false;
        } else {
            return this.categoriaDS.getListaBean().stream().anyMatch((cp) -> (cp.getCategoriaAtleta().equals(categoriaAtleta)));
        }
    }

    private void atualizarVisualizacaoNumeracaoAutomatica() {
        ListaConstantes.NumeracaoAutomatica numeracaoAutomatica = this.comboNumeracaoAutomatica.getValue();
        this.lblDigitoNumeracao.setVisible(numeracaoAutomatica == ListaConstantes.NumeracaoAutomatica.POR_FAIXA || numeracaoAutomatica == ListaConstantes.NumeracaoAutomatica.SEQUENCIAL);
        this.txtDigitoNumeracao.setVisible(numeracaoAutomatica == ListaConstantes.NumeracaoAutomatica.POR_FAIXA || numeracaoAutomatica == ListaConstantes.NumeracaoAutomatica.SEQUENCIAL);
        this.lblInicioNumeracao.setVisible(numeracaoAutomatica == ListaConstantes.NumeracaoAutomatica.POR_FAIXA);
        this.txtInicioNumeracao.setVisible(numeracaoAutomatica == ListaConstantes.NumeracaoAutomatica.POR_FAIXA);
        this.lblFimNumeracao.setVisible(numeracaoAutomatica == ListaConstantes.NumeracaoAutomatica.POR_FAIXA);
        this.txtFimNumeracao.setVisible(numeracaoAutomatica == ListaConstantes.NumeracaoAutomatica.POR_FAIXA);
    }

    private boolean validarFaixaNumeracao(CategoriaDaProva catProva) {
        if (catProva.getInicioNumeracao() >= catProva.getFimNumeracao()) {
            super.cj.exibirAviso(cm.getMensagem("aviso_faixa_numeracao_invalida", catProva.getCategoriaAtleta().getNome()));
            return false;
        }

        if (this.categoriaDS != null) {
            for (CategoriaDaProva cp : this.categoriaDS.getListaBean()) {
                if (!catProva.equals(cp) && cp.getNumeracaoAutomatica() == ListaConstantes.NumeracaoAutomatica.POR_FAIXA) {
                    if ((catProva.getInicioNumeracao() >= cp.getInicioNumeracao() && catProva.getInicioNumeracao() <= cp.getFimNumeracao())
                            || (catProva.getFimNumeracao() >= cp.getInicioNumeracao() && catProva.getFimNumeracao() <= cp.getFimNumeracao())
                            || (catProva.getInicioNumeracao() < cp.getInicioNumeracao() && catProva.getFimNumeracao() > cp.getFimNumeracao())) {
                        super.cj.exibirAviso(cm.getMensagem("aviso_faixa_numeracao_coincidente", catProva.getCategoriaAtleta().getNome(), cp.getCategoriaAtleta().getNome()));
                        return false;
                    }
                }
            }
        }
        return true;
    }
}
