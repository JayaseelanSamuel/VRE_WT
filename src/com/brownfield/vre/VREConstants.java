package com.brownfield.vre;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * The Class VREConstants.
 *
 * @author Onkar Dhuri <onkar.dhuri@synerzip.com>
 */
public class VREConstants {

	/** The teiid driver name. */
	public static final String TEIID_DRIVER_NAME = "org.teiid.jdbc.TeiidDriver";

	// Properties which will be overridden at the application context load
	
	/** The phd teiid url. */
	public static String PHD_TEIID_URL = "<SERVER_URL>";

	/** The teiid user. */
	public static String TEIID_USER = "<USER>";

	/** The teiid password. */
	public static String TEIID_PASSWORD = "<PASSWORD>";

	/** The vre jndi name. */
	public static String VRE_JNDI_NAME = "java:/VRE";

	// VRE Variables

	/** The start offset. */
	public static double START_OFFSET = -135;

	/** The end offset. */
	public static double END_OFFSET = -15;

	/** The switch time zone. */
	public static boolean SWITCH_TIME_ZONE = true;

	/** The min whp. */
	public static double MIN_WHP = -10;

	/** The max whp. */
	public static double MAX_WHP = 20000;

	/** The min liquid rate. */
	public static double MIN_LIQUID_RATE = -10;

	/** The max liquid rate. */
	public static double MAX_LIQUID_RATE = 20000;

	/** The freeze whp limit. */
	public static double FREEZE_WHP_LIMIT = 10;

	/** The freeze liquid rate limit. */
	public static double FREEZE_LIQUID_RATE_LIMIT = 10;

	/** The cv liq rate max. */
	public static double CV_LIQ_RATE_MAX = 0.08;

	/** The cv whp max. */
	public static double CV_WHP_MAX = 0.08;

	/** The shrinkage factor. */
	public static double SHRINKAGE_FACTOR = 0.95;

	// Properties end

	// Queries

	/** The well test new query. */
	// identify new well test by looking for FT testType and Null flag. We will then update VRE_FLAG to false (to
	// exclude it in next run) and update QL1 to standard conditions
	public static final String WELL_TEST_NEW_QUERY = "SELECT STRING_ID, QL1, WHP1, TEST_START_DATE, TEST_END_DATE, TRY_CONVERT(VARCHAR(10), TEST_END_DATE, 120) AS EFFECTIVE_DATE, "
			+ " TEST_TYPE, VRE_FLAG, ROW_CHANGED_BY, ROW_CHANGED_DATE "
			+ " FROM WELL_TEST WHERE TEST_TYPE = 'FT' AND VRE_FLAG IS NULL"; // AND SOURCE_ID

	/** The get string tags query. */
	public static final String GET_STRING_TAGS_QUERY = "SELECT S.STRING_ID, S.UWI, S.STRING_TYPE, P.PLATFORM_ID, P.PLATFORM_NAME, P.TAG_LIQUID_RATE, P.TAG_GAS_RATE, "
			+ " SM.TAG_WHP, SM.TAG_WHT FROM STRING S "
			+ " LEFT OUTER JOIN STRING_METADATA SM ON S.STRING_ID = SM.STRING_ID "
			+ " LEFT OUTER JOIN WELL W ON S.UWI = W.UWI "
			+ " LEFT OUTER JOIN PLATFORM P ON P.PLATFORM_ID = W.PLATFORM_ID " + " WHERE S.STRING_ID = ? ";

	/** The phd query. */
	public static final String PHD_QUERY = "SELECT \"timestamp\", \"sourceValues\", \"startTimestamp\", \"tagid\" "
			+ " FROM \"OPCHD.TagValues\" "
			+ " WHERE ((((\"tagid\" = ? ) AND (\"timestamp\" >= ? )) AND (\"timestamp\" < ?)) AND (\"noOfValues\" = '2000'))";

	/** The wcut stability query. */
	public static final String WCUT_STABILITY_QUERY = "SELECT STRING_ID, WATER_CUT_LAB, STABILITY_FLAG, RECORDED_DATE FROM DAILY_ALLOCATED_DATA "
			+ " WHERE STRING_ID = ? AND RECORDED_DATE = TRY_CONVERT(DATETIME, ?, 102)";

	/** The insert well test query. */
	public static final String INSERT_WELL_TEST_QUERY = "INSERT INTO WELL_TEST (STRING_ID, TEST_TYPE, TEST_START_DATE, TEST_END_DATE, SOURCE_ID, QL1, WHP1, TEST_WATER_CUT, VRE_FLAG, REMARK) "
			+ " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

	/** The Constant VRE_VARIABLE_QUERY. */
	public static final String VRE_VARIABLE_QUERY = "SELECT NAME, \"VALUE\" FROM VRE_VARIABLES";

	// other constants

	/** The single rate test. */
	public static final String SINGLE_RATE_TEST = "SR";

	/** The flow test. */
	public static final String FLOW_TEST = "FT";

	/** The source vre. */
	public static final int SOURCE_VRE = 6;

	/** The date time format. */
	public static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss.S";
	
	/** The Constant WTV_WORKFLOW. */
	public static final String WTV_WORKFLOW = "WellTestValidation Workflow";

