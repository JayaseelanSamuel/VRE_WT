package com.brownfield.vre.exe;

import static com.brownfield.vre.VREConstants.*;

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

import com.brownfield.vre.AlertHandler;
import com.brownfield.vre.Utils;
import com.brownfield.vre.VREConstants.DSIS_JOB_TYPE;
import com.brownfield.vre.VREConstants.DSRTA_JOB_TYPE;
import com.brownfield.vre.VREConstants.VRE_TYPE;
import com.brownfield.vre.exe.models.MultiRateTestModel;
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

				/*
				 * MultiRateTestModel mrTest =
				 * vreEx.runMultiRateWellTest(vreConn, 953, new Double[] {
				 * 3917.5, 583.2128 }, new Double[] { 346.1, 981.5 }, 5);
				 * System.out.println(mrTest);
				 * System.out.println(mrTest.getBhp1());
				 * System.out.println(mrTest.getBhp2());
				 * System.out.println(mrTest.getBhp3());
				 * System.out.println(mrTest.getBhp4());
				 */
			} catch (SQLException e) {
				LOGGER.severe(e.getMessage());
				e.printStackTrace();
			}

		} catch (ClassNotFoundException e) {
			LOGGER.severe(e.getMessage());
			e.printStackTrace();
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
					Double wcut = rset.getObject(WATER_CUT_LAB) == null ? 0 : rset.getDouble(WATER_CUT_LAB);
					Double pdgp = rset.getObject(AVG_DOWNHOLE_PRESSURE) == null ? null
							: rset.getDouble(AVG_DOWNHOLE_PRESSURE);
					Double gasInjRate = rset.getObject(AVG_GASLIFT_INJ_RATE) == null ? null
							: rset.getDouble(AVG_GASLIFT_INJ_RATE);
					Double hp = rset.getObject(AVG_HEADER_PRESSURE) == null ? null
							: rset.getDouble(AVG_HEADER_PRESSURE);
					Double choke = rset.getObject(CHOKE_SETTING) == null ? null : rset.getDouble(CHOKE_SETTING);
					Double chokeMultiplier = rset.getObject(CHOKE_MULTIPLIER) == null ? 1
							: rset.getDouble(CHOKE_MULTIPLIER);
					Boolean isSeabed = rset.getObject(IS_SEABED) == null ? null : rset.getBoolean(IS_SEABED);

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
						params.add(ARG_BEANSIZE + choke);
						params.add(ARG_CHOKE_MULTIPLIER + chokeMultiplier);
					}

					Runnable worker = new VREExeWorker(vreConn, params, stringID, wcut, recordedDate, chokeMultiplier,
							isSeabed);
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
				e.printStackTrace();
			}
		} catch (Exception e) {
			LOGGER.severe(e.getMessage());
			e.printStackTrace();
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
	 * @param pi
	 *            the pi
	 * @param reservoirPressure
	 *            the reservoir pressure
	 * @param holdUPV
	 *            the hold upv
	 * @param ffv
	 *            the ffv
	 * @param chokeMultiplier
	 *            the choke multiplier
	 * @param user
	 *            the user
	 */
	public void runVREForDuration(Connection vreConn, int stringID, List<String> vresToRun, Timestamp startDate,
			Timestamp endDate, double pi, double reservoirPressure, double holdUPV, double ffv, double chokeMultiplier,
			String user) {

		Double endFFV = ffv, endResPres = reservoirPressure, endHoldUPV = holdUPV, endPI = pi;
		double endWHP = 0, endWcut = 0;
		boolean firstRecord = true, resetLast = false;
		String pipesimLoc = null;
		StringModel stringModel = Utils.getStringModel(vreConn, stringID);

		try (PreparedStatement statement = vreConn.prepareStatement(VRE_MODEL_RESET_QUERY)) {
			statement.setInt(1, stringID);
			// statement.setTimestamp(2, startDate);
			statement.setInt(2, stringID);
			try (ResultSet rset = statement.executeQuery()) {
				if (rset.next()) {
					Timestamp vreLastDate = rset.getTimestamp(RECORDED_DATE);
					// reset model to latest value if endDate is less than vre
					// latest date
					// if (endDate.compareTo(vreLastDate) < 0) {
					if (Utils.getDifferenceBetweenTwoDates(endDate, vreLastDate) > RECAL_DATE_DIFF) {
						resetLast = true;
						endFFV = rset.getObject(FRICTION_FACTOR) == null ? 0 : rset.getDouble(FRICTION_FACTOR);
						endResPres = rset.getObject(RESERVOIR_PRESSURE) == null ? 0
								: rset.getDouble(RESERVOIR_PRESSURE);
						endHoldUPV = rset.getObject(HOLDUP) == null ? 0 : rset.getDouble(HOLDUP);
						endPI = rset.getObject(PI) == null ? 0 : rset.getDouble(PI);
					}
				}
			} catch (Exception e) {
				LOGGER.severe(e.getMessage());
				e.printStackTrace();
			}
		} catch (Exception e) {
			LOGGER.severe(e.getMessage());
			e.printStackTrace();
		}

		try (PreparedStatement statement = vreConn.prepareStatement(VRE_DURATION_QUERY);) {
			statement.setInt(1, stringID);
			statement.setTimestamp(2, startDate);
			statement.setTimestamp(3, endDate);
			try (ResultSet rset = statement.executeQuery();) {

				long start = System.currentTimeMillis();
				int rowCount = 0;
				long durationInDays = Utils.getDifferenceBetweenTwoDates(startDate, endDate);
				Timestamp startedOn = new Timestamp(new Date().getTime());

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
					Boolean isSeabed = rset.getObject(IS_SEABED) == null ? null : rset.getBoolean(IS_SEABED);

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
						params.add(ARG_RECALIBRATE_LOW + RECALIBRATE_LOW);// RECALIBRATE_FORCE_LOW
						params.add(ARG_RECALIBRATE_HIGH + RECALIBRATE_HIGH);// RECALIBRATE_FORCE_LOW
					}

					if (firstRecord) {
						// VRE.exe -vre1... -gor270 -resp2090 -ffv1.1 -hhv0.92
						// -index1.57
						LOGGER.info(" Resetting model for first day of duration - PI : " + pi + " , ResPressure : "
								+ reservoirPressure);
						firstRecord = false;
						params.add(ARG_VERTICAL_FRICTION_FACTOR + ffv);
						params.add(ARG_RES_STATIC_PRESSURE + reservoirPressure);
						params.add(ARG_VERTICAL_HOLDUP_FACTOR + holdUPV);
						// params.add(ARG_GAS_OIL_RATIO + startGOR);
						params.add(ARG_PRODUCTIVITY_INDEX + pi);

						if (resetLast) {
							// doesn't matter what whp and wcuts are because
							// they are just fillers for model reset
							pipesimLoc = rset.getString(PIPESIM_MODEL_LOC);
							endWHP = whp;
							endWcut = wcut;
						}
					}

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
						params.add(ARG_BEANSIZE + choke);
						params.add(ARG_CHOKE_MULTIPLIER + chokeMultiplier);
					}
					rowCount++;
					this.insertOrUpdateVreExeJobs(vreConn, stringID, startDate, endDate, durationInDays, Boolean.TRUE,
							0, startedOn, null, DEFAULT_REMARK, user);
					VREExeWorker vreExeWorker = new VREExeWorker(vreConn, params, stringID, wcut, recordedDate,
							chokeMultiplier, isSeabed);
					vreExeWorker.executeVRE("runVREForDuration");

				}
				long end = System.currentTimeMillis();
				double duration = (end - start) / 1000;
				LOGGER.info("Finished running VREDuration for " + rowCount + " records in " + duration + " seconds");

				String wellMonitorDaily = String.format(WELLS_DASHBOARD_LINK, MONITOR_DAILY_DASHBOARD, stringID,
						Utils.convertToString(startDate, DATE_FORMAT_DSPM),
						Utils.convertToString(endDate, DATE_FORMAT_DSPM));

				AlertHandler.notifyByEmail(EMAIL_GROUP, DSBPM_ALERT_TEMPLATE, APP_BASE_URL + wellMonitorDaily,
						String.format(DSBPM_VRE_DURATION_SUBJECT, stringModel.getStringName(), startDate, endDate),
						String.format(DSBPM_VRE_DURATION_BODY, duration, stringModel.getStringName(), startDate,
								endDate, user));

			} catch (Exception e) {
				LOGGER.severe(e.getMessage());
				e.printStackTrace();
			}
		} catch (Exception e) {
			LOGGER.severe(e.getMessage());
			e.printStackTrace();
		} finally {
			Timestamp currTime = new Timestamp(new Date().getTime());
			// mark the job as done
			this.insertOrUpdateVreExeJobs(vreConn, stringID, startDate, endDate, 0, Boolean.TRUE, 0, currTime, currTime,
					DEFAULT_REMARK, user);

			// Reset model back to latest values if this is historic
			// recalculation
			if (resetLast) {
				LOGGER.info(
						" Resetting model back to latest settings - PI : " + endPI + " , ResPressure : " + endResPres);
				List<String> params = new ArrayList<>();
				params.add(VRE_EXE_LOC);// executable
				params.add(ARG_VRE1);
				params.add(ARG_MODEL + pipesimLoc);
				params.add(ARG_WHP + endWHP);
				params.add(ARG_WATERCUT + endWcut);
				params.add(ARG_VERTICAL_FRICTION_FACTOR + endFFV);
				params.add(ARG_RES_STATIC_PRESSURE + endResPres);
				params.add(ARG_VERTICAL_HOLDUP_FACTOR + endHoldUPV);
				params.add(ARG_PRODUCTIVITY_INDEX + endPI);
				VREExeWorker.runVRE(params);
			} else {
				// model might have been calibrated..better update proxy model
				ExecutorService executor = Executors.newFixedThreadPool(CONCURRENT_PIPESIM_LICENCES - 1);
				this.insertOrUpdateVRE6Job(vreConn, stringModel, executor);
				executor.shutdown();
				while (!executor.isTerminated()) {
				}
			}
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
				// leave 1 license for main thread which will calibrate the well
				// model
				ExecutorService executor = Executors.newFixedThreadPool(CONCURRENT_PIPESIM_LICENCES - 1);
				int rowCount = 0;
				while (rset.next()) {
					stringID = rset.getInt(STRING_ID);
					double whp = rset.getDouble(WHP1);
					double wcut = rset.getDouble(TEST_WATER_CUT);
					double qLiq = rset.getDouble(QL1);
					Date effectiveTestDate = rset.getDate(EFFECTIVE_DATE);
					StringModel stringModel = Utils.getStringModel(vreConn, stringID);
					List<String> params = new ArrayList<>();
					params.add(VRE_EXE_LOC);// executable
					params.add(ARG_VRE1);
					params.add(ARG_MODEL + rset.getString(PIPESIM_MODEL_LOC));
					params.add(ARG_WHP + whp);
					params.add(ARG_WATERCUT + wcut);
					params.add(ARG_TEST_LIQ_RATE + qLiq);
					params.add(ARG_RECALIBRATE_LOW + RECALIBRATE_LOW);
					params.add(ARG_RECALIBRATE_HIGH + RECALIBRATE_HIGH);
					LOGGER.info("Running recalibation for " + stringID);
					WellModel wellModel = VREExeWorker.runVRE(params);
					boolean isCalibrated = false;
					if (wellModel != null) {
						if (wellModel.getErrors() == null) {
							RecalModel recal = wellModel.getRecal();
							if (recal != null) {
								isCalibrated = recal.getCalibration();
								boolean review = recal.isReview();
								LOGGER.info("Model calibration flag " + isCalibrated + " review flag : " + review);
								if (isCalibrated) {
									this.insertOrUpdateVRE6Job(vreConn, stringModel, executor);
									LOGGER.info(" VRE6 job submitted for - " + stringID);
									rowCount++;
								}
								if (review) {
									// TODO: RECAL DONE

									String singleRateWellTest = String.format(WELLS_DASHBOARD_LINK,
											SINGLE_RATE_WELL_TEST_DASHBOARD, stringID,
											Utils.convertToString(effectiveTestDate, DATE_FORMAT_DSPM),
											Utils.convertToString(effectiveTestDate, DATE_FORMAT_DSPM));

									AlertHandler.notifyByEmail(EMAIL_GROUP, DSBPM_ALERT_TEMPLATE,
											APP_BASE_URL + singleRateWellTest,
											String.format(DSBPM_WT_CALIBRATION_SUBJECT, stringModel.getStringName()),
											String.format(DSBPM_WT_CALIBRATION_BODY,
													DECIMAL_FORMAT.format(recal.getError()),
													stringModel.getStringName(), effectiveTestDate, RECALIBRATE_LOW,
													RECALIBRATE_HIGH));

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
				if (rowCount != 0) {
					LOGGER.info("VRE6 jobs submitted for " + rowCount + " strings in on " + new Date());
				}
				executor.shutdown();
				while (!executor.isTerminated()) {
				}
			}

		} catch (Exception e) {
			LOGGER.severe(e.getMessage());
			e.printStackTrace();
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
	private void insertOrUpdateVRE6Job(Connection vreConn, StringModel stringModel, ExecutorService executor) {
		try (PreparedStatement statement = vreConn.prepareStatement(VRE6_JOBS_QUERY, ResultSet.TYPE_SCROLL_SENSITIVE,
				ResultSet.CONCUR_UPDATABLE)) {
			statement.setInt(1, stringModel.getStringID());
			try (ResultSet rset = statement.executeQuery()) {
				List<String> params = new ArrayList<>();
				params.add(VRE_EXE_LOC);
				params.add(ARG_VRE6);
				params.add(ARG_MODEL + stringModel.getPipesimModelLoc());
				params.add(ARG_OUTPUT_LOC + VRE6_OUTPUT_FOLDER);
				String outputFile = VRE6_OUTPUT_FOLDER + stringModel.getStringName() + JSON_EXTENSION;
				FileUtils.deleteQuietly(new File(outputFile));
				Runnable worker = new VREExeWorker(params, stringModel.getStringID(), VRE_TYPE.VRE6);
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
					try (PreparedStatement stmt = vreConn.prepareStatement(INSERT_VRE6_JOBS_QUERY);) {
						stmt.setInt(1, stringModel.getStringID());
						stmt.setInt(2, stringModel.getStringCategoryID());
						stmt.setInt(3, DSIS_JOB_TYPE.IN_PROGRESS.getNumVal());
						stmt.setInt(4, DSRTA_JOB_TYPE.INVALID.getNumVal());
						stmt.setString(5, remark);
						stmt.setString(6, RECAL_WORKFLOW);
						stmt.executeUpdate();
					} catch (Exception e) {
						LOGGER.severe(e.getMessage());
						e.printStackTrace();
					}
				}
			} catch (Exception e) {
				LOGGER.severe(e.getMessage());
				e.printStackTrace();
			}
		} catch (Exception e) {
			LOGGER.severe(e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * Insert or update vre exe jobs.
	 *
	 * @param vreConn
	 *            the vre conn
	 * @param stringID
	 *            the string id
	 * @param fromDate
	 *            the from date
	 * @param toDate
	 *            the to date
	 * @param duration
	 *            the duration
	 * @param isRunning
	 *            the is running
	 * @param currentCounter
	 *            the current counter
	 * @param startedOn
	 *            the started on
	 * @param completedOn
	 *            the completed on
	 * @param remark
	 *            the remark
	 * @param rowCreatedBy
	 *            the row created by
	 */
	private void insertOrUpdateVreExeJobs(Connection vreConn, Integer stringID, Timestamp fromDate, Timestamp toDate,
			long duration, boolean isRunning, int currentCounter, Timestamp startedOn, Timestamp completedOn,
			String remark, String rowCreatedBy) {
		int rowsInserted = 0;

		try (PreparedStatement statement = vreConn.prepareStatement(VRE_EXE_RUNNING_QUERY,
				ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE)) {
			statement.setInt(1, stringID);
			try (ResultSet rset = statement.executeQuery()) {
				if (rset.next()) {
					int counter = rset.getInt(CURRENT_COUNTER);
					counter++;
					rset.updateInt(CURRENT_COUNTER, counter);
					rset.updateTimestamp(ROW_CHANGED_DATE, new Timestamp(new Date().getTime()));

					// if there is already a job; update
					if (completedOn == null) {
						// running job; update counter
						LOGGER.info(" Recalculation job in progress for " + stringID + " with current counter =  "
								+ counter);
					} else { // finished job
						LOGGER.info(" Recalculation job finished for " + stringID + " on " + completedOn);
						rset.updateBoolean(IS_RUNNING, Boolean.FALSE);
						rset.updateTimestamp(COMPLETED_ON, completedOn);
					}
					rset.updateRow();
				} else {
					try (PreparedStatement stmt = vreConn.prepareStatement(INSERT_VRE_EXE_JOBS_QUERY);) {
						stmt.setInt(1, stringID);
						stmt.setTimestamp(2, fromDate);
						stmt.setTimestamp(3, toDate);
						stmt.setLong(4, duration);
						stmt.setBoolean(5, isRunning);
						stmt.setInt(6, currentCounter);
						stmt.setTimestamp(7, startedOn);
						stmt.setString(8, remark);
						stmt.setString(9, rowCreatedBy);
						rowsInserted = stmt.executeUpdate();
						LOGGER.info(rowsInserted + " rows inserted in VRE_EXE_JOBS table with String : " + stringID
								+ " & fromDate : " + fromDate + " & toDate : " + toDate);
					} catch (Exception e) {
						LOGGER.severe(e.getMessage());
						e.printStackTrace();
					}
				}
			} catch (Exception e) {
				LOGGER.severe(e.getMessage());
				e.printStackTrace();
			}
		} catch (Exception e) {
			LOGGER.severe(e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * Generate proxy models.
	 *
	 * @param vreConn
	 *            the vre conn
	 */
	public void refreshProxyModels(Connection vreConn) {

		try (PreparedStatement statement = vreConn.prepareStatement(VRE6_PROXY_MODELS_QUERY)) {

			try (ResultSet rset = statement.executeQuery()) {
				ExecutorService executor = Executors.newFixedThreadPool(CONCURRENT_PIPESIM_LICENCES);
				int rowCount = 0;
				int stringID;
				while (rset.next()) {
					stringID = rset.getInt(STRING_ID);
					StringModel stringModel = Utils.getStringModel(vreConn, stringID);
					this.insertOrUpdateVRE6Job(vreConn, stringModel, executor);
					LOGGER.info("Proxy model generation submitted for - " + stringID);
					rowCount++;
				}
				if (rowCount != 0) {
					LOGGER.info("Proxy model generation submitted for " + rowCount + " strings in on " + new Date());
				}
				executor.shutdown();
				while (!executor.isTerminated()) {
				}
			} catch (Exception e) {
				LOGGER.severe(e.getMessage());
				e.printStackTrace();
			}
		} catch (Exception e) {
			LOGGER.severe(e.getMessage());
			e.printStackTrace();
		}

	}

	/**
	 * Run multi rate well test.
	 *
	 * @param vreConn
	 *            the vre conn
	 * @param stringID
	 *            the string id
	 * @param liqRates
	 *            the liq rates
	 * @param whps
	 *            the whps
	 * @param wcut
	 *            the wcut
	 */
	public MultiRateTestModel runMultiRateWellTest(Connection vreConn, Integer stringID, Double[] liqRates,
			Double[] whps, double wcut) {

		try {
			StringModel stringModel = Utils.getStringModel(vreConn, stringID);
			List<String> params = new ArrayList<>();
			params.add(VRE_EXE_LOC);// executable
			params.add(ARG_MODEL + stringModel.getPipesimModelLoc());
			params.add(ARG_WATERCUT + wcut);

			for (int i = 1; i <= liqRates.length; i++) {
				params.add(ARG_MULTI_LIQ_RATE + i + liqRates[i - 1]);
			}

			for (int i = 1; i <= whps.length; i++) {
				params.add(ARG_MULTI_WHP + i + whps[i - 1]);
			}

			WellModel wellModel = VREExeWorker.runVRE(params);
			if (wellModel != null) {
				if (wellModel.getErrors() == null) {
					MultiRateTestModel multiRateTest = wellModel.getMultiRateTest();
					if (multiRateTest != null) {
						return multiRateTest;
					} else {
						LOGGER.severe("No multirate well test tag present in output for string - " + stringID);
					}
				} else {
					LOGGER.severe("Exception in calling multirate well test - " + wellModel.getErrors());
				}
			} else {
				LOGGER.severe("Something went wrong while calling multirate well test for string - " + stringID);
			}
		} catch (Exception e) {
			LOGGER.severe(e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Run model prediction.
	 *
	 * @param vreConn
	 *            the vre conn
	 */
	public void runModelPrediction(Connection vreConn) {
		Timestamp currTime = Utils.getRoundedOffTime(new Timestamp(new Date().getTime()), PREDICTION_FREQUENCY);
		String currDate = Utils.convertToString(currTime, DATE_FORMAT);

		try (PreparedStatement statement = vreConn.prepareStatement(SELECT_LATEST_WHP_QUERY)) {
			statement.setString(1, currDate);
			try (ResultSet rset = statement.executeQuery()) {
				ExecutorService executor = Executors.newFixedThreadPool(MODEL_PREDICTION_THREAD_COUNT);
				int rowCount = 0;
				int stringID;
				Timestamp whpDate;
				Double whp, wcut;
				while (rset.next()) {
					stringID = rset.getInt(STRING_ID);
					whpDate = rset.getTimestamp(RECORDED_DATE);
					whp = rset.getDouble(TAGRAWVALUE);
					wcut = rset.getDouble(WATER_CUT_LAB);
					LOGGER.info("Generating prediction for - " + stringID);
					StringModel stringModel = Utils.getStringModel(vreConn, stringID);

					List<String> params = new ArrayList<>();
					params.add(VRE_EXE_LOC);
					params.add(ARG_VRE6);
					params.add(ARG_MODEL + stringModel.getPipesimModelLoc());
					params.add(ARG_WHP + whp);
					params.add(ARG_WATERCUT + wcut);

					Runnable worker = new VREExeWorker(vreConn, params, stringID, whp, wcut, whpDate, currTime,
							VRE_TYPE.MODEL_PREDICTION);
					executor.execute(worker);

					rowCount++;
				}
				if (rowCount != 0) {
					LOGGER.info("Prediction generation submitted for " + rowCount + " strings in on " + new Date());
				}
				executor.shutdown();
				while (!executor.isTerminated()) {
				}
			} catch (Exception e) {
				LOGGER.severe(e.getMessage());
				e.printStackTrace();
			}
		} catch (Exception e) {
			LOGGER.severe(e.getMessage());
			e.printStackTrace();
		}
	}

}
