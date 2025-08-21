package br.gov.caixa.simulador.infrastructure.exception;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class JsonExceptionMapper implements ExceptionMapper<Throwable> {

    @Override
    public Response toResponse(Throwable exception) {
        Throwable cause = exception.getCause();

        if (cause instanceof JsonParseException || cause instanceof JsonMappingException) {
            ErrorResponseDTO errorResponse = new ErrorResponseDTO("JSON inválido ou campos incorretos!");
            return Response.status(Response.Status.BAD_REQUEST).entity(errorResponse).build();
        }

        ErrorResponseDTO errorResponse = new ErrorResponseDTO("Erro interno inesperado: " + exception.getMessage());
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(errorResponse).build();
    }
}
