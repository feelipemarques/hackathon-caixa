package br.gov.caixa.simulador.api.telemetria;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TelemetriaResponseDTO {
    public LocalDate dataReferencia;
    public List<EndpointInfoDTO> listaEndpoints = new ArrayList<>();
}
