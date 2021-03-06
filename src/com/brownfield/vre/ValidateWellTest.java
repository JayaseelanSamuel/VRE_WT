package com.brownfield.vre;

import static com.brownfield.vre.VREConstants.APP_BASE_URL;
import static com.brownfield.vre.VREConstants.CV_LIQ_RATE_MAX;
import static com.brownfield.vre.VREConstants.CV_WHP_MAX;
import static com.brownfield.vre.VREConstants.DATE_FORMAT;
import static com.brownfield.vre.VREConstants.DATE_TIME_FORMAT;
import static com.brownfield.vre.VREConstants.DSBPM_ALERT_TEMPLATE;
import static com.brownfield.vre.VREConstants.EFFECTIVE_DATE;
import static com.brownfield.vre.VREConstants.EMAIL_GROUP;
import static com.brownfield.vre.VREConstants.END_OFFSET;
import static com.brownfield.vre.VREConstants.FREEZE_LIQUID_RATE_LIMIT;
import static com.brownfield.vre.VREConstants.FREEZE_WHP_LIMIT;
import static com.brownfield.vre.VREConstants.GAS_FLOW_RATE;
import static com.brownfield.vre.VREConstants.GET_STRING_METADATA_QUERY;
import static com.brownfield.vre.VREConstants.INSERT_WELL_TEST_QUERY;
import static com.brownfield.vre.VREConstants.INVALID;
import static com.brownfield.vre.VREConstants.MAX_LIQUID_RATE;
import static com.brownfield.vre.VREConstants.MAX_WHP;
import static com.brownfield.vre.VREConstants.MIN_LIQUID_RATE;
import static com.brownfield.vre.VREConstants.MIN_WHP;
import static com.brownfield.vre.VREConstants.MULTI_RATE_FLOW_TEST;
import static com.brownfield.vre.VREConstants.MULTI_RATE_TEST;
import static com.brownfield.vre.VREConstants.PHD_QUERY;
import static com.brownfield.vre.VREConstants.PHD_TEIID_URL;
import static com.brownfield.vre.VREConstants.PLATFORM_ID;
import static com.brownfield.vre.VREConstants.PLATFORM_NAME;
import static com.brownfield.vre.VREConstants.QL1;
import static com.brownfield.vre.VREConstants.ROW_CHANGED_BY;
import static com.brownfield.vre.VREConstants.ROW_CHANGED_DATE;
import static com.brownfield.vre.VREConstants.SHRINKAGE_FACTOR;
import static com.brownfield.vre.VREConstants.SINGLE_RATE_TEST;
import static com.brownfield.vre.VREConstants.SINGLE_RATE_WELL_TEST_DASHBOARD_LINK;
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
import static com.brownfield.vre.VREConstants.TAG_SEPARATOR_PRESSURE;
import static com.brownfield.vre.VREConstants.TAG_WATERCUT;
import static com.brownfield.vre.VREConstants.TAG_WHP;
import static com.brownfield.vre.VREConstants.TAG_WHT;
import static com.brownfield.vre.VREConstants.TEIID_DRIVER_NAME;
import static com.brownfield.vre.VREConstants.TEIID_PASSWORD;
import static com.brownfield.vre.VREConstants.TEIID_USER;
import static com.brownfield.vre.VREConstants.TEST_END_DATE;
import static com.brownfield.vre.VREConstants.TEST_SEPARATOR_PRESSURE;
import static com.brownfield.vre.VREConstants.TEST_START_DATE;
import static com.brownfield.vre.VREConstants.TEST_TYPE;
import static com.brownfield.vre.VREConstants.UWI;
import static com.brownfield.vre.VREConstants.VALID;
import static com.brownfield.vre.VREConstants.VRE1;
import static com.brownfield.vre.VREConstants.VRE_DB_URL;
import static com.brownfield.vre.VREConstants.VRE_FLAG;
import static com.brownfield.vre.VREConstants.VRE_PASSWORD;
import static com.brownfield.vre.VREConstants.VRE_TABLE_SELECT_QUERY;
import static com.brownfield.vre.VREConstants.VRE_USER;
import static com.brownfield.vre.VREConstants.WATER_CUT;
import static com.brownfield.vre.VREConstants.WCUT_STABILITY_QUERY;
import static com.brownfield.vre.VREConstants.WELL_TEST_BODY_START;
import static com.brownfield.vre.VREConstants.WELL_TEST_EMAIL_TABLE_BODY;
import static com.brownfield.vre.VREConstants.WELL_TEST_EMAIL_TABLE_HEADER;
import static com.brownfield.vre.VREConstants.WELL_TEST_EMAIL_TABLE_ROW;
import static com.brownfield.vre.VREConstants.WELL_TEST_ID;
import static com.brownfield.vre.VREConstants.WELL_TEST_NEW_QUERY;
import static com.brownfield.vre.VREConstants.WELL_TEST_SUBJECT;
import static com.brownfield.vre.VREConstants.WHP1;
import static com.brownfield.vre.VREConstants.WTV_WORKFLOW;
import static com.brownfield.vre.VREConstants.WT_TREND_LIMIT;

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

