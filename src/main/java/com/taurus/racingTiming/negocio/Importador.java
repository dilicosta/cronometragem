package com.taurus.racingTiming.negocio;

import com.taurus.exception.AvisoNegocioException;
import com.taurus.exception.NegocioException;
import com.taurus.exception.ValidacaoException;
import com.taurus.racingTiming.entidade.Endereco;
import com.taurus.racingTiming.entidade.atleta.Atleta;
import com.taurus.racingTiming.entidade.atleta.CategoriaAtleta;
import com.taurus.racingTiming.entidade.atleta.ContatoUrgencia;
import com.taurus.racingTiming.entidade.atleta.Responsavel;
import com.taurus.racingTiming.entidade.corrida.AtletaInscricao;
import com.taurus.racingTiming.entidade.corrida.CategoriaDaProva;
import com.taurus.racingTiming.entidade.corrida.Largada;
import com.taurus.racingTiming.entidade.corrida.Percurso;
import com.taurus.racingTiming.entidade.corrida.Prova;
import com.taurus.racingTiming.pojo.CorrespondenciaEntidadeEExcel;
import com.taurus.util.arquivo.LeitorExcelUtil;
import com.taurus.racingTiming.util.ListaConstantes;
import com.taurus.util.ControleMensagem;
import com.taurus.util.arquivo.ByteManager;
import com.taurus.util.FormatarUtil;
import com.taurus.util.GeralUtil;
import com.taurus.util.ListaConstantesBase;
import com.taurus.util.ListaConstantesBase.Sexo;
import com.taurus.util.ValidadorDocumentos;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.validator.GenericValidator;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Classe responsavel por importacao de dados
 *
 * @author Diego
 */
@Component
public class Importador implements IImportador {

    private static final Log LOG = LogFactory.getLog(Importador.class);

    @Autowired
    private IEstagiario estagiario;
    @Autowired
    private ISecretario secretario;

