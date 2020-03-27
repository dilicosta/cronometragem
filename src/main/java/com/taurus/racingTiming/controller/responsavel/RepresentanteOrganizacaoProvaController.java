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
import com.taurus.racingTiming.entidade.responsavel.RepresentanteOrganizacaoProva;
import com.taurus.racingTiming.negocio.IEstagiario;
import com.taurus.racingTiming.ui.ds.responsavel.RepresentanteOrganizacaoProvaDataSource;
import com.taurus.util.GeralUtil;
import com.taurus.util.ListaConstantesBase;
import com.taurus.util.annotation.Mascara;
import com.taurus.util.annotation.Parametro;
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
public class RepresentanteOrganizacaoProvaController extends FXMLBaseController {
    
    private RepresentanteOrganizacaoProvaDataSource dataSource;
    private static final Log LOG = LogFactory.getLog(RepresentanteOrganizacaoProvaController.class);
    
    public RepresentanteOrganizacaoProvaController() {
        super();
    }
    
    @Autowired
    private IEstagiario estagiario;
    
    @Parametro
    private OrganizacaoProva organizacaoProva;
    
    @FXML
    private TableView<GenericLinhaView<RepresentanteOrganizacaoProva>> tabRepresentante;
    @FXML
    private TableColumn<GenericLinhaView, String> colNome;
    @FXML
    private TableColumn colExcluir;
    @FXML
    private TableColumn colOrganizacaoProva;
    
    @Mascara(tipo = Mascara.TipoMascara.TEXT_FIELD_MAXIMO_CARACTER, tamanhoMax = "50")
    @ValidarCampo
    @FXML
    private TextField txtNome;
    
    @Value("${fxml.representanteOrganizacaoProva.view}")
    @Override
    public void setFxmlFilePath(String filePath) {
        this.fxmlFilePath = filePath;
    }
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        colNome.setCellValueFactory(new PropertyValueFactory("coluna1"));
        
        colNome.getStyleClass().add(ListaConstantesBase.EstiloCss.TAB_COL_ESQUERDO.getValor());
        
        this.configurarEdicaoNome();
        
        colExcluir.getStyleClass().add(ListaConstantesBase.EstiloCss.TAB_COL_CENTRO.getValor());
        colExcluir.setCellValueFactory(new BotaoCelulaTableValueFactory());
        colExcluir.setCellFactory(new BotaoTableColumnFactory<GenericLinhaView>("excluir representante da organização da prova", ListaConstantesBase.EstiloCss.BOTAO_EXCLUIR.getValor()) {
            @Override
            public void eventoBotao(ActionEvent actionEvent, GenericLinhaView linhaView) {
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
        this.colOrganizacaoProva.setText("Representantes do(a) \"" + this.organizacaoProva.getNome() + "\"");
        try {
            this.dataSource = new RepresentanteOrganizacaoProvaDataSource(estagiario.pesquisarRepresentanteOrganizacaoProvaPorOrganizacaoProva(this.organizacaoProva));
            this.tabRepresentante.setItems(this.dataSource.getData());
        } catch (NegocioException ex) {
            cj.exibirErro(cm.getMensagem("erro_pesquisa", "representante_organizacao_prova"), GeralUtil.getMensagemOriginalErro(ex));
        }
    }
    
    @Override
    public void aoFecharJanelaEspecifico() {
    }
    
    private void remover(GenericLinhaView linhaView) {
        if (cj.exibirDialogSimNao("titulo_apagar", "aviso_pergunta_excluir", null, "representante_organizacao_prova")) {
            RepresentanteOrganizacaoProva rf = this.dataSource.getBean(linhaView);
            try {
                estagiario.excluirRepresentanteOrganizacaoProva(rf);
                this.dataSource.remover(linhaView);
                this.tabRepresentante.refresh();
            } catch (NegocioException ex) {
                super.cj.exibirErro(cm.getMensagem("erro_control_excluir", "representante_organizacao_prova"), GeralUtil.getMensagemOriginalErro(ex));
            } catch (AvisoNegocioException ex) {
                super.cj.exibirAviso(ex.getMessage());
            }
        }
    }
    
    public void adicionar() {
        try {
            if (ValidarCampoUtil.validarCampos(this)) {
                RepresentanteOrganizacaoProva rf = new RepresentanteOrganizacaoProva();
                rf.setNome(txtNome.getText());
                rf.setOrganizacaoProva(organizacaoProva);
                rf = estagiario.criarNovoRepresentanteOrganizacaoProva(rf);
                
                if (this.dataSource == null) {
                    this.dataSource = new RepresentanteOrganizacaoProvaDataSource(rf);
                    this.tabRepresentante.setItems(this.dataSource.getData());
                } else {
                    this.dataSource.adicionar(rf);
                }
                
                txtNome.setText("");
            }
        } catch (IllegalArgumentException | IllegalAccessException ex) {
            this.cj.exibirErro("erro_validacao_campos", GeralUtil.getMensagemOriginalErro(ex));
            LOG.error(cm.getMensagem("erro_validacao_campos"), ex);
        } catch (NegocioException ex) {
            this.cj.exibirErro(cm.getMensagem("erro_control_salvar", "representante_organizacao_prova"), GeralUtil.getMensagemOriginalErro(ex));
        }
    }
    
    private void atualizarNome(GenericLinhaView linhaView, String novoValor) {
        RepresentanteOrganizacaoProva rf = this.dataSource.getBean(linhaView);
        rf.setNome(novoValor.length() > 50 ? novoValor.substring(0, 50) : novoValor);
        try {
            rf = this.estagiario.atualizarRepresentanteOrganizacaoProva(rf);
            this.dataSource.atualizar(linhaView, rf);
        } catch (NegocioException ex) {
            this.cj.exibirErro(cm.getMensagem("erro_control_atualizar", "representante_organizacao_prova"), GeralUtil.getMensagemOriginalErro(ex));
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
                    RepresentanteOrganizacaoProvaController.this.atualizarNome(rowValue, novoValor);
                } else {
                    cj.exibirAviso(cm.getMensagem("validar_campo_vazio", "nome"));
                }
            }
        });
    }
    
    public void setOrganizacaoProva(OrganizacaoProva organizacaoProva) {
        this.organizacaoProva = organizacaoProva;
    }
}