import org.teiid.core.util.StringUtil;

import com.brownfield.vre.VREConstants.SOURCE;
import com.brownfield.vre.exe.models.StringModel;

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
				LOGGER.severe(e.getMessage()); e.printStackTrace();
			}

		} catch (ClassNotFoundException e) {
			LOGGER.severe(e.getMessage()); e.printStackTrace();
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

			int stringID, ftTestID;
			Timestamp testDate, prevDay;
			double prevVRE;
			String effectiveTestDate, testType, vreTestType;
			int rowCount = 0;
			StringBuilder emailBody = new StringBuilder();

			if (rset != null) {
				while (rset.next()) {
					ftTestID = rset.getInt(WELL_TEST_ID);
					stringID = rset.getInt(STRING_ID);
					testDate = rset.getTimestamp(TEST_END_DATE);
					effectiveTestDate = rset.getString(EFFECTIVE_DATE);
					testType = rset.getString(TEST_TYPE);
					double ql1Standard = rset.getDouble(QL1);
					double whpFT = rset.getDouble(WHP1);
					double gasFlowRateFT = rset.getDouble(GAS_FLOW_RATE);
					double sepPressureFT = rset.getDouble(TEST_SEPARATOR_PRESSURE);
					vreTestType = SINGLE_RATE_TEST;
					rowCount++;
					StringModel sm = Utils.getStringModel(vreConn, stringID);

					StringBuilder sb = new StringBuilder();

					watercut = 0.0;
					// this will also update watercut
					boolean isStable = this.isStable(vreConn, stringID, effectiveTestDate);
					// update the FTQL1 to standard conditions.
					ql1Standard = Utils.getStandardConditionRate(ql1Standard, SHRINKAGE_FACTOR, watercut / 100);

					Map<String, String> tags = this.getTags(vreConn, stringID);
					Map<String, String> startEndDates = this.getStartEndDates(testDate, START_OFFSET, END_OFFSET,
							SWITCH_TIME_ZONE);


					boolean liqRateHasNulls = false, whpHasNulls = false; //, wcutHasNulls = false;
					boolean isOutOfRangeLiqRate = false, isOutOfRangeWHP = false; //, isOutOfRangeWCUT = false;
					boolean isBelowFreezeLiqRate = false, isBelowFreezeWHP = false; //, isBelowFreezeWCUT = false;

					boolean phdLiqRateDataAvail = true, phdWHPDataAvail = true; //, phdWCUTDataAvail = true;

					double standardLiqRate = ql1Standard; // default to FT
					double liquidRate = ql1Standard;
					//double phdMeanLiqRate = 0;
					double whp = whpFT; // default to FT
					//double phdMeanWHP = 0;
					double wcut = watercut; // default to Lab WCUT
					//double phdMeanWCUT = 0;
					double sepPressure = sepPressureFT; // default to FT
					//double phdMeanSepPressure = 0;
					double gasFlowRate = gasFlowRateFT; // default to FT
					//double phdMeanGasFlowRate = 0;
					double cvLiqRate = 0, cvWHP = 0; //, cvWCUT = 0;

					String startDate = startEndDates.get(TEST_START_DATE);
					String endDate = startEndDates.get(TEST_END_DATE);
					
					boolean liqRateTagAvailable = true, whpTagAvailable = true; //, wcutTagAvailable = true;
					boolean setVRE = false;
					
					if (StringUtil.equalsIgnoreCase(testType, MULTI_RATE_FLOW_TEST)) {
						/*
						 * this is multi-rate well test. Don't do validation and
						 * just set the data to FT err MT data.
						 */
						sb.append("Multirate test. Skipping validation").append("\n");
						vreTestType = MULTI_RATE_TEST;
					} else {
					
						if (tags.get(TAG_LIQUID_RATE) == null) {
							sb.append(TAG_LIQUID_RATE + " tag is missing for Platform " + tags.get(PLATFORM_NAME))
									.append("\n");
							liqRateTagAvailable = false;
						}
						if (tags.get(TAG_WHP) == null) {
							sb.append(TAG_WHP + " is missing for String " + tags.get(STRING_NAME)).append("\n");
							whpTagAvailable = false;
						}
						/*if (tags.get(TAG_WATERCUT) == null) {
							sb.append(TAG_WATERCUT + " is missing for Platform " + tags.get(PLATFORM_NAME)).append("\n");
							wcutTagAvailable = false;
						}*/
	
	
						// get GAS_RATE and SEPARATOR_PRESSURE data w/o validation
						if (tags.get(TAG_GAS_RATE) != null) {
							List<Double> gasRates = this.getPHDData(phdConn, tags.get(TAG_GAS_RATE), startDate, endDate);
							if (!gasRates.isEmpty()) {
								Statistics stat = new Statistics(gasRates);
								gasFlowRate = stat.getMean(); //phdMeanGasFlowRate
							}
						}
	
						if (tags.get(TAG_SEPARATOR_PRESSURE) != null) {
							List<Double> separatorPressure = this.getPHDData(phdConn, tags.get(TAG_SEPARATOR_PRESSURE),
									startDate, endDate);
							if (!separatorPressure.isEmpty()) {
								Statistics stat = new Statistics(separatorPressure);
								sepPressure = stat.getMean(); //phdMeanSepPressure
							}
						}
						
						if (tags.get(TAG_WATERCUT) != null) {
							List<Double> wcuts = this.getPHDData(phdConn, tags.get(TAG_WATERCUT),
									startDate, endDate);
							if (!wcuts.isEmpty()) {
								Statistics stat = new Statistics(wcuts);
								wcut = stat.getMean();
							}
						}
	
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
								liquidRate = stat.getMean();
								cvLiqRate = stat.getCoefficientOfVariation(liquidRate);
								standardLiqRate = Utils.getStandardConditionRate(liquidRate, SHRINKAGE_FACTOR, (watercut / 100));
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
								whp = stat.getMean();
								cvWHP = stat.getCoefficientOfVariation(whp);
							} else {
								sb.append("No PHD whp available for " + tags.get(TAG_WHP)).append("\n");
								phdWHPDataAvail = false;
							}
						}
	
						/*if (wcutTagAvailable) {
							List<Double> wcutList = this.getPHDData(phdConn, tags.get(TAG_WATERCUT), startDate, endDate);
							// modifyWaterCutsToFraction(wcutList);
							if (!wcutList.isEmpty()) {
								wcutHasNulls = this.hasNullValues(wcutList);
								isOutOfRangeWCUT = this.hasOutOfRangeData(wcutList, MIN_WATERCUT, MAX_WATERCUT);
								isBelowFreezeWCUT = this.isSensorFreezed(wcutList, FREEZE_WATERCUT_LIMIT);
	
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
								//cvWCUT = stat.getCoefficientOfVariation(meanWCUT);
							} else {
								sb.append("No PHD watercut available for " + tags.get(TAG_WATERCUT)).append("\n");
								phdWCUTDataAvail = false;
							}
						}
	
						if(phdLiqRateDataAvail && phdWCUTDataAvail){
							standardLiqRate = Utils.getStandardConditionRate(meanLiqRate, SHRINKAGE_FACTOR, (watercut / 100));	
						}*/
						
						prevDay = Utils.getNextOrPreviousDay(testDate, -1);
						prevVRE = this.getVREForPreviousDay(vreConn, stringID, prevDay);
						boolean withinTrend = true;
						
						if(prevVRE != -1 && phdLiqRateDataAvail){ // we have VRE value recorded for previous day
							withinTrend = Utils.isWithinLimit(liquidRate,prevVRE,WT_TREND_LIMIT);
						}
	
						if (isStable) {
							if (!(liqRateTagAvailable && whpTagAvailable /*&& wcutTagAvailable*/)) {
								// No platform/string phd tags; if we don't have any
								// one
								// of them, still mark the well as stable
								setVRE = true;
								sb.append("Setting well test as valid").append("\n");
							} else if (!(phdLiqRateDataAvail && phdWHPDataAvail /*&& phdWCUTDataAvail*/)) {
								// Tags are present but there's no data in PHD
								setVRE = true;
								sb.append("Setting well test as valid").append("\n");
							} else {
								if (!(liqRateHasNulls || whpHasNulls /*|| wcutHasNulls*/)) {
									if (!(isOutOfRangeLiqRate || isOutOfRangeWHP /*|| isOutOfRangeWCUT*/)) {
										if (!(isBelowFreezeLiqRate || isBelowFreezeWHP /*|| isBelowFreezeWCUT*/)) {
											if (cvLiqRate <= CV_LIQ_RATE_MAX) {
												if (cvLiqRate != 0 || liquidRate != 0) {
													if (cvWHP <= CV_WHP_MAX) {
														if (cvWHP != 0 || whp != 0) {
															/*if (cvWCUT <= CV_WATERCUT_MAX) {
																if (cvWCUT != 0 || meanWCUT != 0) {*/
																	if (withinTrend) {
																		/*whp = phdMeanWHP ;
																		wcut = phdMeanWCUT;
																		sepPressure = phdMeanSepPressure;
																		gasFlowRate = phdMeanGasFlowRate;*/
																		setVRE = true;
																		sb.append("All Good").append("\n");
																	} else {
																		sb.append("Liquid rate " + liquidRate + " is not within " + WT_TREND_LIMIT
																				+ "% trend limit of previous days VRE " + prevVRE).append("\n");
																	}
																/*} else {
																	sb.append("Watercut is 0").append("\n");
																}*/
															/*} else {
																sb.append("Stability test failed. Watercut coefficient "
																		+ cvWCUT + " exceeds max limit " + CV_WATERCUT_MAX)
																		.append("\n");
															}*/
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
							if (!(liqRateTagAvailable && whpTagAvailable /*&& wcutTagAvailable*/)) {
								sb.append("Unstable well. No data available for " + tags.get(STRING_NAME)).append("\n");
							} else {
								sb.append("Unstable well - " + tags.get(STRING_NAME)).append("\n");
							}
						}
						
						if(!setVRE){
							// TODO: WT Consolidation DONE
						}
					}
					
					sb.delete(sb.length() - 1, sb.length());

					emailBody.append(String.format(WELL_TEST_EMAIL_TABLE_ROW,
							Utils.convertToString(testDate, DATE_TIME_FORMAT), sm.getPlatformName(), sm.getStringName(),
							setVRE ? VALID : INVALID, sb.toString().replaceAll("\n", ".")));

					this.insertWellTest(vreConn, stringID, vreTestType, startDate, endDate, standardLiqRate, whp, wcut,
							setVRE, sb.toString(), ftTestID, gasFlowRate, sepPressure);

					rset.updateDouble(QL1, ql1Standard);
					rset.updateBoolean(VRE_FLAG, Boolean.FALSE);
					rset.updateString(ROW_CHANGED_BY, WTV_WORKFLOW);
					rset.updateTimestamp(ROW_CHANGED_DATE, new Timestamp(new Date().getTime()));
					rset.updateRow();
				}
				
				if (rowCount > 0) {
					String emailContent = String.format(WELL_TEST_EMAIL_TABLE_BODY,
							WELL_TEST_EMAIL_TABLE_HEADER + emailBody.toString());
					// generate consolidated email
					AlertHandler.notifyByEmail(EMAIL_GROUP, DSBPM_ALERT_TEMPLATE,
							APP_BASE_URL + SINGLE_RATE_WELL_TEST_DASHBOARD_LINK,
							String.format(WELL_TEST_SUBJECT, Utils.convertToString(new Date(), DATE_FORMAT)),
							String.format(WELL_TEST_BODY_START, Utils.convertToString(new Date(), DATE_FORMAT))
									+ emailContent);

				} else {
					String emailContent = String.format("No new well tests to process/validate");
					
					AlertHandler.notifyByEmail(EMAIL_GROUP, DSBPM_ALERT_TEMPLATE,
							APP_BASE_URL,
							String.format(WELL_TEST_SUBJECT, Utils.convertToString(new Date(), DATE_FORMAT)),
							emailContent);
				}
			}

		} catch (SQLException e) {
			LOGGER.severe(e.getMessage()); e.printStackTrace();
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
					watercut = rset.getDouble(WATER_CUT);
					if (rset.getObject(STABILITY_FLAG) != null) {
						isStable = rset.getBoolean(STABILITY_FLAG);
					}
				}
			} catch (Exception e) {
				LOGGER.severe(e.getMessage()); e.printStackTrace();
			}
		} catch (Exception e) {
			LOGGER.severe(e.getMessage()); e.printStackTrace();
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
					tags.put(TAG_SEPARATOR_PRESSURE, rset.getString(TAG_SEPARATOR_PRESSURE));
					tags.put(TAG_WHP, rset.getString(TAG_WHP));
					tags.put(TAG_WHT, rset.getString(TAG_WHT));
				}
			} catch (Exception e) {
				LOGGER.severe(e.getMessage()); e.printStackTrace();
			}
		} catch (Exception e) {
			LOGGER.severe(e.getMessage()); e.printStackTrace();
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
				LOGGER.severe(e.getMessage()); e.printStackTrace();
			}
		} catch (Exception e) {
			LOGGER.severe(e.getMessage()); e.printStackTrace();
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
	private int insertWellTest(Connection conn, int stringID, String testType, String startDate, String endDate, double liquidRate,
			double whp, double watercut, boolean vreFlag, String remark, int ftTestID, double gasFlowRate,
			double separatorPressure) {
		int rowsInserted = 0;
		try (PreparedStatement statement = conn.prepareStatement(INSERT_WELL_TEST_QUERY);) {
			statement.setInt(1, stringID);
			statement.setString(2, testType);
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
			statement.setInt(11, ftTestID);
			statement.setDouble(12, gasFlowRate);
			statement.setDouble(13, separatorPressure);
			rowsInserted = statement.executeUpdate();
			LOGGER.info(rowsInserted + " rows inserted in WELLTEST table with String : " + stringID + " & Date : "
					+ testStartDate);
		} catch (Exception e) {
			LOGGER.severe(e.getMessage()); e.printStackTrace();
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
	@SuppressWarnings("unused")
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
	
	
	/**
	 * Gets the VRE1 for previous day.
	 *
	 * @param conn the conn
	 * @param stringID the string id
	 * @param recordedDate the recorded date
	 * @return the VRE for previous day
	 */
	private double getVREForPreviousDay(Connection conn, int stringID, Timestamp recordedDate) {
		double vre = -1;
		try (PreparedStatement statement = conn.prepareStatement(VRE_TABLE_SELECT_QUERY);) {
			statement.setTimestamp(1, recordedDate);
			statement.setInt(2, stringID);
			try (ResultSet rset = statement.executeQuery();) {
				if (rset != null && rset.next()) {
					vre = rset.getDouble(VRE1);
				}
			} catch (Exception e) {
				LOGGER.severe(e.getMessage()); e.printStackTrace();
			}
		} catch (Exception e) {
			LOGGER.severe(e.getMessage()); e.printStackTrace();
		}
		return vre;
	}


}
