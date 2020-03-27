package com.taurus.racingTiming.controller.corrida;

import com.taurus.racingTiming.controller.corrida.inscricao.it.ICadastroImportacaoInscricao;
import com.taurus.exception.AvisoNegocioException;
import com.taurus.exception.NegocioException;
import com.taurus.javafx.componente.TableViewDuploClique;
import com.taurus.javafx.controller.FXMLBaseController;
import com.taurus.javafx.converter.GenericComboBoxConverter;
import com.taurus.javafx.ds.GenericLinhaView;
import com.taurus.racingTiming.controller.resultado.IResultadoCronometragemController;
import com.taurus.racingTiming.controller.resultado.ISituacaoAtletaProvaController;
import com.taurus.racingTiming.entidade.corrida.CategoriaDaProva;
import com.taurus.racingTiming.entidade.corrida.Prova;
import com.taurus.racingTiming.entidade.responsavel.RepresentanteFederacao;
import com.taurus.racingTiming.entidade.responsavel.RepresentanteOrganizacaoProva;
import com.taurus.racingTiming.negocio.IJuiz;
import com.taurus.racingTiming.negocio.IRolex;
import com.taurus.racingTiming.negocio.ISecretario;
import com.taurus.racingTiming.pojo.CategoriaProvaResumo;
import com.taurus.racingTiming.pojo.SituacaoProva;
import com.taurus.racingTiming.ui.ds.responsavel.RepresentanteFederacaoDataSource;
import com.taurus.racingTiming.ui.ds.responsavel.RepresentanteOrganizacaoProvaDataSource;
import com.taurus.racingTiming.util.ListaConstantes;
import com.taurus.racingTiming.util.ListaConstantes.Stages;
import com.taurus.racingTiming.util.ListaConstantes.StatusProva;
import com.taurus.util.FormatarUtil;
import com.taurus.util.GeralUtil;
import com.taurus.util.ListaConstantesBase;
import com.taurus.util.annotation.Parametro;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.MenuItem;
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
import com.taurus.racingTiming.controller.corrida.inscricao.it.IInscricaoPendenciaController;
import com.taurus.racingTiming.entidade.atleta.CategoriaAtleta;
import com.taurus.racingTiming.entidade.corrida.Percurso;
import com.taurus.racingTiming.negocio.IImpressor;
import com.taurus.racingTiming.negocio.Impressor;
import com.taurus.racingTiming.util.TarefaThread;
import com.taurus.util.ListaConstantesBase.Sexo;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.concurrent.Task;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Tab;

@Component
public class SituacaoProvaController extends FXMLBaseController implements ISituacaoProvaController {

    private static final Log LOG = LogFactory.getLog(SituacaoProvaController.class);

    @Autowired
    private ISecretario secretario;
    @Autowired
    private IRolex rolex;
    @Autowired
    private IJuiz juiz;
    @Autowired
    private IProvaCategoriaAtletaController provaCategoriaAtletaController;
    @Autowired
    private IResultadoCronometragemController resultadoCronometragemController;
    @Autowired
    private ISituacaoAtletaProvaController situacaoAtletaProvaController;
    @Autowired
    private ICadastroImportacaoInscricao cadastroImportacaoInscricao;
    @Autowired
    private IInscricaoPendenciaController pendenciaInscricaoController;
    @Autowired
    private IImpressor impressor;

    @Parametro
    private SituacaoProva situacaoProva;

    private Prova prova;

    private RepresentanteOrganizacaoProvaDataSource representanteProvaDS;
    private RepresentanteFederacaoDataSource representanteFederacaoDS;

    @FXML
    private MenuItem mnuReabrirInscricao;
    @FXML
    private MenuItem mnuFinalizarInscricao;
    @FXML
    private MenuItem mnuVerificarPendencia;
    @FXML
    private MenuItem mnuImportacao;
    @FXML
    private MenuItem mnuCronoVerificarPendencia;
    @FXML
    private MenuItem mnuReabrirCrono;
    @FXML
    private MenuItem mnuVerTodasCrono;
    @FXML
    private MenuItem mnuDesclassificarAtleta;
    @FXML
    private MenuItem mnuFinalizarProva;
    @FXML
    private MenuItem mnuReabrirApuracao;
    @FXML
    private MenuItem mnuFinalizarApuracao;

    @FXML
    private TextField txtNome;
    @FXML
    private TextField txtData;
    @FXML
    private TextField txtSituacao;
    @FXML
    private TextField txtTotalInscritos;

