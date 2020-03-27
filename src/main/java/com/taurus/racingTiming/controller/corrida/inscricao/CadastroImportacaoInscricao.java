package com.taurus.racingTiming.controller.corrida.inscricao;

import com.taurus.racingTiming.controller.corrida.inscricao.it.ICadastroImportacaoInscricao;
import com.taurus.racingTiming.controller.corrida.inscricao.it.IImportarInscricaoXlsController;
import com.taurus.exception.AvisoNegocioException;
import com.taurus.exception.NegocioException;
import com.taurus.javafx.componente.BotaoCelulaTableValueFactory;
import com.taurus.javafx.componente.BotaoTableColumnFactory;
import com.taurus.javafx.controller.FXMLBaseController;
import com.taurus.racingTiming.entidade.corrida.Prova;
import com.taurus.racingTiming.negocio.ISecretario;
import com.taurus.racingTiming.pojo.ImportacaoInscricao;
import com.taurus.racingTiming.util.ListaConstantes.Stages;
import com.taurus.util.FormatarUtil;
import com.taurus.util.GeralUtil;
import com.taurus.util.ListaConstantesBase;
import com.taurus.util.annotation.Parametro;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class CadastroImportacaoInscricao extends FXMLBaseController implements ICadastroImportacaoInscricao {

    private static final Log LOG = LogFactory.getLog(CadastroImportacaoInscricao.class);

    @Autowired
    private IImportarInscricaoXlsController importarInscricaoXlsController;
    @Autowired
    private ISecretario secretario;

    @Parametro
    private Prova prova;

    public CadastroImportacaoInscricao() {
        super();
    }

    @FXML
    private TextField txtNomeProva;
    @FXML
    private TextField txtData;

    @FXML
    private TableView<ImportacaoInscricao> tabImportacao;
    @FXML
    private TableColumn<ImportacaoInscricao, String> colData;
    @FXML
    private TableColumn<ImportacaoInscricao, Long> colInscritos;
    @FXML
    private TableColumn colExcluir;

    @FXML
    ProgressIndicator indicadorProgresso;

    @Value("${fxml.cadastroImportacaoInscricao.view}")
    @Override
    public void setFxmlFilePath(String filePath) {
        this.fxmlFilePath = filePath;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.colData.setCellValueFactory(linha -> new ReadOnlyObjectWrapper(FormatarUtil.localDateTimeToString(linha.getValue().getDataImportacao(), FormatarUtil.FORMATO_DATA_HORA)));
        this.colInscritos.setCellValueFactory(linha -> new ReadOnlyObjectWrapper(linha.getValue().getTotalImportacao()));

        this.colData.getStyleClass().add(ListaConstantesBase.EstiloCss.TAB_COL_CENTRO.getValor());
        this.colInscritos.getStyleClass().add(ListaConstantesBase.EstiloCss.TAB_COL_CENTRO.getValor());

        this.colExcluir.getStyleClass().add(ListaConstantesBase.EstiloCss.TAB_COL_CENTRO.getValor());
        this.colExcluir.setCellValueFactory(new BotaoCelulaTableValueFactory());
        this.colExcluir.setCellFactory(new BotaoTableColumnFactory<ImportacaoInscricao>("excluir importação", ListaConstantesBase.EstiloCss.BOTAO_EXCLUIR.getValor()) {
            @Override
            public void eventoBotao(ActionEvent actionEvent, ImportacaoInscricao importacaoInscricao) {
                remover(importacaoInscricao);
            }
        });
    }

    @Override
    public void reinicializarJanelaEspecifico() {
        this.tabImportacao.getItems().clear();
    }

    @FXML
    public void novo(ActionEvent actionEvent) {
        this.importarInscricaoXlsController.setProva(this.prova);
        super.cj.abrirJanela(Stages.IMPORTAR_INSCRICAO.ordinal(), Stages.CADASTRO_IMPORTAR_INSCRICAO.ordinal(), IImportarInscricaoXlsController.class, "t_importar_inscricao_xls", true);
        this.carregarImportacoes();
    }

    @Override
    public void aoAbrirJanela() {
        this.indicadorProgresso.setVisible(false);

        this.txtNomeProva.setText(this.prova.getNome());
        this.txtData.setText(FormatarUtil.localDateToString(this.prova.getData()));

        this.carregarImportacoes();
    }

    @Override
    public void aoFecharJanelaEspecifico() {
    }

    private void remover(ImportacaoInscricao importacaoInscricao) {
        if (cj.exibirDialogSimNao("titulo_apagar", "aviso_pergunta_excluir", null, "importações")) {
            try {
                this.indicadorProgresso.setVisible(true);
                int totalExcluido = this.secretario.excluirImportacaoInscricaoProva(importacaoInscricao);
                this.tabImportacao.getItems().remove(importacaoInscricao);
                this.indicadorProgresso.setVisible(false);
                super.cj.exibirInformacao(totalExcluido + " inscrições importadas foram excluídas com sucesso.");
            } catch (NegocioException ex) {
                this.indicadorProgresso.setVisible(false);
                super.cj.exibirErro(cm.getMensagem("erro_control_excluir", "importações"), GeralUtil.getMensagemOriginalErro(ex));
            } catch (AvisoNegocioException ex) {
                this.indicadorProgresso.setVisible(false);
                super.cj.exibirAviso(ex.getMessage());
            }
        }
    }

    @Override
    public void setProva(Prova prova) {
        this.prova = prova;
    }

    private void carregarImportacoes() {
        try {
            List<ImportacaoInscricao> importacoes = this.secretario.pesquisarImportacaoInscricaoProva(this.prova);
            this.tabImportacao.getItems().addAll(importacoes);
        } catch (NegocioException ex) {
            super.cj.exibirErro("Não foi possível identificar as importações da prova.", GeralUtil.getMensagemOriginalErro(ex));
        }
    }
}
