package com.taurus.racingTiming.negocio;

import com.taurus.exception.AvisoNegocioException;
import com.taurus.exception.NegocioException;
import com.taurus.racingTiming.entidade.corrida.Prova;
import com.taurus.racingTiming.pojo.CorrespondenciaEntidadeEExcel;
import java.io.File;
import java.util.List;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Diego
 */
public interface IImportador {

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public String importarInscricoesFMCEmExcel(File file, CorrespondenciaEntidadeEExcel correspondencia, Prova prova) throws NegocioException;

    public List<List<String>> carregarXLinhasExcell(File file, int totalLinhas) throws NegocioException;

    public List<String> verificarTipo(File file, int numeroLinha) throws NegocioException, AvisoNegocioException;

    public List<String> carregarCategoriaXls(File file, Integer indiceColunaCategoriaXls, Integer linhasCabecalho) throws NegocioException;
}
