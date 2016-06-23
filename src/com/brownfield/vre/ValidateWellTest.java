package com.brownfield.vre;

import static com.brownfield.vre.VREConstants.CV_LIQ_RATE_MAX;
import static com.brownfield.vre.VREConstants.CV_WHP_MAX;
import static com.brownfield.vre.VREConstants.CV_WATERCUT_MAX;
import static com.brownfield.vre.VREConstants.DATE_TIME_FORMAT;
import static com.brownfield.vre.VREConstants.EFFECTIVE_DATE;
import static com.brownfield.vre.VREConstants.END_OFFSET;
import static com.brownfield.vre.VREConstants.FREEZE_LIQUID_RATE_LIMIT;
import static com.brownfield.vre.VREConstants.FREEZE_WHP_LIMIT;
import static com.brownfield.vre.VREConstants.GET_STRING_METADATA_QUERY;
import static com.brownfield.vre.VREConstants.INSERT_WELL_TEST_QUERY;
import static com.brownfield.vre.VREConstants.MAX_LIQUID_RATE;
import static com.brownfield.vre.VREConstants.MAX_WHP;
import static com.brownfield.vre.VREConstants.MIN_LIQUID_RATE;
import static com.brownfield.vre.VREConstants.MIN_WHP;
import static com.brownfield.vre.VREConstants.PHD_QUERY;
import static com.brownfield.vre.VREConstants.PHD_TEIID_URL;
import static com.brownfield.vre.VREConstants.PLATFORM_ID;
import static com.brownfield.vre.VREConstants.PLATFORM_NAME;
import static com.brownfield.vre.VREConstants.QL1;
import static com.brownfield.vre.VREConstants.ROW_CHANGED_BY;
import static com.brownfield.vre.VREConstants.ROW_CHANGED_DATE;
import static com.brownfield.vre.VREConstants.SHRINKAGE_FACTOR;
import static com.brownfield.vre.VREConstants.SINGLE_RATE_TEST;
import static com.brownfield.vre.VREConstants.SOURCE_VALUES;
import static com.brownfield.vre.VREConstants.SQL_DRIVER_NAME;
import static com.brownfield.vre.VREConstants.STABILITY_FLAG;
import static com.brownfield.vre.VREConstants.START_OFFSET;
import static com.brownfield.vre.VREConstants.STRING_ID;
import static com.brownfield.vre.VREConstants.STRING_NAME;
import static com.brownfield.vre.VREConstants.STRING_TYPE;
import static com.brownfield.vre.VREConstants.SWITCH_TIME_ZONE;
import static com.brownfield.vre.VREConstants.TAG_GAS_RATE;
import static com.brownfield.vre.VREConstants.TAG_LIQUID_RATE;
import static com.brownfield.vre.VREConstants.TAG_WATERCUT;
import static com.brownfield.vre.VREConstants.TAG_WHP;
import static com.brownfield.vre.VREConstants.TAG_WHT;
import static com.brownfield.vre.VREConstants.TEIID_DRIVER_NAME;
import static com.brownfield.vre.VREConstants.TEIID_PASSWORD;
import static com.brownfield.vre.VREConstants.TEIID_USER;
import static com.brownfield.vre.VREConstants.TEST_END_DATE;
import static com.brownfield.vre.VREConstants.TEST_START_DATE;
import static com.brownfield.vre.VREConstants.UWI;
import static com.brownfield.vre.VREConstants.VRE_DB_URL;
import static com.brownfield.vre.VREConstants.VRE_FLAG;
import static com.brownfield.vre.VREConstants.VRE_PASSWORD;
import static com.brownfield.vre.VREConstants.VRE_USER;
import static com.brownfield.vre.VREConstants.WATER_CUT_LAB;
import static com.brownfield.vre.VREConstants.WCUT_STABILITY_QUERY;
import static com.brownfield.vre.VREConstants.WELL_TEST_NEW_QUERY;
import static com.brownfield.vre.VREConstants.WHP1;
import static com.brownfield.vre.VREConstants.WTV_WORKFLOW;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.logging.Logger;

