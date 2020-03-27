package com.taurus.racingTiming.entidade.corrida;

import com.taurus.entidade.BaseEntityID;
import com.taurus.racingTiming.entidade.Endereco;
import com.taurus.racingTiming.entidade.responsavel.RepresentanteFederacao;
import com.taurus.racingTiming.entidade.responsavel.RepresentanteOrganizacaoProva;
import com.taurus.racingTiming.util.ListaConstantes;
import com.taurus.racingTiming.util.ListaConstantes.StatusProva;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.Table;

/**
 *
 * @author Diego
 */
@Entity
@Table(name = "PROVA")
public class Prova extends BaseEntityID<Long> {

    @Column(name = "NOME")
    private String nome;

    @Column(name = "DATA")
    private LocalDate data;

    @Column(name = "STATUS")
    private StatusProva status;

    @Column(name = "MOTIVO_PENDENCIA")
    private String motivoPendencia;
    
    @Column
    private boolean demo;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "ID_ENDERECO")
    private Endereco endereco;

    @ManyToMany
    @JoinTable(name = "PROVA_HAS_REPRESENTANTE_FEDERACAO",
            joinColumns = {
                @JoinColumn(name = "ID_PROVA")},
            inverseJoinColumns = {
                @JoinColumn(name = "ID_REPRESENTANTE_FEDERACAO")})
    private Set<RepresentanteFederacao> listaRepresentanteFederacao;

    @ManyToMany
    @JoinTable(name = "PROVA_HAS_REPRESENTANTE_ORGANIZACAO_PROVA", joinColumns = {
        @JoinColumn(name = "ID_PROVA")}, inverseJoinColumns = {
        @JoinColumn(name = "ID_REPRESENTANTE_ORGANIZACAO_PROVA")})
    private Set<RepresentanteOrganizacaoProva> listaRepresentanteOrganizacao;

    @OneToMany(mappedBy = "prova", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Percurso> listaPercurso;

    @OneToMany(mappedBy = "prova", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Largada> listaLargada;

    @OneToMany(mappedBy = "prova", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("categoriaAtleta")
    private Set<CategoriaDaProva> listaCategoriaDaProva;

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public StatusProva getStatus() {
        return status;
    }

    public void setStatus(StatusProva status) {
        this.status = status;
    }

    public Endereco getEndereco() {
        return endereco;
    }

    public void setEndereco(Endereco endereco) {
        this.endereco = endereco;
    }

    public Set<RepresentanteFederacao> getListaRepresentanteFederacao() {
        return listaRepresentanteFederacao;
    }

    public void setListaRepresentanteFederacao(Set<RepresentanteFederacao> listaRepresentanteFederacao) {
        this.listaRepresentanteFederacao = listaRepresentanteFederacao;
    }

    public Set<RepresentanteOrganizacaoProva> getListaRepresentanteOrganizacao() {
        return listaRepresentanteOrganizacao;
    }

    public void setListaRepresentanteOrganizacao(Set<RepresentanteOrganizacaoProva> listaRepresentanteOrganizacao) {
        this.listaRepresentanteOrganizacao = listaRepresentanteOrganizacao;
    }

    public Set<Percurso> getListaPercurso() {
        return listaPercurso;
    }

    public void setListaPercurso(Set<Percurso> listaPercurso) {
        this.listaPercurso = listaPercurso;
    }

    public Set<Largada> getListaLargada() {
        return listaLargada;
    }

    public void setListaLargada(Set<Largada> listaLargada) {
        this.listaLargada = listaLargada;
    }

    public Set<CategoriaDaProva> getListaCategoriaDaProva() {
        return listaCategoriaDaProva;
    }

    public void setListaCategoriaDaProva(Set<CategoriaDaProva> listaCategoriaDaProva) {
        this.listaCategoriaDaProva = listaCategoriaDaProva;
    }

    public String getMotivoPendencia() {
        return motivoPendencia;
    }

    public void setMotivoPendencia(String motivoPendencia) {
        this.motivoPendencia = motivoPendencia;
    }

    public boolean isDemo() {
        return demo;
    }

    public void setDemo(boolean demo) {
        this.demo = demo;
    }

    
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + Objects.hashCode(this.nome);
        hash = 59 * hash + Objects.hashCode(this.data);
        hash = 59 * hash + Objects.hashCode(this.status);
        hash = 59 * hash + Objects.hashCode(this.motivoPendencia);
        hash = 59 * hash + Objects.hashCode(this.demo);
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
        final Prova other = (Prova) obj;
        if (!Objects.equals(this.nome, other.nome)) {
            return false;
        }
        if (!Objects.equals(this.data, other.data)) {
            return false;
        }
        if (!Objects.equals(this.status, other.status)) {
            return false;
        }
        if (!Objects.equals(this.motivoPendencia, other.motivoPendencia)) {
            return false;
        }
        if (!Objects.equals(this.demo, other.demo)) {
            return false;
        }
        return true;
    }

    public List<String> getListaMotivoPendencia() {
        List<String> pendencias = new ArrayList();
        if (this.motivoPendencia != null) {
            pendencias.addAll(Arrays.asList(this.motivoPendencia.split("\\|")));
            pendencias.remove("");
        }
        return pendencias;
    }

    public void setListaMotivoPendencia(Set<ListaConstantes.PendenciaProvaCrono> pendencias) {
        StringBuilder pendenciaCronometragem = new StringBuilder();
        pendencias.forEach((pendencia) -> {
            pendenciaCronometragem.append(pendencia.getDescricao()).append("|");
        });
        this.motivoPendencia = pendenciaCronometragem.length() == 0 ? null : pendenciaCronometragem.toString();
    }

    public void setListaMotivoPendencia(List<ListaConstantes.PendenciaInscricao> pendencias) {
        StringBuilder pendenciaProva = new StringBuilder();
        pendencias.forEach((pendencia) -> {
            pendenciaProva.append(pendencia.getDescricao()).append("|");
        });
        this.motivoPendencia = pendenciaProva.length() == 0 ? null : pendenciaProva.toString();
    }

}
