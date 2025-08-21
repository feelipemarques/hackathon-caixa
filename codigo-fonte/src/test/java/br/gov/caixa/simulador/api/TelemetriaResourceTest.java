package br.gov.caixa.simulador.api;


import br.gov.caixa.simulador.api.telemetria.TelemetriaResource;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.eclipse.microprofile.metrics.MetricRegistry;
import org.eclipse.microprofile.metrics.annotation.RegistryType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@QuarkusTest
public class TelemetriaResourceTest {

    @Inject
    TelemetriaResource telemetriaResource;

    @InjectMock
    @RegistryType(type = MetricRegistry.Type.APPLICATION)
    MetricRegistry registry;

    @Test
    public void testGetTelemetria(){
        given().when().get("/telemetria").then().statusCode(200)
                .body("dataReferencia", notNullValue())
                .body("listaEndpoints.size()", is(3))
                .body("listaEndpoints.nomeApi", hasItems("Simulacao", "TodasSimulacoes", "EstatisticasProdutoDia"))
                .body("listaEndpoints.qtdRequisicoes", everyItem(equalTo(0)))
                .body("listaEndpoints.tempoMedio", everyItem(equalTo(0)))
                .body("listaEndpoints.tempoMinimo", everyItem(equalTo(0)))
                .body("listaEndpoints.tempoMaximo", everyItem(equalTo(0)))
                .body("listaEndpoints.percentualSucesso", everyItem(equalTo(0.0f)));
    }
}
