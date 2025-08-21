package br.gov.caixa.simulador.api;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@QuarkusTest
public class EstatisticasResourceTest {

    @Test
    public void testGetEstatisticasSucesso() {
        given()
                .queryParam("data", "2025-08-20")
                .when()
                .get("/estatisticas")
                .then()
                .statusCode(200)
                .body("$", not(empty())) // garante que a lista não está vazia
                .body("[0].dataReferencia", equalTo("2025-08-20"))
                .body("[0].simulacoes.size()", greaterThanOrEqualTo(0));
    }

    @Test
    public void testGetEstatisticasDataInvalida() {
        given()
                .queryParam("data", "2025-13-40")
                .when()
                .get("/estatisticas")
                .then()
                .statusCode(400);
    }

}
