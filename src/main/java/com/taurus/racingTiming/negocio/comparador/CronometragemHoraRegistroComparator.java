package com.taurus.racingTiming.negocio.comparador;

import com.taurus.racingTiming.entidade.corrida.Cronometragem;
import java.util.Comparator;

/**
 *
 * @author Diego
 */
public class CronometragemHoraRegistroComparator implements Comparator<Cronometragem> {

    @Override
    public int compare(Cronometragem o1, Cronometragem o2) {
        return o1.getHoraRegistro().compareTo(o2.getHoraRegistro());
    }

}
