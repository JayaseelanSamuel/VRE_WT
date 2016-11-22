package com.brownfield.vre.exe.models;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * The Class MaxFlowRateModel.
 * 
 * @author Onkar Dhuri <onkar.dhuri@synerzip.com>
 */
@XmlRootElement(name = "max_flow_rate")
public class MaxFlowRateModel {
	/** The tech rate. */
	private Double techRate;

	/** The whp. */
	private Double whp;

	/** The time. */
	private Double time;

	/**
	 * @return the techRate
	 */
	@XmlElement(name = "techrate")
	public Double getTechRate() {
		return techRate;
	}

	/**
	 * @param techRate
	 *            the techRate to set
	 */
	public void setTechRate(Double techRate) {
		this.techRate = techRate;
	}

	/**
	 * @return the whp
	 */
	public Double getWhp() {
		return whp;
	}

	/**
	 * @param whp
	 *            the whp to set
	 */
	public void setWhp(Double whp) {
		this.whp = whp;
	}

	/**
	 * @return the time
	 */
	public Double getTime() {
		return time;
	}

	/**
	 * @param time
	 *            the time to set
	 */
	public void setTime(Double time) {
		this.time = time;
	}
}
