package com.taurus.racingTiming.pojo;

import com.taurus.racingTiming.entidade.corrida.Prova;
import java.time.LocalDateTime;

/**
 *
 * @author Diego
 */
public class ImportacaoInscricao {

    private Prova prova;
    private LocalDateTime dataImportacao;
    private Long totalImportacao;

    public ImportacaoInscricao(Prova prova, LocalDateTime dataImportacao, Long totalImportacao) {
        this.prova = prova;
        this.dataImportacao = dataImportacao;
        this.totalImportacao = totalImportacao;
    }

    public Prova getProva() {
        return prova;
    }

    public void setProva(Prova prova) {
        this.prova = prova;
    }

    public LocalDateTime getDataImportacao() {
        return dataImportacao;
    }

    public void setDataImportacao(LocalDateTime dataImportacao) {
        this.dataImportacao = dataImportacao;
    }

    public Long getTotalImportacao() {
        return totalImportacao;
    }

    public void setTotalImportacao(Long totalImportacao) {
        this.totalImportacao = totalImportacao;
    }
}
