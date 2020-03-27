package com.taurus.racingTiming.controller.atleta;

import com.taurus.exception.AvisoNegocioException;
import com.taurus.javafx.controller.FXMLBaseController;
import com.taurus.javafx.converter.EstadoBrasilConverter;
import com.taurus.javafx.util.ValidarCampoUtil;
import com.taurus.racingTiming.entidade.Endereco;
import com.taurus.racingTiming.entidade.atleta.Atleta;
import com.taurus.racingTiming.negocio.ISecretario;
import com.taurus.racingTiming.ui.converter.TipoSanguineoConverter;
import com.taurus.racingTiming.util.ListaConstantes.TipoSanguineo;
import com.taurus.util.FormatarUtil;
import com.taurus.util.GeralUtil;
import com.taurus.util.ListaConstantesBase.EstadoBrasil;
import com.taurus.util.ListaConstantesBase.Operacao;
import com.taurus.util.ListaConstantesBase.Sexo;
import com.taurus.util.ValidadorDocumentos;
import com.taurus.util.annotation.Mascara;
import com.taurus.util.annotation.Parametro;
import com.taurus.util.annotation.ValidarCampo;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.validator.GenericValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AtletaController extends FXMLBaseController implements IAtletaController {

    private static final Log LOG = LogFactory.getLog(AtletaController.class);

    @Parametro
    private Atleta atletaEdicao;
    @Parametro
    private Operacao operacao;
    @Parametro
    private Boolean salvarFechar;

    private Atleta novoAtleta;

    public AtletaController() {
        super();
    }

    @Autowired
    private ISecretario secretario;

    @FXML
    @Mascara(tipo = Mascara.TipoMascara.TEXT_FIELD_MAXIMO_CARACTER, tamanhoMax = "50")
    @ValidarCampo
    private TextField txtNome;

    @FXML
    @Mascara(tipo = Mascara.TipoMascara.TEXT_FIELD_DATA)
    @ValidarCampo
    private TextField txtNascimento;

    @FXML
    @Mascara(tipo = Mascara.TipoMascara.TEXT_FIELD_CPF)
    private TextField txtCpf;

    @FXML
    @Mascara(tipo = Mascara.TipoMascara.TEXT_FIELD_MAXIMO_CARACTER, tamanhoMax = "20")
    private TextField txtRg;

    @FXML
    @ValidarCampo
    private RadioButton radioMasculino;

    @FXML
    @ValidarCampo
    private RadioButton radioFeminino;

    @FXML
    private ComboBox<TipoSanguineo> comboTipoSanguineo;

    @FXML
    @Mascara(tipo = Mascara.TipoMascara.TEXT_FIELD_MAXIMO_CARACTER, tamanhoMax = "30")
    private TextField txtProfissao;

    @FXML
    @Mascara(tipo = Mascara.TipoMascara.TEXT_FIELD_MAXIMO_CARACTER, tamanhoMax = "30")
    private TextField txtEquipamento;

    @FXML
    @Mascara(tipo = Mascara.TipoMascara.TEXT_FIELD_MAXIMO_CARACTER, tamanhoMax = "11")
    private TextField txtFone1;

    @FXML
    @Mascara(tipo = Mascara.TipoMascara.TEXT_FIELD_MAXIMO_CARACTER, tamanhoMax = "11")
    private TextField txtFone2;

    @FXML
    @Mascara(tipo = Mascara.TipoMascara.TEXT_FIELD_MAXIMO_CARACTER, tamanhoMax = "30")
    private TextField txtEmail;

    @FXML
    @Mascara(tipo = Mascara.TipoMascara.TEXT_FIELD_MAXIMO_CARACTER, tamanhoMax = "50")
    private TextField txtLogradouro;

    @FXML
    @Mascara(tipo = Mascara.TipoMascara.TEXT_FIELD_APENAS_NUMERO, tamanhoMax = "6")
    private TextField txtNumero;

    @FXML
    @Mascara(tipo = Mascara.TipoMascara.TEXT_FIELD_MAXIMO_CARACTER, tamanhoMax = "20")
    private TextField txtComplemento;

    @FXML
    @Mascara(tipo = Mascara.TipoMascara.TEXT_FIELD_MAXIMO_CARACTER, tamanhoMax = "40")
    private TextField txtBairro;

    @FXML
    @Mascara(tipo = Mascara.TipoMascara.TEXT_FIELD_MAXIMO_CARACTER, tamanhoMax = "40")
    @ValidarCampo
    private TextField txtCidade;

    @FXML
    @ValidarCampo
    private ComboBox<EstadoBrasil> comboUf;

    @FXML
    @Mascara(tipo = Mascara.TipoMascara.TEXT_FIELD_CEP)
    private TextField txtCep;

    @Value("${fxml.atleta.view}")
    @Override
    public void setFxmlFilePath(String filePath) {
        this.fxmlFilePath = filePath;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.comboTipoSanguineo.setConverter(new TipoSanguineoConverter());
        this.comboUf.setConverter(new EstadoBrasilConverter());

        this.comboTipoSanguineo.getItems().addAll(TipoSanguineo.values());
        this.comboUf.getItems().addAll(EstadoBrasil.values());
    }

    @Override
    public void reinicializarJanelaEspecifico() {
        this.comboUf.setValue(EstadoBrasil.MINAS_GERAIS);
        this.txtNome.requestFocus();
    }

    @Override
    public void aoAbrirJanela() {
        if (operacao == Operacao.EDITAR) {
            this.exibirAtleta();
        } else {
            this.comboUf.setValue(EstadoBrasil.MINAS_GERAIS);
        }
        this.txtNome.requestFocus();
    }

    @Override
    public void aoFecharJanelaEspecifico() {
    }

    @FXML
    public void salvar() {
        try {
            if (ValidarCampoUtil.validarCampos(this)) {
                Atleta atleta;
                if (operacao == Operacao.EDITAR) {
                    atleta = this.atletaEdicao;
                } else {
                    atleta = new Atleta();
                    atleta.setEndereco(new Endereco());
                }
                atleta.setNome(FormatarUtil.iniciaisMaiusculas(txtNome.getText()));
                atleta.setDataNascimento(FormatarUtil.stringToLocalDate(txtNascimento.getText()));
                if (atleta.getDataNascimento() == null || atleta.getDataNascimento().isAfter(LocalDate.now())) {
                    super.cj.exibirAviso("aviso_data_nasc_invalida");
                    return;
                }
                String cpf = FormatarUtil.removerMascaraNumero(txtCpf.getText());
                if (!"".equals(cpf) && !ValidadorDocumentos.validarCPF(cpf)) {
                    super.cj.exibirAviso("aviso_cpf_invalido");
                    return;
                }

                atleta.setCpf(GenericValidator.isBlankOrNull(cpf) || cpf.trim().equals("") ? null : cpf);
                atleta.setRg(txtRg.getText().trim());
                atleta.setSexo(radioMasculino.isSelected() ? Sexo.MASCULINO : Sexo.FEMININO);
                atleta.setTipoSanquineo(comboTipoSanguineo.getValue());
                atleta.setProfissao(txtProfissao.getText());
                atleta.setEquipamento(txtEquipamento.getText().trim());
                atleta.setTelefone1(txtFone1.getText().trim());
                atleta.setTelefone2(txtFone2.getText().trim());
                atleta.setEmail(txtEmail.getText().trim());

                atleta.getEndereco().setLogradouro(FormatarUtil.iniciaisMaiusculas(txtLogradouro.getText().trim()));
                atleta.getEndereco().setNumero(GenericValidator.isInt(txtNumero.getText()) ? Integer.parseInt(txtNumero.getText()) : null);
                atleta.getEndereco().setComplemento(txtComplemento.getText());
                atleta.getEndereco().setBairro(FormatarUtil.iniciaisMaiusculas(txtBairro.getText()));
                atleta.getEndereco().setCidade(FormatarUtil.iniciaisMaiusculas(txtCidade.getText().trim()));
                atleta.getEndereco().setUf(comboUf.getValue() != null ? comboUf.getValue().getSigla() : null);
                String cep = FormatarUtil.removerMascaraNumero(txtCep.getText());
                atleta.getEndereco().setCep(cep);

                if (operacao == Operacao.EDITAR) {
                    this.atletaEdicao = this.secretario.atualizarAtleta(atleta);
                    this.cj.exibirInformacao(cm.getMensagem("sucesso_control_atualizar", "atleta"));
                } else {
                    this.novoAtleta = this.secretario.criarNovoAtleta(atleta);
                    this.cj.exibirInformacao(cm.getMensagem("sucesso_control_salvar", "atleta"));
                }
                if (salvarFechar != null && salvarFechar) {
                    super.aoFecharJanela();
                    super.getStage().close();
                } else {
                    super.reinicializarJanelaBase();
                }
            }

        } catch (AvisoNegocioException ex) {
            super.cj.exibirAviso(ex.getMessage());
        } catch (IllegalArgumentException | IllegalAccessException ex) {
            super.cj.exibirErro(ex.getMessage());
        } catch (Exception ex) {
            super.cj.exibirErro(cm.getMensagem("erro_control_salvar", "atleta"), GeralUtil.getMensagemOriginalErro(ex));
        }
    }

    private void exibirAtleta() {
        this.txtNome.setText(atletaEdicao.getNome());
        this.txtNascimento.setText(FormatarUtil.localDateToString(atletaEdicao.getDataNascimento()));
        this.txtCpf.setText(FormatarUtil.formatarCpf(atletaEdicao.getCpf()));
        this.txtRg.setText(atletaEdicao.getRg());
        if (atletaEdicao.getSexo().equals(Sexo.MASCULINO)) {
            this.radioMasculino.setSelected(true);
        } else {
            this.radioFeminino.setSelected(true);
        }
        this.comboTipoSanguineo.setValue(atletaEdicao.getTipoSanquineo());
        this.txtProfissao.setText(atletaEdicao.getProfissao());
        this.txtEquipamento.setText(atletaEdicao.getEquipamento());
        this.txtFone1.setText(atletaEdicao.getTelefone1());
        this.txtFone2.setText(atletaEdicao.getTelefone2());
        this.txtEmail.setText(atletaEdicao.getEmail());

        this.txtLogradouro.setText(atletaEdicao.getEndereco().getLogradouro());
        this.txtNumero.setText(atletaEdicao.getEndereco().getNumero() != null ? atletaEdicao.getEndereco().getNumero().toString() : "");
        this.txtComplemento.setText(atletaEdicao.getEndereco().getComplemento());
        this.txtBairro.setText(atletaEdicao.getEndereco().getBairro());
        this.txtCidade.setText(atletaEdicao.getEndereco().getCidade());
        this.comboUf.setValue(EstadoBrasil.getEstado(atletaEdicao.getEndereco().getUf()));
        this.txtCep.setText(FormatarUtil.formatarCep(atletaEdicao.getEndereco().getCep()));
    }

    @Override
    public void setOperacao(Operacao op) {
        this.operacao = op;
    }

    @Override
    public void setAtletaEdicao(Atleta atletaEdicao) {
        this.atletaEdicao = atletaEdicao;
    }

    @Override
    public void setSalvarFechar(Boolean salvarFechar) {
        this.salvarFechar = salvarFechar;
    }

    @Override
    public Atleta getNovoAtleta() {
        return novoAtleta;
    }

    @Override
    public void setNovoAtleta(Atleta atleta) {
        this.novoAtleta = atleta;
    }
}
