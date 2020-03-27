package com.taurus.racingTiming.controller.crono;

import com.taurus.exception.AvisoNegocioException;
import com.taurus.exception.NegocioException;
import com.taurus.javafx.componente.BotaoCelulaTableValueFactory;
import com.taurus.javafx.componente.BotaoCondicionalTableColumnFactory;
import com.taurus.javafx.componente.BotaoVariavelTableColumnFactory;
import com.taurus.javafx.componente.CheckBoxTableColumnFactory;
import com.taurus.javafx.componente.TableViewCliqueSimples;
import com.taurus.javafx.componente.TableViewDuploClique;
import com.taurus.javafx.controller.FXMLBaseController;
import com.taurus.javafx.converter.GenericComboBoxConverter;
import com.taurus.javafx.ds.GenericLinhaView;
import com.taurus.javafx.util.JavafxUtil;
import com.taurus.racingTiming.entidade.atleta.Atleta;
import com.taurus.racingTiming.entidade.atleta.CategoriaAtleta;
import com.taurus.racingTiming.entidade.corrida.AtletaInscricao;
import com.taurus.racingTiming.entidade.corrida.CategoriaDaProva;
import com.taurus.racingTiming.entidade.corrida.Cronometragem;
import com.taurus.racingTiming.entidade.corrida.Largada;
import com.taurus.racingTiming.entidade.corrida.Percurso;
import com.taurus.racingTiming.entidade.corrida.Prova;
import com.taurus.racingTiming.negocio.IJuiz;
import com.taurus.racingTiming.negocio.IRolex;
import com.taurus.racingTiming.negocio.ISecretario;
import com.taurus.racingTiming.util.ListaConstantes;
import com.taurus.racingTiming.util.ListaConstantes.PendenciaProvaCrono;
import com.taurus.racingTiming.util.ListaConstantes.Stages;
import com.taurus.util.FormatarUtil;
import com.taurus.util.GeralUtil;
import com.taurus.util.ListaConstantesBase;
import com.taurus.util.annotation.Parametro;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
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
public class ProvaCronoPendenciaCrontroller extends FXMLBaseController implements IProvaCronoPendenciaController {

    private static final Log LOG = LogFactory.getLog(ProvaCronoPendenciaCrontroller.class);

    public ProvaCronoPendenciaCrontroller() {
        super();
    }

    @Autowired
    private ICronometragemEdicaoController cronometragemEdicaoController;
    @Autowired
    private ICronometragemAdicionarAtletaController cronometragemAdicionarAtletaController;

    @Autowired
    private ISecretario secretario;
    @Autowired
    private IRolex rolex;
    @Autowired
    private IJuiz juiz;

    @Parametro
    private Prova prova;

    @FXML
    private TextField txtNomeProva;
    @FXML
    private TextField txtDataProva;
    @FXML
    private TextField txtSituacaoProva;
    @FXML
    private ComboBox<PendenciaProvaCrono> comboPendencia;
    @FXML
    private Button btnPendencia;
    @FXML
    private Button btnDnf;

    @FXML
    private TableView<GenericLinhaView<AtletaInscricao>> tabAtleta;
    @FXML
    private TableColumn<GenericLinhaView<AtletaInscricao>, Boolean> colCheckBox;
    @FXML
    private TableColumn<GenericLinhaView<AtletaInscricao>, String> colNumero;
    @FXML
    private TableColumn<GenericLinhaView<AtletaInscricao>, String> colAtleta;
    @FXML
    private TableColumn<GenericLinhaView<AtletaInscricao>, String> colCategoria;
    @FXML
    private TableColumn<GenericLinhaView<AtletaInscricao>, String> colLargada;
    @FXML
    private TableColumn<GenericLinhaView<AtletaInscricao>, Integer> colAtletaVoltas;
    @FXML
    private TableColumn colAddAtleta;
    @FXML
    private TableColumn colExclusaoCronoTudo;

    @FXML
    private TableView<Cronometragem> tabCrono;
    @FXML
    private TableColumn<Cronometragem, String> colNumeroCrono;
    @FXML
    private TableColumn<Cronometragem, String> colAtletaCrono;
    @FXML
    private TableColumn<Cronometragem, String> colHora;
    @FXML
    private TableColumn<Cronometragem, String> colTempo;
    @FXML
    private TableColumn<Cronometragem, String> colVolta;
    @FXML
    private TableColumn colCronoStatus;
    @FXML
    private TableColumn colCronoOperacao;

