package com.brownfield.vre.exe.models;

import java.sql.Timestamp;

/**
 * The Class StringModel.
 * 
 * @author Onkar Dhuri <onkar.dhuri@synerzip.com>
 */
public class StringModel {

	/** The string id. */
	private int stringID;

	/** The uwi. */
	private String uwi;

	/** The string type. */
	private String stringType;

	/** The string name. */
	private String stringName;

	/** The completion date. */
	private Timestamp completionDate;

	/** The latitude. */
	private double latitude;

	/** The longitude. */
	private double longitude;

	/** The current status. */
	private String currentStatus;

	/** The platform id. */
	private int platformID;

	/** The platform name. */
	private String platformName;

	/** The tag whp. */
	private String tagWHP;

	/** The tag wht. */
	private String tagWHT;

	/** The tag choke size. */
	private String tagChokeSize;

	/** The tag downhole pressure. */
	private String tagDownholePressure;

	/** The tag gaslift inj rate. */
	private String tagGasliftInjRate;

	/** The tag water vol rate. */
	private String tagWaterVolRate;

	/** The tag oil vol rate. */
	private String tagOilVolRate;

	/** The tag ann pressure a. */
	private String tagAnnPressureA;

	/** The tag ann pressure b. */
	private String tagAnnPressureB;

	/** The pipesim model loc. */
	private String pipesimModelLoc;

	/** The stable. */
	private boolean stable;

	/** The tag liquid rate. */
	private String tagLiquidRate;

	/** The tag gas rate. */
	private String tagGasRate;

	/** The tag watercut. */
	private String tagWatercut;

	/** The tag header pressure. */
	private String tagHeaderPressure;

	/** The tag inj header pressure. */
	private String tagInjHeaderPressure;

	/**
	 * Gets the string id.
	 *
	 * @return the string id
	 */
	public int getStringID() {
		return stringID;
	}

	/**
	 * Sets the string id.
	 *
	 * @param stringID
	 *            the new string id
	 */
	public void setStringID(int stringID) {
		this.stringID = stringID;
	}

	/**
	 * Gets the uwi.
	 *
	 * @return the uwi
	 */
	public String getUwi() {
		return uwi;
	}

	/**
	 * Sets the uwi.
	 *
	 * @param uwi
	 *            the new uwi
	 */
	public void setUwi(String uwi) {
		this.uwi = uwi;
	}

	/**
	 * Gets the string type.
	 *
	 * @return the string type
	 */
	public String getStringType() {
		return stringType;
	}

	/**
	 * Sets the string type.
	 *
	 * @param stringType
	 *            the new string type
	 */
	public void setStringType(String stringType) {
		this.stringType = stringType;
	}

	/**
	 * Gets the string name.
	 *
	 * @return the string name
	 */
	public String getStringName() {
		return stringName;
	}

	/**
	 * Sets the string name.
	 *
	 * @param stringName
	 *            the new string name
	 */
	public void setStringName(String stringName) {
		this.stringName = stringName;
	}

	/**
	 * Gets the completion date.
	 *
	 * @return the completion date
	 */
	public Timestamp getCompletionDate() {
		return completionDate;
	}

	/**
	 * Sets the completion date.
	 *
	 * @param completionDate
	 *            the new completion date
	 */
	public void setCompletionDate(Timestamp completionDate) {
		this.completionDate = completionDate;
	}

	/**
	 * Gets the latitude.
	 *
	 * @return the latitude
	 */
	public double getLatitude() {
		return latitude;
	}

	/**
	 * Sets the latitude.
	 *
	 * @param latitude
	 *            the new latitude
	 */
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	/**
	 * Gets the longitude.
	 *
	 * @return the longitude
	 */
	public double getLongitude() {
		return longitude;
	}

	/**
	 * Sets the longitude.
	 *
	 * @param longitude
	 *            the new longitude
	 */
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	/**
	 * Gets the current status.
	 *
	 * @return the current status
	 */
	public String getCurrentStatus() {
		return currentStatus;
	}

