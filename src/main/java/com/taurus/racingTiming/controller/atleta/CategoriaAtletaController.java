package com.taurus.racingTiming.controller.atleta;

import com.taurus.exception.NegocioException;
import com.taurus.javafx.controller.FXMLBaseController;
import com.taurus.javafx.util.ValidarCampoUtil;
import com.taurus.racingTiming.entidade.atleta.CategoriaAtleta;
import com.taurus.racingTiming.negocio.IEstagiario;
import com.taurus.racingTiming.util.ListaSistema;
import com.taurus.util.GeralUtil;
import com.taurus.util.ListaConstantesBase.Operacao;
import com.taurus.util.ListaConstantesBase.Sexo;
import com.taurus.util.annotation.Mascara;
import com.taurus.util.annotation.Parametro;
import com.taurus.util.annotation.ValidarCampo;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.validator.GenericValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class CategoriaAtletaController extends FXMLBaseController implements ICategoriaAtletaController {

    private static final Log LOG = LogFactory.getLog(CategoriaAtletaController.class);
    private boolean atualizacaoListaNecessaria;

    @Parametro
    private CategoriaAtleta categoriaAtletaEdicao;
    @Parametro
    private Operacao operacao;

    public CategoriaAtletaController() {
        super();
    }

    @Autowired
    private IEstagiario estagiario;

    @Autowired
    private ListaSistema listaSistema;

    @FXML
    @Mascara(tipo = Mascara.TipoMascara.TEXT_FIELD_MAXIMO_CARACTER, tamanhoMax = "100")
    @ValidarCampo
    private TextField txtNome;

    @FXML
    @Mascara(tipo = Mascara.TipoMascara.TEXT_FIELD_MAXIMO_CARACTER, tamanhoMax = "100")
    private TextField txtDescricao;
    
    @FXML
    private CheckBox checkDupla;

    @FXML
    @ValidarCampo
    private RadioButton radioMasculino;
    @FXML
    @ValidarCampo
    private RadioButton radioFeminino;
    @FXML
    @ValidarCampo
    private RadioButton radioSexoNA;

    @FXML
    @Mascara(tipo = Mascara.TipoMascara.TEXT_FIELD_APENAS_NUMERO, tamanhoMax = "2")
    private TextField txtIdadeMin;

    @FXML
    @Mascara(tipo = Mascara.TipoMascara.TEXT_FIELD_APENAS_NUMERO, tamanhoMax = "2")
    private TextField txtIdadeMax;

    @Value("${fxml.categoriaAtleta.view}")
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
        this.atualizacaoListaNecessaria = false;

        if (operacao == Operacao.EDITAR) {
            this.exibirCategoriaAtleta();
        }
    }

    @Override
    public void aoFecharJanelaEspecifico() {
        if (atualizacaoListaNecessaria) {
            try {
                listaSistema.atualizarListaCategoriaAtleta();
            } catch (NegocioException ex) {
                cj.exibirErro(cm.getMensagem("erro_atualizacao_lista", "categoria_atleta"), GeralUtil.getMensagemOriginalErro(ex));
            }
        }
    }

    @FXML
    public void salvar() {
        try {
            if (ValidarCampoUtil.validarCampos(this)) {
                CategoriaAtleta categoriaAtleta;
                if (operacao == Operacao.EDITAR) {
                    categoriaAtleta = this.categoriaAtletaEdicao;
                } else {
                    categoriaAtleta = new CategoriaAtleta();
                }
                categoriaAtleta.setNome(txtNome.getText());
                categoriaAtleta.setDescricao(txtDescricao.getText());
                categoriaAtleta.setCategoriaDupla(checkDupla.isSelected());
                categoriaAtleta.setSexo(radioMasculino.isSelected() ? Sexo.MASCULINO : radioFeminino.isSelected() ? Sexo.FEMININO : Sexo.NAO_SE_APLICA);
                categoriaAtleta.setIdadeMinima(GenericValidator.isInt(txtIdadeMin.getText()) ? Integer.valueOf(txtIdadeMin.getText()) : null);
                categoriaAtleta.setIdadeMaxima(GenericValidator.isInt(txtIdadeMax.getText()) ? Integer.valueOf(txtIdadeMax.getText()) : null);

                if (operacao == Operacao.EDITAR) {
                    this.categoriaAtletaEdicao = this.estagiario.atualizarCategoriaAtleta(categoriaAtleta);
                    this.cj.exibirInformacao(cm.getMensagem("sucesso_control_atualizar", "categoria_atleta"));
                } else {
                    this.estagiario.criarNovoCategoriaAtleta(categoriaAtleta);
                    this.cj.exibirInformacao(cm.getMensagem("sucesso_control_salvar", "categoria_atleta"));
                    super.reinicializarJanelaBase();
                }
                this.atualizacaoListaNecessaria = true;
            }
        } catch (IllegalArgumentException | IllegalAccessException ex) {
            this.cj.exibirErro(ex.getMessage());
        } catch (Exception ex) {
            this.cj.exibirErro(cm.getMensagem("erro_control_salvar", "categoria_atleta"), GeralUtil.getMensagemOriginalErro(ex));
        }
    }

    private void exibirCategoriaAtleta() {
        this.txtNome.setText(categoriaAtletaEdicao.getNome());
        this.txtDescricao.setText(categoriaAtletaEdicao.getDescricao());
        this.checkDupla.setSelected(categoriaAtletaEdicao.isCategoriaDupla());
        switch (categoriaAtletaEdicao.getSexo()) {
            case MASCULINO:
                this.radioMasculino.setSelected(true);
                break;
            case FEMININO:
                this.radioFeminino.setSelected(true);
                break;
            default:
                this.radioSexoNA.setSelected(true);
        }

        this.txtIdadeMin.setText(String.valueOf(categoriaAtletaEdicao.getIdadeMinima()));
        this.txtIdadeMax.setText(String.valueOf(categoriaAtletaEdicao.getIdadeMaxima()));
    }

    @Override
    public void setOperacao(Operacao op) {
        this.operacao = op;
    }

    @Override
    public void setCategoriaAtletaEdicao(CategoriaAtleta categoriaAtletaEdicao) {
        this.categoriaAtletaEdicao = categoriaAtletaEdicao;
    }

}
