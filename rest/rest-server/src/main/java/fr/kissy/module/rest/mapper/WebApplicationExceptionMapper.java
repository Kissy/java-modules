package fr.kissy.module.rest.mapper;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

public class WebApplicationExceptionMapper implements ExceptionMapper<WebApplicationException> {

    /**
     * @inheritDoc
     */
    @Override
    public Response toResponse(WebApplicationException exception) {
        return exception.getResponse();
    }
}