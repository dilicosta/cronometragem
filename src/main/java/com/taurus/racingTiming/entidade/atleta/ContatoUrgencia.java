package com.taurus.racingTiming.entidade.atleta;

import com.taurus.entidade.BaseEntityID;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 *
 * @author Diego
 */
@Entity
@Table(name = "CONTATO_URGENCIA")
public class ContatoUrgencia extends BaseEntityID<Long> {

    @Column
    private String nome;
    @Column
    private String parentesco;
    @Column
    private String telefone1;
    @Column
    private String telefone2;

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getParentesco() {
        return parentesco;
    }

    public void setParentesco(String parentesco) {
        this.parentesco = parentesco;
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

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + Objects.hashCode(this.nome);
        hash = 79 * hash + Objects.hashCode(this.parentesco);
        hash = 79 * hash + Objects.hashCode(this.telefone1);
        hash = 79 * hash + Objects.hashCode(this.telefone2);
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
        final ContatoUrgencia other = (ContatoUrgencia) obj;
        if (!Objects.equals(this.nome, other.nome)) {
            return false;
        }
        if (!Objects.equals(this.parentesco, other.parentesco)) {
            return false;
        }
        if (!Objects.equals(this.telefone1, other.telefone1)) {
            return false;
        }
        if (!Objects.equals(this.telefone2, other.telefone2)) {
            return false;
        }
        return true;
    }

}
