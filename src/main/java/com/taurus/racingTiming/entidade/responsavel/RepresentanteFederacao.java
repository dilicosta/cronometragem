package com.taurus.racingTiming.entidade.responsavel;

import com.taurus.entidade.BaseEntityID;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 *
 * @author Diego
 */
@Entity
@Table(name = "REPRESENTANTE_FEDERACAO")
public class RepresentanteFederacao extends BaseEntityID<Long> {

    @Column(name = "NOME")
    private String nome;
    
    @ManyToOne
    @JoinColumn(name = "ID_FEDERACAO")
    private Federacao federacao;

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Federacao getFederacao() {
        return federacao;
    }

    public void setFederacao(Federacao federacao) {
        this.federacao = federacao;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 89 * hash + Objects.hashCode(this.nome);
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
        final RepresentanteFederacao other = (RepresentanteFederacao) obj;
        if (!Objects.equals(this.nome, other.nome)) {
            return false;
        }
        return true;
    }
}
