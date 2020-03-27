package com.taurus.racingTiming.entidade.corrida;

import com.taurus.entidade.BaseEntityID;
import com.taurus.racingTiming.entidade.atleta.Atleta;
import com.taurus.racingTiming.entidade.atleta.ContatoUrgencia;
import com.taurus.racingTiming.entidade.atleta.Responsavel;
import com.taurus.racingTiming.util.ListaConstantes.StatusAtletaCorrida;
import com.taurus.util.FormatarUtil;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 *
 * @author Diego
 */
@Entity
@Table(name = "ATLETA_INSCRICAO")
public class AtletaInscricao extends BaseEntityID<Long> {

    @Column(name = "DATA_INSCRICAO")
    private LocalDateTime dataInscricao;
    @Column(name = "DATA_IMPORTACAO")
    private LocalDateTime dataImportacao;

    @Column(name = "NUMERO_ATLETA")
    private Integer numeroAtleta;

    @Column(name = "EQUIPE")
    private String equipe;

    @Column
    private String telefone1;
    @Column
    private String telefone2;
    @Column
    private String email;
    @Column
    private String profissao;
    @Column
    private String equipamento;
    @Column(name = "CODIGO_FEDERACAO")
    private String codigoFederacao;
    @Column(name = "CODIGO_CBC")
    private String codigoCbc;
    @Column(name = "CODIGO_UCI")
    private String codigoUci;
    @Column(name="STATUS_CORRIDA")
    private StatusAtletaCorrida statusCorrida;
    @Column(name = "MOTIVO_DESCLASSIFICACAO")
    private String motivoDesclassificacao;
    @Column
    private Long tempo;
    @Column(name = "TEMPO_DUPLA")
    private Long tempoDupla;
    @Column(name = "POSICAO_CATEGORIA")
    private Integer posicaoCategoria;
    @Column(name = "POSICAO_GERAL")
    private Integer posicaoGeral;
    @Column(name = "DIFERENCA_CATEGORIA")
    private Long diferencaCategoria;
    @Column(name = "DIFERENCA_GERAL")
    private Long diferencaGeral;
    @Column(name = "NUMERO_VOLTA")
    private Integer numeroVolta;

    @ManyToOne
    @JoinColumn(name = "ID_ATLETA")
    private Atleta atleta;