    @Value("${fxml.provaCronoPendencia.view}")
    @Override
    public void setFxmlFilePath(String filePath) {
        this.fxmlFilePath = filePath;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        this.comboPendencia.setConverter(new GenericComboBoxConverter<>("descricao"));
        this.comboPendencia.valueProperty().addListener((ObservableValue<? extends PendenciaProvaCrono> observable, PendenciaProvaCrono oldValue, PendenciaProvaCrono newValue) -> {
            if (newValue != null && newValue != oldValue) {
                ProvaCronoPendenciaCrontroller.this.filtrarAtletas();
            }
        });

        this.btnPendencia.getStyleClass().add(ListaConstantes.EstiloCss.BOTAO_PESQUISAR_COMPUTADOR.getValor());
        this.btnPendencia.setTooltip(new Tooltip("verificar inconsistências de cronometragem"));
        this.btnDnf.getStyleClass().add(ListaConstantes.EstiloCss.BOTAO_NAO_BANDEIRA_CHEGADA.getValor());
        this.btnDnf.setTooltip(new Tooltip("marcar como D.N.F."));

        this.configurarTabelaAtletaColunas();
        this.configurarTabelaCronoColunas();
    }

    @Override
    public void reinicializarJanelaEspecifico() {
        this.comboPendencia.getItems().clear();

        this.tabAtleta.getItems().clear();
        this.tabCrono.getItems().clear();
    }

    @Override
    public void aoAbrirJanela() {
        this.comboPendencia.getItems().addAll(PendenciaProvaCrono.values());
        this.txtNomeProva.setText(this.prova.getNome());
        this.txtDataProva.setText(FormatarUtil.localDateToString(this.prova.getData()));
        this.txtSituacaoProva.setText(this.prova.getStatus().getDescricao());
        this.comboPendencia.requestFocus();
    }

    @Override
    public void aoFecharJanelaEspecifico() {
    }

    private void filtrarAtletas() {
        this.colCheckBox.setVisible(false);
        this.btnDnf.setVisible(false);
        try {
            List<AtletaInscricao> atletas = new ArrayList<>();
            switch (this.comboPendencia.getValue()) {
                case ATLETA_NAO_COMPLETOU:
                    atletas = this.rolex.pesquisarAtletaProva(this.prova, ListaConstantes.StatusAtletaCorrida.DNF);
                    this.colCheckBox.setVisible(true);
                    this.btnDnf.setVisible(true);
                    break;
                case ATLETA_VOLTA_MAIS:
                    atletas = this.rolex.pesquisarAtletaProva(this.prova, ListaConstantes.StatusAtletaCorrida.VOLTA_A_MAIS);
                    break;
                case ATLETA_SEM_INSCRICAO:
                    atletas = this.criarListaAtletas(this.rolex.pesquisarCronometragemSemInscricaoStatus(this.prova, ListaConstantes.StatusCronometragem.ATIVA));
                    break;
                case CRONO_ANTERIOR_LARGADA:
                    atletas = this.rolex.pesquisarAtletaProvaComCronometragemAtivaAnteriorLargada(this.prova);
                    break;
                case CRONO_DUVIDA:
                    atletas = this.criarListaAtletas(this.rolex.pesquisarCronometragemPorNumeroAtletaStatus(this.prova, null, ListaConstantes.StatusCronometragem.DUVIDA));
                    break;
            }
            this.tabAtleta.getItems().clear();
            this.tabAtleta.getItems().addAll(GenericLinhaView.toList(atletas));
            this.tabCrono.getItems().clear();

        } catch (NegocioException ex) {
            super.cj.exibirErro(cm.getMensagem("erro_control_pesquisar", "atleta"), GeralUtil.getMensagemOriginalErro(ex));
        }
    }

