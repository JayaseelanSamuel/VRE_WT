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

	/** The sql driver name. */
	public static final String SQL_DRIVER_NAME = "com.microsoft.sqlserver.jdbc.SQLServerDriver";

	/** The teiid driver name. */
	public static final String TEIID_DRIVER_NAME = "org.teiid.jdbc.TeiidDriver";

	/** The vre db url. */
	public static String VRE_DB_URL = "<SQL_URL>";

	/** The vre user. */
	public static String VRE_USER = "<USER>";

	/** The vre password. */
	public static String VRE_PASSWORD = "<PASSWORD>";

	// Properties which will be overridden at the application context load
	
	/** The phd teiid url. */
	public static String PHD_TEIID_URL = "<SQL_URL>";

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
	
	/** The vre exe. */
	public static String VRE_EXE = "D:/Pipesim_Models/VRE.exe";

	// Properties end

	
	/**
	 * The Enum VRE_TYPE.
	 */
	public static enum VRE_TYPE {
		/** The recalibration execution . */
		RECAL,

		/** The VRE1 execution. */
		VRE1,

		/** The VRE2 execution. */
		VRE2,

		/** The VRE3 execution. */
		VRE3,

		/** The VRE4 execution. */
		VRE4,

		/** The VRE5 execution. */
		VRE5
	};
	
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
	
	/**
	 * The Constant VRE1_DATASET_QUERY. TODO : Change TRY_CONVERT(DATETIME,
	 * '2015-12-02', 102) to getdate() later
	 */
	public static final String VRE1_DATASET_QUERY = "SELECT T.*, DM.AVG_WELLHEAD_PRESSURE, DD.WATER_CUT_LAB, DM.AVG_HEADER_PRESSURE, DM.AVG_BH_PRESSURE, DM.AVG_GAS_INJ_RATE, DM.AVG_CHOKE "
			+ " FROM ( "
			+ "	SELECT S.STRING_ID, S.UWI, S.STRING_TYPE, SM.PIPESIM_MODEL_LOC, DATEADD(dd, DATEDIFF(dd, 0, TRY_CONVERT(DATETIME, '2015-12-02', 102)), -1) AS RECORDED_DATE, "
			+ "	CAST(IIF(SM.TAG_DOWNHOLE_PRESSURE IS NOT NULL, 1, 0) AS BIT) AS RUN_VRE2, "
			+ "	CAST(IIF(SM.TAG_DOWNHOLE_PRESSURE IS NOT NULL, 1, 0) AS BIT) AS RUN_VRE3,"
			+ "	CAST(IIF(SM.TAG_DOWNHOLE_PRESSURE IS NOT NULL AND TAG_GASLIFT_INJ_RATE IS NOT NULL, 1, 0) AS BIT) AS RUN_VRE4, "
			+ "	CAST(IIF(SM.TAG_CHOKE_SIZE IS NOT NULL AND P.TAG_HEADER_PRESSURE IS NOT NULL, 1, 0) AS BIT) AS RUN_VRE5 "
			+ "	FROM STRING S " + "	LEFT OUTER JOIN STRING_METADATA SM ON S.STRING_ID = SM.STRING_ID "
			+ "	LEFT OUTER JOIN WELL W ON S.UWI = W.UWI "
			+ "	LEFT OUTER JOIN PLATFORM P ON P.PLATFORM_ID = W.PLATFORM_ID "
			+ "	WHERE SM.PIPESIM_MODEL_LOC IS NOT NULL AND TAG_WHP IS NOT NULL) T "
			+ " INNER JOIN DAILY_AVERAGE_MEASUREMENT DM ON DM.STRING_ID = T.STRING_ID AND DM.RECORDED_DATE = T.RECORDED_DATE "
			+ " INNER JOIN DAILY_ALLOCATED_DATA DD ON DD.STRING_ID = T.STRING_ID AND DD.RECORDED_DATE = T.RECORDED_DATE ";

	/**
	 * The Constant WELL_TEST_CALIBRATE_QUERY. identify eligible tests for
	 * calibration by looking for SR testType, VRE flag = true and Calibrated
	 * flag set to null. We will then update IS_CALIBRATED to true or false TODO:
	 * Change VRE flag to 1 later
	 */
	public static final String WELL_TEST_CALIBRATE_QUERY = "SELECT STRING_ID, QL1, WHP1, TEST_START_DATE, TEST_END_DATE, CONVERT(date, TEST_END_DATE) AS EFFECTIVE_DATE, "
			+ " IS_CALIBRATED, ROW_CHANGED_BY, ROW_CHANGED_DATE "
			+ " FROM WELL_TEST WHERE TEST_TYPE = 'SR' AND VRE_FLAG = 0 AND IS_CALIBRATED IS NULL ";

	/** The Constant VRE_TABLE_SELECT_QUERY. */
	public static final String VRE_TABLE_SELECT_QUERY = "SELECT STRING_ID, RECORDED_DATE, VRE1, VRE2, VRE3, VRE4, VRE5, VRE6, "
			+ " WATER_CUT, WATER_CUT_FLAG, GOR, PI, HOLDUP, FRICTION_FACTOR, RESERVOIR_PRESSURE, REMARK, ROW_CHANGED_BY, ROW_CHANGED_DATE "
			+ " FROM VIRTUAL_RATE_ESTIMATION "
			+ " WHERE RECORDED_DATE = ? AND STRING_ID = ? AND (WATER_CUT_FLAG IS NULL OR WATER_CUT_FLAG='LAB')";

	/** The Constant INSERT_VRE_QUERY. */
	public static final String INSERT_VRE_QUERY = "INSERT INTO VIRTUAL_RATE_ESTIMATION (STRING_ID, RECORDED_DATE, SOURCE_ID, VRE1, VRE2, VRE3, VRE4, VRE5, VRE6, "
			+ " WATER_CUT, WATER_CUT_FLAG, GOR, PI, HOLDUP, FRICTION_FACTOR, RESERVOIR_PRESSURE, REMARK) "
			+ " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, " + " ?, ?, ?, ?, ?, ?, ?, ?)";


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
	
	/** The Constant THREAD_POOL_SIZE. */
	public static final int THREAD_POOL_SIZE = 4;

	/** The Constant VRE_WORKFLOW. */
	public static final String VRE_WORKFLOW = "VRE Automation Workflow";

	/** The Constant DEFAULT_WATER_CUT. */
	public static final String DEFAULT_WATER_CUT = "LAB";

	/** The Constant DEFAULT_REMARK. */
	public static final String DEFAULT_REMARK = "Inserted/Updated by VRE Workflow"; // TODO: Rename

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

	/** The Constant RECORDED_DATE. */
	public static final String RECORDED_DATE = "RECORDED_DATE";

	/** The Constant AVG_WELLHEAD_PRESSURE. */
	public static final String AVG_WELLHEAD_PRESSURE = "AVG_WELLHEAD_PRESSURE";

	/** The Constant AVG_HEADER_PRESSURE. */
	public static final String AVG_HEADER_PRESSURE = "AVG_HEADER_PRESSURE";

	/** The Constant AVG_BH_PRESSURE. */
	public static final String AVG_BH_PRESSURE = "AVG_BH_PRESSURE";

	/** The Constant AVG_GAS_INJ_RATE. */
	public static final String AVG_GAS_INJ_RATE = "AVG_GAS_INJ_RATE";

	/** The Constant AVG_CHOKE. */
	public static final String AVG_CHOKE = "AVG_CHOKE";

	/** The Constant RUN_VRE2. */
	public static final String RUN_VRE2 = "RUN_VRE2";

	/** The Constant RUN_VRE3. */
	public static final String RUN_VRE3 = "RUN_VRE3";

	/** The Constant RUN_VRE4. */
	public static final String RUN_VRE4 = "RUN_VRE4";

	/** The Constant RUN_VRE5. */
	public static final String RUN_VRE5 = "RUN_VRE5";

	/** The Constant VRE1. */
	public static final String VRE1 = "VRE1";

	/** The Constant VRE2. */
	public static final String VRE2 = "VRE2";

	/** The Constant VRE3. */
	public static final String VRE3 = "VRE3";

	/** The Constant VRE4. */
	public static final String VRE4 = "VRE4";

	/** The Constant VRE5. */
	public static final String VRE5 = "VRE5";

	/** The Constant VRE6. */
	public static final String VRE6 = "VRE6";

	/** The Constant WATER_CUT. */
	public static final String WATER_CUT = "WATER_CUT";

	/** The Constant WATER_CUT_FLAG. */
	public static final String WATER_CUT_FLAG = "WATER_CUT_FLAG";

	/** The Constant GOR. */
	public static final String GOR = "GOR";

	/** The Constant PI. */
	public static final String PI = "PI";

	/** The Constant HOLDUP. */
	public static final String HOLDUP = "HOLDUP";

	/** The Constant FRICTION_FACTOR. */
	public static final String FRICTION_FACTOR = "FRICTION_FACTOR";

	/** The Constant RESERVOIR_PRESSURE. */
	public static final String RESERVOIR_PRESSURE = "RESERVOIR_PRESSURE";

	/** The Constant REMARK. */
	public static final String REMARK = "REMARK";

	// arguments

	/** The Constant ARG_VRE1. */
	public static final String ARG_VRE1 = "-vre1";

	/** The Constant ARG_VRE2. */
	public static final String ARG_VRE2 = "-vre2";

	/** The Constant ARG_VRE3. */
	public static final String ARG_VRE3 = "-vre3";

	/** The Constant ARG_VRE4. */
	public static final String ARG_VRE4 = "-vre4";

	/** The Constant ARG_VRE5. */
	public static final String ARG_VRE5 = "-vre5";

	/** The Constant ARG_VRE6. */
	public static final String ARG_VRE6 = "-vre6";

	/** The Constant ARG_MODEL. */
	public static final String ARG_MODEL = "-model";

	/** The Constant ARG_WHP. */
	public static final String ARG_WHP = "-whp";

	/** The Constant ARG_WATERCUT. */
	public static final String ARG_WATERCUT = "-wc";

	/** The Constant ARG_HEADER. */
	public static final String ARG_HEADER = "-header";

	/** The Constant ARG_PDGP. */
	public static final String ARG_PDGP = "-pdgp";

	/** The Constant ARG_CHOKE. */
	public static final String ARG_CHOKE = "-choke";

	/** The Constant ARG_RESERVOIR. */
	public static final String ARG_RESERVOIR = "-reservoir";

	/** The Constant ARG_TEST_LIQ_RATE. */
	public static final String ARG_TEST_LIQ_RATE = "-qtest";

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
