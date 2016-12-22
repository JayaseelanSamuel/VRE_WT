package com.brownfield.vre.rest;

import static com.brownfield.vre.VREConstants.PHD_TEIID_URL;
import static com.brownfield.vre.VREConstants.PREDICTION_FREQUENCY;
import static com.brownfield.vre.VREConstants.RECAL_DATE_DIFF;
import static com.brownfield.vre.VREConstants.TEIID_DRIVER_NAME;
import static com.brownfield.vre.VREConstants.TEIID_PASSWORD;
import static com.brownfield.vre.VREConstants.TEIID_USER;
import static com.brownfield.vre.VREConstants.VRE_JNDI_NAME;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import com.brownfield.vre.AvgCalculator;
import com.brownfield.vre.RatePopulator;
import com.brownfield.vre.Utils;
import com.brownfield.vre.ValidateWellTest;
import com.brownfield.vre.exe.VREExecutioner;
import com.brownfield.vre.exe.models.MultiRateTestModel;
import com.brownfield.vre.exe.models.StringModel;
import com.brownfield.vre.jobs.JobsMonitor;
import com.google.gson.Gson;

/**
 * The Class InternalVREManager.
 * 
 * @author Onkar Dhuri <onkar.dhuri@synerzip.com>
 */
public class InternalVREManager {

	/** The logger. */
	private static final Logger LOGGER = Logger.getLogger(InternalVREManager.class.getName());

	/**
	 * Gets the PHD connection.
	 *
	 * @return the PHD connection
	 * @throws ClassNotFoundException
	 *             the class not found exception
	 * @throws SQLException
	 *             the SQL exception
	 */
	private Connection getPHDConnection() throws ClassNotFoundException, SQLException {
		Class.forName(TEIID_DRIVER_NAME);
		Connection result = DriverManager.getConnection(PHD_TEIID_URL, TEIID_USER, TEIID_PASSWORD);
		return result;
	}

	/**
	 * Gets the VRE connection.
	 *
	 * @return the VRE connection
	 * @throws SQLException
	 *             the SQL exception
	 * @throws NamingException
	 *             the naming exception
	 */
	private Connection getVREConnection() throws SQLException, NamingException {
		Context initialContext = new InitialContext();
		DataSource datasource = (DataSource) initialContext.lookup(VRE_JNDI_NAME);
		Connection result = datasource.getConnection();
		return result;
	}

