package com.brownfield.vre;

import static com.brownfield.vre.VREConstants.*;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import com.brownfield.vre.VREConstants.VRE_TYPE;
import com.brownfield.vre.exe.models.StringModel;
import com.brownfield.vre.exe.models.VREDBModel;

// TODO: Auto-generated Javadoc
/**
 * The Class Utils.
 * 
 * @author Onkar Dhuri <onkar.dhuri@synerzip.com>
 */
public class Utils {

	/** The logger. */
	private static final Logger LOGGER = Logger.getLogger(Utils.class.getName());

	/**
	 * The main method.
	 *
	 * @param args
	 *            the arguments
	 */
	public static void main(String[] args) {
		Timestamp curr = new Timestamp(new Date().getTime());
		Timestamp rounded = Utils.getRoundedOffTime(curr, 30);
		System.out.println("Current : " + curr);
		System.out.println("Rounded : " + rounded);
	}

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
			LOGGER.warning("Can not parse " + date + " using format " + format);
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
	 * Parses the date with either date format or date time format.
	 *
	 * @param date
	 *            the date
	 * @return the timestamp
	 */
	public static Timestamp parseDate(String date) {
		Timestamp t = getDateFromString(date, DATE_FORMAT, Boolean.FALSE);
		if (t != null) {
			return t;
		} else {
			return getDateFromString(date, DATE_TIME_FORMAT, Boolean.FALSE);
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
	 * Convert to string.
	 *
	 * @param date
	 *            the date
	 * @param format
	 *            the format
	 * @return the string
	 */
	public static String convertToString(Timestamp date, String format) {
		return new SimpleDateFormat(format).format(date.getTime());
	}

	/**
	 * Convert to string.
	 *
	 * @param date
	 *            the date
	 * @param format
	 *            the format
	 * @return the string
	 */
	public static String convertToString(Date date, String format) {
		return new SimpleDateFormat(format).format(date.getTime());
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
				e.printStackTrace();
			}
		} catch (Exception e) {
			LOGGER.severe(e.getMessage());
			e.printStackTrace();
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
					sm.setStringCategoryID(rset.getInt(STRING_CATEGORY_ID));
					sm.setCompletionDate(rset.getTimestamp(COMPLETION_DATE));
					sm.setLatitude(rset.getDouble(LATITUDE));
					sm.setLongitude(rset.getDouble(LONGITUDE));
					sm.setCurrentStatus(rset.getString(CURRENT_STATUS));
					sm.setSelectedVRE(VRE_TYPE.valueOf(rset.getString(SELECTED_VRE)));
					sm.setPlatformID(rset.getInt(PLATFORM_ID));
					sm.setPlatformName(rset.getString(PLATFORM_NAME));
					// string tags
					sm.setTagWHP(rset.getString(TAG_WHP));
					sm.setTagWHT(rset.getString(TAG_WHT));
					sm.setTagChokeSize(rset.getString(TAG_CHOKE_SIZE));
					sm.setTagDownholePressure(rset.getString(TAG_DOWNHOLE_PRESSURE));
					sm.setTagWaterInjRate(rset.getString(TAG_WATER_INJ_RATE));
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
					sm.setTagSeparatorPressure(rset.getString(TAG_SEPARATOR_PRESSURE));
				}
			} catch (Exception e) {
				LOGGER.severe(e.getMessage());
				e.printStackTrace();
			}
		} catch (Exception e) {
			LOGGER.severe(e.getMessage());
			e.printStackTrace();
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
	 * Gets the next or previous day.
	 *
	 * @param testDate
	 *            the test date
	 * @param offset
	 *            the offset
	 * @return the next or previous day
	 */
	public static Timestamp getNextOrPreviousDay(Date testDate, int offset) {
		return Utils.getNextOrPreviousDay(new Timestamp(testDate.getTime()), offset);
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

	/**
	 * Gets the rounded off time.
	 *
	 * @param date
	 *            the date
	 * @param interval
	 *            the interval
	 * @return the rounded off time
	 */
	public static Timestamp getRoundedOffTime(Timestamp date, int interval) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		int unroundedMinutes = cal.get(Calendar.MINUTE);
		int mod = unroundedMinutes % interval;
		cal.add(Calendar.MINUTE, mod < (interval / 2) ? -mod : (interval - mod));
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return new Timestamp(cal.getTimeInMillis());
	}

	/**
	 * Gets the day from timestamp.
	 *
	 * @param date
	 *            the date
	 * @return the day from timestamp
	 */
	public static Timestamp getDayFromTimestamp(Timestamp date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return new Timestamp(cal.getTimeInMillis());
	}

	/**
	 * Gets the start of the month.
	 *
	 * @param date
	 *            the date
	 * @return the start of the month
	 */
	public static Timestamp getStartOfTheMonth(Timestamp date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH));
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return new Timestamp(cal.getTimeInMillis());
	}

	/**
	 * Gets the VREDB model.
	 *
	 * @param vreConn
	 *            the vre conn
	 * @param stringID
	 *            the string ID
	 * @param recordedDate
	 *            the recorded date
	 * @return the VREDB model
	 */
	public static VREDBModel getVREDBModel(Connection vreConn, int stringID, Timestamp recordedDate) {
		VREDBModel vm = null;
		try (PreparedStatement statement = vreConn.prepareStatement(VRE_TABLE_SELECT_QUERY);) {
			statement.setTimestamp(1, recordedDate);
			statement.setInt(2, stringID);
			try (ResultSet rset = statement.executeQuery();) {
				if (rset != null && rset.next()) { // always one row
					vm = new VREDBModel();
					// vm.setVreID(rset.getInt(VRE_ID));
					vm.setStringID(rset.getInt(STRING_ID));
					vm.setVre1(rset.getDouble(VRE1));
					vm.setVre2(rset.getDouble(VRE2));
					vm.setVre3(rset.getDouble(VRE3));
					vm.setVre4(rset.getDouble(VRE4));
					vm.setVre5(rset.getDouble(VRE5));
					vm.setVre6(rset.getDouble(VRE6));
					vm.setGor(rset.getDouble(GOR));
					vm.setPi(rset.getDouble(PI));
					vm.setFrictionFactor(rset.getDouble(FRICTION_FACTOR));
					vm.setReservoirPressure(rset.getDouble(RESERVOIR_PRESSURE));
					vm.setChokeMultiplier(rset.getDouble(CHOKE_MULTIPLIER));
				}
			} catch (Exception e) {
				LOGGER.severe(e.getMessage());
				e.printStackTrace();
			}
		} catch (Exception e) {
			LOGGER.severe(e.getMessage());
			e.printStackTrace();
		}
		return vm;
	}

	/**
	 * Refresh variables.
	 *
	 * @param conn
	 *            the conn
	 */
	public static void refreshVariables(Connection conn) {
		try (Statement stmt = conn.createStatement(); ResultSet rset = stmt.executeQuery(VRE_VARIABLE_QUERY);) {
			if (rset != null) {
				LOGGER.info("Refreshing variables...");
				while (rset.next()) {
					String name = rset.getString(NAME);
					String val = rset.getString(VALUE);
					switch (name) {
					case "START_OFFSET":
						START_OFFSET = Double.parseDouble(val);
						break;
					case "END_OFFSET":
						END_OFFSET = Double.parseDouble(val);
						break;
					case "SWITCH_TIME_ZONE":
						SWITCH_TIME_ZONE = val.equalsIgnoreCase(Boolean.TRUE.toString()) ? true : false;
						break;
					case "MIN_WHP":
						MIN_WHP = Double.parseDouble(val);
						break;
					case "MAX_WHP":
						MAX_WHP = Double.parseDouble(val);
						break;
					case "MIN_LIQUID_RATE":
						MIN_LIQUID_RATE = Double.parseDouble(val);
						break;
					case "MAX_LIQUID_RATE":
						MAX_LIQUID_RATE = Double.parseDouble(val);
						break;
					case "MIN_WATERCUT":
						MIN_WATERCUT = Double.parseDouble(val);
						break;
					case "MAX_WATERCUT":
						MAX_WATERCUT = Double.parseDouble(val);
						break;
					case "FREEZE_WHP_LIMIT":
						FREEZE_WHP_LIMIT = Double.parseDouble(val);
						break;
					case "FREEZE_LIQUID_RATE_LIMIT":
						FREEZE_LIQUID_RATE_LIMIT = Double.parseDouble(val);
						break;
					case "FREEZE_WATERCUT_LIMIT":
						FREEZE_WATERCUT_LIMIT = Double.parseDouble(val);
						break;
					case "CV_WHP_MAX":
						CV_WHP_MAX = Double.parseDouble(val);
						break;
					case "CV_LIQ_RATE_MAX":
						CV_LIQ_RATE_MAX = Double.parseDouble(val);
						break;
					case "CV_WATERCUT_MAX":
						CV_WATERCUT_MAX = Double.parseDouble(val);
						break;
					case "SHRINKAGE_FACTOR":
						SHRINKAGE_FACTOR = Double.parseDouble(val);
						break;
					case "VRE_EXE_LOC":
						VRE_EXE_LOC = val;
						break;
					case "CONCURRENT_PIPESIM_LICENCES":
						CONCURRENT_PIPESIM_LICENCES = Integer.parseInt(val);
						break;
					case "RECALIBRATE_LOW":
						RECALIBRATE_LOW = Double.parseDouble(val);
						break;
					case "RECALIBRATE_HIGH":
						RECALIBRATE_HIGH = Double.parseDouble(val);
						break;
					case "WT_TREND_LIMIT":
						WT_TREND_LIMIT = Double.parseDouble(val);
						break;
					case "MAX_RATE_DIFF":
						MAX_RATE_DIFF = Double.parseDouble(val);
						break;
					case "MAX_INJ_RATE_PRESS":
						MAX_INJ_RATE_PRESS = Double.parseDouble(val);
						break;
					case "RECAL_DATE_DIFF":
						RECAL_DATE_DIFF = Integer.parseInt(val);
						break;
					case "TECHNICAL_RATE_DIFF":
						TECHNICAL_RATE_DIFF = Double.parseDouble(val);
						break;

					}
				}
			}
		} catch (Exception e) {
			LOGGER.severe(e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * Refresh properties.
	 */
	public static void refreshProperties() {
		try {
			String configDir = System.getProperty(CONFIG_DIR);
			String propertyFileLocation = configDir + File.separator + PROPERTY_FILE_NAME;
			PropertyReader.loadProperties(propertyFileLocation);
			PHD_TEIID_URL = PropertyReader.getProperty("PHD_TEIID_URL");

			TEIID_USER = PropertyReader.getProperty("TEIID_USER");
			TEIID_PASSWORD = PropertyReader.getProperty("TEIID_PASSWORD");
			VRE_JNDI_NAME = PropertyReader.getProperty("VRE_JNDI_NAME");
			DSIS_HOST = PropertyReader.getProperty("DSIS_HOST");
			DSIS_PORT = PropertyReader.getProperty("DSIS_PORT");
			DSBPM_BASE_URL = "http://" + DSIS_HOST + ":" + DSIS_PORT + "/dsbpm-engine/rest";
			APP_BASE_URL = PropertyReader.getProperty("APP_BASE_URL");
			EMAIL_GROUP = PropertyReader.getProperty("EMAIL_GROUP");
			LOGGER.info("Loaded values from properties file.");
		} catch (IOException e) {
			LOGGER.severe(e.getMessage());
			e.printStackTrace();
		}
	}
}
