package com.brownfield.vre;

import static com.brownfield.vre.VREConstants.AVG_ANN_PRESSURE_A;
import static com.brownfield.vre.VREConstants.AVG_ANN_PRESSURE_B;
import static com.brownfield.vre.VREConstants.AVG_CALC_WORKFLOW;
import static com.brownfield.vre.VREConstants.AVG_CHOKE_SIZE;
import static com.brownfield.vre.VREConstants.AVG_DOWNHOLE_PRESSURE;
import static com.brownfield.vre.VREConstants.AVG_GASLIFT_INJ_RATE;
import static com.brownfield.vre.VREConstants.AVG_GAS_RATE;
import static com.brownfield.vre.VREConstants.AVG_HEADER_PRESSURE;
import static com.brownfield.vre.VREConstants.AVG_LIQUID_RATE;
import static com.brownfield.vre.VREConstants.AVG_MEASUREMENT_QUERY;
import static com.brownfield.vre.VREConstants.AVG_MEASUREMENT_SELECT_QUERY;
import static com.brownfield.vre.VREConstants.AVG_OIL_VOL_RATE;
import static com.brownfield.vre.VREConstants.AVG_WATERCUT;
import static com.brownfield.vre.VREConstants.AVG_WATER_VOL_RATE;
import static com.brownfield.vre.VREConstants.AVG_WHERE_CLAUSE;
import static com.brownfield.vre.VREConstants.AVG_WHP;
import static com.brownfield.vre.VREConstants.AVG_WHT;
import static com.brownfield.vre.VREConstants.DATE_FORMAT;
import static com.brownfield.vre.VREConstants.DATE_TIME_FORMAT;
import static com.brownfield.vre.VREConstants.END_DATE;
import static com.brownfield.vre.VREConstants.INSERT_AVG_MEASUREMENT_QUERY;
import static com.brownfield.vre.VREConstants.ROW_CHANGED_BY;
import static com.brownfield.vre.VREConstants.ROW_CHANGED_DATE;
import static com.brownfield.vre.VREConstants.RT_DISTINCT_STRING_FOR_DAY_QUERY;
import static com.brownfield.vre.VREConstants.SQL_DRIVER_NAME;
import static com.brownfield.vre.VREConstants.START_DATE;
import static com.brownfield.vre.VREConstants.STRING_ID;
import static com.brownfield.vre.VREConstants.VRE_DB_URL;
import static com.brownfield.vre.VREConstants.VRE_PASSWORD;
import static com.brownfield.vre.VREConstants.VRE_USER;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Logger;

/**
 * The Class AvgCalculator.
 *
 * @author Onkar Dhuri <onkar.dhuri@synerzip.com>
 */
public class AvgCalculator {

	/** The Constant LOGGER. */
	private static final Logger LOGGER = Logger.getLogger(AvgCalculator.class.getName());

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