    private ControleMensagem cm = ControleMensagem.getInstance();

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public String importarInscricoesFMCEmExcel(File file, CorrespondenciaEntidadeEExcel correspondencia, Prova prova) throws NegocioException {
        StringBuilder logImportacao = new StringBuilder();

        prova = this.secretario.pesquisarProvaPorId(prova.getId());

        LeitorExcelUtil leitorExcel = this.carregarArquivoExcel(file);

        int totalLinhas = leitorExcel.getTotalLinhas();
        if (totalLinhas < correspondencia.getTotalLinhasCabecalho()) {
            throw new NegocioException("O número de linhas informado para o cabeçalho é menor do que o número total de linhas do arquivo excel.");
        }
        int linha = 0;
        Integer indiceColuna = null;
        LocalDateTime dataImportacao = LocalDateTime.now();
        try {
            for (linha = correspondencia.getTotalLinhasCabecalho(); linha < totalLinhas; linha++) {

                indiceColuna = correspondencia.getColunaCategoria();
                String nomeCategoria = leitorExcel.getCellString(linha, indiceColuna);

                CategoriaDaProva categoriaDaProva = correspondencia.getMapCategoriaAssociada().get(nomeCategoria);

                if (categoriaDaProva == null) {
                    throw new AvisoNegocioException("Importação de inscrição ignorada. Não foi encontrada a categoria da prova associada à categoria " + nomeCategoria);
                }

                Atleta atleta = obterAtletaImportado(leitorExcel, correspondencia, linha);

                Atleta atletaCadastrado = null;
                if (!GenericValidator.isBlankOrNull(atleta.getCpf())) {
                    atletaCadastrado = this.secretario.pesquisarAtletaPorCpf(atleta.getCpf());
                }
                if (atletaCadastrado == null) {
                    atleta = this.secretario.criarNovoAtleta(atleta);
                } else {
                    atleta = this.atualizarAtletaCadastrado(atletaCadastrado, atleta);
                }

                AtletaInscricao atletaInscricaoExistente = this.secretario.pesquisarAtletaInscricao(prova, atleta);
                if (atletaInscricaoExistente != null) {
                    this.adicionarLogImportacao(logImportacao, linha, indiceColuna, cm.getMensagem("aviso_atleta_inscrito", atletaInscricaoExistente.getAtleta().getNome(), atletaInscricaoExistente.getCategoria().getProva().getNome(), atletaInscricaoExistente.getCategoria().getCategoriaAtleta().getNome()));
                } else {
                    Responsavel responsavel = this.obterResponsavelImportado(leitorExcel, correspondencia, linha);
                    ContatoUrgencia contatoUrgencia = this.obterContatoUrgenciaImportado(leitorExcel, correspondencia, linha);

                    LocalDateTime dataInscricaoTempo = null;
                    indiceColuna = correspondencia.getColunaDataInscricao();
                    if (indiceColuna != null) {
                        LocalDate dataInscricao;
                        if (GenericValidator.isBlankOrNull(correspondencia.getDtInscricaoPattern())) {
                            dataInscricao = leitorExcel.getCellLocalDate(linha, indiceColuna);
                        } else {
                            dataInscricao = leitorExcel.getCellLocalDate(linha, indiceColuna, correspondencia.getDtInscricaoPattern());
                        }

                        if (dataInscricao != null) {
                            dataInscricaoTempo = GeralUtil.adicionarTempoNaData(dataInscricao, "00:00");
                        }
                    }
                    AtletaInscricao atletaInscricao = new AtletaInscricao();
                    atletaInscricao.setAtleta(atleta);
                    atletaInscricao.setCategoria(categoriaDaProva);
                    atletaInscricao.setResponsavel(responsavel);
                    atletaInscricao.setContatoUrgencia(contatoUrgencia);
                    atletaInscricao.setDataInscricao(dataInscricaoTempo != null ? dataInscricaoTempo : dataImportacao);
                    atletaInscricao.setDataImportacao(dataImportacao);

                    if (correspondencia.getColunaNumeroAtleta() != null) {
                        Integer numeroAtleta = leitorExcel.getCellInteger(linha, correspondencia.getColunaNumeroAtleta());
                        /* if (numeroAtleta.contains(".")) {
                            numeroAtleta = numeroAtleta.substring(0, numeroAtleta.indexOf("."));
                        }*/
                        atletaInscricao.setNumeroAtleta(numeroAtleta);
                    }
                    if (correspondencia.getColunaEquipeAtleta() != null) {
                        String equipe = leitorExcel.getCellString(linha, correspondencia.getColunaEquipeAtleta());
                        if (!GenericValidator.isBlankOrNull(equipe)) {
                            atletaInscricao.setEquipe(GeralUtil.getTamanhoMaximo(equipe, 40));
                        }
                    }
                    this.secretario.criarNovoAtletaInscricao(atletaInscricao);
                    LOG.info("Inscrição Importada: " + atletaInscricao.getNumeroAtleta() + " " + atletaInscricao.getAtleta().getNome() + " " + FormatarUtil.localDateToString(atletaInscricao.getAtleta().getDataNascimento()) + " " + atletaInscricao.getCategoria().getCategoriaAtleta().getNome());
                }
            }
        } catch (Exception ex) {
            LOG.error("", ex);
            this.adicionarLogImportacao(logImportacao, linha, indiceColuna, ex.getMessage());
            throw new NegocioException("Erro na importação de aquivo excel.", ex);
        } finally {
            try {
                leitorExcel.fecharArquivo();
            } catch (IOException ex) {
                throw new NegocioException("Erro ao fechar o arquivo de excel.", ex);
            }
            try {
                ByteManager.writeFile("/Taurus/logs", "Importacao.txt", logImportacao.toString().getBytes());
            } catch (IOException ex) {
                LOG.error("Erro na gravação do arquivo de importação", ex);
            }
        }

        return logImportacao.toString();
    }

