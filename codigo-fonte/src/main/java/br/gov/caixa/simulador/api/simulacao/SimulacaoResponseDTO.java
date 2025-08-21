package br.gov.caixa.simulador.api.simulacao;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class SimulacaoResponseDTO {
    public UUID idSimulacao;
    public int codigoProduto;
    public String descricaoProduto;
    public BigDecimal taxaJuros;
    public List<ParcelasPorTipoDTO> resultadoSimulacao;
}