    private void configurarTabelaAtletaColunas() {
        this.colNumero.setCellValueFactory(linha -> new ReadOnlyObjectWrapper(linha.getValue().getBean().getNumeroAtleta()));
        this.colAtleta.setCellValueFactory(linha -> new ReadOnlyObjectWrapper(linha.getValue().getBean().getAtleta().getNome()));
        this.colCategoria.setCellValueFactory(linha -> new ReadOnlyObjectWrapper(linha.getValue().getBean().getCategoria().getCategoriaAtleta().getNome()));
        this.colLargada.setCellValueFactory(linha -> new ReadOnlyObjectWrapper(linha.getValue().getBean().getCategoria().getLargada().getNome()));
        this.colAtletaVoltas.setCellValueFactory(linha -> new ReadOnlyObjectWrapper(linha.getValue().getBean().getCategoria().getPercurso().getNumeroVolta()));

        this.colNumero.getStyleClass().add(ListaConstantesBase.EstiloCss.TAB_COL_CENTRO.getValor());
        this.colAtleta.getStyleClass().add(ListaConstantesBase.EstiloCss.TAB_COL_ESQUERDO.getValor());
        this.colCategoria.getStyleClass().add(ListaConstantesBase.EstiloCss.TAB_COL_ESQUERDO.getValor());
        this.colLargada.getStyleClass().add(ListaConstantesBase.EstiloCss.TAB_COL_CENTRO.getValor());
        this.colAtletaVoltas.getStyleClass().add(ListaConstantesBase.EstiloCss.TAB_COL_CENTRO.getValor());

        this.colCheckBox.getStyleClass().add(ListaConstantesBase.EstiloCss.TAB_COL_CENTRO.getValor());
        this.colCheckBox.setGraphic(JavafxUtil.criarSelectAllCheckBox(this.tabAtleta.getItems()));
        this.colCheckBox.setCellValueFactory(new PropertyValueFactory<>("selecionar"));
        this.colCheckBox.setCellFactory(new CheckBoxTableColumnFactory<GenericLinhaView<AtletaInscricao>>() {
            @Override
            public void eventoCheckBox(ActionEvent actionEvent, GenericLinhaView<AtletaInscricao> linhaView) {
            }

            @Override
            protected boolean exibirItem(GenericLinhaView<AtletaInscricao> linhaView) {
                return true;
            }
        });
        /* this.colCheckBox.setCellFactory(new Callback<TableColumn<GenericLinhaView<AtletaInscricao>, Boolean>, TableCell<GenericLinhaView<AtletaInscricao>, Boolean>>() {
            public TableCell<GenericLinhaView<AtletaInscricao>, Boolean> call(TableColumn<GenericLinhaView<AtletaInscricao>, Boolean> p) {
                final TableCell<GenericLinhaView<AtletaInscricao>, Boolean> cell = new TableCell<GenericLinhaView<AtletaInscricao>, Boolean>() {
                    @Override
                    public void updateItem(final Boolean item, boolean empty) {
                        super.updateItem(item, empty);
                        if (!isEmpty()) {
                            GenericLinhaView linhaView = getTableView().getItems().get(getIndex());
                            CheckBox checkBox = new CheckBox();
                            checkBox.selectedProperty().bindBidirectional(linhaView.selectedProperty());
                            // checkBox.setOnAction(event);
                            setGraphic(checkBox);
                        }
                    }
                };
                cell.setAlignment(Pos.CENTER);
                return cell;
            }
        });
         */
        this.colAddAtleta.getStyleClass().add(ListaConstantesBase.EstiloCss.TAB_COL_CENTRO.getValor());
        this.colAddAtleta.setCellValueFactory(new BotaoCelulaTableValueFactory());
        this.colAddAtleta.setCellFactory(new BotaoVariavelTableColumnFactory<GenericLinhaView<AtletaInscricao>>() {
            @Override
            public void eventoBotao(ActionEvent actionEvent, GenericLinhaView<AtletaInscricao> linhaView) {
                if (linhaView.getBean().getId() == null) {
                    ProvaCronoPendenciaCrontroller.this.adicionarAtletaTodasCronometragens(linhaView);
                }
            }

            @Override
            protected boolean exibirItem(GenericLinhaView<AtletaInscricao> linhaView) {
                return linhaView.getBean().getId() == null;
            }

            @Override
            public BotaoVariavelTableColumnFactory.DadosBotao getDadosBotao(GenericLinhaView<AtletaInscricao> linhaView) {
                if (linhaView.getBean().getId() == null) {
                    return new BotaoVariavelTableColumnFactory.DadosBotao(ListaConstantesBase.EstiloCss.BOTAO_ADICIONAR.getValor(), "adicionar atleta");
                } else {
                    return null;
                }
            }
        });

        this.colExclusaoCronoTudo.getStyleClass().add(ListaConstantesBase.EstiloCss.TAB_COL_CENTRO.getValor());
        this.colExclusaoCronoTudo.setCellValueFactory(new BotaoCelulaTableValueFactory());
        this.colExclusaoCronoTudo.setCellFactory(new BotaoCondicionalTableColumnFactory<GenericLinhaView<AtletaInscricao>>("excluir todas as cronometragens do atleta", ListaConstantes.EstiloCss.BOTAO_CRONOMETRO_REMOVER.getValor()) {
            @Override
            protected boolean exibirItem(GenericLinhaView<AtletaInscricao> linhaView) {
                return linhaView.getBean().getId() == null;
            }

            @Override
            public void eventoBotao(ActionEvent actionEvent, GenericLinhaView<AtletaInscricao> linhaView) {
                excluirTodasCronometragem(linhaView);
            }
        });

        // Evento clique na tabela
        this.tabAtleta.setRowFactory(new TableViewCliqueSimples<GenericLinhaView<AtletaInscricao>>() {
            @Override
            public void eventoClique(GenericLinhaView<AtletaInscricao> linhaView) {
                filtrarCronometragem(linhaView.getBean().getNumeroAtleta());
            }
        });
    }

