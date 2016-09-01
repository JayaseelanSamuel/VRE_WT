package com.brownfield.vre.exe;

import static com.brownfield.vre.VREConstants.ARG_BEANSIZE;
import static com.brownfield.vre.VREConstants.ARG_GAS_INJ_RATE;
import static com.brownfield.vre.VREConstants.ARG_GAS_OIL_RATIO;
import static com.brownfield.vre.VREConstants.ARG_HEADER;
import static com.brownfield.vre.VREConstants.ARG_MODEL;
import static com.brownfield.vre.VREConstants.ARG_OUTPUT_LOC;
import static com.brownfield.vre.VREConstants.ARG_PDGP;
import static com.brownfield.vre.VREConstants.ARG_PRODUCTIVITY_INDEX;
import static com.brownfield.vre.VREConstants.ARG_RESERVOIR;
import static com.brownfield.vre.VREConstants.ARG_RES_STATIC_PRESSURE;
import static com.brownfield.vre.VREConstants.ARG_TEST_LIQ_RATE;
import static com.brownfield.vre.VREConstants.ARG_VERTICAL_FRICTION_FACTOR;
import static com.brownfield.vre.VREConstants.ARG_VERTICAL_HOLDUP_FACTOR;
import static com.brownfield.vre.VREConstants.ARG_VRE1;
import static com.brownfield.vre.VREConstants.ARG_VRE2;
import static com.brownfield.vre.VREConstants.ARG_VRE3;
import static com.brownfield.vre.VREConstants.ARG_VRE4;
import static com.brownfield.vre.VREConstants.ARG_VRE5;
import static com.brownfield.vre.VREConstants.ARG_VRE6;
import static com.brownfield.vre.VREConstants.ARG_WATERCUT;
import static com.brownfield.vre.VREConstants.ARG_WHP;
import static com.brownfield.vre.VREConstants.AVG_DOWNHOLE_PRESSURE;
import static com.brownfield.vre.VREConstants.AVG_GASLIFT_INJ_RATE;
import static com.brownfield.vre.VREConstants.AVG_HEADER_PRESSURE;
import static com.brownfield.vre.VREConstants.AVG_WHP;
import static com.brownfield.vre.VREConstants.CHOKE_SETTING;
import static com.brownfield.vre.VREConstants.CONCURRENT_PIPESIM_LICENCES;
import static com.brownfield.vre.VREConstants.DATE_FORMAT;
import static com.brownfield.vre.VREConstants.DSIS_STATUS_ID;
import static com.brownfield.vre.VREConstants.DSRTA_STATUS_ID;
import static com.brownfield.vre.VREConstants.FRICTION_FACTOR;
import static com.brownfield.vre.VREConstants.GOR;
import static com.brownfield.vre.VREConstants.HOLDUP;
import static com.brownfield.vre.VREConstants.INSERT_VRE_JOBS_QUERY;
import static com.brownfield.vre.VREConstants.IS_CALIBRATED;
import static com.brownfield.vre.VREConstants.JOBS_REMARK;
import static com.brownfield.vre.VREConstants.JSON_EXTENSION;
import static com.brownfield.vre.VREConstants.PI;
import static com.brownfield.vre.VREConstants.PIPESIM_MODEL_LOC;
import static com.brownfield.vre.VREConstants.QL1;
import static com.brownfield.vre.VREConstants.RECAL;
import static com.brownfield.vre.VREConstants.RECAL_WORKFLOW;
import static com.brownfield.vre.VREConstants.RECORDED_DATE;
import static com.brownfield.vre.VREConstants.REMARK;
import static com.brownfield.vre.VREConstants.RESERVOIR_MODEL_LOC;
import static com.brownfield.vre.VREConstants.RESERVOIR_PRESSURE;
import static com.brownfield.vre.VREConstants.ROW_CHANGED_BY;
import static com.brownfield.vre.VREConstants.ROW_CHANGED_DATE;
import static com.brownfield.vre.VREConstants.SQL_DRIVER_NAME;
import static com.brownfield.vre.VREConstants.STRING_ID;
import static com.brownfield.vre.VREConstants.TEST_WATER_CUT;
import static com.brownfield.vre.VREConstants.VRE6_OUTPUT_FOLDER;
import static com.brownfield.vre.VREConstants.VRE_DATASET_QUERY;
import static com.brownfield.vre.VREConstants.VRE_DB_URL;
import static com.brownfield.vre.VREConstants.VRE_DURATION_QUERY;
import static com.brownfield.vre.VREConstants.VRE_EXE_LOC;
import static com.brownfield.vre.VREConstants.VRE_JOBS_QUERY;
import static com.brownfield.vre.VREConstants.VRE_MODEL_RESET_QUERY;
import static com.brownfield.vre.VREConstants.VRE_PASSWORD;
import static com.brownfield.vre.VREConstants.VRE_USER;
import static com.brownfield.vre.VREConstants.WATER_CUT;
import static com.brownfield.vre.VREConstants.WATER_CUT_LAB;
import static com.brownfield.vre.VREConstants.WELL_TEST_CALIBRATE_QUERY;
import static com.brownfield.vre.VREConstants.WHP1;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;

