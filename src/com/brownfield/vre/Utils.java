package com.brownfield.vre;

import static com.brownfield.vre.VREConstants.GET_STRING_INFO_QUERY;
import static com.brownfield.vre.VREConstants.STRING_NAME;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.logging.Logger;

/**
 * The Class Utils.
 * 
 * @author Onkar Dhuri <onkar.dhuri@synerzip.com>
 */
public class Utils {
	
	/** The logger. */
	private static final Logger LOGGER = Logger.getLogger(Utils.class.getName());

	/**
	 * Gets the standard condition rate.
	 *
	 * @param rate
	 *            the rate
	 * @param shrinkFact
	 *            the shrink fact
	 * @param wcut
	 *            the wcut
	 * @return the standard condition rate
	 */
	public static double getStandardConditionRate(double rate, double shrinkFact, double wcut) {
		double scr = rate * shrinkFact * (1 - wcut) + rate * wcut;
		return scr;
	}

	/**
	 * Gets the string name from id.
	 *
	 * @param vreConn
	 *            the vre conn
	 * @param stringID
	 *            the string id
	 * @return the string name from id
	 */
	public static String getStringNameFromID(Connection vreConn, int stringID) {
		String stringName = null;
		try (PreparedStatement statement = vreConn.prepareStatement(GET_STRING_INFO_QUERY);) {
			statement.setInt(1, stringID);
			try (ResultSet rset = statement.executeQuery();) {
				if (rset != null && rset.next()) { // always one row per date

					stringName = rset.getString(STRING_NAME);
				}
			} catch (Exception e) {
				LOGGER.severe(e.getMessage());
			}
		} catch (Exception e) {
			LOGGER.severe(e.getMessage());
		}
		return stringName;
	}

}
