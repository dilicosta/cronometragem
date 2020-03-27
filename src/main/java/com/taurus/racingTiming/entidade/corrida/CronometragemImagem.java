package com.taurus.racingTiming.entidade.corrida;

import com.taurus.entidade.BaseEntityID;
import java.util.Arrays;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 *
 * @author Diego
 */
@Entity
@Table(name = "CRONOMETRAGEM_IMAGEM")
public class CronometragemImagem extends BaseEntityID<Long> {

    @Column(name = "imagem_byte")
    private byte[] imagem;

    public CronometragemImagem() {
    }

    public CronometragemImagem(byte[] imagem) {
        this.imagem = imagem;
    }

    public byte[] getImagem() {
        return imagem;
    }

    public void setImagem(byte[] imagem) {
        this.imagem = imagem;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 37 * hash + Arrays.hashCode(this.imagem);
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
        final CronometragemImagem other = (CronometragemImagem) obj;
        if (!Arrays.equals(this.imagem, other.imagem)) {
            return false;
        }
        return true;
    }
}
