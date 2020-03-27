package com.taurus.racingTiming.dao.corrida;

import com.taurus.dao.IGenericDAO;
import com.taurus.exception.PersistenciaException;
import com.taurus.racingTiming.entidade.corrida.CategoriaDaProva;
import com.taurus.racingTiming.entidade.corrida.Prova;
import java.util.List;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Diego
 * @param <T>
 */
public interface ICategoriaDaProvaDAO<T extends CategoriaDaProva> extends IGenericDAO<T> {

    @Transactional(propagation = Propagation.SUPPORTS)
    public List<CategoriaDaProva> pesquisarCategoria(Prova prova) throws PersistenciaException;

}