    @FXML
    private TableView<GenericLinhaView<RepresentanteOrganizacaoProva>> tabOrganizacao;
    @FXML
    private TableColumn<GenericLinhaView, String> colOrganizacao;
    @FXML
    private TableColumn<GenericLinhaView, String> colRepOrganizacao;

    @FXML
    private TableView<GenericLinhaView<RepresentanteFederacao>> tabFederacao;
    @FXML
    private TableColumn<GenericLinhaView, String> colFederacao;
    @FXML
    private TableColumn<GenericLinhaView, String> colRepFederacao;

    @FXML
    private TableView<CategoriaProvaResumo> tabCategoria;
    @FXML
    private TableColumn<CategoriaProvaResumo, String> colNomeCategoria;
    @FXML
    private TableColumn<CategoriaProvaResumo, String> colDescricaoCategoria;
    @FXML
    private TableColumn<CategoriaProvaResumo, Long> colInscritos;

    @FXML
    private ComboBox<TipoRelatorioInscritos> comboTipoRelInscrito;
    @FXML
    private Button btnRelInscrito;
    @FXML
    private RadioButton radioRelInscTodos;
    @FXML
    private RadioButton radioRelInscNumero;
    @FXML
    private RadioButton radioRelInscSemNumero;
    @FXML
    private RadioButton radioRelInscOrdemNumero;
    @FXML
    private RadioButton radioRelInscOrdemNome;

    @FXML
    private Tab tabRelClassificacao;
    @FXML
    private ComboBox<Percurso> comboRelClassificacaoPercurso;
    @FXML
    private Button btnRelClassificacaoGeral;
    @FXML
    private RadioButton radioRelMasculino;
    @FXML
    private RadioButton radioRelFeminino;
    @FXML
    private ComboBox<CategoriaDaProva> comboRelClassificacaoCategoria;
    @FXML
    private Button btnRelClassificacaoCategoria;

    @FXML
    ProgressIndicator indicadorProgresso;

    @Value("${fxml.statusProva.view}")
    @Override
    public void setFxmlFilePath(String filePath) {
        this.fxmlFilePath = filePath;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.configurarTabelaOrganizacao();
        this.configurarTabelaRepresentanteFederacao();
        this.configurarTabelaCategoria();
        this.configurarRelatorioInscritos();
        this.configurarMudancaStatus();
        this.indicadorProgresso.setVisible(false);
    }

    @Override
    public void reinicializarJanelaEspecifico() {
        this.representanteProvaDS = null;
        this.tabCategoria.getItems().clear();
        this.representanteFederacaoDS = null;
    }

    @Override
    public void aoAbrirJanela() {
        this.prova = this.situacaoProva.getProva();
        this.exibirSituacaoProva();
        this.atualizarMenu();
        this.carregarCombos();
        this.configurarLayoutTabClassificacao();
    }

    @Override
    public void aoFecharJanelaEspecifico() {
    }

    private void configurarTabelaOrganizacao() {
        this.colOrganizacao.getStyleClass().add(ListaConstantesBase.EstiloCss.TAB_COL_ESQUERDO.getValor());
        this.colOrganizacao.setCellValueFactory(new PropertyValueFactory("coluna2"));
        this.colRepOrganizacao.getStyleClass().add(ListaConstantesBase.EstiloCss.TAB_COL_ESQUERDO.getValor());
        this.colRepOrganizacao.setCellValueFactory(new PropertyValueFactory("coluna1"));
    }

    private void configurarTabelaCategoria() {
        this.colNomeCategoria.setCellValueFactory((TableColumn.CellDataFeatures<CategoriaProvaResumo, String> linha) -> new ReadOnlyObjectWrapper<>(linha.getValue().getCategoriaDaProva().getCategoriaAtleta().getNome()));
        this.colDescricaoCategoria.setCellValueFactory((TableColumn.CellDataFeatures<CategoriaProvaResumo, String> linha) -> new ReadOnlyObjectWrapper<>(linha.getValue().getCategoriaDaProva().getCategoriaAtleta().getDescricao()));
        this.colInscritos.setCellValueFactory((TableColumn.CellDataFeatures<CategoriaProvaResumo, Long> linha) -> new ReadOnlyObjectWrapper<>(linha.getValue().getInscritos()));

        this.colNomeCategoria.getStyleClass().add(ListaConstantesBase.EstiloCss.TAB_COL_ESQUERDO.getValor());
        this.colDescricaoCategoria.getStyleClass().add(ListaConstantesBase.EstiloCss.TAB_COL_ESQUERDO.getValor());
        this.colInscritos.getStyleClass().add(ListaConstantesBase.EstiloCss.TAB_COL_CENTRO.getValor());
        this.configurarDuploCliqueCategoria();
    }

