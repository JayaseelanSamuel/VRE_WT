package com.brownfield.vre.exe;

import static com.brownfield.vre.VREConstants.*;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import com.brownfield.vre.AlertHandler;
import com.brownfield.vre.TagType;
import com.brownfield.vre.Utils;
import com.brownfield.vre.VREConstants.ALERT_TYPE;
import com.brownfield.vre.VREConstants.SOURCE;
import com.brownfield.vre.VREConstants.VRE_TYPE;
import com.brownfield.vre.exe.models.StringModel;
import com.brownfield.vre.exe.models.WellModel;

// TODO: Auto-generated Javadoc
/**
 * The Class VREExecutioner.
 * 
 * @author Onkar Dhuri <onkar.dhuri@synerzip.com>
 */
public class VREExeWorker implements Runnable {

	/** The logger. */
	private static final Logger LOGGER = Logger.getLogger(VREExeWorker.class.getName());

	/** The vre conn. */
	private Connection vreConn;

	/** The params. */
	private List<String> params;

	/** The string id. */
	private int stringID;

	/** The wcut. */
	private double wcut;

	/** The recorded date. */
	private Timestamp recordedDate;

	/** The choke multiplier. */
	private double chokeMultiplier;

	/** The is seabed. */
	private boolean isSeabed;

	/** The vre type. */
	private VRE_TYPE vreType;

	/** The whp. */
	private double whp;

	/** The whp date. */
	private Timestamp whpDate;

	/**
	 * Instantiates a new VRE exe worker.
	 *
	 * @param vreConn
	 *            the vre conn
	 * @param params
	 *            the params
	 * @param stringID
	 *            the string id
	 * @param wcut
	 *            the wcut
	 * @param recordedDate
	 *            the recorded date
	 * @param chokeMultiplier
	 *            the choke multiplier
	 * @param isSeabed
	 *            the is seabed
	 */
	public VREExeWorker(Connection vreConn, List<String> params, int stringID, double wcut, Timestamp recordedDate,
			double chokeMultiplier, boolean isSeabed) {
		this.vreConn = vreConn;
		this.params = params;
		this.stringID = stringID;
		this.wcut = wcut;
		this.recordedDate = recordedDate;
		this.chokeMultiplier = chokeMultiplier;
		this.isSeabed = isSeabed;
	}

	/**
	 * Instantiates a new VRE exe worker for VRE6 output calculation.
	 *
	 * @param params
	 *            the params
	 * @param stringID
	 *            the string id
	 * @param vreType
	 *            the vre type
	 */
	public VREExeWorker(List<String> params, int stringID, VRE_TYPE vreType) {
		this.params = params;
		this.stringID = stringID;
		this.vreType = vreType;
	}

	/**
	 * Instantiates a new VRE exe worker for model prediction.
	 *
	 * @param vreConn
	 *            the vre conn
	 * @param params
	 *            the params
	 * @param stringID
	 *            the string id
	 * @param whp
	 *            the whp
	 * @param wcut
	 *            the wcut
	 * @param whpDate
	 *            the whp date
	 * @param recordedDate
	 *            the recorded date
	 * @param vreType
	 *            the vre type
	 */
	public VREExeWorker(Connection vreConn, List<String> params, int stringID, double whp, double wcut,
			Timestamp whpDate, Timestamp recordedDate, VRE_TYPE vreType) {
		this.vreConn = vreConn;
		this.params = params;
		this.stringID = stringID;
		this.whp = whp;
		this.wcut = wcut;
		this.whpDate = whpDate;
		this.recordedDate = recordedDate;
		this.vreType = vreType;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		String threadName = Thread.currentThread().getName();
		if (this.vreType == VRE_TYPE.VRE6) {
			this.executeVRE6(threadName);
		} else if (this.vreType == VRE_TYPE.MODEL_PREDICTION) {
			this.executeModelPrediction(threadName);
		} else {
			this.executeVRE(threadName);
		}
	}