	/**
	 * Sets the current status.
	 *
	 * @param currentStatus
	 *            the new current status
	 */
	public void setCurrentStatus(String currentStatus) {
		this.currentStatus = currentStatus;
	}

	/**
	 * Gets the platform id.
	 *
	 * @return the platform id
	 */
	public int getPlatformID() {
		return platformID;
	}

	/**
	 * Sets the platform id.
	 *
	 * @param platformID
	 *            the new platform id
	 */
	public void setPlatformID(int platformID) {
		this.platformID = platformID;
	}

	/**
	 * Gets the platform name.
	 *
	 * @return the platform name
	 */
	public String getPlatformName() {
		return platformName;
	}

	/**
	 * Sets the platform name.
	 *
	 * @param platformName
	 *            the new platform name
	 */
	public void setPlatformName(String platformName) {
		this.platformName = platformName;
	}

	/**
	 * Gets the tag whp.
	 *
	 * @return the tag whp
	 */
	public String getTagWHP() {
		return tagWHP;
	}

	/**
	 * Sets the tag whp.
	 *
	 * @param tagWHP
	 *            the new tag whp
	 */
	public void setTagWHP(String tagWHP) {
		this.tagWHP = tagWHP;
	}

	/**
	 * Gets the tag wht.
	 *
	 * @return the tag wht
	 */
	public String getTagWHT() {
		return tagWHT;
	}

	/**
	 * Sets the tag wht.
	 *
	 * @param tagWHT
	 *            the new tag wht
	 */
	public void setTagWHT(String tagWHT) {
		this.tagWHT = tagWHT;
	}

	/**
	 * Gets the tag choke size.
	 *
	 * @return the tag choke size
	 */
	public String getTagChokeSize() {
		return tagChokeSize;
	}

	/**
	 * Sets the tag choke size.
	 *
	 * @param tagChokeSize
	 *            the new tag choke size
	 */
	public void setTagChokeSize(String tagChokeSize) {
		this.tagChokeSize = tagChokeSize;
	}

	/**
	 * Gets the tag downhole pressure.
	 *
	 * @return the tag downhole pressure
	 */
	public String getTagDownholePressure() {
		return tagDownholePressure;
	}

	/**
	 * Sets the tag downhole pressure.
	 *
	 * @param tagDownholePressure
	 *            the new tag downhole pressure
	 */
	public void setTagDownholePressure(String tagDownholePressure) {
		this.tagDownholePressure = tagDownholePressure;
	}

	/**
	 * Gets the tag gaslift inj rate.
	 *
	 * @return the tag gaslift inj rate
	 */
	public String getTagGasliftInjRate() {
		return tagGasliftInjRate;
	}

	/**
	 * Sets the tag gaslift inj rate.
	 *
	 * @param tagGasliftInjRate
	 *            the new tag gaslift inj rate
	 */
	public void setTagGasliftInjRate(String tagGasliftInjRate) {
		this.tagGasliftInjRate = tagGasliftInjRate;
	}

	/**
	 * Gets the tag water vol rate.
	 *
	 * @return the tag water vol rate
	 */
	public String getTagWaterVolRate() {
		return tagWaterVolRate;
	}

	/**
	 * Sets the tag water vol rate.
	 *
	 * @param tagWaterVolRate
	 *            the new tag water vol rate
	 */
	public void setTagWaterVolRate(String tagWaterVolRate) {
		this.tagWaterVolRate = tagWaterVolRate;
	}

	/**
	 * Gets the tag oil vol rate.
	 *
	 * @return the tag oil vol rate
	 */
	public String getTagOilVolRate() {
		return tagOilVolRate;
	}

	/**
	 * Sets the tag oil vol rate.
	 *
	 * @param tagOilVolRate
	 *            the new tag oil vol rate
	 */
	public void setTagOilVolRate(String tagOilVolRate) {
		this.tagOilVolRate = tagOilVolRate;
	}

