package br.gov.caixa.simulador.api.estatisticas;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.time.LocalDate;
import java.util.List;

public class EstatisticasPorDiaDTO {
    @Schema(description = "Data das estatísticas", example = "2025-19-08")
    public LocalDate dataReferencia;
    public List<EstatisticasDTO> simulacoes;

}
