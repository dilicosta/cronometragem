package com.taurus.racingTiming.controller.resultado;

import com.taurus.exception.NegocioException;
import com.taurus.javafx.componente.BotaoCelulaTableValueFactory;
import com.taurus.javafx.componente.BotaoVariavelTableColumnFactory;
import com.taurus.javafx.controller.FXMLBaseController;
import com.taurus.javafx.converter.GenericComboBoxConverter;
import com.taurus.javafx.util.ControleJanela;
import com.taurus.racingTiming.entidade.corrida.AtletaInscricao;
import com.taurus.racingTiming.entidade.corrida.CategoriaDaProva;
import com.taurus.racingTiming.entidade.corrida.Prova;
import com.taurus.racingTiming.negocio.IJuiz;
import com.taurus.racingTiming.negocio.ISecretario;
import com.taurus.racingTiming.util.ListaConstantes;
import com.taurus.util.FormatarUtil;
import com.taurus.util.GeralUtil;
import com.taurus.util.ListaConstantesBase;
import com.taurus.util.Pagina;
import com.taurus.util.annotation.Mascara;
import com.taurus.util.annotation.Parametro;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Pagination;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.validator.GenericValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class SituacaoAtletaProvaController extends FXMLBaseController implements ISituacaoAtletaProvaController {

    private static final Log LOG = LogFactory.getLog(SituacaoAtletaProvaController.class);
    private static final int ITENS_POR_PAGINA = 13;

    public SituacaoAtletaProvaController() {
        super();
    }

    @Autowired
    private IResultadoCronoPopupController cronoPopup;

    @Autowired
    private ISecretario secretario;
    @Autowired
    private IJuiz juiz;

    @Parametro
    private Prova prova;

    private Pagina pagina = new Pagina(1, ITENS_POR_PAGINA, null);
    private String filtroNomeAtleta;
    private String filtroNumeroAtleta;
    private ListaConstantes.StatusAtletaCorrida filtroSituacaoAtleta;
    private CategoriaDaProva filtroCategoria;
    private TipoFiltro tipoFiltro;

    @FXML
    private TextField txtNomeProva;
    @FXML
    private TextField txtSituacao;
    @FXML
    @Mascara(tipo = Mascara.TipoMascara.TEXT_FIELD_MAXIMO_CARACTER, tamanhoMax = "50")
    private TextField txtNomeAtleta;
    @FXML
    @Mascara(tipo = Mascara.TipoMascara.TEXT_FIELD_APENAS_NUMERO, tamanhoMax = "8")
    private TextField txtNumeroAtleta;
    @FXML
    private ComboBox<ListaConstantes.StatusAtletaCorrida> comboSituacao;
    @FXML
    private ComboBox<CategoriaDaProva> comboCategoria;
    @FXML
    private Button btnPesquisarAtleta;
    @FXML
    private Button btnPesquisarSituacaoCategoria;

    @FXML
    private Pagination paginacao;
    @FXML
    private TableView<AtletaInscricao> tabAtleta;
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
    private TableColumn colOperacao;

    @Value("${fxml.situacao_atleta_prova.view}")
    @Override
    public void setFxmlFilePath(String filePath) {
        this.fxmlFilePath = filePath;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.paginacao.setPageFactory(this::paginar);

        this.btnPesquisarAtleta.getStyleClass().add(ListaConstantesBase.EstiloCss.BOTAO_PESQUISAR.getValor());
        this.btnPesquisarAtleta.setTooltip(new Tooltip("pesquisar pelo atleta"));
        this.btnPesquisarSituacaoCategoria.getStyleClass().add(ListaConstantesBase.EstiloCss.BOTAO_PESQUISAR.getValor());
        this.btnPesquisarSituacaoCategoria.setTooltip(new Tooltip("pesquisar pela situação e/ou categoria"));
        this.configurarTabelaColunas();

        this.comboCategoria.setConverter(new GenericComboBoxConverter<>("categoriaAtleta.nome"));
        this.comboSituacao.setConverter(new GenericComboBoxConverter<>("descricao"));
        this.comboSituacao.getItems().add(null);
        this.comboSituacao.getItems().addAll(ListaConstantes.StatusAtletaCorrida.values());
    }

    @Override
    public void reinicializarJanelaEspecifico() {
        this.filtroNumeroAtleta = null;
        this.filtroNomeAtleta = null;

        this.tabAtleta.getItems().clear();
    }

    @Override
    public void aoAbrirJanela() {
        this.txtNomeProva.setText(this.prova.getNome());
        this.txtSituacao.setText(this.prova.getStatus().getDescricao());

        try {
            this.comboCategoria.getItems().add(null);
            this.comboCategoria.getItems().addAll(this.secretario.pesquisarCategoriaDaProva(this.prova));
        } catch (NegocioException ex) {
            this.cj.exibirErro(cm.getMensagem("erro_control_pesquisar", "categoria"), GeralUtil.getMensagemOriginalErro(ex));
        }
        this.txtNomeAtleta.requestFocus();
    }

    @Override
    public void aoFecharJanelaEspecifico() {
    }

    @FXML
    public void filtrarAtleta() {
        this.filtroNumeroAtleta = this.txtNumeroAtleta.getText();
        this.filtroNomeAtleta = this.txtNomeAtleta.getText();
        this.tipoFiltro = TipoFiltro.ATLETA;
        this.reiniciarPaginacao();
        this.paginacao.getPageFactory().call(0);
    }

    @FXML
    public void filtrarSituacaoCategoria() {
        this.filtroSituacaoAtleta = this.comboSituacao.getValue();
        this.filtroCategoria = this.comboCategoria.getValue();
        this.tipoFiltro = TipoFiltro.SITUACAO_CATEGORIA;
        this.reiniciarPaginacao();
        this.paginacao.getPageFactory().call(0);
    }

    private TableView paginar(int indexPagina) {
        try {
            this.pagina.setNumeroPagina(indexPagina + 1);
            List<AtletaInscricao> atletas = new ArrayList();
            if (this.tipoFiltro == TipoFiltro.ATLETA) {
                if (!GenericValidator.isBlankOrNull(this.filtroNumeroAtleta)) {
                    AtletaInscricao atleta = this.juiz.pesquisarClassificacaoAtleta(this.prova, this.filtroNumeroAtleta);
                    if (atleta != null) {
                        atletas.add(atleta);
                    }
                } else if (!GenericValidator.isBlankOrNull(this.filtroNomeAtleta)) {
                    atletas = this.secretario.pesquisarAtletaInscricaoPorNome(this.prova, this.filtroNomeAtleta, pagina);
                }
            } else {
                atletas = this.secretario.pesquisarAtletaPorStatusCategoria(this.prova, this.filtroSituacaoAtleta, this.filtroCategoria, pagina);
            }
            this.tabAtleta.getItems().clear();
            this.tabAtleta.getItems().addAll(atletas);
            this.paginacao.setPageCount(pagina.getNumeroPaginas());
        } catch (NegocioException ex) {
            this.cj.exibirErro(cm.getMensagem("erro_control_pesquisar", "atletas"), GeralUtil.getMensagemOriginalErro(ex));
        }
        return this.tabAtleta;
    }

    private void configurarTabelaColunas() {
        this.colNumero.setCellValueFactory(linha -> new ReadOnlyObjectWrapper(linha.getValue().getNumeroAtleta()));
        this.colAtleta.setCellValueFactory(linha -> new ReadOnlyObjectWrapper(linha.getValue().getAtleta().getNome()));
        this.colEquipe.setCellValueFactory(linha -> new ReadOnlyObjectWrapper(linha.getValue().getEquipe()));
        this.colCategoria.setCellValueFactory(linha -> new ReadOnlyObjectWrapper(linha.getValue().getCategoria().getCategoriaAtleta().getNome()));
        this.colTempo.setCellValueFactory(linha -> new ReadOnlyObjectWrapper(linha.getValue().getTempo() == null ? null : FormatarUtil.formatarTempoMilisegundos(linha.getValue().getTempo())));
        this.colDifCat.setCellValueFactory(linha -> new ReadOnlyObjectWrapper(linha.getValue().getDiferencaCategoria() == null ? null : FormatarUtil.formatarTempoMilisegundos(linha.getValue().getDiferencaCategoria())));
        this.colDifGeral.setCellValueFactory(linha -> new ReadOnlyObjectWrapper(linha.getValue().getDiferencaGeral() == null ? null : FormatarUtil.formatarTempoMilisegundos(linha.getValue().getDiferencaGeral())));

        this.colNumero.getStyleClass().add(ListaConstantesBase.EstiloCss.TAB_COL_CENTRO.getValor());
        this.colAtleta.getStyleClass().add(ListaConstantesBase.EstiloCss.TAB_COL_ESQUERDO.getValor());
        this.colEquipe.getStyleClass().add(ListaConstantesBase.EstiloCss.TAB_COL_ESQUERDO.getValor());
        this.colCategoria.getStyleClass().add(ListaConstantesBase.EstiloCss.TAB_COL_ESQUERDO.getValor());
        this.colTempo.getStyleClass().add(ListaConstantesBase.EstiloCss.TAB_COL_CENTRO.getValor());
        this.colDifCat.getStyleClass().add(ListaConstantesBase.EstiloCss.TAB_COL_CENTRO.getValor());
        this.colDifGeral.getStyleClass().add(ListaConstantesBase.EstiloCss.TAB_COL_CENTRO.getValor());

        this.colOperacao.getStyleClass().add(ListaConstantesBase.EstiloCss.TAB_COL_CENTRO.getValor());
        this.colOperacao.setCellValueFactory(new BotaoCelulaTableValueFactory());
        this.colOperacao.setCellFactory(new BotaoVariavelTableColumnFactory<AtletaInscricao>() {
            @Override
            protected boolean exibirItem(AtletaInscricao atletaInscricao) {
                return true;
            }

            @Override
            public void eventoBotao(ActionEvent actionEvent, AtletaInscricao atletaInscricao) {
                switch (atletaInscricao.getStatusCorrida()) {
                    case DESCLASSIFICADO:
                        desfazerDesclassificacaoAtleta(atletaInscricao);
                        break;
                    case DNF_CONFIRMADO:
                        desfazerDnfAtleta(atletaInscricao);
                        break;
                    case COMPLETOU:
                        desclassificarAtleta(atletaInscricao);
                        break;
                }
            }

            @Override
            public BotaoVariavelTableColumnFactory.DadosBotao getDadosBotao(AtletaInscricao atletaInscricao) {
                switch (atletaInscricao.getStatusCorrida()) {
                    case DESCLASSIFICADO:
                        return new BotaoVariavelTableColumnFactory.DadosBotao(ListaConstantes.EstiloCss.BOTAO_DESCLASSIFICACAO.getValor(), "desfazer desclassificação");
                    case DNF:
                        return new BotaoVariavelTableColumnFactory.DadosBotao(ListaConstantes.EstiloCss.BOTAO_NAO_BANDEIRA_CHEGADA.getValor(), "D.N.F.");
                    case DNF_CONFIRMADO:
                        return new BotaoVariavelTableColumnFactory.DadosBotao(ListaConstantes.EstiloCss.BOTAO_NAO_BANDEIRA_CHEGADA_VERDE.getValor(), "desfazer D.N.F. confirmado");
                    case COMPLETOU:
                        return new BotaoVariavelTableColumnFactory.DadosBotao(ListaConstantesBase.EstiloCss.BOTAO_OK.getValor(), "desclassificar atleta");
                    default:
                        return null;
                }
            }
        });

        // Evento clique na tabela
        this.tabAtleta.setRowFactory((TableView<AtletaInscricao> param) -> {
            TableRow<AtletaInscricao> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 1 && (!row.isEmpty())) {
                    cronoPopup.setAtletaInscricao(row.getItem());
                    SituacaoAtletaProvaController.super.cj.abrirPopup(ListaConstantes.Stages.STATUS_ATLETA_PROVA.ordinal(), IResultadoCronoPopupController.class, row, ControleJanela.PosicaoReferenciaPopup.CENTRO_ABAIXO);
                }
            });
            return row;
        });

    }

    private void desclassificarAtleta(AtletaInscricao atletaInscricao) {
        Optional<String> motivoDesclassificacao = (super.cj.exibirDialogoTexto("Desclassificação", "Confirma a desclassificação do atleta " + atletaInscricao.getAtleta().getNome() + "?", "Motivo para a desclassificação:"));
        if (motivoDesclassificacao.isPresent() && !GenericValidator.isBlankOrNull(motivoDesclassificacao.get())) {
            try {
                int index = this.tabAtleta.getItems().indexOf(atletaInscricao);
                atletaInscricao = this.juiz.desclassificarAtleta(atletaInscricao, motivoDesclassificacao.get());
                this.tabAtleta.getItems().set(index, atletaInscricao);
                super.cj.exibirInformacao(cm.getMensagem("info_atleta_desclassificado", atletaInscricao.getAtleta().getNome()));
            } catch (NegocioException ex) {
                super.cj.exibirErro(cm.getMensagem("erro_control_atualizar", "atleta"), GeralUtil.getMensagemOriginalErro(ex));
            }
        }
    }

    private void desfazerDesclassificacaoAtleta(AtletaInscricao atletaInscricao) {
        if (super.cj.exibirDialogSimNao("Re classificação", "Confirmar a operação para desfazer a desclassificação do atleta?", null)) {
            try {
                int index = this.tabAtleta.getItems().indexOf(atletaInscricao);
                atletaInscricao = this.juiz.desfazerDesclassificacaoAtleta(atletaInscricao);
                this.tabAtleta.getItems().set(index, atletaInscricao);
                super.cj.exibirInformacao(cm.getMensagem("info_atleta_classificado", atletaInscricao.getAtleta().getNome()));
            } catch (NegocioException ex) {
                super.cj.exibirErro(cm.getMensagem("erro_control_atualizar", "atleta"), GeralUtil.getMensagemOriginalErro(ex));
            }
        }
    }

    private void desfazerDnfAtleta(AtletaInscricao atletaInscricao) {
        if (super.cj.exibirDialogSimNao("Re classificação", "Confirmar a operação para desfazer o D.N.F. do atleta?", null)) {
            try {
                int index = this.tabAtleta.getItems().indexOf(atletaInscricao);
                atletaInscricao = this.juiz.desfazerConfirmarDnfAtleta(atletaInscricao);
                this.tabAtleta.getItems().set(index, atletaInscricao);
                super.cj.exibirInformacao(cm.getMensagem("info_atleta_desf_dnf", atletaInscricao.getAtleta().getNome()));
            } catch (NegocioException ex) {
                super.cj.exibirErro(cm.getMensagem("erro_control_atualizar", "atleta"), GeralUtil.getMensagemOriginalErro(ex));
            }
        }
    }

    @Override
    public void setProva(Prova prova) {
        this.prova = prova;
    }

    private void reiniciarPaginacao() {
        this.pagina.setNumeroPagina(0);
        this.pagina.setTotalItens(null);
    }

    private static enum TipoFiltro {
        ATLETA, SITUACAO_CATEGORIA;
    }
}