import com.brownfield.vre.VREConstants.SOURCE;

/**
 * The Class ValidateWellTest.
 *
 * @author Onkar Dhuri <onkar.dhuri@synerzip.com>
 */
public class ValidateWellTest {

	/** The watercut. */
	private double watercut = 0.0;

	/** The logger. */
	private static final Logger LOGGER = Logger.getLogger(ValidateWellTest.class.getName());

	/**
	 * The main method.
	 *
	 * @param args
	 *            the arguments
	 */
	public static void main(String[] args) {
		try {
			Class.forName(SQL_DRIVER_NAME);
			Class.forName(TEIID_DRIVER_NAME);
			try (Connection vreConn = DriverManager.getConnection(VRE_DB_URL, VRE_USER, VRE_PASSWORD);
					Connection phdConn = DriverManager.getConnection(PHD_TEIID_URL, TEIID_USER, TEIID_PASSWORD);) {

				ValidateWellTest vw = new ValidateWellTest();
				vw.validateNewWellTests(vreConn, phdConn);

			} catch (SQLException e) {
				LOGGER.severe(e.getMessage());
			}

		} catch (ClassNotFoundException e) {
			LOGGER.severe(e.getMessage());
		}
	}

	/**
	 * Identify new well tests.
	 *
	 * @param vreConn
	 *            the vre conn
	 * @param phdConn
	 *            the phd conn
	 */
	public void validateNewWellTests(Connection vreConn, Connection phdConn) {
		try (Statement statement = vreConn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
				ResultSet rset = statement.executeQuery(WELL_TEST_NEW_QUERY);) {

			int stringID;
			Timestamp testDate;
			String effectiveTestDate;

			if (rset != null) {
				while (rset.next()) {
					stringID = rset.getInt(STRING_ID);
					testDate = rset.getTimestamp(TEST_END_DATE);
					effectiveTestDate = rset.getString(EFFECTIVE_DATE);
					double ql1Standard = rset.getDouble(QL1);
					double whpFT = rset.getDouble(WHP1);

					StringBuilder sb = new StringBuilder();

					watercut = 0.0;
					// this will also update watercut
					boolean isStable = this.isStable(vreConn, stringID, effectiveTestDate);
					// update the FTQL1 to standard conditions.
					ql1Standard = Utils.getStandardConditionRate(ql1Standard, SHRINKAGE_FACTOR, watercut);

					Map<String, String> tags = this.getTags(vreConn, stringID);
					Map<String, String> startEndDates = this.getStartEndDates(testDate, START_OFFSET, END_OFFSET,
							SWITCH_TIME_ZONE);

					boolean liqRateTagAvailable = true, whpTagAvailable = true, wcutTagAvailable = true;
					boolean setVRE = false;
					if (tags.get(TAG_LIQUID_RATE) == null) {
						sb.append(TAG_LIQUID_RATE + " tag is missing for Platform " + tags.get(PLATFORM_NAME))
								.append("\n");
						liqRateTagAvailable = false;
					}
					if (tags.get(TAG_WHP) == null) {
						sb.append(TAG_WHP + " is missing for String " + tags.get(STRING_NAME)).append("\n");
						whpTagAvailable = false;
					}
					if (tags.get(TAG_WATERCUT) == null) {
						sb.append(TAG_WATERCUT + " is missing for Platform " + tags.get(PLATFORM_NAME)).append("\n");
						wcutTagAvailable = false;
					}

					boolean liqRateHasNulls = false, whpHasNulls = false, wcutHasNulls = false;
					boolean isOutOfRangeLiqRate = false, isOutOfRangeWHP = false, isOutOfRangeWCUT = false;
					boolean isBelowFreezeLiqRate = false, isBelowFreezeWHP = false, isBelowFreezeWCUT = false;

					boolean phdLiqRateDataAvail = true, phdWHPDataAvail = true, phdWCUTDataAvail = true;

					double meanLiqRate = ql1Standard;
					double meanWHP = whpFT; // default to FT
					double meanWCUT = watercut; // default to Lab WCUT
					double cvLiqRate = 0, cvWHP = 0, cvWCUT = 0;
					double standardLiqRate = ql1Standard; // default to FT

					String startDate = startEndDates.get(TEST_START_DATE);
					String endDate = startEndDates.get(TEST_END_DATE);
					if (liqRateTagAvailable) {
						List<Double> liqidRates = this.getPHDData(phdConn, tags.get(TAG_LIQUID_RATE), startDate,
								endDate);
						if (!liqidRates.isEmpty()) {
							liqRateHasNulls = this.hasNullValues(liqidRates);
							isOutOfRangeLiqRate = this.hasOutOfRangeData(liqidRates, MIN_LIQUID_RATE, MAX_LIQUID_RATE);
							isBelowFreezeLiqRate = this.isSensorFreezed(liqidRates, FREEZE_LIQUID_RATE_LIMIT);

							if (liqRateHasNulls) {
								sb.append("Null liquid rate values for " + tags.get(TAG_LIQUID_RATE)).append("\n");
							}
							if (isOutOfRangeLiqRate) {
								sb.append("Out of range liquid rate for " + tags.get(TAG_LIQUID_RATE)).append("\n");
							}
							if (isBelowFreezeLiqRate) {
								sb.append("Liquid rate sensor frozen for " + tags.get(TAG_LIQUID_RATE)).append("\n");
							}
							Statistics stat = new Statistics(liqidRates);
							meanLiqRate = stat.getMean();
							cvLiqRate = stat.getCoefficientOfVariation(meanLiqRate);
						} else {
							sb.append("No PHD liquid rate available for " + tags.get(TAG_LIQUID_RATE)).append("\n");
							phdLiqRateDataAvail = false;
						}
					}

					if (whpTagAvailable) {
						List<Double> whpList = this.getPHDData(phdConn, tags.get(TAG_WHP), startDate, endDate);
						if (!whpList.isEmpty()) {
							whpHasNulls = this.hasNullValues(whpList);
							isOutOfRangeWHP = this.hasOutOfRangeData(whpList, MIN_WHP, MAX_WHP);
							isBelowFreezeWHP = this.isSensorFreezed(whpList, FREEZE_WHP_LIMIT);

							if (whpHasNulls) {
								sb.append("Null WHP values for " + tags.get(TAG_WHP)).append("\n");
							}
							if (isOutOfRangeWHP) {
								sb.append("Out of range WHP for " + tags.get(TAG_WHP)).append("\n");
							}
							if (isBelowFreezeWHP) {
								sb.append("WHP sensor frozen for " + tags.get(TAG_WHP)).append("\n");
							}
							Statistics stat = new Statistics(whpList);
							meanWHP = stat.getMean();
							cvWHP = stat.getCoefficientOfVariation(meanWHP);
						} else {
							sb.append("No PHD whp available for " + tags.get(TAG_WHP)).append("\n");
							phdWHPDataAvail = false;
						}
					}

					if (wcutTagAvailable) {
						List<Double> wcutList = this.getPHDData(phdConn, tags.get(TAG_WATERCUT), startDate, endDate);
						modifyWaterCutsToFraction(wcutList);
						if (!wcutList.isEmpty()) {
							wcutHasNulls = this.hasNullValues(wcutList);
							isOutOfRangeWCUT = this.hasOutOfRangeData(wcutList, MIN_WHP, MAX_WHP);
							isBelowFreezeWCUT = this.isSensorFreezed(wcutList, FREEZE_WHP_LIMIT);

							if (wcutHasNulls) {
								sb.append("Null watercut values for " + tags.get(TAG_WATERCUT)).append("\n");
							}
							if (isOutOfRangeWCUT) {
								sb.append("Out of range watercut for " + tags.get(TAG_WATERCUT)).append("\n");
							}
							if (isBelowFreezeWCUT) {
								sb.append("Watercut sensor frozen for " + tags.get(TAG_WATERCUT)).append("\n");
							}
							Statistics stat = new Statistics(wcutList);
							meanWCUT = stat.getMean();
							cvWCUT = stat.getCoefficientOfVariation(meanWCUT);
						} else {
							sb.append("No PHD watercut available for " + tags.get(TAG_WATERCUT)).append("\n");
							phdWCUTDataAvail = false;
						}
					}

					standardLiqRate = Utils.getStandardConditionRate(meanLiqRate, SHRINKAGE_FACTOR, meanWCUT);

					if (isStable) {
						if (!(liqRateTagAvailable && whpTagAvailable && wcutTagAvailable)) {
							// No platform/string phd tags; if we don't have any
							// one
							// of them, still mark the well as stable
							setVRE = true;
							sb.append("Setting well test as valid").append("\n");
						} else if (!(phdLiqRateDataAvail && phdWHPDataAvail && phdWCUTDataAvail)) {
							// Tags are present but there's no data in PHD
							setVRE = true;
							sb.append("Setting well test as valid").append("\n");
						} else {
							if (!(liqRateHasNulls && whpHasNulls && wcutHasNulls)) {
								if (!(isOutOfRangeLiqRate && isOutOfRangeWHP && isOutOfRangeWCUT)) {
									if (!(isBelowFreezeLiqRate && isBelowFreezeWHP && isBelowFreezeWCUT)) {
										if (cvLiqRate <= CV_LIQ_RATE_MAX) {
											if (cvLiqRate != 0 || meanLiqRate != 0) {
												if (cvWHP <= CV_WHP_MAX) {
													if (cvWHP != 0 || meanWHP != 0) {
														if (cvWCUT <= CV_WATERCUT_MAX) {
															if (cvWCUT != 0 || meanWCUT != 0) {
																setVRE = true;
																sb.append("All Good").append("\n");
															} else {
																sb.append("Watercut is 0").append("\n");
															}
														} else {
															sb.append("Stability test failed. Watercut coefficient "
																	+ cvWCUT + " exceeds max limit " + CV_WATERCUT_MAX)
																	.append("\n");
														}
													} else {
														sb.append("WHP is 0").append("\n");
													}
												} else {
													sb.append("Stability test failed. WHP coefficient " + cvWHP
															+ " exceeds max limit " + CV_WHP_MAX).append("\n");
												}
											} else {
												sb.append("Liquid rate is 0").append("\n");
											}
										} else {
											sb.append("Stability test failed. Liq rate coefficient " + cvLiqRate
													+ " exceeds max limit " + CV_LIQ_RATE_MAX).append("\n");
										}
									}
								}
							}
						}
					} else {
						if (!(liqRateTagAvailable && whpTagAvailable && wcutTagAvailable)) {
							sb.append("Unstable well. No data available for " + tags.get(STRING_NAME)).append("\n");
						} else {
							sb.append("Unstable well - " + tags.get(STRING_NAME)).append("\n");
						}
					}
					sb.delete(sb.length() - 1, sb.length());
					this.insertWellTest(vreConn, stringID, startDate, endDate, standardLiqRate, meanWHP, meanWCUT,
							setVRE, sb.toString());

					rset.updateDouble(QL1, ql1Standard);
					rset.updateBoolean(VRE_FLAG, Boolean.FALSE);
					rset.updateString(ROW_CHANGED_BY, WTV_WORKFLOW);
					rset.updateTimestamp(ROW_CHANGED_DATE, new Timestamp(new Date().getTime()));
					rset.updateRow();
				}
			}

		} catch (SQLException e) {
			LOGGER.severe(e.getMessage());
		}
	}

