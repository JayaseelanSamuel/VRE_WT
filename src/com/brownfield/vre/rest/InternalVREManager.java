package com.brownfield.vre.rest;

import static com.brownfield.vre.VREConstants.PHD_TEIID_URL;
import static com.brownfield.vre.VREConstants.TEIID_DRIVER_NAME;
import static com.brownfield.vre.VREConstants.TEIID_PASSWORD;
import static com.brownfield.vre.VREConstants.TEIID_USER;
import static com.brownfield.vre.VREConstants.VRE_JNDI_NAME;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import com.brownfield.vre.AvgCalculator;
import com.brownfield.vre.VREConstants;
import com.brownfield.vre.ValidateWellTest;
import com.brownfield.vre.exe.VREExecutioner;
import com.brownfield.vre.jobs.JobsMonitor;

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
		} catch (NamingException e) {
			LOGGER.log(Level.SEVERE, e.getMessage());
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
		} catch (NamingException e) {
			LOGGER.log(Level.SEVERE, e.getMessage());
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
		} catch (NamingException e) {
			LOGGER.log(Level.SEVERE, e.getMessage());
		} catch (ClassNotFoundException e) {
			LOGGER.log(Level.SEVERE, e.getMessage());
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
		} catch (NamingException e) {
			LOGGER.log(Level.SEVERE, e.getMessage());
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
		} catch (NamingException e) {
			LOGGER.log(Level.SEVERE, e.getMessage());
		}
	}

	/**
	 * Run vres.
	 */
	public void runVREs(Timestamp recordedDate) {
		try (Connection vreConn = getVREConnection()) {
			LOGGER.info("run VREs started !!!");
			VREExecutioner vreEx = new VREExecutioner();
			vreEx.runVREs(vreConn, recordedDate);
			LOGGER.info("run VREs finished !!!");
		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE, e.getMessage());
		} catch (NamingException e) {
			LOGGER.log(Level.SEVERE, e.getMessage());
		}
	}

}