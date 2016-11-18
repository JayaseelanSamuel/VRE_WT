package com.brownfield.vre.rest;

import static com.brownfield.vre.VREConstants.VRE_LIST;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
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
	 * @param date
	 *            the date
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
	 * Run VRE for duration.
	 *
	 * @param stringID
	 *            the string ID
	 * @param vres
	 *            the vres
	 * @param start_date
	 *            the start date
	 * @param end_date
	 *            the end date
	 * @param pi
	 *            the pi
	 * @param reservoirPressure
	 *            the reservoir pressure
	 * @param holdUPV
	 *            the hold upv
	 * @param ffv
	 *            the ffv
	 * @param chokeMultiplier
	 *            the choke multiplier
	 * @param user
	 *            the user
	 * @return the response
	 */
	@GET
	@Path("/runVREForDuration")
	public Response runVREForDuration(@QueryParam("stringID") int stringID, @QueryParam("vres") String vres,
			@QueryParam("start_date") String start_date, @QueryParam("end_date") String end_date,
			@QueryParam("pi") double pi, @QueryParam("reservoirPressure") double reservoirPressure,
			@QueryParam("holdUPV") double holdUPV, @QueryParam("ffv") double ffv,
			@QueryParam("chokeMultiplier") double chokeMultiplier, @QueryParam("user") String user) {

		Timestamp startDate = null, endDate = null;
		List<String> vresToRun = null;
		String result = null;
		if (start_date != null && start_date.length() != 0) {
			startDate = Utils.parseDate(start_date);
		} else {
			result = "Invalid start date format. Please use either {" + VREConstants.DATE_TIME_FORMAT + "} OR {"
					+ VREConstants.DATE_FORMAT + "}";
		}
		if (end_date != null && end_date.length() != 0) {
			endDate = Utils.parseDate(end_date);
		} else {
			result = "Invalid end date format. Please use either {" + VREConstants.DATE_TIME_FORMAT + "} OR {"
					+ VREConstants.DATE_FORMAT + "}";
		}
		if (vres != null && vres.length() != 0) {
			String[] vresArr = vres.toUpperCase().split(",");
			vresToRun = Arrays.asList(vresArr);
			if (!VRE_LIST.containsAll(vresToRun)) {
				vresToRun = null;
				result = "List contains invalid vre option";
			}
		} else {
			result = "Empty list. Please select at least one VRE to run";
		}

		if (startDate != null && endDate != null) {
			if (vresToRun != null) {
				InternalVREManager ivm = new InternalVREManager();
				result = ivm.runVREForDuration(stringID, vresToRun, startDate, endDate, pi, reservoirPressure, holdUPV,
						ffv, chokeMultiplier, user);
			}
		} else {
			result = "Invalid date format. Please use either {" + VREConstants.DATE_TIME_FORMAT + "} OR {"
					+ VREConstants.DATE_FORMAT + "}";
		}

		result = "{ \"value\" : [\"" + result + "\"] }";
		return Response.status(200).entity(result).type(MediaType.APPLICATION_JSON).build();
	}

	/**
	 * Monitor jobs.
	 *
	 * @return the response
	 */
	@GET
	@Path("/refreshProxyModels")
	public Response refreshProxyModels() {
		String result = "Refreshing proxy models started...";
		InternalVREManager ivm = new InternalVREManager();
		ivm.refreshProxyModels();
		return Response.status(200).entity(result).build();
	}

	/**
	 * Run multi rate well test.
	 *
	 * @param stringID
	 *            the string id
	 * @param liqRates
	 *            the liq rates
	 * @param whps
	 *            the whps
	 * @param wcut
	 *            the wcut
	 * @return the response
	 */
	@GET
	@Path("/runMultiRateWellTest")
	public Response runMultiRateWellTest(@QueryParam("stringID") int stringID,
			@QueryParam("liqRates") Double[] liqRates, @QueryParam("whps") Double[] whps,
			@QueryParam("wcut") double wcut) {
		InternalVREManager ivm = new InternalVREManager();
		String result = ivm.runMultiRateWellTest(stringID, liqRates, whps, wcut);
		result = "{ \"value\" : [" + result + "] }";
		return Response.status(200).entity(result).build();
	}

	/**
	 * Run model prediction.
	 *
	 * @return the response
	 */
	@GET
	@Path("/runModelPrediction")
	public Response runModelPrediction() {
		String result = "Model prediction started...";
		InternalVREManager ivm = new InternalVREManager();
		ivm.runModelPrediction();
		return Response.status(200).entity(result).build();
	}

	/**
	 * Run VRE post recalibration.
	 *
	 * @param stringID
	 *            the string ID
	 * @param wellTestID
	 *            the well test ID
	 * @param wellTestDate
	 *            the well test date
	 * @param user
	 *            the user
	 * @param calibrateFlag
	 *            the calibrate flag
	 * @return the response
	 */
	@GET
	@Path("/runVREPostRecalibration")
	public Response runVREPostRecalibration(@QueryParam("stringID") int stringID,
			@QueryParam("wellTestID") int wellTestID, @QueryParam("wellTestDate") String wellTestDate,
			@QueryParam("user") String user, @QueryParam("calibrateFlag") boolean calibrateFlag) {

		Timestamp startDate = null;
		String result = null;
		if (wellTestDate != null && wellTestDate.length() != 0) {
			startDate = Utils.parseDate(wellTestDate);
		} else {
			result = "Invalid well test date format. Please use either {" + VREConstants.DATE_TIME_FORMAT + "} OR {"
					+ VREConstants.DATE_FORMAT + "}";
		}

		if (startDate != null) {
			InternalVREManager ivm = new InternalVREManager();
			result = ivm.runVREPostRecalibration(stringID, wellTestID, startDate, user, calibrateFlag);
		} else {
			result = "Invalid date format. Please use either {" + VREConstants.DATE_TIME_FORMAT + "} OR {"
					+ VREConstants.DATE_FORMAT + "}";
		}

		result = "{ \"value\" : [\"" + result + "\"] }";
		return Response.status(200).entity(result).type(MediaType.APPLICATION_JSON).build();
	}

	/**
	 * Populate technical rate.
	 *
	 * @param date
	 *            the date
	 * @return the response
	 */
	@GET
	@Path("/populateTechnicalRate")
	public Response populateTechnicalRate(@QueryParam("date") String date) {
		Timestamp recordedDate = null;
		String result = null;
		if (date == null || date.length() == 0) {
			// get tomorrows date
			recordedDate = Utils.getNextOrPreviousDay(new Timestamp(new Date().getTime()), 1);
		} else {
			recordedDate = Utils.parseDate(date);
		}
		if (recordedDate != null) {
			result = "Populated technical rates for the month of - " + recordedDate;
			InternalVREManager ivm = new InternalVREManager();
			ivm.populateTechnicalRate(recordedDate);
		} else {
			result = "Invalid date format. Please use either {" + VREConstants.DATE_TIME_FORMAT + "} OR {"
					+ VREConstants.DATE_FORMAT + "}";
		}
		return Response.status(200).entity(result).build();
	}

	/**
	 * Run injection calibration.
	 *
	 * @param date
	 *            the date
	 * @return the response
	 */
	@GET
	@Path("/runInjectionCalibration")
	public Response runInjectionCalibration(@QueryParam("date") String date) {

		Timestamp recordedDate = null;
		String result = null;
		if (date == null || date.length() == 0) {
			recordedDate = Utils.getYesterdayTimestamp();
		} else {
			recordedDate = Utils.parseDate(date);
		}
		if (recordedDate != null) {
			result = "Ran injection calibration for - " + recordedDate;
			InternalVREManager ivm = new InternalVREManager();
			ivm.runInjectionCalibration(recordedDate);
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