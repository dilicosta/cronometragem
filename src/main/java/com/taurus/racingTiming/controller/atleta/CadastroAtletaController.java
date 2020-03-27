package com.taurus.racingTiming.controller.atleta;

import com.taurus.exception.AvisoNegocioException;
import com.taurus.exception.NegocioException;
import com.taurus.javafx.componente.BotaoCelulaTableValueFactory;
import com.taurus.javafx.componente.BotaoTableColumnFactory;
import com.taurus.javafx.componente.TableViewDuploClique;
import com.taurus.javafx.controller.FXMLBaseController;
import com.taurus.javafx.ds.GenericLinhaView;
import com.taurus.racingTiming.entidade.atleta.Atleta;
import com.taurus.racingTiming.negocio.ISecretario;
import com.taurus.racingTiming.ui.ds.atleta.AtletaDataSource;
import com.taurus.racingTiming.util.ListaConstantes;
import com.taurus.util.FormatarUtil;
import com.taurus.util.GeralUtil;
import com.taurus.util.ListaConstantesBase;
import com.taurus.util.Pagina;
import com.taurus.util.annotation.Mascara;
import com.taurus.util.annotation.Parametro;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
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
public class CadastroAtletaController extends FXMLBaseController {

    private static final Log LOG = LogFactory.getLog(CadastroAtletaController.class);
    private static final int ITENS_POR_PAGINA = 15;

    private AtletaDataSource dataSource;

    public CadastroAtletaController() {
        super();
    }

    @Autowired
    private ISecretario secretario;

    @Autowired
    private IAtletaController atletaController;
    private Pagina pagina = null;

    @Parametro
    private String filtroNome;
    @Parametro
    private String filtroCpf;

    @FXML
    @Mascara(tipo = Mascara.TipoMascara.TEXT_FIELD_MAXIMO_CARACTER, tamanhoMax = "30")
    private TextField txtNome;
    @FXML
    @Mascara(tipo = Mascara.TipoMascara.TEXT_FIELD_CPF)
    private TextField txtCpf;
    @FXML
    private Pagination paginacao;
    @FXML
    private TableView<GenericLinhaView<Atleta>> tabAtleta;
    @FXML
    private TableColumn<GenericLinhaView, String> colNome;
    @FXML
    private TableColumn<GenericLinhaView, String> colCpf;
    @FXML
    private TableColumn<GenericLinhaView, String> colNascimento;
    @FXML
    private TableColumn<GenericLinhaView, String> colSexo;
    @FXML
    private TableColumn colExcluir;

    @Value("${fxml.cadastroAtleta.view}")
    @Override
    public void setFxmlFilePath(String filePath) {
        this.fxmlFilePath = filePath;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.paginacao.setPageFactory(this::paginar);

        colNome.setCellValueFactory(new PropertyValueFactory("coluna1"));
        colCpf.setCellValueFactory(new PropertyValueFactory("coluna2"));
        colNascimento.setCellValueFactory(new PropertyValueFactory("coluna3"));
        colSexo.setCellValueFactory(new PropertyValueFactory("coluna4"));

        colNome.getStyleClass().add(ListaConstantesBase.EstiloCss.TAB_COL_ESQUERDO.getValor());
        colCpf.getStyleClass().add(ListaConstantesBase.EstiloCss.TAB_COL_CENTRO.getValor());
        colNascimento.getStyleClass().add(ListaConstantesBase.EstiloCss.TAB_COL_CENTRO.getValor());
        colSexo.getStyleClass().add(ListaConstantesBase.EstiloCss.TAB_COL_CENTRO.getValor());

        colExcluir.getStyleClass().add(ListaConstantesBase.EstiloCss.TAB_COL_CENTRO.getValor());
        colExcluir.setCellValueFactory(new BotaoCelulaTableValueFactory());
        colExcluir.setCellFactory(new BotaoTableColumnFactory<GenericLinhaView<Atleta>>("excluir atleta", ListaConstantesBase.EstiloCss.BOTAO_EXCLUIR.getValor()) {
            @Override
            public void eventoBotao(ActionEvent actionEvent, GenericLinhaView<Atleta> linhaView) {
                remover(linhaView);
            }
        });

        // Evento duplo clique na tabela
        // Abre a Janela de Atleta para edicao
        tabAtleta.setRowFactory(new TableViewDuploClique<GenericLinhaView<Atleta>>() {
            @Override
            public GenericLinhaView eventoDuploClique(GenericLinhaView<Atleta> linhaView) {
                editarAtleta(linhaView);
                return linhaView;
            }
        });
    }

    @Override
    public void reinicializarJanelaEspecifico() {
        this.dataSource = null;
        this.pagina = new Pagina(1, ITENS_POR_PAGINA, null);

    }

    @FXML
    public void novo(ActionEvent actionEvent) {
        this.atletaController.setOperacao(ListaConstantesBase.Operacao.NOVO);
        super.cj.abrirJanela(ListaConstantes.Stages.ATLETA.ordinal(), ListaConstantes.Stages.CADASTRO_ATLETA.ordinal(), IAtletaController.class, "atleta", true);
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

    private void remover(GenericLinhaView linhaView) {
        if (cj.exibirDialogSimNao("titulo_apagar", "aviso_pergunta_excluir", null, "atleta")) {
            Atleta atleta = this.dataSource.getBean(linhaView);
            try {
                secretario.excluirAtleta(atleta);
                this.dataSource.remover(linhaView);
                this.tabAtleta.refresh();
            } catch (NegocioException ex) {
                super.cj.exibirErro(cm.getMensagem("erro_control_excluir", "atleta"), GeralUtil.getMensagemOriginalErro(ex));
            } catch (AvisoNegocioException ex) {
                super.cj.exibirAviso(ex.getMessage());
            }
        }
    }

    private void editarAtleta(GenericLinhaView linhaView) {
        Atleta atleta = this.dataSource.getBean(linhaView);
        this.atletaController.setOperacao(ListaConstantesBase.Operacao.EDITAR);
        this.atletaController.setAtletaEdicao(atleta);
        super.cj.abrirJanela(ListaConstantes.Stages.ATLETA.ordinal(), ListaConstantes.Stages.CADASTRO_ATLETA.ordinal(), IAtletaController.class, "atleta", true);
        this.dataSource.atualizar(linhaView, atleta);
    }

    @FXML
    public void filtrar() {
        this.filtroNome = this.txtNome.getText();
        this.filtroCpf = FormatarUtil.removerMascaraNumero(this.txtCpf.getText());

        this.pagina = new Pagina(1, ITENS_POR_PAGINA, null);
        this.paginacao.getPageFactory().call(0);
    }

    private TableView paginar(int indexPagina) {
        try {
            this.pagina.setNumeroPagina(indexPagina + 1);
            List<Atleta> lista = this.secretario.pesquisarAtleta(this.filtroNome, this.filtroCpf, this.pagina);
            this.dataSource = new AtletaDataSource(lista);
            this.tabAtleta.setItems(dataSource.getData());
            this.paginacao.setPageCount(pagina.getNumeroPaginas());
        } catch (NegocioException ex) {
            this.cj.exibirErro(cm.getMensagem("erro_control_pesquisar", "atleta"), GeralUtil.getMensagemOriginalErro(ex));
        }
        return this.tabAtleta;
    }
}