				AvgCalculator ac = new AvgCalculator();
				// String yesterdayString = Utils.getYesterdayString();
				// System.out.println(yesterdayString);
				// Timestamp yesterdayTimestamp = Utils.getYesterdayTimestamp();
				// System.out.println(yesterdayTimestamp);
				String recDate = "2016-03-02";
				Timestamp recordedDate = Utils.getDateFromString(recDate, DATE_FORMAT, Boolean.FALSE);
				// System.out.println(recordedDate);
				ac.calculateAverage(vreConn, recordedDate);
			} catch (SQLException e) {
				LOGGER.severe(e.getMessage());
			}

		} catch (ClassNotFoundException e) {
			LOGGER.severe(e.getMessage());
		}
	}

	/**
	 * Calculate average.
	 *
	 * @param vreConn
	 *            the vre conn
	 * @param recordedDate
	 *            the recorded date
	 * @return true, if successful
	 */
	private void calculateAverage(Connection vreConn, Timestamp recordedDate) {
		try (PreparedStatement statement = vreConn.prepareStatement(RT_DISTINCT_STRING_FOR_DAY_QUERY);) {
			statement.setTimestamp(1, recordedDate);
			try (ResultSet rset = statement.executeQuery();) {
				if (rset != null) {
					int stringID;
					Timestamp startDate, endDate;
					String whereClause = null;
					SimpleDateFormat sdf = new SimpleDateFormat(DATE_TIME_FORMAT);
					String avgQuery, remark;
					while (rset.next()) {
						stringID = rset.getInt(STRING_ID);
						startDate = rset.getTimestamp(START_DATE);
						endDate = rset.getTimestamp(END_DATE);
						whereClause = "";
						remark = "";
						if (startDate != null & endDate != null) {
							whereClause = String.format(AVG_WHERE_CLAUSE, sdf.format(startDate), sdf.format(endDate));
							remark = "Excluding downtime hours from " + startDate + " to " + endDate;
						}
						LOGGER.info(stringID + " : " + startDate + " : " + endDate);
						avgQuery = String.format(AVG_MEASUREMENT_QUERY, whereClause);
						try (PreparedStatement stmt = vreConn.prepareStatement(avgQuery);) {
							stmt.setTimestamp(1, recordedDate);
							stmt.setInt(2, stringID);
							try (ResultSet avgRset = stmt.executeQuery();) {
								if (avgRset != null && avgRset.next()) {
									// one row per string
									Double whp = avgRset.getDouble(AVG_WHP);
									Double wht = avgRset.getDouble(AVG_WHT);
									Double choke = avgRset.getDouble(AVG_CHOKE_SIZE);
									Double pdgp = avgRset.getDouble(AVG_DOWNHOLE_PRESSURE);
									Double gasInjRate = avgRset.getDouble(AVG_GASLIFT_INJ_RATE);
									Double waterVolRate = avgRset.getDouble(AVG_WATER_VOL_RATE);
									Double oilVolRate = avgRset.getDouble(AVG_OIL_VOL_RATE);
									Double annPreA = avgRset.getDouble(AVG_ANN_PRESSURE_A);
									Double annPreB = avgRset.getDouble(AVG_ANN_PRESSURE_B);
									Double liqRate = avgRset.getDouble(AVG_LIQUID_RATE);
									Double gasRate = avgRset.getDouble(AVG_GAS_RATE);
									Double wcut = avgRset.getDouble(AVG_WATERCUT);
									Double hp = avgRset.getDouble(AVG_HEADER_PRESSURE);

									this.insertOrUpdateAvgRecord(vreConn, stringID, recordedDate, whp, wht, choke, pdgp,
											gasInjRate, waterVolRate, oilVolRate, annPreA, annPreB, liqRate, gasRate,
											wcut, hp, remark);
								} else {
									// no record as downtime overshadows
									LOGGER.severe("Downtime overshadows the average period for String : " + stringID
											+ " with downtime " + startDate + " to " + endDate);
								}
							} catch (Exception e) {
								LOGGER.severe(e.getMessage());
							}
						} catch (Exception e) {
							LOGGER.severe(e.getMessage());
						}
					}
				}
			} catch (Exception e) {
				LOGGER.severe(e.getMessage());
			}
		} catch (Exception e) {
			LOGGER.severe(e.getMessage());
		}
	}

	/**
	 * Insert or update avg record.
	 *
	 * @param conn
	 *            the conn
	 * @param stringID
	 *            the string id
	 * @param recordedDate
	 *            the recorded date
	 * @param whp
	 *            the whp
	 * @param wht
	 *            the wht
	 * @param choke
	 *            the choke
	 * @param pdgp
	 *            the pdgp
	 * @param gasInjRate
	 *            the gas inj rate
	 * @param waterVolRate
	 *            the water vol rate
	 * @param oilVolRate
	 *            the oil vol rate
	 * @param annPreA
	 *            the ann pre a
	 * @param annPreB
	 *            the ann pre b
	 * @param liqRate
	 *            the liq rate
	 * @param gasRate
	 *            the gas rate
	 * @param wcut
	 *            the wcut
	 * @param hp
	 *            the hp
	 * @param remark
	 *            the remark
	 */
	private void insertOrUpdateAvgRecord(Connection conn, int stringID, Timestamp recordedDate, Double whp, Double wht,
			Double choke, Double pdgp, Double gasInjRate, Double waterVolRate, Double oilVolRate, Double annPreA,
			Double annPreB, Double liqRate, Double gasRate, Double wcut, Double hp, String remark) {

		try (PreparedStatement statement = conn.prepareStatement(AVG_MEASUREMENT_SELECT_QUERY,
				ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE)) {

			statement.setInt(1, stringID);
			statement.setTimestamp(2, recordedDate);
			try (ResultSet rset = statement.executeQuery()) {
				if (rset.next()) { // record present, just update
					rset.updateObject(AVG_WHP, whp);
					rset.updateObject(AVG_WHT, wht);
					rset.updateObject(AVG_CHOKE_SIZE, choke);
					rset.updateObject(AVG_DOWNHOLE_PRESSURE, pdgp);
					rset.updateObject(AVG_GASLIFT_INJ_RATE, gasInjRate);
					rset.updateObject(AVG_WATER_VOL_RATE, waterVolRate);
					rset.updateObject(AVG_OIL_VOL_RATE, oilVolRate);
					rset.updateObject(AVG_ANN_PRESSURE_A, annPreA);
					rset.updateObject(AVG_ANN_PRESSURE_B, annPreB);
					rset.updateObject(AVG_LIQUID_RATE, liqRate);
					rset.updateObject(AVG_GAS_RATE, gasRate);
					rset.updateObject(AVG_WATERCUT, wcut);
					rset.updateObject(AVG_HEADER_PRESSURE, hp);
					rset.updateString(ROW_CHANGED_BY, AVG_CALC_WORKFLOW);
					rset.updateTimestamp(ROW_CHANGED_DATE, new Timestamp(new Date().getTime()));
					rset.updateRow();
					LOGGER.info(
							"updated row in Measurement table with String : " + stringID + " & Date : " + recordedDate);
				} else { // insert
					this.insertAverageMeasurementRecord(conn, stringID, recordedDate, whp, wht, choke, pdgp, gasInjRate,
							waterVolRate, oilVolRate, annPreA, annPreB, liqRate, gasRate, wcut, hp, remark);

				}
			} catch (Exception e) {
				LOGGER.severe(e.getMessage());
			}
		} catch (Exception e) {
			LOGGER.severe(e.getMessage());
		}
	}

	/**
	 * Insert average measurement record.
	 *
	 * @param conn
	 *            the conn
	 * @param stringID
	 *            the string id
	 * @param recordedDate
	 *            the recorded date
	 * @param whp
	 *            the whp
	 * @param wht
	 *            the wht
	 * @param choke
	 *            the choke
	 * @param pdgp
	 *            the pdgp
	 * @param gasInjRate
	 *            the gas inj rate
	 * @param waterVolRate
	 *            the water vol rate
	 * @param oilVolRate
	 *            the oil vol rate
	 * @param annPreA
	 *            the ann pre a
	 * @param annPreB
	 *            the ann pre b
	 * @param liqRate
	 *            the liq rate
	 * @param gasRate
	 *            the gas rate
	 * @param wcut
	 *            the wcut
	 * @param hp
	 *            the hp
	 * @param remark
	 *            the remark
	 * @return the int
	 */
	private int insertAverageMeasurementRecord(Connection conn, int stringID, Timestamp recordedDate, Double whp,
			Double wht, Double choke, Double pdgp, Double gasInjRate, Double waterVolRate, Double oilVolRate,
			Double annPreA, Double annPreB, Double liqRate, Double gasRate, Double wcut, Double hp, String remark) {
		int rowsInserted = 0;
		try (PreparedStatement statement = conn.prepareStatement(INSERT_AVG_MEASUREMENT_QUERY);) {
			statement.setInt(1, stringID);
			statement.setTimestamp(2, recordedDate);
			statement.setObject(3, whp);
			statement.setObject(4, wht);
			statement.setObject(5, choke);
			statement.setObject(6, pdgp);
			statement.setObject(7, gasInjRate);
			statement.setObject(8, waterVolRate);
			statement.setObject(9, oilVolRate);
			statement.setObject(10, annPreA);
			statement.setObject(11, annPreB);
			statement.setObject(12, liqRate);
			statement.setObject(13, gasRate);
			statement.setObject(14, wcut);
			statement.setObject(15, hp);
			statement.setString(16, remark);
			statement.setString(17, AVG_CALC_WORKFLOW);

			rowsInserted = statement.executeUpdate();
			LOGGER.info(rowsInserted + " rows inserted in AvgMeasurement table with String : " + stringID + " & Date : "
					+ recordedDate);
		} catch (Exception e) {
			LOGGER.severe(e.getMessage());
		}
		return rowsInserted;
	}

}
