package com.brownfield.vre.exe;

import static com.brownfield.vre.VREConstants.CHOKE_MULTIPLIER;
import static com.brownfield.vre.VREConstants.DEFAULT_REMARK;
import static com.brownfield.vre.VREConstants.DEFAULT_WATER_CUT;
import static com.brownfield.vre.VREConstants.EMAIL_GROUP;
import static com.brownfield.vre.VREConstants.FRICTION_FACTOR;
import static com.brownfield.vre.VREConstants.GOR;
import static com.brownfield.vre.VREConstants.HIGH_TECH_RATE;
import static com.brownfield.vre.VREConstants.HOLDUP;
import static com.brownfield.vre.VREConstants.INSERT_ALERTS_QUERY;
import static com.brownfield.vre.VREConstants.INSERT_REAL_TIME_DATA_QUERY;
import static com.brownfield.vre.VREConstants.INSERT_TARGET_RATE_QUERY;
import static com.brownfield.vre.VREConstants.INSERT_VRE_QUERY;
import static com.brownfield.vre.VREConstants.INSERT_VRE_SHORT_QUERY;
import static com.brownfield.vre.VREConstants.IS_SEABED;
import static com.brownfield.vre.VREConstants.LOWER_LIMIT;
import static com.brownfield.vre.VREConstants.LOW_TECH_RATE;
import static com.brownfield.vre.VREConstants.MAX_FLOW_RATE_PRESSURE;
import static com.brownfield.vre.VREConstants.MODEL_ALERT_MESSAGE;
import static com.brownfield.vre.VREConstants.MODEL_PREDECTION_QUERY;
import static com.brownfield.vre.VREConstants.PERCENT_LIMIT;
import static com.brownfield.vre.VREConstants.PI;
import static com.brownfield.vre.VREConstants.REMARK;
import static com.brownfield.vre.VREConstants.RESERVOIR_PRESSURE;
import static com.brownfield.vre.VREConstants.ROW_CHANGED_BY;
import static com.brownfield.vre.VREConstants.ROW_CHANGED_DATE;
import static com.brownfield.vre.VREConstants.SEABED_REMARK;
import static com.brownfield.vre.VREConstants.SELECT_ALERT_LIMIT_QUERY;
import static com.brownfield.vre.VREConstants.SELECT_TECHNICAL_RATE_QUERY;
import static com.brownfield.vre.VREConstants.STRING_ALERT_MESSAGE;
import static com.brownfield.vre.VREConstants.STRING_TARGET_RATE_QUERY;
import static com.brownfield.vre.VREConstants.TAGRAWVALUE;
import static com.brownfield.vre.VREConstants.TAG_TYPE_ID;
import static com.brownfield.vre.VREConstants.UPDATE_WHP_AT_TECH_RATE;
import static com.brownfield.vre.VREConstants.UPPER_LIMIT;
import static com.brownfield.vre.VREConstants.VALUE;
import static com.brownfield.vre.VREConstants.VRE1;
import static com.brownfield.vre.VREConstants.VRE2;
import static com.brownfield.vre.VREConstants.VRE3;
import static com.brownfield.vre.VREConstants.VRE4;
import static com.brownfield.vre.VREConstants.VRE5;
import static com.brownfield.vre.VREConstants.VRE6;
import static com.brownfield.vre.VREConstants.VRE_TABLE_SELECT_QUERY;
import static com.brownfield.vre.VREConstants.VRE_WORKFLOW;
import static com.brownfield.vre.VREConstants.WATER_CUT;
import static com.brownfield.vre.VREConstants.WATER_CUT_FLAG;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.io.IOUtils;

