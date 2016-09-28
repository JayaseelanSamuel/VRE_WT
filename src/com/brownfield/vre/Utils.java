package com.brownfield.vre;

import static com.brownfield.vre.VREConstants.COMPLETION_DATE;
import static com.brownfield.vre.VREConstants.CURRENT_STATUS;
import static com.brownfield.vre.VREConstants.DATE_FORMAT;
import static com.brownfield.vre.VREConstants.DATE_TIME_FORMAT;
import static com.brownfield.vre.VREConstants.GET_STRING_METADATA_QUERY;
import static com.brownfield.vre.VREConstants.GET_STRING_NAME_QUERY;
import static com.brownfield.vre.VREConstants.LATITUDE;
import static com.brownfield.vre.VREConstants.LONGITUDE;
import static com.brownfield.vre.VREConstants.PIPESIM_MODEL_LOC;
import static com.brownfield.vre.VREConstants.PLATFORM_ID;
import static com.brownfield.vre.VREConstants.PLATFORM_NAME;
import static com.brownfield.vre.VREConstants.STABILITY_FLAG;
import static com.brownfield.vre.VREConstants.STRING_ID;
import static com.brownfield.vre.VREConstants.STRING_NAME;
import static com.brownfield.vre.VREConstants.STRING_TYPE;
import static com.brownfield.vre.VREConstants.TAG_ANN_PRESSURE_A;
import static com.brownfield.vre.VREConstants.TAG_ANN_PRESSURE_B;
import static com.brownfield.vre.VREConstants.TAG_CHOKE_SIZE;
import static com.brownfield.vre.VREConstants.TAG_DOWNHOLE_PRESSURE;
import static com.brownfield.vre.VREConstants.TAG_GASLIFT_INJ_RATE;
import static com.brownfield.vre.VREConstants.TAG_GAS_RATE;
import static com.brownfield.vre.VREConstants.TAG_HEADER_PRESSURE;
import static com.brownfield.vre.VREConstants.TAG_INJ_HEADER_PRESSURE;
import static com.brownfield.vre.VREConstants.TAG_LIQUID_RATE;
import static com.brownfield.vre.VREConstants.TAG_OIL_VOL_RATE;
import static com.brownfield.vre.VREConstants.TAG_WATERCUT;
import static com.brownfield.vre.VREConstants.TAG_WATER_VOL_RATE;
import static com.brownfield.vre.VREConstants.TAG_WHP;
import static com.brownfield.vre.VREConstants.TAG_WHT;
import static com.brownfield.vre.VREConstants.UWI;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import com.brownfield.vre.exe.models.StringModel;

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
	 * Convert date.
	 *
	 * @param date
	 *            the date
	 * @param format
	 *            the format
	 * @param shiftTimeZone
	 *            the shift time zone
	 * @return the timestamp
	 */
	public static Timestamp getDateFromString(String date, String format, boolean shiftTimeZone) {
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		Date shiftDate = new Date();
		try {
			if (shiftTimeZone) {
				TimeZone utcTZ = TimeZone.getTimeZone("GST");
				sdf.setTimeZone(utcTZ);
			}
			shiftDate = sdf.parse(date);
			return new Timestamp(shiftDate.getTime());
		} catch (ParseException e) {
			LOGGER.severe("Can not parse " + date + " using format " + format);
		}
		return null;
	}

	/**
	 * Gets the date from string.
	 *
	 * @param date
	 *            the date
	 * @param shiftTimeZone
	 *            the shift time zone
	 * @return the date from string
	 */
	public static Timestamp getDateFromString(String date, boolean shiftTimeZone) {
		return getDateFromString(date, DATE_TIME_FORMAT, shiftTimeZone);
	}

	/**
	 * Parses the date with either date format or date time format
	 *
	 * @param date
	 *            the date
	 * @return the timestamp
	 */
	public static Timestamp parseDate(String date) {
		Timestamp t = getDateFromString(date, DATE_TIME_FORMAT, Boolean.FALSE);
		if (t != null) {
			return t;
		} else {
			return getDateFromString(date, DATE_FORMAT, Boolean.FALSE);
		}
	}

	/**
	 * Gets the yesterday date string.
	 *
	 * @return the yesterday date string
	 */
	public static String getYesterdayString() {
		SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
		/*
		 * Calendar cal = Calendar.getInstance(); cal.add(Calendar.DATE, -1);
		 * return dateFormat.format(cal.getTime());
		 */
		Timestamp yesterday = Utils.getYesterdayTimestamp();
		return dateFormat.format(yesterday.getTime());
	}

	/**
	 * Gets the yesterday timestamp.
	 *
	 * @return the yesterday timestamp
	 */
	public static Timestamp getYesterdayTimestamp() {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, -1);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return new Timestamp(cal.getTimeInMillis());
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
		try (PreparedStatement statement = vreConn.prepareStatement(GET_STRING_NAME_QUERY);) {
			statement.setInt(1, stringID);
			try (ResultSet rset = statement.executeQuery();) {
				if (rset != null && rset.next()) { // always one row 
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

	/**
	 * Gets the string model.
	 *
	 * @param vreConn
	 *            the vre conn
	 * @param stringID
	 *            the string id
	 * @return the string model
	 */
	public static StringModel getStringModel(Connection vreConn, int stringID) {
		StringModel sm = null;
		try (PreparedStatement statement = vreConn.prepareStatement(GET_STRING_METADATA_QUERY);) {
			statement.setInt(1, stringID);
			try (ResultSet rset = statement.executeQuery();) {
				if (rset != null && rset.next()) { // always one row
					sm = new StringModel();
					sm.setStringID(rset.getInt(STRING_ID));
					sm.setUwi(rset.getString(UWI));
					sm.setStringType(rset.getString(STRING_TYPE));
					sm.setStringName(rset.getString(STRING_NAME));
					sm.setCompletionDate(rset.getTimestamp(COMPLETION_DATE));
					sm.setLatitude(rset.getDouble(LATITUDE));
					sm.setLongitude(rset.getDouble(LONGITUDE));
					sm.setCurrentStatus(rset.getString(CURRENT_STATUS));
					sm.setPlatformID(rset.getInt(PLATFORM_ID));
					sm.setPlatformName(rset.getString(PLATFORM_NAME));
					// string tags
					sm.setTagWHP(rset.getString(TAG_WHP));
					sm.setTagWHT(rset.getString(TAG_WHT));
					sm.setTagChokeSize(rset.getString(TAG_CHOKE_SIZE));
					sm.setTagDownholePressure(rset.getString(TAG_DOWNHOLE_PRESSURE));
					sm.setTagGasliftInjRate(rset.getString(TAG_GASLIFT_INJ_RATE));
					sm.setTagWaterVolRate(rset.getString(TAG_WATER_VOL_RATE));
					sm.setTagOilVolRate(rset.getString(TAG_OIL_VOL_RATE));
					sm.setTagAnnPressureA(rset.getString(TAG_ANN_PRESSURE_A));
					sm.setTagAnnPressureB(rset.getString(TAG_ANN_PRESSURE_B));
					sm.setPipesimModelLoc(rset.getString(PIPESIM_MODEL_LOC));
					sm.setStable(rset.getBoolean(STABILITY_FLAG));
					// platform tags
					sm.setTagLiquidRate(rset.getString(TAG_LIQUID_RATE));
					sm.setTagGasRate(rset.getString(TAG_GAS_RATE));
					sm.setTagWatercut(rset.getString(TAG_WATERCUT));
					sm.setTagHeaderPressure(rset.getString(TAG_HEADER_PRESSURE));
					sm.setTagInjHeaderPressure(rset.getString(TAG_INJ_HEADER_PRESSURE));
				}
			} catch (Exception e) {
				LOGGER.severe(e.getMessage());
			}
		} catch (Exception e) {
			LOGGER.severe(e.getMessage());
		}
		return sm;
	}

	/**
	 * Gets the next or previous day.
	 *
	 * @param testDate
	 *            the test date
	 * @param offset
	 *            the offset
	 * @return the next or previous day
	 */
	public static Timestamp getNextOrPreviousDay(Timestamp testDate, int offset) {
		// Use the Calendar class to add/subtract days
		Calendar cal = Calendar.getInstance();
		cal.setTime(testDate);
		cal.add(Calendar.DATE, offset);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return new Timestamp(cal.getTimeInMillis());
	}

	/**
	 * Checks if is within limit.
	 *
	 * @param number1
	 *            the number1
	 * @param number2
	 *            the number2
	 * @param percentLimit
	 *            the percent limit
	 * @return true, if is within limit
	 */
	public static boolean isWithinLimit(double number1, double number2, double percentLimit) {
		if (number2 != 0) {
			double percentDiff = Math.abs((number1 - number2) * 100 / number2);
			return percentDiff <= percentLimit;
		}
		return false;
	}

	/**
	 * Gets the difference between two dates.
	 *
	 * @param startDate
	 *            the start date
	 * @param endDate
	 *            the end date
	 * @return the difference between two dates
	 */
	public static long getDifferenceBetweenTwoDates(Timestamp startDate, Timestamp endDate) {
		long duration = endDate.getTime() - startDate.getTime();
		long diffInDays = TimeUnit.MILLISECONDS.toDays(duration);
		return diffInDays + 1;
	}

}