    private CategoriaDaProva criarNovaCategoriaImportada(Prova prova, String nomeCategoria, String descricaoCategoria) throws NegocioException {
        Sexo sexo = nomeCategoria.toLowerCase().contains("feminino") ? Sexo.FEMININO : ListaConstantesBase.Sexo.MASCULINO;
        CategoriaAtleta categoria = new CategoriaAtleta();
        categoria.setNome(nomeCategoria);
        categoria.setDescricao(descricaoCategoria);
        categoria.setSexo(sexo);
        categoria = this.estagiario.criarNovoCategoriaAtleta(categoria);

        CategoriaDaProva cdp = new CategoriaDaProva();
        cdp.setProva(prova);
        cdp.setCategoriaAtleta(categoria);
        cdp.setLargada(this.obterLargada(prova));
        cdp.setPercurso(this.obterPercurso(prova));

        return this.secretario.criarNovoCategoriaDaProva(cdp);
    }

    private Atleta obterAtletaImportado(LeitorExcelUtil leitorExcel, CorrespondenciaEntidadeEExcel correspondencia, int linha) throws AvisoNegocioException {
        Atleta atleta = new Atleta();
        Integer indiceColuna = null;
        try {
            indiceColuna = correspondencia.getColunaNomeAtleta();
            String nome = leitorExcel.getCellString(linha, indiceColuna);
            if (GenericValidator.isBlankOrNull(nome)) {
                throw new AvisoNegocioException("Importação de inscrição ignorada. Nome do atleta em branco ou nulo");
            }
            atleta.setNome(FormatarUtil.iniciaisMaiusculas(nome));

            indiceColuna = correspondencia.getColunaDataNascimentoAtleta();
            LocalDate dataNascimento;
            // Data em formato data no excel
            if (GenericValidator.isBlankOrNull(correspondencia.getDtNascimentoAtletaPattern())) {
                dataNascimento = leitorExcel.getCellLocalDate(linha, indiceColuna);
            } else {
                dataNascimento = leitorExcel.getCellLocalDate(linha, indiceColuna, correspondencia.getDtNascimentoAtletaPattern());
            }
            if (dataNascimento == null) {
                throw new AvisoNegocioException("Importação de inscrição ignorada. Data de nascimento em branco ou inválida: " + leitorExcel.getCellString(linha, indiceColuna));
            }
            atleta.setDataNascimento(dataNascimento);

            indiceColuna = correspondencia.getColunaSexoAtleta();
            String sexoStr = leitorExcel.getCellString(linha, indiceColuna);
            if (GenericValidator.isBlankOrNull(sexoStr)) {
                throw new AvisoNegocioException("Importação de inscrição ignorada. Sexo do atleta em branco ou nulo");
            }
            Sexo sexo = null;
            String sexoStrL = sexoStr.toLowerCase();
            if (sexoStrL.equals("feminino") || sexoStrL.equals("f")) {
                sexo = Sexo.FEMININO;
            } else if (sexoStrL.equals("masculino") || sexoStrL.equals("m")) {
                sexo = Sexo.MASCULINO;
            }
            if (sexo == null) {
                throw new AvisoNegocioException("Importação de inscrição ignorada. Sexo do atleta não foi identificado: " + sexoStr);
            }
            atleta.setSexo(sexo);

            indiceColuna = correspondencia.getColunaCpfAtleta();
            if (indiceColuna != null) {
                String cpf = leitorExcel.getCellString(linha, indiceColuna);
                if (!GenericValidator.isBlankOrNull(cpf) && !ValidadorDocumentos.validarCPF(cpf)) {
                    throw new AvisoNegocioException("Importação de inscrição ignorada. CPF do Atleta inválido: " + cpf);
                }
                atleta.setCpf(cpf);
            }

            indiceColuna = correspondencia.getColunaRgAtleta();
            if (indiceColuna != null) {
                String rg = leitorExcel.getCellString(linha, indiceColuna);
                if (!GenericValidator.isBlankOrNull(rg)) {
                    atleta.setRg(GeralUtil.getTamanhoMaximo(rg, 20));
                }
            }

            indiceColuna = correspondencia.getColunaDataRgAtleta();
            if (indiceColuna != null) {
                LocalDate dataRgAtleta;
                if (GenericValidator.isBlankOrNull(correspondencia.getDtRgAtletaPattern())) {
                    dataRgAtleta = leitorExcel.getCellLocalDate(linha, indiceColuna);
                } else {
                    dataRgAtleta = leitorExcel.getCellLocalDate(linha, indiceColuna, correspondencia.getDtRgAtletaPattern());
                }
                atleta.setDataRg(dataRgAtleta);
            }

            indiceColuna = correspondencia.getColunaOrgaoRgAtleta();
            if (indiceColuna != null) {
                String orgaoRg = leitorExcel.getCellString(linha, indiceColuna);
                if (!GenericValidator.isBlankOrNull(orgaoRg)) {
                    atleta.setOrgaoRg(GeralUtil.getTamanhoMaximo(orgaoRg, 20));
                }
            }

            indiceColuna = correspondencia.getColunaTipoSaguineoAtleta();
            if (indiceColuna != null) {
                String tipoSangueStr = leitorExcel.getCellString(linha, indiceColuna);
                if (!GenericValidator.isBlankOrNull(tipoSangueStr)) {
                    tipoSangueStr = tipoSangueStr.replaceAll(" ", "").toUpperCase();
                    for (ListaConstantes.TipoSanguineo tp : ListaConstantes.TipoSanguineo.values()) {
                        if (tp.getNome().equals(tipoSangueStr)) {
                            atleta.setTipoSanquineo(tp);
                        }
                    }
                }
            }

            indiceColuna = correspondencia.getColunaTelefone1Atleta();
            if (indiceColuna != null) {
                String fone1 = leitorExcel.getCellString(linha, indiceColuna);
                fone1 = FormatarUtil.removerMascaraNumero(fone1);
                atleta.setTelefone1(GeralUtil.getTamanhoMaximo(fone1, 11));
            }

            indiceColuna = correspondencia.getColunaTelefone2Atleta();
            if (indiceColuna != null) {
                String fone2 = leitorExcel.getCellString(linha, indiceColuna);
                fone2 = FormatarUtil.removerMascaraNumero(fone2);
                atleta.setTelefone2(GeralUtil.getTamanhoMaximo(fone2, 11));
            }

            indiceColuna = correspondencia.getColunaEmailAtleta();
            if (indiceColuna != null) {
                String email = leitorExcel.getCellString(linha, indiceColuna);
                if (!GenericValidator.isBlankOrNull(email)) {
                    atleta.setEmail(GeralUtil.getTamanhoMaximo(email, 50));
                }
            }

            indiceColuna = correspondencia.getColunaEquipamentoAtleta();
            if (indiceColuna != null) {
                String equipamento = leitorExcel.getCellString(linha, indiceColuna);
                atleta.setEquipamento(equipamento);
            }

            indiceColuna = correspondencia.getColunaProfissaoAtleta();
            if (indiceColuna != null) {
                String profissao = leitorExcel.getCellString(linha, indiceColuna);
                atleta.setProfissao(profissao);
            }

            indiceColuna = correspondencia.getColunaCodigoFederacaoAtleta();
            if (indiceColuna != null) {
                String codFed = leitorExcel.getCellString(linha, indiceColuna);
                atleta.setCodigoFederacao(codFed);
            }

            indiceColuna = correspondencia.getColunaCodigoCbcAtleta();
            if (indiceColuna != null) {
                String codCbc = leitorExcel.getCellString(linha, indiceColuna);
                atleta.setCodigoCbc(codCbc);
            }

            indiceColuna = correspondencia.getColunaCodigoUciAtleta();
            if (indiceColuna != null) {
                String codUci = leitorExcel.getCellString(linha, indiceColuna);
                atleta.setCodigoUci(codUci);
            }

            Endereco endereco = new Endereco();

            indiceColuna = correspondencia.getColunaLogradouroAtleta();
            if (indiceColuna != null) {
                String logradouro = leitorExcel.getCellString(linha, indiceColuna);
                endereco.setLogradouro(FormatarUtil.iniciaisMaiusculas(logradouro));
            }

            indiceColuna = correspondencia.getColunaNumeroLogradouroAtleta();
            if (indiceColuna != null) {
                Integer numero = leitorExcel.getCellInteger(linha, indiceColuna);
                endereco.setNumero(numero);
            }

            indiceColuna = correspondencia.getColunaComplementoEnderecoAtleta();
            if (indiceColuna != null) {
                String complemento = leitorExcel.getCellString(linha, indiceColuna);
                endereco.setComplemento(complemento);
            }

            indiceColuna = correspondencia.getColunaBairroAtleta();
            if (indiceColuna != null) {
                String bairro = leitorExcel.getCellString(linha, indiceColuna);
                endereco.setBairro(FormatarUtil.iniciaisMaiusculas(bairro));
            }

            indiceColuna = correspondencia.getColunaCidadeAtleta();
            if (indiceColuna != null) {
                String cidade = leitorExcel.getCellString(linha, indiceColuna);
                endereco.setCidade(FormatarUtil.iniciaisMaiusculas(cidade));
            }

            indiceColuna = correspondencia.getColunaUfAtleta();
            if (indiceColuna != null) {
                String uf = leitorExcel.getCellString(linha, indiceColuna);
                if (!GenericValidator.isBlankOrNull(uf)) {
                    try {
                        endereco.setUf(ListaConstantesBase.EstadoBrasil.getEstado(uf).getSigla());
                    } catch (Exception ex) {
                        throw new AvisoNegocioException("Importação de inscrição ignorada. UF inválido: " + uf);

                    }
                }
            }

            indiceColuna = correspondencia.getColunaCepAtleta();
            if (indiceColuna != null) {
                String cep = leitorExcel.getCellString(linha, indiceColuna);
                endereco.setCep(GeralUtil.getTamanhoMaximo(FormatarUtil.removerMascaraNumero(cep), 8));
            }

            atleta.setEndereco(endereco);

        } catch (Exception ex) {
            throw new AvisoNegocioException("Erro na leitura do arquivo de importação: linha[" + (linha + 1) + "] coluna[" + (indiceColuna + 1) + "]", ex);
        }
        return atleta;
    }

