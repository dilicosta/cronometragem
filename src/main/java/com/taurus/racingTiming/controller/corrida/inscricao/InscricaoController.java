package com.taurus.racingTiming.controller.corrida.inscricao;

import com.taurus.racingTiming.controller.corrida.inscricao.it.IInscricaoController;
import com.taurus.racingTiming.controller.corrida.inscricao.it.IPopupAtletaInscricaoController;
import com.taurus.exception.AvisoNegocioException;
import com.taurus.exception.NegocioException;
import com.taurus.javafx.controller.FXMLBaseController;
import com.taurus.javafx.converter.GenericComboBoxConverter;
import com.taurus.javafx.util.ControleJanela;
import com.taurus.javafx.util.JavafxUtil;
import com.taurus.javafx.util.ValidarCampoUtil;
import com.taurus.racingTiming.controller.atleta.IAtletaController;
import com.taurus.racingTiming.controller.atleta.IPopupAtletaController;
import com.taurus.racingTiming.controller.corrida.IPopupProvaController;
import com.taurus.racingTiming.entidade.atleta.Atleta;
import com.taurus.racingTiming.entidade.atleta.CategoriaAtleta;
import com.taurus.racingTiming.entidade.atleta.ContatoUrgencia;
import com.taurus.racingTiming.entidade.corrida.AtletaInscricao;
import com.taurus.racingTiming.entidade.corrida.CategoriaDaProva;
import com.taurus.racingTiming.entidade.corrida.Prova;
import com.taurus.racingTiming.negocio.ISecretario;
import com.taurus.racingTiming.util.ListaConstantes;
import com.taurus.util.FormatarUtil;
import com.taurus.util.GeralUtil;
import com.taurus.util.ListaConstantesBase;
import com.taurus.util.ListaConstantesBase.EstadoBrasil;
import com.taurus.util.ListaConstantesBase.Operacao;
import com.taurus.util.annotation.Mascara;
import com.taurus.util.annotation.Parametro;
import com.taurus.util.annotation.ValidarCampo;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class InscricaoController extends FXMLBaseController implements IInscricaoController {

    private static final Log LOG = LogFactory.getLog(InscricaoController.class);

    @Parametro
    private AtletaInscricao atletaInscricaoEdicao;
    @Parametro
    private Operacao operacao;
    @Parametro
    Boolean edicaoApuracao = false;
    @Parametro
    private Boolean salvarFechar;
    @Parametro
    private Prova prova;

    private boolean novoInscricaoEmProvaDefinida = false;

    private AtletaInscricao atletaInscricaoNovo;
    private AtletaInscricao atletaInscricaoDupla;

    private Atleta atleta;
    private boolean exibir = false; // variavel de controle para os eventos onchange nao ativarem na exibicao de valores
    private boolean categoriaDupla;

    public InscricaoController() {
        super();
    }

    @Autowired
    private ISecretario secretario;
    @Autowired
    private IAtletaController atletaController;
    @Autowired
    private IPopupProvaController popupProvaController;
    @Autowired
    private IPopupAtletaController popupAtletaController;
    @Autowired
    private IPopupAtletaInscricaoController popupAtletaInscricaoController;

    @FXML
    AnchorPane panePrincipal;
    @FXML
    @Mascara(tipo = Mascara.TipoMascara.TEXT_FIELD_MAXIMO_CARACTER, tamanhoMax = "50")
    @ValidarCampo
    private TextField txtNomeProva;
    @FXML
    private TextField txtSituacao;
    @FXML
    private Button btnPesquisarProva;
    @FXML
    @ValidarCampo
    private TextField txtData;
    @FXML
    @ValidarCampo
    private TextField txtCidade;
    @FXML
    @ValidarCampo
    private TextField txtUf;

    @FXML
    private TitledPane paneAtleta;
    @FXML
    @Mascara(tipo = Mascara.TipoMascara.TEXT_FIELD_MAXIMO_CARACTER, tamanhoMax = "50")
    @ValidarCampo
    private TextField txtNomeAtleta;
    @FXML
    private Button btnPesquisarAtletaNome;
    @FXML
    private Button btnEditarAtleta;
    @FXML
    private Button btnTrocarAtleta;
    @FXML
    private Button btnNovoAtleta;
    @FXML
    @Mascara(tipo = Mascara.TipoMascara.TEXT_FIELD_CPF)
    private TextField txtCpf;
    @FXML
    private Button btnPesquisarAtletaCpf;
    @FXML
    private TextField txtDtNascimento;
    @FXML
    @Mascara(tipo = Mascara.TipoMascara.TEXT_FIELD_MAXIMO_CARACTER, tamanhoMax = "40")
    private TextField txtEquipe;
    @FXML
    @ValidarCampo
    private ComboBox<CategoriaDaProva> comboCategoria;
    @FXML
    private TextField txtCategoria;

    @FXML
    private Label lblNomeDupla;
    @FXML
    @Mascara(tipo = Mascara.TipoMascara.TEXT_FIELD_MAXIMO_CARACTER, tamanhoMax = "50")
    private TextField txtNomeDupla;
    @FXML
    private Button btnPesquisarDuplaNome;
    @FXML
    private Button btnExcluirDupla;
    @FXML
    private Button btnTrocarDupla;
    @FXML
    private Label lblCpfDupla;
    @FXML
    @Mascara(tipo = Mascara.TipoMascara.TEXT_FIELD_CPF)
    private TextField txtCpfDupla;
    @FXML
    private Button btnPesquisarDuplaCpf;
    @FXML
    private Label lblDtNascDupla;
    @FXML
    private TextField txtDtNascimentoDupla;

    @FXML
    Label lblNumero;
    @FXML
    @Mascara(tipo = Mascara.TipoMascara.TEXT_FIELD_APENAS_NUMERO, tamanhoMax = "8")
    private TextField txtNumero;
    @FXML
    Label lblPercurso;
    @FXML
    private TextField txtPercurso;
    @FXML
    private Button btnSalvar;

    @Value("${fxml.inscricao.view}")
    @Override
    public void setFxmlFilePath(String filePath) {
        this.fxmlFilePath = filePath;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.btnEditarAtleta.visibleProperty().bind(this.btnPesquisarAtletaNome.visibleProperty().not());
        
        this.comboCategoria.setConverter(new GenericComboBoxConverter<>("categoriaAtleta.nome"));
        this.btnPesquisarProva.getStyleClass().add(ListaConstantesBase.EstiloCss.BOTAO_PESQUISAR.getValor());
        this.btnTrocarAtleta.getStyleClass().add(ListaConstantesBase.EstiloCss.BOTAO_TROCAR.getValor());
        this.btnPesquisarAtletaNome.getStyleClass().add(ListaConstantesBase.EstiloCss.BOTAO_PESQUISAR.getValor());
        this.btnEditarAtleta.getStyleClass().add(ListaConstantesBase.EstiloCss.BOTAO_EDITAR.getValor());
        this.btnNovoAtleta.getStyleClass().add(ListaConstantesBase.EstiloCss.BOTAO_NOVO.getValor());
        this.btnPesquisarAtletaCpf.getStyleClass().add(ListaConstantesBase.EstiloCss.BOTAO_PESQUISAR.getValor());
        this.btnTrocarDupla.getStyleClass().add(ListaConstantesBase.EstiloCss.BOTAO_TROCAR.getValor());
        this.btnPesquisarDuplaNome.getStyleClass().add(ListaConstantesBase.EstiloCss.BOTAO_PESQUISAR.getValor());
        this.btnExcluirDupla.getStyleClass().add(ListaConstantesBase.EstiloCss.BOTAO_EXCLUIR.getValor());
        this.btnPesquisarDuplaCpf.getStyleClass().add(ListaConstantesBase.EstiloCss.BOTAO_PESQUISAR.getValor());

        this.btnPesquisarProva.setTooltip(new Tooltip("pesquisar prova"));
        this.btnTrocarAtleta.setTooltip(new Tooltip("trocar atleta"));
        this.btnPesquisarAtletaNome.setTooltip(new Tooltip("pesquisar atleta por nome"));
        this.btnEditarAtleta.setTooltip(new Tooltip("exibir/alterar informações do atleta"));
        this.btnNovoAtleta.setTooltip(new Tooltip("novo atleta"));
        this.btnPesquisarAtletaCpf.setTooltip(new Tooltip("pesquisar atleta por cpf"));
        this.btnTrocarDupla.setTooltip(new Tooltip("trocar dupla"));
        this.btnPesquisarDuplaNome.setTooltip(new Tooltip("pesquisar dupla por nome"));
        this.btnExcluirDupla.setTooltip(new Tooltip("excluir dupla"));
        this.btnPesquisarDuplaCpf.setTooltip(new Tooltip("pesquisar dupla por cpf"));

        this.txtNomeProva.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.equals(oldValue) && !exibir) {
                this.txtData.setText("");
                this.txtCidade.setText("");
                this.txtUf.setText("");
                if (!this.novoInscricaoEmProvaDefinida) {
                    this.prova = null;
                }
                this.paneAtleta.setDisable(true);
            }
        });
        this.paneAtleta.setDisable(true);

        this.txtNomeAtleta.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.equals(oldValue) && !exibir) {
                this.txtCpf.setText("");
                this.txtDtNascimento.setText("");
                this.atleta = null;
                this.btnPesquisarAtletaNome.setVisible(true);
                this.btnPesquisarAtletaCpf.setVisible(true);
            }
        });
        this.txtCpf.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.equals(oldValue) && !exibir) {
                this.txtNomeAtleta.setText("");
                this.atleta = null;
                this.btnPesquisarAtletaNome.setVisible(true);
                this.btnPesquisarAtletaCpf.setVisible(true);
            }
        });

        this.comboCategoria.valueProperty().addListener((ObservableValue<? extends CategoriaDaProva> observable, CategoriaDaProva oldValue, CategoriaDaProva newValue) -> {
            if (newValue == null) {
                InscricaoController.this.txtNumero.setEditable(false);
                InscricaoController.this.txtNumero.setFocusTraversable(false);
            } else if (newValue != oldValue) {
                InscricaoController.this.txtPercurso.setText(newValue == null ? "" : newValue.getPercurso().getNome() + " - " + newValue.getPercurso().getDistancia() + "km");
                InscricaoController.this.txtNumero.setEditable(true);
                InscricaoController.this.txtNumero.setFocusTraversable(true);
                this.txtNumero.requestFocus();
            }
            this.categoriaDupla = newValue == null ? false : newValue.getCategoriaAtleta().isCategoriaDupla();
            this.prepararJanelaComSemDupla();
        });
    }

    @Override
    public void reinicializarJanelaEspecifico() {
        this.paneAtleta.setDisable(true);
    }

    @Override
    public void aoAbrirJanela() {
        switch (this.operacao) {
            case EDITAR:
            case LEITURA:
                this.categoriaDupla = this.atletaInscricaoEdicao.getCategoria().getCategoriaAtleta().isCategoriaDupla();
                this.exibirInscricao();
                break;
            case NOVO:
                this.categoriaDupla = false;
                if (this.novoInscricaoEmProvaDefinida) {
                    this.exibirProva();
                } else {
                    this.prova = null;
                }
                this.atleta = null;
        }
        this.prepararJanelaComSemDupla();

        this.btnTrocarAtleta.setVisible(operacao == Operacao.EDITAR && this.atletaInscricaoEdicao.getCategoria().getProva().getStatus().equals(ListaConstantes.StatusProva.INSCRICAO_ABERTA));
        this.btnPesquisarProva.setVisible(operacao == Operacao.NOVO && !this.novoInscricaoEmProvaDefinida);
        this.btnPesquisarAtletaNome.setVisible(operacao == Operacao.NOVO);
        this.btnPesquisarAtletaCpf.setVisible(operacao == Operacao.NOVO);
        this.btnNovoAtleta.setVisible(operacao == Operacao.NOVO);
        this.txtNomeProva.setEditable(operacao == Operacao.NOVO && !this.novoInscricaoEmProvaDefinida);
        this.txtNomeAtleta.setEditable(operacao == Operacao.NOVO);
        this.txtCpf.setEditable(operacao == Operacao.NOVO);
        this.txtNomeProva.requestFocus();

        this.verificarSomenteLeitura();
    }

    @Override
    public void aoFecharJanelaEspecifico() {
    }

    @FXML
    public void salvar() {
        try {
            if (ValidarCampoUtil.validarCampos(this)) {
                AtletaInscricao atletaInscricao = this.preencherInscricao();
                atletaInscricao.setContatoUrgencia(this.preencherContatoUrgencia());
                AtletaInscricao duplaAntiga = atletaInscricao.getDupla();
                if (atletaInscricao.getCategoria().getCategoriaAtleta().isCategoriaDupla()) {
                    atletaInscricao.setDupla(this.atletaInscricaoDupla);
                    if (this.atletaInscricaoDupla != null) {
                        this.atletaInscricaoDupla.setDupla(atletaInscricao);
                    }
                } else {
                    atletaInscricao.setDupla(null);
                }

                // Caso a inscricao nao tenha mais dupla ou mudar de dupla, remove o vinculo anterior
                if (duplaAntiga != null && !duplaAntiga.equals(atletaInscricao.getDupla())) {
                    duplaAntiga.setDupla(null);
                    this.secretario.atualizarAtletaInscricao(atletaInscricao);
                }

                if (operacao == Operacao.EDITAR) {
                    this.atletaInscricaoEdicao = this.secretario.atualizarAtletaInscricao(atletaInscricao);
                    this.cj.exibirInformacao(cm.getMensagem("sucesso_control_atualizar", "inscricao"));
                } else {
                    this.atletaInscricaoNovo = this.secretario.criarNovoAtletaInscricao(atletaInscricao);
                    this.cj.exibirInformacao(cm.getMensagem("sucesso_control_salvar", "inscricao"));

                    this.limparDadosAtleta();
                    this.txtNomeAtleta.requestFocus();
                }
                if (salvarFechar != null && salvarFechar) {
                    super.aoFecharJanela();
                    super.getStage().close();
                }
            }
        } catch (AvisoNegocioException ex) {
            this.cj.exibirAviso(ex.getMessage());
        } catch (Exception ex) {
            this.cj.exibirErro(cm.getMensagem("erro_control_salvar", "inscricao"), GeralUtil.getMensagemOriginalErro(ex));
        }
    }

    private void exibirInscricao() {
        this.prova = this.atletaInscricaoEdicao.getCategoria().getProva();
        this.atleta = this.atletaInscricaoEdicao.getAtleta();
        this.atletaInscricaoDupla = this.atletaInscricaoEdicao.getDupla();

        this.txtNumero.setText("" + this.atletaInscricaoEdicao.getNumeroAtleta());
        this.txtEquipe.setText(this.atletaInscricaoEdicao.getEquipe());
        this.exibirProva();
        this.exibirAtleta();
        this.exibirAtletaDupla();

        this.comboCategoria.setValue(this.atletaInscricaoEdicao.getCategoria());
        this.txtCategoria.setText(this.atletaInscricaoEdicao.getCategoria().getCategoriaAtleta().getNome());
        this.txtPercurso.setText(this.atletaInscricaoEdicao.getCategoria().getPercurso().getNome());
    }

    private void exibirProva() {
        this.exibir = true;
        this.txtNomeProva.setText(this.prova.getNome());
        this.txtSituacao.setText(this.prova.getStatus().getDescricao());
        this.txtData.setText(FormatarUtil.localDateToString(prova.getData()));
        this.txtCidade.setText(this.prova.getEndereco().getCidade());
        this.txtUf.setText(EstadoBrasil.getEstado(this.prova.getEndereco().getUf()).getNome());
        this.paneAtleta.setDisable(false);
        this.carregarComboCategorias();
        this.exibir = false;
        this.txtNomeAtleta.requestFocus();
    }

    private void exibirAtleta() {
        this.exibir = true;
        this.txtNomeAtleta.setText(this.atleta.getNome());
        this.txtCpf.setText(FormatarUtil.formatarCpf(this.atleta.getCpf()));
        this.txtDtNascimento.setText(FormatarUtil.localDateToString(this.atleta.getDataNascimento()));
        this.carregarComboCategorias();
        this.btnPesquisarAtletaNome.setVisible(false);
        this.btnPesquisarAtletaCpf.setVisible(false);
        this.exibir = false;
        this.txtEquipe.requestFocus();
    }

    private void exibirAtletaDupla() {
        this.txtNomeDupla.setText(this.atletaInscricaoDupla != null ? this.atletaInscricaoDupla.getAtleta().getNome() : "");
        this.txtCpfDupla.setText(this.atletaInscricaoDupla != null ? FormatarUtil.formatarCpf(this.atletaInscricaoDupla.getAtleta().getCpf()) : "");
        this.txtDtNascimentoDupla.setText(this.atletaInscricaoDupla != null ? FormatarUtil.localDateToString(this.atletaInscricaoDupla.getAtleta().getDataNascimento()) : "");
    }

    @Override
    public void setOperacao(Operacao op) {
        this.operacao = op;
    }

    @Override
    public void setAtletaInscricaoEdicao(AtletaInscricao inscricaoEdicao) {
        this.atletaInscricaoEdicao = inscricaoEdicao;
    }

    @Override
    public void setSalvarFechar(Boolean salvarFechar) {
        this.salvarFechar = salvarFechar;
    }

    @Override
    public AtletaInscricao getAtletaInscricaoNovo() {
        return this.atletaInscricaoNovo;
    }

    @Override
    public void setAtletaInscricaoNovo(AtletaInscricao atletaInscricao) {
        this.atletaInscricaoNovo = atletaInscricao;
    }

    @Override
    public void setProva(Prova prova) {
        this.prova = prova;
        this.novoInscricaoEmProvaDefinida = prova != null;
    }

    @Override
    public void setEdicaoApuracao(Boolean edicaoApuracao) {
        this.edicaoApuracao = edicaoApuracao;
    }

    @FXML
    public void pesquisarProva(ActionEvent actionEvent) {
        this.popupProvaController.setNomeProva(this.txtNomeProva.getText());
        this.cj.abrirJanelaPopup(ListaConstantes.Stages.POPUP_PROVA.ordinal(), ListaConstantes.Stages.INSCRICAO.ordinal(), IPopupProvaController.class, (Region) actionEvent.getSource(), ControleJanela.PosicaoReferenciaPopup.DIREITA_CENTRO);
        if (this.popupProvaController.getProvaSelecionado() != null) {
            this.prova = this.popupProvaController.getProvaSelecionado();
            this.exibirProva();
        }
    }

    @FXML
    public void trocarAtleta(ActionEvent actionEvent) {
        this.btnTrocarAtleta.setVisible(false);
        this.btnPesquisarAtletaNome.setVisible(true);
        this.btnPesquisarAtletaCpf.setVisible(true);
        this.btnNovoAtleta.setVisible(true);
        this.txtNomeAtleta.setEditable(true);
        this.txtCpf.setEditable(true);
        this.txtNomeAtleta.requestFocus();
        this.txtNomeAtleta.selectAll();
    }

    @FXML
    public void trocarDupla(ActionEvent actionEvent) {
        this.btnTrocarDupla.setVisible(false);
        this.btnPesquisarDuplaNome.setVisible(true);
        this.btnExcluirDupla.setVisible(true);
        this.btnPesquisarDuplaCpf.setVisible(true);
        this.txtNomeDupla.setEditable(true);
        this.txtCpfDupla.setEditable(true);
        this.txtNomeDupla.requestFocus();
        this.txtNomeDupla.selectAll();
    }

    @FXML
    public void pesquisarAtletaNome(ActionEvent actionEvent) {
        this.popupAtletaController.setNomeAtleta(this.txtNomeAtleta.getText());
        this.cj.abrirJanelaPopup(ListaConstantes.Stages.POPUP_ATLETA.ordinal(), ListaConstantes.Stages.INSCRICAO.ordinal(), IPopupAtletaController.class, (Region) actionEvent.getSource(), ControleJanela.PosicaoReferenciaPopup.DIREITA_CENTRO);
        if (this.popupAtletaController.getAtletaSelecionado() != null) {
            this.atleta = this.popupAtletaController.getAtletaSelecionado();
            if (!this.atletaInscrito()) {
                this.abrirJanelaEdicaoAtleta();
            }
        }
    }

    @FXML
    public void pesquisarDuplaNome(ActionEvent actionEvent) {
        this.popupAtletaInscricaoController.setCategoriaDaProva(this.comboCategoria.getValue());
        this.popupAtletaInscricaoController.setNomeAtleta(this.txtNomeDupla.getText());
        this.cj.abrirJanelaPopup(ListaConstantes.Stages.POPUP_ATLETA_INSCRICAO.ordinal(), ListaConstantes.Stages.INSCRICAO.ordinal(), IPopupAtletaInscricaoController.class, (Region) actionEvent.getSource(), ControleJanela.PosicaoReferenciaPopup.DIREITA_CENTRO);
        if (this.popupAtletaInscricaoController.getAtletaInscricaoSelecionado() != null) {
            AtletaInscricao atletaInscricao = this.popupAtletaInscricaoController.getAtletaInscricaoSelecionado();
            if (atletaInscricao != null) {
                if (atletaInscricao.getDupla() == null) {
                    if (!atletaInscricao.equals(this.atletaInscricaoEdicao)) {
                        this.atletaInscricaoDupla = atletaInscricao;
                        this.exibirAtletaDupla();
                    } else {
                        super.cj.exibirAviso("aviso_dupla_igual_inscricao");
                    }
                } else {
                    super.cj.exibirAviso(cm.getMensagem("aviso_atleta_com_outra_dupla"), cm.getMensagem("aviso_atleta_com_outra_dupla_detalhes", this.atleta.getNome()));
                }
            }
        }
    }

    @FXML
    public void novoAtleta(ActionEvent actionEvent) {
        this.atletaController.setOperacao(Operacao.NOVO);
        this.atletaController.setSalvarFechar(Boolean.TRUE);
        super.cj.abrirJanela(ListaConstantes.Stages.ATLETA.ordinal(), ListaConstantes.Stages.INSCRICAO.ordinal(), IAtletaController.class, "atleta", true);
        this.atleta = this.atletaController.getNovoAtleta();
        this.atletaController.setNovoAtleta(null);
        if (this.atleta != null) {
            this.exibirAtleta();
        }
    }

    @FXML
    public void pesquisarAtletaCpf(ActionEvent actionEvent) {
        Atleta atletaTemp;
        try {
            atletaTemp = this.secretario.pesquisarAtletaPorCpf(FormatarUtil.removerMascaraNumero(this.txtCpf.getText()));
        } catch (NegocioException ex) {
            super.cj.exibirErro(cm.getMensagem("erro_control_pesquisar", "atleta"), GeralUtil.getMensagemOriginalErro(ex));
            return;
        }
        if (atletaTemp == null) {
            super.cj.exibirAviso("aviso_atleta_cpf_nao_encontrado");
        } else {
            this.atleta = atletaTemp;
            if (!this.atletaInscrito()) {
                this.exibirAtleta();
            }
        }
    }

    @FXML
    public void pesquisarDuplaCpf(ActionEvent actionEvent) {
        AtletaInscricao atletaInscricaoTemp;
        try {
            atletaInscricaoTemp = this.secretario.pesquisarAtletaInscricao(this.comboCategoria.getValue(), FormatarUtil.removerMascaraNumero(this.txtCpfDupla.getText()));
        } catch (NegocioException ex) {
            super.cj.exibirErro(cm.getMensagem("erro_control_pesquisar", "inscricao"), GeralUtil.getMensagemOriginalErro(ex));
            return;
        }

        if (atletaInscricaoTemp == null) {
            super.cj.exibirAviso("aviso_atleta_cpf_nao_encontrado");
        } else {
            if (atletaInscricaoTemp.getDupla() == null) {
                if (!atletaInscricaoTemp.equals(this.atletaInscricaoEdicao)) {
                    this.atletaInscricaoDupla = atletaInscricaoTemp;
                    this.exibirAtletaDupla();
                } else {
                    super.cj.exibirAviso("aviso_dupla_igual_inscricao");
                }
            } else {
                super.cj.exibirAviso(cm.getMensagem("aviso_atleta_com_outra_dupla" + this.atleta.getNome()));
            }
        }
    }

    @FXML
    public void excluirDupla() {
        this.atletaInscricaoDupla = null;
        this.exibirAtletaDupla();
    }

    private void carregarComboCategorias() {
        if (this.prova != null && this.atleta != null) {
            List<CategoriaDaProva> categorias = new ArrayList<>();
            try {
                this.prova.setListaCategoriaDaProva(new LinkedHashSet<>(this.secretario.pesquisarCategoriaDaProva(this.prova)));
            } catch (NegocioException ex) {
                super.cj.exibirErro(cm.getMensagem("erro_control_pesquisar", "categoria_atleta"), GeralUtil.getMensagemOriginalErro(ex));
            }
            for (CategoriaDaProva cdp : prova.getListaCategoriaDaProva()) {
                if ((cdp.getCategoriaAtleta().getSexo() == ListaConstantesBase.Sexo.NAO_SE_APLICA || cdp.getCategoriaAtleta().getSexo().equals(this.atleta.getSexo()))
                        && this.idadeCompativel(cdp.getCategoriaAtleta(), this.atleta)) {
                    categorias.add(cdp);
                }
            }
            this.comboCategoria.getItems().clear();
            this.comboCategoria.getItems().addAll(categorias);
        }
    }

    private boolean idadeCompativel(CategoriaAtleta categoriaAtleta, Atleta atleta) {
        if (categoriaAtleta.getIdadeMinima() == null) {
            return true;
        } else {
            Integer idadeAtleta = LocalDate.now().getYear() - atleta.getDataNascimento().getYear();
            if (categoriaAtleta.getIdadeMinima() <= idadeAtleta) {
                return (categoriaAtleta.getIdadeMaxima() == null || categoriaAtleta.getIdadeMaxima() >= idadeAtleta);
            } else {
                return false;
            }

        }
    }

    private void limparDadosAtleta() {
        this.txtNomeAtleta.setText("");
        this.txtCpf.setText("");
        this.txtDtNascimento.setText("");
        this.txtNomeDupla.setText("");
        this.txtCpfDupla.setText("");
        this.txtDtNascimentoDupla.setText("");
        this.atleta = null;
        this.atletaInscricaoDupla = null;
    }

    private boolean atletaInscrito() {
        if (this.prova == null) {
            super.cj.exibirAviso(cm.getMensagem("aviso_selecione_para_continuar", "prova"));
            return true;
        } else if (this.atleta == null) {
            super.cj.exibirAviso(cm.getMensagem("aviso_selecione_para_continuar", "atleta"));
            return true;
        } else {
            if (this.operacao == Operacao.EDITAR) {
                if (this.atleta.equals(this.atletaInscricaoEdicao.getAtleta())) {
                    return false;
                }
            }
            AtletaInscricao atletaInscricao;
            try {
                atletaInscricao = this.secretario.pesquisarAtletaInscricao(this.prova, this.atleta);
            } catch (NegocioException ex) {
                super.cj.exibirErro(cm.getMensagem("erro_control_pesquisar", "inscricao"), GeralUtil.getMensagemOriginalErro(ex));
                return true;
            }
            if (atletaInscricao == null) {
                return false;
            } else {
                super.cj.exibirAviso(cm.getMensagem("aviso_atleta_inscrito", this.atleta.getNome(), this.prova.getNome(), atletaInscricao.getCategoria().getCategoriaAtleta().getNome()));
                return true;
            }
        }
    }

    private AtletaInscricao preencherInscricao() throws AvisoNegocioException, NegocioException {
        AtletaInscricao atletaInscricao;
        if (operacao == Operacao.EDITAR) {
            atletaInscricao = this.atletaInscricaoEdicao;
        } else {
            atletaInscricao = new AtletaInscricao();
        }

        atletaInscricao.setCategoria(this.comboCategoria.getValue());
        atletaInscricao.setAtleta(this.atleta);
        atletaInscricao.setEquipe(this.txtEquipe.getText() == null ? "" : this.txtEquipe.getText().trim());

        if (this.novoInscricaoEmProvaDefinida && !ValidarCampoUtil.validarCampos(this.txtNumero)) {
            throw new AvisoNegocioException("Preenchimento obrigatório do número do atleta.");
        }

        Integer numero = JavafxUtil.getInteger(this.txtNumero);
        if (numero != null && !numero.equals(atletaInscricao.getNumeroAtleta())) {
            AtletaInscricao inscricaoExistente = this.secretario.pesquisarAtletaInscricaoPorNumeroAtleta(this.prova, numero);
            if (inscricaoExistente != null) {
                throw new AvisoNegocioException(cm.getMensagem("aviso_num_insc_existente", inscricaoExistente.getAtleta().getNome(), inscricaoExistente.getCategoria().getCategoriaAtleta().getNome()));
            }
        }
        atletaInscricao.setNumeroAtleta(numero);
        return atletaInscricao;
    }

    private ContatoUrgencia preencherContatoUrgencia() {
        return null;
    }

    private void verificarSomenteLeitura() {
        boolean somenteLeitura = this.operacao == Operacao.LEITURA && (this.edicaoApuracao == null || !this.edicaoApuracao);

        this.txtEquipe.setEditable(!somenteLeitura);
        this.comboCategoria.setVisible(!somenteLeitura);
        this.txtCategoria.setVisible(somenteLeitura);
        this.txtNumero.setEditable(!somenteLeitura);
        this.btnSalvar.setDisable(somenteLeitura);
    }

    private void prepararJanelaComSemDupla() {
        this.lblNomeDupla.setVisible(this.categoriaDupla);
        this.txtNomeDupla.setVisible(this.categoriaDupla);
        this.btnPesquisarDuplaNome.setVisible(this.categoriaDupla && this.operacao == Operacao.NOVO);
        this.btnTrocarDupla.setVisible(this.categoriaDupla && this.operacao == Operacao.EDITAR);
        this.btnExcluirDupla.setVisible(false);
        this.lblCpfDupla.setVisible(this.categoriaDupla);
        this.txtCpfDupla.setVisible(this.categoriaDupla);
        this.btnPesquisarDuplaCpf.setVisible(this.categoriaDupla && this.operacao == Operacao.NOVO);
        this.lblDtNascDupla.setVisible(this.categoriaDupla);
        this.txtDtNascimentoDupla.setVisible(this.categoriaDupla);

        if (!this.categoriaDupla) {
            super.getStage().setHeight(460d);
            this.panePrincipal.setPrefHeight(431d);
            this.paneAtleta.setPrefHeight(197d);
            this.lblNumero.setLayoutY(119d);
            this.txtNumero.setLayoutY(136d);
            this.lblPercurso.setLayoutY(119d);
            this.txtPercurso.setLayoutY(136d);
            this.btnSalvar.setLayoutY(392d);
        } else {
            super.getStage().setHeight(511d);
            this.panePrincipal.setPrefHeight(482d);
            this.paneAtleta.setPrefHeight(248d);
            this.lblNumero.setLayoutY(170d);
            this.txtNumero.setLayoutY(187d);
            this.lblPercurso.setLayoutY(170d);
            this.txtPercurso.setLayoutY(187d);
            this.btnSalvar.setLayoutY(443d);
        }
    }

    private void abrirJanelaEdicaoAtleta() {
        this.atletaController.setOperacao(Operacao.EDITAR);
        this.atletaController.setSalvarFechar(Boolean.TRUE);
        this.atletaController.setAtletaEdicao(this.atleta);
        super.cj.abrirJanela(ListaConstantes.Stages.ATLETA.ordinal(), ListaConstantes.Stages.INSCRICAO.ordinal(), IAtletaController.class, "atleta", true);
        this.exibirAtleta();
    }
    
    @FXML
    public void editarAtleta(ActionEvent actionEvent) {
        this.abrirJanelaEdicaoAtleta();
    }
}
