package br.gov.caixa.simulador.api;

import br.gov.caixa.simulador.api.simulacao.SimulacaoResource;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.eclipse.microprofile.metrics.*;
import org.eclipse.microprofile.metrics.annotation.RegistryType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

@QuarkusTest
public class SimulacaoResourceTest {

    @Inject
    SimulacaoResource simulacaoResource;

    @InjectMock
    @RegistryType(type = MetricRegistry.Type.APPLICATION)
    MetricRegistry registry;

    @BeforeEach
    public void setup() {
        Counter mockCounter = mock(Counter.class);
        when(registry.counter(anyString())).thenReturn(mockCounter);
        doNothing().when(mockCounter).inc();

        Timer mockTimer = mock(Timer.class);
        Timer.Context mockContext = mock(Timer.Context.class);
        when(registry.timer(anyString())).thenReturn(mockTimer);
        when(mockTimer.time()).thenReturn(mockContext);
    }

    @Test
    public void testSolicitaSimulacaoSucesso() {
        String payload = """
            {
                "valorDesejado": 200,
                "prazo": 2
            }
            """;

        given()
                .header("Content-Type", "application/json")
                .body(payload)
                .when()
                .post("/simulacao")
                .then()
                .statusCode(200)
                .body("idSimulacao", notNullValue())
                .body("codigoProduto", notNullValue())
                .body("descricaoProduto", notNullValue())
                .body("resultadoSimulacao.tipo", hasItems("SAC", "PRICE"))
                .body("resultadoSimulacao[0].parcelas.size()", greaterThan(0));
    }

    @Test
    public void testSolicitaSimulacaoProdutoNaoEncontrado() {
        String payload = """
            {
                "valorDesejado": 200,
                "prazo": 25
            }
            """;

        given()
                .header("Content-Type", "application/json")
                .body(payload)
                .when()
                .post("/simulacao")
                .then()
                .statusCode(400)
                .body("message", equalTo("Não foi encontrado produto adequado aos parâmetros! Tente outro valor ou prazo"));
    }
}