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
@Table(name = "ORGANIZACAO_PROVA")
public class OrganizacaoProva extends BaseEntityID<Long> {

    @Column(name = "NOME")
    private String nome;

    @OneToMany(mappedBy = "organizacaoProva")
    private Set<RepresentanteOrganizacaoProva> listaRepresentante;

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Set<RepresentanteOrganizacaoProva> getListaRepresentante() {
        return listaRepresentante;
    }

    public void setListaRepresentante(Set<RepresentanteOrganizacaoProva> listaRepresentante) {
        this.listaRepresentante = listaRepresentante;
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
        final OrganizacaoProva other = (OrganizacaoProva) obj;
        if (!Objects.equals(this.nome, other.nome)) {
            return false;
        }
        return true;
    }
}
