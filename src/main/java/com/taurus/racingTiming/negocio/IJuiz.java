package com.taurus.racingTiming.negocio;

import com.taurus.exception.AvisoNegocioException;
import com.taurus.exception.NegocioException;
import com.taurus.racingTiming.entidade.corrida.AtletaInscricao;
import com.taurus.racingTiming.entidade.corrida.CategoriaDaProva;
import com.taurus.racingTiming.entidade.corrida.Percurso;
import com.taurus.racingTiming.entidade.corrida.Prova;
import com.taurus.racingTiming.negocio.comparador.AtletaInscricaoComparadorClassificacao;
import com.taurus.util.ListaConstantesBase;
import com.taurus.util.Pagina;
import java.util.List;
import java.util.Map;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Diego
 */
public interface IJuiz {

    public Map<String, Map<AtletaInscricaoComparadorClassificacao, AtletaInscricao>> getMapClassificacaoTemporaria();

    public void adicionarAtletaClassificacaoTemporaria(AtletaInscricao atletaInscricao);

    @Transactional(propagation = Propagation.SUPPORTS)
    public void carregarClassificacaoTemporiaria(Prova prova) throws NegocioException;

    public Integer pesquisarPosicaoTemporariaAtleta(AtletaInscricao atletaInscricao);

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public AtletaInscricao desclassificarAtleta(AtletaInscricao atletaInscricao, String motivoDesclassificacao) throws NegocioException;

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public AtletaInscricao desfazerDesclassificacaoAtleta(AtletaInscricao atletaInscricao) throws NegocioException;

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public AtletaInscricao confirmarDnfAtleta(AtletaInscricao atletaInscricao) throws NegocioException;

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public AtletaInscricao desfazerConfirmarDnfAtleta(AtletaInscricao atletaInscricao) throws NegocioException;

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Prova reabrirCronometragem(Prova prova) throws NegocioException, AvisoNegocioException;

    @Transactional(propagation = Propagation.SUPPORTS)
    public List<AtletaInscricao> pesquisarClassificacaoProvaGeral(Percurso percurso, ListaConstantesBase.Sexo sexo) throws NegocioException;

    @Transactional(propagation = Propagation.SUPPORTS)
    public List<AtletaInscricao> pesquisarClassificacaoProvaGeral(Percurso percurso, ListaConstantesBase.Sexo sexo, Pagina pagina) throws NegocioException;

    @Transactional(propagation = Propagation.SUPPORTS)
    public List<AtletaInscricao> pesquisarClassificacaoProvaCategoria(CategoriaDaProva categoriaDaProva) throws NegocioException;

    @Transactional(propagation = Propagation.SUPPORTS)
    public List<AtletaInscricao> pesquisarClassificacaoProvaCategoria(CategoriaDaProva categoriaDaProva, Pagina pagina) throws NegocioException;

    @Transactional(propagation = Propagation.SUPPORTS)
    public List<AtletaInscricao> pesquisarClassificacaoAtleta(Prova prova, String nomeAtleta, Pagina pagina) throws NegocioException;

    @Transactional(propagation = Propagation.SUPPORTS)
    public AtletaInscricao pesquisarClassificacaoAtleta(Prova prova, String numeroAtleta) throws NegocioException;

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void criarClassificacao(Prova prova) throws NegocioException;

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Prova finalizarApuracao(Prova prova) throws NegocioException, AvisoNegocioException;

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Prova reabrirApuracao(Prova prova) throws NegocioException, AvisoNegocioException;

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Prova finalizarProva(Prova prova) throws NegocioException, AvisoNegocioException;

}