	/**
	 * Refresh variables.
	 */
	public void refreshVariables() {
		try (Connection vreConn = getVREConnection()) {
			LOGGER.info("Refreshing VRE variables started !!!");
			Utils.refreshVariables(vreConn);
			Utils.refreshProperties();
			//Changed by Jay
			Utils.refreshEmailGroup(vreConn);
			LOGGER.info("Refreshing VRE variables finished !!!");
		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE, e.getMessage());
			e.printStackTrace();
		} catch (NamingException e) {
			LOGGER.log(Level.SEVERE, e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * Calculate average.
	 *
	 * @param recordedDate
	 *            the recorded date
	 */
	public void calculateAverage(Timestamp recordedDate) {
		try (Connection vreConn = getVREConnection()) {
			LOGGER.info("Calculate averages started for - " + recordedDate);
			AvgCalculator ac = new AvgCalculator();
			ac.calculateAverage(vreConn, recordedDate);
			LOGGER.info("Calculate averages finished for - " + recordedDate);
		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE, e.getMessage());
			e.printStackTrace();
		} catch (NamingException e) {
			LOGGER.log(Level.SEVERE, e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * Validate new tests.
	 */
	public void validateWellTests() {
		try (Connection vreConn = getVREConnection(); Connection phdConn = getPHDConnection();) {
			LOGGER.info("validate wellTests started !!!");
			ValidateWellTest vwt = new ValidateWellTest();
			vwt.validateNewWellTests(vreConn, phdConn);
			LOGGER.info("validate wellTests finished !!!");
		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE, e.getMessage());
			e.printStackTrace();
		} catch (NamingException e) {
			LOGGER.log(Level.SEVERE, e.getMessage());
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			LOGGER.log(Level.SEVERE, e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * Run calibration.
	 */ //changed by Jay
	public void runCalibration(int stringIDParam,String testType) {
		try (Connection vreConn = getVREConnection()) {
			LOGGER.info("Run calibrations started !!! with atrindIDparam "+stringIDParam+" test type"+testType);
			VREExecutioner vreEx = new VREExecutioner();
			vreEx.runCalibration(vreConn,stringIDParam,testType);
			LOGGER.info("Run calibrations finished !!!");
		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE, e.getMessage());
			e.printStackTrace();
		} catch (NamingException e) {
			LOGGER.log(Level.SEVERE, e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * Monitor jobs.
	 */
	public void monitorJobs() {
		try (Connection vreConn = getVREConnection()) {
			LOGGER.info("Monitor jobs started !!!");
			JobsMonitor jm = new JobsMonitor();
			jm.monitorJobs(vreConn);
			LOGGER.info("Monitor jobs finished !!!");
		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE, e.getMessage());
			e.printStackTrace();
		} catch (NamingException e) {
			LOGGER.log(Level.SEVERE, e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * Run vres.
	 *
	 * @param recordedDate
	 *            the recorded date
	 */
	public void runVREs(Timestamp recordedDate) {
		try (Connection vreConn = getVREConnection()) {
			LOGGER.info("run VREs started for - " + recordedDate);
			VREExecutioner vreEx = new VREExecutioner();
			vreEx.runVREs(vreConn, recordedDate);
			LOGGER.info("run VREs finished for - " + recordedDate);
		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE, e.getMessage());
			e.printStackTrace();
		} catch (NamingException e) {
			LOGGER.log(Level.SEVERE, e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * Run VRE for duration.
	 *
	 * @param stringID
	 *            the string ID
	 * @param vresToRun
	 *            the vres to run
	 * @param startDate
	 *            the start date
	 * @param endDate
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
	 * @return the string
	 */
	public String runVREForDuration(final int stringID, final List<String> vresToRun, final Timestamp startDate,
			final Timestamp endDate, final double pi, final double reservoirPressure, final double holdUPV,
			final double ffv, final double chokeMultiplier, final String user) {

		String message = "Recalculation process started for - " + vresToRun;

		try (Connection vreConn = getVREConnection()) {
			StringModel sm = Utils.getStringModel(vreConn, stringID);
			if (sm == null) {
				message = "Invalid stringID";
			} else {
				boolean isRunning = false;
				VREExecutioner vreEx = new VREExecutioner();
				message = vreEx.isRunning(vreConn, sm);

				if (message != null) {
					isRunning = true;
				} else {
					message = "Started running " + vresToRun + " for " + sm.getStringName() + " from " + startDate
							+ " to " + endDate;
				}
				if (!isRunning) {
					Runnable r = new Runnable() {
						public void run() {
							LOGGER.info("Running " + vresToRun + " for " + stringID + " from " + startDate + " to "
									+ endDate);
							try (final Connection vreConn = getVREConnection()) {
								VREExecutioner vreEx = new VREExecutioner();
								vreEx.runVREForDuration(vreConn, stringID, vresToRun, startDate, endDate, pi,
										reservoirPressure, holdUPV, ffv, chokeMultiplier, user, true);
								LOGGER.info("Finished running " + vresToRun + " for " + stringID + " from " + startDate
										+ " to " + endDate);
							} catch (SQLException e) {
								LOGGER.log(Level.SEVERE, e.getMessage());
							} catch (NamingException e) {
								LOGGER.log(Level.SEVERE, e.getMessage());
							}
						}
					};
					new Thread(r).start();
				}
			}
		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE, e.getMessage());
			e.printStackTrace();
		} catch (NamingException e) {
			LOGGER.log(Level.SEVERE, e.getMessage());
			e.printStackTrace();
		}

		return message;

	}

	/**
	 * Refresh proxy models.
	 */
	public void refreshProxyModels() {
		try {
			Runnable r = new Runnable() {
				public void run() {
					try (final Connection vreConn = getVREConnection()) {
						LOGGER.info("Refresh proxy models started !!!");
						VREExecutioner vreEx = new VREExecutioner();
						vreEx.refreshProxyModels(vreConn);
						LOGGER.info("Refresh proxy models finished !!!");
					} catch (SQLException e) {
						LOGGER.log(Level.SEVERE, e.getMessage());
						e.printStackTrace();
					} catch (NamingException e) {
						LOGGER.log(Level.SEVERE, e.getMessage());
						e.printStackTrace();
					}
				}
			};
			new Thread(r).start();
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, e.getMessage());
			e.printStackTrace();
		}
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
	 * @return the string
	 */
	public String runMultiRateWellTest(int stringID, Double[] liqRates, Double[] whps, double wcut) {
		String message = "Error calculating multirate well test. Please try again later";
		try (Connection vreConn = getVREConnection()) {
			LOGGER.info("Started running MultirateWellTest for - " + stringID);
			VREExecutioner vreEx = new VREExecutioner();
			MultiRateTestModel multiRateTest = vreEx.runMultiRateWellTest(vreConn, stringID, liqRates, whps, wcut);
			Gson gson = new Gson();
			if (multiRateTest != null) {
				message = gson.toJson(multiRateTest);
			} else {
				message = gson.toJson(message);
			}
			LOGGER.info(message + " \nMultirateWellTest finished for - " + stringID);
		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE, e.getMessage());
			e.printStackTrace();
		} catch (NamingException e) {
			LOGGER.log(Level.SEVERE, e.getMessage());
			e.printStackTrace();
		}
		return message;
	}

	/**
	 * Run model prediction.
	 */
	public void runModelPrediction() {
		try {
			Runnable r = new Runnable() {
				public void run() {
					try (final Connection vreConn = getVREConnection()) {
						LOGGER.info("Started Running model prediction !!!");
						Timestamp currTime = Utils.getRoundedOffTime(new Timestamp(new Date().getTime()),
								PREDICTION_FREQUENCY);
						VREExecutioner vreEx = new VREExecutioner();
						vreEx.runModelPrediction(vreConn, currTime);
						LOGGER.info("Model prediction finished !!!");
					} catch (SQLException e) {
						LOGGER.log(Level.SEVERE, e.getMessage());
						e.printStackTrace();
					} catch (NamingException e) {
						LOGGER.log(Level.SEVERE, e.getMessage());
						e.printStackTrace();
					}
				}
			};
			new Thread(r).start();
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, e.getMessage());
			e.printStackTrace();
		}
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
	 * @return the string
	 */
	public String runVREPostRecalibration(final int stringID, final int wellTestID, final Timestamp wellTestDate,
			final String user, final boolean calibrateFlag) {

		String message = "Recalibration process started for - " + stringID;
		final Timestamp startDate = Utils.getDayFromTimestamp(wellTestDate);
		Timestamp currDate = Utils.getDayFromTimestamp(new Timestamp(new Date().getTime()));
		boolean isHistoric = (Utils.getDifferenceBetweenTwoDates(wellTestDate, currDate) > RECAL_DATE_DIFF) ? true
				: false;
		final Timestamp endDate = isHistoric ? startDate : currDate;

		try (Connection vreConn = getVREConnection()) {
			StringModel sm = Utils.getStringModel(vreConn, stringID);
			if (sm == null) {
				message = "Invalid stringID";
			} else {
				boolean isRunning = false;
				VREExecutioner vreEx = new VREExecutioner();
				message = vreEx.isRunning(vreConn, sm);

				if (message != null) {
					isRunning = true;
				} else {
					message = "Started running recalibration for " + sm.getStringName() + " from " + startDate + " to "
							+ endDate;
				}
				if (!isRunning) {
					Runnable r = new Runnable() {
						public void run() {
							LOGGER.info(
									"Running recalibration for " + stringID + " from " + startDate + " to " + endDate);
							try (final Connection vreConn = getVREConnection()) {
								VREExecutioner vreEx = new VREExecutioner();
								vreEx.runVREPostRecalibration(vreConn, stringID, wellTestID, wellTestDate, user, false,
										calibrateFlag);
								LOGGER.info("Finished running recalibration for " + stringID + " from " + startDate
										+ " to " + endDate);
							} catch (SQLException e) {
								LOGGER.log(Level.SEVERE, e.getMessage());
							} catch (NamingException e) {
								LOGGER.log(Level.SEVERE, e.getMessage());
							}
						}
					};
					new Thread(r).start();
				}
			}
		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE, e.getMessage());
			e.printStackTrace();
		} catch (NamingException e) {
			LOGGER.log(Level.SEVERE, e.getMessage());
			e.printStackTrace();
		}

		return message;
	}

	/**
	 * Populate technical rate.
	 *
	 * @param recordedDate
	 *            the recorded date
	 */
	public void populateTechnicalRate(Timestamp recordedDate) {
		try (Connection vreConn = getVREConnection(); Connection phdConn = getPHDConnection();) {
			LOGGER.info("Populate technical rates started for - " + recordedDate);
			RatePopulator rp = new RatePopulator();
			rp.populateTechnicalRates(vreConn, phdConn, recordedDate);
			LOGGER.info("Populate technical rates finished for - " + recordedDate);
		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE, e.getMessage());
			e.printStackTrace();
		} catch (NamingException e) {
			LOGGER.log(Level.SEVERE, e.getMessage());
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			LOGGER.log(Level.SEVERE, e.getMessage());
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Populate satellite measured rates.
	 *
	 * @param recordedDate the recorded date
	 */
	public void populateSatelliteMeasuredRates(Timestamp recordedDate) {
		try (Connection vreConn = getVREConnection(); Connection phdConn = getPHDConnection();) {
			LOGGER.info("Populate satellite measured rates started for - " + recordedDate);
			RatePopulator rp = new RatePopulator();
			rp.populateSatelliteMeasuredRates(vreConn, phdConn, recordedDate);
			LOGGER.info("Populate satellite measured finished for - " + recordedDate);
		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE, e.getMessage());
			e.printStackTrace();
		} catch (NamingException e) {
			LOGGER.log(Level.SEVERE, e.getMessage());
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			LOGGER.log(Level.SEVERE, e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * Run injection calibration.
	 *
	 * @param recordedDate
	 *            the recorded date
	 */
	public void runInjectionCalibration(Timestamp recordedDate) {
		try (Connection vreConn = getVREConnection()) {
			LOGGER.info("run injection calibration started for - " + recordedDate);
			VREExecutioner vreEx = new VREExecutioner();
			vreEx.runInjectionCalibration(vreConn, recordedDate);
			LOGGER.info("run injection calibration finished for - " + recordedDate);
		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE, e.getMessage());
			e.printStackTrace();
		} catch (NamingException e) {
			LOGGER.log(Level.SEVERE, e.getMessage());
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Populate WHP at tech rate.
	 *
	 * @param recordedDate the recorded date
	 */
	public void populateWHPAtTechRate(Timestamp recordedDate) {
		try (Connection vreConn = getVREConnection(); Connection phdConn = getPHDConnection();) {
			LOGGER.info("Populate WHP at technical rate started for - " + recordedDate);
			VREExecutioner vreEx = new VREExecutioner();
			vreEx.populateWHPAtTechRate(vreConn, recordedDate);
			LOGGER.info("Populate WHP at technical rate finished for - " + recordedDate);
		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE, e.getMessage());
			e.printStackTrace();
		} catch (NamingException e) {
			LOGGER.log(Level.SEVERE, e.getMessage());
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			LOGGER.log(Level.SEVERE, e.getMessage());
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Populate max flow rate pressure.
	 *
	 * @param recordedDate the recorded date
	 */
	public void populateMaxFlowRatePressure(Timestamp recordedDate) {
		try (Connection vreConn = getVREConnection(); Connection phdConn = getPHDConnection();) {
			LOGGER.info("Populate Max flow rate pressure started for - " + recordedDate);
			VREExecutioner vreEx = new VREExecutioner();
			vreEx.populateMaxFlowRatePressure(vreConn, recordedDate);
			LOGGER.info("Populate Max flow rate pressure finished for - " + recordedDate);
		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE, e.getMessage());
			e.printStackTrace();
		} catch (NamingException e) {
			LOGGER.log(Level.SEVERE, e.getMessage());
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			LOGGER.log(Level.SEVERE, e.getMessage());
			e.printStackTrace();
		}
	}

}