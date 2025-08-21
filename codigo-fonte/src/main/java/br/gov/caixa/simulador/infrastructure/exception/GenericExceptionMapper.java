package br.gov.caixa.simulador.infrastructure.exception;

import br.gov.caixa.simulador.domain.exception.GenericException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class GenericExceptionMapper implements ExceptionMapper<GenericException> {

    @Override
    public Response toResponse(GenericException e) {
        ErrorResponseDTO errorResponse = new ErrorResponseDTO(e.getMessage());
        return Response.status(e.getStatus()).entity(errorResponse).build();
    }

}