	/**
	 * Execute vres.
	 *
	 * @param threadName
	 *            the thread name
	 */
	protected void executeVRE(String threadName) {
		LOGGER.info(threadName + " : Started to run VRE for " + this.stringID);
		long startT = System.currentTimeMillis();
		WellModel wellModel = runVRE(params);
		long endT = System.currentTimeMillis();
		double duration = ((endT - startT) / 1000);
		LOGGER.info(threadName + " : Time to run VRE : " + duration);
		if (wellModel != null) {
			if (wellModel.getError() == null || wellModel.getError().isEmpty()) {
				LOGGER.info(threadName + " : Time reported in VRE : " + wellModel.getTime());
				this.insertOrUpdateVRE(stringID, wcut, wellModel, recordedDate, vreConn, chokeMultiplier, isSeabed);
			} else {
				LOGGER.severe(threadName + " : Exception in calling VRE - " + wellModel.getErrors());
				this.insertOrUpdateVRERemark(vreConn, stringID, recordedDate, wellModel.getError());
			}
		} else {
			LOGGER.severe(threadName + " : Something went wrong while calling VRE for string - " + stringID);
			// TODO: Constant later
			this.insertOrUpdateVRERemark(vreConn, stringID, recordedDate, "Something went wrong calling VRE");
		}
		LOGGER.info(threadName + " Finished VRE for " + this.stringID);
	}

	/**
	 * Execute VRE 6.
	 *
	 * @param threadName
	 *            the thread name
	 */
	private void executeVRE6(String threadName) {
		LOGGER.info(threadName + " : Started to run VRE6 for " + this.stringID);
		long startT = System.currentTimeMillis();
		runVRE(params);
		long endT = System.currentTimeMillis();
		double duration = ((endT - startT) / 1000);
		LOGGER.info(threadName + " Finished VRE6 for " + this.stringID + " in " + duration + " seconds");
	}

	/**
	 * Execute model prediction.
	 *
	 * @param threadName
	 *            the thread name
	 */
	private void executeModelPrediction(String threadName) {
		LOGGER.info(threadName + " : Started to model prediction for " + this.stringID);
		long startT = System.currentTimeMillis();
		WellModel wellModel = runVRE(params);
		long endT = System.currentTimeMillis();
		double duration = ((endT - startT) / 1000);
		LOGGER.info(threadName + " : Time to run model prediction : " + duration);
		if (wellModel != null) {
			if (wellModel.getError() == null || wellModel.getError().isEmpty()) {
				LOGGER.info(threadName + " : Time reported in model prediction : " + wellModel.getTime());
				this.insertOrUpdateModelPrediction(stringID, whp, wellModel, recordedDate, vreConn, whpDate);
			} else {
				LOGGER.severe(threadName + " : Exception in calling model prediction - " + wellModel.getErrors());
			}
		} else {
			LOGGER.severe(threadName + " : Something went wrong while predicting model for string - " + stringID);
		}
		LOGGER.info(threadName + " Finished model prediction for " + this.stringID);
	}
	// TODO: Onkar : Remove unnecessary arguments like connection

