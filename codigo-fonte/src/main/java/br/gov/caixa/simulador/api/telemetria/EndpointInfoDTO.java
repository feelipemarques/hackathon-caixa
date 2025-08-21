package br.gov.caixa.simulador.api.telemetria;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

public class EndpointInfoDTO {

    @Schema(description = "Nome do Endpoint", example = "Simulacao")
    public String nomeApi;
    public int qtdRequisicoes;
    public long tempoMedio;
    public long tempoMinimo;
    public long tempoMaximo;
    @Schema(description = "Divisão de requisições com sucesso pelas com falhas", example = "0.98")
    public double percentualSucesso;

}
