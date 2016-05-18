package com.brownfield.vre.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

/**
 * The Class VREApplicationResource.
 * 
 * @author Onkar Dhuri <onkar.dhuri@synerzip.com>
 */
@Path("/WellTestValidation")
public class VREBaseResource {

	/*
	 * @GET public Response doNothing() {
	 * 
	 * String result = "Almost there !!";
	 * 
	 * return Response.status(200).entity(result).build();
	 * 
	 * }
	 * 
	 * @GET public Response doNothing(@PathParam("param") String msg) {
	 * 
	 * String result = "It Works !!! " + msg;
	 * 
	 * return Response.status(200).entity(result).build();
	 * 
	 * }
	 */

	@GET
	public Response validateWellTEst() {
		String result = "Well Test Validation Success";
		InternalVREManager ivm = new InternalVREManager();
		ivm.validateNewTests();
		return Response.status(200).entity(result).build();

	}

	/**
	 * Prints the message.
	 *
	 * @param msg
	 *            the msg
	 * @return the response
	 */
	@GET
	@Path("/{param}")
	public Response printMessage(@PathParam("param") String msg) {

		String result = "Restful example : " + msg;

		return Response.status(200).entity(result).build();

	}

}