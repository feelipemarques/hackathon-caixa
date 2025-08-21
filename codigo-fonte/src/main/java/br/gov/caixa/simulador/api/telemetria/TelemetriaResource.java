package br.gov.caixa.simulador.api.telemetria;

import br.gov.caixa.simulador.application.telemetria.TelemetriaService;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.infrastructure.Infrastructure;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/telemetria")
@Tag(name = "Telemetria", description = "Endpoints dos dados de telemetria da aplicação")
public class TelemetriaResource {

    @Inject
    TelemetriaService telemetriaService;

    @GET
    @Operation(summary = "Lista dados de telemetria por endpoint")
    public Uni<Response> getTelemetria() {
        return Uni.createFrom().item(() -> telemetriaService.gerarTelemetria())
                .runSubscriptionOn(Infrastructure.getDefaultExecutor())
                .map(resp -> Response.ok(resp).build());
    }

}
