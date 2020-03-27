package com.taurus.racingTiming.entidade.atleta;

import com.taurus.entidade.BaseEntityID;
import com.taurus.racingTiming.entidade.Endereco;
import com.taurus.racingTiming.entidade.corrida.AtletaInscricao;
import com.taurus.racingTiming.util.ListaConstantes.TipoSanguineo;
import com.taurus.util.ListaConstantesBase.Sexo;
import java.time.LocalDate;
import java.util.Objects;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 *
 * @author Diego
 */
@Entity
@Table(name = "ATLETA")
public class Atleta extends BaseEntityID<Long> {

    @Column(name = "NOME")
    private String nome;

    @Column(name = "DATA_NASCIMENTO")
    private LocalDate dataNascimento;

    @Column
    private Sexo sexo;

    @Column
    private String cpf;

    @Column
    private String rg;
    @Column(name = "DATA_RG")
    private LocalDate dataRg;
    @Column(name = "ORGAO_RG")
    private String orgaoRg;

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
    @Column(name = "TIPO_SANGUINEO")
    private TipoSanguineo tipoSanquineo;
    @Column(name = "CODIGO_FEDERACAO")
    private String codigoFederacao;
    @Column(name = "CODIGO_CBC")
    private String codigoCbc;
    @Column(name = "CODIGO_UCI")
    private String codigoUci;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "ID_ENDERECO")
    private Endereco endereco;

    @OneToMany(mappedBy = "atleta")
    private Set<AtletaInscricao> listaAtletaInscricao;

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public LocalDate getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(LocalDate dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getRg() {
        return rg;
    }

    public void setRg(String rg) {
        this.rg = rg;
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

    public TipoSanguineo getTipoSanquineo() {
        return tipoSanquineo;
    }

    public void setTipoSanquineo(TipoSanguineo tipoSanquineo) {
        this.tipoSanquineo = tipoSanquineo;
    }

    public Endereco getEndereco() {
        return endereco;
    }

    public void setEndereco(Endereco endereco) {
        this.endereco = endereco;
    }

    public Sexo getSexo() {
        return sexo;
    }

    public void setSexo(Sexo sexo) {
        this.sexo = sexo;
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

    public LocalDate getDataRg() {
        return dataRg;
    }

    public void setDataRg(LocalDate dataRg) {
        this.dataRg = dataRg;
    }

    public String getOrgaoRg() {
        return orgaoRg;
    }

    public void setOrgaoRg(String orgaoRg) {
        this.orgaoRg = orgaoRg;
    }

    public Set<AtletaInscricao> getListaAtletaInscricao() {
        return listaAtletaInscricao;
    }

    public void setListaAtletaInscricao(Set<AtletaInscricao> listaAtletaInscricao) {
        this.listaAtletaInscricao = listaAtletaInscricao;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 47 * hash + Objects.hashCode(this.nome);
        hash = 47 * hash + Objects.hashCode(this.dataNascimento);
        hash = 47 * hash + Objects.hashCode(this.sexo);
        hash = 47 * hash + Objects.hashCode(this.cpf);
        hash = 47 * hash + Objects.hashCode(this.rg);
        hash = 47 * hash + Objects.hashCode(this.dataRg);
        hash = 47 * hash + Objects.hashCode(this.orgaoRg);
        hash = 47 * hash + Objects.hashCode(this.telefone1);
        hash = 47 * hash + Objects.hashCode(this.telefone2);
        hash = 47 * hash + Objects.hashCode(this.email);
        hash = 47 * hash + Objects.hashCode(this.profissao);
        hash = 47 * hash + Objects.hashCode(this.equipamento);
        hash = 47 * hash + Objects.hashCode(this.tipoSanquineo);
        hash = 47 * hash + Objects.hashCode(this.codigoFederacao);
        hash = 47 * hash + Objects.hashCode(this.codigoCbc);
        hash = 47 * hash + Objects.hashCode(this.codigoUci);
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
        final Atleta other = (Atleta) obj;
        if (!Objects.equals(this.nome, other.nome)) {
            return false;
        }
        if (!Objects.equals(this.cpf, other.cpf)) {
            return false;
        }
        if (!Objects.equals(this.rg, other.rg)) {
            return false;
        }
        if (!Objects.equals(this.dataRg, other.dataRg)) {
            return false;
        }
        if (!Objects.equals(this.orgaoRg, other.orgaoRg)) {
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
        if (!Objects.equals(this.dataNascimento, other.dataNascimento)) {
            return false;
        }
        if (this.sexo != other.sexo) {
            return false;
        }
        if (this.tipoSanquineo != other.tipoSanquineo) {
            return false;
        }
        return true;
    }

}
