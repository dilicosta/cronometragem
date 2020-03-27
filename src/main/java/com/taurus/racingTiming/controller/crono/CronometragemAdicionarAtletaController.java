package com.taurus.racingTiming.controller.crono;

import com.taurus.racingTiming.controller.corrida.inscricao.it.IInscricaoController;
import com.taurus.exception.NegocioException;
import com.taurus.racingTiming.controller.corrida.*;
import com.taurus.javafx.controller.FXMLBaseController;
import com.taurus.javafx.listener.SaidaFocoListener;
import com.taurus.javafx.util.JavafxUtil;
import com.taurus.javafx.util.ValidarCampoUtil;
import com.taurus.racingTiming.entidade.corrida.AtletaInscricao;
import com.taurus.racingTiming.entidade.corrida.Prova;
import com.taurus.racingTiming.negocio.IRolex;
import com.taurus.racingTiming.negocio.ISecretario;
import com.taurus.racingTiming.util.ListaConstantes;
import com.taurus.util.FormatarUtil;
import com.taurus.util.GeralUtil;
import com.taurus.util.ListaConstantesBase;
import com.taurus.util.ListaConstantesBase.Operacao;
import com.taurus.util.annotation.Mascara;
import com.taurus.util.annotation.ValidarCampo;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class CronometragemAdicionarAtletaController extends FXMLBaseController implements ICronometragemAdicionarAtletaController {

    private static final Log LOG = LogFactory.getLog(CronometragemAdicionarAtletaController.class);

    private Prova prova;
    private Integer numeroAtleta;
    private AtletaInscricao atletaInscricao;

    public CronometragemAdicionarAtletaController() {
        super();
    }

    @Autowired
    private IRolex rolex;
    @Autowired
    private ISecretario secretario;
    @Autowired
    private IInscricaoController inscricaoController;

    @FXML
    private TextField txtNomeProva;
    @FXML
    private TextField txtDataProva;

    @FXML
    @ValidarCampo
    @Mascara(tipo = Mascara.TipoMascara.TEXT_FIELD_APENAS_NUMERO, tamanhoMax = "8")
    private TextField txtNumeroAtleta;
    @FXML
    private Button btnNovoAtleta;
    @FXML
    private TextField txtNomeAtleta;
    @FXML
    private TextField txtLargada;
    @FXML
    private TextField txtCategoria;

    @Value("${fxml.cronoAdicionarAtleta.view}")
    @Override
    public void setFxmlFilePath(String filePath) {
        this.fxmlFilePath = filePath;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.btnNovoAtleta.getStyleClass().add(ListaConstantesBase.EstiloCss.BOTAO_NOVO.getValor());
        this.btnNovoAtleta.setTooltip(new Tooltip("inscrever novo atleta"));

        this.txtNumeroAtleta.focusedProperty().addListener(new SaidaFocoListener(this.txtNumeroAtleta) {
            @Override
            public void executar() {
                try {
                    CronometragemAdicionarAtletaController.this.atletaInscricao = CronometragemAdicionarAtletaController.this.secretario.pesquisarAtletaInscricaoPorNumeroAtleta(CronometragemAdicionarAtletaController.this.prova, JavafxUtil.getInteger(CronometragemAdicionarAtletaController.this.txtNumeroAtleta));
                    CronometragemAdicionarAtletaController.this.exibirInscricao();
                } catch (NegocioException ex) {
                    CronometragemAdicionarAtletaController.super.cj.exibirErro(cm.getMensagem("erro_control_pesquisar", "inscricao"), GeralUtil.getMensagemOriginalErro(ex));
                }
            }
        });
    }

    @Override
    public void reinicializarJanelaEspecifico() {
        this.atletaInscricao = null;
    }

    @Override
    public void aoAbrirJanela() {
        this.txtNomeProva.setText(prova.getNome());
        this.txtDataProva.setText(FormatarUtil.localDateToString(prova.getData()));

        this.txtNumeroAtleta.setText(""+this.numeroAtleta);
        this.txtNumeroAtleta.selectAll();
        this.txtNumeroAtleta.requestFocus();
    }

    @Override
    public void aoFecharJanelaEspecifico() {
    }

    private void exibirInscricao() {
        if (this.atletaInscricao != null) {
            this.txtNumeroAtleta.setText("" + this.atletaInscricao.getNumeroAtleta());
            this.txtNomeAtleta.setText(this.atletaInscricao.getAtleta().getNome());
            this.txtLargada.setText(FormatarUtil.localDateTimeToString(this.atletaInscricao.getCategoria().getLargada().getHoraInicio(), FormatarUtil.FORMATO_DATA_HORA_SEG));
            this.txtCategoria.setText(this.atletaInscricao.getCategoria().getCategoriaAtleta().getNome());
        } else {
            this.txtNumeroAtleta.setText("");
            this.txtNomeAtleta.setText("");
            this.txtLargada.setText("");
            this.txtCategoria.setText("");
        }
    }

    @FXML
    public void salvar() {
        try {
            if (!ValidarCampoUtil.validarCampos(this) || this.atletaInscricao == null) {
                super.cj.exibirAviso("Informe um atleta para associar aos registros de cronometragens.");
            } else {
                this.rolex.adicionarAtletaCronometragem(this.atletaInscricao, this.prova, this.numeroAtleta);
                super.cj.exibirInformacao("Cronometragens associadas ao atleta com sucesso!");
                super.aoFecharJanela();
                super.getStage().close();
            }
        } catch (Exception ex) {
            this.cj.exibirErro(cm.getMensagem("erro_control_atualizar", "cronometragem"), GeralUtil.getMensagemOriginalErro(ex));
        }
    }

    @FXML
    public void novoAtleta(ActionEvent actionEvent) {
        this.inscricaoController.setOperacao(Operacao.NOVO);
        this.inscricaoController.setSalvarFechar(Boolean.TRUE);
        this.inscricaoController.setProva(this.prova);

        super.cj.abrirJanela(ListaConstantes.Stages.INSCRICAO.ordinal(), ListaConstantes.Stages.CRONOMETRAGEM_ADD_ATLETA.ordinal(),
                IInscricaoController.class, "Nova Inscrição", true);
        this.atletaInscricao = this.inscricaoController.getAtletaInscricaoNovo();
        this.inscricaoController.setAtletaInscricaoNovo(null);
        if (this.atletaInscricao != null) {
            this.exibirInscricao();
        }
    }

    @Override
    public void setProva(Prova prova) {
        this.prova = prova;
    }

    @Override
    public void setNumeroAtleta(Integer numeroAtleta) {
        this.numeroAtleta = numeroAtleta;
    }

    @Override
    public AtletaInscricao getAtletaInscricao() {
        return this.atletaInscricao;
    }

}