	/**
	 * Checks if is stable.
	 *
	 * @param conn
	 *            the conn
	 * @param stringID
	 *            the string id
	 * @param effectiveDate
	 *            the effective date
	 * @return true, if is stable
	 */
	private boolean isStable(Connection conn, int stringID, String effectiveDate) {
		boolean isStable = true;
		try (PreparedStatement statement = conn.prepareStatement(WCUT_STABILITY_QUERY);) {
			statement.setInt(1, stringID);
			statement.setString(2, effectiveDate);
			try (ResultSet rset = statement.executeQuery();) {
				if (rset != null && rset.next()) { // always one row per date
					// set watercut to seabed wc / 100
					watercut = rset.getDouble(WATER_CUT_LAB) / 100;
					if (rset.getObject(STABILITY_FLAG) != null) {
						isStable = rset.getBoolean(STABILITY_FLAG);
					}
				}
			} catch (Exception e) {
				LOGGER.severe(e.getMessage());
			}
		} catch (Exception e) {
			LOGGER.severe(e.getMessage());
		}
		return isStable;
	}

	/**
	 * Gets the tags.
	 *
	 * @param conn
	 *            the conn
	 * @param stringID
	 *            the string id
	 * @return the tags
	 */
	private Map<String, String> getTags(Connection conn, int stringID) {
		Map<String, String> tags = new HashMap<>();

		try (PreparedStatement statement = conn.prepareStatement(GET_STRING_METADATA_QUERY);) {
			statement.setInt(1, stringID);
			try (ResultSet rset = statement.executeQuery();) {
				if (rset != null && rset.next()) { // one row per string
					tags.put(UWI, rset.getString(UWI));
					tags.put(STRING_TYPE, rset.getString(STRING_TYPE));
					tags.put(STRING_NAME, rset.getString(STRING_NAME));
					tags.put(PLATFORM_ID, rset.getString(PLATFORM_ID));
					tags.put(PLATFORM_NAME, rset.getString(PLATFORM_NAME));
					tags.put(TAG_LIQUID_RATE, rset.getString(TAG_LIQUID_RATE));
					tags.put(TAG_GAS_RATE, rset.getString(TAG_GAS_RATE));
					tags.put(TAG_WATERCUT, rset.getString(TAG_WATERCUT));
					tags.put(TAG_WHP, rset.getString(TAG_WHP));
					tags.put(TAG_WHT, rset.getString(TAG_WHT));
				}
			} catch (Exception e) {
				LOGGER.severe(e.getMessage());
			}
		} catch (Exception e) {
			LOGGER.severe(e.getMessage());
		}

		return tags;
	}

