package com.taurus.racingTiming.entidade.corrida;

import com.taurus.entidade.BaseEntityID;
import java.time.LocalDateTime;
import java.util.Objects;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 *
 * @author Diego
 */
@Entity
@Table(name = "CRONOMETRAGEM")
public class Cronometragem extends BaseEntityID<Long> {

    @Column(name = "NUMERO_ATLETA")
    private Integer numeroAtleta;

    @Column(name = "HORA_REGISTRO")
    private LocalDateTime horaRegistro;

    @Column
    private Integer volta;

    @Column(name = "TEMPO_VOLTA")
    private Long tempoVolta;

    @Column
    private boolean duvida;

    @Column
    private boolean excluida;

    @OneToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_IMAGEM")
    private CronometragemImagem cronometragemImagem;

    @ManyToOne
    @JoinColumn(name = "ID_ATLETA_INSCRICAO")
    private AtletaInscricao atletaInscricao;

    @ManyToOne
    @JoinColumn(name = "ID_PROVA")
    private Prova prova;

    public LocalDateTime getHoraRegistro() {
        return horaRegistro;
    }

    public void setHoraRegistro(LocalDateTime horaRegistro) {
        this.horaRegistro = horaRegistro;
    }

    public AtletaInscricao getAtletaInscricao() {
        return atletaInscricao;
    }

    public void setAtletaInscricao(AtletaInscricao atletaInscricao) {
        this.atletaInscricao = atletaInscricao;
    }

    public boolean isDuvida() {
        return duvida;
    }

    public void setDuvida(boolean duvida) {
        this.duvida = duvida;
    }

    public Integer getVolta() {
        return volta;
    }

    public void setVolta(Integer volta) {
        this.volta = volta;
    }

    public Integer getNumeroAtleta() {
        return numeroAtleta;
    }

    public void setNumeroAtleta(Integer numeroAtleta) {
        this.numeroAtleta = numeroAtleta;
    }

    public Prova getProva() {
        return prova;
    }

    public void setProva(Prova prova) {
        this.prova = prova;
    }

    public Long getTempoVolta() {
        return tempoVolta;
    }

    public void setTempoVolta(Long tempoVolta) {
        this.tempoVolta = tempoVolta;
    }

    public boolean isExcluida() {
        return excluida;
    }

    public void setExcluida(boolean excluida) {
        this.excluida = excluida;
    }

    public CronometragemImagem getCronometragemImagem() {
        return cronometragemImagem;
    }

    public void setCronometragemImagem(CronometragemImagem cronometragemImagem) {
        this.cronometragemImagem = cronometragemImagem;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.numeroAtleta);
        hash = 97 * hash + Objects.hashCode(this.horaRegistro);
        hash = 97 * hash + Objects.hashCode(this.duvida);
        hash = 97 * hash + Objects.hashCode(this.volta);
        hash = 97 * hash + Objects.hashCode(this.tempoVolta);
        hash = 97 * hash + Objects.hashCode(this.atletaInscricao);
        hash = 97 * hash + Objects.hashCode(this.excluida);
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
        final Cronometragem other = (Cronometragem) obj;
        if (!Objects.equals(this.numeroAtleta, other.numeroAtleta)) {
            return false;
        }
        if (!Objects.equals(this.horaRegistro, other.horaRegistro)) {
            return false;
        }
        if (!Objects.equals(this.duvida, other.duvida)) {
            return false;
        }
        if (!Objects.equals(this.volta, other.volta)) {
            return false;
        }
        if (!Objects.equals(this.excluida, other.excluida)) {
            return false;
        }
        if (!Objects.equals(this.volta, other.tempoVolta)) {
            return false;
        }
        if (!Objects.equals(this.atletaInscricao, other.atletaInscricao)) {
            return false;
        }
        return true;
    }
}