	/**
	 * Gets the tag ann pressure a.
	 *
	 * @return the tag ann pressure a
	 */
	public String getTagAnnPressureA() {
		return tagAnnPressureA;
	}

	/**
	 * Sets the tag ann pressure a.
	 *
	 * @param tagAnnPressureA
	 *            the new tag ann pressure a
	 */
	public void setTagAnnPressureA(String tagAnnPressureA) {
		this.tagAnnPressureA = tagAnnPressureA;
	}

	/**
	 * Gets the tag ann pressure b.
	 *
	 * @return the tag ann pressure b
	 */
	public String getTagAnnPressureB() {
		return tagAnnPressureB;
	}

	/**
	 * Sets the tag ann pressure b.
	 *
	 * @param tagAnnPressureB
	 *            the new tag ann pressure b
	 */
	public void setTagAnnPressureB(String tagAnnPressureB) {
		this.tagAnnPressureB = tagAnnPressureB;
	}

	/**
	 * Gets the pipesim model loc.
	 *
	 * @return the pipesim model loc
	 */
	public String getPipesimModelLoc() {
		return pipesimModelLoc;
	}

	/**
	 * Sets the pipesim model loc.
	 *
	 * @param pipesimModelLoc
	 *            the new pipesim model loc
	 */
	public void setPipesimModelLoc(String pipesimModelLoc) {
		this.pipesimModelLoc = pipesimModelLoc;
	}

	/**
	 * Checks if is stable.
	 *
	 * @return true, if is stable
	 */
	public boolean isStable() {
		return stable;
	}

	/**
	 * Sets the stable.
	 *
	 * @param stable
	 *            the new stable
	 */
	public void setStable(boolean stable) {
		this.stable = stable;
	}

	/**
	 * Gets the tag liquid rate.
	 *
	 * @return the tag liquid rate
	 */
	public String getTagLiquidRate() {
		return tagLiquidRate;
	}

	/**
	 * Sets the tag liquid rate.
	 *
	 * @param tagLiquidRate the new tag liquid rate
	 */
	public void setTagLiquidRate(String tagLiquidRate) {
		this.tagLiquidRate = tagLiquidRate;
	}

	/**
	 * Gets the tag gas rate.
	 *
	 * @return the tag gas rate
	 */
	public String getTagGasRate() {
		return tagGasRate;
	}

	/**
	 * Sets the tag gas rate.
	 *
	 * @param tagGasRate the new tag gas rate
	 */
	public void setTagGasRate(String tagGasRate) {
		this.tagGasRate = tagGasRate;
	}

	/**
	 * Gets the tag watercut.
	 *
	 * @return the tag watercut
	 */
	public String getTagWatercut() {
		return tagWatercut;
	}

	/**
	 * Sets the tag watercut.
	 *
	 * @param tagWatercut the new tag watercut
	 */
	public void setTagWatercut(String tagWatercut) {
		this.tagWatercut = tagWatercut;
	}

	/**
	 * Gets the tag header pressure.
	 *
	 * @return the tag header pressure
	 */
	public String getTagHeaderPressure() {
		return tagHeaderPressure;
	}

	/**
	 * Sets the tag header pressure.
	 *
	 * @param tagHeaderPressure the new tag header pressure
	 */
	public void setTagHeaderPressure(String tagHeaderPressure) {
		this.tagHeaderPressure = tagHeaderPressure;
	}

	/**
	 * Gets the tag inj header pressure.
	 *
	 * @return the tag inj header pressure
	 */
	public String getTagInjHeaderPressure() {
		return tagInjHeaderPressure;
	}

	/**
	 * Sets the tag inj header pressure.
	 *
	 * @param tagInjHeaderPressure the new tag inj header pressure
	 */
	public void setTagInjHeaderPressure(String tagInjHeaderPressure) {
		this.tagInjHeaderPressure = tagInjHeaderPressure;
	}
}
