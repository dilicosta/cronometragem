package com.taurus.racingTiming.ui.converter;

import com.taurus.racingTiming.util.ListaConstantes.TipoSanguineo;
import javafx.util.StringConverter;

/**
 *
 * @author Diego
 */
public class TipoSanguineoConverter extends StringConverter<TipoSanguineo> {

    @Override
    public String toString(TipoSanguineo tipoSanguineo) {
        return tipoSanguineo == null ? null : tipoSanguineo.getNome();
    }

    @Override
    public TipoSanguineo fromString(String string) {
        for (TipoSanguineo tipoSanguineo : TipoSanguineo.values()) {
            if (tipoSanguineo.getNome().equals(string)) {
                return tipoSanguineo;
            }
        }
        return null;
    }
}
