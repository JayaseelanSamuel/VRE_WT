package com.brownfield.vre.exe.models;

import javax.xml.bind.annotation.XmlElement;

/**
 * The Class VREModel.
 * 
 * @author Onkar Dhuri <onkar.dhuri@synerzip.com>
 */
public class VREModel {

	/** The rate liquid. */
	private double rateLiquid;

	/** The rate oil. */
	private double rateOil;

	/** The iterations. */
	private double iterations;

	/** The pdg pressure. */
	private double pdgPressure;

	/** The error. */
	private double error;

	/** The whp. */
	// VRE3
	private double whp;

	/** The qgi. */
	// VRE4
	private double qgi;

	/** The interrupted. */
	private boolean interrupted;

	/** The time. */
	private double time;

	/** The choke multiplier. */
	// VRE 5
	private double chokeMultiplier;

	/** The dp choke. */
	private double dpChoke;

	/** The header pressure. */
	private double headerPressure;

	/** The bean size. */
	private double beanSize;

	/** The properties. */
	private PropertiesModel properties;

	/**
	 * Gets the rate liquid.
	 *
	 * @return the rate liquid
	 */
	@XmlElement(name = "rate_liquid")
	public double getRateLiquid() {
		return rateLiquid;
	}

	/**
	 * Sets the rate liquid.
	 *
	 * @param rateLiquid
	 *            the new rate liquid
	 */
	public void setRateLiquid(double rateLiquid) {
		this.rateLiquid = rateLiquid;
	}

	/**
	 * Gets the rate oil.
	 *
	 * @return the rate oil
	 */
	@XmlElement(name = "rate_oil")
	public double getRateOil() {
		return rateOil;
	}

	/**
	 * Sets the rate oil.
	 *
	 * @param rateOil
	 *            the new rate oil
	 */
	public void setRateOil(double rateOil) {
		this.rateOil = rateOil;
	}

	/**
	 * Gets the iterations.
	 *
	 * @return the iterations
	 */
	@XmlElement(name = "iterations")
	public double getIterations() {
		return iterations;
	}

	/**
	 * Sets the iterations.
	 *
	 * @param iterations
	 *            the new iterations
	 */
	public void setIterations(double iterations) {
		this.iterations = iterations;
	}

	/**
	 * Gets the pdg pressure.
	 *
	 * @return the pdg pressure
	 */
	@XmlElement(name = "pdg_pressure")
	public double getPdgPressure() {
		return pdgPressure;
	}

	/**
	 * Sets the pdg pressure.
	 *
	 * @param pdgPressure
	 *            the new pdg pressure
	 */
	public void setPdgPressure(double pdgPressure) {
		this.pdgPressure = pdgPressure;
	}

	/**
	 * Gets the error.
	 *
	 * @return the error
	 */
	@XmlElement(name = "error")
	public double getError() {
		return error;
	}

	/**
	 * Sets the error.
	 *
	 * @param error
	 *            the new error
	 */
	public void setError(double error) {
		this.error = error;
	}

	/**
	 * Gets the whp.
	 *
	 * @return the whp
	 */
	@XmlElement(name = "whp")
	public double getWhp() {
		return whp;
	}

	/**
	 * Sets the whp.
	 *
	 * @param whp
	 *            the new whp
	 */
	public void setWhp(double whp) {
		this.whp = whp;
	}

	/**
	 * Gets the qgi.
	 *
	 * @return the qgi
	 */
	@XmlElement(name = "qgi")
	public double getQgi() {
		return qgi;
	}

	/**
	 * Sets the qgi.
	 *
	 * @param qgi
	 *            the new qgi
	 */
	public void setQgi(double qgi) {
		this.qgi = qgi;
	}

	/**
	 * Checks if is interrupted.
	 *
	 * @return true, if is interrupted
	 */
	@XmlElement(name = "interrupted")
	public boolean isInterrupted() {
		return interrupted;
	}

	/**
	 * Sets the interrupted.
	 *
	 * @param interrupted
	 *            the new interrupted
	 */
	public void setInterrupted(boolean interrupted) {
		this.interrupted = interrupted;
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

	/**
	 * Gets the choke multiplier.
	 *
	 * @return the choke multiplier
	 */
	@XmlElement(name = "choke_multiplier")
	public double getChokeMultiplier() {
		return chokeMultiplier;
	}

	/**
	 * Sets the choke multiplier.
	 *
	 * @param chokeMultiplier
	 *            the new choke multiplier
	 */
	public void setChokeMultiplier(double chokeMultiplier) {
		this.chokeMultiplier = chokeMultiplier;
	}

	/**
	 * Gets the dp choke.
	 *
	 * @return the dp choke
	 */
	@XmlElement(name = "dpchoke")
	public double getDpChoke() {
		return dpChoke;
	}

	/**
	 * Sets the dp choke.
	 *
	 * @param dpChoke
	 *            the new dp choke
	 */
	public void setDpChoke(double dpChoke) {
		this.dpChoke = dpChoke;
	}

	/**
	 * Gets the header pressure.
	 *
	 * @return the header pressure
	 */
	@XmlElement(name = "header_pressure")
	public double getHeaderPressure() {
		return headerPressure;
	}

	/**
	 * Sets the header pressure.
	 *
	 * @param headerPressure
	 *            the new header pressure
	 */
	public void setHeaderPressure(double headerPressure) {
		this.headerPressure = headerPressure;
	}

	/**
	 * Gets the bean size.
	 *
	 * @return the bean size
	 */
	@XmlElement(name = "beansize")
	public double getBeanSize() {
		return beanSize;
	}

	/**
	 * Sets the bean size.
	 *
	 * @param beanSize
	 *            the new bean size
	 */
	public void setBeanSize(double beanSize) {
		this.beanSize = beanSize;
	}

	/**
	 * Gets the properties.
	 *
	 * @return the properties
	 */
	@XmlElement(name = "properties", type = PropertiesModel.class)
	public PropertiesModel getProperties() {
		return properties;
	}

	/**
	 * Sets the properties.
	 *
	 * @param properties
	 *            the new properties
	 */
	public void setProperties(PropertiesModel properties) {
		this.properties = properties;
	}

}
