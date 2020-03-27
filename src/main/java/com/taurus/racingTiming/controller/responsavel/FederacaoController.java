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
import com.taurus.racingTiming.entidade.responsavel.Federacao;
import com.taurus.racingTiming.negocio.IEstagiario;
import com.taurus.racingTiming.ui.ds.responsavel.FederacaoDataSource;
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
public class FederacaoController extends FXMLBaseController {
    
    private FederacaoDataSource dataSource;
    private static final Log LOG = LogFactory.getLog(FederacaoController.class);
    private boolean atualizacaoListaNecessaria;
    
    public FederacaoController() {
        super();
    }
    
    @Autowired
    private IEstagiario estagiario;
    
    @Autowired
    private ListaSistema listaSistema;
    
    @Autowired
    private RepresentanteFederacaoController repFedController;
    
    @FXML
    private TableView<GenericLinhaView<Federacao>> tabFederacao;
    @FXML
    private TableColumn<GenericLinhaView, String> colNome;
    @FXML
    private TableColumn<GenericLinhaView, String> colSigla;
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
    
    @Value("${fxml.federacao.view}")
    @Override
    public void setFxmlFilePath(String filePath) {
        this.fxmlFilePath = filePath;
    }
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        colNome.setCellValueFactory(new PropertyValueFactory("coluna1"));
        colSigla.setCellValueFactory(new PropertyValueFactory("coluna2"));
        
        colNome.getStyleClass().add(ListaConstantesBase.EstiloCss.TAB_COL_ESQUERDO.getValor());
        colSigla.getStyleClass().add(ListaConstantesBase.EstiloCss.TAB_COL_CENTRO.getValor());
        
        this.configurarEdicaoNome();
        this.configurarEdicaoSigla();
        
