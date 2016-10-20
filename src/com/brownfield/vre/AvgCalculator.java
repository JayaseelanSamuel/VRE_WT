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
import static com.brownfield.vre.VREConstants.AVG_MEDIAN_QUERY;
import static com.brownfield.vre.VREConstants.AVG_VRE6_CALC;
import static com.brownfield.vre.VREConstants.AVG_WATERCUT;
import static com.brownfield.vre.VREConstants.AVG_WATERCUT_HONEYWELL;
import static com.brownfield.vre.VREConstants.AVG_WATER_INJ_RATE;
import static com.brownfield.vre.VREConstants.AVG_WHERE_CLAUSE;
import static com.brownfield.vre.VREConstants.AVG_WHP;
import static com.brownfield.vre.VREConstants.AVG_WHT;
import static com.brownfield.vre.VREConstants.DATE_FORMAT;
import static com.brownfield.vre.VREConstants.DATE_TIME_FORMAT;
import static com.brownfield.vre.VREConstants.END_DATE;
import static com.brownfield.vre.VREConstants.INSERT_AVG_MEASUREMENT_QUERY;
import static com.brownfield.vre.VREConstants.INSERT_VRE6_QUERY;
import static com.brownfield.vre.VREConstants.ROW_CHANGED_BY;
import static com.brownfield.vre.VREConstants.ROW_CHANGED_DATE;
import static com.brownfield.vre.VREConstants.RT_DISTINCT_STRING_FOR_DAY_QUERY;
import static com.brownfield.vre.VREConstants.SQL_DRIVER_NAME;
import static com.brownfield.vre.VREConstants.START_DATE;
import static com.brownfield.vre.VREConstants.STRING_ID;
import static com.brownfield.vre.VREConstants.VRE6;
import static com.brownfield.vre.VREConstants.VRE_DB_URL;
import static com.brownfield.vre.VREConstants.VRE_PASSWORD;
import static com.brownfield.vre.VREConstants.VRE_TABLE_SELECT_QUERY_VRE6;
import static com.brownfield.vre.VREConstants.VRE_USER;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
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
				@SuppressWarnings("unused")
				Timestamp recordedDate = Utils.getDateFromString(recDate, DATE_FORMAT, Boolean.FALSE);
				// System.out.println(recordedDate);
				// ac.calculateAverage(vreConn, recordedDate);
				//1, 4, Utils.parseDate("2016-10-13")
				//906, 1, Utils.parseDate("2016-09-06")
				Double median = ac.getMedian(vreConn, 1, 4, Utils.parseDate("2016-09-06"), null, null);
				System.out.println(median);
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
	 * Calculate average.
	 *
	 * @param vreConn
	 *            the vre conn
	 * @param recordedDate
	 *            the recorded date
	 * @return true, if successful
	 */
	public void calculateAverage(Connection vreConn, Timestamp recordedDate) {
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
								Double whp = null;
								Double wht = null;
								Double choke = null;
								Double pdgp = null;
								Double gasInjRate = null;
								Double waterInjRate = null;
								Double wcutHoneyWell = null;
								Double annPreA = null;
								Double annPreB = null;
								Double liqRate = null;
								Double gasRate = null;
								Double wcut = null;
								Double hp = null;
								Double vre6 = null;
								if (avgRset != null && avgRset.next()) {
									// one row per string
									// getObject instead of getDouble to store
									// null values instead of default 0.0
									whp = avgRset.getObject(AVG_WHP) == null ? null : avgRset.getDouble(AVG_WHP);
									wht = avgRset.getObject(AVG_WHT) == null ? null : avgRset.getDouble(AVG_WHT);
									choke = avgRset.getObject(AVG_CHOKE_SIZE) == null ? null
											: avgRset.getDouble(AVG_CHOKE_SIZE);
									pdgp = avgRset.getObject(AVG_DOWNHOLE_PRESSURE) == null ? null
											: avgRset.getDouble(AVG_DOWNHOLE_PRESSURE);
									gasInjRate = avgRset.getObject(AVG_GASLIFT_INJ_RATE) == null ? null
											: avgRset.getDouble(AVG_GASLIFT_INJ_RATE);
									waterInjRate = avgRset.getObject(AVG_WATER_INJ_RATE) == null ? null
											: avgRset.getDouble(AVG_WATER_INJ_RATE);
									wcutHoneyWell = avgRset.getObject(AVG_WATERCUT_HONEYWELL) == null ? null
											: avgRset.getDouble(AVG_WATERCUT_HONEYWELL);
									annPreA = avgRset.getObject(AVG_ANN_PRESSURE_A) == null ? null
											: avgRset.getDouble(AVG_ANN_PRESSURE_A);
									annPreB = avgRset.getObject(AVG_ANN_PRESSURE_B) == null ? null
											: avgRset.getDouble(AVG_ANN_PRESSURE_B);
									liqRate = avgRset.getObject(AVG_LIQUID_RATE) == null ? null
											: avgRset.getDouble(AVG_LIQUID_RATE);
									gasRate = avgRset.getObject(AVG_GAS_RATE) == null ? null
											: avgRset.getDouble(AVG_GAS_RATE);
									wcut = avgRset.getObject(AVG_WATERCUT) == null ? null
											: avgRset.getDouble(AVG_WATERCUT);
									hp = avgRset.getObject(AVG_HEADER_PRESSURE) == null ? null
											: avgRset.getDouble(AVG_HEADER_PRESSURE);
									vre6 = avgRset.getObject(AVG_VRE6_CALC) == null ? null
											: avgRset.getDouble(AVG_VRE6_CALC);

									// calculate medians instead of avg for
									// pressures
									whp = this.getMedian(vreConn, stringID, TagType.WHP.getTagTypeID(), recordedDate,
											startDate, endDate);
									pdgp = this.getMedian(vreConn, stringID, TagType.DOWNHOLE_PRESSURE.getTagTypeID(),
											recordedDate, startDate, endDate);
									hp = this.getMedian(vreConn, stringID, TagType.HEADER_PRESSURE.getTagTypeID(),
											recordedDate, startDate, endDate);
									vre6 = this.getMedian(vreConn, stringID, TagType.VRE6_CALC.getTagTypeID(),
											recordedDate, startDate, endDate);

								} else {
									// no record as downtime overshadows
									remark = "Downtime overshadows for the day " + startDate + " to " + endDate;
									LOGGER.severe("Downtime overshadows the average period for String : " + stringID
											+ " with downtime " + startDate + " to " + endDate);
								}
								this.insertOrUpdateAvgRecord(vreConn, stringID, recordedDate, whp, wht, choke, pdgp,
										gasInjRate, waterInjRate, wcutHoneyWell, annPreA, annPreB, liqRate, gasRate,
										wcut, hp, remark);
								// insert vre 6
								if (vre6 != null) {
									this.insertOrUpdateVRE6(vreConn, stringID, recordedDate, vre6);
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
				e.printStackTrace();
			}
		} catch (Exception e) {
			LOGGER.severe(e.getMessage());
			e.printStackTrace();
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
	 * @param waterInjRate
	 *            the water vol rate
	 * @param wcutHoneyWell
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
			Double choke, Double pdgp, Double gasInjRate, Double waterInjRate, Double wcutHoneyWell, Double annPreA,
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
					rset.updateObject(AVG_WATER_INJ_RATE, waterInjRate);
					rset.updateObject(AVG_WATERCUT_HONEYWELL, wcutHoneyWell);
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
							waterInjRate, wcutHoneyWell, annPreA, annPreB, liqRate, gasRate, wcut, hp, remark);

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
			e.printStackTrace();
		}
		return rowsInserted;
	}

	/**
	 * Gets the median.
	 *
	 * @param conn
	 *            the conn
	 * @param stringID
	 *            the string id
	 * @param tagTypeID
	 *            the tag type id
	 * @param recordedDate
	 *            the recorded date
	 * @param startDownTime
	 *            the start down time
	 * @param endDownTime
	 *            the end down time
	 * @return the median
	 */
	private Double getMedian(Connection conn, int stringID, int tagTypeID, Timestamp recordedDate,
			Timestamp startDownTime, Timestamp endDownTime) {
		Double median = null;
		Timestamp currTime = new Timestamp(new Date().getTime());
		/*
		 * set downtime values to current time if they are null as both of them
		 * are mandatory in sql-procedure
		 */
		if (startDownTime == null) {
			startDownTime = currTime;
		}
		if (endDownTime == null) {
			endDownTime = currTime;
		}
		try (CallableStatement statement = conn.prepareCall(AVG_MEDIAN_QUERY);) {
			statement.setInt(1, stringID);
			statement.setInt(2, tagTypeID);
			statement.setTimestamp(3, recordedDate);
			statement.setTimestamp(4, startDownTime);
			statement.setTimestamp(5, endDownTime);
			statement.registerOutParameter(6, Types.DOUBLE);

			statement.executeUpdate();

			median = (Double) statement.getObject(6);
		} catch (Exception e) {
			LOGGER.severe(e.getMessage());
			e.printStackTrace();
		}
		return median;
	}

	/**
	 * Insert or update vr e6.
	 *
	 * @param vreConn
	 *            the vre conn
	 * @param stringID
	 *            the string id
	 * @param recordedDate
	 *            the recorded date
	 * @param vre6
	 *            the vre6
	 */
	private void insertOrUpdateVRE6(Connection vreConn, int stringID, Timestamp recordedDate, Double vre6) {
		try (PreparedStatement statement = vreConn.prepareStatement(VRE_TABLE_SELECT_QUERY_VRE6,
				ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE)) {

			statement.setTimestamp(1, recordedDate);
			statement.setInt(2, stringID);
			try (ResultSet rset = statement.executeQuery()) {

				if (rset.next()) { // record present, just update
					rset.updateDouble(VRE6, vre6);
					rset.updateString(ROW_CHANGED_BY, AVG_CALC_WORKFLOW);
					rset.updateTimestamp(ROW_CHANGED_DATE, new Timestamp(new Date().getTime()));
					rset.updateRow();
					LOGGER.info("updated row with VRE6 value in VRE table for String : " + stringID + " & Date : "
							+ recordedDate);
				} else { // insert
					this.insertVRE6Record(vreConn, stringID, recordedDate, vre6);
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
	 * Insert vr e6 record.
	 *
	 * @param conn
	 *            the conn
	 * @param stringID
	 *            the string id
	 * @param recordedDate
	 *            the recorded date
	 * @param vre6
	 *            the vre6
	 * @return the int
	 */
	private int insertVRE6Record(Connection conn, int stringID, Timestamp recordedDate, Double vre6) {
		int rowsInserted = 0;
		try (PreparedStatement statement = conn.prepareStatement(INSERT_VRE6_QUERY);) {
			statement.setInt(1, stringID);
			statement.setTimestamp(2, recordedDate);
			statement.setDouble(3, vre6 != null ? vre6 : 0);
			statement.setString(4, AVG_CALC_WORKFLOW);
			rowsInserted = statement.executeUpdate();
			LOGGER.info(rowsInserted + " rows inserted with VRE6 value in VRE table for String : " + stringID
					+ " & Date : " + recordedDate);
		} catch (Exception e) {
			LOGGER.severe(e.getMessage());
			e.printStackTrace();
		}
		return rowsInserted;
	}

}
