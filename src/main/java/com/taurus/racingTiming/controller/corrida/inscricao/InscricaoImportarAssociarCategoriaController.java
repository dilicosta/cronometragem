package com.taurus.racingTiming.controller.corrida.inscricao;

import com.taurus.racingTiming.controller.corrida.inscricao.it.IInscricaoImportarAssociarCategoriaController;
import com.taurus.exception.NegocioException;
import com.taurus.javafx.componente.GenericListCell;
import com.taurus.javafx.controller.FXMLBaseController;
import com.taurus.javafx.ds.GenericLinhaView;
import com.taurus.racingTiming.controller.corrida.IProvaController;
import com.taurus.racingTiming.entidade.corrida.CategoriaDaProva;
import com.taurus.racingTiming.entidade.corrida.Prova;
import com.taurus.racingTiming.negocio.IImportador;
import com.taurus.racingTiming.negocio.ISecretario;
import com.taurus.racingTiming.ui.ds.corrida.CategoriaProvaDataSource;
import com.taurus.racingTiming.util.ListaConstantes;
import com.taurus.util.GeralUtil;
import com.taurus.util.ListaConstantesBase;
import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.Tooltip;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 *
 * @author Diego
 */
@Component
public class InscricaoImportarAssociarCategoriaController extends FXMLBaseController implements IInscricaoImportarAssociarCategoriaController {

    private static final Log LOG = LogFactory.getLog(InscricaoImportarAssociarCategoriaController.class);

    private Prova prova;
    private File file;
    private Integer indiceColunaCategoriaXls;
    private Integer linhasCabecalho;

    private CategoriaProvaDataSource categoriaProvaDataSource;

    private Map<String, CategoriaDaProva> mapCategoriaAssociada = new HashMap<>();

    @Autowired
    private IImportador importador;
    @Autowired
    private ISecretario secretario;
    @Autowired
    private IProvaController provaController;

    @FXML
    private Button btnAssociar;
    @FXML
    private Button btnAbrirProva;
    @FXML
    private Button btnExcluir;
    @FXML
    private ListView<String> listCategoriaXLS;
    @FXML
    private ListView<GenericLinhaView<CategoriaDaProva>> listCategoriaProva;
    @FXML
    private ListView<String> listCategoriaAssociada;

    @Value("${fxml.importarInscricaoAssociarCategoria.view}")
    @Override
    public void setFxmlFilePath(String filePath) {
        this.fxmlFilePath = filePath;
    }

    @Override
    public void reinicializarJanelaEspecifico() {
        this.mapCategoriaAssociada.clear();
        this.listCategoriaAssociada.getItems().clear();
        this.listCategoriaProva.getItems().clear();
        this.listCategoriaXLS.getItems().clear();
    }

    @Override
    public void aoFecharJanelaEspecifico() {
    }

