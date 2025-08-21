package br.gov.caixa.simulador.api.simulacao;

import jakarta.validation.constraints.Min;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.math.BigDecimal;

public class SimulacaoRequestDTO {

    @Min(value = 200, message = "Valor deve ser maior do que R$ 200!")
    @Schema(description = "Valor desejado no empréstimo", example = "2000")
    public BigDecimal valorDesejado;

    @Min(value = 1, message = "Prazo deve ser pelo menos 1 mês!")
    @Schema(description = "Quantidade de parcelas desejadas", example = "12")
    public int prazo;
}
