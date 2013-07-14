package fr.kissy.module.rest.mapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

/**
 * @author Guillaume <lebiller@fullsix.com>
 */
public class CustomExceptionMapper implements ExceptionMapper<Exception> {

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomExceptionMapper.class);

    /**
     * @inheritDoc
     */
    @Override
    public Response toResponse(Exception exception) {
        LOGGER.error("An unexpected exception was thrown", exception);
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(exception.getMessage()).build();
    }
}