import com.brownfield.vre.Utils;
import com.brownfield.vre.VREConstants.DSIS_JOB_TYPE;
import com.brownfield.vre.VREConstants.DSRTA_JOB_TYPE;
import com.brownfield.vre.VREConstants.VRE_TYPE;
import com.brownfield.vre.exe.models.RecalModel;
import com.brownfield.vre.exe.models.StringModel;
import com.brownfield.vre.exe.models.WellModel;

/**
 * The Class VREExecutioner.
 * 
 * @author Onkar Dhuri <onkar.dhuri@synerzip.com>
 */
/**
 * @author onkard
 *
 */
public class VREExecutioner {

	/** The logger. */
	private static final Logger LOGGER = Logger.getLogger(VREExecutioner.class.getName());

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
				// Timestamp yesterdayTimestamp = Utils.getYesterdayTimestamp();
				// System.out.println(yesterdayTimestamp);
				String recDate = "2016-01-01";
				Timestamp recordedDate = Utils.getDateFromString(recDate, DATE_FORMAT, Boolean.FALSE);
				VREExecutioner vreEx = new VREExecutioner();
				vreEx.runVREs(vreConn, recordedDate);
				// vreEx.runCalibration(vreConn);
			} catch (SQLException e) {
				LOGGER.severe(e.getMessage());
			}

		} catch (ClassNotFoundException e) {
			LOGGER.severe(e.getMessage());
		}
	}

	/**
	 * Run vre1.
	 *
	 * @param vreConn
	 *            the vre conn
	 * @param recordedDate
	 *            the recorded date
	 */
	public void runVREs(Connection vreConn, Timestamp recordedDate) {
		int stringID;

		try (PreparedStatement statement = vreConn.prepareStatement(VRE_DATASET_QUERY);) {
			statement.setTimestamp(1, recordedDate);
			try (ResultSet rset = statement.executeQuery();) {
				ExecutorService executor = Executors.newFixedThreadPool(CONCURRENT_PIPESIM_LICENCES);
				long start = System.currentTimeMillis();
				int rowCount = 0;
				while (rset.next()) {
					// VRE.exe -vre1 -modelUZ496L.bps -whp450 -wc10
					stringID = rset.getInt(STRING_ID);
					recordedDate = rset.getTimestamp(RECORDED_DATE);
					Double whp = rset.getObject(AVG_WHP) == null ? null : rset.getDouble(AVG_WHP);
					Double wcut = rset.getObject(WATER_CUT_LAB) == null ? null : rset.getDouble(WATER_CUT_LAB);
					Double pdgp = rset.getObject(AVG_DOWNHOLE_PRESSURE) == null ? null
							: rset.getDouble(AVG_DOWNHOLE_PRESSURE);
					Double gasInjRate = rset.getObject(AVG_GASLIFT_INJ_RATE) == null ? null
							: rset.getDouble(AVG_GASLIFT_INJ_RATE);
					Double hp = rset.getObject(AVG_HEADER_PRESSURE) == null ? null
							: rset.getDouble(AVG_HEADER_PRESSURE);
					Double choke = rset.getObject(CHOKE_SETTING) == null ? null : rset.getDouble(CHOKE_SETTING);

					List<String> params = new ArrayList<>();
					params.add(VRE_EXE_LOC);// executable
					params.add(ARG_VRE1);
					params.add(ARG_MODEL + rset.getString(PIPESIM_MODEL_LOC));
					params.add(ARG_WHP + whp);
					params.add(ARG_WATERCUT + wcut);
					// VRE2 , VRE3, VRE4
					if (pdgp != null) {
						params.add(ARG_VRE2);
						params.add(ARG_VRE3);
						params.add(ARG_VRE4);
						params.add(ARG_PDGP + pdgp);

						if (gasInjRate != null) {
							params.add(ARG_GAS_INJ_RATE + gasInjRate);
						}
					}
					// VRE5
					if (hp != null && choke != null) {
						params.add(ARG_VRE5);
						params.add(ARG_HEADER + hp);
						params.add(ARG_RESERVOIR + rset.getString(RESERVOIR_MODEL_LOC));
						if (choke != null) {
							params.add(ARG_BEANSIZE + choke);
						}
					}

					Runnable worker = new VREExeWorker(vreConn, params, stringID, whp, wcut, recordedDate);
					executor.execute(worker);
					rowCount++;
				}
				executor.shutdown();
				while (!executor.isTerminated()) {
				}
				long end = System.currentTimeMillis();
				double duration = (end - start) / 1000;
				LOGGER.info("Finished running VRE for " + rowCount + " strings in " + duration + " seconds");
			} catch (Exception e) {
				LOGGER.severe(e.getMessage());
			}
		} catch (Exception e) {
			LOGGER.severe(e.getMessage());
		}
	}

	/**
	 * Run VRE for duration.
	 *
	 * @param vreConn
	 *            the vre conn
	 * @param stringID
	 *            the string ID
	 * @param vresToRun
	 *            the vres to run
	 * @param startDate
	 *            the start date
	 * @param endDate
	 *            the end date
	 */
	public void runVREForDuration(Connection vreConn, Integer stringID, List<String> vresToRun, Timestamp startDate,
			Timestamp endDate) {

		// TODO: Model Resetting

		Double startFFV = 0.0, startResPres = 0.0, startHoldUPV = 0.0, startGOR = 0.0, startPI = 0.0;
		Double endFFV = 0.0, endResPres = 0.0, endHoldUPV = 0.0, endGOR = 0.0, endPI = 0.0;
		int rowCnt = 0;
		boolean resetFirst = false;
		try (PreparedStatement statement = vreConn.prepareStatement(VRE_MODEL_RESET_QUERY)) {
			statement.setInt(1, stringID);
			statement.setTimestamp(2, startDate);
			statement.setInt(3, stringID);
			try (ResultSet rset = statement.executeQuery()) {
				while (rset.next()) {
					rowCnt++;
					if (rset.getTimestamp(RECORDED_DATE) == startDate) {
						startFFV = rset.getObject(FRICTION_FACTOR) == null ? 0 : rset.getDouble(FRICTION_FACTOR);
						startResPres = rset.getObject(RESERVOIR_PRESSURE) == null ? 0
								: rset.getDouble(RESERVOIR_PRESSURE);
						startHoldUPV = rset.getObject(HOLDUP) == null ? 0 : rset.getDouble(HOLDUP);
						startGOR = rset.getObject(GOR) == null ? 0 : rset.getDouble(GOR);
						startPI = rset.getObject(PI) == null ? 0 : rset.getDouble(PI);
					} else {
						endFFV = rset.getObject(FRICTION_FACTOR) == null ? 0 : rset.getDouble(FRICTION_FACTOR);
						endResPres = rset.getObject(RESERVOIR_PRESSURE) == null ? 0
								: rset.getDouble(RESERVOIR_PRESSURE);
						endHoldUPV = rset.getObject(HOLDUP) == null ? 0 : rset.getDouble(HOLDUP);
						endGOR = rset.getObject(GOR) == null ? 0 : rset.getDouble(GOR);
						endPI = rset.getObject(PI) == null ? 0 : rset.getDouble(PI);
					}
				}
			} catch (Exception e) {
				LOGGER.severe(e.getMessage());
			}
		} catch (Exception e) {
			LOGGER.severe(e.getMessage());
		}

		if (rowCnt == 2) {
			resetFirst = true;
		} else {
			LOGGER.severe("Query did not fetch two records for model resetting");
		}

		try (PreparedStatement statement = vreConn.prepareStatement(VRE_DURATION_QUERY);) {
			statement.setInt(1, stringID);
			statement.setTimestamp(2, startDate);
			statement.setTimestamp(3, endDate);
			try (ResultSet rset = statement.executeQuery();) {

				long start = System.currentTimeMillis();
				int rowCount = 0;

				while (rset.next()) {
					// VRE.exe -vre1 -modelUZ496L.bps -whp450 -wc10
					Timestamp recordedDate = rset.getTimestamp(RECORDED_DATE);
					Double whp = rset.getObject(AVG_WHP) == null ? null : rset.getDouble(AVG_WHP);
					Double wcut = rset.getObject(WATER_CUT) == null ? null : rset.getDouble(WATER_CUT);
					Double pdgp = rset.getObject(AVG_DOWNHOLE_PRESSURE) == null ? null
							: rset.getDouble(AVG_DOWNHOLE_PRESSURE);
					Double gasInjRate = rset.getObject(AVG_GASLIFT_INJ_RATE) == null ? null
							: rset.getDouble(AVG_GASLIFT_INJ_RATE);
					Double hp = rset.getObject(AVG_HEADER_PRESSURE) == null ? null
							: rset.getDouble(AVG_HEADER_PRESSURE);
					Double choke = rset.getObject(CHOKE_SETTING) == null ? null : rset.getDouble(CHOKE_SETTING);

					Double qLiq = rset.getObject(QL1) == null ? null : rset.getDouble(QL1);

					Boolean recal = rset.getObject(RECAL) == null ? null : rset.getBoolean(RECAL);

					List<String> params = new ArrayList<>();
					params.add(VRE_EXE_LOC);// executable
					if (vresToRun.contains(VRE_TYPE.VRE1.name())) {
						params.add(ARG_VRE1);
					}
					params.add(ARG_MODEL + rset.getString(PIPESIM_MODEL_LOC));
					params.add(ARG_WHP + whp);
					params.add(ARG_WATERCUT + wcut);

					if (recal != null && recal) {
						LOGGER.info("Executing recalibration for " + stringID + " for " + recordedDate);
						params.add(ARG_TEST_LIQ_RATE + qLiq);
					}

					if (resetFirst) {
						// VRE.exe -vre1... -gor270 -resp2090 -ffv1.1 -hhv0.92
						// -index1.57
						resetFirst = false;
						params.add(ARG_VERTICAL_FRICTION_FACTOR + startFFV);
						params.add(ARG_RES_STATIC_PRESSURE + startResPres);
						params.add(ARG_VERTICAL_HOLDUP_FACTOR + startHoldUPV);
						params.add(ARG_GAS_OIL_RATIO + startGOR);
						params.add(ARG_PRODUCTIVITY_INDEX + startPI);
					}

					params = new ArrayList<>();
					// VRE2 , VRE3, VRE4
					if (pdgp != null) {
						if (vresToRun.contains(VRE_TYPE.VRE2.name())) {
							params.add(ARG_VRE2);
						}
						if (vresToRun.contains(VRE_TYPE.VRE3.name())) {
							params.add(ARG_VRE3);
						}
						if (vresToRun.contains(VRE_TYPE.VRE4.name())) {
							params.add(ARG_VRE4);
						}
						params.add(ARG_PDGP + pdgp);

						if (gasInjRate != null) {
							params.add(ARG_GAS_INJ_RATE + gasInjRate);
						}
					}
					// VRE5
					if (hp != null && choke != null && vresToRun.contains(VRE_TYPE.VRE5.name())) {
						params.add(ARG_VRE5);
						params.add(ARG_HEADER + hp);
						params.add(ARG_RESERVOIR + rset.getString(RESERVOIR_MODEL_LOC));
						if (choke != null) {
							params.add(ARG_BEANSIZE + choke);
						}
					}
					rowCount++;
					// VREExeWorker vreExeWorker =
					new VREExeWorker(vreConn, params, stringID, whp, wcut, recordedDate);
					// vreExeWorker.executeVRE("runVREForDuration");
				}
				long end = System.currentTimeMillis();
				double duration = (end - start) / 1000;
				LOGGER.info("Finished running VRE for " + rowCount + " records in " + duration + " seconds");
				// TODO: Trigger BPM Email workflow from here
			} catch (Exception e) {
				LOGGER.severe(e.getMessage());
			}
		} catch (Exception e) {
			LOGGER.severe(e.getMessage());
		} finally {
			// TODO: Model Resetting
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
				ExecutorService executor = Executors.newFixedThreadPool(CONCURRENT_PIPESIM_LICENCES);
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
									LOGGER.info(" Submitted job for - " + stringID);
									rowCount++;
								}
								if (review) {
									// TODO: Trigger email either here or BPM
								}

							} else {
								LOGGER.severe("No recal tag present in output for string - " + stringID);
							}
						} else {
							LOGGER.severe("Exception in calling recal - " + wellModel.getErrors());
						}
					} else {
						LOGGER.severe("Something went wrong while calling recal for string - " + stringID);
					}
					rset.updateBoolean(IS_CALIBRATED, isCalibrated);
					rset.updateString(ROW_CHANGED_BY, RECAL_WORKFLOW);
					rset.updateTimestamp(ROW_CHANGED_DATE, new Timestamp(new Date().getTime()));
					rset.updateRow();
				}
				executor.shutdown();
				// don't wait for executor to terminate
				if (rowCount != 0) {
					LOGGER.info("VRE6 jobs submitted for " + rowCount + " strings in on " + new Date());
				}
			}

		} catch (Exception e) {
			LOGGER.severe(e.getMessage());
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
				ResultSet.CONCUR_UPDATABLE)) {
			statement.setInt(1, stringID);
			StringModel stringModel = Utils.getStringModel(vreConn, stringID);
			try (ResultSet rset = statement.executeQuery()) {
				List<String> params = new ArrayList<>();
				params.add(VRE_EXE_LOC);
				params.add(ARG_VRE6);
				params.add(ARG_MODEL + stringModel.getPipesimModelLoc());
				params.add(ARG_OUTPUT_LOC + VRE6_OUTPUT_FOLDER);
				String outputFile = VRE6_OUTPUT_FOLDER + stringModel.getStringName() + JSON_EXTENSION;
				FileUtils.deleteQuietly(new File(outputFile));
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
						stmt.setString(4, remark);
						stmt.setString(5, RECAL_WORKFLOW);
						stmt.executeUpdate();
					} catch (Exception e) {
						LOGGER.severe(e.getMessage());
					}
				}
			} catch (Exception e) {
				LOGGER.severe(e.getMessage());
			}
		} catch (Exception e) {
			LOGGER.severe(e.getMessage());
		}
	}
}
