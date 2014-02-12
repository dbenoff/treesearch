package com.dbenoff.ws;

import com.itasoftware.nrhp.SearchResult;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

@Path("/search")

public interface SearchService {

       @Path("/property/{searchterm}/{page}")
	   @GET
       @Produces("application/json")
	   public SearchResult searchProperties(@PathParam("searchterm") String searchterm, @PathParam("page") int page);


}
