package com.taurus.racingTiming.controller.corrida.inscricao;

import com.taurus.racingTiming.controller.corrida.inscricao.it.IInscricaoNumeracaoAutomaticaController;
import com.taurus.racingTiming.controller.corrida.inscricao.it.IInscricaoController;
import com.taurus.racingTiming.controller.corrida.inscricao.it.IInscricaoPendenciaController;
import com.taurus.exception.AvisoNegocioException;
import com.taurus.exception.NegocioException;
import com.taurus.javafx.componente.TableViewDuploClique;
import com.taurus.javafx.controller.FXMLBaseController;
import com.taurus.javafx.converter.GenericComboBoxConverter;
import com.taurus.racingTiming.entidade.corrida.AtletaInscricao;
import com.taurus.racingTiming.entidade.corrida.Prova;
import com.taurus.racingTiming.negocio.ISecretario;
import com.taurus.racingTiming.util.ListaConstantes;
import com.taurus.racingTiming.util.ListaConstantes.Stages;
import com.taurus.util.FormatarUtil;
import com.taurus.util.GeralUtil;
import com.taurus.util.ListaConstantesBase;
import com.taurus.util.Pagina;
import com.taurus.util.annotation.Parametro;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Pagination;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class InscricaoPendenciaController extends FXMLBaseController implements IInscricaoPendenciaController {

    private static final Log LOG = LogFactory.getLog(InscricaoPendenciaController.class);
    private static final int ITENS_POR_PAGINA = 15;

    public InscricaoPendenciaController() {
        super();
    }

    @Autowired
    private IInscricaoController inscricaoController;
    @Autowired
    private IInscricaoNumeracaoAutomaticaController inscricaoNumeracaoAutomaticaController;
    @Autowired
    private ISecretario secretario;

    @Parametro
    private Prova prova;

    private Pagina pagina = new Pagina(1, ITENS_POR_PAGINA, null);

    @FXML
    private TextField txtNomeProva;
    @FXML
    private TextField txtDataProva;
    @FXML
    private TextField txtSituacaoProva;
    @FXML
    private ComboBox<ListaConstantes.PendenciaInscricao> comboPendencia;
    @FXML
    private Button btnNumAutomatica;
    @FXML
    private Button btnPendencia;

    @FXML
    private Pagination paginacao;
    @FXML
    private TableView<AtletaInscricao> tabInscricao;
    @FXML
    private TableColumn<AtletaInscricao, Integer> colNumero;
    @FXML
    private TableColumn<AtletaInscricao, String> colNome;
    @FXML
    private TableColumn<AtletaInscricao, String> colCategoria;
    @FXML
    private TableColumn<AtletaInscricao, String> colEquipe;

    @Value("${fxml.inscricaoPendencia.view}")
    @Override
    public void setFxmlFilePath(String filePath) {
        this.fxmlFilePath = filePath;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.paginacao.setPageFactory(this::paginar);

        this.comboPendencia.setConverter(new GenericComboBoxConverter<>("descricao"));

        //this.btnNumAutomatica.getStyleClass().add(ListaConstantesBase.EstiloCss.BOTAO_PRINT.getValor());
        this.btnNumAutomatica.setTooltip(new Tooltip("inserir numeração dos atletas automaticamente"));

        this.btnPendencia.getStyleClass().add(ListaConstantes.EstiloCss.BOTAO_PESQUISAR_COMPUTADOR.getValor());
        this.btnPendencia.setTooltip(new Tooltip("verificar e fechar insconsistências"));

        this.comboPendencia.getItems().addAll(ListaConstantes.PendenciaInscricao.values());
        this.comboPendencia.valueProperty().addListener((ObservableValue<? extends ListaConstantes.PendenciaInscricao> observable, ListaConstantes.PendenciaInscricao oldValue, ListaConstantes.PendenciaInscricao newValue) -> {
            if (newValue != null && newValue != oldValue) {
                InscricaoPendenciaController.this.reiniciarPaginacao();

                InscricaoPendenciaController.this.btnNumAutomatica.setVisible(newValue == ListaConstantes.PendenciaInscricao.ATLETA_SEM_NUMERO);
            }
        });

        this.configurarTabelaColunas();
    }

    @Override
    public void reinicializarJanelaEspecifico() {
    }

    @Override
    public void aoAbrirJanela() {
        this.txtNomeProva.setText(this.prova.getNome());
        this.txtDataProva.setText(FormatarUtil.localDateToString(this.prova.getData()));
        this.txtSituacaoProva.setText(this.prova.getStatus().getDescricao());
        this.comboPendencia.requestFocus();
    }

    @Override
    public void aoFecharJanelaEspecifico() {
    }

    private TableView paginar(int indexPagina) {
        List<AtletaInscricao> atletas = new ArrayList();

        try {
            this.pagina.setNumeroPagina(indexPagina + 1);
            if (this.comboPendencia.getValue() != null) {
                switch (this.comboPendencia.getValue()) {
                    case ATLETA_SEM_DUPLA:
                        atletas = this.secretario.pesquisarAtletaInscricaoSemDupla(this.prova, pagina);
                        break;
                    case ATLETA_SEM_NUMERO:
                        atletas = this.secretario.pesquisarAtletaInscricaoSemNumero(this.prova, pagina);
                        break;
                    case ATLETA_NUMERACAO_REPETIDA:
                        atletas = this.secretario.pesquisarAtletaInscricaoNumeracaoRepetida(prova, pagina);
                    default:

                }
            }
        } catch (NegocioException ex) {
            this.cj.exibirErro(cm.getMensagem("erro_control_pesquisar", "inscricao"), GeralUtil.getMensagemOriginalErro(ex));

        }
        this.tabInscricao.getItems().clear();
        this.tabInscricao.getItems().addAll(atletas);
        this.paginacao.setPageCount(pagina.getNumeroPaginas());
        return this.tabInscricao;
    }

    private void configurarTabelaColunas() {
        this.colNumero.setCellValueFactory(linha -> new ReadOnlyObjectWrapper(linha.getValue().getNumeroAtleta()));
        this.colNome.setCellValueFactory(linha -> new ReadOnlyObjectWrapper(linha.getValue().getAtleta().getNome()));
        this.colCategoria.setCellValueFactory(linha -> new ReadOnlyObjectWrapper(linha.getValue().getCategoria().getCategoriaAtleta().getNome()));
        this.colEquipe.setCellValueFactory(linha -> new ReadOnlyObjectWrapper(linha.getValue().getEquipe()));

        this.tabInscricao.setRowFactory(new TableViewDuploClique<AtletaInscricao>() {
            @Override
            public AtletaInscricao eventoDuploClique(AtletaInscricao atletaInscricao) {
                InscricaoPendenciaController.this.inscricaoController.setOperacao(ListaConstantesBase.Operacao.EDITAR);
                InscricaoPendenciaController.this.inscricaoController.setSalvarFechar(true);
                InscricaoPendenciaController.this.inscricaoController.setAtletaInscricaoEdicao(atletaInscricao);
                InscricaoPendenciaController.super.cj.abrirJanela(Stages.INSCRICAO.ordinal(), Stages.PENDENCIA_INSCRICAO.ordinal(), IInscricaoController.class, "t_insc_categoria", true);
                InscricaoPendenciaController.this.reiniciarPaginacao();
                return atletaInscricao;
            }
        });
    }

    private void reiniciarPaginacao() {
        this.pagina.setTotalItens(null);
        this.paginacao.setCurrentPageIndex(0);
        this.paginacao.getPageFactory().call(0);
    }

    @Override
    public void setProva(Prova prova) {
        this.prova = prova;
    }

    @FXML
    public void verificarFecharInscricao() {
        boolean possuiPendencia;
        try {
            if (this.prova.getStatus().equals(ListaConstantes.StatusProva.INSCRICAO_ENCERRADA_PENDENTE)) {
                possuiPendencia = this.secretario.finalizarInscricaoProva(this.prova);
            } else {
                possuiPendencia = this.secretario.verificarPendenciasInscricoesProva(this.prova);
            }
        } catch (NegocioException ex) {
            this.cj.exibirErro(cm.getMensagem("erro_control_atualizar", "prova"), GeralUtil.getMensagemOriginalErro(ex));
            return;
        } catch (AvisoNegocioException ex) {
            super.cj.exibirAviso(ex.getMessage());
            return;
        }
        if (possuiPendencia) {
            super.cj.exibirAviso("aviso_pendencia_inscricao", this.prova.getMotivoPendencia().replaceAll("\\|", "\n"));
        } else {
            super.cj.exibirInformacao("info_sem_pendencia");
        }
    }

    @FXML
    public void abrirNumeracaoAutomatica() {
        this.inscricaoNumeracaoAutomaticaController.setProva(this.prova);
        super.cj.abrirJanela(Stages.INSCRICAO_NUM_AUTOMATICA.ordinal(), Stages.PENDENCIA_INSCRICAO.ordinal(), IInscricaoNumeracaoAutomaticaController.class, "t_inscricao_num_auto", true);
        this.reiniciarPaginacao();
    }
}
