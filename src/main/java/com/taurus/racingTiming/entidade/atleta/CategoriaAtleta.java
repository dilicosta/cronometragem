package com.taurus.racingTiming.entidade.atleta;

import com.taurus.entidade.BaseEntityID;
import com.taurus.util.ListaConstantesBase.Sexo;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 *
 * @author Diego
 */
@Entity
@Table(name = "CATEGORIA_ATLETA")
public class CategoriaAtleta extends BaseEntityID<Long> {

    @Column(name = "NOME")
    private String nome;

    @Column
    private String descricao;

    @Column
    private Sexo sexo;

    @Column(name = "IDADE_MINIMA")
    private Integer idadeMinima;

    @Column(name = "IDADE_MAXIMA")
    private Integer idadeMaxima;

    @Column(name = "CATEGORIA_DUPLA")
    private boolean categoriaDupla;
    
    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Integer getIdadeMinima() {
        return idadeMinima;
    }

    public void setIdadeMinima(Integer idadeMinima) {
        this.idadeMinima = idadeMinima;
    }

    public Integer getIdadeMaxima() {
        return idadeMaxima;
    }

    public void setIdadeMaxima(Integer idadeMaxima) {
        this.idadeMaxima = idadeMaxima;
    }

    public Sexo getSexo() {
        return sexo;
    }

    public void setSexo(Sexo sexo) {
        this.sexo = sexo;
    }

    public boolean isCategoriaDupla() {
        return categoriaDupla;
    }

    public void setCategoriaDupla(boolean categoriaDupla) {
        this.categoriaDupla = categoriaDupla;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.nome);
        hash = 97 * hash + Objects.hashCode(this.descricao);
        hash = 97 * hash + Objects.hashCode(this.idadeMinima);
        hash = 97 * hash + Objects.hashCode(this.idadeMaxima);
        hash = 97 * hash + Objects.hashCode(this.sexo);
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
        final CategoriaAtleta other = (CategoriaAtleta) obj;
        if (!Objects.equals(this.nome, other.nome)) {
            return false;
        }
        if (!Objects.equals(this.descricao, other.descricao)) {
            return false;
        }
        if (!Objects.equals(this.idadeMinima, other.idadeMinima)) {
            return false;
        }
        if (!Objects.equals(this.idadeMaxima, other.idadeMaxima)) {
            return false;
        }
        if (!Objects.equals(this.sexo, other.sexo)) {
            return false;
        }
        return true;
    }

}
