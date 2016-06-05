package com.brownfield.vre.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

// TODO: Auto-generated Javadoc
/**
 * The Class VREApplicationResource.
 * 
 * @author Onkar Dhuri <onkar.dhuri@synerzip.com>
 */
@Path("/VRE")
public class VREBaseResource {

	/**
	 * Do nothing.
	 *
	 * @return the response
	 */
	@GET
	public Response doNothing() {

		String result = "Almost there !!";

		return Response.status(200).entity(result).build();

	}

	/**
	 * Do nothing.
	 *
	 * @param msg
	 *            the msg
	 * @return the response
	 */
	@GET
	public Response doNothing(@PathParam("param") String msg) {

		String result = "It Works !!! " + msg;

		return Response.status(200).entity(result).build();

	}

	/**
	 * Validate well t est.
	 *
	 * @return the response
	 */
	@GET
	@Path("/validateWellTests")
	public Response validateWellTests() {
		String result = "Well test validation started";
		InternalVREManager ivm = new InternalVREManager();
		ivm.validateWellTests();
		return Response.status(200).entity(result).build();

	}

	/**
	 * Refresh variables.
	 *
	 * @return the response
	 */
	@GET
	@Path("/refreshVariables")
	public Response refreshVariables() {
		String result = "Refreshed VRE variables";
		InternalVREManager ivm = new InternalVREManager();
		ivm.refreshVariables();
		return Response.status(200).entity(result).build();

	}

	/**
	 * Run calibration.
	 *
	 * @return the response
	 */
	@GET
	@Path("/runCalibration")
	public Response runCalibration() {
		String result = "Recalibration of models started";
		InternalVREManager ivm = new InternalVREManager();
		ivm.refreshVariables();
		return Response.status(200).entity(result).build();

	}

	/**
	 * Run vr es.
	 *
	 * @return the response
	 */
	@GET
	@Path("/runVREs")
	public Response runVREs() {
		String result = "Running VREs";
		InternalVREManager ivm = new InternalVREManager();
		ivm.refreshVariables();
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