    private void configurarTabelaCronoColunas() {
        this.colNumeroCrono.setCellValueFactory(linha -> new ReadOnlyObjectWrapper(linha.getValue().getNumeroAtleta()));
        this.colAtletaCrono.setCellValueFactory(linha -> new ReadOnlyObjectWrapper(linha.getValue().getAtletaInscricao() == null ? null : linha.getValue().getAtletaInscricao().getAtleta().getNome()));
        this.colHora.setCellValueFactory(linha -> new ReadOnlyObjectWrapper(FormatarUtil.localDateTimeToString(linha.getValue().getHoraRegistro(), FormatarUtil.FORMATO_DATA_HORA_MILI)));
        this.colTempo.setCellValueFactory(linha -> new ReadOnlyObjectWrapper(linha.getValue().getTempoVolta() == null ? null : FormatarUtil.formatarTempoMilisegundos(linha.getValue().getTempoVolta())));
        this.colVolta.setCellValueFactory(linha -> new ReadOnlyObjectWrapper(linha.getValue().getVolta() == null ? null : linha.getValue().getTempoVolta()));

        this.colNumeroCrono.getStyleClass().add(ListaConstantesBase.EstiloCss.TAB_COL_CENTRO.getValor());
        this.colAtletaCrono.getStyleClass().add(ListaConstantesBase.EstiloCss.TAB_COL_ESQUERDO.getValor());
        this.colHora.getStyleClass().add(ListaConstantesBase.EstiloCss.TAB_COL_CENTRO.getValor());
        this.colTempo.getStyleClass().add(ListaConstantesBase.EstiloCss.TAB_COL_CENTRO.getValor());
        this.colVolta.getStyleClass().add(ListaConstantesBase.EstiloCss.TAB_COL_CENTRO.getValor());

        this.colCronoStatus.getStyleClass().add(ListaConstantesBase.EstiloCss.TAB_COL_CENTRO.getValor());
        this.colCronoStatus.setCellValueFactory(new BotaoCelulaTableValueFactory());
        this.colCronoStatus.setCellFactory(new BotaoVariavelTableColumnFactory<Cronometragem>() {
            @Override
            public BotaoVariavelTableColumnFactory.DadosBotao getDadosBotao(Cronometragem cronometragem) {
                if (cronometragem.isExcluida()) {
                    return new DadosBotao(ListaConstantesBase.EstiloCss.BOTAO_FALHA.getValor(), "Cronometragem desconsiderada");
                } else if (cronometragem.isDuvida()) {
                    return new DadosBotao(ListaConstantesBase.EstiloCss.BOTAO_DUVIDA.getValor(), "Cronometragem com marcação de dúvida");
                } else {
                    return new DadosBotao(ListaConstantesBase.EstiloCss.BOTAO_OK.getValor(), "Cronometragem válida");
                }
            }

            @Override
            public void eventoBotao(ActionEvent actionEvent, Cronometragem cronometragem) {
            }

            @Override
            protected boolean exibirItem(Cronometragem cronometragem) {
                return true;
            }
        }
        );

        this.colCronoOperacao.getStyleClass().add(ListaConstantesBase.EstiloCss.TAB_COL_CENTRO.getValor());
        this.colCronoOperacao.setCellValueFactory(new BotaoCelulaTableValueFactory());
        this.colCronoOperacao.setCellFactory(new BotaoVariavelTableColumnFactory<Cronometragem>() {
            @Override
            protected boolean exibirItem(Cronometragem linhaView) {
                return true;
            }

            @Override
            public void eventoBotao(ActionEvent actionEvent, Cronometragem cronometragem) {
                if (cronometragem.isExcluida()) {
                    ativarCronometragem(cronometragem);
                } else {
                    excluirCronometragem(cronometragem);
                }
            }

            @Override
            public BotaoVariavelTableColumnFactory.DadosBotao getDadosBotao(Cronometragem cronometragem) {
                if (cronometragem.isExcluida()) {
                    return new DadosBotao(ListaConstantes.EstiloCss.BOTAO_CRONOMETRO_ADICIONAR.getValor(), "reativar cronometragem");
                } else {
                    return new DadosBotao(ListaConstantes.EstiloCss.BOTAO_CRONOMETRO_REMOVER.getValor(), "excluir cronometragem");
                }
            }
        });

        // Evento duplo clique na tabela
        this.tabCrono.setRowFactory(new TableViewDuploClique<Cronometragem>() {
            @Override
            public Cronometragem eventoDuploClique(Cronometragem cronometragem) {
                ProvaCronoPendenciaCrontroller.this.cronometragemEdicaoController.setOperacao(ListaConstantesBase.Operacao.EDITAR);
                ProvaCronoPendenciaCrontroller.this.cronometragemEdicaoController.setCronometragemEdicao(cronometragem);
                ProvaCronoPendenciaCrontroller.super.cj.abrirJanela(Stages.CRONOMETRAGEM_EDICAO.ordinal(), Stages.PROVA_CRONO_PENDENCIA.ordinal(), ICronometragemEdicaoController.class, "cronometragem", true);
                ProvaCronoPendenciaCrontroller.this.filtrarCronometragem(cronometragem.getNumeroAtleta());
                return cronometragem;
            }
        });
    }

