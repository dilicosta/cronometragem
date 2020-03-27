package com.taurus.racingTiming.entidade.corrida;

import com.taurus.entidade.BaseEntityID;
import com.taurus.racingTiming.util.ListaConstantes.GrauDificuldade;
import java.util.Objects;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 *
 * @author Diego
 */
@Entity
@Table(name = "PERCURSO")
public class Percurso extends BaseEntityID<Long> {

    @Column(name = "NOME")
    private String nome;

    @Column(name = "DISTANCIA")
    private Float distancia;

    @Column(name = "GRAU_DIFICULDADE")
    private GrauDificuldade grauDificuldade;

    @Column(name = "NUMERO_VOLTA")
    private Integer numeroVolta;

    @ManyToOne
    @JoinColumn(name = "ID_PROVA")
    private Prova prova;

    @OneToMany(mappedBy = "percurso")
    private Set<CategoriaDaProva> listaCategoriaDaProva;

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Float getDistancia() {
        return distancia;
    }

    public void setDistancia(Float distancia) {
        this.distancia = distancia;
    }

    public GrauDificuldade getGrauDificuldade() {
        return grauDificuldade;
    }

    public void setGrauDificuldade(GrauDificuldade grauDificuldade) {
        this.grauDificuldade = grauDificuldade;
    }

    public Integer getNumeroVolta() {
        return numeroVolta;
    }

    public void setNumeroVolta(Integer numeroVolta) {
        this.numeroVolta = numeroVolta;
    }

    public Prova getProva() {
        return prova;
    }

    public void setProva(Prova prova) {
        this.prova = prova;
    }

    public Set<CategoriaDaProva> getListaCategoriaDaProva() {
        return listaCategoriaDaProva;
    }

    public void setListaCategoriaDaProva(Set<CategoriaDaProva> listaCategoriaDaProva) {
        this.listaCategoriaDaProva = listaCategoriaDaProva;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 17 * hash + Objects.hashCode(this.nome);
        hash = 17 * hash + Objects.hashCode(this.distancia);
        hash = 17 * hash + Objects.hashCode(this.grauDificuldade);
        hash = 17 * hash + Objects.hashCode(this.numeroVolta);
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
        final Percurso other = (Percurso) obj;
        if (!Objects.equals(this.nome, other.nome)) {
            return false;
        }
        if (!Objects.equals(this.distancia, other.distancia)) {
            return false;
        }
        if (this.grauDificuldade != other.grauDificuldade) {
            return false;
        }
        if (!Objects.equals(this.numeroVolta, other.numeroVolta)) {
            return false;
        }
        return true;
    }

}