	/**
	 * Gets the PHD data.
	 *
	 * @param conn
	 *            the conn
	 * @param tagName
	 *            the tag name
	 * @param startDate
	 *            the start date
	 * @param endDate
	 *            the end date
	 * @return the PHD data
	 */
	private List<Double> getPHDData(Connection conn, String tagName, String startDate, String endDate) {
		List<Double> values = new ArrayList<>();
		try (PreparedStatement statement = conn.prepareStatement(PHD_QUERY);) {
			statement.setString(1, tagName);
			statement.setString(2, startDate);
			statement.setString(3, endDate);
			try (ResultSet rset = statement.executeQuery();) {
				if (rset != null) {
					while (rset.next()) {
						BigDecimal bigDecimal = rset.getBigDecimal(SOURCE_VALUES);
						values.add(bigDecimal != null ? bigDecimal.doubleValue() : null);
					}
				}
			} catch (Exception e) {
				LOGGER.severe(e.getMessage());
			}
		} catch (Exception e) {
			LOGGER.severe(e.getMessage());
		}
		return values;
	}

	/**
	 * Insert well test.
	 *
	 * @param conn
	 *            the conn
	 * @param stringID
	 *            the string id
	 * @param startDate
	 *            the start date
	 * @param endDate
	 *            the end date
	 * @param liquidRate
	 *            the liquid rate
	 * @param whp
	 *            the whp
	 * @param watercut
	 *            the watercut
	 * @param vreFlag
	 *            the vre flag
	 * @param remark
	 *            the remark
	 * @return the int
	 */
	private int insertWellTest(Connection conn, int stringID, String startDate, String endDate, double liquidRate,
			double whp, double watercut, boolean vreFlag, String remark) {
		int rowsInserted = 0;
		try (PreparedStatement statement = conn.prepareStatement(INSERT_WELL_TEST_QUERY);) {
			statement.setInt(1, stringID);
			statement.setString(2, SINGLE_RATE_TEST);
			Timestamp testStartDate = Utils.getDateFromString(startDate, SWITCH_TIME_ZONE);
			Timestamp testEndDate = Utils.getDateFromString(endDate, SWITCH_TIME_ZONE);
			statement.setTimestamp(3, testStartDate);
			statement.setTimestamp(4, testEndDate);
			statement.setInt(5, SOURCE.VRE.getNumVal());
			statement.setDouble(6, liquidRate);
			statement.setDouble(7, whp);
			statement.setDouble(8, watercut);
			statement.setBoolean(9, vreFlag);
			statement.setString(10, remark);
			rowsInserted = statement.executeUpdate();
			LOGGER.info(rowsInserted + " rows inserted in WELLTEST table with String : " + stringID + " & Date : "
					+ startDate);
		} catch (Exception e) {
			LOGGER.severe(e.getMessage());
		}
		return rowsInserted;
	}

