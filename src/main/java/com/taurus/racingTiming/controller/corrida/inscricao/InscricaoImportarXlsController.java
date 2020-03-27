package com.taurus.racingTiming.controller.corrida.inscricao;

import com.taurus.racingTiming.controller.corrida.inscricao.it.IImportarInscricaoXlsController;
import com.taurus.racingTiming.controller.corrida.inscricao.it.IInscricaoImportarAssociarCategoriaController;
import com.taurus.pojo.ResultadoThread;
import com.taurus.exception.AvisoNegocioException;
import com.taurus.exception.NegocioException;
import com.taurus.javafx.controller.FXMLBaseController;
import com.taurus.javafx.listener.SaidaFocoListener;
import com.taurus.javafx.util.ControleJanela;
import com.taurus.javafx.util.JavafxUtil;
import com.taurus.javafx.util.ValidarCampoUtil;
import com.taurus.racingTiming.controller.corrida.IPopupProvaController;
import com.taurus.racingTiming.entidade.corrida.Prova;
import com.taurus.racingTiming.negocio.IImportador;
import com.taurus.racingTiming.negocio.ISecretario;
import com.taurus.racingTiming.pojo.CorrespondenciaEntidadeEExcel;
import com.taurus.racingTiming.util.ListaConstantes;
import com.taurus.racingTiming.util.ListaConstantes.Stages;
import com.taurus.util.FormatarUtil;
import com.taurus.util.GeralUtil;
import com.taurus.util.ListaConstantesBase;
import com.taurus.util.ListaConstantesBase.EstadoBrasil;
import com.taurus.util.annotation.Mascara;
import com.taurus.util.annotation.Parametro;
import com.taurus.util.annotation.ValidarCampo;
import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;
import javafx.stage.FileChooser;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.validator.GenericValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class InscricaoImportarXlsController extends FXMLBaseController implements IImportarInscricaoXlsController {

    private static final Log LOG = LogFactory.getLog(InscricaoImportarXlsController.class);

    private boolean selecionarProva = true;
    @Parametro
    private Prova prova;
    private File file;
    private FileChooser fileChooser = new FileChooser();
    private FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Arquivo excel", "*.xls", "*.xlsx");
    private CorrespondenciaEntidadeEExcel correspondencia;
    private Integer totalCategoriaXls = null;

    public InscricaoImportarXlsController() {
        super();
    }

    @Autowired
    private IImportador importador;

    @Autowired
    private IPopupProvaController popupProvaController;
    @Autowired
    private IInscricaoImportarAssociarCategoriaController associarCategoria;

    boolean exibirProva = false;

    @FXML
    @Mascara(tipo = Mascara.TipoMascara.TEXT_FIELD_MAXIMO_CARACTER, tamanhoMax = "50")
    @ValidarCampo
    private TextField txtNomeProva;
    @FXML
    private TextField txtSituacao;
    @FXML
    private Button btnPesquisarProva;
    @FXML
    private TextField txtData;
    @FXML
    private TextField txtCidade;
    @FXML
    private TextField txtUf;
    @FXML
    @ValidarCampo
    private TextField txtPathArquivo;
    @FXML
    private TableView<ObservableList<String>> tabXls;

    @FXML
    @Mascara(tipo = Mascara.TipoMascara.TEXT_FIELD_APENAS_NUMERO, tamanhoMax = "2")
    @ValidarCampo
    private TextField txtLinhaCabecalho;

    @FXML
    AnchorPane paneColuna;

    //Campos de correspondência de colunas
    @FXML
    @Mascara(tipo = Mascara.TipoMascara.TEXT_FIELD_APENAS_NUMERO, tamanhoMax = "2")
    @ValidarCampo
    private TextField txtCategoria;
    @FXML
    private Button btnCorrespondenciaCategoria;
    @FXML
    @Mascara(tipo = Mascara.TipoMascara.TEXT_FIELD_APENAS_NUMERO, tamanhoMax = "2")
    @ValidarCampo
    private TextField txtNomeAtleta;
    @FXML
    @Mascara(tipo = Mascara.TipoMascara.TEXT_FIELD_APENAS_NUMERO, tamanhoMax = "2")
    private TextField txtCodigoFederacao;
    @FXML
    @Mascara(tipo = Mascara.TipoMascara.TEXT_FIELD_APENAS_NUMERO, tamanhoMax = "2")
    private TextField txtCodigoCbc;
    @FXML
    @Mascara(tipo = Mascara.TipoMascara.TEXT_FIELD_APENAS_NUMERO, tamanhoMax = "2")
    private TextField txtCodigoUci;
    @FXML
    @Mascara(tipo = Mascara.TipoMascara.TEXT_FIELD_APENAS_NUMERO, tamanhoMax = "2")
    @ValidarCampo
    private TextField txtSexo;
    @FXML
    @Mascara(tipo = Mascara.TipoMascara.TEXT_FIELD_APENAS_NUMERO, tamanhoMax = "2")
    @ValidarCampo
    private TextField txtDataNascimentoAtleta;
    @FXML
    private TextField txtDtNascAtletaPattern;
    @FXML
    @Mascara(tipo = Mascara.TipoMascara.TEXT_FIELD_APENAS_NUMERO, tamanhoMax = "2")
    private TextField txtRgAtleta;
    @FXML
    @Mascara(tipo = Mascara.TipoMascara.TEXT_FIELD_APENAS_NUMERO, tamanhoMax = "2")
    private TextField txtDataRg;
    @FXML
    private TextField txtDtRgPattern;
    @FXML
    @Mascara(tipo = Mascara.TipoMascara.TEXT_FIELD_APENAS_NUMERO, tamanhoMax = "2")
    private TextField txtOrgaoRg;
    @FXML
    @Mascara(tipo = Mascara.TipoMascara.TEXT_FIELD_APENAS_NUMERO, tamanhoMax = "2")
    private TextField txtCpf;
    @FXML
    @Mascara(tipo = Mascara.TipoMascara.TEXT_FIELD_APENAS_NUMERO, tamanhoMax = "2")
    private TextField txtLogradouro;
    @FXML
    @Mascara(tipo = Mascara.TipoMascara.TEXT_FIELD_APENAS_NUMERO, tamanhoMax = "2")
    private TextField txtNumero;
    @FXML
    @Mascara(tipo = Mascara.TipoMascara.TEXT_FIELD_APENAS_NUMERO, tamanhoMax = "2")
    private TextField txtComplemento;
    @FXML
    @Mascara(tipo = Mascara.TipoMascara.TEXT_FIELD_APENAS_NUMERO, tamanhoMax = "2")
    private TextField txtBairroAtleta;
    @FXML
    @Mascara(tipo = Mascara.TipoMascara.TEXT_FIELD_APENAS_NUMERO, tamanhoMax = "2")
    @ValidarCampo
    private TextField txtCidadeAtleta;
    @FXML
    @Mascara(tipo = Mascara.TipoMascara.TEXT_FIELD_APENAS_NUMERO, tamanhoMax = "2")
    @ValidarCampo
    private TextField txtUfAtleta;
    @FXML
    @Mascara(tipo = Mascara.TipoMascara.TEXT_FIELD_APENAS_NUMERO, tamanhoMax = "2")
    private TextField txtCep;
    @FXML
    @Mascara(tipo = Mascara.TipoMascara.TEXT_FIELD_APENAS_NUMERO, tamanhoMax = "2")
    private TextField txtTelefone1;
    @FXML
    @Mascara(tipo = Mascara.TipoMascara.TEXT_FIELD_APENAS_NUMERO, tamanhoMax = "2")
    private TextField txtTelefone2;
    @FXML
    @Mascara(tipo = Mascara.TipoMascara.TEXT_FIELD_APENAS_NUMERO, tamanhoMax = "2")
    private TextField txtEmail;
    @FXML
    @Mascara(tipo = Mascara.TipoMascara.TEXT_FIELD_APENAS_NUMERO, tamanhoMax = "2")
    private TextField txtEquipe;
    @FXML
    @Mascara(tipo = Mascara.TipoMascara.TEXT_FIELD_APENAS_NUMERO, tamanhoMax = "2")
    private TextField txtEquipamento;
    @FXML
    @Mascara(tipo = Mascara.TipoMascara.TEXT_FIELD_APENAS_NUMERO, tamanhoMax = "2")
    private TextField txtProfissao;
    @FXML
    @Mascara(tipo = Mascara.TipoMascara.TEXT_FIELD_APENAS_NUMERO, tamanhoMax = "2")
    private TextField txtTipoSanguineo;
    @FXML
    @Mascara(tipo = Mascara.TipoMascara.TEXT_FIELD_APENAS_NUMERO, tamanhoMax = "2")
    private TextField txtDataInscricao;
    @FXML
    private TextField txtDtInscricaoPattern;
    @FXML
    @Mascara(tipo = Mascara.TipoMascara.TEXT_FIELD_APENAS_NUMERO, tamanhoMax = "2")
    private TextField txtNomeContatoUrgencia;
    @FXML
    @Mascara(tipo = Mascara.TipoMascara.TEXT_FIELD_APENAS_NUMERO, tamanhoMax = "2")
    private TextField txtParentesco;
    @FXML
    @Mascara(tipo = Mascara.TipoMascara.TEXT_FIELD_APENAS_NUMERO, tamanhoMax = "2")
    private TextField txtTelefone1ContUrg;
    @FXML
    @Mascara(tipo = Mascara.TipoMascara.TEXT_FIELD_APENAS_NUMERO, tamanhoMax = "2")
    private TextField txtTelefone2ContUrg;
    @FXML
    @Mascara(tipo = Mascara.TipoMascara.TEXT_FIELD_APENAS_NUMERO, tamanhoMax = "2")
    private TextField txtNumeroAtleta;

    @FXML
    ProgressIndicator indicadorProgresso;

    @Value("${fxml.importarInscricaoXls.view}")
    @Override
    public void setFxmlFilePath(String filePath) {
        this.fxmlFilePath = filePath;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.indicadorProgresso.setVisible(false);

        this.fileChooser.getExtensionFilters().add(extFilter);

        this.btnPesquisarProva.getStyleClass().add(ListaConstantesBase.EstiloCss.BOTAO_PESQUISAR.getValor());
        this.btnPesquisarProva.setTooltip(new Tooltip("pesquisar prova"));
        this.btnCorrespondenciaCategoria.getStyleClass().add(ListaConstantesBase.EstiloCss.BOTAO_TROCAR.getValor());
        this.btnCorrespondenciaCategoria.setTooltip(new Tooltip("associar categorias"));

        this.txtNomeProva.focusedProperty().addListener(new SaidaFocoListener(this.txtNomeProva) {
            @Override
            public void executar() {
                if (!exibirProva) {
                    txtData.setText("");
                    txtCidade.setText("");
                    txtUf.setText("");
                    txtSituacao.setText("");
                    prova = null;
                }
            }
        });

        // Evento clique na tabela
        this.tabXls.setRowFactory((TableView<ObservableList<String>> param) -> {
            TableRow<ObservableList<String>> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 1 && (!row.isEmpty())) {
                    TablePosition pos = this.tabXls.getSelectionModel().getSelectedCells().get(0);
                    int coluna = pos.getColumn();
                    Node nodeFocused = InscricaoImportarXlsController.this.obterNodeColunaFucused();
                    if (nodeFocused != null) {
                        ((TextField) nodeFocused).setText(String.valueOf(coluna + 1));
                    }
                }
            });
            return row;
        });
    }

    @Override
    public void reinicializarJanelaEspecifico() {
    }

    @Override
    public void aoAbrirJanela() {
        this.txtNomeProva.requestFocus();
        //TODO verificar pq nao exibir a luba
        //TODO a lupa esta sumindo ao reabrir a janela
        //this.btnPesquisarProva.setVisible(this.selecionarProva);

        if (!this.selecionarProva && this.prova != null) {
            this.exibirProva();
        }
    }

    @Override
    public void aoFecharJanelaEspecifico() {
        this.selecionarProva = true;
    }

    private void exibirProva() {
        this.exibirProva = true;
        this.txtNomeProva.setText(this.prova.getNome());
        this.txtSituacao.setText(this.prova.getStatus().getDescricao());
        this.txtData.setText(FormatarUtil.localDateToString(prova.getData()));
        this.txtCidade.setText(this.prova.getEndereco().getCidade());
        this.txtUf.setText(EstadoBrasil.getEstado(this.prova.getEndereco().getUf()).getNome());
        this.txtPathArquivo.requestFocus();
        this.exibirProva = false;
    }

    @FXML
    public void pesquisarProva(ActionEvent actionEvent) {
        this.popupProvaController.setNomeProva(this.txtNomeProva.getText());
        this.cj.abrirJanelaPopup(ListaConstantes.Stages.POPUP_PROVA.ordinal(), ListaConstantes.Stages.IMPORTAR_INSCRICAO.ordinal(), IPopupProvaController.class, (Region) actionEvent.getSource(), ControleJanela.PosicaoReferenciaPopup.DIREITA_CENTRO);
        if (this.popupProvaController.getProvaSelecionado() != null) {
            this.prova = this.popupProvaController.getProvaSelecionado();
            this.exibirProva();
        }
    }

    @FXML
    public void abrirArquivo(ActionEvent actionEvent) {
        this.file = fileChooser.showOpenDialog(super.getStage());
        if (this.file != null) {
            this.txtPathArquivo.setText(this.file.getPath());

            Task tarefa = this.criarTarefaExibirPlanilhaView();
            this.indicadorProgresso.visibleProperty().unbind();
            this.indicadorProgresso.visibleProperty().bind(tarefa.runningProperty());

            // Utilizado quando precisa rodar uma thread que ira modificar o GUI
            // Entretanto a Thread atual nao termina enquanto a thread chamada não finalizar.
            // Platform.runLater(this.tarefa);
            new Thread(tarefa).start();
            this.correspondencia = new CorrespondenciaEntidadeEExcel();
        } else {
            this.txtPathArquivo.setText("");
        }
    }

    @FXML
    public void importar(ActionEvent actionEvent) {
        try {
            if (this.prova == null) {
                super.cj.exibirAviso("aviso_selecione_prova");
                return;
            } else if (this.file == null) {
                super.cj.exibirAviso("aviso_selecione_arquivo_xls");
                return;
            } else if (!ValidarCampoUtil.validarCampos(this)) {
                super.cj.exibirAviso("aviso_campos_obrigatorios");
                return;
            } else if (!this.isCategoriasAssociadas()) {
                super.cj.exibirAviso("aviso_cat_impor_nao_associada");
                return;
            }
        } catch (IllegalArgumentException | IllegalAccessException ex) {
            super.cj.exibirErro("erro_validacao_campos", GeralUtil.getMensagemOriginalErro(ex));
            return;
        }

        this.correspondencia = this.preencherCorrespondencias();

        Task tarefa2 = this.criarTarefaImportarArquivo();

        this.indicadorProgresso.visibleProperty().unbind();
        this.indicadorProgresso.visibleProperty().bind(tarefa2.runningProperty());
        new Thread(tarefa2).start();
    }

    private void abrirPlanilhaView() {
        List<List<String>> linhas;
        try {
            linhas = this.importador.carregarXLinhasExcell(this.file, 10);
        } catch (Exception ex) {
            super.cj.exibirErro("Não foi possivel ler o arquivo do excel", GeralUtil.getMensagemOriginalErro(ex));
            this.file = null;
            return;
        }
        this.exibirPlanilhaView(linhas);
    }

    private void exibirPlanilhaView(List<List<String>> linhas) {
        try {
            this.excluirColunas();
            this.tabXls.getItems().clear();

            int totalColunas = 0;
            for (List<String> celulas : linhas) {
                int coluna = 1;
                for (String celula : celulas) {
                    final int finalIdx = coluna - 1;
                    if (coluna > totalColunas) {
                        totalColunas = coluna;

                        TableColumn<ObservableList<String>, String> col = new TableColumn<>("Coluna " + coluna);
                        col.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue().get(finalIdx)));

                        col.setResizable(true);
                        col.setPrefWidth(100d);
                        //col.setMaxWidth(100d);
                        col.setEditable(true);
                        col.setSortable(false);
                        this.tabXls.getColumns().add(col);
                    }
                    coluna++;
                }
                this.tabXls.getItems().add(FXCollections.observableArrayList(celulas));
            }
            this.tabXls.setEditable(true);
        } catch (Exception ex) {
            super.cj.exibirErro("Não foi possivel montar a tabela do arquivo excel.", GeralUtil.getMensagemOriginalErro(ex));
            this.file = null;
        }
    }

    private void excluirColunas() {
        while (this.tabXls.getColumns().size() > 0) {
            this.tabXls.getColumns().remove(0);
        }
    }

    private CorrespondenciaEntidadeEExcel preencherCorrespondencias() {
        if (!GenericValidator.isBlankOrNull(this.txtLinhaCabecalho.getText())) {
            correspondencia.setTotalLinhasCabecalho(Integer.valueOf(this.txtLinhaCabecalho.getText()));
        }
        if (!GenericValidator.isBlankOrNull(this.txtCategoria.getText())) {
            correspondencia.setColunaCategoria(Integer.valueOf(this.txtCategoria.getText()) - 1);
        }
        if (!GenericValidator.isBlankOrNull(this.txtNomeAtleta.getText())) {
            correspondencia.setColunaNomeAtleta(Integer.valueOf(this.txtNomeAtleta.getText()) - 1);
        }
        if (!GenericValidator.isBlankOrNull(this.txtCodigoFederacao.getText())) {
            correspondencia.setColunaCodigoFederacaoAtleta(JavafxUtil.getInteger(this.txtCodigoFederacao) - 1);
        }
        if (!GenericValidator.isBlankOrNull(this.txtCodigoCbc.getText())) {
            correspondencia.setColunaCodigoCbcAtleta(JavafxUtil.getInteger(this.txtCodigoCbc) - 1);
        }
        if (!GenericValidator.isBlankOrNull(this.txtCodigoUci.getText())) {
            correspondencia.setColunaCodigoUciAtleta(JavafxUtil.getInteger(this.txtCodigoUci) - 1);
        }
        if (!GenericValidator.isBlankOrNull(this.txtSexo.getText())) {
            correspondencia.setColunaSexoAtleta(Integer.valueOf(this.txtSexo.getText()) - 1);
        }
        if (!GenericValidator.isBlankOrNull(this.txtDataNascimentoAtleta.getText())) {
            correspondencia.setColunaDataNascimentoAtleta(Integer.valueOf(this.txtDataNascimentoAtleta.getText()) - 1);
        }
        if (!GenericValidator.isBlankOrNull(this.txtDtNascAtletaPattern.getText())) {
            correspondencia.setDtNascimentoAtletaPattern(this.txtDtNascAtletaPattern.getText());
        }
        if (!GenericValidator.isBlankOrNull(this.txtRgAtleta.getText())) {
            correspondencia.setColunaRgAtleta(JavafxUtil.getInteger(this.txtRgAtleta) - 1);
        }
        if (!GenericValidator.isBlankOrNull(this.txtDataRg.getText())) {
            correspondencia.setColunaDataRgAtleta(JavafxUtil.getInteger(this.txtDataRg) - 1);
        }
        if (!GenericValidator.isBlankOrNull(this.txtDtRgPattern.getText())) {
            correspondencia.setDtRgAtletaPattern(this.txtDtRgPattern.getText());
        }
        if (!GenericValidator.isBlankOrNull(this.txtOrgaoRg.getText())) {
            correspondencia.setColunaOrgaoRgAtleta(JavafxUtil.getInteger(this.txtOrgaoRg) - 1);
        }
        if (!GenericValidator.isBlankOrNull(this.txtCpf.getText())) {
            correspondencia.setColunaCpfAtleta(JavafxUtil.getInteger(this.txtCpf) - 1);
        }
        if (!GenericValidator.isBlankOrNull(this.txtLogradouro.getText())) {
            correspondencia.setColunaLogradouroAtleta(JavafxUtil.getInteger(this.txtLogradouro) - 1);
        }
        if (!GenericValidator.isBlankOrNull(this.txtNumero.getText())) {
            correspondencia.setColunaNumeroLogradouroAtleta(JavafxUtil.getInteger(this.txtNumero) - 1);
        }
        if (!GenericValidator.isBlankOrNull(this.txtComplemento.getText())) {
            correspondencia.setColunaComplementoEnderecoAtleta(JavafxUtil.getInteger(this.txtComplemento) - 1);
        }
        if (!GenericValidator.isBlankOrNull(this.txtBairroAtleta.getText())) {
            correspondencia.setColunaBairroAtleta(JavafxUtil.getInteger(this.txtBairroAtleta) - 1);
        }
        if (!GenericValidator.isBlankOrNull(this.txtCidadeAtleta.getText())) {
            correspondencia.setColunaCidadeAtleta(Integer.valueOf(this.txtCidadeAtleta.getText()) - 1);
        }
        if (!GenericValidator.isBlankOrNull(this.txtUfAtleta.getText())) {
            correspondencia.setColunaUfAtleta(Integer.valueOf(this.txtUfAtleta.getText()) - 1);
        }
        if (!GenericValidator.isBlankOrNull(this.txtCep.getText())) {
            correspondencia.setColunaCepAtleta(JavafxUtil.getInteger(this.txtCep) - 1);
        }
        if (!GenericValidator.isBlankOrNull(this.txtTelefone1.getText())) {
            correspondencia.setColunaTelefone1Atleta(JavafxUtil.getInteger(this.txtTelefone1) - 1);
        }
        if (!GenericValidator.isBlankOrNull(this.txtTelefone2.getText())) {
            correspondencia.setColunaTelefone2Atleta(JavafxUtil.getInteger(this.txtTelefone2) - 1);
        }
        if (!GenericValidator.isBlankOrNull(this.txtEmail.getText())) {
            correspondencia.setColunaEmailAtleta(JavafxUtil.getInteger(this.txtEmail) - 1);
        }
        if (!GenericValidator.isBlankOrNull(this.txtEquipe.getText())) {
            correspondencia.setColunaEquipeAtleta(JavafxUtil.getInteger(this.txtEquipe) - 1);
        }
        if (!GenericValidator.isBlankOrNull(this.txtEquipamento.getText())) {
            correspondencia.setColunaEquipamentoAtleta(JavafxUtil.getInteger(this.txtEquipamento) - 1);
        }
        if (!GenericValidator.isBlankOrNull(this.txtProfissao.getText())) {
            correspondencia.setColunaProfissaoAtleta(JavafxUtil.getInteger(this.txtProfissao) - 1);
        }
        if (!GenericValidator.isBlankOrNull(this.txtTipoSanguineo.getText())) {
            correspondencia.setColunaTipoSaguineoAtleta(JavafxUtil.getInteger(this.txtTipoSanguineo) - 1);
        }
        if (!GenericValidator.isBlankOrNull(this.txtDataInscricao.getText())) {
            correspondencia.setColunaDataInscricao(JavafxUtil.getInteger(this.txtDataInscricao) - 1);
        }
        if (!GenericValidator.isBlankOrNull(this.txtDtInscricaoPattern.getText())) {
            correspondencia.setDtInscricaoPattern(this.txtDtInscricaoPattern.getText());
        }
        if (!GenericValidator.isBlankOrNull(this.txtNomeContatoUrgencia.getText())) {
            correspondencia.setColunaNomeContatoUrgencia(JavafxUtil.getInteger(this.txtNomeContatoUrgencia) - 1);
        }
        if (!GenericValidator.isBlankOrNull(this.txtParentesco.getText())) {
            correspondencia.setColunaParentescoContatoUrgencia(JavafxUtil.getInteger(this.txtParentesco) - 1);
        }
        if (!GenericValidator.isBlankOrNull(this.txtTelefone1ContUrg.getText())) {
            correspondencia.setColunaTelefone1ContatoUrgencia(JavafxUtil.getInteger(this.txtTelefone1ContUrg) - 1);
        }
        if (!GenericValidator.isBlankOrNull(this.txtTelefone2ContUrg.getText())) {
            correspondencia.setColunaTelefone2ContatoUrgencia(JavafxUtil.getInteger(this.txtTelefone2ContUrg) - 1);
        }
        if (!GenericValidator.isBlankOrNull(this.txtNumeroAtleta.getText())) {
            correspondencia.setColunaNumeroAtleta(JavafxUtil.getInteger(this.txtNumeroAtleta) - 1);
        }

        return correspondencia;
    }

    private Node obterNodeColunaFucused() {
        for (Node node : this.paneColuna.getChildren()) {
            if (node.isFocused()) {
                return node;
            }
        }
        return null;
    }

    @FXML
    public void verificarTipoColunas() {
        if (this.file == null) {
            super.cj.exibirAviso("Selecione um arquivo xls antes de realizar esta operação.");
        } else if (GenericValidator.isBlankOrNull(this.txtLinhaCabecalho.getText())) {
            super.cj.exibirAviso("Informe quantas linhas possui o cabeçalho.");
        } else {
            int numeroLinha = Integer.valueOf(this.txtLinhaCabecalho.getText()) + 1;
            try {
                List<String> tipos = this.importador.verificarTipo(this.file, numeroLinha);
                this.tabXls.getItems().set(numeroLinha - 1, FXCollections.observableArrayList(tipos));
            } catch (NegocioException ex) {
                super.cj.exibirErro("Não foi possivel identificar os tipos das colunas do arquivo excel.", GeralUtil.getMensagemOriginalErro(ex));
            } catch (AvisoNegocioException ex) {
                super.cj.exibirAviso(ex.getMessage());
            }
        }
    }

    @FXML
    public void associarCategorias() {
        if (ValidarCampoUtil.validarCampos(this.txtLinhaCabecalho, this.txtCategoria, this.txtPathArquivo, this.txtNomeProva) && this.prova != null) {
            boolean newWindow = this.correspondencia.getMapCategoriaAssociada() == null || this.correspondencia.getMapCategoriaAssociada().isEmpty();
            this.associarCategoria.setProva(this.prova);
            this.associarCategoria.setFile(this.file);
            this.associarCategoria.setLinhasCabecalho(Integer.valueOf(this.txtLinhaCabecalho.getText()));
            this.associarCategoria.setIndiceColunaCategoriaXls(Integer.valueOf(this.txtCategoria.getText()) - 1);
            super.cj.abrirJanela(Stages.ASSOCIAR_CATEGORIA_XLS.ordinal(), Stages.IMPORTAR_INSCRICAO.ordinal(), IInscricaoImportarAssociarCategoriaController.class, "t_associar_categoria_xls", newWindow);
            this.correspondencia.setMapCategoriaAssociada(this.associarCategoria.getMapCategoriaAssociada());
            this.totalCategoriaXls = this.associarCategoria.getTotalCategoriaXls();

        }
    }

    private boolean isCategoriasAssociadas() {
        return this.totalCategoriaXls != null && this.correspondencia.getMapCategoriaAssociada() != null && this.correspondencia.getMapCategoriaAssociada().size() == this.totalCategoriaXls;
    }

    @Override
    public void setProva(Prova prova) {
        this.prova = prova;
        this.selecionarProva = false;
    }

    private Task criarTarefaExibirPlanilhaView() {
        Task tarefa = new Task() {
            @Override
            protected Object call() throws Exception {
                return InscricaoImportarXlsController.this.importador.carregarXLinhasExcell(InscricaoImportarXlsController.this.file, 10);
            }
        };

        tarefa.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent event) {
                InscricaoImportarXlsController.this.exibirPlanilhaView((List<List<String>>) event.getSource().getValue());
            }
        });
        tarefa.setOnFailed(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent event) {
                InscricaoImportarXlsController.super.cj.exibirErro("Não foi possivel ler o arquivo do excel", GeralUtil.getMensagemOriginalErro(event.getSource().getException()));
                InscricaoImportarXlsController.this.file = null;
            }
        });
        return tarefa;
    }

    private Task criarTarefaImportarArquivo() {
        Task tarefa = new Task() {
            @Override
            protected Object call() throws Exception {
                String log = InscricaoImportarXlsController.this.importador.importarInscricoesFMCEmExcel(InscricaoImportarXlsController.this.file, correspondencia, InscricaoImportarXlsController.this.prova);
                if (log.isEmpty()) {
                    return new ResultadoThread(true, "info_importacao_xls_sucesso", null);
                } else {
                    return new ResultadoThread(false, "aviso_importacao_parcial", log);
                }
            }
        };

        tarefa.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent event) {
                ResultadoThread resultadoThread = (ResultadoThread) event.getSource().getValue();
                if (resultadoThread != null) {
                    if (resultadoThread.isSucesso()) {
                        InscricaoImportarXlsController.super.cj.exibirInformacao(resultadoThread.getMensagem());
                    } else {
                        InscricaoImportarXlsController.super.cj.exibirAviso(resultadoThread.getMensagem(), resultadoThread.getDetalheMensagem());
                    }
                }
            }
        });

        tarefa.setOnFailed(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent event) {
                InscricaoImportarXlsController.super.cj.exibirErro("erro_importacao_xls", GeralUtil.getMensagemOriginalErro(event.getSource().getException()));
            }
        });

        return tarefa;
    }
}