    private Atleta atualizarAtletaCadastrado(Atleta atletaCadastrado, Atleta atleta) throws NegocioException {
        atletaCadastrado.setNome(atleta.getNome());

        if (atleta.getDataNascimento() != null) {
            atletaCadastrado.setDataNascimento(atleta.getDataNascimento());
        }
        if (atleta.getSexo() != null) {
            atletaCadastrado.setSexo(atleta.getSexo());
        }
        if (!GenericValidator.isBlankOrNull(atleta.getRg())) {
            atletaCadastrado.setRg(atleta.getRg());
        }
        if (atleta.getDataRg() != null) {
            atletaCadastrado.setDataRg(atleta.getDataRg());
        }
        if (!GenericValidator.isBlankOrNull(atleta.getOrgaoRg())) {
            atletaCadastrado.setOrgaoRg(atleta.getOrgaoRg());
        }
        if (atleta.getTipoSanquineo() != null) {
            atletaCadastrado.setTipoSanquineo(atleta.getTipoSanquineo());
        }
        if (!GenericValidator.isBlankOrNull(atleta.getTelefone1())) {
            atletaCadastrado.setTelefone1(atleta.getTelefone1());
        }
        if (!GenericValidator.isBlankOrNull(atleta.getTelefone2())) {
            atletaCadastrado.setTelefone2(atleta.getTelefone2());
        }
        if (!GenericValidator.isBlankOrNull(atleta.getEmail())) {
            atletaCadastrado.setEmail(atleta.getEmail());
        }
        if (!GenericValidator.isBlankOrNull(atleta.getProfissao())) {
            atletaCadastrado.setProfissao(atleta.getProfissao());
        }
        if (!GenericValidator.isBlankOrNull(atleta.getEquipamento())) {
            atletaCadastrado.setEquipamento(atleta.getEquipamento());
        }
        if (!GenericValidator.isBlankOrNull(atleta.getCodigoFederacao())) {
            atletaCadastrado.setCodigoFederacao(atleta.getEquipamento());
        }
        if (!GenericValidator.isBlankOrNull(atleta.getCodigoCbc())) {
            atletaCadastrado.setCodigoCbc(atleta.getCodigoCbc());
        }
        if (!GenericValidator.isBlankOrNull(atleta.getCodigoUci())) {
            atletaCadastrado.setCodigoUci(atleta.getCodigoUci());
        }

        if (!GenericValidator.isBlankOrNull(atleta.getEndereco().getLogradouro())) {
            atletaCadastrado.getEndereco().setLogradouro(atleta.getEndereco().getLogradouro());
        }
        if (atleta.getEndereco().getNumero() != null) {
            atletaCadastrado.getEndereco().setNumero(atleta.getEndereco().getNumero());
        }
        if (!GenericValidator.isBlankOrNull(atleta.getEndereco().getComplemento())) {
            atletaCadastrado.getEndereco().setComplemento(atleta.getEndereco().getComplemento());
        }
        if (!GenericValidator.isBlankOrNull(atleta.getEndereco().getBairro())) {
            atletaCadastrado.getEndereco().setBairro(atleta.getEndereco().getBairro());
        }
        if (!GenericValidator.isBlankOrNull(atleta.getEndereco().getCidade())) {
            atletaCadastrado.getEndereco().setCidade(atleta.getEndereco().getCidade());
        }
        if (!GenericValidator.isBlankOrNull(atleta.getEndereco().getUf())) {
            atletaCadastrado.getEndereco().setUf(atleta.getEndereco().getUf());
        }
        if (!GenericValidator.isBlankOrNull(atleta.getEndereco().getCep())) {
            atletaCadastrado.getEndereco().setCep(atleta.getEndereco().getCep());
        }

        return this.secretario.atualizarAtleta(atletaCadastrado);
    }

