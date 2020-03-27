package com.taurus.racingTiming.controller.atleta;

import com.taurus.exception.AvisoNegocioException;
import com.taurus.exception.NegocioException;
import com.taurus.javafx.componente.BotaoCelulaTableValueFactory;
import com.taurus.javafx.componente.BotaoTableColumnFactory;
import com.taurus.javafx.componente.TableViewDuploClique;
import com.taurus.javafx.controller.FXMLBaseController;
import com.taurus.javafx.ds.GenericLinhaView;
import com.taurus.racingTiming.entidade.atleta.CategoriaAtleta;
import com.taurus.racingTiming.negocio.IEstagiario;
import com.taurus.racingTiming.ui.ds.atleta.CategoriaAtletaDataSource;
import com.taurus.racingTiming.util.ListaConstantes;
import com.taurus.racingTiming.util.ListaSistema;
import com.taurus.util.GeralUtil;
import com.taurus.util.ListaConstantesBase;
import com.taurus.util.Pagina;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Pagination;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class CadastroCategoriaAtletaController extends FXMLBaseController {

    private static final Log LOG = LogFactory.getLog(CadastroCategoriaAtletaController.class);
    private static final int ITENS_POR_PAGINA = 12;

    private CategoriaAtletaDataSource dataSource;

    public CadastroCategoriaAtletaController() {
        super();
    }

    @Autowired
    private IEstagiario estagiario;

    @Autowired
    private ListaSistema listaSistema;

    @Autowired
    private ICategoriaAtletaController categoriaAtletaController;
    private Pagina pagina = null;

    @FXML
    Pagination paginacao;
    @FXML
    private TableView<GenericLinhaView<CategoriaAtleta>> tabCategoriaAtleta;
    @FXML
    private TableColumn<GenericLinhaView, String> colNome;
    @FXML
    private TableColumn<GenericLinhaView, String> colDescricao;
    @FXML
    private TableColumn<GenericLinhaView, String> colSexo;
    @FXML
    private TableColumn<GenericLinhaView, String> colIdadeMin;
    @FXML
    private TableColumn<GenericLinhaView, String> colIdadeMax;
    @FXML
    private TableColumn colExcluir;

    @Value("${fxml.cadastroCategoriaAtleta.view}")
    @Override
    public void setFxmlFilePath(String filePath) {
        this.fxmlFilePath = filePath;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.paginacao.setPageFactory(this::paginar);

        colNome.setCellValueFactory(new PropertyValueFactory("coluna1"));
        colDescricao.setCellValueFactory(new PropertyValueFactory("coluna2"));
        colSexo.setCellValueFactory(new PropertyValueFactory("coluna3"));
        colIdadeMin.setCellValueFactory(new PropertyValueFactory("coluna4"));
        colIdadeMax.setCellValueFactory(new PropertyValueFactory("coluna5"));

        colNome.getStyleClass().add(ListaConstantesBase.EstiloCss.TAB_COL_ESQUERDO.getValor());
        colDescricao.getStyleClass().add(ListaConstantesBase.EstiloCss.TAB_COL_ESQUERDO.getValor());
        colSexo.getStyleClass().add(ListaConstantesBase.EstiloCss.TAB_COL_CENTRO.getValor());
        colIdadeMin.getStyleClass().add(ListaConstantesBase.EstiloCss.TAB_COL_CENTRO.getValor());
        colIdadeMax.getStyleClass().add(ListaConstantesBase.EstiloCss.TAB_COL_CENTRO.getValor());

        colExcluir.getStyleClass().add(ListaConstantesBase.EstiloCss.TAB_COL_CENTRO.getValor());
        colExcluir.setCellValueFactory(new BotaoCelulaTableValueFactory());
        colExcluir.setCellFactory(new BotaoTableColumnFactory<GenericLinhaView<CategoriaAtleta>>("excluir categoria de atleta", ListaConstantesBase.EstiloCss.BOTAO_EXCLUIR.getValor()) {
            @Override
            public void eventoBotao(ActionEvent actionEvent, GenericLinhaView<CategoriaAtleta> linhaView) {
                remover(linhaView);
            }
        });

        // Evento duplo clique na tabela
        // Abre a Janela de Categoria de Atleta para edicao
        tabCategoriaAtleta.setRowFactory(new TableViewDuploClique<GenericLinhaView<CategoriaAtleta>>() {
            @Override
            public GenericLinhaView eventoDuploClique(GenericLinhaView<CategoriaAtleta> linhaView) {
                editarCategoriaAtleta(linhaView);
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
        this.categoriaAtletaController.setOperacao(ListaConstantesBase.Operacao.NOVO);
        super.cj.abrirJanela(ListaConstantes.Stages.CATEGORIA_ATLETA.ordinal(), ListaConstantes.Stages.CADASTRO_CATEGORIA_ATLETA.ordinal(), ICategoriaAtletaController.class, "categoria_atleta", true);
        // Atualiza a pagina
        this.paginacao.getPageFactory().call(this.paginacao.getCurrentPageIndex());
    }

    @Override
    public void aoAbrirJanela() {
        this.pagina = new Pagina(1, ITENS_POR_PAGINA, null);
        int indiceInicio = (pagina.getNumeroPagina() - 1) * pagina.getItensPorPagina();
        int indiceFim = indiceInicio + pagina.getItensPorPagina();
        int tamLista = this.listaSistema.getListaCategoriaAtleta().size();
        this.pagina.setTotalItens(Integer.toUnsignedLong(tamLista));
        this.dataSource = new CategoriaAtletaDataSource(this.listaSistema.getListaCategoriaAtleta().subList(indiceInicio, tamLista > indiceFim ? indiceFim : tamLista));
        this.tabCategoriaAtleta.setItems(this.dataSource.getData());
    }

    @Override
    public void aoFecharJanelaEspecifico() {
    }

    private void remover(GenericLinhaView linhaView) {
        if (cj.exibirDialogSimNao("titulo_apagar", "aviso_pergunta_excluir", null, "categoria_atleta")) {
            CategoriaAtleta categoriaAtleta = this.dataSource.getBean(linhaView);
            try {
                estagiario.excluirCategoriaAtleta(categoriaAtleta);
                this.dataSource.remover(linhaView);
                this.tabCategoriaAtleta.refresh();
                this.listaSistema.atualizarListaCategoriaAtleta();
            } catch (NegocioException ex) {
                super.cj.exibirErro(cm.getMensagem("erro_control_excluir", "categoria_atleta"), GeralUtil.getMensagemOriginalErro(ex));
            } catch (AvisoNegocioException ex) {
                super.cj.exibirAviso(ex.getMessage());
            }
        }
    }

    private void editarCategoriaAtleta(GenericLinhaView linhaView) {
        CategoriaAtleta categoriaAtleta = this.dataSource.getBean(linhaView);
        this.categoriaAtletaController.setOperacao(ListaConstantesBase.Operacao.EDITAR);
        this.categoriaAtletaController.setCategoriaAtletaEdicao(categoriaAtleta);
        super.cj.abrirJanela(ListaConstantes.Stages.CATEGORIA_ATLETA.ordinal(), ListaConstantes.Stages.CADASTRO_CATEGORIA_ATLETA.ordinal(), ICategoriaAtletaController.class, "categoria_atleta", true);
        this.dataSource.atualizar(linhaView, categoriaAtleta);
    }

    private TableView paginar(int indexPagina) {
        this.pagina.setNumeroPagina(indexPagina + 1);
        //List<CategoriaAtleta> lista = this.estagiario.pesquisarCategoriaAtletaTodos(pagina);
        int indiceInicio = (pagina.getNumeroPagina() - 1) * pagina.getItensPorPagina();
        int indiceFim = indiceInicio + pagina.getItensPorPagina();
        int tamLista = this.listaSistema.getListaCategoriaAtleta().size();
        this.pagina.setTotalItens(Integer.toUnsignedLong(tamLista));
        List<CategoriaAtleta> lista = this.listaSistema.getListaCategoriaAtleta().subList(indiceInicio, tamLista > indiceFim ? indiceFim : tamLista);

        this.dataSource = new CategoriaAtletaDataSource(lista);
        this.tabCategoriaAtleta.setItems(dataSource.getData());
        this.paginacao.setPageCount(pagina.getNumeroPaginas());
        return this.tabCategoriaAtleta;
    }
}
