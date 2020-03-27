package com.taurus.racingTiming.negocio.comparador;

import com.taurus.racingTiming.entidade.corrida.AtletaInscricao;
import java.util.Objects;

/**
 * Esta classe foi implementada exclusimante para ordenacao de classificacao
 * temporaria na tela de cronometragem e tela de resultados publica.
 *
 * Seu metodo "equals" eh mais simplificado considerando apenas o numero do
 * atleta e data de inscricao, assim, um atleta que passou na segunda volta eh
 * considerado o mesmo que passou na volta anterior na chave de mapeamento do
 * treeMap posicionando a odenacao segundo o metodo "compareTo" da super classe.
 *
 * @author Diego
 */
public class AtletaInscricaoComparadorClassificacao implements Comparable<AtletaInscricaoComparadorClassificacao> {

    private Integer numeroAtleta;
    private Integer numeroVolta;
    private Long tempo;
    private boolean dupla;
    private Long tempoDupla;

    public AtletaInscricaoComparadorClassificacao(AtletaInscricao atletaInscricao) {
        this.numeroAtleta = atletaInscricao.getNumeroAtleta();
        this.numeroVolta = atletaInscricao.getNumeroVolta();
        this.tempo = atletaInscricao.getTempo();
        this.dupla = atletaInscricao.getCategoria().getCategoriaAtleta().isCategoriaDupla();
        this.tempoDupla = atletaInscricao.getTempoDupla();
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 71 * hash + Objects.hashCode(this.numeroAtleta);
        hash = 71 * hash + Objects.hashCode(this.numeroVolta);
        hash = 71 * hash + Objects.hashCode(this.tempo);
        hash = 71 * hash + (this.dupla ? 1 : 0);
        hash = 71 * hash + Objects.hashCode(this.tempoDupla);
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
        final AtletaInscricaoComparadorClassificacao other = (AtletaInscricaoComparadorClassificacao) obj;
        if (this.dupla != other.dupla) {
            return false;
        }
        if (!Objects.equals(this.numeroAtleta, other.numeroAtleta)) {
            return false;
        }
//        if (!Objects.equals(this.numeroVolta, other.numeroVolta)) {
//            return false;
//        }
//        if (!Objects.equals(this.tempo, other.tempo)) {
//            return false;
//        }
//        if (!Objects.equals(this.tempoDupla, other.tempoDupla)) {
//            return false;
//        }
        return true;
    }

    @Override
    public int compareTo(AtletaInscricaoComparadorClassificacao o) {
        if (Objects.equals(this.numeroAtleta, o.numeroAtleta)) {
            //TODO SE FOR DUPLA COM O MESMO NUMERO, IMPLEMENTAR REGRA
            return 0;
        } else if (this.numeroVolta == null && o.numeroVolta != null) {
            return 1;
        } else if (this.numeroVolta != null && o.numeroVolta == null) {
            return -1;
        } else if (this.numeroVolta == null && o.numeroVolta == null) {
            return 0;
        } else {
            int r = this.numeroVolta.compareTo(o.numeroVolta);
            if (r != 0) {
                return r * -1; //inverter a posicao, quem tem mais volta fica em primeiro
            } else {
                if (this.dupla) {
                    //return this.tempoDupla.compareTo(o.tempoDupla);
                    return 0;
                } else {
                    return this.tempo.compareTo(o.tempo);
                }
            }
        }
    }
}
