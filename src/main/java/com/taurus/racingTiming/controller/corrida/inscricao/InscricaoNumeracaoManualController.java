package com.taurus.racingTiming.controller.corrida.inscricao;

import com.taurus.exception.NegocioException;
import com.taurus.javafx.controller.FXMLBaseController;
import com.taurus.javafx.tableView.TableCellCommitGeneric;
import com.taurus.javafx.tableView.TableCellEditFactoryGeneric;
import com.taurus.racingTiming.controller.corrida.inscricao.it.IInscricaoNumeracaoManualController;
import com.taurus.racingTiming.entidade.corrida.AtletaInscricao;
import com.taurus.racingTiming.entidade.corrida.CategoriaDaProva;
import com.taurus.racingTiming.negocio.ISecretario;
import com.taurus.util.FormatarUtil;
import com.taurus.util.GeralUtil;
import com.taurus.util.ListaConstantesBase;
import com.taurus.util.Pagina;
import com.taurus.util.annotation.Parametro;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.fxml.FXML;
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
public class InscricaoNumeracaoManualController extends FXMLBaseController implements IInscricaoNumeracaoManualController {

    public InscricaoNumeracaoManualController() {
        super();
    }

    private static final Log LOG = LogFactory.getLog(InscricaoNumeracaoManualController.class);
    private static final int ITENS_POR_PAGINA = 18;

    @Parametro
    private CategoriaDaProva categoriaDaProva;

    @Parametro
    private Long totalInscritos;

    private Pagina pagina = new Pagina(1, ITENS_POR_PAGINA, null);

    @Autowired
    private ISecretario secretario;

    @FXML
    private TextField txtNomeProva;
    @FXML
    private TextField txtDataProva;
    @FXML
    private TextField txtSituacaoProva;
    @FXML
    private TextField txtCategoria;
    @FXML
    private TextField txtInscritos;
    @FXML
    private TextField txtNumAutomatica;
    @FXML
    private TextField txtDigito;
    @FXML
    private TextField txtInicioNumeracao;
    @FXML
    private TextField txtFimNumeracao;

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

    @Value("${fxml.inscricaoNumeracaoManual.view}")
    @Override
    public void setFxmlFilePath(String filePath) {
        this.fxmlFilePath = filePath;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.paginacao.setPageFactory(this::paginar);

        this.configurarTabela();
        this.configurarEdicaoNumeroAtleta();
    }

    @Override
    public void reinicializarJanelaEspecifico() {
        this.tabInscricao.getItems().clear();
    }

    @Override
    public void aoAbrirJanela() {
        this.txtNomeProva.setText(this.categoriaDaProva.getProva().getNome());
        this.txtDataProva.setText(FormatarUtil.localDateToString(this.categoriaDaProva.getProva().getData()));
        this.txtSituacaoProva.setText(this.categoriaDaProva.getProva().getStatus().getDescricao());

        this.txtCategoria.setText(this.categoriaDaProva.getCategoriaAtleta().getNome());
        this.txtNumAutomatica.setText(this.categoriaDaProva.getNumeracaoAutomatica().getDescricao());
        this.txtDigito.setText("" + (this.categoriaDaProva.getDigitosNumeracao() == null ? "" : this.categoriaDaProva.getDigitosNumeracao()));
        this.txtInicioNumeracao.setText("" + (this.categoriaDaProva.getInicioNumeracao() == null ? "" : this.categoriaDaProva.getInicioNumeracao()));
        this.txtFimNumeracao.setText("" + (this.categoriaDaProva.getFimNumeracao() == null ? "" : this.categoriaDaProva.getFimNumeracao()));

        try {
            this.totalInscritos = this.secretario.pesquisarTotalInscricoes(this.categoriaDaProva);
            this.txtInscritos.setText("" + this.totalInscritos);
        } catch (NegocioException ex) {
            super.cj.exibirErro(cm.getMensagem("erro_pesquisa", "total inscrições da categoria"), GeralUtil.getMensagemOriginalErro(ex));
        }

        this.reiniciarPaginacao();

    }

    @Override
    public void aoFecharJanelaEspecifico() {

    }

    private void configurarEdicaoNumeroAtleta() {
        // para editar o campo nome
        this.colNumero.setCellFactory(col -> new TableCellEditFactoryGeneric<AtletaInscricao, Integer>(true, TableCellEditFactoryGeneric.Tipo.NUMERO_INTEGER) {
            @Override
            public boolean isValido(Integer digitos) {
                return digitos > 0;
            }
        });

        this.colNumero.setOnEditCommit(new TableCellCommitGeneric<AtletaInscricao, Integer>(true) {
            @Override
            public void atualizar(AtletaInscricao atletaInscricao, Integer novoValor, Integer valorAntigo, boolean valido) {
                if (novoValor == null || novoValor > 0) {
                    atletaInscricao.setNumeroAtleta(novoValor);
                }
                try {
                    secretario.atualizarAtletaInscricao(atletaInscricao);
                } catch (NegocioException ex) {
                    cj.exibirErro(cm.getMensagem("erro_control_atualizar", "inscricao"), GeralUtil.getMensagemOriginalErro(ex));
                }
            }
        });
    }

    private void configurarTabela() {
        this.colNumero.setCellValueFactory(linha -> new ReadOnlyObjectWrapper(linha.getValue().getNumeroAtleta()));
        this.colNome.setCellValueFactory(linha -> new ReadOnlyObjectWrapper(linha.getValue().getAtleta().getNome()));
        this.colCategoria.setCellValueFactory(linha -> new ReadOnlyObjectWrapper(linha.getValue().getCategoria().getCategoriaAtleta().getNome()));
        this.colEquipe.setCellValueFactory(linha -> new ReadOnlyObjectWrapper(linha.getValue().getEquipe()));

        this.colNumero.getStyleClass().add(ListaConstantesBase.EstiloCss.TAB_COL_CENTRO.getValor());
        this.colNome.getStyleClass().add(ListaConstantesBase.EstiloCss.TAB_COL_ESQUERDO.getValor());
        this.colCategoria.getStyleClass().add(ListaConstantesBase.EstiloCss.TAB_COL_ESQUERDO.getValor());
        this.colEquipe.getStyleClass().add(ListaConstantesBase.EstiloCss.TAB_COL_ESQUERDO.getValor());
    }

    @Override
    public void setCategoriaDaProva(CategoriaDaProva categoriaDaProva) {
        this.categoriaDaProva = categoriaDaProva;
    }

    private void reiniciarPaginacao() {
        this.pagina.setTotalItens(null);
        this.paginacao.setCurrentPageIndex(0);
        this.paginacao.getPageFactory().call(0);
    }

    private TableView paginar(int indexPagina) {
        List<AtletaInscricao> atletas = null;

        try {
            this.pagina.setNumeroPagina(indexPagina + 1);
            atletas = this.secretario.pesquisarAtletaInscricaoOrdenadoPorNumero(categoriaDaProva, pagina);
        } catch (NegocioException ex) {
            this.cj.exibirErro(cm.getMensagem("erro_control_pesquisar", "inscricao"), GeralUtil.getMensagemOriginalErro(ex));
        }
        this.tabInscricao.getItems().clear();
        this.tabInscricao.getItems().addAll(atletas);
        this.paginacao.setPageCount(pagina.getNumeroPaginas());
        return this.tabInscricao;
    }
}
