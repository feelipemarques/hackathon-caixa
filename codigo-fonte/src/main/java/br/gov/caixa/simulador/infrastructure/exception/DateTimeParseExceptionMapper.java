package br.gov.caixa.simulador.infrastructure.exception;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import java.time.format.DateTimeParseException;

@Provider
public class DateTimeParseExceptionMapper implements ExceptionMapper<DateTimeParseException> {
    @Override
    public Response toResponse(DateTimeParseException exception) {
        return Response.status(Response.Status.BAD_REQUEST)
                .entity("Data inválida: " + exception.getParsedString())
                .build();
    }
}
