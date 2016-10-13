package com.brownfield.vre.exe.models;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * The Class MultiRateTestModel.
 * 
 * @author Onkar Dhuri <onkar.dhuri@synerzip.com>
 */
@XmlRootElement(name = "multi-rate_welltest")
public class MultiRateTestModel {

	/** The ql1. */
	private double ql1;

	/** The ql2. */
	private double ql2;

	/** The ql3. */
	private double ql3;

	/** The ql4. */
	private double ql4;

	/** The whp1. */
	private double whp1;

	/** The whp2. */
	private double whp2;

	/** The whp3. */
	private double whp3;

	/** The whp4. */
	private double whp4;

	/** The bhp1. */
	private double bhp1;

	/** The bhp2. */
	private double bhp2;

	/** The bhp3. */
	private double bhp3;

	/** The bhp4. */
	private double bhp4;

	/** The calc index. */
	private double calcIndex;

	/** The reservoir pressure. */
	private double reservoirPressure;

	/** The time. */
	private double time;

	/**
	 * Gets the ql1.
	 *
	 * @return the ql1
	 */
	@XmlElement(name = "ql1")
	public double getQl1() {
		return ql1;
	}

	/**
	 * Sets the ql1.
	 *
	 * @param ql1
	 *            the new ql1
	 */
	public void setQl1(double ql1) {
		this.ql1 = ql1;
	}

	/**
	 * Gets the ql2.
	 *
	 * @return the ql2
	 */
	@XmlElement(name = "ql2")
	public double getQl2() {
		return ql2;
	}

	/**
	 * Sets the ql2.
	 *
	 * @param ql2
	 *            the new ql2
	 */
	public void setQl2(double ql2) {
		this.ql2 = ql2;
	}

	/**
	 * Gets the ql3.
	 *
	 * @return the ql3
	 */
	@XmlElement(name = "ql3")
	public double getQl3() {
		return ql3;
	}

	/**
	 * Sets the ql3.
	 *
	 * @param ql3
	 *            the new ql3
	 */
	public void setQl3(double ql3) {
		this.ql3 = ql3;
	}

	/**
	 * Gets the ql4.
	 *
	 * @return the ql4
	 */
	public double getQl4() {
		return ql4;
	}

	/**
	 * Sets the ql4.
	 *
	 * @param ql4
	 *            the new ql4
	 */
	@XmlElement(name = "ql4")
	public void setQl4(double ql4) {
		this.ql4 = ql4;
	}

	/**
	 * Gets the whp1.
	 *
	 * @return the whp1
	 */
	@XmlElement(name = "whp1")
	public double getWhp1() {
		return whp1;
	}

	/**
	 * Sets the whp1.
	 *
	 * @param whp1
	 *            the new whp1
	 */
	public void setWhp1(double whp1) {
		this.whp1 = whp1;
	}

	/**
	 * Gets the whp2.
	 *
	 * @return the whp2
	 */
	@XmlElement(name = "whp1")
	public double getWhp2() {
		return whp2;
	}

	/**
	 * Sets the whp2.
	 *
	 * @param whp2
	 *            the new whp2
	 */
	public void setWhp2(double whp2) {
		this.whp2 = whp2;
	}

	/**
	 * Gets the whp3.
	 *
	 * @return the whp3
	 */
	@XmlElement(name = "whp3")
	public double getWhp3() {
		return whp3;
	}

	/**
	 * Sets the whp3.
	 *
	 * @param whp3
	 *            the new whp3
	 */
	public void setWhp3(double whp3) {
		this.whp3 = whp3;
	}

	/**
	 * Gets the whp4.
	 *
	 * @return the whp4
	 */
	@XmlElement(name = "whp4")
	public double getWhp4() {
		return whp4;
	}

	/**
	 * Sets the whp4.
	 *
	 * @param whp4
	 *            the new whp4
	 */
	public void setWhp4(double whp4) {
		this.whp4 = whp4;
	}

	/**
	 * Gets the bhp1.
	 *
	 * @return the bhp1
	 */
	@XmlElement(name = "bhp1")
	public double getBhp1() {
		return bhp1;
	}

	/**
	 * Sets the bhp1.
	 *
	 * @param bhp1
	 *            the new bhp1
	 */
	public void setBhp1(double bhp1) {
		this.bhp1 = bhp1;
	}

	/**
	 * Gets the bhp2.
	 *
	 * @return the bhp2
	 */
	@XmlElement(name = "bhp2")
	public double getBhp2() {
		return bhp2;
	}

	/**
	 * Sets the bhp2.
	 *
	 * @param bhp2
	 *            the new bhp2
	 */
	public void setBhp2(double bhp2) {
		this.bhp2 = bhp2;
	}

	/**
	 * Gets the bhp3.
	 *
	 * @return the bhp3
	 */
	@XmlElement(name = "bhp3")
	public double getBhp3() {
		return bhp3;
	}

	/**
	 * Sets the bhp3.
	 *
	 * @param bhp3
	 *            the new bhp3
	 */
	public void setBhp3(double bhp3) {
		this.bhp3 = bhp3;
	}

	/**
	 * Gets the bhp4.
	 *
	 * @return the bhp4
	 */
	@XmlElement(name = "bhp4")
	public double getBhp4() {
		return bhp4;
	}

	/**
	 * Sets the bhp4.
	 *
	 * @param bhp4
	 *            the new bhp4
	 */
	public void setBhp4(double bhp4) {
		this.bhp4 = bhp4;
	}

	/**
	 * Gets the calc index.
	 *
	 * @return the calc index
	 */
	@XmlElement(name = "calc_index")
	public double getCalcIndex() {
		return calcIndex;
	}

	/**
	 * Sets the calc index.
	 *
	 * @param calcIndex
	 *            the new calc index
	 */
	public void setCalcIndex(double calcIndex) {
		this.calcIndex = calcIndex;
	}

	/**
	 * Gets the reservoir pressure.
	 *
	 * @return the reservoir pressure
	 */
	@XmlElement(name = "reservoir_pressure")
	public double getReservoirPressure() {
		return reservoirPressure;
	}

	/**
	 * Sets the reservoir pressure.
	 *
	 * @param reservoirPressure
	 *            the new reservoir pressure
	 */
	public void setReservoirPressure(double reservoirPressure) {
		this.reservoirPressure = reservoirPressure;
	}

	/**
	 * Gets the time.
	 *
	 * @return the time
	 */
	@XmlElement(name = "time")
	public double getTime() {
		return time;
	}

	/**
	 * Sets the time.
	 *
	 * @param time
	 *            the new time
	 */
	public void setTime(double time) {
		this.time = time;
	}

}
