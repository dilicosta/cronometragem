package com.taurus.racingTiming.controller.crono;

import com.taurus.exception.AvisoNegocioException;
import com.taurus.exception.NegocioException;
import com.taurus.javafx.controller.FXMLBaseController;
import com.taurus.javafx.util.JavafxUtil;
import com.taurus.javafx.util.ValidarCampoUtil;
import com.taurus.racingTiming.entidade.corrida.Prova;
import com.taurus.racingTiming.negocio.ISecretario;
import com.taurus.util.GeralUtil;
import com.taurus.util.annotation.Mascara;
import com.taurus.util.annotation.ValidarCampo;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ProvaDemoController extends FXMLBaseController {

    private static final Log LOG = LogFactory.getLog(ProvaDemoController.class);

    @Autowired
    private ISecretario secretario;

    private Prova prova;

    public ProvaDemoController() {
        super();
    }

    @FXML
    @ValidarCampo
    @Mascara(tipo = Mascara.TipoMascara.TEXT_FIELD_APENAS_NUMERO, tamanhoMax = "1")
    private TextField txtNumPercurso;
    @FXML
    @ValidarCampo
    @Mascara(tipo = Mascara.TipoMascara.TEXT_FIELD_APENAS_NUMERO, tamanhoMax = "1")
    private TextField txtNumVolta;
    @FXML
    @ValidarCampo
    @Mascara(tipo = Mascara.TipoMascara.TEXT_FIELD_APENAS_NUMERO, tamanhoMax = "1")
    private TextField txtNumCatMasculina;
    @FXML
    @ValidarCampo
    @Mascara(tipo = Mascara.TipoMascara.TEXT_FIELD_APENAS_NUMERO, tamanhoMax = "1")
    private TextField txtNumCatFeminino;
    @FXML
    @ValidarCampo
    @Mascara(tipo = Mascara.TipoMascara.TEXT_FIELD_APENAS_NUMERO, tamanhoMax = "1")
    private TextField txtNumCatDupla;

    @Value("${fxml.criarProvaDemo.view}")
    @Override
    public void setFxmlFilePath(String filePath) {
        this.fxmlFilePath = filePath;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    @Override
    public void reinicializarJanelaEspecifico() {
    }

    @Override
    public void aoAbrirJanela() {
        this.prova = null;
    }

    @Override
    public void aoFecharJanelaEspecifico() {
    }

    @FXML
    public void concluir(ActionEvent actionEvent) {
        try {
            if (ValidarCampoUtil.validarCampos(this)) {
                int numPercurso = JavafxUtil.getInteger(this.txtNumPercurso);
                int numVolta = JavafxUtil.getInteger(this.txtNumVolta);
                int numCatMasc = JavafxUtil.getInteger(this.txtNumCatMasculina);
                int numCatFem = JavafxUtil.getInteger(this.txtNumCatFeminino);
                int numCatDupla = JavafxUtil.getInteger(this.txtNumCatDupla);

                try {
                    this.prova = this.secretario.criarProvaDemo(numPercurso, numVolta, numCatMasc, numCatFem, numCatDupla);
                    super.aoFecharJanela();
                    super.getStage().close();
                } catch (AvisoNegocioException ex) {
                    super.cj.exibirAviso(ex.getMessage());
                } catch (NegocioException ex) {
                    super.cj.exibirErro("Não foi possível criar a prova demo.", GeralUtil.getMensagemOriginalErro(ex));
                }
            }
        } catch (IllegalArgumentException | IllegalAccessException ex) {
            super.cj.exibirErro(ex.getMessage());
        }
    }

    public Prova getProva() {
        return this.prova;
    }
}