        colAddRep.getStyleClass().add(ListaConstantesBase.EstiloCss.TAB_COL_CENTRO.getValor());
        colAddRep.setCellValueFactory(new BotaoCelulaTableValueFactory());
        colAddRep.setCellFactory(new BotaoTableColumnFactory<GenericLinhaView<Federacao>>("adicionar representante", ListaConstantesBase.EstiloCss.BOTAO_ADICIONAR.getValor()) {
            @Override
            public void eventoBotao(ActionEvent actionEvent, GenericLinhaView<Federacao> linhaView) {
                abrirRepresentanteFederacao(linhaView);
            }
        });
        colExcluir.getStyleClass().add(ListaConstantesBase.EstiloCss.TAB_COL_CENTRO.getValor());
        colExcluir.setCellValueFactory(new BotaoCelulaTableValueFactory());
        colExcluir.setCellFactory(new BotaoTableColumnFactory<GenericLinhaView<Federacao>>("excluir federação", ListaConstantesBase.EstiloCss.BOTAO_EXCLUIR.getValor()) {
            @Override
            public void eventoBotao(ActionEvent actionEvent,  GenericLinhaView<Federacao> linhaView) {
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
        this.dataSource = new FederacaoDataSource(this.listaSistema.getListaFederacao());
        this.tabFederacao.setItems(this.dataSource.getData());
    }
    
    @Override
    public void aoFecharJanelaEspecifico() {
        if (atualizacaoListaNecessaria) {
            try {
                listaSistema.atualizarListaFederacao();
            } catch (NegocioException ex) {
                cj.exibirErro(cm.getMensagem("erro_atualizacao_lista", "federacao"), GeralUtil.getMensagemOriginalErro(ex));
            }
        }
    }
    
    private void remover(GenericLinhaView linhaView) {
        if (cj.exibirDialogSimNao("titulo_apagar", "aviso_pergunta_excluir", null, "federacao")) {
            Federacao federacao = this.dataSource.getBean(linhaView);
            try {
                estagiario.excluirFederacao(federacao);
                this.dataSource.remover(linhaView);
                this.tabFederacao.refresh();
                this.atualizacaoListaNecessaria = true;
            } catch (NegocioException ex) {
                super.cj.exibirErro(cm.getMensagem("erro_control_excluir", "federacao"), GeralUtil.getMensagemOriginalErro(ex));
            } catch (AvisoNegocioException ex) {
                super.cj.exibirAviso(ex.getMessage());
            }
        }
    }
    
    public void adicionar() {
        try {
            if (ValidarCampoUtil.validarCampos(this)) {
                Federacao federacao = new Federacao();
                federacao.setNome(txtNome.getText());
                federacao.setSigla(txtSigla.getText());
                federacao = estagiario.criarNovoFederacao(federacao);
                
                if (this.dataSource == null) {
                    this.dataSource = new FederacaoDataSource(federacao);
                    this.tabFederacao.setItems(this.dataSource.getData());
                } else {
                    this.dataSource.adicionar(federacao);
                }
                this.atualizacaoListaNecessaria = true;
                txtNome.setText("");
            }
        } catch (IllegalArgumentException | IllegalAccessException ex) {
            this.cj.exibirErro("erro_validacao_campos", GeralUtil.getMensagemOriginalErro(ex));
            LOG.error(cm.getMensagem("erro_validacao_campos"), ex);
        } catch (NegocioException ex) {
            this.cj.exibirErro(cm.getMensagem("erro_control_salvar", "federacao"), GeralUtil.getMensagemOriginalErro(ex));
        }
    }
    
    private void atualizarNome(GenericLinhaView linhaView, String novoValor) {
        Federacao fed = this.dataSource.getBean(linhaView);
        fed.setNome(novoValor.length() > 50 ? novoValor.substring(0, 50) : novoValor);
        try {
            fed = this.estagiario.atualizarFederacao(fed);
            this.dataSource.atualizar(linhaView, fed);
            this.atualizacaoListaNecessaria = true;
        } catch (NegocioException ex) {
            this.cj.exibirErro(cm.getMensagem("erro_control_atualizar", "federacao"), GeralUtil.getMensagemOriginalErro(ex));
        }
    }
    
    private void atualizarSigla(GenericLinhaView linhaView, String novoValor) {
        Federacao fed = this.dataSource.getBean(linhaView);
        fed.setSigla(novoValor.length() > 5 ? novoValor.substring(0, 5) : novoValor);
        try {
            fed = this.estagiario.atualizarFederacao(fed);
            this.dataSource.atualizar(linhaView, fed);
            this.atualizacaoListaNecessaria = true;
        } catch (NegocioException ex) {
            this.cj.exibirErro(cm.getMensagem("erro_control_atualizar", "federacao"), GeralUtil.getMensagemOriginalErro(ex));
        }
    }
    
    private void configurarEdicaoNome() {
        // para editar o campo nome
        this.colNome.setCellFactory(col -> new TableCellEditFactoryGeneric<GenericLinhaView, String>(false, TableCellEditFactoryGeneric.Tipo.STRING){
            @Override
            public boolean isValido(String item) {
                return !GenericValidator.isBlankOrNull(item);
            }
        });
        
        this.colNome.setOnEditCommit(new TableCellCommitGeneric<GenericLinhaView, String>(false) {
            @Override
            public void atualizar(GenericLinhaView rowValue, String novoValor, String valorAntigo, boolean valido) {
                if (novoValor != null) {
                    FederacaoController.this.atualizarNome(rowValue, novoValor);
                } else {
                    cj.exibirAviso(cm.getMensagem("validar_campo_vazio", "nome"));
                }
            }
        });
    }
    
    private void configurarEdicaoSigla() {
        // para editar o campo sigla
        this.colSigla.setCellFactory(col -> new TableCellEditFactoryGeneric<GenericLinhaView, String>(true,TableCellEditFactoryGeneric.Tipo.STRING) {
            @Override
            public boolean isValido(String item) {
                return true;
            }
        });
        
        this.colSigla.setOnEditCommit(new TableCellCommitGeneric<GenericLinhaView, String>(true) {
            @Override
            public void atualizar(GenericLinhaView rowValue, String novoValor, String valorAntigo, boolean valido) {
                FederacaoController.this.atualizarSigla(rowValue, novoValor);
            }
        });
    }
    
    private void abrirRepresentanteFederacao(GenericLinhaView linhaView) {
        Federacao federacao = this.dataSource.getBean(linhaView);
        this.repFedController.setFederacao(federacao);
        super.cj.abrirJanela(ListaConstantes.Stages.REPRESENTANTE_FEDERACAO.ordinal(), ListaConstantes.Stages.FEDERACAO.ordinal(), RepresentanteFederacaoController.class, "t_representante_federacao", true);
    }
}
