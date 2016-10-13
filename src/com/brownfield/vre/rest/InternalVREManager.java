package com.brownfield.vre.rest;

import static com.brownfield.vre.VREConstants.PHD_TEIID_URL;
import static com.brownfield.vre.VREConstants.TEIID_DRIVER_NAME;
import static com.brownfield.vre.VREConstants.TEIID_PASSWORD;
import static com.brownfield.vre.VREConstants.TEIID_USER;
import static com.brownfield.vre.VREConstants.VRE_EXE_RUNNING_QUERY;
import static com.brownfield.vre.VREConstants.VRE_JNDI_NAME;
import static com.brownfield.vre.VREConstants.FROM_DATE;
import static com.brownfield.vre.VREConstants.TO_DATE;
import static com.brownfield.vre.VREConstants.STARTED_ON;
import static com.brownfield.vre.VREConstants.CURRENT_COUNTER;
import static com.brownfield.vre.VREConstants.ROW_CREATED_BY;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import com.brownfield.vre.AvgCalculator;
import com.brownfield.vre.Utils;
import com.brownfield.vre.VREConstants;
import com.brownfield.vre.ValidateWellTest;
import com.brownfield.vre.exe.VREExecutioner;
import com.brownfield.vre.exe.models.MultiRateTestModel;
import com.brownfield.vre.jobs.JobsMonitor;
import com.google.gson.Gson;

// TODO: Auto-generated Javadoc
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
			VREConstants.refreshVariables(vreConn);
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
	 */
	public void runCalibration() {
		try (Connection vreConn = getVREConnection()) {
			LOGGER.info("Run calibrations started !!!");
			VREExecutioner vreEx = new VREExecutioner();
			vreEx.runCalibration(vreConn);
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
			// TODO :StringID is valid and job is not running
			String stringName = Utils.getStringNameFromID(vreConn, stringID);
			if (stringName == null) {
				message = "Invalid stringID";
			} else {
				message = "Started running " + vresToRun + " for " + stringName + " from " + startDate + " to "
						+ endDate;
				boolean isRunning = false;
				Timestamp fromDate, toDate, startedOn;
				String currentUser;
				int currentCounter;
				try (PreparedStatement statement = vreConn.prepareStatement(VRE_EXE_RUNNING_QUERY);) {
					statement.setInt(1, stringID);
					try (ResultSet rset = statement.executeQuery();) {
						if (rset != null && rset.next()) {
							isRunning = true;
							fromDate = rset.getTimestamp(FROM_DATE);
							toDate = rset.getTimestamp(TO_DATE);
							startedOn = rset.getTimestamp(STARTED_ON);
							currentCounter = rset.getInt(CURRENT_COUNTER);
							currentUser = rset.getString(ROW_CREATED_BY);
							message = "Another recalculation for " + stringName + " is already running from : "
									+ fromDate + " to : " + toDate + " initiated by user : " + currentUser + " on "
									+ startedOn;
							message += ".\n The process has already executed for " + currentCounter
									+ " days. Kindly wait for it to finish and then try again.";
						}
					} catch (Exception e) {
						LOGGER.severe(e.getMessage());
						e.printStackTrace();
					}
				} catch (Exception e) {
					LOGGER.severe(e.getMessage());
					e.printStackTrace();
				}

				if (!isRunning) {
					Runnable r = new Runnable() {
						public void run() {
							LOGGER.info("Running " + vresToRun + " for " + stringID + " from " + startDate + " to "
									+ endDate);
							try (final Connection vreConn = getVREConnection()) {
								VREExecutioner vreEx = new VREExecutioner();
								vreEx.runVREForDuration(vreConn, stringID, vresToRun, startDate, endDate, pi,
										reservoirPressure, holdUPV, ffv, chokeMultiplier, user);
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

}