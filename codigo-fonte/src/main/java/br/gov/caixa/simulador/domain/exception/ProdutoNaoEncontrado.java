package br.gov.caixa.simulador.domain.exception;

import jakarta.ws.rs.core.Response;

public class ProdutoNaoEncontrado extends GenericException{

    public ProdutoNaoEncontrado() {
        super(Response.Status.BAD_REQUEST, "Não foi encontrado produto adequado aos parâmetros! Tente outro valor ou prazo");
    }

}