	/**
	 * Run vre.
	 *
	 * @param params
	 *            the parameters
	 * @return the well model
	 */
	public static WellModel runVRE(List<String> params) {
		LOGGER.info(Thread.currentThread().getName() + " : Running VRE with " + "\n" + params);
		WellModel wellModel = null;
		try {
			ProcessBuilder pb = new ProcessBuilder(params);

			Process p = pb.start();

			InputStream input = p.getInputStream();
			// InputStream error = p.getErrorStream();
			// OutputStream output = p.getOutputStream();

			JAXBContext jaxbContext = JAXBContext.newInstance(WellModel.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			wellModel = (WellModel) jaxbUnmarshaller.unmarshal(input);
		} catch (IOException e) {
			LOGGER.severe("Exception occurred while exeuting system command");
			LOGGER.severe(e.getMessage());
			e.printStackTrace();
		} catch (JAXBException e) {
			LOGGER.severe("Exception occurred while parsing");
			LOGGER.severe(e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			LOGGER.severe(e.getMessage());
			e.printStackTrace();
		}
		return wellModel;
	}

	/**
	 * Insert or update vre.
	 *
	 * @param stringID
	 *            the string id
	 * @param wcut
	 *            the wcut
	 * @param model
	 *            the model
	 * @param recordedDate
	 *            the recorded date
	 * @param vreConn
	 *            the vre conn
	 * @param chokeMuliplier
	 *            the choke muliplier
	 * @param isSeabed
	 *            the is seabed
	 */
	private void insertOrUpdateVRE(int stringID, double wcut, WellModel model, Timestamp recordedDate,
			Connection vreConn, double chokeMuliplier, boolean isSeabed) {
		try (PreparedStatement statement = vreConn.prepareStatement(VRE_TABLE_SELECT_QUERY,
				ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE)) {

			statement.setTimestamp(1, recordedDate);
			statement.setInt(2, stringID);
			StringModel stringModel = Utils.getStringModel(vreConn, stringID);
			Double vre = null;
			try (ResultSet rset = statement.executeQuery()) {
				Double vre1LiqRate = null;
				if (model.getVre1() != null) {
					vre1LiqRate = model.getVre1().getRateLiquid();
				}
				Double vre2LiqRate = null;
				if (model.getVre2() != null) {
					vre2LiqRate = model.getVre2().getRateLiquid();
				}
				Double vre3LiqRate = null;
				if (model.getVre3() != null) {
					vre3LiqRate = model.getVre3().getRateLiquid();
				}
				Double vre4LiqRate = null;
				if (model.getVre4() != null) {
					vre4LiqRate = model.getVre4().getRateLiquid();
				}
				Double vre5LiqRate = null;
				if (model.getVre5() != null) {
					vre5LiqRate = model.getVre5().getRateLiquid();
				}
				Double vre6LiqRate = null;

				double ffv = model.getProperties().getFfv();
				double resPres = model.getProperties().getReservoirPressure();
				double holdUPV = model.getProperties().getHoldUPV();
				double gor = model.getProperties().getGor();
				double pi = model.getProperties().getPi();
				double ii = model.getProperties().getIi();
				// set pi to ii if pi is 0 (e.g. Injector wells)
				pi = pi != 0 ? pi : ii;
				if (rset.next()) { // record present, just update
					if (vre1LiqRate != null) {
						rset.updateDouble(VRE1, vre1LiqRate);
					}
					if (vre2LiqRate != null) {
						rset.updateDouble(VRE2, vre2LiqRate);
					}
					if (vre3LiqRate != null) {
						rset.updateDouble(VRE3, vre3LiqRate);
					}
					if (vre4LiqRate != null) {
						rset.updateDouble(VRE4, vre4LiqRate);
					}
					if (vre5LiqRate != null) {
						rset.updateDouble(VRE5, vre5LiqRate);
					}
					vre6LiqRate = rset.getDouble(VRE6);

					rset.updateDouble(WATER_CUT, wcut);
					rset.updateString(WATER_CUT_FLAG, DEFAULT_WATER_CUT);
					rset.updateDouble(PI, pi);
					rset.updateDouble(HOLDUP, holdUPV);
					rset.updateDouble(GOR, gor);
					rset.updateDouble(FRICTION_FACTOR, ffv);
					rset.updateDouble(RESERVOIR_PRESSURE, resPres);
					rset.updateDouble(CHOKE_MULTIPLIER, chokeMuliplier);
					rset.updateBoolean(IS_SEABED, isSeabed);

					// rset.updateString(REMARK, DEFAULT_REMARK);
					rset.updateString(ROW_CHANGED_BY, VRE_WORKFLOW);
					rset.updateTimestamp(ROW_CHANGED_DATE, new Timestamp(new Date().getTime()));
					rset.updateRow();
					LOGGER.info("updated row in VRE table with String : " + stringID + " & Date : " + recordedDate);
				} else { // insert
					this.insertVRERecord(vreConn, stringID, recordedDate, vre1LiqRate, vre2LiqRate, vre3LiqRate,
							vre4LiqRate, vre5LiqRate, null, wcut, DEFAULT_WATER_CUT, gor, pi, holdUPV, ffv, resPres,
							chokeMuliplier, isSeabed, DEFAULT_REMARK);
				}
				vre = this.getVRE(vreConn, stringModel, recordedDate, vre1LiqRate, vre2LiqRate, vre3LiqRate,
						vre4LiqRate, vre5LiqRate, vre6LiqRate);
				this.generateTechnicalAndModelAlerts(vreConn, stringModel, recordedDate, vre, gor, pi, holdUPV, ffv,
						resPres);
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
	 * Insert vre record.
	 *
	 * @param conn
	 *            the conn
	 * @param stringID
	 *            the string id
	 * @param recordedDate
	 *            the recorded date
	 * @param vre1
	 *            the vre1
	 * @param vre2
	 *            the vre2
	 * @param vre3
	 *            the vre3
	 * @param vre4
	 *            the vre4
	 * @param vre5
	 *            the vre5
	 * @param vre6
	 *            the vre6
	 * @param wcut
	 *            the wcut
	 * @param wcutFlag
	 *            the wcut flag
	 * @param gor
	 *            the gor
	 * @param pi
	 *            the pi
	 * @param holdUPV
	 *            the hold upv
	 * @param ffv
	 *            the ffv
	 * @param resPres
	 *            the res pres
	 * @param chokeMuliplier
	 *            the choke muliplier
	 * @param isSeabed
	 *            the is seabed
	 * @param remark
	 *            the remark
	 * @return the int
	 */
	private int insertVRERecord(Connection conn, int stringID, Timestamp recordedDate, Double vre1, Double vre2,
			Double vre3, Double vre4, Double vre5, Double vre6, Double wcut, String wcutFlag, Double gor, Double pi,
			Double holdUPV, Double ffv, Double resPres, Double chokeMuliplier, Boolean isSeabed, String remark) {
		int rowsInserted = 0;
		try (PreparedStatement statement = conn.prepareStatement(INSERT_VRE_QUERY);) {
			statement.setInt(1, stringID);
			statement.setTimestamp(2, recordedDate);
			statement.setInt(3, SOURCE.VRE.getNumVal());
			// statement.setDouble(4, vre1 != null ? vre1 : 0);
			// statement.setDouble(5, vre2 != null ? vre2 : 0);
			statement.setObject(4, vre1);
			statement.setObject(5, vre2);
			statement.setObject(6, vre3);
			statement.setObject(7, vre4);
			statement.setObject(8, vre5);
			statement.setObject(9, vre6);

			statement.setObject(10, wcut);
			statement.setObject(11, wcutFlag);
			statement.setObject(12, gor);
			statement.setObject(13, pi);
			statement.setObject(14, holdUPV);
			statement.setObject(15, ffv);
			statement.setObject(16, resPres);
			statement.setObject(17, chokeMuliplier != null ? chokeMuliplier : 1);
			statement.setObject(18, isSeabed != null ? isSeabed : false);
			statement.setObject(19, remark);
			statement.setObject(20, VRE_WORKFLOW);

			rowsInserted = statement.executeUpdate();
			LOGGER.info(rowsInserted + " rows inserted in VRE table with String : " + stringID + " & Date : "
					+ recordedDate);
		} catch (Exception e) {
			LOGGER.severe(e.getMessage());
			e.printStackTrace();
		}
		return rowsInserted;
	}

	/**
	 * Insert or update vre remark.
	 *
	 * @param conn the conn
	 * @param stringID the string id
	 * @param recordedDate the recorded date
	 * @param remark the remark
	 */
	private void insertOrUpdateVRERemark(Connection conn, int stringID, Timestamp recordedDate, String remark) {
		try (PreparedStatement statement = vreConn.prepareStatement(VRE_TABLE_SELECT_QUERY,
				ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE)) {

			statement.setTimestamp(1, recordedDate);
			statement.setInt(2, stringID);
			try (ResultSet rset = statement.executeQuery()) {
				if (rset.next()) {
					rset.updateString(REMARK, remark);
					rset.updateString(ROW_CHANGED_BY, VRE_WORKFLOW);
					rset.updateTimestamp(ROW_CHANGED_DATE, new Timestamp(new Date().getTime()));
					rset.updateRow();
					LOGGER.info("updated row for error message in VRE table with String : " + stringID + " & Date : "
							+ recordedDate);
				} else { // insert
					this.insertVRERecord(conn, stringID, recordedDate, remark);
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
	 * Insert vre record.
	 *
	 * @param conn the conn
	 * @param stringID the string id
	 * @param recordedDate the recorded date
	 * @param remark the remark
	 * @return the int
	 */
	private int insertVRERecord(Connection conn, int stringID, Timestamp recordedDate, String remark) {
		int rowsInserted = 0;
		try (PreparedStatement statement = conn.prepareStatement(INSERT_VRE_SHORT_QUERY);) {
			statement.setInt(1, stringID);
			statement.setTimestamp(2, recordedDate);
			statement.setObject(3, null);
			statement.setString(4, remark);
			statement.setString(5, VRE_WORKFLOW);
			rowsInserted = statement.executeUpdate();
			LOGGER.info(rowsInserted + " rows inserted with error message in VRE table for String : " + stringID
					+ " & Date : " + recordedDate);
		} catch (Exception e) {
			LOGGER.severe(e.getMessage());
			e.printStackTrace();
		}
		return rowsInserted;
	}

	/**
	 * Generate technical and model alerts.
	 *
	 * @param vreConn
	 *            the conn
	 * @param stringModel
	 *            the string model
	 * @param recordedDate
	 *            the recorded date
	 * @param vre
	 *            the vre
	 * @param gor
	 *            the gor
	 * @param pi
	 *            the pi
	 * @param holdUPV
	 *            the hold upv
	 * @param ffv
	 *            the ffv
	 * @param resPres
	 *            the res pres
	 * @return the int
	 */
	private int generateTechnicalAndModelAlerts(Connection vreConn, StringModel stringModel, Timestamp recordedDate,
			Double vre, Double gor, Double pi, Double holdUPV, Double ffv, Double resPres) {

		int rowsInserted = 0;
		try (PreparedStatement statement = vreConn.prepareStatement(SELECT_TECHNICAL_RATE_QUERY);) {
			statement.setInt(1, stringModel.getStringID());
			statement.setTimestamp(2, recordedDate);
			statement.setTimestamp(3, recordedDate);
			LOGGER.info("Checking technical rate difference for : " + stringModel.getStringID() + " & Date : "
					+ recordedDate);
			try (ResultSet rset = statement.executeQuery()) {
				if (rset.next()) { // only one record
					double lowTechRate = rset.getDouble(LOW_TECH_RATE);
					double highTechRate = rset.getDouble(HIGH_TECH_RATE);
					rowsInserted += generateAlert(vreConn, stringModel, TagType.TECHNICAL_RATE, recordedDate, vre,
							lowTechRate, highTechRate);
				}
			} catch (Exception e) {
				LOGGER.severe(e.getMessage());
				e.printStackTrace();
			}
		} catch (Exception e) {
			LOGGER.severe(e.getMessage());
			e.printStackTrace();
		}

		try (PreparedStatement statement = vreConn.prepareStatement(SELECT_ALERT_LIMIT_QUERY);) {
			statement.setTimestamp(1, recordedDate);
			statement.setInt(2, stringModel.getStringID());
			try (ResultSet rset = statement.executeQuery()) {
				LOGGER.info("Checking model rate difference for : " + stringModel.getStringID() + " & Date : "
						+ recordedDate);
				while (rset.next()) {
					int tagTypeID = rset.getInt(TAG_TYPE_ID);
					double lowLimit = rset.getDouble(LOW_LIMIT);
					double highLimit = rset.getDouble(HIGH_LIMIT);

					if (tagTypeID == TagType.GOR.getTagTypeID()) {
						rowsInserted += generateAlert(vreConn, stringModel, TagType.GOR, recordedDate, gor, lowLimit,
								highLimit);
						continue;
					}
					if (tagTypeID == TagType.PI.getTagTypeID()) {
						rowsInserted += generateAlert(vreConn, stringModel, TagType.PI, recordedDate, pi, lowLimit,
								highLimit);
						continue;
					}
					if (tagTypeID == TagType.HOLDUP.getTagTypeID()) {
						rowsInserted += generateAlert(vreConn, stringModel, TagType.HOLDUP, recordedDate, holdUPV,
								lowLimit, highLimit);
						continue;
					}
					if (tagTypeID == TagType.FRICTION_FACTOR.getTagTypeID()) {
						rowsInserted += generateAlert(vreConn, stringModel, TagType.FRICTION_FACTOR, recordedDate, ffv,
								lowLimit, highLimit);
						continue;
					}
					if (tagTypeID == TagType.RESERVOIR_PRESSURE.getTagTypeID()) {
						rowsInserted += generateAlert(vreConn, stringModel, TagType.RESERVOIR_PRESSURE, recordedDate,
								resPres, lowLimit, highLimit);
						continue;
					}
				}
			} 
			LOGGER.info(rowsInserted + " alerts were triggered for String : " + stringModel.getStringID()
					+ " & Date : " + recordedDate);
		} catch (Exception e) {
			LOGGER.severe(e.getMessage());
			e.printStackTrace();
		}
		return rowsInserted;

	}


	/**
	 * Prepare insert record.
	 *
	 * @param vreConn the vre conn
	 * @param stringName the string name
	 * @param tagType the tag type
	 * @param alertType the alert type
	 * @param msgType the msg type
	 * @param recordedDate the recorded date
	 * @param value the value
	 * @param limit the limit
	 * @return the string
	 */
	private String insertAlert(Connection vreConn, String stringName, TagType tagType, ALERT_TYPE alertType, String msgType,
			Timestamp recordedDate, Double value, double limit) {
		String message = String.format(STRING_ALERT_MESSAGE, tagType, value, stringName, msgType, limit,
				recordedDate);
		
		try(PreparedStatement ps = vreConn.prepareStatement(INSERT_ALERTS_QUERY);) {
			ps.setInt(1, stringID);
			ps.setInt(2, alertType.getAlertTypeID());
			ps.setInt(3, tagType.getTagTypeID());

			ps.setString(4, message);
			ps.setString(5, message);
			ps.setString(6, null); // additional data
			ps.setTimestamp(7, recordedDate);
			ps.setString(8, VRE_WORKFLOW);
			ps.executeUpdate();
		} catch (SQLException e) {
			LOGGER.severe(e.getMessage());
			// no need to print complete stack
		}
		return message;
	}


	/**
	 * Generate alert.
	 *
	 * @param vreConn the vre conn
	 * @param stringModel the string model
	 * @param tagType the tag type
	 * @param recordedDate the recorded date
	 * @param value the value
	 * @param lowLimit the low limit
	 * @param highLimit the high limit
	 * @return the int
	 */
	private int generateAlert(Connection vreConn, StringModel stringModel, TagType tagType, Timestamp recordedDate,
			Double value, double lowLimit, double highLimit) {
		String wellMonitorDailyAlert = String.format(WELLS_DASHBOARD_LINK, MONITOR_DAILY_ALERT_DASHBOARD,
				stringModel.getStringID(), Utils.convertToString(recordedDate, DATE_FORMAT_DSPM),
				Utils.convertToString(recordedDate, DATE_FORMAT_DSPM));
		if (value < lowLimit) {
			String message = this.insertAlert(vreConn, stringModel.getStringName(), tagType, ALERT_TYPE.LOWER, UPPER_LIMIT,
					recordedDate, value, lowLimit);
			/*
			 * AlertHandler.notifyByEmail(EMAIL_GROUP, DSBPM_ALERT_TEMPLATE,
			 * APP_BASE_URL + wellMonitorDailyAlert,
			 * String.format(DSBPM_ALERT_SUBJECT, tagType,
			 * stringModel.getStringName()), message);
			 */
			LOGGER.info("Generating " + ALERT_TYPE.LOWER + " alert for : " + tagType + " and string : "
					+ stringModel.getStringName() + " with value " + value + " and limit : " + lowLimit + " & Date : "
					+ recordedDate + " and emailing it to : " + EMAIL_GROUP);
			return 1;
		}
		if (value > highLimit) {
			String message = this.insertAlert(vreConn, stringModel.getStringName(), tagType, ALERT_TYPE.UPPER, LOWER_LIMIT,
					recordedDate, value, highLimit);
			/*
			 * AlertHandler.notifyByEmail(EMAIL_GROUP, DSBPM_ALERT_TEMPLATE,
			 * APP_BASE_URL + wellMonitorDailyAlert,
			 * String.format(DSBPM_ALERT_SUBJECT, tagType,
			 * stringModel.getStringName()), message);
			 */
			LOGGER.info("Generating " + ALERT_TYPE.UPPER + " alert for : " + tagType + " and string : "
					+ stringModel.getStringName() + " with value " + value + " and limit : " + highLimit + " & Date : "
					+ recordedDate + " and emailing it to : " + EMAIL_GROUP);
			return 1;
		}
		return 0;
	}

	/**
	 * Gets the vre.
	 *
	 * @param conn
	 *            the conn
	 * @param stringModel
	 *            the string model
	 * @param recordedDate
	 *            the recorded date
	 * @param vre1
	 *            the vre1
	 * @param vre2
	 *            the vre2
	 * @param vre3
	 *            the vre3
	 * @param vre4
	 *            the vre4
	 * @param vre5
	 *            the vre5
	 * @param vre6
	 *            the vre6
	 * @return the vre
	 */
	private double getVRE(Connection conn, StringModel stringModel, Timestamp recordedDate, Double vre1, Double vre2,
			Double vre3, Double vre4, Double vre5, Double vre6) {

		switch (stringModel.getSelectedVRE()) {
		case VRE1:
			return vre1;
		case VRE2:
			return vre2;
		case VRE3:
			return vre3;
		case VRE4:
			return vre4;
		case VRE5:
			return vre5;
		case VRE6:
			return vre6;
		default:
			return vre1;
		}
	}

	/**
	 * Insert or update model prediction.
	 *
	 * @param stringID
	 *            the string id
	 * @param whp
	 *            the whp
	 * @param model
	 *            the model
	 * @param recordedDate
	 *            the recorded date
	 * @param vreConn
	 *            the vre conn
	 * @param whpDate
	 *            the whp date
	 */
	private void insertOrUpdateModelPrediction(int stringID, double whp, WellModel model, Timestamp recordedDate,
			Connection vreConn, Timestamp whpDate) {
		try (PreparedStatement statement = vreConn.prepareStatement(MODEL_PREDECTION_QUERY,
				ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE)) {
			statement.setInt(1, stringID);
			statement.setTimestamp(2, recordedDate);
			try (ResultSet rset = statement.executeQuery()) {
				Double vre6LiqRate = null;
				if (model.getVre6() != null) {
					vre6LiqRate = model.getVre6().getRateLiquid();
				}
				if (rset.next()) { // record present, just update
					if (vre6LiqRate != null) {
						rset.updateDouble(TAGRAWVALUE, vre6LiqRate);
					}
					// rset.updateTimestamp(ROW_CHANGED_DATE, new Timestamp(new
					// Date().getTime()));
					rset.updateRow();
					LOGGER.info("updated row in real time data table with String : " + stringID + " & Date : "
							+ recordedDate);
				} else { // insert
					this.insertRealTimeData(vreConn, stringID, recordedDate, TagType.VRE6_CALC.getTagTypeID(),
							whpDate.toString(), whp, vre6LiqRate);
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
	 * Insert real time data.
	 *
	 * @param conn
	 *            the conn
	 * @param stringID
	 *            the string id
	 * @param recordedDate
	 *            the recorded date
	 * @param tagTypeID
	 *            the tag type id
	 * @param quality
	 *            the quality
	 * @param processedValue
	 *            the processed value
	 * @param rawValue
	 *            the raw value
	 * @return the int
	 */
	private int insertRealTimeData(Connection conn, int stringID, Timestamp recordedDate, int tagTypeID, String quality,
			Double processedValue, Double rawValue) {
		int rowsInserted = 0;
		try (PreparedStatement statement = conn.prepareStatement(INSERT_REAL_TIME_DATA_QUERY);) {
			statement.setInt(1, stringID);
			statement.setObject(2, recordedDate);
			statement.setInt(3, tagTypeID);
			statement.setObject(4, quality);
			statement.setObject(5, processedValue);
			statement.setObject(6, rawValue);
			// statement.setObject(7, VRE_WORKFLOW);

			rowsInserted = statement.executeUpdate();
			LOGGER.info(rowsInserted + " rows inserted in real time data table with String : " + stringID + " & Date : "
					+ recordedDate);
		} catch (Exception e) {
			LOGGER.severe(e.getMessage());
			e.printStackTrace();
		}
		return rowsInserted;
	}

}
