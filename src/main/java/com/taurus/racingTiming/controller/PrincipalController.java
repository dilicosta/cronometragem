package com.taurus.racingTiming.controller;

import com.taurus.javafx.controller.FXMLBaseController;
import com.taurus.racingTiming.controller.administracao.ConfiguracaoCameraController;
import com.taurus.racingTiming.controller.atleta.CadastroCategoriaAtletaController;
import com.taurus.racingTiming.controller.atleta.CadastroAtletaController;
import com.taurus.racingTiming.controller.corrida.inscricao.CadastroInscricaoController;
import com.taurus.racingTiming.controller.corrida.CadastroProvaController;
import com.taurus.racingTiming.controller.corrida.inscricao.it.IImportarInscricaoXlsController;
import com.taurus.racingTiming.controller.corrida.SituacaoProvaPesquisaController;
import com.taurus.racingTiming.controller.crono.CadastroCronometroController;
import com.taurus.racingTiming.controller.crono.CadastroProvaCronoPendenciaController;
import com.taurus.racingTiming.controller.crono.ICronometroController;
import com.taurus.racingTiming.controller.responsavel.FederacaoController;
import com.taurus.racingTiming.controller.responsavel.OrganizacaoProvaController;
import com.taurus.racingTiming.controller.resultado.CadastroResultadoController;
import com.taurus.racingTiming.util.ListaConstantes;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
// Aqui configura o application.properties para ser acessado pelo applicationContext na classe principal
@PropertySource("classpath:META-INF/application.properties")
public class PrincipalController extends FXMLBaseController implements IPrincipalController {

    private static final Log LOG = LogFactory.getLog(PrincipalController.class);

    @Autowired
    ICronometroController cronometroController;
    
    @Value("${application.name}")
    private String nome;
    @Value("${application.version}")
    private String versao;

    @FXML
    Label lblSistema;

    public PrincipalController() {
        super();
    }

    @Value("${fxml.principal.view}")
    @Override
    public void setFxmlFilePath(String filePath) {
        this.fxmlFilePath = filePath;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        lblSistema.setText(nome + " " + versao);
    }

    @Override
    public void setPrimaryStage(Stage primaryStage) {
        cj.setPrimaryStage(primaryStage);
    }

    @Override
    public void reinicializarJanelaEspecifico() {
    }

    @Override
    public void aoAbrirJanela() {
    }

    @Override
    public void aoFecharJanelaEspecifico() {
        this.cronometroController.desligarCamera();
    }

    @FXML
    public void abrirFederacao() {
        cj.abrirJanela(ListaConstantes.Stages.FEDERACAO.ordinal(), ListaConstantes.Stages.PRIMARY_STAGE.ordinal(), FederacaoController.class, "t_federacao", true);
    }
    
    @FXML
    public void abrirOrganizacaoProva() {
        cj.abrirJanela(ListaConstantes.Stages.ORGANIZACAO_PROVA.ordinal(), ListaConstantes.Stages.PRIMARY_STAGE.ordinal(), OrganizacaoProvaController.class, "t_organizacao_prova", true);
    }
    
    @FXML
    public void abrirCadastroCategoriaAtleta() {
        cj.abrirJanela(ListaConstantes.Stages.CADASTRO_CATEGORIA_ATLETA.ordinal(), ListaConstantes.Stages.PRIMARY_STAGE.ordinal(), CadastroCategoriaAtletaController.class, "t_cad_categoria_atleta", true);
    }

    @FXML
    public void abrirCadastroAtleta() {
        cj.abrirJanela(ListaConstantes.Stages.CADASTRO_ATLETA.ordinal(), ListaConstantes.Stages.PRIMARY_STAGE.ordinal(), CadastroAtletaController.class, "t_cad_atleta", true);
    }
    
    @FXML
    public void abrirCadastroProva() {
        cj.abrirJanela(ListaConstantes.Stages.CADASTRO_PROVA.ordinal(), ListaConstantes.Stages.PRIMARY_STAGE.ordinal(), CadastroProvaController.class, "t_cad_prova", true);
    }
    
    @FXML
    public void abrirStatusProvaPesquisa() {
        cj.abrirJanela(ListaConstantes.Stages.STATUS_PROVA_PESQUISA.ordinal(), ListaConstantes.Stages.PRIMARY_STAGE.ordinal(), SituacaoProvaPesquisaController.class, "t_cad_prova", true);
    }
    
    @FXML
    public void abrirCadastroInscricao() {
        cj.abrirJanela(ListaConstantes.Stages.CADASTRO_INSCRICAO.ordinal(), ListaConstantes.Stages.PRIMARY_STAGE.ordinal(), CadastroInscricaoController.class, "t_cad_inscricao", true);
    }
    
    @FXML
    public void abrirCronometragemProva() {
        cj.abrirJanela(ListaConstantes.Stages.CADASTRO_CRONOMETRAGEM.ordinal(), ListaConstantes.Stages.PRIMARY_STAGE.ordinal(), CadastroCronometroController.class, "t_cad_cronometragem", true);
    }
    
    @FXML
    public void abrirCadastroProvaCronoPendencia() {
        cj.abrirJanela(ListaConstantes.Stages.CADASTRO_PROV_CRONO_PEND.ordinal(), ListaConstantes.Stages.PRIMARY_STAGE.ordinal(), CadastroProvaCronoPendenciaController.class, "t_cad_prova_crono_pend", true);
    }
    
    @FXML
    public void abrirCadastroResultado() {
        cj.abrirJanela(ListaConstantes.Stages.CADASTRO_RESULTADO.ordinal(), ListaConstantes.Stages.PRIMARY_STAGE.ordinal(), CadastroResultadoController.class, "t_cad_resultado", true);
    }

    @FXML
    public void abrirImportarInscricao() {
        cj.abrirJanela(ListaConstantes.Stages.IMPORTAR_INSCRICAO.ordinal(), ListaConstantes.Stages.PRIMARY_STAGE.ordinal(), IImportarInscricaoXlsController.class, "t_importar_inscricao_xls", true);
    }
    
    @FXML
    public void abrirConfCameraVideo() {
        cj.abrirJanela(ListaConstantes.Stages.CONF_CAMERA.ordinal(), ListaConstantes.Stages.PRIMARY_STAGE.ordinal(), ConfiguracaoCameraController.class, "Configuração da Câmera de vídeo", true);
    }
    
    @FXML
    public void abrirTesteWebcam() {
        cj.abrirJanela(ListaConstantes.Stages.TESTE_CAMERA.ordinal(), ListaConstantes.Stages.PRIMARY_STAGE.ordinal(), TesteCameraController.class, "Teste de câmera", true);
    }
    

}
