package com.taurus.racingTiming.entidade.responsavel;

import com.taurus.entidade.BaseEntityID;
import java.util.Objects;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 *
 * @author Diego
 */
@Entity
@Table(name = "FEDERACAO")
public class Federacao extends BaseEntityID<Long> {

    @Column(name = "NOME")
    private String nome;

    @Column(name = "SIGLA")
    private String sigla;

    @OneToMany(mappedBy = "federacao")
    private Set<RepresentanteFederacao> listaRepresentante;

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getSigla() {
        return sigla;
    }

    public void setSigla(String sigla) {
        this.sigla = sigla;
    }

    public Set<RepresentanteFederacao> getListaRepresentante() {
        return listaRepresentante;
    }

    public void setListaRepresentante(Set<RepresentanteFederacao> listaRepresentante) {
        this.listaRepresentante = listaRepresentante;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.nome);
        hash = 97 * hash + Objects.hashCode(this.sigla);
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
        final Federacao other = (Federacao) obj;
        if (!Objects.equals(this.nome, other.nome)) {
            return false;
        }
        if (!Objects.equals(this.sigla, other.sigla)) {
            return false;
        }
        return true;
    }
}