    @Override
    public void setProva(Prova prova) {
        this.prova = prova;
    }

    private List<AtletaInscricao> criarListaAtletas(List<Cronometragem> listaCronometragens) {
        Set<AtletaInscricao> atletas = new LinkedHashSet<>();
        listaCronometragens.forEach((crono) -> {
            if (crono.getAtletaInscricao() == null) {
                AtletaInscricao ai = new AtletaInscricao();
                ai.setAtleta(new Atleta());
                ai.setCategoria(new CategoriaDaProva());
                ai.getCategoria().setProva(crono.getProva());
                ai.getCategoria().setCategoriaAtleta(new CategoriaAtleta());
                ai.getCategoria().setLargada(new Largada());
                ai.getCategoria().setPercurso(new Percurso());

                ai.getAtleta().setNome("Desconhecido - " + crono.getNumeroAtleta());
                ai.setNumeroAtleta(crono.getNumeroAtleta());
                ai.getCategoria().getCategoriaAtleta().setNome("Indefinida");
                atletas.add(ai);
            } else {
                atletas.add(crono.getAtletaInscricao());
            }
        });
        return new ArrayList(atletas);
    }

    private void filtrarCronometragem(Integer numeroAtleta) {
        try {
            List<Cronometragem> cronometragens = this.rolex.pesquisarCronometragemPorNumeroAtleta(this.prova, numeroAtleta);
            this.tabCrono.getItems().clear();
            this.tabCrono.getItems().addAll(cronometragens);
        } catch (NegocioException ex) {
            super.cj.exibirErro(cm.getMensagem("erro_control_pesquisar", "cronometragem"), GeralUtil.getMensagemOriginalErro(ex));
        }
    }

