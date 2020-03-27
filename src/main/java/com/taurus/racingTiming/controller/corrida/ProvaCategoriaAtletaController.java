package com.taurus.racingTiming.controller.corrida;

import com.taurus.racingTiming.controller.corrida.inscricao.it.IInscricaoController;
import com.taurus.exception.NegocioException;
import com.taurus.javafx.componente.TableViewDuploClique;
import com.taurus.javafx.controller.FXMLBaseController;
import com.taurus.racingTiming.entidade.corrida.AtletaInscricao;
import com.taurus.racingTiming.entidade.corrida.CategoriaDaProva;
import com.taurus.racingTiming.negocio.ISecretario;
import com.taurus.racingTiming.pojo.SituacaoProva;
import com.taurus.racingTiming.util.ListaConstantes;
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
public class ProvaCategoriaAtletaController extends FXMLBaseController implements IProvaCategoriaAtletaController {

    private static final Log LOG = LogFactory.getLog(SituacaoProvaController.class);

    private static final int ITENS_POR_PAGINA = 20;
    private Pagina pagina = new Pagina(1, ITENS_POR_PAGINA, null);

    @Autowired
    private IInscricaoController inscricaoController;

    @Autowired
    private ISecretario secretario;

    @Parametro
    private SituacaoProva situacaoProva;
    @Parametro
    private CategoriaDaProva categoriaDaProva;

    @FXML
    private TextField txtNome;
    @FXML
    private TextField txtData;
    @FXML
    private TextField txtSituacao;
    @FXML
    private TextField txtTotalInscritos;

    @FXML
    private Pagination paginacao;
    @FXML
    private TableView<AtletaInscricao> tabInscricao;
    @FXML
    private TableColumn colCategoria;
    @FXML
    private TableColumn<AtletaInscricao, String> colNumero;
    @FXML
    private TableColumn<AtletaInscricao, String> colNome;
    @FXML
    private TableColumn<AtletaInscricao, String> colEquipe;

    @Value("${fxml.provaCategoriaAtleta.view}")
    @Override
    public void setFxmlFilePath(String filePath) {
        this.fxmlFilePath = filePath;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.paginacao.setPageFactory(this::paginar);
        this.configurarTabelaInscricoes();
    }

    @Override
    public void reinicializarJanelaEspecifico() {
        this.tabInscricao.getItems().clear();
        pagina = new Pagina(1, ITENS_POR_PAGINA, null);
    }

    @Override
    public void aoAbrirJanela() {
        this.exibirSituacaoProvaInscricoes();
    }

    @Override
    public void aoFecharJanelaEspecifico() {
    }

    private TableView paginar(int indexPagina) {
        try {
            this.pagina.setNumeroPagina(indexPagina + 1);
            List<AtletaInscricao> inscricoes = this.secretario.pesquisarAtletaInscricao(this.categoriaDaProva, this.pagina);
            this.tabInscricao.getItems().clear();
            this.tabInscricao.getItems().addAll(inscricoes);
            this.paginacao.setPageCount(pagina.getNumeroPaginas());
        } catch (NegocioException ex) {
            this.cj.exibirErro(cm.getMensagem("erro_control_pesquisar", "inscricao"), GeralUtil.getMensagemOriginalErro(ex));
        }
        return this.tabInscricao;
    }

    private void configurarTabelaInscricoes() {
        this.colNumero.getStyleClass().add(ListaConstantesBase.EstiloCss.TAB_COL_CENTRO.getValor());
        this.colNome.getStyleClass().add(ListaConstantesBase.EstiloCss.TAB_COL_ESQUERDO.getValor());
        this.colEquipe.getStyleClass().add(ListaConstantesBase.EstiloCss.TAB_COL_ESQUERDO.getValor());

        this.colNumero.setCellValueFactory(linha -> new ReadOnlyObjectWrapper(linha.getValue().getNumeroAtleta()));
        this.colNome.setCellValueFactory(linha -> new ReadOnlyObjectWrapper(linha.getValue().getAtleta().getNome()));
        this.colEquipe.setCellValueFactory(linha -> new ReadOnlyObjectWrapper(linha.getValue().getEquipe()));
        this.configurarDuploCliqueCategoria();
    }

    private void configurarDuploCliqueCategoria() {
        // Evento duplo clique na tabela
        tabInscricao.setRowFactory(new TableViewDuploClique<AtletaInscricao>(true) {
            @Override
            public AtletaInscricao eventoDuploClique(AtletaInscricao atletaInscricao) {
                ProvaCategoriaAtletaController.this.editarInscricao(atletaInscricao);
                if (!ProvaCategoriaAtletaController.this.categoriaDaProva.equals(atletaInscricao.getCategoria())) {
                    this.removerLinha();
                }
                return atletaInscricao;
            }
        });
    }

    private void exibirSituacaoProvaInscricoes() {

        this.txtNome.setText(this.situacaoProva.getProva().getNome());
        this.txtData.setText(FormatarUtil.localDateToString(this.situacaoProva.getProva().getData()));
        this.txtSituacao.setText(this.situacaoProva.getProva().getStatus().getDescricao());
        this.txtTotalInscritos.setText(this.situacaoProva.getNumeroInscritos().toString());
        this.colCategoria.setText(this.categoriaDaProva.getCategoriaAtleta().getNome());
        this.paginacao.getPageFactory().call(0);
    }

    private void editarInscricao(AtletaInscricao atletaInscricao) {
        switch (this.categoriaDaProva.getProva().getStatus()) {
            case INSCRICAO_ABERTA:
                this.inscricaoController.setOperacao(ListaConstantesBase.Operacao.EDITAR);
                break;
            case ENCERRADA_APURANDO_RESULTADOS:
                this.inscricaoController.setOperacao(ListaConstantesBase.Operacao.EDITAR);
                this.inscricaoController.setEdicaoApuracao(true);
                break;
            default:
                this.inscricaoController.setOperacao(ListaConstantesBase.Operacao.LEITURA);
        }
        this.inscricaoController.setAtletaInscricaoEdicao(atletaInscricao);
        super.cj.abrirJanela(ListaConstantes.Stages.INSCRICAO.ordinal(), ListaConstantes.Stages.PROVA_CATEGORIA_ATLETA.ordinal(), IInscricaoController.class, "inscricao", true);
    }

    @Override
    public void setSituacaoProva(SituacaoProva situacaoProva) {
        this.situacaoProva = situacaoProva;
    }

    @Override
    public void setCategoriaDaProva(CategoriaDaProva categoriaDaProva) {
        this.categoriaDaProva = categoriaDaProva;
    }

}