    private void adicionarLogImportacao(StringBuilder logImportacao, int linha, Integer coluna, String mensagem) {
        logImportacao.append("\r\nImportação de inscrição ignorada. Linha[").append(linha + 1)
                .append("] Coluna[").append(coluna).append("]")
                .append("\r\n").append(mensagem);
    }

    private Largada obterLargada(Prova prova) {
        return prova.getListaLargada().iterator().next();
    }

    private Percurso obterPercurso(Prova prova) {
        return prova.getListaPercurso().iterator().next();
    }

    @Override
    public List<List<String>> carregarXLinhasExcell(File file, int totalLinhas) throws NegocioException {
        if (file != null && file.exists() && totalLinhas > 0) {

            List<List<String>> linhas = new ArrayList<>();

            LeitorExcelUtil leitorExcelUtil = this.carregarArquivoExcel(file);
            Integer totalLinhasArquivo = leitorExcelUtil.getTotalLinhas();
            int totalEfetivo = totalLinhas < totalLinhasArquivo ? totalLinhas : totalLinhasArquivo;

            for (int linha = 0; linha < totalEfetivo; linha++) {
                List<String> colunas = new ArrayList<>();
                Integer totalColunas = leitorExcelUtil.getTotalColunas(linha);
                if (totalColunas != null) {
                    for (int coluna = 0; coluna < totalColunas; coluna++) {
                        colunas.add(leitorExcelUtil.getCellString(linha, coluna));
                    }
                }
                linhas.add(colunas);
            }
            try {
                leitorExcelUtil.fecharArquivo();
            } catch (IOException ex) {
                throw new NegocioException("Erro ao fechar o arquivo de excel.", ex);
            }
            return linhas;
        } else {
            return new ArrayList();
        }
    }

