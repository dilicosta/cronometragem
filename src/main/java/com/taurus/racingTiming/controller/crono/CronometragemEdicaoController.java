package com.taurus.racingTiming.controller.crono;

import com.taurus.racingTiming.controller.corrida.inscricao.it.IInscricaoController;
import com.taurus.exception.NegocioException;
import com.taurus.javafx.controller.FXMLBaseController;
import com.taurus.javafx.converter.GenericComboBoxConverter;
import com.taurus.javafx.listener.SaidaFocoListener;
import com.taurus.javafx.util.ControleJanela;
import com.taurus.javafx.util.JavafxUtil;
import com.taurus.javafx.util.ValidarCampoUtil;
import com.taurus.racingTiming.controller.IImagemViewController;
import com.taurus.racingTiming.entidade.corrida.AtletaInscricao;
import com.taurus.racingTiming.entidade.corrida.Cronometragem;
import com.taurus.racingTiming.entidade.corrida.Prova;
import com.taurus.racingTiming.negocio.IRolex;
import com.taurus.racingTiming.negocio.ISecretario;
import com.taurus.racingTiming.util.ListaConstantes;
import com.taurus.util.FormatarUtil;
import com.taurus.util.GeralUtil;
import com.taurus.util.ListaConstantesBase;
import com.taurus.util.ListaConstantesBase.Operacao;
import com.taurus.util.annotation.Mascara;
import com.taurus.util.annotation.Parametro;
import java.io.ByteArrayInputStream;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ResourceBundle;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class CronometragemEdicaoController extends FXMLBaseController implements ICronometragemEdicaoController {

    private static final Log LOG = LogFactory.getLog(CronometragemEdicaoController.class);

    @Autowired
    private IImagemViewController cronometragemImagemController;

    private Cronometragem cronometragemEdicao;

    @Parametro
    private AtletaInscricao atletaInscricao;
    @Parametro
    private Operacao operacao;

    private LocalDateTime horaRegistro;
    private Cronometragem cronometragemNovo;

    private ObjectProperty<Image> imageProperty = new SimpleObjectProperty<>();

    public CronometragemEdicaoController() {
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
    @Mascara(tipo = Mascara.TipoMascara.TEXT_FIELD_APENAS_NUMERO, tamanhoMax = "8")
    private TextField txtNumeroAtleta;
    @FXML
    private Button btnTrocarAtleta;
    @FXML
    private Button btnNovoAtleta;
    @FXML
    private TextField txtNomeAtleta;
    @FXML
    private TextField txtLargada;
    @FXML
    private TextField txtCategoria;

    @FXML
    private DatePicker dpDataCrono;
    @FXML
    @Mascara(tipo = Mascara.TipoMascara.TEXT_FIELD_CUSTOM, custom = "99:99:99:999")
    private TextField txtHoraCrono;
    @FXML
    private CheckBox checkDuvida;
    @FXML
    private ComboBox<SituacaoCrono> comboStatus;
    @FXML
    private TextField txtTempo;
    @FXML
    private TextField txtVolta;

    @FXML
    private ImageView imageView;

    public enum SituacaoCrono {
        ATIVA("Ativa"), EXCLUIDA("Excluída");

        private final String descricao;

        SituacaoCrono(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }
    }

    @Value("${fxml.cronoEdicao.view}")
    @Override
    public void setFxmlFilePath(String filePath) {
        this.fxmlFilePath = filePath;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.btnTrocarAtleta.getStyleClass().add(ListaConstantesBase.EstiloCss.BOTAO_TROCAR.getValor());
        this.btnTrocarAtleta.setTooltip(new Tooltip("trocar atleta"));
        this.btnNovoAtleta.getStyleClass().add(ListaConstantesBase.EstiloCss.BOTAO_NOVO.getValor());
        this.btnNovoAtleta.setTooltip(new Tooltip("inscrever novo atleta"));
        this.btnNovoAtleta.setVisible(false);

        this.comboStatus.setConverter(new GenericComboBoxConverter<>("descricao"));
        this.comboStatus.getItems().addAll(SituacaoCrono.values());

        this.imageView.imageProperty().bind(this.imageProperty);
        this.imageView.visibleProperty().bind(this.imageProperty.isNotNull());

        this.txtNumeroAtleta.focusedProperty().addListener(new SaidaFocoListener(this.txtNumeroAtleta) {
            @Override
            public void executar() {
                Cronometragem crono = CronometragemEdicaoController.this.cronometragemEdicao;
                if (crono != null) {
                    try {
                        CronometragemEdicaoController.this.atletaInscricao = CronometragemEdicaoController.this.secretario.pesquisarAtletaInscricaoPorNumeroAtleta(crono.getProva(), JavafxUtil.getInteger(CronometragemEdicaoController.this.txtNumeroAtleta));
                        CronometragemEdicaoController.this.exibirInscricao();
                    } catch (NegocioException ex) {
                        CronometragemEdicaoController.super.cj.exibirErro(cm.getMensagem("erro_control_pesquisar", "inscricao"), GeralUtil.getMensagemOriginalErro(ex));
                    }
                }
            }
        });

    }

    @Override
    public void reinicializarJanelaEspecifico() {
        this.txtNumeroAtleta.setEditable(false);
        this.btnTrocarAtleta.setVisible(true);
        this.btnNovoAtleta.setVisible(false);
    }

    @Override
    public void aoAbrirJanela() {
        Prova prova;
        if (operacao == Operacao.EDITAR) {
            prova = this.cronometragemEdicao.getProva();
            if (this.cronometragemEdicao.getAtletaInscricao() == null) {
                this.txtNumeroAtleta.setText("" + this.cronometragemEdicao.getNumeroAtleta());
                this.txtNumeroAtleta.selectAll();
                this.txtNumeroAtleta.requestFocus();
                this.trocarAtleta(null);
            } else {
                this.atletaInscricao = this.cronometragemEdicao.getAtletaInscricao();
                this.exibirInscricao();
                this.txtHoraCrono.requestFocus();
            }
            this.dpDataCrono.getEditor().setText(FormatarUtil.localDateTimeToString(this.cronometragemEdicao.getHoraRegistro(), FormatarUtil.FORMATO_DATA));
            this.txtHoraCrono.setText(FormatarUtil.localDateTimeToString(this.cronometragemEdicao.getHoraRegistro(), FormatarUtil.FORMATO_HORA_MILI));
            this.checkDuvida.setSelected(this.cronometragemEdicao.isDuvida());
            this.comboStatus.setValue(cronometragemEdicao.isExcluida() ? SituacaoCrono.EXCLUIDA : SituacaoCrono.ATIVA);
            if (this.cronometragemEdicao.getTempoVolta() != null) {
                this.txtTempo.setText(FormatarUtil.formatarTempoMilisegundos(this.cronometragemEdicao.getTempoVolta()));
            }
            if (this.cronometragemEdicao.getVolta() != null) {
                this.txtVolta.setText(this.cronometragemEdicao.getVolta().toString());
            }
            try {
                if (this.rolex.carregarImagemCronometragem(this.cronometragemEdicao)) {
                    this.imageProperty.set(new Image(new ByteArrayInputStream(this.cronometragemEdicao.getCronometragemImagem().getImagem())));
                } else this.imageProperty.set(null);
            } catch (NegocioException ex) {
                super.cj.exibirErro(cm.getMensagem("erro_control_pesquisar", "imagem"), GeralUtil.getMensagemOriginalErro(ex));
            }
        } else {
            prova = this.atletaInscricao.getCategoria().getProva();

            this.exibirInscricao();
            this.dpDataCrono.getEditor().setText(FormatarUtil.localDateToString(prova.getData()));
            this.checkDuvida.setSelected(false);
            this.checkDuvida.setDisable(true);
            this.txtHoraCrono.requestFocus();

        }
        this.txtNomeProva.setText(prova.getNome());
        this.txtDataProva.setText(FormatarUtil.localDateToString(prova.getData()));

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
            if (this.validarCronometragem()) {

                Cronometragem crono = new Cronometragem();
                AtletaInscricao inscricaoAntiga = null;
                if (this.operacao == Operacao.EDITAR) {
                    crono = this.cronometragemEdicao;
                    inscricaoAntiga = this.cronometragemEdicao.getAtletaInscricao();
                } else {
                    crono.setProva(this.atletaInscricao.getCategoria().getProva());
                }

                crono.setNumeroAtleta(this.atletaInscricao.getNumeroAtleta());
                crono.setAtletaInscricao(this.atletaInscricao);
                crono.setHoraRegistro(this.horaRegistro);
                crono.setDuvida(this.checkDuvida.isSelected());
                crono.setExcluida(this.comboStatus.getValue() == SituacaoCrono.EXCLUIDA);

                if (operacao == Operacao.EDITAR) {
                    if (inscricaoAntiga != null) {
                        this.cronometragemEdicao = this.rolex.trocarCronometragemAtleta(inscricaoAntiga, crono);
                    } else {
                        this.cronometragemEdicao = this.rolex.atualizarCronometragemComVerificacaoTempoRelacionado(crono);
                    }
                    this.cj.exibirInformacao(cm.getMensagem("sucesso_control_atualizar", "cronometragem"));
                } else {
                    this.cronometragemNovo = this.rolex.criarNovoCronometragemComVerificacaoTempoRelacionado(crono);
                    this.cj.exibirInformacao(cm.getMensagem("sucesso_control_salvar", "cronometragem"));
                }
                super.aoFecharJanela();
                super.getStage().close();
            }
        } catch (Exception ex) {
            this.cj.exibirErro(cm.getMensagem("erro_control_salvar", "cronometragem"), GeralUtil.getMensagemOriginalErro(ex));
        }
    }

    @Override
    public void setOperacao(Operacao op) {
        this.operacao = op;
    }

    @Override
    public void setAtletaInscricao(AtletaInscricao inscricao) {
        this.atletaInscricao = inscricao;
    }

    @Override
    public Cronometragem getCronometragemEdicao() {
        return this.cronometragemEdicao;
    }

    @Override
    public void setCronometragemEdicao(Cronometragem crono) {
        this.cronometragemEdicao = crono;
    }

    @Override
    public Cronometragem getCronometragemNovo() {
        return this.cronometragemNovo;
    }

    @Override
    public void setCronometragemNovo(Cronometragem cronometragem) {
        this.cronometragemNovo = cronometragem;
    }

    @FXML
    public void trocarAtleta(ActionEvent actionEvent) {
        this.btnTrocarAtleta.setVisible(false);
        this.btnNovoAtleta.setVisible(true);
        this.txtNumeroAtleta.setEditable(true);
    }

    @FXML
    public void novoAtleta(ActionEvent actionEvent) {
        this.inscricaoController.setOperacao(Operacao.NOVO);
        this.inscricaoController.setSalvarFechar(Boolean.TRUE);
        this.inscricaoController.setProva(this.cronometragemEdicao.getProva());
        super.cj.abrirJanela(ListaConstantes.Stages.INSCRICAO.ordinal(), ListaConstantes.Stages.CRONOMETRAGEM_EDICAO.ordinal(), IInscricaoController.class, "Nova Inscrição", true);
        this.atletaInscricao = this.inscricaoController.getAtletaInscricaoNovo();
        this.inscricaoController.setAtletaInscricaoNovo(null);
        if (this.atletaInscricao != null) {
            this.exibirInscricao();
        }
    }

    private boolean validarCronometragem() {
        if (ValidarCampoUtil.validarCampos(this.txtNumeroAtleta, this.dpDataCrono, this.txtHoraCrono)) {
            if (this.atletaInscricao == null) {
                super.cj.exibirAviso("Informe um atleta para registrar a cronometragem");
                return false;
            }

            String dataHoraCrono = this.dpDataCrono.getEditor().getText() + " " + this.txtHoraCrono.getText();
            this.horaRegistro = FormatarUtil.stringToLocalDateTime(dataHoraCrono, FormatarUtil.FORMATO_DATA_HORA_MILI);
            if (this.horaRegistro == null) {
                super.cj.exibirAviso("aviso_hora_invalida");
                return false;
            }
            if (this.horaRegistro.isBefore(this.atletaInscricao.getCategoria().getLargada().getHoraInicio())) {
                super.cj.exibirAviso("aviso_hora_crono_anterior_largada");
                return false;
            }

            return true;
        }
        return false;
    }

    @FXML
    public void abrirPopupFoto() {
        this.cronometragemImagemController.setImagem(new Image(new ByteArrayInputStream(this.cronometragemEdicao.getCronometragemImagem().getImagem())));
        super.cj.abrirPopup(ListaConstantes.Stages.CRONOMETRAGEM_EDICAO.ordinal(), IImagemViewController.class, this.comboStatus, ControleJanela.PosicaoReferenciaPopup.ESQUERDA_ACIMA);

    }
}