    private void configurarDuploCliqueCategoria() {
        // Evento duplo clique na tabela
        tabCategoria.setRowFactory(new TableViewDuploClique<CategoriaProvaResumo>(true) {
            @Override
            public CategoriaProvaResumo eventoDuploClique(CategoriaProvaResumo categoriaProvaResumo) {
                SituacaoProvaController.this.provaCategoriaAtletaController.setCategoriaDaProva(categoriaProvaResumo.getCategoriaDaProva());
                situacaoProva.setProva(prova);
                SituacaoProvaController.this.provaCategoriaAtletaController.setSituacaoProva(SituacaoProvaController.this.situacaoProva);
                SituacaoProvaController.super.cj.abrirJanela(ListaConstantes.Stages.PROVA_CATEGORIA_ATLETA.ordinal(), ListaConstantes.Stages.STATUS_PROVA.ordinal(), IProvaCategoriaAtletaController.class, "t_insc_categoria", true);
                try {
                    categoriaProvaResumo.setInscritos(SituacaoProvaController.this.secretario.pesquisarTotalInscricoes(categoriaProvaResumo.getCategoriaDaProva()));
                } catch (NegocioException ex) {
                    SituacaoProvaController.super.cj.exibirErro(cm.getMensagem("erro_control_pesquisar", "categoria_atleta"), GeralUtil.getMensagemOriginalErro(ex));
                }
                return categoriaProvaResumo;
            }
        });
    }

    private void configurarTabelaRepresentanteFederacao() {
        this.colFederacao.getStyleClass().add(ListaConstantesBase.EstiloCss.TAB_COL_ESQUERDO.getValor());
        this.colRepFederacao.getStyleClass().add(ListaConstantesBase.EstiloCss.TAB_COL_ESQUERDO.getValor());

        this.colFederacao.setCellValueFactory(new PropertyValueFactory("coluna2"));
        this.colRepFederacao.setCellValueFactory(new PropertyValueFactory("coluna1"));
    }