    private LeitorExcelUtil carregarArquivoExcel(File file) throws NegocioException {
        try {
            return new LeitorExcelUtil(file);
        } catch (IOException | InvalidFormatException ex) {
            LOG.error("Erro ao carregar o arquivo excel: " + file.getAbsolutePath(), ex);
            throw new NegocioException(ex);
        }
    }

    private Responsavel obterResponsavelImportado(LeitorExcelUtil leitorExcel, CorrespondenciaEntidadeEExcel correspondencia, int linha) {
        return null;
    }

    private ContatoUrgencia obterContatoUrgenciaImportado(LeitorExcelUtil leitorExcel, CorrespondenciaEntidadeEExcel correspondencia, int linha) throws AvisoNegocioException {
        Integer indiceColuna = null;
        try {
            ContatoUrgencia contatoUrgencia = new ContatoUrgencia();

            indiceColuna = correspondencia.getColunaNomeContatoUrgencia();
            if (indiceColuna != null) {
                String nome = leitorExcel.getCellString(linha, indiceColuna);
                contatoUrgencia.setNome(FormatarUtil.iniciaisMaiusculas(nome));
            }

            indiceColuna = correspondencia.getColunaParentescoContatoUrgencia();
            if (indiceColuna != null) {
                String parentesco = leitorExcel.getCellString(linha, indiceColuna);
                contatoUrgencia.setParentesco(parentesco.toLowerCase());
            }

            indiceColuna = correspondencia.getColunaTelefone1ContatoUrgencia();
            if (indiceColuna != null) {
                String fone1 = leitorExcel.getCellString(linha, indiceColuna);
                fone1 = FormatarUtil.removerMascaraNumero(fone1);
                contatoUrgencia.setTelefone1(GeralUtil.getTamanhoMaximo(fone1, 11));
            }

            indiceColuna = correspondencia.getColunaTelefone2ContatoUrgencia();
            if (indiceColuna != null) {
                String fone2 = leitorExcel.getCellString(linha, indiceColuna);
                fone2 = FormatarUtil.removerMascaraNumero(fone2);
                contatoUrgencia.setTelefone2(GeralUtil.getTamanhoMaximo(fone2, 11));
            }

            return GenericValidator.isBlankOrNull(contatoUrgencia.getNome()) ? null : contatoUrgencia;
        } catch (Exception ex) {
            throw new AvisoNegocioException("Erro na leitura do arquivo de importação: linha[" + (linha + 1) + "] coluna[" + (indiceColuna + 1) + "]", ex);
        }
    }

