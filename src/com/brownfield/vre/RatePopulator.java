package com.brownfield.vre;

import static com.brownfield.vre.VREConstants.GET_PHD_QUERY;
import static com.brownfield.vre.VREConstants.GET_SATELLITE_QUERY;
import static com.brownfield.vre.VREConstants.GET_TECHNICAL_RATE_TAG;
import static com.brownfield.vre.VREConstants.INSERT_MEASURED_RATE_QUERY;
import static com.brownfield.vre.VREConstants.INSERT_TARGET_RATE_QUERY;
import static com.brownfield.vre.VREConstants.MEASURED_RATE;
import static com.brownfield.vre.VREConstants.MEASURED_RATE_QUERY;
import static com.brownfield.vre.VREConstants.ROW_CHANGED_DATE;
import static com.brownfield.vre.VREConstants.ROW_CHANGED_BY;
import static com.brownfield.vre.VREConstants.VRE_WORKFLOW;
import static com.brownfield.vre.VREConstants.SATELLITE_ID;
import static com.brownfield.vre.VREConstants.SOURCE_VALUES;
import static com.brownfield.vre.VREConstants.STRING_ID;
import static com.brownfield.vre.VREConstants.STRING_TARGET_RATE_QUERY;
import static com.brownfield.vre.VREConstants.TAG_MEASURED_RATE;
import static com.brownfield.vre.VREConstants.TECHNICAL_RATE;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Date;
import java.util.logging.Logger;

/**
 * The Class RatePopulator.
 * 
 * @author Onkar Dhuri <onkar.dhuri@synerzip.com>
 */
public class RatePopulator {

	/** The Constant LOGGER. */
	private static final Logger LOGGER = Logger.getLogger(RatePopulator.class.getName());

	/**
	 * Populate technical rates.
	 *
	 * @param vreConn
	 *            the vre conn
	 * @param phdConn
	 *            the phd conn
	 * @param date
	 *            the date
	 */
	public void populateTechnicalRates(Connection vreConn, Connection phdConn, Timestamp date) {
		Timestamp startDate = Utils.getStartOfTheMonth(date);
		try (PreparedStatement statement = vreConn.prepareStatement(GET_TECHNICAL_RATE_TAG);) {
			try (ResultSet rset = statement.executeQuery();) {
				if (rset != null) {
					String technicalTag;
					int stringID;
					int stringCount = 0, tagCount = 0, trCount = 0;
					while (rset.next()) {
						stringCount++;
						stringID = rset.getInt(STRING_ID);
						technicalTag = rset.getString(TECHNICAL_RATE);
						if (technicalTag != null) {
							tagCount++;
							try (PreparedStatement stmt = phdConn.prepareStatement(GET_PHD_QUERY);) {
								stmt.setString(1, technicalTag);
								stmt.setTimestamp(2, date);
								try (ResultSet rs = stmt.executeQuery();) {
									if (rs != null && rs.next()) {
										trCount++;
										BigDecimal bigDecimal = rs.getBigDecimal(SOURCE_VALUES);
										Double tr = bigDecimal != null ? bigDecimal.doubleValue() : null;
										this.insertOrUpdateTechnicalRate(vreConn, stringID, startDate, tr);
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
						// startDate = rset.getTimestamp(START_DATE);
						// endDate = rset.getTimestamp(END_DATE);
					}
					LOGGER.info("Found " + tagCount + " out of " + stringCount
							+ " with technical rate tags and inserted/updated technical rates for - " + trCount);
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
	 * Insert or update technical rate.
	 *
	 * @param vreConn
	 *            the vre conn
	 * @param stringID
	 *            the string ID
	 * @param recordedDate
	 *            the recorded date
	 * @param technicalRate
	 *            the technical rate
	 */ 
	private void insertOrUpdateTechnicalRate(Connection vreConn, int stringID, Timestamp recordedDate,
			Double technicalRate) {
		try (PreparedStatement statement = vreConn.prepareStatement(STRING_TARGET_RATE_QUERY,
				ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE)) {
			statement.setInt(1, stringID);
			statement.setTimestamp(2, recordedDate);
			try (ResultSet rset = statement.executeQuery()) {
				if (rset.next()) {
					rset.updateDouble(TECHNICAL_RATE, technicalRate);
					//Changed by Jay
					rset.updateTimestamp(ROW_CHANGED_DATE, new Timestamp(new Date().getTime()));
					rset.updateString(ROW_CHANGED_BY,VRE_WORKFLOW);
					rset.updateRow();
				} else {
					try (PreparedStatement stmt = vreConn.prepareStatement(INSERT_TARGET_RATE_QUERY);) {
						stmt.setInt(1, stringID);
						stmt.setTimestamp(2, recordedDate);
						stmt.setDouble(3, technicalRate);
						stmt.setObject(4, null);// max_flow_rate_pressure
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

	/**
	 * Populate satellite measured rates.
	 *
	 * @param vreConn
	 *            the vre conn
	 * @param phdConn
	 *            the phd conn
	 * @param date
	 *            the date
	 */
	public void populateSatelliteMeasuredRates(Connection vreConn, Connection phdConn, Timestamp date) {
		// Timestamp startDate = Utils.getStartOfTheMonth(date);
		try (PreparedStatement statement = vreConn.prepareStatement(GET_SATELLITE_QUERY);) {
			try (ResultSet rset = statement.executeQuery();) {
				if (rset != null) {
					String technicalTag;
					int satelliteID;
					while (rset.next()) {
						satelliteID = rset.getInt(SATELLITE_ID);
						technicalTag = rset.getString(TAG_MEASURED_RATE);
						if (technicalTag != null) {
							try (PreparedStatement stmt = phdConn.prepareStatement(GET_PHD_QUERY);) {
								stmt.setString(1, technicalTag);
								stmt.setTimestamp(2, date);
								try (ResultSet rs = stmt.executeQuery();) {
									if (rs != null && rs.next()) {
										BigDecimal bigDecimal = rs.getBigDecimal(SOURCE_VALUES);
										Double measuredRate = bigDecimal != null ? bigDecimal.doubleValue() : null;
										this.insertOrUpdateSatelliteMeasureRate(vreConn, satelliteID, date,
												measuredRate);
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
	 * Insert or update satellite measure rate.
	 *
	 * @param vreConn
	 *            the vre conn
	 * @param satelliteID
	 *            the satellite ID
	 * @param recordedDate
	 *            the recorded date
	 * @param measuredRate
	 *            the measured rate
	 */
	private void insertOrUpdateSatelliteMeasureRate(Connection vreConn, int satelliteID, Timestamp recordedDate,
			Double measuredRate) {
		try (PreparedStatement statement = vreConn.prepareStatement(MEASURED_RATE_QUERY,
				ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE)) {
			statement.setInt(1, satelliteID);
			statement.setTimestamp(2, recordedDate);
			LOGGER.info("Insert/update satellite measured rates for - " + satelliteID + " and " + recordedDate);
			try (ResultSet rset = statement.executeQuery()) {
				if (rset.next()) {
					rset.updateDouble(MEASURED_RATE, measuredRate);
					rset.updateTimestamp(ROW_CHANGED_DATE, new Timestamp(new Date().getTime()));
					rset.updateRow();
				} else {
					try (PreparedStatement stmt = vreConn.prepareStatement(INSERT_MEASURED_RATE_QUERY);) {
						stmt.setInt(1, satelliteID);
						stmt.setTimestamp(2, recordedDate);
						stmt.setDouble(3, measuredRate);
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