    private void ativarCronometragem(Cronometragem cronometragem) {
        if (super.cj.exibirDialogSimNao("Cronometragem", "Confirmar a ativação da cronometragem?", null)) {
            try {
                this.rolex.ativarCronometragem(cronometragem);
                super.cj.exibirInformacao("info_op_sucesso");
                this.filtrarCronometragem(cronometragem.getNumeroAtleta());
            } catch (NegocioException ex) {
                super.cj.exibirErro(cm.getMensagem("erro_control_atualizar", "cronometragem"), GeralUtil.getMensagemOriginalErro(ex));
            }
        }
    }

    private void excluirCronometragem(Cronometragem cronometragem) {
        if (super.cj.exibirDialogSimNao("Cronometragem", "Confirmar a exclusão desta cronometragem?", null)) {
            try {
                Cronometragem crono = this.rolex.excluirCronometragem(cronometragem);
                //this.cronoDataSource.atualizar(cronometragem, crono);
                super.cj.exibirInformacao(cm.getMensagem("sucesso_control_excluir", "cronometragem"));
                this.filtrarCronometragem(cronometragem.getNumeroAtleta());
            } catch (NegocioException ex) {
                super.cj.exibirErro(cm.getMensagem("erro_control_atualizar", "cronometragem"), GeralUtil.getMensagemOriginalErro(ex));
            }
        }
    }

    private void excluirTodasCronometragem(GenericLinhaView<AtletaInscricao> linhaView) {
        if (super.cj.exibirDialogSimNao("Cronometragem", "Confirmar a exclusão de todas as cronometragens?", null)) {
            try {
                this.rolex.excluirTodasCronometragemAtleta(linhaView.getBean());
                this.tabAtleta.getItems().remove(linhaView);
                this.tabCrono.getItems().clear();
                super.cj.exibirInformacao(cm.getMensagem("sucesso_control_excluir", "cronometragem"));
            } catch (NegocioException ex) {
                super.cj.exibirErro(cm.getMensagem("erro_control_atualizar", "cronometragem"), GeralUtil.getMensagemOriginalErro(ex));
            }
        }
    }

    private void adicionarAtletaTodasCronometragens(GenericLinhaView<AtletaInscricao> linhaView) {
        this.cronometragemAdicionarAtletaController.setNumeroAtleta(linhaView.getBean().getNumeroAtleta());
        this.cronometragemAdicionarAtletaController.setProva(linhaView.getBean().getCategoria().getProva());
        super.cj.abrirJanela(Stages.CRONOMETRAGEM_ADD_ATLETA.ordinal(), Stages.PROVA_CRONO_PENDENCIA.ordinal(), ICronometragemAdicionarAtletaController.class, "Adicionar um atleta às cronometragens", true);
        AtletaInscricao atletaInscricaoNovo = this.cronometragemAdicionarAtletaController.getAtletaInscricao();
        if (atletaInscricaoNovo != null) {
            this.tabAtleta.getItems().remove(linhaView);
        }
    }

    @FXML
    public void confirmarDnfAtletaLote() {
        boolean marcarDnf = super.cj.exibirDialogSimNao("D.N.F.", "Define que o atleta não finalizou a prova", "Confirma a marcação de D.N.F. para o(s) atleta(a) selecionado(s)?");
        if (marcarDnf) {
            List<GenericLinhaView<AtletaInscricao>> listaRemover = new ArrayList();
            for (GenericLinhaView<AtletaInscricao> linhaView : this.tabAtleta.getItems()) {
                if (linhaView.isSelected()) {
                    try {
                        this.juiz.confirmarDnfAtleta(linhaView.getBean());
                        listaRemover.add(linhaView);
                    } catch (NegocioException ex) {
                        super.cj.exibirErro(cm.getMensagem("erro_control_atualizar", "atleta"), GeralUtil.getMensagemOriginalErro(ex));
                    }
                }
            }
            if (!listaRemover.isEmpty()) {
                this.tabAtleta.getItems().removeAll(listaRemover);
                super.cj.exibirInformacao("Atleta(s) confirmado(s) como D.N.F. com sucesso.");
            }
        }
    }

    @FXML
    public void verificarPendenciasCronometragem(ActionEvent actionEvent) {
        try {
            this.prova = this.rolex.verificarInconsitenciasCronometragem(prova);
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
}
