package fr.kissy.module.rest.resource;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.jaxrs.listing.ApiListing;

import javax.jws.WebService;
import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Api("/api")
@Path("/api")
@Produces(MediaType.APPLICATION_JSON)
public class ApiListingResource extends ApiListing {
}