import com.brownfield.vre.TagType;
import com.brownfield.vre.Utils;
import com.brownfield.vre.VREConstants.ALERT_TYPE;
import com.brownfield.vre.VREConstants.SOURCE;
import com.brownfield.vre.VREConstants.VRE_TYPE;
import com.brownfield.vre.exe.models.StringModel;
import com.brownfield.vre.exe.models.WellModel;

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

	/** The string tech rate ID. */
	private int stringTechRateID;

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

	/**
	 * Instantiates a new VRE exe worker.
	 *
	 * @param params
	 *            the params
	 * @param stringID
	 *            the string ID
	 * @param stringTechRateID
	 *            the string tech rate ID
	 * @param vreType
	 *            the vre type
	 */
	public VREExeWorker(Connection vreConn, List<String> params, int stringID, int stringTechRateID, VRE_TYPE vreType) {
		this.vreConn = vreConn;
		this.params = params;
		this.stringID = stringID;
		this.stringTechRateID = stringTechRateID;
		this.vreType = vreType;
	}

	/**
	 * Instantiates a new VRE exe worker.
	 *
	 * @param params
	 *            the params
	 * @param stringID
	 *            the string ID
	 * @param recordedDate
	 *            the recorded date
	 * @param vreType
	 *            the vre type
	 */
	public VREExeWorker(Connection vreConn, List<String> params, int stringID, Timestamp recordedDate, VRE_TYPE vreType) {
		this.vreConn = vreConn;
		this.params = params;
		this.stringID = stringID;
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
		} else if (this.vreType == VRE_TYPE.WHP_AT_TECH_RATE) {
			this.executeWHPAtTechRate(threadName);
		} else if (this.vreType == VRE_TYPE.MAX_FLOW_RATE) {
			this.executeMaxFlowRate(threadName);
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

	// TODO: Onkar : Remove unnecessary arguments like connection
	/**
	 * Execute model prediction.
	 *
	 * @param threadName
	 *            the thread name
	 */
	private void executeModelPrediction(String threadName) {
		LOGGER.fine(threadName + " : Started to model prediction for " + this.stringID);
		String output = runPrediction(params);
		if (output != null) {
			Double rateLiquid = this.getVRE6Value(output);
			if (rateLiquid != null) {
				// WellModel wellModel = new WellModel();
				// VREModel vreModel = new VREModel();
				// vreModel.setRateLiquid(rateLiquid);
				// wellModel.setVre6(vreModel);
				// this.insertOrUpdateModelPrediction(stringID, whp, wellModel,
				// recordedDate, vreConn, whpDate);
				this.insertRealTimeData(vreConn, stringID, recordedDate, TagType.VRE6_CALC.getTagTypeID(),
						whpDate.toString(), whp, rateLiquid);
			}
		} else {
			LOGGER.severe(threadName + " : Something went wrong while predicting model for string - " + stringID);
		}
		LOGGER.fine(threadName + " Finished model prediction for " + this.stringID);
	}

	/**
	 * Execute WHP at tech rate.
	 *
	 * @param threadName
	 *            the thread name
	 */
	private void executeWHPAtTechRate(String threadName) {
		LOGGER.info(threadName + " : Started to run WHPAtTechRate rate for " + this.stringID);
		long startT = System.currentTimeMillis();
		WellModel wellModel = runVRE(params);
		long endT = System.currentTimeMillis();
		double duration = ((endT - startT) / 1000);
		if (wellModel != null) {
			if (wellModel.getError() == null || wellModel.getError().isEmpty()) {
				LOGGER.info(threadName + " : Time reported in WHPAtTechRate : " + wellModel.getTime());
				Double whpAtTechRate = wellModel.getWhpTechRate().getWhp();
				this.updateWHPAtTechRate(vreConn, stringTechRateID, whpAtTechRate);
			} else {
				LOGGER.severe(threadName + " : Exception in calling WHPAtTechRate - " + wellModel.getErrors());
			}
		} else {
			LOGGER.severe(threadName + " : Something went wrong while calling WHPAtTechRate for string - " + stringID);
		}
		LOGGER.info(threadName + " Finished WHPAtTechRate for " + this.stringID + " in " + duration + " seconds");
	}

	/**
	 * Execute max flow rate.
	 *
	 * @param threadName
	 *            the thread name
	 */
	private void executeMaxFlowRate(String threadName) {
		LOGGER.info(threadName + " : Started to run MaxFlowRate rate for " + this.stringID);
		long startT = System.currentTimeMillis();
		WellModel wellModel = runVRE(params);
		long endT = System.currentTimeMillis();
		double duration = ((endT - startT) / 1000);
		if (wellModel != null) {
			if (wellModel.getError() == null || wellModel.getError().isEmpty()) {
				LOGGER.info(threadName + " : Time reported in MaxFlowRate : " + wellModel.getTime());
				Double maxFlowRatePressure = wellModel.getMaxFlowRate().getHeaderPressure();
				this.insertOrUpdateMaxFlowRate(vreConn, stringID, recordedDate, maxFlowRatePressure);
			} else {
				LOGGER.severe(threadName + " : Exception in calling MaxFlowRate - " + wellModel.getErrors());
			}
		} else {
			LOGGER.severe(threadName + " : Something went wrong while calling MaxFlowRate for string - " + stringID);
		}
		LOGGER.info(threadName + " Finished MaxFlowRate for " + this.stringID + " in " + duration + " seconds");
	}

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
		long startT = System.currentTimeMillis();

		try {
			ProcessBuilder pb = new ProcessBuilder(params);
			long endT = System.currentTimeMillis();
			double duration = ((endT - startT));
			LOGGER.fine(" : Time to build process : " + duration + " milliseconds");
			startT = System.currentTimeMillis();

			Process p = pb.start();

			endT = System.currentTimeMillis();
			duration = ((endT - startT));
			LOGGER.fine(" : Time to start process : " + duration + " milliseconds");
			startT = System.currentTimeMillis();
			
            
			InputStream input = p.getInputStream();
			
			endT = System.currentTimeMillis();
			duration = ((endT - startT));
			LOGGER.fine(" : Time to get input stream : " + duration + " milliseconds");
			startT = System.currentTimeMillis();
			// InputStream error = p.getErrorStream();
			// OutputStream output = p.getOutputStream();

			JAXBContext jaxbContext = JAXBContext.newInstance(WellModel.class);

			endT = System.currentTimeMillis();
			duration = ((endT - startT));
			LOGGER.fine(" : Time to create new instance : " + duration + " milliseconds");
			startT = System.currentTimeMillis();

			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

			endT = System.currentTimeMillis();
			duration = ((endT - startT));
			LOGGER.fine(" : Time to create unmarshaller : " + duration + " milliseconds");
			startT = System.currentTimeMillis();

			wellModel = (WellModel) jaxbUnmarshaller.unmarshal(input);

			endT = System.currentTimeMillis();
			duration = ((endT - startT));
			LOGGER.fine(" : Time to unmarshal response : " + duration + " milliseconds");
			startT = System.currentTimeMillis();
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
	 * Run prediction.
	 *
	 * @param params
	 *            the params
	 * @return the string
	 */
	public static String runPrediction(List<String> params) {
		LOGGER.fine(Thread.currentThread().getName() + " : Running Prediction with " + "\n" + params);
		String output = null;
		try {
			ProcessBuilder pb = new ProcessBuilder(params);

			Process p = pb.start();

			InputStream input = p.getInputStream();

			InputStreamReader reader = new InputStreamReader(input);

			output = IOUtils.toString(reader);

			// InputStream error = p.getErrorStream();
			// OutputStream output = p.getOutputStream();

		} catch (IOException e) {
			LOGGER.severe("Exception occurred while exeuting system command");
			LOGGER.severe(e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			LOGGER.severe(e.getMessage());
			e.printStackTrace();
		}
		return output;
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

				String remark = isSeabed ? SEABED_REMARK : DEFAULT_REMARK;

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

					rset.updateString(REMARK, remark);
					rset.updateString(ROW_CHANGED_BY, VRE_WORKFLOW);
					rset.updateTimestamp(ROW_CHANGED_DATE, new Timestamp(new Date().getTime()));
					rset.updateRow();
					LOGGER.info("updated row in VRE table with String : " + stringID + " & Date : " + recordedDate);
				} else { // insert
					this.insertVRERecord(vreConn, stringID, recordedDate, vre1LiqRate, vre2LiqRate, vre3LiqRate,
							vre4LiqRate, vre5LiqRate, null, wcut, DEFAULT_WATER_CUT, gor, pi, holdUPV, ffv, resPres,
							chokeMuliplier, isSeabed, remark);
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
	 * @param conn
	 *            the conn
	 * @param stringID
	 *            the string id
	 * @param recordedDate
	 *            the recorded date
	 * @param remark
	 *            the remark
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
					this.insertVRERemark(conn, stringID, recordedDate, remark);
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
	 * @param conn
	 *            the conn
	 * @param stringID
	 *            the string id
	 * @param recordedDate
	 *            the recorded date
	 * @param remark
	 *            the remark
	 * @return the int
	 */
	private int insertVRERemark(Connection conn, int stringID, Timestamp recordedDate, String remark) {
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
					rowsInserted += generateTechnicalRateAlert(vreConn, stringModel, recordedDate, vre, lowTechRate,
							highTechRate);
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
					double percentLimit = rset.getDouble(PERCENT_LIMIT);
					double preValue = rset.getDouble(VALUE);

					if (tagTypeID == TagType.GOR.getTagTypeID()) {
						rowsInserted += generateModelAlert(vreConn, stringModel, TagType.GOR, recordedDate, gor,
								preValue, percentLimit);
						continue;
					}
					if (tagTypeID == TagType.PI.getTagTypeID()) {
						rowsInserted += generateModelAlert(vreConn, stringModel, TagType.PI, recordedDate, pi, preValue,
								percentLimit);
						continue;
					}
					if (tagTypeID == TagType.HOLDUP.getTagTypeID()) {
						rowsInserted += generateModelAlert(vreConn, stringModel, TagType.HOLDUP, recordedDate, holdUPV,
								preValue, percentLimit);
						continue;
					}
					if (tagTypeID == TagType.FRICTION_FACTOR.getTagTypeID()) {
						rowsInserted += generateModelAlert(vreConn, stringModel, TagType.FRICTION_FACTOR, recordedDate,
								ffv, preValue, percentLimit);
						continue;
					}
					if (tagTypeID == TagType.RESERVOIR_PRESSURE.getTagTypeID()) {
						rowsInserted += generateModelAlert(vreConn, stringModel, TagType.RESERVOIR_PRESSURE,
								recordedDate, resPres, preValue, percentLimit);
						continue;
					}
				}
			}
			LOGGER.info(rowsInserted + " alerts were triggered for String : " + stringModel.getStringID() + " & Date : "
					+ recordedDate);
		} catch (Exception e) {
			LOGGER.severe(e.getMessage());
			e.printStackTrace();
		}
		return rowsInserted;

	}

	/**
	 * Prepare insert record.
	 *
	 * @param vreConn
	 *            the vre conn
	 * @param stringName
	 *            the string name
	 * @param tagType
	 *            the tag type
	 * @param alertType
	 *            the alert type
	 * @param msgType
	 *            the msg type
	 * @param recordedDate
	 *            the recorded date
	 * @param value
	 *            the value
	 * @param limit
	 *            the limit
	 * @return the string
	 */
	private String insertAlert(Connection vreConn, String stringName, TagType tagType, ALERT_TYPE alertType,
			String message, Timestamp recordedDate) {
		try (PreparedStatement ps = vreConn.prepareStatement(INSERT_ALERTS_QUERY);) {
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

	private int generateTechnicalRateAlert(Connection vreConn, StringModel stringModel, Timestamp recordedDate,
			Double value, double lowLimit, double highLimit) {
		/*
		 * String wellMonitorDailyAlert = String.format(WELLS_DASHBOARD_LINK,
		 * MONITOR_DAILY_ALERT_DASHBOARD, stringModel.getStringID(),
		 * Utils.convertToString(recordedDate, DATE_FORMAT_DSPM),
		 * Utils.convertToString(recordedDate, DATE_FORMAT_DSPM));
		 */
		if (value < lowLimit) {
			String message = String.format(STRING_ALERT_MESSAGE, TagType.TECHNICAL_RATE, value,
					stringModel.getStringName(), LOWER_LIMIT, lowLimit, recordedDate);
			this.insertAlert(vreConn, stringModel.getStringName(), TagType.TECHNICAL_RATE, ALERT_TYPE.LOWER, message,
					recordedDate);
			/*
			 * AlertHandler.notifyByEmail(EMAIL_GROUP, DSBPM_ALERT_TEMPLATE,
			 * APP_BASE_URL + wellMonitorDailyAlert,
			 * String.format(DSBPM_ALERT_SUBJECT, tagType,
			 * stringModel.getStringName()), message);
			 */
			LOGGER.info("Generating " + ALERT_TYPE.LOWER + " alert for : " + TagType.TECHNICAL_RATE + " and string : "
					+ stringModel.getStringName() + " with value " + value + " and limit : " + lowLimit + " & Date : "
					+ recordedDate + " and emailing it to : " + EMAIL_GROUP);
			return 1;
		}
		if (value > highLimit) {
			String message = String.format(STRING_ALERT_MESSAGE, TagType.TECHNICAL_RATE, value,
					stringModel.getStringName(), UPPER_LIMIT, highLimit, recordedDate);
			this.insertAlert(vreConn, stringModel.getStringName(), TagType.TECHNICAL_RATE, ALERT_TYPE.UPPER, message,
					recordedDate);
			/*
			 * AlertHandler.notifyByEmail(EMAIL_GROUP, DSBPM_ALERT_TEMPLATE,
			 * APP_BASE_URL + wellMonitorDailyAlert,
			 * String.format(DSBPM_ALERT_SUBJECT, tagType,
			 * stringModel.getStringName()), message);
			 */
			LOGGER.info("Generating " + ALERT_TYPE.UPPER + " alert for : " + TagType.TECHNICAL_RATE + " and string : "
					+ stringModel.getStringName() + " with value " + value + " and limit : " + highLimit + " & Date : "
					+ recordedDate + " and emailing it to : " + EMAIL_GROUP);
			return 1;
		}
		return 0;
	}

	/**
	 * Generate model alert.
	 *
	 * @param vreConn
	 *            the vre conn
	 * @param stringModel
	 *            the string model
	 * @param tagType
	 *            the tag type
	 * @param recordedDate
	 *            the recorded date
	 * @param value
	 *            the value
	 * @param preValue
	 *            the pre value
	 * @param percentLimit
	 *            the percent limit
	 * @return the int
	 */
	private int generateModelAlert(Connection vreConn, StringModel stringModel, TagType tagType, Timestamp recordedDate,
			Double value, double preValue, double percentLimit) {

		boolean withinTrend = Utils.isWithinLimit(value, preValue, percentLimit);

		if (!withinTrend) {
			ALERT_TYPE alertType = ALERT_TYPE.UPPER;
			if (value < preValue) {
				alertType = ALERT_TYPE.LOWER;
			}

			String message = String.format(MODEL_ALERT_MESSAGE, tagType, value, stringModel.getStringName(),
					percentLimit, preValue, recordedDate);
			this.insertAlert(vreConn, stringModel.getStringName(), tagType, alertType, message, recordedDate);

			LOGGER.info("Generating " + alertType + " alert for : " + tagType + " and string : "
					+ stringModel.getStringName() + " with value " + value + " and percent limit : " + percentLimit
					+ " & Date : " + recordedDate + " and emailing it to : " + EMAIL_GROUP);
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
	@SuppressWarnings("unused")
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
					LOGGER.fine("updated row in real time data table with String : " + stringID + " & Date : "
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
			statement.setDouble(6, rawValue);
			// statement.setObject(7, VRE_WORKFLOW);

			rowsInserted = statement.executeUpdate();
			LOGGER.fine(rowsInserted + " rows inserted in real time data table with String : " + stringID + " & Date : "
					+ recordedDate);
		} catch (Exception e) {
			LOGGER.severe(e.getMessage());
			e.printStackTrace();
		}
		return rowsInserted;
	}

	/**
	 * Gets the VR e6 value.
	 *
	 * @param output
	 *            the output
	 * @return the VR e6 value
	 */
	public Double getVRE6Value(String output) {
		Double d = null;
		String value = "";
		try {
			String regex = "<qliq>(.*)</qliq>";
			Pattern p = Pattern.compile(regex);
			Matcher m = p.matcher(output);
			if (m.find()) {
				value = m.group(1);
				d = Double.parseDouble(value);
			} else {
				LOGGER.fine(
						"<qliq> not present in " + output + " for string " + stringID + " when executing " + params);
			}
		} catch (Exception e) {
			LOGGER.severe(e.getMessage());
			e.printStackTrace();
		}
		return d;
	}

	/**
	 * Update WHP at tech rate.
	 *
	 * @param vreConn2
	 *            the vre conn 2
	 * @param stringTechRateID
	 *            the string tech rate ID
	 * @param whpAtTechRate
	 *            the whp at tech rate
	 */
	private void updateWHPAtTechRate(Connection vreConn, int stringTechRateID, Double whpAtTechRate) {
		try (PreparedStatement stmt = vreConn.prepareStatement(UPDATE_WHP_AT_TECH_RATE);) {
			stmt.setDouble(1, whpAtTechRate);
			stmt.setInt(2, stringTechRateID);
			stmt.executeUpdate();
			LOGGER.info("updated row in technical rate table with stringTechRateID : " + stringTechRateID
					+ " with WHP : " + whpAtTechRate);
		} catch (Exception e) {
			LOGGER.severe(e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * Insert or update max flow rate.
	 *
	 * @param vreConn
	 *            the vre conn
	 * @param stringID
	 *            the string ID
	 * @param recordedDate
	 *            the recorded date
	 * @param maxFlowRate
	 *            the max flow rate
	 */
	private void insertOrUpdateMaxFlowRate(Connection vreConn, int stringID, Timestamp recordedDate,
			double maxFlowRate) {
		try (PreparedStatement statement = vreConn.prepareStatement(STRING_TARGET_RATE_QUERY,
				ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE)) {
			statement.setInt(1, stringID);
			statement.setTimestamp(2, recordedDate);
			try (ResultSet rset = statement.executeQuery()) {
				if (rset.next()) {
					rset.updateDouble(MAX_FLOW_RATE_PRESSURE, maxFlowRate);
					rset.updateTimestamp(ROW_CHANGED_DATE, new Timestamp(new Date().getTime()));
					rset.updateRow();
				} else {
					try (PreparedStatement stmt = vreConn.prepareStatement(INSERT_TARGET_RATE_QUERY);) {
						stmt.setInt(1, stringID);
						stmt.setTimestamp(2, recordedDate);
						stmt.setObject(3, null);
						stmt.setDouble(4, maxFlowRate);
						//Changed by Jay
						stmt.setString(5, VRE_WORKFLOW);
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
}