    @Override
    public List<String> verificarTipo(File file, int numeroLinha) throws NegocioException, AvisoNegocioException {
        if (file != null && file.exists() && numeroLinha > 0) {

            List<String> tipos = new ArrayList<>();

            LeitorExcelUtil leitorExcelUtil = this.carregarArquivoExcel(file);
            Integer totalLinhasArquivo = leitorExcelUtil.getTotalLinhas();
            if (numeroLinha > totalLinhasArquivo) {
                throw new AvisoNegocioException("O número da linha que se quer verificar é maior que o número de linhas do arquivo xls");
            }

            Integer totalColunas = leitorExcelUtil.getTotalColunas(numeroLinha);
            if (totalColunas != null) {
                for (int coluna = 0; coluna < totalColunas; coluna++) {
                    tipos.add(leitorExcelUtil.getCellTipo(numeroLinha, coluna));
                }
            }
            try {
                leitorExcelUtil.fecharArquivo();
            } catch (IOException ex) {
                throw new NegocioException("Erro ao fechar o arquivo de excel.", ex);
            }
            return tipos;
        } else {
            return new ArrayList();
        }
    }

    @Override
    public List<String> carregarCategoriaXls(File file, Integer indiceColunaCategoriaXls, Integer linhasCabecalho) throws NegocioException {
        try {
            LeitorExcelUtil leitorExcel = this.carregarArquivoExcel(file);

            List<String> categorias;
            try {
                categorias = leitorExcel.getValoresColuna(linhasCabecalho, indiceColunaCategoriaXls);
            } catch (ValidacaoException ex) {
                throw new NegocioException(ex);
            }
            List<String> categoriasSemRepeticao = new ArrayList();
            categorias.stream().filter((nomeCategoria) -> (!categoriasSemRepeticao.contains(nomeCategoria))).forEachOrdered((nomeCategoria) -> {
                categoriasSemRepeticao.add(nomeCategoria);
            });
            leitorExcel.fecharArquivo();
            return categoriasSemRepeticao;
        } catch (IOException ex) {
            throw new NegocioException(ex);
        }
    }
}
