package com.taurus.racingTiming.controller.corrida.inscricao;

import com.taurus.racingTiming.controller.corrida.inscricao.it.IInscricaoNumeracaoAutomaticaController;
import com.taurus.exception.AvisoNegocioException;
import com.taurus.exception.NegocioException;
import com.taurus.javafx.componente.BotaoCelulaTableValueFactory;
import com.taurus.javafx.componente.BotaoTableColumnFactory;
import com.taurus.javafx.componente.CheckBoxTableColumnFactory;
import com.taurus.javafx.controller.FXMLBaseController;
import com.taurus.javafx.tableView.TableCellCommitGeneric;
import com.taurus.javafx.tableView.TableCellEditFactoryGeneric;
import com.taurus.javafx.util.JavafxUtil;
import com.taurus.racingTiming.controller.corrida.inscricao.it.IInscricaoNumeracaoManualController;
import com.taurus.racingTiming.entidade.corrida.CategoriaDaProva;
import com.taurus.racingTiming.entidade.corrida.Prova;
import com.taurus.racingTiming.negocio.ISecretario;
import com.taurus.racingTiming.pojo.CategoriaProvaResumo;
import com.taurus.racingTiming.util.ListaConstantes;
import com.taurus.racingTiming.util.ListaConstantes.Stages;
import com.taurus.util.FormatarUtil;
import com.taurus.util.GeralUtil;
import com.taurus.util.ListaConstantesBase;
import com.taurus.util.annotation.Parametro;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class InscricaoNumeracaoAutomaticaController extends FXMLBaseController implements IInscricaoNumeracaoAutomaticaController {

    private static final Log LOG = LogFactory.getLog(InscricaoNumeracaoAutomaticaController.class);

    @Parametro
    private Prova prova;

    public InscricaoNumeracaoAutomaticaController() {
        super();
    }

    @Autowired
    private IInscricaoNumeracaoManualController inscricaoNumeracaoManualController;
    @Autowired
    private ISecretario secretario;

    @FXML
    private TextField txtNomeProva;
    @FXML
    private TextField txtDataProva;
    @FXML
    private TextField txtSituacaoProva;

    @FXML
    private TableView<CategoriaProvaResumo> tabCategoria;
    @FXML
    private TableColumn<CategoriaProvaResumo, Boolean> colCheckBox;
    @FXML
    private TableColumn<CategoriaProvaResumo, String> colCategoria;
    @FXML
    private TableColumn<CategoriaProvaResumo, Long> colInscritos;
    @FXML
    private TableColumn<CategoriaProvaResumo, Long> colSemNumeracao;
    @FXML
    private TableColumn<CategoriaProvaResumo, Integer> colDigito;
    @FXML
    private TableColumn<CategoriaProvaResumo, Integer> colFaixaInicio;
    @FXML
    private TableColumn<CategoriaProvaResumo, Integer> colFaixaFim;
    @FXML
    private TableColumn colNumManual;

    @FXML
    private CheckBox checkSubstituirNumeracao;

    @Value("${fxml.inscricaoNumeracaoAutomatica.view}")
    @Override
    public void setFxmlFilePath(String filePath) {
        this.fxmlFilePath = filePath;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.configurarTabela();

        this.configurarEdicaoDigitos();
        this.configurarEdicaoInicioNumeracao();
        this.configurarEdicaoFimNumeracao();
    }

    @Override
    public void reinicializarJanelaEspecifico() {
        this.tabCategoria.getItems().clear();
    }

    @Override
    public void aoAbrirJanela() {
        this.txtNomeProva.setText(this.prova.getNome());
        this.txtDataProva.setText(FormatarUtil.localDateToString(this.prova.getData()));
        this.txtSituacaoProva.setText(this.prova.getStatus().getDescricao());

        try {
            List<CategoriaDaProva> categorias = this.secretario.pesquisarCategoriaDaProva(prova);
            List<CategoriaProvaResumo> categoriasInscritos = new ArrayList<>();
            for (CategoriaDaProva categoriaDaProva : categorias) {
                Long totalInscritos = this.secretario.pesquisarTotalInscricoes(categoriaDaProva);
                Long totalSemNumero = this.secretario.pesquisarTotalAtletaSemNumero(categoriaDaProva);
                categoriasInscritos.add(new CategoriaProvaResumo(categoriaDaProva, totalInscritos, totalSemNumero));
            }
            this.tabCategoria.getItems().addAll(categoriasInscritos);
        } catch (NegocioException ex) {
            super.cj.exibirErro("Erro ao carregar as informações das inscrições por categorias.", GeralUtil.getMensagemOriginalErro(ex));
        }

    }

    @Override
    public void aoFecharJanelaEspecifico() {

    }

    private void configurarEdicaoDigitos() {
        // para editar o campo nome
        this.colDigito.setCellFactory(col -> new TableCellEditFactoryGeneric<CategoriaProvaResumo, Integer>(true, TableCellEditFactoryGeneric.Tipo.NUMERO_INTEGER) {
            @Override
            public boolean isValido(Integer digitos) {
                return digitos > 0 && digitos < 10;
            }
        });

        this.colDigito.setOnEditCommit(new TableCellCommitGeneric<CategoriaProvaResumo, Integer>(true) {
            @Override
            public void atualizar(CategoriaProvaResumo rowValue, Integer novoValor, Integer valorAntigo, boolean valido) {
                CategoriaDaProva cat = rowValue.getCategoriaDaProva();
                if (novoValor == null || (novoValor > 0 && novoValor < 10)) {
                    if (novoValor == null) {
                        cat.setNumeracaoAutomatica(ListaConstantes.NumeracaoAutomatica.NA);
                    } else if (cat.getInicioNumeracao() != null && cat.getFimNumeracao() != null) {
                        cat.setNumeracaoAutomatica(ListaConstantes.NumeracaoAutomatica.POR_FAIXA);
                    } else {
                        cat.setNumeracaoAutomatica(ListaConstantes.NumeracaoAutomatica.SEQUENCIAL);
                    }
                }
                cat.setDigitosNumeracao(novoValor);
                InscricaoNumeracaoAutomaticaController.this.atualizarCategoria(cat);
            }
        });
    }

    private void configurarEdicaoInicioNumeracao() {
        // para editar o campo sigla
        this.colFaixaInicio.setCellFactory(col -> new TableCellEditFactoryGeneric<CategoriaProvaResumo, Integer>(true, TableCellEditFactoryGeneric.Tipo.NUMERO_INTEGER) {
            @Override
            public boolean isValido(Integer numero) {
                return numero > 0;
            }
        });

        this.colFaixaInicio.setOnEditCommit(new TableCellCommitGeneric<CategoriaProvaResumo, Integer>(true) {
            @Override
            public void atualizar(CategoriaProvaResumo rowValue, Integer novoValor, Integer valorAntigo, boolean valido) {
                rowValue.getCategoriaDaProva().setInicioNumeracao(novoValor);
                if (novoValor == null || novoValor > 0) {
                    if (rowValue.getCategoriaDaProva().getInicioNumeracao() != null && rowValue.getCategoriaDaProva().getFimNumeracao() != null) {
                        rowValue.getCategoriaDaProva().setNumeracaoAutomatica(ListaConstantes.NumeracaoAutomatica.POR_FAIXA);
                        validarFaixaNumeracao(rowValue.getCategoriaDaProva());
                    } else {
                        rowValue.getCategoriaDaProva().setNumeracaoAutomatica(ListaConstantes.NumeracaoAutomatica.SEQUENCIAL);
                    }
                }
                InscricaoNumeracaoAutomaticaController.this.atualizarCategoria(rowValue.getCategoriaDaProva());
            }
        });
    }

    private void configurarEdicaoFimNumeracao() {
        // para editar o campo sigla
        this.colFaixaFim.setCellFactory(col -> new TableCellEditFactoryGeneric<CategoriaProvaResumo, Integer>(true, TableCellEditFactoryGeneric.Tipo.NUMERO_INTEGER) {
            @Override
            public boolean isValido(Integer numero) {
                return numero > 0;
            }
        });

        this.colFaixaFim.setOnEditCommit(new TableCellCommitGeneric<CategoriaProvaResumo, Integer>(true) {
            @Override
            public void atualizar(CategoriaProvaResumo rowValue, Integer novoValor, Integer valorAntigo, boolean valido) {
                rowValue.getCategoriaDaProva().setFimNumeracao(novoValor);

                if (novoValor == null || novoValor > 0) {
                    if (rowValue.getCategoriaDaProva().getInicioNumeracao() != null && rowValue.getCategoriaDaProva().getFimNumeracao() != null) {
                        rowValue.getCategoriaDaProva().setNumeracaoAutomatica(ListaConstantes.NumeracaoAutomatica.POR_FAIXA);
                        validarFaixaNumeracao(rowValue.getCategoriaDaProva());
                    } else {
                        rowValue.getCategoriaDaProva().setNumeracaoAutomatica(ListaConstantes.NumeracaoAutomatica.SEQUENCIAL);
                    }
                }
                InscricaoNumeracaoAutomaticaController.this.atualizarCategoria(rowValue.getCategoriaDaProva());
            }
        });
    }

    @Override
    public void setProva(Prova prova) {
        this.prova = prova;
    }

    private void configurarTabela() {
        this.colCategoria.setCellValueFactory(linha -> new ReadOnlyObjectWrapper(linha.getValue().getCategoriaDaProva().getCategoriaAtleta().getNome()));
        this.colInscritos.setCellValueFactory(linha -> new ReadOnlyObjectWrapper(linha.getValue().getInscritos()));
        this.colSemNumeracao.setCellValueFactory(linha -> new ReadOnlyObjectWrapper(linha.getValue().getSemNumeracao()));
        this.colDigito.setCellValueFactory(linha -> new ReadOnlyObjectWrapper(linha.getValue().getCategoriaDaProva().getDigitosNumeracao()));
        this.colFaixaInicio.setCellValueFactory(linha -> new ReadOnlyObjectWrapper(linha.getValue().getCategoriaDaProva().getInicioNumeracao()));
        this.colFaixaFim.setCellValueFactory(linha -> new ReadOnlyObjectWrapper(linha.getValue().getCategoriaDaProva().getFimNumeracao()));

        this.colCategoria.getStyleClass().add(ListaConstantesBase.EstiloCss.TAB_COL_ESQUERDO.getValor());
        this.colInscritos.getStyleClass().add(ListaConstantesBase.EstiloCss.TAB_COL_CENTRO.getValor());
        this.colSemNumeracao.getStyleClass().add(ListaConstantesBase.EstiloCss.TAB_COL_CENTRO.getValor());
        this.colDigito.getStyleClass().add(ListaConstantesBase.EstiloCss.TAB_COL_CENTRO.getValor());
        this.colFaixaInicio.getStyleClass().add(ListaConstantesBase.EstiloCss.TAB_COL_CENTRO.getValor());
        this.colFaixaFim.getStyleClass().add(ListaConstantesBase.EstiloCss.TAB_COL_CENTRO.getValor());

        this.colCheckBox.getStyleClass().add(ListaConstantesBase.EstiloCss.TAB_COL_CENTRO.getValor());
        this.colCheckBox.setGraphic(JavafxUtil.criarSelectAllCheckBox(this.tabCategoria.getItems()));
        this.colCheckBox.setCellValueFactory(new PropertyValueFactory<>("selecionar"));
        this.colCheckBox.setCellFactory(new CheckBoxTableColumnFactory<CategoriaProvaResumo>() {
            @Override
            public void eventoCheckBox(ActionEvent actionEvent, CategoriaProvaResumo linhaView) {
            }

            @Override
            protected boolean exibirItem(CategoriaProvaResumo linhaView) {
                return linhaView != null;
            }
        });

        this.colNumManual.getStyleClass().add(ListaConstantesBase.EstiloCss.TAB_COL_CENTRO.getValor());
        this.colNumManual.setCellValueFactory(new BotaoCelulaTableValueFactory());
        this.colNumManual.setCellFactory(new BotaoTableColumnFactory<CategoriaProvaResumo>("numeração manual", ListaConstantesBase.EstiloCss.BOTAO_EDITAR.getValor()) {
            @Override
            public void eventoBotao(ActionEvent actionEvent, CategoriaProvaResumo linhaView) {
                inscricaoNumeracaoManualController.setCategoriaDaProva(linhaView.getCategoriaDaProva());
                cj.abrirJanela(Stages.INSCRICAO_NUM_MANUAL.ordinal(), Stages.INSCRICAO_NUM_AUTOMATICA.ordinal(), IInscricaoNumeracaoManualController.class, "t_inscricao_num_manual", true);
            }
        });
    }

    private void atualizarCategoria(CategoriaDaProva categoriaDaProva) {
        try {
            this.secretario.atualizarCategoriaDaProva(categoriaDaProva);
        } catch (NegocioException ex) {
            super.cj.exibirErro(cm.getMensagem("erro_control_atualizar", "Categoria"), GeralUtil.getMensagemOriginalErro(ex));
        }
    }

    @FXML
    public void realizarNumeracaoAutomatica(ActionEvent actionEvent) {
        List<CategoriaDaProva> categoriasSelecionadas = this.getCategoriasSelecionadas();
        boolean valido = true;
        for (CategoriaDaProva cdp : categoriasSelecionadas) {
            if (cdp.getNumeracaoAutomatica() == ListaConstantes.NumeracaoAutomatica.POR_FAIXA && !validarFaixaNumeracao(cdp)) {
                valido = false;
            }
        }
        if (valido) {
            boolean substituirNumeracao = this.checkSubstituirNumeracao.isSelected();
            if (substituirNumeracao && !super.cj.exibirDialogSimNao("Numeração automática", "Confirmar a substituição de todos os números de atletas das categorias selecionadas?", "Esta operação é irreversível e todos os números de atletas previamente cadastrados serão perdidos.")) {
                return;
            }
            try {
                this.secretario.inserirNumeracaoAutomatica(categoriasSelecionadas, substituirNumeracao);
                super.cj.exibirInformacao("Numeração automática inserida com sucesso!");
            } catch (NegocioException ex) {
                super.cj.exibirErro(cm.getMensagem("erro_control_salvar", "Categoria"), GeralUtil.getMensagemOriginalErro(ex));
            } catch (AvisoNegocioException ex) {
                super.cj.exibirAviso(ex.getMessage());
            }
        }

    }

    private boolean validarFaixaNumeracao(CategoriaDaProva catProva) {
        if (catProva.getInicioNumeracao() >= catProva.getFimNumeracao()) {
            super.cj.exibirAviso(cm.getMensagem("aviso_faixa_numeracao_invalida", catProva.getCategoriaAtleta().getNome()));
            return false;
        }

        for (CategoriaProvaResumo cpr : this.tabCategoria.getItems()) {
            if (!catProva.equals(cpr.getCategoriaDaProva()) && cpr.getCategoriaDaProva().getNumeracaoAutomatica() == ListaConstantes.NumeracaoAutomatica.POR_FAIXA) {
                if ((catProva.getInicioNumeracao() >= cpr.getCategoriaDaProva().getInicioNumeracao() && catProva.getInicioNumeracao() <= cpr.getCategoriaDaProva().getFimNumeracao())
                        || (catProva.getFimNumeracao() >= cpr.getCategoriaDaProva().getInicioNumeracao() && catProva.getFimNumeracao() <= cpr.getCategoriaDaProva().getFimNumeracao())
                        || (catProva.getInicioNumeracao() < cpr.getCategoriaDaProva().getInicioNumeracao() && catProva.getFimNumeracao() > cpr.getCategoriaDaProva().getFimNumeracao())) {
                    super.cj.exibirAviso(cm.getMensagem("aviso_faixa_numeracao_coincidente", catProva.getCategoriaAtleta().getNome(), cpr.getCategoriaDaProva().getCategoriaAtleta().getNome()));
                    return false;
                }
            }
        }
        return true;
    }

    private List<CategoriaDaProva> getCategoriasSelecionadas() {
        List<CategoriaDaProva> categorias = new ArrayList();
        this.tabCategoria.getItems().stream().filter((cpi) -> (cpi.isSelected())).forEachOrdered((cpi) -> {
            categorias.add(cpi.getCategoriaDaProva());
        });
        return categorias;
    }
}
