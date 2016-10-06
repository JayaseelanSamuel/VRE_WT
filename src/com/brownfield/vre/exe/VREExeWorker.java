package com.brownfield.vre.exe;

import static com.brownfield.vre.VREConstants.DEFAULT_REMARK;
import static com.brownfield.vre.VREConstants.DEFAULT_WATER_CUT;
import static com.brownfield.vre.VREConstants.FRICTION_FACTOR;
import static com.brownfield.vre.VREConstants.GOR;
import static com.brownfield.vre.VREConstants.HOLDUP;
import static com.brownfield.vre.VREConstants.INSERT_VRE_QUERY;
import static com.brownfield.vre.VREConstants.PI;
import static com.brownfield.vre.VREConstants.RESERVOIR_PRESSURE;
import static com.brownfield.vre.VREConstants.ROW_CHANGED_BY;
import static com.brownfield.vre.VREConstants.ROW_CHANGED_DATE;
import static com.brownfield.vre.VREConstants.VRE1;
import static com.brownfield.vre.VREConstants.VRE2;
import static com.brownfield.vre.VREConstants.VRE3;
import static com.brownfield.vre.VREConstants.VRE4;
import static com.brownfield.vre.VREConstants.VRE5;
import static com.brownfield.vre.VREConstants.VRE_TABLE_SELECT_QUERY;
import static com.brownfield.vre.VREConstants.VRE_WORKFLOW;
import static com.brownfield.vre.VREConstants.CHOKE_MULTIPLIER;
import static com.brownfield.vre.VREConstants.IS_SEABED;
import static com.brownfield.vre.VREConstants.WATER_CUT;
import static com.brownfield.vre.VREConstants.WATER_CUT_FLAG;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import com.brownfield.vre.VREConstants.SOURCE;
import com.brownfield.vre.VREConstants.VRE_TYPE;
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

	/**
	 * Instantiates a new VRE exe worker.
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
	 * @param recordedDate
	 *            the recorded date
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
			if (wellModel.getErrors() == null) {
				LOGGER.info(threadName + " : Time reported in VRE : " + wellModel.getTime());
				this.insertOrUpdateVRE(stringID, wcut, wellModel, recordedDate, vreConn, chokeMultiplier, isSeabed);
			} else {
				LOGGER.severe(threadName + " : Exception in calling VRE - " + wellModel.getErrors());
			}
		} else {
			LOGGER.severe(threadName + " : Something went wrong while calling VRE for string - " + stringID);
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
			statement.setDouble(4, vre1 != null ? vre1 : 0);
			statement.setDouble(5, vre2 != null ? vre2 : 0);
			statement.setDouble(6, vre3 != null ? vre3 : 0);
			statement.setDouble(7, vre4 != null ? vre4 : 0);
			statement.setDouble(8, vre5 != null ? vre5 : 0);
			statement.setDouble(9, vre6 != null ? vre6 : 0);

			statement.setDouble(10, wcut != null ? wcut : 0);
			statement.setString(11, wcutFlag);
			statement.setDouble(12, gor != null ? gor : 0);
			statement.setDouble(13, pi != null ? pi : 0);
			statement.setDouble(14, holdUPV != null ? holdUPV : 0);
			statement.setDouble(15, ffv != null ? ffv : 0);
			statement.setDouble(16, resPres != null ? resPres : 0);
			statement.setDouble(17, chokeMuliplier != null ? chokeMuliplier : 1);
			statement.setBoolean(18, isSeabed != null ? isSeabed : false);
			statement.setString(19, remark);

			rowsInserted = statement.executeUpdate();
			LOGGER.info(rowsInserted + " rows inserted in VRE table with String : " + stringID + " & Date : "
					+ recordedDate);
		} catch (Exception e) {
			LOGGER.severe(e.getMessage());
			e.printStackTrace();
		}
		return rowsInserted;
	}
}