    private void configurarMudancaStatus() {
        this.txtSituacao.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            SituacaoProvaController.this.txtSituacao.getStyleClass().clear();
            SituacaoProvaController.this.txtSituacao.getStyleClass().add("text-input");
            SituacaoProvaController.this.txtSituacao.getStyleClass().add("text-field");
            SituacaoProvaController.this.txtSituacao.setTooltip(null);
            switch (this.prova.getStatus()) {
                case INSCRICAO_ABERTA:
                    SituacaoProvaController.this.txtSituacao.getStyleClass().add(ListaConstantesBase.EstiloCss.TEXTO_VERDE.getValor());
                    break;
                case INSCRICAO_ENCERRADA_PENDENTE:
                case ENCERRADA_APURANDO_RESULTADOS:
                    SituacaoProvaController.this.txtSituacao.getStyleClass().add(ListaConstantesBase.EstiloCss.TEXTO_VERMELHO.getValor());
                    SituacaoProvaController.this.txtSituacao.getStyleClass().add(ListaConstantesBase.EstiloCss.TEXTO_NEGRITO.getValor());
                    if (GenericValidator.isBlankOrNull(this.prova.getMotivoPendencia())) {
                        SituacaoProvaController.this.txtSituacao.setTooltip(null);
                    } else {
                        SituacaoProvaController.this.txtSituacao.setTooltip(new Tooltip(this.prova.getMotivoPendencia().replaceAll("\\|", "\n")));
                    }
                    break;
                case INSCRICAO_FECHADA:
                case ENCERRADA_RESULTADOS_CONCLUIDOS:
                    SituacaoProvaController.this.txtSituacao.getStyleClass().add(ListaConstantesBase.EstiloCss.TEXTO_AZUL.getValor());
                    break;
                case FINALIZADA:
                    SituacaoProvaController.this.txtSituacao.getStyleClass().add(ListaConstantesBase.EstiloCss.TEXTO_NEGRITO.getValor());
                    break;
            }
        });
    }

    private void exibirSituacaoProva() {
        try {
            this.prova = this.secretario.carregarTudoProva(this.situacaoProva.getProva());
            this.situacaoProva.setProva(prova);
        } catch (NegocioException ex) {
            cj.exibirErro(cm.getMensagem("erro_control_pesquisar", "prova"), GeralUtil.getMensagemOriginalErro(ex));
            return;
        }
        this.txtNome.setText(prova.getNome());
        this.txtData.setText(FormatarUtil.localDateToString(prova.getData()));
        this.txtSituacao.setText(prova.getStatus().getDescricao());
        this.txtTotalInscritos.setText(this.situacaoProva.getNumeroInscritos().toString());

        // Representante Organizacao de Prova
        this.representanteProvaDS = new RepresentanteOrganizacaoProvaDataSource(new ArrayList(prova.getListaRepresentanteOrganizacao()));
        this.tabOrganizacao.setItems(this.representanteProvaDS.getData());

        // Representante da Federacao
        this.representanteFederacaoDS = new RepresentanteFederacaoDataSource(new ArrayList(prova.getListaRepresentanteFederacao()));
        this.tabFederacao.setItems(this.representanteFederacaoDS.getData());

        // Categoria
        List<CategoriaProvaResumo> categorias = new ArrayList<>();
        for (CategoriaDaProva cp : prova.getListaCategoriaDaProva()) {
            try {
                Long inscritos = this.secretario.pesquisarTotalInscricoes(cp);
                categorias.add(new CategoriaProvaResumo(cp, inscritos));
            } catch (NegocioException ex) {
                super.cj.exibirErro(cm.getMensagem("erro_control_pesquisar", "categoria_atleta"), GeralUtil.getMensagemOriginalErro(ex));
                return;
            }
        }
        this.tabCategoria.getItems().addAll(categorias);
    }

    @Override
    public void setSituacaoProva(SituacaoProva situacaoProva) {
        this.situacaoProva = situacaoProva;
    }

    private void atualizarMenu() {
        switch (this.prova.getStatus()) {
            case FORNO:
                this.mnuReabrirInscricao.setDisable(true);
                this.mnuFinalizarInscricao.setDisable(true);
                this.mnuVerificarPendencia.setDisable(true);
                this.mnuImportacao.setDisable(true);

                this.mnuCronoVerificarPendencia.setDisable(true);
                this.mnuReabrirCrono.setDisable(true);

                this.mnuVerTodasCrono.setDisable(true);
                this.mnuDesclassificarAtleta.setDisable(true);
                this.mnuFinalizarApuracao.setDisable(true);
                this.mnuReabrirApuracao.setDisable(true);
                this.mnuFinalizarProva.setDisable(true);
                break;
            case INSCRICAO_ABERTA:
                this.mnuReabrirInscricao.setDisable(true);
                this.mnuFinalizarInscricao.setDisable(false);
                this.mnuVerificarPendencia.setDisable(false);
                this.mnuImportacao.setDisable(false);

                this.mnuCronoVerificarPendencia.setDisable(true);
                this.mnuReabrirCrono.setDisable(true);

                this.mnuVerTodasCrono.setDisable(true);
                this.mnuDesclassificarAtleta.setDisable(true);
                this.mnuFinalizarApuracao.setDisable(true);
                this.mnuReabrirApuracao.setDisable(true);
                this.mnuFinalizarProva.setDisable(true);
                break;
            case INSCRICAO_ENCERRADA_PENDENTE:
                this.mnuReabrirInscricao.setDisable(false);
                this.mnuFinalizarInscricao.setDisable(true);
                this.mnuVerificarPendencia.setDisable(false);
                this.mnuImportacao.setDisable(true);

                this.mnuCronoVerificarPendencia.setDisable(true);
                this.mnuReabrirCrono.setDisable(true);

                this.mnuVerTodasCrono.setDisable(true);
                this.mnuDesclassificarAtleta.setDisable(true);
                this.mnuFinalizarApuracao.setDisable(true);
                this.mnuReabrirApuracao.setDisable(true);
                this.mnuFinalizarProva.setDisable(true);
                break;
            case INSCRICAO_FECHADA:
                this.mnuReabrirInscricao.setDisable(false);
                this.mnuFinalizarInscricao.setDisable(true);
                this.mnuVerificarPendencia.setDisable(true);
                this.mnuImportacao.setDisable(true);

                this.mnuCronoVerificarPendencia.setDisable(true);
                this.mnuReabrirCrono.setDisable(true);

                this.mnuVerTodasCrono.setDisable(true);
                this.mnuDesclassificarAtleta.setDisable(true);
                this.mnuFinalizarApuracao.setDisable(true);
                this.mnuReabrirApuracao.setDisable(true);
                this.mnuFinalizarProva.setDisable(true);
                break;
            case CRONOMETRANDO_PARCIAL_LARGADA:
            case CRONOMETRANDO_TODAS_LARGADAS:
                this.mnuReabrirInscricao.setDisable(true);
                this.mnuFinalizarInscricao.setDisable(true);
                this.mnuVerificarPendencia.setDisable(true);
                this.mnuImportacao.setDisable(true);

                this.mnuCronoVerificarPendencia.setDisable(true);
                this.mnuReabrirCrono.setDisable(true);

                this.mnuVerTodasCrono.setDisable(false);
                this.mnuDesclassificarAtleta.setDisable(true);
                this.mnuFinalizarApuracao.setDisable(true);
                this.mnuReabrirApuracao.setDisable(true);
                this.mnuFinalizarProva.setDisable(true);
                break;
            case ENCERRADA_APURANDO_RESULTADOS:
                this.mnuReabrirInscricao.setDisable(true);
                this.mnuFinalizarInscricao.setDisable(true);
                this.mnuVerificarPendencia.setDisable(true);
                this.mnuImportacao.setDisable(true);

                this.mnuCronoVerificarPendencia.setDisable(false);
                this.mnuReabrirCrono.setDisable(false);

                this.mnuVerTodasCrono.setDisable(false);
                this.mnuDesclassificarAtleta.setDisable(false);
                this.mnuFinalizarApuracao.setDisable(false);
                this.mnuReabrirApuracao.setDisable(true);
                this.mnuFinalizarProva.setDisable(true);
                break;
            case ENCERRADA_RESULTADOS_CONCLUIDOS:
                this.mnuReabrirInscricao.setDisable(true);
                this.mnuFinalizarInscricao.setDisable(true);
                this.mnuVerificarPendencia.setDisable(true);
                this.mnuImportacao.setDisable(true);

                this.mnuCronoVerificarPendencia.setDisable(true);
                this.mnuReabrirCrono.setDisable(true);

                this.mnuVerTodasCrono.setDisable(false);
                this.mnuDesclassificarAtleta.setDisable(true);
                this.mnuFinalizarApuracao.setDisable(true);
                this.mnuReabrirApuracao.setDisable(false);
                this.mnuFinalizarProva.setDisable(false);
                break;
            case FINALIZADA:
                this.mnuReabrirInscricao.setDisable(true);
                this.mnuFinalizarInscricao.setDisable(true);
                this.mnuVerificarPendencia.setDisable(true);
                this.mnuImportacao.setDisable(true);

                this.mnuCronoVerificarPendencia.setDisable(true);
                this.mnuReabrirCrono.setDisable(true);

                this.mnuVerTodasCrono.setDisable(false);
                this.mnuDesclassificarAtleta.setDisable(true);
                this.mnuReabrirApuracao.setDisable(true);
                this.mnuFinalizarProva.setDisable(true);
                break;
            case CANCELADA:
                this.mnuReabrirInscricao.setDisable(true);
                this.mnuFinalizarInscricao.setDisable(true);
                this.mnuVerificarPendencia.setDisable(true);
                this.mnuImportacao.setDisable(true);

                this.mnuCronoVerificarPendencia.setDisable(true);
                this.mnuReabrirCrono.setDisable(true);

                this.mnuVerTodasCrono.setDisable(true);
                this.mnuDesclassificarAtleta.setDisable(true);
                this.mnuReabrirApuracao.setDisable(true);
                this.mnuFinalizarProva.setDisable(true);
                break;
        }
    }

    @FXML
    public void reabrirInscricao() {
        if (!this.prova.getStatus().equals(StatusProva.ENCERRADA_APURANDO_RESULTADOS)) {
            this.prova.setStatus(StatusProva.INSCRICAO_ABERTA);
            try {
                this.secretario.atualizarProva(this.prova);
            } catch (NegocioException ex) {
                super.cj.exibirErro(cm.getMensagem("erro_control_atualizar", "prova"), GeralUtil.getMensagemOriginalErro(ex));
                return;
            }
            this.atualizarMenuStatusProva(StatusProva.INSCRICAO_ABERTA);
            super.cj.exibirInformacao("info_op_sucesso");
        }
    }

    @FXML
    public void finalizarInscricao() {
        if (this.prova.getStatus().equals(StatusProva.INSCRICAO_ABERTA)
                || this.prova.getStatus().equals(StatusProva.INSCRICAO_ENCERRADA_PENDENTE)) {
            boolean possuiPendencia;
            try {
                possuiPendencia = this.secretario.finalizarInscricaoProva(this.prova);
            } catch (NegocioException ex) {
                super.cj.exibirErro(cm.getMensagem("erro_control_atualizar", "prova"), GeralUtil.getMensagemOriginalErro(ex));
                return;
            } catch (AvisoNegocioException ex) {
                super.cj.exibirAviso(ex.getMessage());
                return;
            }
            if (!possuiPendencia) {
                super.cj.exibirInformacao("info_op_sucesso");
            } else {
                super.cj.exibirAviso("aviso_pendencia_inscricao", this.prova.getMotivoPendencia().replaceAll("\\|", "\n"));
            }
            this.atualizarMenuStatusProva(this.prova.getStatus());

        }
    }

    private void atualizarMenuStatusProva(StatusProva status) {
        this.txtSituacao.setText(status.getDescricao());
        this.atualizarMenu();
    }

    @FXML
    public void verificarPendenciaCronometragem(ActionEvent actionEvent) {
        try {
            this.prova = this.rolex.verificarInconsitenciasCronometragem(this.prova);
            if (GenericValidator.isBlankOrNull(this.prova.getMotivoPendencia())) {
                super.cj.exibirInformacao("info_crono_sem_pendencia");
            } else {
                super.cj.exibirAviso("aviso_crono_pendencia", this.prova.getMotivoPendencia().replaceAll("\\|", "\n"));
            }
        } catch (NegocioException ex) {
            super.cj.exibirErro(cm.getMensagem("erro_control_atualizar", "prova"), GeralUtil.getMensagemOriginalErro(ex));
        } catch (AvisoNegocioException ex) {
            super.cj.exibirAviso(ex.getMessage());
        }
    }

    @FXML
    public void reabrirCronometragem(ActionEvent actionEvent) {
        try {
            this.prova = this.juiz.reabrirCronometragem(this.prova);
            super.cj.exibirInformacao("Cronometragem da prova reaberta com sucesso.");
            this.atualizarMenuStatusProva(this.prova.getStatus());
        } catch (NegocioException ex) {
            super.cj.exibirErro(cm.getMensagem("erro_control_atualizar", "prova"), GeralUtil.getMensagemOriginalErro(ex));
        } catch (AvisoNegocioException ex) {
            super.cj.exibirAviso(ex.getMessage());
        }
    }

    @FXML
    public void abrirSituacaoAtleta(ActionEvent actionEvent) {
        this.situacaoAtletaProvaController.setProva(this.prova);
        super.cj.abrirJanela(Stages.STATUS_ATLETA_PROVA.ordinal(), Stages.STATUS_PROVA.ordinal(), ISituacaoAtletaProvaController.class, "Situação do Atleta", true);
    }

    @FXML
    public void abrirTodasCrono(ActionEvent actionEvent) {
        this.resultadoCronometragemController.setProva(this.prova);
        super.cj.abrirJanela(ListaConstantes.Stages.RESULTADO_CRONOMETRAGEM.ordinal(), ListaConstantes.Stages.STATUS_PROVA.ordinal(), IResultadoCronometragemController.class, "t_result_crono", true);
    }

    @FXML
    public void abrirPendenciaInscricao(ActionEvent actionEvent) {
        this.pendenciaInscricaoController.setProva(this.prova);
        super.cj.abrirJanela(Stages.PENDENCIA_INSCRICAO.ordinal(), Stages.STATUS_PROVA.ordinal(), IInscricaoPendenciaController.class, "t_pendencia_inscricao", true);
        this.atualizarMenuStatusProva(this.prova.getStatus());
    }

    @FXML
    public void finalizarApuracao(ActionEvent actionEvent) {
        try {
            this.prova = this.juiz.finalizarApuracao(this.prova);
            super.cj.exibirInformacao("Finalizada apuração, classificação disponível.");
            this.atualizarMenuStatusProva(this.prova.getStatus());
        } catch (NegocioException ex) {
            super.cj.exibirErro(cm.getMensagem("erro_control_atualizar", "prova"), GeralUtil.getMensagemOriginalErro(ex));
        } catch (AvisoNegocioException ex) {
            super.cj.exibirAviso(ex.getMessage());
        }
    }

    @FXML
    public void reabrirApuracao(ActionEvent actionEvent) {
        try {
            this.prova = this.juiz.reabrirApuracao(this.prova);
            super.cj.exibirInformacao("Apuração da prova reaberta com sucesso.");
            this.atualizarMenuStatusProva(this.prova.getStatus());
        } catch (NegocioException ex) {
            super.cj.exibirErro(cm.getMensagem("erro_control_atualizar", "prova"), GeralUtil.getMensagemOriginalErro(ex));
        } catch (AvisoNegocioException ex) {
            super.cj.exibirAviso(ex.getMessage());
        }
    }

    @FXML
    public void finalizarProva(ActionEvent actionEvent) {
        try {
            this.prova = this.juiz.finalizarProva(this.prova);
            super.cj.exibirInformacao("A prova foi finalizada com sucesso.");
            this.atualizarMenuStatusProva(this.prova.getStatus());
        } catch (NegocioException ex) {
            super.cj.exibirErro(cm.getMensagem("erro_control_atualizar", "prova"), GeralUtil.getMensagemOriginalErro(ex));
        } catch (AvisoNegocioException ex) {
            super.cj.exibirAviso(ex.getMessage());
        }
    }

    @FXML
    public void abrirCadastroImportacao(ActionEvent actionEvent) {
        this.cadastroImportacaoInscricao.setProva(this.prova);
        super.cj.abrirJanela(Stages.CADASTRO_IMPORTAR_INSCRICAO.ordinal(), Stages.STATUS_PROVA.ordinal(), ICadastroImportacaoInscricao.class, "Importações de inscrições da prova", true);
        this.aoAbrirJanela();
    }

    private void configurarRelatorioInscritos() {
        this.comboTipoRelInscrito.setConverter(new GenericComboBoxConverter<>("descricao"));
        this.comboTipoRelInscrito.getItems().addAll(TipoRelatorioInscritos.values());
        this.comboRelClassificacaoPercurso.setConverter(new GenericComboBoxConverter<>("nome"));
        this.comboRelClassificacaoCategoria.setConverter(new GenericComboBoxConverter<>("categoriaAtleta.nome"));

        this.btnRelInscrito.getStyleClass().add(ListaConstantesBase.EstiloCss.BOTAO_PRINT.getValor());
        this.btnRelInscrito.setTooltip(new Tooltip("Gerar relatório"));
        this.btnRelClassificacaoGeral.getStyleClass().add(ListaConstantesBase.EstiloCss.BOTAO_PRINT.getValor());
        this.btnRelClassificacaoGeral.setTooltip(new Tooltip("Gerar relatório"));
        this.btnRelClassificacaoCategoria.getStyleClass().add(ListaConstantesBase.EstiloCss.BOTAO_PRINT.getValor());
        this.btnRelClassificacaoCategoria.setTooltip(new Tooltip("Gerar relatório"));
    }

    private void carregarCombos() {
        try {
            this.comboRelClassificacaoPercurso.getItems().clear();
            this.comboRelClassificacaoPercurso.getItems().addAll(this.secretario.pesquisarPercurso(this.prova));
            this.comboRelClassificacaoCategoria.getItems().clear();
            this.comboRelClassificacaoCategoria.getItems().add(this.criarCategoriaTodos());
            this.comboRelClassificacaoCategoria.getItems().addAll(this.secretario.pesquisarCategoriaDaProva(this.prova));
        } catch (NegocioException ex) {
            this.cj.exibirErro(cm.getMensagem("erro_control_pesquisar", "Percurso ou Classificação"), GeralUtil.getMensagemOriginalErro(ex));
        }
    }

    @FXML
    public void criarRelClassificacaoGeral(ActionEvent actionEvent) {
        Sexo sexo = this.radioRelMasculino.isSelected() ? ListaConstantesBase.Sexo.MASCULINO : ListaConstantesBase.Sexo.FEMININO;
        if (this.comboRelClassificacaoPercurso.getValue() != null) {
            try {
                this.impressor.gerarRelatorioClassificacaoProvaGeral(this.comboRelClassificacaoPercurso.getValue(), sexo);
                super.cj.exibirInformacao("info_rpt_concluido");
            } catch (NegocioException ex) {
                this.cj.exibirErro(cm.getMensagem("erro_rpt_geral", sexo.getDescricao(), this.comboRelClassificacaoPercurso.getValue().getNome()), GeralUtil.getMensagemOriginalErro(ex));
            }
        }
    }

    @FXML
    public void criarRelClassificacaoCategoria(ActionEvent actionEvent) {
        if (this.comboRelClassificacaoCategoria.getValue() != null) {
            String msgErro = cm.getMensagem("erro_rpt_classificacao_cat", this.comboRelClassificacaoCategoria.getValue().getCategoriaAtleta().getNome());
            Task<byte[]> tarefa = new TarefaThread<byte[]>("info_rpt_concluido", msgErro, super.cj) {
                @Override
                protected byte[] call() throws Exception {
                    if (SituacaoProvaController.this.comboRelClassificacaoCategoria.getValue().getId() == null) {
                        SituacaoProvaController.this.impressor.gerarRelatorioClassificacaoProvaTodasCategorias(prova);
                        return null;
                    } else {
                        return SituacaoProvaController.this.impressor.gerarRelatorioClassificacaoProvaCategoria(SituacaoProvaController.this.comboRelClassificacaoCategoria.getValue());
                    }
                }
            };
            this.indicadorProgresso.visibleProperty().unbind();
            this.indicadorProgresso.visibleProperty().bind(tarefa.runningProperty());

            new Thread(tarefa).start();
        }
    }

    @FXML
    public void criarRelInscritos(ActionEvent actionEvent) {
        if (this.comboTipoRelInscrito.getValue() != null) {
            Boolean numeracao = this.radioRelInscTodos.isSelected() ? null : this.radioRelInscNumero.isSelected();
            Impressor.OrdenacaoInscritos ordenacao = this.radioRelInscOrdemNome.isSelected() ? Impressor.OrdenacaoInscritos.NOME_ATLETA : Impressor.OrdenacaoInscritos.NUMERO_ATLETA;

            Task<byte[]> tarefa = new TarefaThread<byte[]>("info_rpt_concluido", "erro_rpt_inscritos", super.cj) {
                @Override
                protected byte[] call() throws Exception {

                    if (SituacaoProvaController.this.comboTipoRelInscrito.getValue() == TipoRelatorioInscritos.TODOS) {
                        return SituacaoProvaController.this.impressor.gerarRelatorioInscritosGeral(SituacaoProvaController.this.prova, numeracao, ordenacao);
                    } else {
                        return SituacaoProvaController.this.impressor.gerarRelatorioInscritosPorCategoria(SituacaoProvaController.this.prova, null, numeracao, ordenacao);
                    }
                }
            };
            this.indicadorProgresso.visibleProperty().unbind();
            this.indicadorProgresso.visibleProperty().bind(tarefa.runningProperty());

            new Thread(tarefa).start();
        }
    }

    private void configurarLayoutTabClassificacao() {
        this.radioRelInscTodos.setSelected(true);
        this.radioRelInscOrdemNumero.setSelected(true);
        this.radioRelMasculino.setSelected(true);

        switch (this.prova.getStatus()) {
            case ENCERRADA_RESULTADOS_CONCLUIDOS:
            case FINALIZADA:
                this.tabRelClassificacao.setDisable(false);
                break;
            default:
                this.tabRelClassificacao.setDisable(true);
        }
    }

    private CategoriaDaProva criarCategoriaTodos() {
        CategoriaDaProva cp = new CategoriaDaProva();
        cp.setCategoriaAtleta(new CategoriaAtleta());
        cp.getCategoriaAtleta().setNome("Todas categorias");
        return cp;
    }

    public static enum TipoRelatorioInscritos {
        TODOS("Geral: todos atletas"),
        POR_CATEGORIA("Por categoria");
        private String descricao;

        private TipoRelatorioInscritos(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }
    }
//cm.getMensagem("erro_rpt_classificacao_cat", this.comboCategoria.getValue().getCategoriaAtleta().getNome())

//    private abstract class TaskRelatorio<V> extends Task<V> {
//
//        private String msgErro;
//
//        public TaskRelatorio(String msgErro) {
//            super();
//            this.msgErro = msgErro;
//
//            this.setOnSucceeded((WorkerStateEvent event) -> {
//                SituacaoProvaController.super.cj.exibirInformacao("info_rpt_concluido");
//            });
//
//            this.setOnFailed((WorkerStateEvent event) -> {
//                SituacaoProvaController.super.cj.exibirErro(msgErro, GeralUtil.getMensagemOriginalErro(event.getSource().getException()));
//            });
//        }
//    }
}
