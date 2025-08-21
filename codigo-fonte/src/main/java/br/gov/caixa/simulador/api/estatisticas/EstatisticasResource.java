package br.gov.caixa.simulador.api.estatisticas;

import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.infrastructure.Infrastructure;
import jakarta.inject.Inject;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import br.gov.caixa.simulador.application.estatisticas.EstatisticasService;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.metrics.MetricRegistry;
import org.eclipse.microprofile.metrics.Timer;
import org.eclipse.microprofile.metrics.annotation.RegistryType;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

@Path("/estatisticas")
@Tag(name = "Estatísticas", description = "Endpoints das estatísticas de simulações")
public class EstatisticasResource {

    @Inject
    EstatisticasService estatisticasService;

    @Inject
    @RegistryType(type = MetricRegistry.Type.APPLICATION)
    MetricRegistry registry;

    @GET
    @Operation(summary = "Lista estatísticas por dia e produto", description = "Baseado em uma data, retorna as estatísticas agrupadas por produto")
    @APIResponse(responseCode = "200", description = "Retorno bem sucedido")
    @APIResponse(responseCode = "400", description = "Data inválida ou mal formatada")
    @APIResponse(responseCode = "500", description = "Erro interno do servidor")
    public Uni<Response> getEstatisticasPorProdutoDia(
            @Parameter(description = "Data formatada em: AAAA-MM-DD", required = true)
            @QueryParam("data") String data) {

        registry.counter("estatisticas_produto_dia_chamadas").inc();
        Timer.Context context = registry.timer("estatisticas_produto_dia_tempo").time();

        return Uni.createFrom().item(() -> {
                    LocalDate dataReferencia = LocalDate.parse(data);
                    return estatisticasService.listaEstatisticasPorProdutoEDia(dataReferencia);
                })
                .runSubscriptionOn(Infrastructure.getDefaultExecutor())
                .onItem().invoke(resp -> registry.counter("estatisticas_produto_dia_sucesso").inc())
                .onFailure().invoke(err -> registry.counter("estatisticas_produto_dia_erro").inc())
                .map(resp -> Response.ok(resp).build())
                .eventually(context::stop);
    }
}