    @ManyToOne
    @JoinColumn(name = "ID_CATEGORIA")
    private CategoriaDaProva categoria;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "ID_CONTATO_URGENCIA")
    private ContatoUrgencia contatoUrgencia;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "ID_RESPONSAVEL")
    private Responsavel responsavel;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_ENDERECO_ATLETA_INSCRICAO")
    private EnderecoAtletaInscricao enderecoAtletaInscricao;

    @OneToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "ID_DUPLA")
    private AtletaInscricao dupla;

    @OneToMany(mappedBy = "atletaInscricao")
    private Set<Cronometragem> listaCronometragem;

    public AtletaInscricao() {
    }

    private void atualizarInformacoesAtletaInscricao(Atleta atleta) {
        if (atleta != null) {
            this.telefone1 = atleta.getTelefone1();
            this.telefone2 = atleta.getTelefone2();
            this.email = atleta.getEmail();
            this.profissao = atleta.getProfissao();
            this.equipamento = atleta.getEquipamento();
            this.codigoFederacao = atleta.getCodigoFederacao();
            this.codigoCbc = atleta.getCodigoCbc();
            this.codigoUci = atleta.getCodigoUci();
        }
    }

    public LocalDateTime getDataInscricao() {
        return dataInscricao;
    }

    public void setDataInscricao(LocalDateTime data) {
        this.dataInscricao = data;
    }

    public Integer getNumeroAtleta() {
        return numeroAtleta;
    }

    public void setNumeroAtleta(Integer numeroAtleta) {
        this.numeroAtleta = numeroAtleta;
    }

    public String getEquipe() {
        return equipe;
    }

    public void setEquipe(String equipe) {
        this.equipe = equipe;
    }

    public String getTelefone1() {
        return telefone1;
    }

    public void setTelefone1(String telefone1) {
        this.telefone1 = telefone1;
    }

    public String getTelefone2() {
        return telefone2;
    }

    public void setTelefone2(String telefone2) {
        this.telefone2 = telefone2;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProfissao() {
        return profissao;
    }

    public void setProfissao(String profissao) {
        this.profissao = profissao;
    }

    public String getEquipamento() {
        return equipamento;
    }

    public void setEquipamento(String equipamento) {
        this.equipamento = equipamento;
    }

    public String getCodigoFederacao() {
        return codigoFederacao;
    }

    public void setCodigoFederacao(String codigoFederacao) {
        this.codigoFederacao = codigoFederacao;
    }

    public String getCodigoCbc() {
        return codigoCbc;
    }

    public void setCodigoCbc(String codigoCbc) {
        this.codigoCbc = codigoCbc;
    }

    public String getCodigoUci() {
        return codigoUci;
    }

    public void setCodigoUci(String codigoUci) {
        this.codigoUci = codigoUci;
    }

    public EnderecoAtletaInscricao getEnderecoAtletaInscricao() {
        return enderecoAtletaInscricao;
    }

    public void setEnderecoAtletaInscricao(EnderecoAtletaInscricao enderecoAtletaInscricao) {
        this.enderecoAtletaInscricao = enderecoAtletaInscricao;
    }

    public StatusAtletaCorrida getStatusCorrida() {
        return statusCorrida;
    }

    public void setStatusCorrida(StatusAtletaCorrida status) {
        this.statusCorrida = status;
    }

    public String getMotivoDesclassificacao() {
        return motivoDesclassificacao;
    }

    public void setMotivoDesclassificacao(String motivoDesclassificacao) {
        this.motivoDesclassificacao = motivoDesclassificacao;
    }

    public Long getTempo() {
        return tempo;
    }

    public void setTempo(Long tempo) {
        this.tempo = tempo;
    }

    public Long getTempoDupla() {
        return tempoDupla;
    }

    public void setTempoDupla(Long tempoDupla) {
        this.tempoDupla = tempoDupla;
    }

    public Long getDiferencaCategoria() {
        return diferencaCategoria;
    }

    public void setDiferencaCategoria(Long diferencaCategoria) {
        this.diferencaCategoria = diferencaCategoria;
    }

    public Long getDiferencaGeral() {
        return diferencaGeral;
    }

    public void setDiferencaGeral(Long diferencaGeral) {
        this.diferencaGeral = diferencaGeral;
    }

    public Integer getPosicaoCategoria() {
        return posicaoCategoria;
    }

    public void setPosicaoCategoria(Integer posicaoCategoria) {
        this.posicaoCategoria = posicaoCategoria;
    }

    public Integer getPosicaoGeral() {
        return posicaoGeral;
    }

    public void setPosicaoGeral(Integer posicaoGeral) {
        this.posicaoGeral = posicaoGeral;
    }

    public Atleta getAtleta() {
        return atleta;
    }

    public void setAtleta(Atleta atleta) {
        this.atleta = atleta;
        this.atualizarInformacoesAtletaInscricao(atleta);
    }

    public CategoriaDaProva getCategoria() {
        return categoria;
    }

    public void setCategoria(CategoriaDaProva categoria) {
        this.categoria = categoria;
    }

    public ContatoUrgencia getContatoUrgencia() {
        return contatoUrgencia;
    }

    public void setContatoUrgencia(ContatoUrgencia contatoUrgencia) {
        this.contatoUrgencia = contatoUrgencia;
    }

    public Responsavel getResponsavel() {
        return responsavel;
    }

    public void setResponsavel(Responsavel responsavel) {
        this.responsavel = responsavel;
    }

    public AtletaInscricao getDupla() {
        return dupla;
    }

    public void setDupla(AtletaInscricao dupla) {
        this.dupla = dupla;
    }

    public LocalDateTime getDataImportacao() {
        return dataImportacao;
    }

    public void setDataImportacao(LocalDateTime dataImportacao) {
        this.dataImportacao = dataImportacao;
    }

    public Set<Cronometragem> getListaCronometragem() {
        return listaCronometragem;
    }

    public void setListaCronometragem(Set<Cronometragem> listaCronometragem) {
        this.listaCronometragem = listaCronometragem;
    }

    public Integer getNumeroVolta() {
        return numeroVolta;
    }

    public void setNumeroVolta(Integer volta) {
        this.numeroVolta = volta;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 97 * hash + Objects.hashCode(this.dataInscricao);
        hash = 97 * hash + Objects.hashCode(this.numeroAtleta);
        hash = 97 * hash + Objects.hashCode(this.equipe);
        hash = 97 * hash + Objects.hashCode(this.telefone1);
        hash = 97 * hash + Objects.hashCode(this.telefone2);
        hash = 97 * hash + Objects.hashCode(this.email);
        hash = 97 * hash + Objects.hashCode(this.profissao);
        hash = 97 * hash + Objects.hashCode(this.equipamento);
        hash = 97 * hash + Objects.hashCode(this.codigoFederacao);
        hash = 97 * hash + Objects.hashCode(this.codigoCbc);
        hash = 97 * hash + Objects.hashCode(this.codigoUci);
        hash = 97 * hash + Objects.hashCode(this.statusCorrida);
        hash = 97 * hash + Objects.hashCode(this.motivoDesclassificacao);
        hash = 97 * hash + Objects.hashCode(this.tempo);
        hash = 97 * hash + Objects.hashCode(this.tempoDupla);
        hash = 97 * hash + Objects.hashCode(this.diferencaCategoria);
        hash = 97 * hash + Objects.hashCode(this.diferencaGeral);
        hash = 97 * hash + Objects.hashCode(this.posicaoCategoria);
        hash = 97 * hash + Objects.hashCode(this.posicaoGeral);
        hash = 97 * hash + Objects.hashCode(this.numeroVolta);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final AtletaInscricao other = (AtletaInscricao) obj;
        if (!Objects.equals(this.dataInscricao, other.dataInscricao)) {
            return false;
        }
        if (!Objects.equals(this.numeroAtleta, other.numeroAtleta)) {
            return false;
        }
        if (!Objects.equals(this.equipe, other.equipe)) {
            return false;
        }
        if (!Objects.equals(this.telefone1, other.telefone1)) {
            return false;
        }
        if (!Objects.equals(this.telefone2, other.telefone2)) {
            return false;
        }
        if (!Objects.equals(this.email, other.email)) {
            return false;
        }
        if (!Objects.equals(this.profissao, other.profissao)) {
            return false;
        }
        if (!Objects.equals(this.equipamento, other.equipamento)) {
            return false;
        }
        if (!Objects.equals(this.codigoFederacao, other.codigoFederacao)) {
            return false;
        }
        if (!Objects.equals(this.codigoCbc, other.codigoCbc)) {
            return false;
        }
        if (!Objects.equals(this.codigoUci, other.codigoUci)) {
            return false;
        }
        if (!Objects.equals(this.statusCorrida, other.statusCorrida)) {
            return false;
        }
        if (!Objects.equals(this.motivoDesclassificacao, other.motivoDesclassificacao)) {
            return false;
        }
        if (!Objects.equals(this.tempo, other.tempo)) {
            return false;
        }
        if (!Objects.equals(this.tempoDupla, other.tempoDupla)) {
            return false;
        }
        if (!Objects.equals(this.diferencaCategoria, other.diferencaCategoria)) {
            return false;
        }
        if (!Objects.equals(this.diferencaGeral, other.diferencaGeral)) {
            return false;
        }
        if (!Objects.equals(this.posicaoCategoria, other.posicaoCategoria)) {
            return false;
        }
        if (!Objects.equals(this.posicaoGeral, other.posicaoGeral)) {
            return false;
        }
        if (!Objects.equals(this.numeroVolta, other.numeroVolta)) {
            return false;
        }
        return true;
    }

    public String getTempoFormatado() {
        return this.tempo == null ? null : FormatarUtil.formatarTempoMilisegundos(this.tempo);
    }

    public String getTempoFormatadoDiferencaCategoria() {
        return this.diferencaCategoria == null ? null : FormatarUtil.formatarTempoMilisegundos(this.diferencaCategoria);
    }

    public String getTempoFormatadoDiferencaGeral() {
        return this.diferencaGeral == null ? null : FormatarUtil.formatarTempoMilisegundos(this.diferencaGeral);
    }
}
