package com.taurus.racingTiming.entidade.corrida;

import com.taurus.racingTiming.entidade.atleta.CategoriaAtleta;
import com.taurus.entidade.BaseEntityID;
import com.taurus.racingTiming.util.ListaConstantes;
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
@Table(name = "CATEGORIA_DA_PROVA")
public class CategoriaDaProva extends BaseEntityID<Long> {

    @Column(name = "NUMERACAO_AUTOMATICA")
    private ListaConstantes.NumeracaoAutomatica numeracaoAutomatica;

    @Column(name = "DIGITOS_NUMERACAO")
    private Integer digitosNumeracao;

    @Column(name = "INICIO_NUMERACAO")
    Integer inicioNumeracao;
    @Column(name = "FIM_NUMERACAO")
    Integer fimNumeracao;

    @ManyToOne
    @JoinColumn(name = "ID_PROVA")
    private Prova prova;

    @ManyToOne
    @JoinColumn(name = "ID_CATEGORIA_ATLETA")
    private CategoriaAtleta categoriaAtleta;

    @ManyToOne
    @JoinColumn(name = "ID_LARGADA")
    private Largada largada;

    @ManyToOne
    @JoinColumn(name = "ID_PERCURSO")
    private Percurso percurso;

    @OneToMany(mappedBy = "categoria")
    private Set<AtletaInscricao> listaAtletaInscricao;

    public ListaConstantes.NumeracaoAutomatica getNumeracaoAutomatica() {
        return numeracaoAutomatica;
    }

    public void setNumeracaoAutomatica(ListaConstantes.NumeracaoAutomatica numeracaoAutomatica) {
        this.numeracaoAutomatica = numeracaoAutomatica;
    }

    public Integer getDigitosNumeracao() {
        return digitosNumeracao;
    }

    public void setDigitosNumeracao(Integer digitosNumeracao) {
        this.digitosNumeracao = digitosNumeracao;
    }

    public Integer getInicioNumeracao() {
        return inicioNumeracao;
    }

    public void setInicioNumeracao(Integer inicioNumeracao) {
        this.inicioNumeracao = inicioNumeracao;
    }

    public Integer getFimNumeracao() {
        return fimNumeracao;
    }

    public void setFimNumeracao(Integer fimNumeracao) {
        this.fimNumeracao = fimNumeracao;
    }

    public Set<AtletaInscricao> getListaAtletaInscricao() {
        return listaAtletaInscricao;
    }

    public void setListaAtletaInscricao(Set<AtletaInscricao> listaAtletaInscricao) {
        this.listaAtletaInscricao = listaAtletaInscricao;
    }

    public Prova getProva() {
        return prova;
    }

    public void setProva(Prova prova) {
        this.prova = prova;
    }

    public CategoriaAtleta getCategoriaAtleta() {
        return categoriaAtleta;
    }

    public void setCategoriaAtleta(CategoriaAtleta categoriaAtleta) {
        this.categoriaAtleta = categoriaAtleta;
    }

    public Largada getLargada() {
        return largada;
    }

    public void setLargada(Largada largada) {
        this.largada = largada;
    }

    public Percurso getPercurso() {
        return percurso;
    }

    public void setPercurso(Percurso percurso) {
        this.percurso = percurso;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + Objects.hashCode(this.numeracaoAutomatica);
        hash = 53 * hash + Objects.hashCode(this.digitosNumeracao);
        hash = 53 * hash + Objects.hashCode(this.inicioNumeracao);
        hash = 53 * hash + Objects.hashCode(this.fimNumeracao);
        hash = 53 * hash + Objects.hashCode(this.prova);
        hash = 53 * hash + Objects.hashCode(this.categoriaAtleta);
        hash = 53 * hash + Objects.hashCode(this.largada);
        hash = 53 * hash + Objects.hashCode(this.percurso);
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
        final CategoriaDaProva other = (CategoriaDaProva) obj;
        if (!Objects.equals(this.numeracaoAutomatica, other.numeracaoAutomatica)) {
            return false;
        }
        if (!Objects.equals(this.digitosNumeracao, other.digitosNumeracao)) {
            return false;
        }
        if (!Objects.equals(this.inicioNumeracao, other.inicioNumeracao)) {
            return false;
        }
        if (!Objects.equals(this.fimNumeracao, other.fimNumeracao)) {
            return false;
        }
        if (!Objects.equals(this.prova, other.prova)) {
            return false;
        }
        if (!Objects.equals(this.categoriaAtleta, other.categoriaAtleta)) {
            return false;
        }
        if (!Objects.equals(this.largada, other.largada)) {
            return false;
        }
        if (!Objects.equals(this.percurso, other.percurso)) {
            return false;
        }
        return true;
    }

}
