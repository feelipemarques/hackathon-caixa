package br.gov.caixa.simulador.api.registro;

import br.gov.caixa.simulador.domain.registrodb.Registro;

import java.math.BigDecimal;
import java.util.UUID;

public class RegistroSimulacaoDTO {
    public UUID idSimulacao;
    public BigDecimal valorDesejado;
    public int prazo;
    public BigDecimal valorTotalParcelasSAC = BigDecimal.ZERO;
    public BigDecimal valorTotalParcelasPRICE = BigDecimal.ZERO;

    public static RegistroSimulacaoDTO fromEntity(Registro registro){
        RegistroSimulacaoDTO dto = new RegistroSimulacaoDTO();
        dto.idSimulacao = registro.idSimulacao;
        dto.valorDesejado = registro.valorDesejado;
        dto.prazo = registro.prazo;
        dto.valorTotalParcelasSAC = registro.valorTotalParcelasSAC;
        dto.valorTotalParcelasPRICE = registro.valorTotalParcelasPRICE;

        return dto;
    }
}