    @Override
    public void aoAbrirJanela() {
        // Se a janela não foi reinicializada, mostra os dados previamente carregados
        if (super.novaJanela) {
            try {
                this.listCategoriaXLS.getItems().clear();
                this.listCategoriaXLS.getItems().addAll(this.importador.carregarCategoriaXls(this.file, this.indiceColunaCategoriaXls, this.linhasCabecalho));
            } catch (NegocioException ex) {
                super.cj.exibirErro("Não foi possivel listar as categorias do arquivo excel", GeralUtil.getMensagemOriginalErro(ex));
            }
            this.carregarCategoriasProva();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.btnAssociar.getStyleClass().add(ListaConstantesBase.EstiloCss.BOTAO_TROCAR.getValor());
        this.btnAssociar.setTooltip(new Tooltip("associar categoria"));
        this.btnAbrirProva.getStyleClass().add(ListaConstantesBase.EstiloCss.BOTAO_ADICIONAR.getValor());
        this.btnAbrirProva.setTooltip(new Tooltip("adicionar categoria à prova"));
        this.btnExcluir.getStyleClass().add(ListaConstantesBase.EstiloCss.BOTAO_EXCLUIR.getValor());
        this.btnExcluir.setTooltip(new Tooltip("excluir associação"));

        this.listCategoriaProva.setCellFactory((ListView<GenericLinhaView<CategoriaDaProva>> param) -> {
            return new GenericListCell<CategoriaDaProva>() {
                @Override
                protected void eventoDuploClique(GenericLinhaView<CategoriaDaProva> item) {
                }
            };
        });
    }

    @Override
    public void setFile(File file) {
        this.file = file;
    }

    @Override
    public void setProva(Prova prova) {
        this.prova = prova;
    }

    @Override
    public void setIndiceColunaCategoriaXls(Integer indiceColunaCategoriaXls) {
        this.indiceColunaCategoriaXls = indiceColunaCategoriaXls;
    }

    @Override
    public void setLinhasCabecalho(Integer linhasCabecalho) {
        this.linhasCabecalho = linhasCabecalho;
    }

    @Override
    public Map<String, CategoriaDaProva> getMapCategoriaAssociada() {
        return mapCategoriaAssociada;
    }

    @Override
    public Integer getTotalCategoriaXls() {
        return this.listCategoriaXLS.getItems().size() + this.listCategoriaAssociada.getItems().size();
    }

    @FXML
    public void associar() {
        GenericLinhaView<CategoriaDaProva> categoriaProvaView = this.listCategoriaProva.getSelectionModel().getSelectedItem();
        String categoriaXls = this.listCategoriaXLS.getSelectionModel().getSelectedItem();

        if (categoriaProvaView == null || categoriaXls == null) {
            super.cj.exibirAviso("Selecione as categorias nas respectivas listas para serem associadas.");
        } else {
            String nomeAssociacao = categoriaXls + " / " + categoriaProvaView.getBean().getCategoriaAtleta().getNome();
            this.mapCategoriaAssociada.put(categoriaXls, categoriaProvaView.getBean());
            this.listCategoriaAssociada.getItems().add(nomeAssociacao);
            this.listCategoriaXLS.getItems().remove(categoriaXls);
            this.listCategoriaProva.getItems().remove(categoriaProvaView);
        }
    }

    @FXML
    public void excluir() {
        String categoriaAssociada = this.listCategoriaAssociada.getSelectionModel().getSelectedItem();

        if (categoriaAssociada == null) {
            super.cj.exibirAviso("Selecione a categoria associada na respectiva lista para excluí-la.");
        } else {
            String categoriaXls = categoriaAssociada.split(" / ")[0];
            CategoriaDaProva categoriaProva = this.mapCategoriaAssociada.get(categoriaXls);
            this.mapCategoriaAssociada.remove(categoriaXls);

            this.listCategoriaAssociada.getItems().remove(categoriaAssociada);
            this.listCategoriaXLS.getItems().add(categoriaXls);
            this.categoriaProvaDataSource.adicionar(categoriaProva);
        }
    }

    @FXML
    public void abrirProva() {
        this.provaController.setOperacao(ListaConstantesBase.Operacao.EDITAR);
        this.provaController.setProvaEdicao(this.prova);
        super.cj.abrirJanela(ListaConstantes.Stages.PROVA.ordinal(), ListaConstantes.Stages.ASSOCIAR_CATEGORIA_XLS.ordinal(), IProvaController.class, "prova", true);
        this.prova = this.provaController.getProvaEdicao();
        this.provaController.setProvaEdicao(null);
        this.carregarCategoriasProva();
    }

    private void carregarCategoriasProva() {
        List<CategoriaDaProva> categoriasDaProva;
        try {
            categoriasDaProva = this.secretario.pesquisarCategoriaDaProva(this.prova);
            this.categoriaProvaDataSource = new CategoriaProvaDataSource(categoriasDaProva);
        } catch (NegocioException ex) {
            super.cj.exibirErro("Não foi possivel listar as categorias da Prova", GeralUtil.getMensagemOriginalErro(ex));
            return;
        }
        if (this.mapCategoriaAssociada.isEmpty()) {
            this.listCategoriaProva.setItems(this.categoriaProvaDataSource.getData());
        } else {
            for (CategoriaDaProva catProva : categoriasDaProva) {
                boolean contemCategoria = false;
                if (this.mapCategoriaAssociada.values().contains(catProva)) {
                    contemCategoria = true;
                } else {
                    contemCategoria = this.categoriaProvaDataSource.getListaBean().contains(catProva);
                }
                if (!contemCategoria) {
                    this.categoriaProvaDataSource.adicionar(catProva);
                }
            }
            this.listCategoriaProva.setItems(this.categoriaProvaDataSource.getData());
        }
    }
}
