package com.brownfield.vre.exe;

import static com.brownfield.vre.VREConstants.ARG_CHOKE;
import static com.brownfield.vre.VREConstants.ARG_HEADER;
import static com.brownfield.vre.VREConstants.ARG_MODEL;
import static com.brownfield.vre.VREConstants.ARG_PDGP;
import static com.brownfield.vre.VREConstants.ARG_VRE1;
import static com.brownfield.vre.VREConstants.ARG_VRE2;
import static com.brownfield.vre.VREConstants.ARG_VRE3;
import static com.brownfield.vre.VREConstants.ARG_VRE4;
import static com.brownfield.vre.VREConstants.ARG_VRE5;
import static com.brownfield.vre.VREConstants.ARG_WATERCUT;
import static com.brownfield.vre.VREConstants.ARG_WHP;
import static com.brownfield.vre.VREConstants.AVG_BH_PRESSURE;
import static com.brownfield.vre.VREConstants.AVG_CHOKE;
import static com.brownfield.vre.VREConstants.AVG_GAS_INJ_RATE;
import static com.brownfield.vre.VREConstants.AVG_HEADER_PRESSURE;
import static com.brownfield.vre.VREConstants.AVG_WELLHEAD_PRESSURE;
import static com.brownfield.vre.VREConstants.PIPESIM_MODEL_LOC;
import static com.brownfield.vre.VREConstants.RECORDED_DATE;
import static com.brownfield.vre.VREConstants.RUN_VRE2;
import static com.brownfield.vre.VREConstants.RUN_VRE3;
import static com.brownfield.vre.VREConstants.RUN_VRE4;
import static com.brownfield.vre.VREConstants.RUN_VRE5;
import static com.brownfield.vre.VREConstants.SQL_DRIVER_NAME;
import static com.brownfield.vre.VREConstants.STRING_ID;
import static com.brownfield.vre.VREConstants.THREAD_POOL_SIZE;
import static com.brownfield.vre.VREConstants.VRE1_DATASET_QUERY;
import static com.brownfield.vre.VREConstants.VRE_DB_URL;
import static com.brownfield.vre.VREConstants.VRE_EXE;
import static com.brownfield.vre.VREConstants.VRE_PASSWORD;
import static com.brownfield.vre.VREConstants.VRE_USER;
import static com.brownfield.vre.VREConstants.WATER_CUT_LAB;
import static com.brownfield.vre.VREConstants.WELL_TEST_CALIBRATE_QUERY;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The Class VREExecutioner.
 * 
 * @author Onkar Dhuri <onkar.dhuri@synerzip.com>
 */
public class VREExecutioner {

	/** The logger. */
	private static Logger LOGGER = Logger.getLogger(VREExecutioner.class.getName());

	/**
	 * The main method.
	 *
	 * @param args
	 *            the arguments
	 */
	public static void main(String[] args) {
		try {
			Class.forName(SQL_DRIVER_NAME);
			try (Connection vreConn = DriverManager.getConnection(VRE_DB_URL, VRE_USER, VRE_PASSWORD)) {

				VREExecutioner vreEx = new VREExecutioner();
				vreEx.runVREs(vreConn);
			} catch (SQLException e) {
				LOGGER.log(Level.SEVERE, e.getMessage());
			}

		} catch (ClassNotFoundException e) {
			LOGGER.log(Level.SEVERE, e.getMessage());
		}
	}

	/**
	 * Run vre1.
	 *
	 * @param vreConn
	 *            the vre conn
	 */
	public void runVREs(Connection vreConn) {
		int stringID;
		Timestamp recordedDate = null;

		try (Statement statement = vreConn.createStatement();
				ResultSet rset = statement.executeQuery(VRE1_DATASET_QUERY);) {

			ExecutorService executor = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
			Instant startTime = Instant.now();
			int rowCount = 0;
			while (rset.next()) {
				// VRE.exe -vre1 -modelUZ496L.bps -whp450 -wc10
				stringID = rset.getInt(STRING_ID);
				recordedDate = rset.getTimestamp(RECORDED_DATE);
				double whp = rset.getDouble(AVG_WELLHEAD_PRESSURE);
				double wcut = rset.getDouble(WATER_CUT_LAB);
				double hp = rset.getDouble(AVG_HEADER_PRESSURE);
				double pdgp = rset.getDouble(AVG_BH_PRESSURE);
				double gasInjRate = rset.getDouble(AVG_GAS_INJ_RATE);
				double choke = rset.getDouble(AVG_CHOKE);
				boolean runVRE2 = rset.getBoolean(RUN_VRE2);
				boolean runVRE3 = rset.getBoolean(RUN_VRE3);
				boolean runVRE4 = rset.getBoolean(RUN_VRE4);
				boolean runVRE5 = rset.getBoolean(RUN_VRE5);
				List<String> params = new ArrayList<>();
				params.add(VRE_EXE);// executable
				params.add(ARG_VRE1);
				params.add(ARG_MODEL + rset.getString(PIPESIM_MODEL_LOC));
				params.add(ARG_WHP + whp);
				params.add(ARG_WATERCUT + wcut);
				if (runVRE2) {
					params.add(ARG_VRE2);
					params.add(ARG_PDGP + pdgp);
				}
				if (runVRE3) {
					params.add(ARG_VRE3);
					params.add(ARG_PDGP + pdgp);
				}
				if (runVRE4) {
					params.add(ARG_VRE4);
					params.add(ARG_PDGP + pdgp);
					params.add("FIXMELATER" + gasInjRate);
				}
				if (runVRE5) {
					params.add(ARG_VRE5);
					params.add(ARG_CHOKE + choke);
					params.add(ARG_HEADER + hp);
				}
				// TODO: Add more params for vre2-5 later
				Runnable worker = new VREExeWorker(vreConn, params, stringID, whp, wcut, recordedDate);
				executor.execute(worker);
				rowCount++;
			}
			executor.shutdown();
			while (!executor.isTerminated()) {
			}
			Instant endTime = Instant.now();
			LOGGER.log(Level.INFO, "Finished running VRE1 for " + rowCount + " strings in "
					+ Duration.between(startTime, endTime) + " seconds");
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, e.getMessage());
		}
	}

	/**
	 * Run calibration.
	 *
	 * @param vreConn
	 *            the vre conn
	 */
	public void runCalibration(Connection vreConn) {
		int stringID;
		// Timestamp recordedDate = null;

		try (Statement statement = vreConn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
				ResultSet rset = statement.executeQuery(WELL_TEST_CALIBRATE_QUERY);) {
			if (rset != null) {
				while (rset.next()) {
					stringID = rset.getInt(STRING_ID);
					System.out.println(stringID);
				}
			}

		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, e.getMessage());
		}
	}
}
