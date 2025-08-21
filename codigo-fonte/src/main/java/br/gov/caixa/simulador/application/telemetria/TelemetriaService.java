package br.gov.caixa.simulador.application.telemetria;

import br.gov.caixa.simulador.api.telemetria.EndpointInfoDTO;
import br.gov.caixa.simulador.api.telemetria.TelemetriaResponseDTO;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.metrics.Counter;
import org.eclipse.microprofile.metrics.MetricID;
import org.eclipse.microprofile.metrics.MetricRegistry;
import org.eclipse.microprofile.metrics.Timer;
import org.eclipse.microprofile.metrics.annotation.RegistryType;

import java.time.LocalDate;
import java.util.ArrayList;

import java.util.List;

@ApplicationScoped
public class TelemetriaService {

    @Inject
    @RegistryType(type = MetricRegistry.Type.APPLICATION)
    MetricRegistry registry;

    public TelemetriaResponseDTO gerarTelemetria() {
        TelemetriaResponseDTO response = new TelemetriaResponseDTO();
        response.dataReferencia = LocalDate.now();

        List<EndpointInfoDTO> lista = new ArrayList<>();

        lista.add(montarInfo("simulacao", "Simulacao"));
        lista.add(montarInfo("todas_simulacoes", "TodasSimulacoes"));
        lista.add(montarInfo("estatisticas_produto_dia", "EstatisticasProdutoDia"));

        response.listaEndpoints = lista;
        return response;
    }

    private EndpointInfoDTO montarInfo(String baseName, String nomeApi) {
        EndpointInfoDTO info = new EndpointInfoDTO();
        info.nomeApi = nomeApi;

        Counter chamadas = registry.getCounters().get(new MetricID(baseName + "_chamadas"));
        Counter sucesso = registry.getCounters().get(new MetricID(baseName + "_sucesso"));
        Timer tempo = registry.getTimers().get(new MetricID(baseName + "_tempo"));

        if (chamadas != null) {
            info.qtdRequisicoes = (int) chamadas.getCount();
        }

        if (tempo != null && tempo.getCount() > 0) {
            info.tempoMedio = (long) tempo.getSnapshot().getMean() / 1_000_000;
            info.tempoMinimo = tempo.getSnapshot().getMin() / 1_000_000;
            info.tempoMaximo = tempo.getSnapshot().getMax() / 1_000_000;
        }

        if (sucesso != null && info.qtdRequisicoes > 0) {
            info.percentualSucesso = (double) sucesso.getCount() / info.qtdRequisicoes;
        }

        return info;
    }

}
