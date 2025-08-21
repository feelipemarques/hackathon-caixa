package br.gov.caixa.simulador.domain.exception;

import jakarta.ws.rs.core.Response;

public class GenericException extends RuntimeException{

    private final Response.Status statusCode;

    public GenericException(Response.Status status, String message){
        super(message);
        this.statusCode = status;
    }

    public Response.Status getStatus(){
        return statusCode;
    }
}
