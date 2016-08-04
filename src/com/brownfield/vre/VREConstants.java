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
	public static String VRE_DB_URL = "<SERVER_URL>";

	/** The vre user. */
	public static String VRE_USER = "<USER>";

	/** The vre password. */
	public static String VRE_PASSWORD = "<PASSWORD>";

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

	/** The min watercut. */
	public static double MIN_WATERCUT = -10;

	/** The max watercut. */
	public static double MAX_WATERCUT = 100;

	/** The freeze whp limit. */
	public static double FREEZE_WHP_LIMIT = 0;

	/** The freeze liquid rate limit. */
	public static double FREEZE_LIQUID_RATE_LIMIT = 6;

	/** The freeze watercut limit. */
	public static double FREEZE_WATERCUT_LIMIT = 0;

	/** The cv liq rate max. */
	public static double CV_LIQ_RATE_MAX = 0.08;

	/** The cv whp max. */
	public static double CV_WHP_MAX = 0.08;

	/** The cv watercut max. */
	public static double CV_WATERCUT_MAX = 0.08;

	/** The shrinkage factor. */
	public static double SHRINKAGE_FACTOR = 0.95;

	/** The vre exe. */
	public static String VRE_EXE_LOC = "D:/Pipesim_Models/VRE.exe";
	
	/** The Constant THREAD_POOL_SIZE. */
	public static int CONCURRENT_PIPESIM_LICENCES = 4;

	/** The VRE6 output folder. */
	public static final String VRE6_OUTPUT_FOLDER = "D:/Pipesim_Models/VRE6_OutputModels/";

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
		VRE5,

		/** The VRE6 execution. */
		VRE6
	};

	/**
	 * The Enum DSIS_JOB_TYPE.
	 */
	public static enum DSIS_JOB_TYPE {
		/** The submitted. */
		SUBMITTED(0),
		/** The in progress. */
		IN_PROGRESS(1),
		/** The finished. */
		FINISHED(2),
		/** The failed. */
		FAILED(3);

		/** The num val. */
		private int numVal;

		/**
		 * Instantiates a new dsis job type.
		 *
		 * @param numVal
		 *            the num val
		 */
		DSIS_JOB_TYPE(int numVal) {
			this.numVal = numVal;
		}

		/**
		 * Gets the num val.
		 *
		 * @return the num val
		 */
		public int getNumVal() {
			return numVal;
		}
	}

	/**
	 * The Enum DSRTA_JOB_TYPE.
	 */
	public static enum DSRTA_JOB_TYPE {

		/** The reset. */
		INVALID(0),
		/** The finished. */
		READY(1),
		/** The updated. */
		FINISHED(2);

		/** The num val. */
		private int numVal;

		/**
		 * Instantiates a new dsrta job type.
		 *
		 * @param numVal
		 *            the num val
		 */
		DSRTA_JOB_TYPE(int numVal) {
			this.numVal = numVal;
		}

		/**
		 * Gets the num val.
		 *
		 * @return the num val
		 */
		public int getNumVal() {
			return numVal;
		}
	}

	/**
	 * The Enum SOURCE.
	 */
	public static enum SOURCE {

		/** The zadco. */
		ZADCO(1),
		/** The seabed. */
		SEABED(2),
		/** The avocet. */
		AVOCET(3),
		/** The phd. */
		PHD(4),
		/** The excel. */
		EXCEL(5),
		/** The vre. */
		VRE(6);

		/** The num val. */
		private int numVal;

		/**
		 * Instantiates a new source.
		 *
		 * @param numVal
		 *            the num val
		 */
		SOURCE(int numVal) {
			this.numVal = numVal;
		}

		/**
		 * Gets the num val.
		 *
		 * @return the num val
		 */
		public int getNumVal() {
			return numVal;
		}
	}

	// Queries

	/** The well test new query. */
	// identify new well test by looking for FT testType and Null flag. We will
	// then update VRE_FLAG to false (to
	// exclude it in next run) and update QL1 to standard conditions
	public static final String WELL_TEST_NEW_QUERY = "SELECT STRING_ID, QL1, WHP1, TEST_START_DATE, TEST_END_DATE, TRY_CONVERT(VARCHAR(10), TEST_END_DATE, 120) AS EFFECTIVE_DATE, "
			+ " TEST_TYPE, VRE_FLAG, ROW_CHANGED_BY, ROW_CHANGED_DATE "
			+ " FROM WELL_TEST WHERE TEST_TYPE = 'FT' AND VRE_FLAG IS NULL";
	// AND SOURCE_ID = 2

	/** The complete string information. */
	public static final String GET_STRING_METADATA_QUERY = "SELECT S.STRING_ID, S.UWI, S.STRING_TYPE, S.STRING_NAME, S.COMPLETION_DATE, S.LATITUDE, S.LONGITUDE, S.CURRENT_STATUS, S.SELECTED_VRE, "
			+ " P.PLATFORM_ID, P.PLATFORM_NAME, P.TAG_LIQUID_RATE, P.TAG_GAS_RATE, P.TAG_WATERCUT, P.TAG_HEADER_PRESSURE, P.TAG_INJ_HEADER_PRESSURE, "
			+ " TAG_WHP, TAG_WHT, TAG_CHOKE_SIZE, TAG_DOWNHOLE_PRESSURE, TAG_GASLIFT_INJ_RATE, TAG_WATER_VOL_RATE, TAG_OIL_VOL_RATE, TAG_ANN_PRESSURE_A, TAG_ANN_PRESSURE_B, PIPESIM_MODEL_LOC, STABILITY_FLAG "
			+ " FROM STRING S " + " LEFT OUTER JOIN STRING_METADATA SM ON S.STRING_ID = SM.STRING_ID "
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

	/** The Constant GET_STRING_NAME_QUERY. */
	public static final String GET_STRING_NAME_QUERY = "SELECT STRING_ID, UWI, STRING_TYPE, S.STRING_NAME "
			+ " FROM STRING WHERE STRING_ID = ? ";

	/** The Constant VRE_VARIABLE_QUERY. */
	public static final String VRE_VARIABLE_QUERY = "SELECT NAME, \"VALUE\" FROM VRE_VARIABLES";

	/** The Constant VRE_DATASET_QUERY. */
	public static final String VRE_DATASET_QUERY = "SELECT T.*, DM.RECORDED_DATE, DM.AVG_WHP, DD.WATER_CUT_LAB, DM.AVG_DOWNHOLE_PRESSURE, DM.AVG_GASLIFT_INJ_RATE, "
			+ " DM.AVG_HEADER_PRESSURE, (DD.CHOKE_SETTING / 100) AS CHOKE_SETTING " + " FROM ( "
			+ " SELECT S.STRING_ID, S.UWI, S.STRING_TYPE, SM.PIPESIM_MODEL_LOC, R.RESERVOIR_MODEL_LOC "
			+ " FROM STRING S LEFT OUTER JOIN STRING_METADATA SM ON SM.STRING_ID = S.STRING_ID "
			+ " LEFT OUTER JOIN WELL W ON W.UWI = S.UWI "
			+ " LEFT OUTER JOIN PLATFORM P ON P.PLATFORM_ID = W.PLATFORM_ID "
			+ "	LEFT OUTER JOIN STRING_SECTOR_ALLOCATION SSA ON SSA.STRING_ID = S.STRING_ID "
			+ " LEFT OUTER JOIN SECTOR SC ON SC.SECTOR_ID = SSA.SECTOR_ID "
			+ "	LEFT OUTER JOIN RESERVOIR R ON R.RESERVOIR_ID = SC.RESERVOIR_ID "
			+ "	WHERE SM.PIPESIM_MODEL_LOC IS NOT NULL AND TAG_WHP IS NOT NULL " + " ) T "
			+ " INNER JOIN DAILY_AVERAGE_MEASUREMENT DM ON DM.STRING_ID = T.STRING_ID AND DM.RECORDED_DATE = ? "
			+ " INNER JOIN DAILY_ALLOCATED_DATA DD ON DD.STRING_ID = T.STRING_ID AND DD.RECORDED_DATE = DM.RECORDED_DATE  ";

	/**
	 * The Constant WELL_TEST_CALIBRATE_QUERY. identify eligible tests for
	 * calibration by looking for SR testType, VRE flag = true and Calibrated
	 * flag set to null. We will then update IS_CALIBRATED to true or false.
	 */
	public static final String WELL_TEST_CALIBRATE_QUERY = "SELECT WT.STRING_ID, QL1, WHP1, TEST_START_DATE, TEST_END_DATE, CONVERT(date, TEST_END_DATE) AS EFFECTIVE_DATE, "
			+ " IS_CALIBRATED, TEST_WATER_CUT, SM.PIPESIM_MODEL_LOC, WT.ROW_CHANGED_BY, WT.ROW_CHANGED_DATE "
			+ " FROM WELL_TEST WT " + " LEFT OUTER JOIN STRING_METADATA SM ON WT.STRING_ID = SM.STRING_ID "
			+ " WHERE TEST_TYPE = 'SR' AND VRE_FLAG = 1 AND IS_CALIBRATED IS NULL AND SM.PIPESIM_MODEL_LOC IS NOT NULL ";

	/** The Constant VRE_TABLE_SELECT_QUERY. */
	public static final String VRE_TABLE_SELECT_QUERY = "SELECT STRING_ID, RECORDED_DATE, VRE1, VRE2, VRE3, VRE4, VRE5, VRE6, "
			+ " WATER_CUT, WATER_CUT_FLAG, GOR, PI, HOLDUP, FRICTION_FACTOR, RESERVOIR_PRESSURE, REMARK, ROW_CHANGED_BY, ROW_CHANGED_DATE "
			+ " FROM VIRTUAL_RATE_ESTIMATION "
			+ " WHERE RECORDED_DATE = ? AND STRING_ID = ? AND (WATER_CUT_FLAG IS NULL OR WATER_CUT_FLAG='LAB')";

	/** The Constant INSERT_VRE_QUERY. */
	public static final String INSERT_VRE_QUERY = "INSERT INTO VIRTUAL_RATE_ESTIMATION (STRING_ID, RECORDED_DATE, SOURCE_ID, VRE1, VRE2, VRE3, VRE4, VRE5, VRE6, "
			+ " WATER_CUT, WATER_CUT_FLAG, GOR, PI, HOLDUP, FRICTION_FACTOR, RESERVOIR_PRESSURE, REMARK) "
			+ " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, " + " ?, ?, ?, ?, ?, ?, ?, ?)";

	/** The Constant VRE_JOBS_QUERY. */
	public static final String VRE_JOBS_QUERY = "SELECT J.STRING_ID, S.STRING_NAME, DSIS_STATUS_ID, VRE6_EXE_OUTPUT, DSRTA_STATUS_ID, REMARK, J.ROW_CHANGED_BY, J.ROW_CHANGED_DATE "
			+ " FROM VRE6_JOBS J " + " LEFT OUTER JOIN STRING S ON J.STRING_ID = S.STRING_ID "
			+ " WHERE J.STRING_ID = ? ";

	/** The Constant INSERT_VRE_JOBS_QUERY. */
	public static final String INSERT_VRE_JOBS_QUERY = "INSERT INTO VRE6_JOBS (STRING_ID, DSIS_STATUS_ID, DSRTA_STATUS_ID, REMARK, ROW_CREATED_BY) "
			+ " VALUES (?, ?, ?, ?, ?) ";

	/** The Constant VRE_JOBS_IN_PROGRESS_QUERY. */
	public static final String VRE_JOBS_IN_PROGRESS_QUERY = "SELECT STRING_ID, DSIS_STATUS_ID, VRE6_EXE_OUTPUT, DSRTA_STATUS_ID, REMARK, ROW_CHANGED_BY, ROW_CHANGED_DATE "
			+ " FROM VRE6_JOBS WHERE DSIS_STATUS_ID = 1 AND DSRTA_STATUS_ID = 0 ";

	/** The Constant RT_DISTINCT_STRING_FOR_DAY_QUERY. */
	public static final String RT_DISTINCT_STRING_FOR_DAY_QUERY = "SELECT RT.STRING_ID, START_DATE, END_DATE FROM( "
			+ " SELECT DISTINCT STRING_ID, CAST(RECORDED_DATE AS DATE) RECORDED_DATE FROM REAL_TIME_DATA WHERE CAST(RECORDED_DATE AS DATE) = ? "
			+ " ) RT LEFT OUTER JOIN STRING_DOWNTIME SD ON SD.STRING_ID = RT.STRING_ID AND RECORDED_DATE BETWEEN CAST(START_DATE AS DATE) AND END_DATE";

	/** The Constant AVG_WHERE_CLAUSE. */
	public static final String AVG_WHERE_CLAUSE = " AND RECORDED_DATE NOT BETWEEN '%S' AND '%S' ";

	/** The Constant AVG_MEASUREMENT_QUERY. */
	public static final String AVG_MEASUREMENT_QUERY = "SELECT * FROM ("
			+ " SELECT STRING_ID, RECORDED_DATE, CONCAT('AVG_',TAG_TYPE) AS TAG_TYPE, AVERAGE " + " FROM( "
			+ " SELECT STRING_ID, CAST(RECORDED_DATE AS DATE) AS RECORDED_DATE, TAG_TYPE_ID , AVG(TAGRAWVALUE) AS AVERAGE "
			+ " FROM REAL_TIME_DATA " + " WHERE CAST(RECORDED_DATE AS DATE) = ? AND STRING_ID = ? " + " %S "
			+ " GROUP BY STRING_ID, CAST(RECORDED_DATE AS DATE), TAG_TYPE_ID " + " ) RT "
			+ " LEFT OUTER JOIN TAG_TYPE TT ON TT.TAG_TYPE_ID = RT.TAG_TYPE_ID " + " ) SRC " + " PIVOT ( "
			+ " SUM(AVERAGE) FOR TAG_TYPE IN ([AVG_WHP], [AVG_WHT], [AVG_CHOKE_SIZE], [AVG_DOWNHOLE_PRESSURE], [AVG_GASLIFT_INJ_RATE], [AVG_WATER_VOL_RATE], "
			+ " [AVG_OIL_VOL_RATE], [AVG_ANN_PRESSURE_A], [AVG_ANN_PRESSURE_B], [AVG_LIQUID_RATE], [AVG_GAS_RATE], [AVG_WATERCUT], [AVG_HEADER_PRESSURE])"
			+ " ) PIV ";

	/** The Constant AVG_MEASUREMENT_SELECT_QUERY. */
	public static final String AVG_MEASUREMENT_SELECT_QUERY = "SELECT STRING_ID, RECORDED_DATE, AVG_WHP, AVG_WHT, AVG_CHOKE_SIZE, AVG_DOWNHOLE_PRESSURE, AVG_GASLIFT_INJ_RATE, "
			+ " AVG_WATER_VOL_RATE, AVG_OIL_VOL_RATE, AVG_ANN_PRESSURE_A, AVG_ANN_PRESSURE_B, AVG_LIQUID_RATE, AVG_GAS_RATE, AVG_WATERCUT, AVG_HEADER_PRESSURE, ROW_CHANGED_BY, ROW_CHANGED_DATE "
			+ " FROM DAILY_AVERAGE_MEASUREMENT " + " WHERE STRING_ID = ? AND RECORDED_DATE = ? ";

	/** The Constant INSERT_AVG_MEASUREMENT_QUERY. */
	public static final String INSERT_AVG_MEASUREMENT_QUERY = "INSERT INTO DAILY_AVERAGE_MEASUREMENT ("
			+ " STRING_ID, RECORDED_DATE, AVG_WHP, AVG_WHT, AVG_CHOKE_SIZE, AVG_DOWNHOLE_PRESSURE, AVG_GASLIFT_INJ_RATE, AVG_WATER_VOL_RATE, AVG_OIL_VOL_RATE, "
			+ " AVG_ANN_PRESSURE_A, AVG_ANN_PRESSURE_B, AVG_LIQUID_RATE, AVG_GAS_RATE, AVG_WATERCUT, AVG_HEADER_PRESSURE, REMARK, ROW_CREATED_BY) "
			+ " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ";

	/** The Constant JOBS_REMARK. */
	public static final String JOBS_REMARK = "Job %s on %s";
	// other constants

	/** The single rate test. */
	public static final String SINGLE_RATE_TEST = "SR";

	/** The flow test. */
	public static final String FLOW_TEST = "FT";

	/** The date time format. */
	public static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss.S";

	/** The Constant DATE_FORMAT. */
	public static final String DATE_FORMAT = "yyyy-MM-dd";

	/** The Constant AVG_CALC_WORKFLOW. */
	public static final String AVG_CALC_WORKFLOW = "Average Calculation Workflow";

	/** The Constant WTV_WORKFLOW. */
	public static final String WTV_WORKFLOW = "WellTestValidation Workflow";

	/** The Constant VRE_WORKFLOW. */
	public static final String RECAL_WORKFLOW = "Recalibration Workflow";

	/** The Constant VRE_WORKFLOW. */
	public static final String VRE_WORKFLOW = "VRE Automation Workflow";

	/** The Constant FILE_MONITORING_SERVICE. */
	public static final String FILE_MONITORING_SERVICE = "File Monitoring Service";

	/** The Constant DEFAULT_WATER_CUT. */
	public static final String DEFAULT_WATER_CUT = "LAB";

	/** The Constant DEFAULT_REMARK. */
	public static final String DEFAULT_REMARK = "Inserted/Updated by VRE Workflow";

	/** The Constant CSV_EXTENSION. */
	public static final String CSV_EXTENSION = ".csv";

	/** The Constant JSON_EXTENSION. */
	public static final String JSON_EXTENSION = ".json";

	// Column Names

	/** The string id. */
	public static final String STRING_ID = "STRING_ID";

	/** The uwi. */
	public static final String UWI = "UWI";

	/** The string type. */
	public static final String STRING_TYPE = "STRING_TYPE";

	/** The Constant STRING_NAME. */
	public static final String STRING_NAME = "STRING_NAME";

	/** The Constant COMPLETION_DATE. */
	public static final String COMPLETION_DATE = "COMPLETION_DATE";

	/** The Constant LATITUDE. */
	public static final String LATITUDE = "LATITUDE";

	/** The Constant LONGITUDE. */
	public static final String LONGITUDE = "LONGITUDE";

	/** The Constant CURRENT_STATUS. */
	public static final String CURRENT_STATUS = "CURRENT_STATUS";

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

	/** The vre flag. */
	public static final String VRE_FLAG = "VRE_FLAG";

	/** The Constant IS_CALIBRATED. */
	public static final String IS_CALIBRATED = "IS_CALIBRATED";

	/** The Constant TEST_WATER_CUT. */
	public static final String TEST_WATER_CUT = "TEST_WATER_CUT";

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

	/** The Constant TIMESTAMP. */
	public static final String TIMESTAMP = "timestamp";

	/** The source values. */
	public static final String SOURCE_VALUES = "sourceValues";

	/** The Constant QUALITY. */
	public static final String QUALITY = "quality";

	/** The Constant AGGREGATE_ID. */
	public static final String AGGREGATE_ID = "aggregateId";

	/** The Constant NO_OF_VALUES. */
	public static final String NO_OF_VALUES = "noOfValues";

	/** The Constant RESAMPLE_INTERVAL. */
	public static final String RESAMPLE_INTERVAL = "resampleInterval";

	/** The Constant START_TIMESTAMP. */
	public static final String START_TIMESTAMP = "startTimestamp";

	/** The Constant END_TIMESTAMP. */
	public static final String END_TIMESTAMP = "endTimestamp";

	/** The tag liquid rate. */
	public static final String TAG_LIQUID_RATE = "TAG_LIQUID_RATE";

	/** The tag gas rate. */
	public static final String TAG_GAS_RATE = "TAG_GAS_RATE";

	/** The Constant TAG_WATERCUT. */
	public static final String TAG_WATERCUT = "TAG_WATERCUT";

	/** The Constant TAG_HEADER_PRESSURE. */
	public static final String TAG_HEADER_PRESSURE = "TAG_HEADER_PRESSURE";

	/** The Constant TAG_INJ_HEADER_PRESSURE. */
	public static final String TAG_INJ_HEADER_PRESSURE = "TAG_INJ_HEADER_PRESSURE";

	/** The Constant TAG_WHP. */
	public static final String TAG_WHP = "TAG_WHP";

	/** The Constant TAG_WHT. */
	public static final String TAG_WHT = "TAG_WHT";

	/** The Constant TAG_CHOKE_SIZE. */
	public static final String TAG_CHOKE_SIZE = "TAG_CHOKE_SIZE";

	/** The Constant TAG_DOWNHOLE_PRESSURE. */
	public static final String TAG_DOWNHOLE_PRESSURE = "TAG_DOWNHOLE_PRESSURE";

	/** The Constant TAG_GASLIFT_INJ_RATE. */
	public static final String TAG_GASLIFT_INJ_RATE = "TAG_GASLIFT_INJ_RATE";

	/** The Constant TAG_WATER_VOL_RATE. */
	public static final String TAG_WATER_VOL_RATE = "TAG_WATER_VOL_RATE";

	/** The Constant TAG_OIL_VOL_RATE. */
	public static final String TAG_OIL_VOL_RATE = "TAG_OIL_VOL_RATE";

	/** The Constant TAG_ANN_PRESSURE_A. */
	public static final String TAG_ANN_PRESSURE_A = "TAG_ANN_PRESSURE_A";

	/** The Constant TAG_ANN_PRESSURE_B. */
	public static final String TAG_ANN_PRESSURE_B = "TAG_ANN_PRESSURE_B";

	/** The Constant PIPESIM_MODEL_LOC. */
	public static final String PIPESIM_MODEL_LOC = "PIPESIM_MODEL_LOC";

	/** The Constant RESERVOIR_MODEL_LOC. */
	public static final String RESERVOIR_MODEL_LOC = "RESERVOIR_MODEL_LOC";

	/** The Constant ROW_CHANGED_BY. */
	public static final String ROW_CHANGED_BY = "ROW_CHANGED_BY";

	/** The Constant ROW_CHANGED_DATE. */
	public static final String ROW_CHANGED_DATE = "ROW_CHANGED_DATE";

	/** The Constant RECORDED_DATE. */
	public static final String RECORDED_DATE = "RECORDED_DATE";

	/** The Constant RUN_VRE2. */
	//public static final String RUN_VRE2 = "RUN_VRE2";

	/** The Constant RUN_VRE3. */
	//public static final String RUN_VRE3 = "RUN_VRE3";

	/** The Constant RUN_VRE4. */
	//public static final String RUN_VRE4 = "RUN_VRE4";

	/** The Constant RUN_VRE5. */
	//public static final String RUN_VRE5 = "RUN_VRE5";

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

	/** The Constant CHOKE_SETTING. */
	public static final String CHOKE_SETTING = "CHOKE_SETTING";

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

	/** The Constant DSIS_STATUS_ID. */
	public static final String DSIS_STATUS_ID = "DSIS_STATUS_ID";

	/** The Constant VRE6_EXE_OUTPUT. */
	public static final String VRE6_EXE_OUTPUT = "VRE6_EXE_OUTPUT";

	/** The Constant DSRTA_STATUS_ID. */
	public static final String DSRTA_STATUS_ID = "DSRTA_STATUS_ID";

	/** The Constant START_DATE. */
	public static final String START_DATE = "START_DATE";

	/** The Constant END_DATE. */
	public static final String END_DATE = "END_DATE";

	/** The Constant AVG_WHP. */
	public static final String AVG_WHP = "AVG_WHP";

	/** The Constant AVG_WHT. */
	public static final String AVG_WHT = "AVG_WHT";

	/** The Constant AVG_CHOKE_SIZE. */
	public static final String AVG_CHOKE_SIZE = "AVG_CHOKE_SIZE";

	/** The Constant AVG_DOWNHOLE_PRESSURE. */
	public static final String AVG_DOWNHOLE_PRESSURE = "AVG_DOWNHOLE_PRESSURE";

	/** The Constant AVG_GASLIFT_INJ_RATE. */
	public static final String AVG_GASLIFT_INJ_RATE = "AVG_GASLIFT_INJ_RATE";

	/** The Constant AVG_WATER_VOL_RATE. */
	public static final String AVG_WATER_VOL_RATE = "AVG_WATER_VOL_RATE";

	/** The Constant AVG_OIL_VOL_RATE. */
	public static final String AVG_OIL_VOL_RATE = "AVG_OIL_VOL_RATE";

	/** The Constant AVG_ANN_PRESSURE_A. */
	public static final String AVG_ANN_PRESSURE_A = "AVG_ANN_PRESSURE_A";

	/** The Constant AVG_ANN_PRESSURE_B. */
	public static final String AVG_ANN_PRESSURE_B = "AVG_ANN_PRESSURE_B";

	/** The Constant AVG_LIQUID_RATE. */
	public static final String AVG_LIQUID_RATE = "AVG_LIQUID_RATE";

	/** The Constant AVG_GAS_RATE. */
	public static final String AVG_GAS_RATE = "AVG_GAS_RATE";

	/** The Constant AVG_WATERCUT. */
	public static final String AVG_WATERCUT = "AVG_WATERCUT";

	/** The Constant AVG_HEADER_PRESSURE. */
	public static final String AVG_HEADER_PRESSURE = "AVG_HEADER_PRESSURE";

	// public static final String AVG_HEADER_PRESSURE = "AVG_HEADER_PRESSURE";

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

	/** The Constant ARG_OUTPUT_LOC. */
	public static final String ARG_OUTPUT_LOC = "-o";

	/** The Constant ARG_WHP. */
	public static final String ARG_WHP = "-whp";

	/** The Constant ARG_WATERCUT. */
	public static final String ARG_WATERCUT = "-wc";

	/** The Constant ARG_HEADER. */
	public static final String ARG_HEADER = "-header";

	/** The Constant ARG_PDGP. */
	public static final String ARG_PDGP = "-pdgp";

	/** The Constant ARG_BEANSIZE. */
	public static final String ARG_BEANSIZE = "-beansize";

	/** The Constant ARG_GAS_INJ_RATE. */
	public static final String ARG_GAS_INJ_RATE = "-qgi";

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
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
