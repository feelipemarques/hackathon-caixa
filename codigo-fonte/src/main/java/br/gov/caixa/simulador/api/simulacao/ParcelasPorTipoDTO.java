package br.gov.caixa.simulador.api.simulacao;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.util.List;

public class ParcelasPorTipoDTO {
    @Schema(description = "Sistema de amortização", example = "SAC")
    public String tipo;
    public List<ParcelaDTO> parcelas;

}
