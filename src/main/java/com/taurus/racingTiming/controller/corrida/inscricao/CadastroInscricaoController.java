package com.taurus.racingTiming.controller.corrida.inscricao;

import com.taurus.racingTiming.controller.corrida.inscricao.it.IInscricaoController;
import com.taurus.exception.AvisoNegocioException;
import com.taurus.exception.NegocioException;
import com.taurus.javafx.componente.BotaoCelulaTableValueFactory;
import com.taurus.javafx.componente.BotaoCondicionalTableColumnFactory;
import com.taurus.javafx.componente.TableViewDuploClique;
import com.taurus.javafx.controller.FXMLBaseController;
import com.taurus.racingTiming.entidade.corrida.AtletaInscricao;
import com.taurus.racingTiming.negocio.ISecretario;
import com.taurus.racingTiming.util.ListaConstantes;
import com.taurus.util.FormatarUtil;
import com.taurus.util.GeralUtil;
import com.taurus.util.ListaConstantesBase;
import com.taurus.util.Pagina;
import com.taurus.util.annotation.Mascara;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Pagination;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class CadastroInscricaoController extends FXMLBaseController {

    private static final Log LOG = LogFactory.getLog(CadastroInscricaoController.class);
    private static final int ITENS_POR_PAGINA = 15;

    @Autowired
    private IInscricaoController inscricaoController;

    public CadastroInscricaoController() {
        super();
    }

    @Autowired
    private ISecretario secretario;

    private Pagina pagina = null;

    private String filtroNome;
    private String filtroCpf;
    private String filtroNomeProva;
    private LocalDate filtroDataInicio;
    private LocalDate filtroDataFim;

    @FXML
    @Mascara(tipo = Mascara.TipoMascara.TEXT_FIELD_MAXIMO_CARACTER, tamanhoMax = "30")
    private TextField txtNome;
    @FXML
    @Mascara(tipo = Mascara.TipoMascara.TEXT_FIELD_CPF)
    private TextField txtCpf;
    @FXML
    @Mascara(tipo = Mascara.TipoMascara.TEXT_FIELD_MAXIMO_CARACTER, tamanhoMax = "30")
    private TextField txtNomeProva;
    @FXML
    @Mascara(tipo = Mascara.TipoMascara.DATE_PICKER)
    private DatePicker dtInicial;
    @FXML
    @Mascara(tipo = Mascara.TipoMascara.DATE_PICKER)
    private DatePicker dtFinal;
    @FXML
    private Pagination paginacao;
    @FXML
    private TableView<AtletaInscricao> tabInscricao;
    @FXML
    private TableColumn<AtletaInscricao, String> colNome;
    @FXML
    private TableColumn<AtletaInscricao, String> colCpf;
    @FXML
    private TableColumn<AtletaInscricao, String> colProva;
    @FXML
    private TableColumn<AtletaInscricao, String> colCategoria;
    @FXML
    private TableColumn<AtletaInscricao, String> colData;
    @FXML
    private TableColumn colExcluir;

    @Value("${fxml.cadastroInscricao.view}")
    @Override
    public void setFxmlFilePath(String filePath) {
        this.fxmlFilePath = filePath;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.paginacao.setPageFactory(this::paginar);

        colNome.setCellValueFactory(linha -> new ReadOnlyObjectWrapper(linha.getValue().getAtleta().getNome()));
        colCpf.setCellValueFactory(linha -> new ReadOnlyObjectWrapper(linha.getValue().getAtleta().getCpf() == null ? "" : FormatarUtil.formatarCpf(linha.getValue().getAtleta().getCpf())));
        colProva.setCellValueFactory(linha -> new ReadOnlyObjectWrapper(linha.getValue().getCategoria().getProva().getNome()));
        colCategoria.setCellValueFactory(linha -> new ReadOnlyObjectWrapper(linha.getValue().getCategoria().getCategoriaAtleta().getNome()));
        colData.setCellValueFactory(linha -> new ReadOnlyObjectWrapper(linha.getValue().getCategoria().getProva().getData() == null ? "" : FormatarUtil.localDateToString(linha.getValue().getCategoria().getProva().getData())));

        colNome.getStyleClass().add(ListaConstantesBase.EstiloCss.TAB_COL_ESQUERDO.getValor());
        colCpf.getStyleClass().add(ListaConstantesBase.EstiloCss.TAB_COL_CENTRO.getValor());
        colProva.getStyleClass().add(ListaConstantesBase.EstiloCss.TAB_COL_ESQUERDO.getValor());
        colCategoria.getStyleClass().add(ListaConstantesBase.EstiloCss.TAB_COL_ESQUERDO.getValor());
        colData.getStyleClass().add(ListaConstantesBase.EstiloCss.TAB_COL_CENTRO.getValor());

        colExcluir.getStyleClass().add(ListaConstantesBase.EstiloCss.TAB_COL_CENTRO.getValor());
        colExcluir.setCellValueFactory(new BotaoCelulaTableValueFactory());
        colExcluir.setCellFactory(new BotaoCondicionalTableColumnFactory<AtletaInscricao>("excluir inscricao", ListaConstantesBase.EstiloCss.BOTAO_EXCLUIR.getValor()) {
            @Override
            public void eventoBotao(ActionEvent actionEvent, AtletaInscricao atletaInscricao) {
                remover(atletaInscricao);
            }

            @Override
            protected boolean exibirItem(AtletaInscricao atletaInscricao) {
                return atletaInscricao.getCategoria().getProva().getStatus() == ListaConstantes.StatusProva.INSCRICAO_ABERTA;
            }
        });

        // Evento duplo clique na tabela
        // Abre a Janela de Inscricao para edicao
        tabInscricao.setRowFactory(new TableViewDuploClique<AtletaInscricao>(true) {
            @Override
            public AtletaInscricao eventoDuploClique(AtletaInscricao atletaInscricao) {
                editarInscricao(atletaInscricao);
                return atletaInscricao;
            }
        });
    }

    @Override
    public void reinicializarJanelaEspecifico() {
        this.filtroNome = null;
        this.filtroCpf = null;
        this.filtroDataInicio = null;
        this.filtroDataFim = null;
        this.tabInscricao.getItems().clear();
        this.pagina = new Pagina(1, ITENS_POR_PAGINA, null);

    }

    @FXML
    public void novo(ActionEvent actionEvent) {
        this.inscricaoController.setOperacao(ListaConstantesBase.Operacao.NOVO);
        super.cj.abrirJanela(ListaConstantes.Stages.INSCRICAO.ordinal(), ListaConstantes.Stages.CADASTRO_INSCRICAO.ordinal(), IInscricaoController.class, "inscricao", true);
        //Atualiza a pagina
        this.pagina.setTotalItens(null);
        this.paginacao.getPageFactory().call(this.paginacao.getCurrentPageIndex());
    }

    @Override
    public void aoAbrirJanela() {
        this.pagina = new Pagina(1, ITENS_POR_PAGINA, null);
        this.filtrar();
        this.txtNome.requestFocus();
    }

    @Override
    public void aoFecharJanelaEspecifico() {
    }

    private void remover(AtletaInscricao atletaInscricao) {
        if (cj.exibirDialogSimNao("titulo_apagar", "aviso_pergunta_excluir", null, "inscricao")) {
            try {
                secretario.excluirAtletaInscricao(atletaInscricao);
                this.tabInscricao.getItems().remove(atletaInscricao);
                this.tabInscricao.refresh();
            } catch (NegocioException ex) {
                super.cj.exibirErro(cm.getMensagem("erro_control_excluir", "inscricao"), GeralUtil.getMensagemOriginalErro(ex));
            } catch (AvisoNegocioException ex) {
                super.cj.exibirAviso(ex.getMessage());
            }
        }
    }

    private void editarInscricao(AtletaInscricao atletaInscricao) {
        this.inscricaoController.setOperacao(atletaInscricao.getCategoria().getProva().getStatus() == ListaConstantes.StatusProva.INSCRICAO_ABERTA ? ListaConstantesBase.Operacao.EDITAR : ListaConstantesBase.Operacao.LEITURA);
        this.inscricaoController.setOperacao(ListaConstantesBase.Operacao.EDITAR);
        this.inscricaoController.setAtletaInscricaoEdicao(atletaInscricao);
        super.cj.abrirJanela(ListaConstantes.Stages.INSCRICAO.ordinal(), ListaConstantes.Stages.CADASTRO_INSCRICAO.ordinal(), IInscricaoController.class, "inscricao", true);
        this.inscricaoController.setAtletaInscricaoEdicao(null);
    }

    @FXML
    public void filtrar() {
        this.filtroNome = this.txtNome.getText();
        this.filtroCpf = FormatarUtil.removerMascaraNumero(this.txtCpf.getText());
        this.filtroNomeProva = this.txtNomeProva.getText();
        this.filtroDataInicio = FormatarUtil.stringToLocalDate(this.dtInicial.getEditor().getText());
        this.filtroDataFim = FormatarUtil.stringToLocalDate(this.dtFinal.getEditor().getText());

        this.pagina = new Pagina(1, ITENS_POR_PAGINA, null);
        this.paginacao.getPageFactory().call(0);
    }

    private TableView paginar(int indexPagina) {
        try {
            this.pagina.setNumeroPagina(indexPagina + 1);
            List<AtletaInscricao> lista = this.secretario.pesquisarAtletaInscricao(this.filtroNome, this.filtroCpf, this.filtroNomeProva, this.filtroDataInicio, this.filtroDataFim, this.pagina);
            this.tabInscricao.getItems().clear();
            this.tabInscricao.getItems().addAll(lista);
            this.paginacao.setPageCount(pagina.getNumeroPaginas());
        } catch (NegocioException ex) {
            this.cj.exibirErro(cm.getMensagem("erro_control_pesquisar", "inscricao"), GeralUtil.getMensagemOriginalErro(ex));
        }
        return this.tabInscricao;
    }
}