	// Column Names

	/** The vre flag. */
	public static final String VRE_FLAG = "VRE_FLAG";

	/** The string id. */
	public static final String STRING_ID = "STRING_ID";

	/** The uwi. */
	public static final String UWI = "UWI";

	/** The string type. */
	public static final String STRING_TYPE = "STRING_TYPE";

	/** The platform id. */
	public static final String PLATFORM_ID = "PLATFORM_ID";

	/** The platform name. */
	public static final String PLATFORM_NAME = "PLATFORM_NAME";

	/** The Constant NAME. */
	public static final String NAME = "NAME";

	/** The Constant VALUE. */
	public static final String VALUE = "VALUE";

	/** The Q l1. */
	public static final String QL1 = "QL1";

	/** The WH p1. */
	public static final String WHP1 = "WHP1";

	/** The test start date. */
	public static final String TEST_START_DATE = "TEST_START_DATE";

	/** The test end date. */
	public static final String TEST_END_DATE = "TEST_END_DATE";

	/** The effective date. */
	public static final String EFFECTIVE_DATE = "EFFECTIVE_DATE";

	/** The water cut lab. */
	public static final String WATER_CUT_LAB = "WATER_CUT_LAB";

	/** The stability flag. */
	public static final String STABILITY_FLAG = "STABILITY_FLAG";

	/** The source values. */
	public static final String SOURCE_VALUES = "sourceValues";

	/** The tag liquid rate. */
	public static final String TAG_LIQUID_RATE = "TAG_LIQUID_RATE";

	/** The tag gas rate. */
	public static final String TAG_GAS_RATE = "TAG_GAS_RATE";

	/** The tag watercut. */
	public static final String TAG_WATERCUT = "TAG_WATERCUT";

	/** The tag header pressure. */
	public static final String TAG_HEADER_PRESSURE = "TAG_HEADER_PRESSURE";

	/** The tag inj header pressure. */
	public static final String TAG_INJ_HEADER_PRESSURE = "TAG_INJ_HEADER_PRESSURE";

	/** The tag whp. */
	public static final String TAG_WHP = "TAG_WHP";

	/** The tag wht. */
	public static final String TAG_WHT = "TAG_WHT";

	/** The tag choke size. */
	public static final String TAG_CHOKE_SIZE = "TAG_CHOKE_SIZE";

	/** The tag downhole pressure. */
	public static final String TAG_DOWNHOLE_PRESSURE = "TAG_DOWNHOLE_PRESSURE";

	/** The tag gaslift inj rate. */
	public static final String TAG_GASLIFT_INJ_RATE = "TAG_GASLIFT_INJ_RATE";

	/** The tag water vol rate. */
	public static final String TAG_WATER_VOL_RATE = "TAG_WATER_VOL_RATE";

	/** The tag oil vol rate. */
	public static final String TAG_OIL_VOL_RATE = "TAG_OIL_VOL_RATE";

	/** The tag ann pressure a. */
	public static final String TAG_ANN_PRESSURE_A = "TAG_ANN_PRESSURE_A";

	/** The tag ann pressure b. */
	public static final String TAG_ANN_PRESSURE_B = "TAG_ANN_PRESSURE_B";

	/** The pipesim model loc. */
	public static final String PIPESIM_MODEL_LOC = "PIPESIM_MODEL_LOC";
	
	/** The Constant ROW_CHANGED_BY. */
	public static final String ROW_CHANGED_BY = "ROW_CHANGED_BY";
	
	/** The Constant ROW_CHANGED_DATE. */
	public static final String ROW_CHANGED_DATE = "ROW_CHANGED_DATE";

	/**
	 * Refresh variables.
	 *
	 * @param conn
	 *            the conn
	 */
	public static void refreshVariables(Connection conn) {
		try (Statement stmt = conn.createStatement(); ResultSet rset = stmt.executeQuery(VRE_VARIABLE_QUERY);) {
			if (rset != null) {
				while (rset.next()) {
					String name = rset.getString(NAME);
					Double d = rset.getDouble(VALUE);
					switch (name) {
					case "START_OFFSET":
						START_OFFSET = d;
						break;
					case "END_OFFSET":
						END_OFFSET = d;
						break;
					case "SWITCH_TIME_ZONE":
						SWITCH_TIME_ZONE = d == 1 ? true : false;
						break;
					case "MIN_WHP":
						MIN_WHP = d;
						break;
					case "MAX_WHP":
						MAX_WHP = d;
						break;
					case "MIN_LIQUID_RATE":
						MIN_LIQUID_RATE = d;
						break;
					case "MAX_LIQUID_RATE":
						MAX_LIQUID_RATE = d;
						break;
					case "FREEZE_WHP_LIMIT":
						FREEZE_WHP_LIMIT = d;
						break;
					case "FREEZE_LIQUID_RATE_LIMIT":
						FREEZE_LIQUID_RATE_LIMIT = d;
						break;
					case "CV_WHP_MAX":
						CV_WHP_MAX = d;
						break;
					case "CV_LIQ_RATE_MAX":
						CV_LIQ_RATE_MAX = d;
						break;
					case "SHRINKAGE_FACTOR":
						SHRINKAGE_FACTOR = d;
						break;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
