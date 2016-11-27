package com.brownfield.vre.exe.models;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * The Class MaxFlowRateModel.
 * 
 * @author Onkar Dhuri <onkar.dhuri@synerzip.com>
 */
@XmlRootElement(name = "max_flowrate")
public class MaxFlowRateModel {

	/** The header pressure. */
	private Double headerPressure;

	/** The minimum whp. */
	private Double minimumWhp;

	/** The rate liquid. */
	private Double rateLiquid;

	/** The rate oil. */
	private Double rateOil;

	/** The time. */
	private Double time;

	/**
	 * @return the headerPressure
	 */
	@XmlElement(name = "header_pressure")
	public Double getHeaderPressure() {
		return headerPressure;
	}

	/**
	 * @param headerPressure the headerPressure to set
	 */
	public void setHeaderPressure(Double headerPressure) {
		this.headerPressure = headerPressure;
	}

	/**
	 * @return the minimumWhp
	 */
	@XmlElement(name = "minimum_whp")
	public Double getMinimumWhp() {
		return minimumWhp;
	}

	/**
	 * @param minimumWhp the minimumWhp to set
	 */
	public void setMinimumWhp(Double minimumWhp) {
		this.minimumWhp = minimumWhp;
	}

	/**
	 * @return the rateLiquid
	 */
	@XmlElement(name = "rate_liquid")
	public Double getRateLiquid() {
		return rateLiquid;
	}

	/**
	 * @param rateLiquid the rateLiquid to set
	 */
	public void setRateLiquid(Double rateLiquid) {
		this.rateLiquid = rateLiquid;
	}

	/**
	 * @return the rateOil
	 */
	@XmlElement(name = "rate_oil")
	public Double getRateOil() {
		return rateOil;
	}

	/**
	 * @param rateOil the rateOil to set
	 */
	public void setRateOil(Double rateOil) {
		this.rateOil = rateOil;
	}

	/**
	 * @return the time
	 */
	public Double getTime() {
		return time;
	}

	/**
	 * @param time the time to set
	 */
	public void setTime(Double time) {
		this.time = time;
	}

}
