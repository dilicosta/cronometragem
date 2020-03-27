package com.taurus.racingTiming.controller.responsavel;

import com.taurus.exception.AvisoNegocioException;
import com.taurus.exception.NegocioException;
import com.taurus.javafx.componente.BotaoCelulaTableValueFactory;
import com.taurus.javafx.componente.BotaoTableColumnFactory;
import com.taurus.javafx.controller.FXMLBaseController;
import com.taurus.javafx.ds.GenericLinhaView;
import com.taurus.javafx.tableView.TableCellCommitGeneric;
import com.taurus.javafx.tableView.TableCellEditFactoryGeneric;
import com.taurus.javafx.util.ValidarCampoUtil;
import com.taurus.racingTiming.entidade.responsavel.OrganizacaoProva;
import com.taurus.racingTiming.negocio.IEstagiario;
import com.taurus.racingTiming.ui.ds.responsavel.OrganizacaoProvaDataSource;
import com.taurus.racingTiming.util.ListaConstantes;
import com.taurus.racingTiming.util.ListaSistema;
import com.taurus.util.GeralUtil;
import com.taurus.util.ListaConstantesBase;
import com.taurus.util.annotation.Mascara;
import com.taurus.util.annotation.ValidarCampo;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.validator.GenericValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class OrganizacaoProvaController extends FXMLBaseController {

    private OrganizacaoProvaDataSource dataSource;
    private static final Log LOG = LogFactory.getLog(OrganizacaoProvaController.class);
    private boolean atualizacaoListaNecessaria;

    public OrganizacaoProvaController() {
        super();
    }

    @Autowired
    private IEstagiario estagiario;

    @Autowired
    private ListaSistema listaSistema;

    @Autowired
    private RepresentanteOrganizacaoProvaController repOrgProvController;

    @FXML
    private TableView<GenericLinhaView<OrganizacaoProva>> tabOrganizacaoProva;
    @FXML
    private TableColumn<GenericLinhaView, String> colNome;
    @FXML
    private TableColumn colAddRep;
    @FXML
    private TableColumn colExcluir;

    @Mascara(tipo = Mascara.TipoMascara.TEXT_FIELD_MAXIMO_CARACTER, tamanhoMax = "50")
    @ValidarCampo
    @FXML
    private TextField txtNome;
    @Mascara(tipo = Mascara.TipoMascara.TEXT_FIELD_MAXIMO_CARACTER, tamanhoMax = "5")
    @FXML
    private TextField txtSigla;

    @Value("${fxml.organizacaoProva.view}")
    @Override
    public void setFxmlFilePath(String filePath) {
        this.fxmlFilePath = filePath;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        colNome.setCellValueFactory(new PropertyValueFactory("coluna1"));
        colNome.getStyleClass().add(ListaConstantesBase.EstiloCss.TAB_COL_ESQUERDO.getValor());

        this.configurarEdicaoNome();

        colAddRep.getStyleClass().add(ListaConstantesBase.EstiloCss.TAB_COL_CENTRO.getValor());
        colAddRep.setCellValueFactory(new BotaoCelulaTableValueFactory());
        colAddRep.setCellFactory(new BotaoTableColumnFactory<GenericLinhaView<OrganizacaoProva>>("adicionar representante", ListaConstantesBase.EstiloCss.BOTAO_ADICIONAR.getValor()) {
            @Override
            public void eventoBotao(ActionEvent actionEvent, GenericLinhaView<OrganizacaoProva> linhaView) {
                abrirRepresentanteOrganizacaoProva(linhaView);
            }
        });
        colExcluir.getStyleClass().add(ListaConstantesBase.EstiloCss.TAB_COL_CENTRO.getValor());
        colExcluir.setCellValueFactory(new BotaoCelulaTableValueFactory());
        colExcluir.setCellFactory(new BotaoTableColumnFactory<GenericLinhaView<OrganizacaoProva>>("excluir organização da prova", ListaConstantesBase.EstiloCss.BOTAO_EXCLUIR.getValor()) {
            @Override
            public void eventoBotao(ActionEvent actionEvent, GenericLinhaView<OrganizacaoProva> linhaView) {
                remover(linhaView);
            }
        });

    }

    @Override
    public void reinicializarJanelaEspecifico() {
        this.dataSource = null;
    }

    @Override
    public void aoAbrirJanela() {
        this.atualizacaoListaNecessaria = false;
        this.dataSource = new OrganizacaoProvaDataSource(this.listaSistema.getListaOrganizacaoProva());
        this.tabOrganizacaoProva.setItems(this.dataSource.getData());
    }

    @Override
    public void aoFecharJanelaEspecifico() {
        if (atualizacaoListaNecessaria) {
            try {
                listaSistema.atualizarListaOrganizacaoProva();
            } catch (NegocioException ex) {
                cj.exibirErro(cm.getMensagem("erro_atualizacao_lista", "organizacao_prova"), GeralUtil.getMensagemOriginalErro(ex));
            }
        }
    }

    private void remover(GenericLinhaView linhaView) {
        if (cj.exibirDialogSimNao("titulo_apagar", "aviso_pergunta_excluir", null, "organizacao_prova")) {
            OrganizacaoProva organizacaoProva = this.dataSource.getBean(linhaView);
            try {
                estagiario.excluirOrganizacaoProva(organizacaoProva);
                this.dataSource.remover(linhaView);
                this.tabOrganizacaoProva.refresh();
                this.atualizacaoListaNecessaria = true;
            } catch (NegocioException ex) {
                super.cj.exibirErro(cm.getMensagem("erro_control_excluir", "organizacao_prova"), GeralUtil.getMensagemOriginalErro(ex));
            } catch (AvisoNegocioException ex) {
                super.cj.exibirAviso(ex.getMessage());
            }
        }
    }

    public void adicionar() {
        try {
            if (ValidarCampoUtil.validarCampos(this)) {
                OrganizacaoProva organizacaoProva = new OrganizacaoProva();
                organizacaoProva.setNome(txtNome.getText());
                organizacaoProva = estagiario.criarNovoOrganizacaoProva(organizacaoProva);
                this.atualizacaoListaNecessaria = true;

                if (this.dataSource == null) {
                    this.dataSource = new OrganizacaoProvaDataSource(organizacaoProva);
                    this.tabOrganizacaoProva.setItems(this.dataSource.getData());
                } else {
                    this.dataSource.adicionar(organizacaoProva);
                }

                txtNome.setText("");
            }
        } catch (IllegalArgumentException | IllegalAccessException ex) {
            this.cj.exibirErro("erro_validacao_campos", GeralUtil.getMensagemOriginalErro(ex));
            LOG.error(cm.getMensagem("erro_validacao_campos"), ex);
        } catch (NegocioException ex) {
            this.cj.exibirErro(cm.getMensagem("erro_control_salvar", "organizacao_prova"), GeralUtil.getMensagemOriginalErro(ex));
        }
    }

    private void atualizarNome(GenericLinhaView linhaView, String novoValor) {
        OrganizacaoProva organizacaoProva = this.dataSource.getBean(linhaView);
        organizacaoProva.setNome(novoValor.length() > 50 ? novoValor.substring(0, 50) : novoValor);
        try {
            organizacaoProva = this.estagiario.atualizarOrganizacaoProva(organizacaoProva);
            this.dataSource.atualizar(linhaView, organizacaoProva);
            this.atualizacaoListaNecessaria = true;
        } catch (NegocioException ex) {
            this.cj.exibirErro(cm.getMensagem("erro_control_atualizar", "organizacao_prova"), GeralUtil.getMensagemOriginalErro(ex));
        }
    }

    private void configurarEdicaoNome() {
        // para editar o campo nome
        this.colNome.setCellFactory(col -> new TableCellEditFactoryGeneric<GenericLinhaView, String>(false, TableCellEditFactoryGeneric.Tipo.STRING) {
            @Override
            public boolean isValido(String item) {
                return !GenericValidator.isBlankOrNull(item);
            }
        });

        this.colNome.setOnEditCommit(new TableCellCommitGeneric<GenericLinhaView, String>(false) {
            @Override
            public void atualizar(GenericLinhaView rowValue, String novoValor, String valorAntigo, boolean valido) {
                if (novoValor != null) {
                    OrganizacaoProvaController.this.atualizarNome(rowValue, novoValor);
                } else {
                    cj.exibirAviso(cm.getMensagem("validar_campo_vazio", "nome"));
                }
            }
        });
    }

    private void abrirRepresentanteOrganizacaoProva(GenericLinhaView linhaView) {
        OrganizacaoProva organizacaoProva = this.dataSource.getBean(linhaView);
        this.repOrgProvController.setOrganizacaoProva(organizacaoProva);
        super.cj.abrirJanela(ListaConstantes.Stages.REPRESENTANTE_ORGANIZACAO_PROVA.ordinal(), ListaConstantes.Stages.ORGANIZACAO_PROVA.ordinal(), RepresentanteOrganizacaoProvaController.class, "t_representante_organizacao_prova", true);
    }
}
