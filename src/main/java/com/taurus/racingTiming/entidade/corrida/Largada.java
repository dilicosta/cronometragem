package com.taurus.racingTiming.entidade.corrida;

import com.taurus.entidade.BaseEntityID;
import java.time.LocalDateTime;
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
@Table(name = "LARGADA")
public class Largada extends BaseEntityID<Long> {

    @Column(name = "NOME")
    private String nome;

    @Column(name = "HORA_PREVISTA")
    private LocalDateTime horaPrevista;

    @Column(name = "HORA_INICIO")
    private LocalDateTime horaInicio;
    @ManyToOne
    @JoinColumn(name = "ID_PROVA")
    private Prova prova;
    @OneToMany(mappedBy = "largada")
    private Set<CategoriaDaProva> listaCategoriaDaProva;

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public LocalDateTime getHoraPrevista() {
        return horaPrevista;
    }

    public void setHoraPrevista(LocalDateTime horaPrevista) {
        this.horaPrevista = horaPrevista;
    }

    public LocalDateTime getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(LocalDateTime horaInicio) {
        this.horaInicio = horaInicio;
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
        hash = 83 * hash + Objects.hashCode(this.nome);
        hash = 83 * hash + Objects.hashCode(this.horaPrevista);
        hash = 83 * hash + Objects.hashCode(this.horaInicio);
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
        final Largada other = (Largada) obj;
        if (!Objects.equals(this.nome, other.nome)) {
            return false;
        }
        if (!Objects.equals(this.horaPrevista, other.horaPrevista)) {
            return false;
        }
        if (!Objects.equals(this.horaInicio, other.horaInicio)) {
            return false;
        }
        return true;
    }

}
