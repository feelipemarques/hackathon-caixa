package br.gov.caixa.simulador.domain.registrodb;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;


@Entity
public class Registro extends PanacheEntity {

    public UUID idSimulacao;
    public BigDecimal valorDesejado;
    public int prazo;
    public BigDecimal valorTotalParcelasSAC = BigDecimal.ZERO;
    public BigDecimal valorTotalParcelasPRICE = BigDecimal.ZERO;
    public LocalDate dataReferencia = LocalDate.now();
    public int codigoProduto;
}