	/**
	 * Gets the start end dates.
	 *
	 * @param testDate
	 *            the test date
	 * @param startOffSet
	 *            the start off set
	 * @param endOffSet
	 *            the end off set
	 * @param shiftTimeZone
	 *            the shift time zone
	 * @return the start end dates
	 */
	private Map<String, String> getStartEndDates(Timestamp testDate, double startOffSet, double endOffSet,
			boolean shiftTimeZone) {
		Map<String, String> dates = new HashMap<>();
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_TIME_FORMAT);
		System.out.println("TestDate : " + testDate);

		Calendar startCal = Calendar.getInstance();
		startCal.setTime(testDate);
		startCal.add(Calendar.MINUTE, (int) startOffSet);

		Calendar endCal = Calendar.getInstance();
		endCal.setTime(testDate);
		endCal.add(Calendar.MINUTE, (int) endOffSet);

		if (shiftTimeZone) {
			TimeZone utcTZ = TimeZone.getTimeZone("UTC");
			sdf.setTimeZone(utcTZ);
			System.out.println("PHD Date : " + sdf.format(testDate.getTime()));
		}

		System.out.println("Start : " + sdf.format(startCal.getTime()));
		System.out.println("End : " + sdf.format(endCal.getTime()));

		dates.put(TEST_START_DATE, sdf.format(startCal.getTime()));
		dates.put(TEST_END_DATE, sdf.format(endCal.getTime()));

