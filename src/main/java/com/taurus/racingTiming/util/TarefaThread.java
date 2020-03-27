package com.taurus.racingTiming.util;

import com.taurus.javafx.util.ControleJanela;
import com.taurus.util.GeralUtil;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;

/**
 *
 * @author Diego
 * @param <V>
 */
public abstract class TarefaThread<V> extends Task<V> {

    public TarefaThread(String msgSucesso, String msgErro, ControleJanela cj) {
        super();

        this.setOnSucceeded((WorkerStateEvent event) -> {
            cj.exibirInformacao(msgSucesso);
        });

        this.setOnFailed((WorkerStateEvent event) -> {
            cj.exibirErro(msgErro, GeralUtil.getMensagemOriginalErro(event.getSource().getException()));
        });
    }
}
