package com.brownfield.vre.rest;

import static com.brownfield.vre.VREConstants.PHD_TEIID_URL;
import static com.brownfield.vre.VREConstants.TEIID_DRIVER_NAME;
import static com.brownfield.vre.VREConstants.TEIID_PASSWORD;
import static com.brownfield.vre.VREConstants.TEIID_USER;
import static com.brownfield.vre.VREConstants.VRE_JNDI_NAME;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import com.brownfield.vre.ValidateWellTest;

/**
 * The Class InternalVREManager.
 * 
 * @author Onkar Dhuri <onkar.dhuri@synerzip.com>
 */
public class InternalVREManager {

	/** The logger. */
	private static Logger LOGGER = Logger.getLogger(InternalVREManager.class.getName());

	/**
	 * Gets the PHD connection.
	 *
	 * @return the PHD connection
	 */
	private Connection getPHDConnection() {
		Connection result = null;
		try {
			Class.forName(TEIID_DRIVER_NAME);
			result = DriverManager.getConnection(PHD_TEIID_URL, TEIID_USER, TEIID_PASSWORD);
		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE, e.getMessage());
		} catch (ClassNotFoundException e) {
			LOGGER.log(Level.SEVERE, e.getMessage());
		}
		return result;
	}

	/**
	 * Gets the VRE connection.
	 *
	 * @return the VRE connection
	 */
	private Connection getVREConnection() {
		Connection result = null;
		try {
			Context initialContext = new InitialContext();
			DataSource datasource = (DataSource) initialContext.lookup(VRE_JNDI_NAME);
			if (datasource != null) {
				result = datasource.getConnection();
			} else {
				LOGGER.log(Level.SEVERE, "Failed to lookup datasource.");
			}
		} catch (NamingException ex) {
			LOGGER.log(Level.SEVERE, "Cannot get connection: " + ex);
		} catch (SQLException ex) {
			LOGGER.log(Level.SEVERE, "Cannot get connection: " + ex);
		}
		return result;
	}

	/**
	 * Validate new tests.
	 */
	public void validateNewTests() {
		try (Connection vreConn = getVREConnection(); Connection phdConn = getPHDConnection()) {

			if (vreConn != null) {
				if (phdConn != null) {
					ValidateWellTest vwt = new ValidateWellTest();
					vwt.validateNewWellTests(vreConn, phdConn);
				} else {
					LOGGER.log(Level.SEVERE, "Can not validate Wells as PHD connection is unavailable");
				}
			} else {
				LOGGER.log(Level.SEVERE, "Can not validate Wells as VRE connection is unavailable");
			}

		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE, e.getMessage());
		}
	}

}