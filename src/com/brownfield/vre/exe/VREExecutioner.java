package com.brownfield.vre.exe;

import static com.brownfield.vre.VREConstants.*;
import static com.brownfield.vre.VREConstants.ARG_HEADER;
import static com.brownfield.vre.VREConstants.ARG_MODEL;
import static com.brownfield.vre.VREConstants.ARG_PDGP;
import static com.brownfield.vre.VREConstants.ARG_TEST_LIQ_RATE;
import static com.brownfield.vre.VREConstants.ARG_VRE1;
import static com.brownfield.vre.VREConstants.ARG_VRE2;
import static com.brownfield.vre.VREConstants.ARG_VRE3;
import static com.brownfield.vre.VREConstants.ARG_VRE4;
import static com.brownfield.vre.VREConstants.ARG_VRE5;
import static com.brownfield.vre.VREConstants.ARG_VRE6;
import static com.brownfield.vre.VREConstants.ARG_WATERCUT;
import static com.brownfield.vre.VREConstants.ARG_WHP;
import static com.brownfield.vre.VREConstants.AVG_BH_PRESSURE;
import static com.brownfield.vre.VREConstants.AVG_CHOKE;
import static com.brownfield.vre.VREConstants.AVG_GAS_INJ_RATE;
import static com.brownfield.vre.VREConstants.AVG_HEADER_PRESSURE;
import static com.brownfield.vre.VREConstants.AVG_WELLHEAD_PRESSURE;
import static com.brownfield.vre.VREConstants.CSV_EXTENSION;
import static com.brownfield.vre.VREConstants.DSIS_STATUS_ID;
import static com.brownfield.vre.VREConstants.DSRTA_STATUS_ID;
import static com.brownfield.vre.VREConstants.FILE_MONITORING_SERVICE;
import static com.brownfield.vre.VREConstants.INSERT_VRE_JOBS_QUERY;
import static com.brownfield.vre.VREConstants.IS_CALIBRATED;
import static com.brownfield.vre.VREConstants.JOBS_REMARK;
import static com.brownfield.vre.VREConstants.PIPESIM_MODEL_LOC;
import static com.brownfield.vre.VREConstants.QL1;
import static com.brownfield.vre.VREConstants.RECAL_WORKFLOW;
import static com.brownfield.vre.VREConstants.RECORDED_DATE;
import static com.brownfield.vre.VREConstants.REMARK;
import static com.brownfield.vre.VREConstants.ROW_CHANGED_BY;
import static com.brownfield.vre.VREConstants.ROW_CHANGED_DATE;
import static com.brownfield.vre.VREConstants.RUN_VRE2;
import static com.brownfield.vre.VREConstants.RUN_VRE3;
import static com.brownfield.vre.VREConstants.RUN_VRE4;
import static com.brownfield.vre.VREConstants.RUN_VRE5;
import static com.brownfield.vre.VREConstants.SQL_DRIVER_NAME;
import static com.brownfield.vre.VREConstants.STRING_ID;
import static com.brownfield.vre.VREConstants.STRING_NAME;
import static com.brownfield.vre.VREConstants.TEST_WATER_CUT;
import static com.brownfield.vre.VREConstants.THREAD_POOL_SIZE;
import static com.brownfield.vre.VREConstants.VRE1_DATASET_QUERY;
import static com.brownfield.vre.VREConstants.VRE6_INPUT_FOLDER;
import static com.brownfield.vre.VREConstants.VRE6_OUTPUT_FOLDER;
import static com.brownfield.vre.VREConstants.VRE_DB_URL;
import static com.brownfield.vre.VREConstants.VRE_EXE_LOC;
import static com.brownfield.vre.VREConstants.VRE_JOBS_IN_PROGRESS_QUERY;
import static com.brownfield.vre.VREConstants.VRE_JOBS_QUERY;
import static com.brownfield.vre.VREConstants.VRE_PASSWORD;
import static com.brownfield.vre.VREConstants.VRE_USER;
import static com.brownfield.vre.VREConstants.WATER_CUT_LAB;
import static com.brownfield.vre.VREConstants.WELL_TEST_CALIBRATE_QUERY;
import static com.brownfield.vre.VREConstants.WHP1;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import com.brownfield.vre.VREConstants.DSIS_JOB_TYPE;
import com.brownfield.vre.VREConstants.DSRTA_JOB_TYPE;
import com.brownfield.vre.VREConstants.VRE_TYPE;
import com.brownfield.vre.exe.models.RecalModel;
import com.brownfield.vre.exe.models.WellModel;

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
				// vreEx.runVREs(vreConn);
				vreEx.runCalibration(vreConn);
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
				params.add(VRE_EXE_LOC);// executable
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
		try (Statement statement = vreConn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
				ResultSet rset = statement.executeQuery(WELL_TEST_CALIBRATE_QUERY);) {
			if (rset != null) {
				ExecutorService executor = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
				int rowCount = 0;
				while (rset.next()) {
					stringID = rset.getInt(STRING_ID);
					double whp = rset.getDouble(WHP1);
					double wcut = rset.getDouble(TEST_WATER_CUT);
					double qLiq = rset.getDouble(QL1);
					List<String> params = new ArrayList<>();
					params.add(VRE_EXE_LOC);// executable
					params.add(ARG_VRE1);
					params.add(ARG_MODEL + rset.getString(PIPESIM_MODEL_LOC));
					params.add(ARG_WHP + whp);
					params.add(ARG_WATERCUT + wcut);
					params.add(ARG_TEST_LIQ_RATE + qLiq);
					WellModel wellModel = VREExeWorker.runVRE(params);
					boolean isCalibrated = false;
					if (wellModel != null) {
						if (wellModel.getErrors() == null) {
							RecalModel recal = wellModel.getRecal();
							if (recal != null) {
								isCalibrated = recal.getCalibration();
								boolean review = recal.isReview();
								if (isCalibrated) {
									this.insertOrUpdateJob(vreConn, stringID, executor);
									rowCount++;
								}
								if (review) {
									// TODO: Trigger email either here or from
									// BPM
								}

							} else {
								LOGGER.log(Level.SEVERE, " No recal tag present in output for string - " + stringID);
							}
						} else {
							LOGGER.log(Level.SEVERE, " Exception in calling recal - " + wellModel.getErrors());
						}
					} else {
						LOGGER.log(Level.SEVERE, " Something went wrong while calling recal for string - " + stringID);
					}

					rset.updateBoolean(IS_CALIBRATED, isCalibrated);
					rset.updateString(ROW_CHANGED_BY, RECAL_WORKFLOW);
					rset.updateTimestamp(ROW_CHANGED_DATE, new Timestamp(new Date().getTime()));
					rset.updateRow();
				}
				executor.shutdown();
				// don't wait for executor to terminate
				if (rowCount != 0) {
					LOGGER.log(Level.INFO, "VRE6 jobs submitted for " + rowCount + " strings in on " + new Date());
				}
			}

		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, e.getMessage());
		}
	}

	/**
	 * Insert or update job in database as submitted.
	 *
	 * @param vreConn
	 *            the vre conn
	 * @param stringID
	 *            the string id
	 * @param executor
	 *            the executor
	 */
	private void insertOrUpdateJob(Connection vreConn, int stringID, ExecutorService executor) {
		try (PreparedStatement statement = vreConn.prepareStatement(VRE_JOBS_QUERY, ResultSet.TYPE_SCROLL_SENSITIVE,
				ResultSet.CONCUR_UPDATABLE); ResultSet rset = statement.executeQuery()) {
			List<String> params = new ArrayList<>();
			params.add(VRE_EXE_LOC);// executable
			params.add(ARG_VRE6); // TODO: Check the syntax
			String stringName = rset.getString(STRING_NAME) + CSV_EXTENSION;
			// TODO: only 4/5 files and not per string
			params.add(ARG_MODEL + VRE6_INPUT_FOLDER + stringName);
			// TODO: Delete the existing file
			String outputFile = VRE6_OUTPUT_FOLDER + stringName;
			FileUtils.forceDelete(new File(outputFile));
			Runnable worker = new VREExeWorker(params, stringID, VRE_TYPE.VRE6);
			executor.execute(worker);
			String remark = String.format(JOBS_REMARK, DSIS_JOB_TYPE.IN_PROGRESS, new Date());
			if (rset.next()) {
				// if there is already a job; update
				rset.updateInt(DSIS_STATUS_ID, DSIS_JOB_TYPE.IN_PROGRESS.getNumVal());
				rset.updateInt(DSRTA_STATUS_ID, DSRTA_JOB_TYPE.INVALID.getNumVal());
				rset.updateString(REMARK, remark);
				rset.updateString(ROW_CHANGED_BY, RECAL_WORKFLOW);
				rset.updateTimestamp(ROW_CHANGED_DATE, new Timestamp(new Date().getTime()));
				rset.updateRow();
			} else {
				try (PreparedStatement stmt = vreConn.prepareStatement(INSERT_VRE_JOBS_QUERY);) {
					stmt.setInt(1, stringID);
					stmt.setInt(2, DSIS_JOB_TYPE.IN_PROGRESS.getNumVal());
					stmt.setInt(3, DSRTA_JOB_TYPE.INVALID.getNumVal());
					stmt.setString(3, remark);
					stmt.executeUpdate();
				} catch (Exception e) {
					LOGGER.log(Level.SEVERE, e.getMessage());
				}
			}
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, e.getMessage());
		}
	}

	/**
	 * Update vre6 output.
	 *
	 * @param vreConn the vre conn
	 * @param stringID the string id
	 */
	public void updateVRE6Output(Connection vreConn, int stringID) {
		try (PreparedStatement statement = vreConn.prepareStatement(VRE_JOBS_IN_PROGRESS_QUERY,
				ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
				ResultSet rset = statement.executeQuery()) {
			if (rset != null) {
				while (rset.next()) {
					String outputFile = VRE6_OUTPUT_FOLDER + rset.getString(STRING_NAME);
					File f = new File(outputFile);
					if (f.exists() && !f.isDirectory()) {
						String content = this.getFileContent(f);
						if (content != null) {
							String remark = String.format(JOBS_REMARK, DSIS_JOB_TYPE.FINISHED, new Date());
							// file read successfully
							rset.updateInt(DSIS_STATUS_ID, DSIS_JOB_TYPE.FINISHED.getNumVal());
							rset.updateInt(DSRTA_STATUS_ID, DSRTA_JOB_TYPE.READY.getNumVal());
							rset.updateString(VRE6_EXE_OUTPUT, content);
							rset.updateString(REMARK, remark);
							rset.updateString(ROW_CHANGED_BY, FILE_MONITORING_SERVICE);
							rset.updateTimestamp(ROW_CHANGED_DATE, new Timestamp(new Date().getTime()));
							rset.updateRow();
							// Delete the output file
							FileUtils.deleteQuietly(f);
						}
					}
				}
			}
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, e.getMessage());
		}
	}

	/**
	 * Gets the file content.
	 *
	 * @param file the file
	 * @return the file content
	 */
	private String getFileContent(File file) {
		String content = null;
		try {
			// see if file is being written or not
			FileUtils.touch(file);
			// this will be called only when file is not locked by another
			// process (in this case VRE6 exe)
			content = IOUtils.toString(new FileInputStream(file));
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, file.getName()
					+ " : Can't read the file; file is already opened by another process.\n" + e.getMessage());
		}
		return content;
	}
}