		return dates;
	}

	/**
	 * Checks for null values.
	 *
	 * @param rates
	 *            the rates
	 * @return true, if successful
	 */
	private boolean hasNullValues(List<Double> rates) {
		boolean hasNull = false;
		if (!rates.isEmpty()) {
			for (Double rate : rates) {
				if (rate == null) {
					hasNull = true;
					break;
				}
			}
		} else {
			hasNull = true;
		}
		return hasNull;
	}

	/**
	 * Checks for out of range data.
	 *
	 * @param rates
	 *            the rates
	 * @param minValue
	 *            the min value
	 * @param maxValue
	 *            the max value
	 * @return true, if successful
	 */
	private boolean hasOutOfRangeData(List<Double> rates, double minValue, double maxValue) {
		boolean hasOutOfRange = false;
		if (!rates.isEmpty()) {
			for (Double rate : rates) {
				if (rate > maxValue || rate < minValue) {
					hasOutOfRange = true;
					break;
				}
			}
		} else {
			hasOutOfRange = true;
		}
		return hasOutOfRange;
	}

	/**
	 * Checks if sensor is freezed.
	 *
	 * @param rates
	 *            the rates
	 * @param freezeLimit
	 *            the freeze limit
	 * @return true, if is sensor freezed
	 */
	private boolean isSensorFreezed(List<Double> rates, double freezeLimit) {
		if (!rates.isEmpty()) {
			Double max = Collections.max(rates);
			Double min = Collections.min(rates);
			return max - min < freezeLimit ? true : false;
		}
		return true;
	}

	/**
	 * Modify water cuts to fraction.
	 *
	 * @param wcuts
	 *            the wcuts
	 */
	private void modifyWaterCutsToFraction(List<Double> wcuts) {
		List<Double> values = new ArrayList<>();
		if (!wcuts.isEmpty()) {
			for (Double wcut : wcuts) {
				wcut = wcut / 100;
				values.add(wcut);
			}
		}
		wcuts.removeAll(wcuts);
		wcuts.addAll(values);
	}

}
