package com.taurus.racingTiming.entidade;

import java.util.Objects;
import com.taurus.entidade.BaseEntityID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 *
 * @author Diego
 */
@Entity
@Table(name = "CONFIGURACAO")
public class Configuracao extends BaseEntityID<Long> {

    @Column(name = "NOME_CAMERA")
    private String nomeCamera;
    @Column(name = "RESOLUCAO_CAMERA")
    private String resolucaoCamera;

    public String getNomeCamera() {
        return nomeCamera;
    }

    public void setNomeCamera(String nomeCamera) {
        this.nomeCamera = nomeCamera;
    }

    public String getResolucaoCamera() {
        return resolucaoCamera;
    }

    public void setResolucaoCamera(String resolucaoCamera) {
        this.resolucaoCamera = resolucaoCamera;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 71 * hash + Objects.hashCode(this.nomeCamera);
        hash = 71 * hash + Objects.hashCode(this.resolucaoCamera);
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
        final Configuracao other = (Configuracao) obj;
        if (!Objects.equals(this.nomeCamera, other.nomeCamera)) {
            return false;
        }
        if (!Objects.equals(this.resolucaoCamera, other.resolucaoCamera)) {
            return false;
        }
        return true;
    }

}
