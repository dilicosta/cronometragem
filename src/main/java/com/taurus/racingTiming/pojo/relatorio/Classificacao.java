package com.taurus.racingTiming.pojo.relatorio;

import com.taurus.racingTiming.entidade.corrida.AtletaInscricao;
import com.taurus.racingTiming.entidade.corrida.Cronometragem;
import com.taurus.racingTiming.negocio.comparador.CronometragemHoraRegistroComparator;
import com.taurus.util.GeralUtil;
import com.taurus.util.ListaConstantesBase.Sexo;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Diego
 */
public class Classificacao {

    private Integer posicao;
    private Integer numeroAtleta;
    private String nomeAtleta;
    private Sexo sexo;
    private String equipe;
    private String cidadeAtleta;
    private String categoriaAtleta;
    private String nomePercurso;
    private Float km;
    private LocalDateTime horaLargada;
    private LocalDateTime horaChegadaAtleta;
    private List<Cronometragem> cronometragens;
    private Classificacao dupla;

    public Classificacao() {
    }

    public Classificacao(Integer numeroAtleta, String nomeAtleta, Sexo sexo, String equipe, String cidadeAtleta, String categoriaAtleta, String nomePercurso, Float km, LocalDateTime horaLargada, List<Cronometragem> cronometragens) {
        this.numeroAtleta = numeroAtleta;
        this.nomeAtleta = nomeAtleta;
        this.sexo = sexo;
        this.equipe = equipe;
        this.cidadeAtleta = cidadeAtleta;
        this.categoriaAtleta = categoriaAtleta;
        this.nomePercurso = nomePercurso;
        this.km = km;
        this.horaLargada = horaLargada;
        this.cronometragens = cronometragens;
        this.cronometragens.sort(new CronometragemHoraRegistroComparator());
    }

    public Classificacao(AtletaInscricao atletaInscricao) {
        this.numeroAtleta = atletaInscricao.getNumeroAtleta();
        this.nomeAtleta = atletaInscricao.getAtleta().getNome();
        this.sexo = atletaInscricao.getAtleta().getSexo();
        this.equipe = atletaInscricao.getEquipe();
        this.cidadeAtleta = atletaInscricao.getEnderecoAtletaInscricao().getCidade();
        this.categoriaAtleta = atletaInscricao.getCategoria().getCategoriaAtleta().getNome();
        this.nomePercurso = atletaInscricao.getCategoria().getPercurso().getNome();
        this.km = atletaInscricao.getCategoria().getPercurso().getDistancia();
        this.horaLargada = atletaInscricao.getCategoria().getLargada().getHoraInicio();

       // this.cronometragens = new ArrayList(atletaInscricao.getListaCronometragemValidas());
        this.cronometragens.sort(new CronometragemHoraRegistroComparator());
    }

    public Integer getPosicao() {
        return posicao;
    }

    public void setPosicao(Integer posicao) {
        this.posicao = posicao;
    }

    public Integer getNumeroAtleta() {
        return numeroAtleta;
    }

    public void setNumeroAtleta(Integer numeroAtleta) {
        this.numeroAtleta = numeroAtleta;
    }

    public String getNomeAtleta() {
        return nomeAtleta;
    }

    public void setNomeAtleta(String nomeAtleta) {
        this.nomeAtleta = nomeAtleta;
    }

    public Sexo getSexo() {
        return sexo;
    }

    public void setSexo(Sexo sexo) {
        this.sexo = sexo;
    }

    public String getEquipe() {
        return equipe;
    }

    public void setEquipe(String equipe) {
        this.equipe = equipe;
    }

    public String getCidadeAtleta() {
        return cidadeAtleta;
    }

    public void setCidadeAtleta(String cidadeAtleta) {
        this.cidadeAtleta = cidadeAtleta;
    }

    public String getCategoriaAtleta() {
        return categoriaAtleta;
    }

    public void setCategoriaAtleta(String categoriaAtleta) {
        this.categoriaAtleta = categoriaAtleta;
    }

    public String getNomePercurso() {
        return nomePercurso;
    }

    public void setNomePercurso(String nomePercurso) {
        this.nomePercurso = nomePercurso;
    }

    public Float getKm() {
        return km;
    }

    public void setKm(Float km) {
        this.km = km;
    }

    public LocalDateTime getHoraLargada() {
        return horaLargada;
    }

    public void setHoraLargada(LocalDateTime horaLargada) {
        this.horaLargada = horaLargada;
    }

    public LocalDateTime getHoraChegadaAtleta() {
        return horaChegadaAtleta;
    }

    public void setHoraChegadaAtleta(LocalDateTime horaChegadaAtleta) {
        this.horaChegadaAtleta = horaChegadaAtleta;
    }

    public List<Cronometragem> getCronometragens() {
        return cronometragens;
    }

    public void setCronometragens(List<Cronometragem> cronometragens) {
        cronometragens.sort(new CronometragemHoraRegistroComparator());
        this.cronometragens = cronometragens;
    }

    public Classificacao getDupla() {
        return dupla;
    }

    public void setDupla(Classificacao dupla) {
        this.dupla = dupla;
    }

    public String getTempo() {
        return GeralUtil.calcularDiferencaTempo(this.horaLargada, this.horaChegadaAtleta);
    }
}
