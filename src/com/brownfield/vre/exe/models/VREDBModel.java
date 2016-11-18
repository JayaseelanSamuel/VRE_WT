package com.brownfield.vre.exe.models;

import java.sql.Timestamp;

public class VREDBModel {

	private int vreID;

	private int stringID;

	/** The string type. */
	private Timestamp recordedDate;
	
	private Double vre1;
	
	private Double vre2;
	
	private Double vre3;
	
	private Double vre4;
	
	private Double vre5;
	
	private Double vre6;
	
	private Double gor;
	
	private Double pi;
	
	private Double holdup;
	
	private Double frictionFactor;
	
	private Double reservoirPressure;
	
	private Double chokeMultiplier;

	/**
	 * @return the vreID
	 */
	public int getVreID() {
		return vreID;
	}

	/**
	 * @param vreID the vreID to set
	 */
	public void setVreID(int vreID) {
		this.vreID = vreID;
	}

	/**
	 * @return the stringID
	 */
	public int getStringID() {
		return stringID;
	}

	/**
	 * @param stringID the stringID to set
	 */
	public void setStringID(int stringID) {
		this.stringID = stringID;
	}

	/**
	 * @return the recordedDate
	 */
	public Timestamp getRecordedDate() {
		return recordedDate;
	}

	/**
	 * @param recordedDate the recordedDate to set
	 */
	public void setRecordedDate(Timestamp recordedDate) {
		this.recordedDate = recordedDate;
	}

	/**
	 * @return the vre1
	 */
	public Double getVre1() {
		return vre1;
	}

	/**
	 * @param vre1 the vre1 to set
	 */
	public void setVre1(Double vre1) {
		this.vre1 = vre1;
	}

	/**
	 * @return the vre2
	 */
	public Double getVre2() {
		return vre2;
	}

	/**
	 * @param vre2 the vre2 to set
	 */
	public void setVre2(Double vre2) {
		this.vre2 = vre2;
	}

	/**
	 * @return the vre3
	 */
	public Double getVre3() {
		return vre3;
	}

	/**
	 * @param vre3 the vre3 to set
	 */
	public void setVre3(Double vre3) {
		this.vre3 = vre3;
	}

	/**
	 * @return the vre4
	 */
	public Double getVre4() {
		return vre4;
	}

	/**
	 * @param vre4 the vre4 to set
	 */
	public void setVre4(Double vre4) {
		this.vre4 = vre4;
	}

	/**
	 * @return the vre5
	 */
	public Double getVre5() {
		return vre5;
	}

	/**
	 * @param vre5 the vre5 to set
	 */
	public void setVre5(Double vre5) {
		this.vre5 = vre5;
	}

	/**
	 * @return the vre6
	 */
	public Double getVre6() {
		return vre6;
	}

	/**
	 * @param vre6 the vre6 to set
	 */
	public void setVre6(Double vre6) {
		this.vre6 = vre6;
	}

	/**
	 * @return the gor
	 */
	public Double getGor() {
		return gor;
	}

	/**
	 * @param gor the gor to set
	 */
	public void setGor(Double gor) {
		this.gor = gor;
	}

	/**
	 * @return the pi
	 */
	public Double getPi() {
		return pi;
	}

	/**
	 * @param pi the pi to set
	 */
	public void setPi(Double pi) {
		this.pi = pi;
	}

	/**
	 * @return the holdup
	 */
	public Double getHoldup() {
		return holdup;
	}

	/**
	 * @param holdup the holdup to set
	 */
	public void setHoldup(Double holdup) {
		this.holdup = holdup;
	}

	/**
	 * @return the frictionFactor
	 */
	public Double getFrictionFactor() {
		return frictionFactor;
	}

	/**
	 * @param frictionFactor the frictionFactor to set
	 */
	public void setFrictionFactor(Double frictionFactor) {
		this.frictionFactor = frictionFactor;
	}

	/**
	 * @return the reservoirPressure
	 */
	public Double getReservoirPressure() {
		return reservoirPressure;
	}

	/**
	 * @param reservoirPressure the reservoirPressure to set
	 */
	public void setReservoirPressure(Double reservoirPressure) {
		this.reservoirPressure = reservoirPressure;
	}

	/**
	 * @return the chokeMultiplier
	 */
	public Double getChokeMultiplier() {
		return chokeMultiplier;
	}

	/**
	 * @param chokeMultiplier the chokeMultiplier to set
	 */
	public void setChokeMultiplier(Double chokeMultiplier) {
		this.chokeMultiplier = chokeMultiplier;
	}
}
