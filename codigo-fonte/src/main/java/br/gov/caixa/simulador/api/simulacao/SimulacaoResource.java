package br.gov.caixa.simulador.api.simulacao;

import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.infrastructure.Infrastructure;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.metrics.MetricRegistry;
import org.eclipse.microprofile.metrics.Timer;
import org.eclipse.microprofile.metrics.annotation.RegistryType;
import br.gov.caixa.simulador.application.registro.RegistroService;
import br.gov.caixa.simulador.application.simulacao.SimulacaoService;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/simulacao")
@Tag(name = "Simulações", description = "Endpoints para simular e obter as simulações")
public class SimulacaoResource {

    @Inject
    SimulacaoService simulacaoService;

    @Inject
    @RegistryType(type = MetricRegistry.Type.APPLICATION)
    MetricRegistry registry;

    @Inject
    RegistroService registroService;

    @POST
    @Operation(summary = "Solicita simulação SAC e PRICE", description = "Dados valor e prazo, retorna, de acordo com o produto equivalente, o parcelamento na SAC e na PRICE")
    @APIResponse(responseCode = "200", description = "Simulação realizada")
    @APIResponse(responseCode = "400", description = "Request mal formatado")
    @APIResponse(responseCode = "404", description = "Não há produtos que atendam aos parâmetros")
    @APIResponse(responseCode = "500", description = "Erro interno do servidor")
    public Uni<Response> solicitaSimulacao(
            @Parameter(description = "JSON com valor e prazo", required = true)
            @Valid SimulacaoRequestDTO request) {

        registry.counter("simulacao_chamadas").inc();
        Timer.Context context = registry.timer("simulacao_tempo").time();

        return Uni.createFrom().item(() -> simulacaoService.processaSimulacao(request))
                .runSubscriptionOn(Infrastructure.getDefaultExecutor())
                .onItem().invoke(resp -> registry.counter("simulacao_sucesso").inc())
                .map(resp -> Response.ok(resp).build())
                .eventually(context::stop);
    }

    @GET
    @Path("/all")
    @Operation(summary = "Lista todas as simulações", description = "Retorna todas as simulações realizadas de maneira paginada")
    @APIResponse(responseCode = "200", description = "Operação bem sucedida")
    @APIResponse(responseCode = "400", description = "Request mal formatado")
    @APIResponse(responseCode = "500", description = "Erro interno do servidor")
    public Uni<Response> todasAsSimulacoes(@QueryParam("pagina") @DefaultValue("1") int pagina, @QueryParam("qtdRegistrosPagina") @DefaultValue("5") int limit) {

        registry.counter("todas_simulacoes_chamadas").inc();
        Timer.Context context = registry.timer("todas_simulacoes_tempo").time();

        return Uni.createFrom().item(() -> registroService.listarSimulacoesPaginadas(pagina, limit))
                .runSubscriptionOn(Infrastructure.getDefaultExecutor())
                .onItem().invoke(resp -> registry.counter("todas_simulacoes_sucesso").inc())
                .onFailure().invoke(err -> registry.counter("todas_simulacoes_erro").inc())
                .map(resp -> Response.ok(resp).build())
                .eventually(context::stop);
    }

}
