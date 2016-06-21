package com.brownfield.vre.rest;

import java.sql.Timestamp;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import com.brownfield.vre.Utils;
import com.brownfield.vre.VREConstants;

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
		String result = "Almost there !!!<br />Try again with actual resource now.";
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
	 * Calculate average.
	 *
	 * @param date
	 *            the date
	 * @return the response
	 */
	@GET
	@Path("/calculateAverage")
	public Response calculateAverage(@QueryParam("date") String date) {
		Timestamp recordedDate = null;
		String result = null;
		if (date == null || date.length() == 0) {
			recordedDate = Utils.getYesterdayTimestamp();
		} else {
			recordedDate = Utils.parseDate(date);
		}
		if (recordedDate != null) {
			result = "Averages calculated for - " + recordedDate;
			InternalVREManager ivm = new InternalVREManager();
			ivm.calculateAverage(recordedDate);
		} else {
			result = "Invalid date format. Please use either {" + VREConstants.DATE_TIME_FORMAT + "} OR {"
					+ VREConstants.DATE_FORMAT + "}";
		}
		return Response.status(200).entity(result).build();
	}

	/**
	 * Validate well test.
	 *
	 * @return the response
	 */
	@GET
	@Path("/validateWellTests")
	public Response validateWellTests() {
		String result = "Validated new well tests";
		InternalVREManager ivm = new InternalVREManager();
		ivm.validateWellTests();
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
		ivm.runCalibration();
		return Response.status(200).entity(result).build();
	}

	/**
	 * Monitor jobs.
	 *
	 * @return the response
	 */
	@GET
	@Path("/monitorJobs")
	public Response monitorJobs() {
		String result = "Monitored and updated current jobs";
		InternalVREManager ivm = new InternalVREManager();
		ivm.monitorJobs();
		return Response.status(200).entity(result).build();
	}

	/**
	 * Run vres.
	 *
	 * @return the response
	 */
	@GET
	@Path("/runVREs")
	public Response runVREs(@QueryParam("date") String date) {

		Timestamp recordedDate = null;
		String result = null;
		if (date == null || date.length() == 0) {
			recordedDate = Utils.getYesterdayTimestamp();
		} else {
			recordedDate = Utils.parseDate(date);
		}
		if (recordedDate != null) {
			result = "Ran VREs for - " + recordedDate;
			InternalVREManager ivm = new InternalVREManager();
			ivm.runVREs(recordedDate);
		} else {
			result = "Invalid date format. Please use either {" + VREConstants.DATE_TIME_FORMAT + "} OR {"
					+ VREConstants.DATE_FORMAT + "}";
		}
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
		String result = "Invalid Resource : " + msg;
		return Response.status(200).entity(result).build();
	}

}