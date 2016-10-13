package com.brownfield.vre.exe.models;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * The Class RecalModel.
 * 
 * @author Onkar Dhuri <onkar.dhuri@synerzip.com>
 */
@XmlRootElement(name = "calibration")
public class RecalModel {

	/** The test liquid. */
	private double testLiquid;

	/** The calibration. */
	private boolean calibration;

	/** The review. */
	private boolean review;

	/** The tolerance limit. */
	private double toleranceLimit;

	/** The error limit. */
	private double errorLimit;

	/** The pwf. */
	private double pwf;

	/** The new pi. */
	private double newPI;

	/** The error. */
	private double error;

	/** The time. */
	private double time;

	/**
	 * Gets the test liquid.
	 *
	 * @return the test liquid
	 */
	@XmlElement(name = "test_liquid")
	public double getTestLiquid() {
		return testLiquid;
	}

	/**
	 * Sets the test liquid.
	 *
	 * @param testLiquid
	 *            the new test liquid
	 */
	public void setTestLiquid(double testLiquid) {
		this.testLiquid = testLiquid;
	}

	/**
	 * Gets the calibration.
	 *
	 * @return the calibration
	 */
	@XmlElement(name = "calibrated")
	public boolean getCalibration() {
		return calibration;
	}

	/**
	 * Sets the calibration.
	 *
	 * @param calibration
	 *            the new calibration
	 */
	public void setCalibration(boolean calibration) {
		this.calibration = calibration;
	}

	/**
	 * Checks if is review.
	 *
	 * @return true, if is review
	 */
	@XmlElement(name = "review")
	public boolean isReview() {
		return review;
	}

	/**
	 * Sets the review.
	 *
	 * @param review
	 *            the new review
	 */
	public void setReview(boolean review) {
		this.review = review;
	}

	/**
	 * Gets the tolerance limit.
	 *
	 * @return the tolerance limit
	 */
	@XmlElement(name = "tolerance_limit")
	public double getToleranceLimit() {
		return toleranceLimit;
	}

	/**
	 * Sets the tolerance limit.
	 *
	 * @param toleranceLimit
	 *            the new tolerance limit
	 */
	public void setToleranceLimit(double toleranceLimit) {
		this.toleranceLimit = toleranceLimit;
	}

	/**
	 * Gets the error limit.
	 *
	 * @return the error limit
	 */
	@XmlElement(name = "error_limit")
	public double getErrorLimit() {
		return errorLimit;
	}

	/**
	 * Sets the error limit.
	 *
	 * @param errorLimit
	 *            the new error limit
	 */
	public void setErrorLimit(double errorLimit) {
		this.errorLimit = errorLimit;
	}

	/**
	 * Gets the pwf.
	 *
	 * @return the pwf
	 */
	@XmlElement(name = "pwf")
	public double getPwf() {
		return pwf;
	}

	/**
	 * Sets the pwf.
	 *
	 * @param pwf
	 *            the new pwf
	 */
	public void setPwf(double pwf) {
		this.pwf = pwf;
	}

	/**
	 * Gets the new pi.
	 *
	 * @return the new pi
	 */
	@XmlElement(name = "new_pi")
	public double getNewPI() {
		return newPI;
	}

	/**
	 * Sets the new pi.
	 *
	 * @param newPI
	 *            the new new pi
	 */
	public void setNewPI(double newPI) {
		this.newPI = newPI;
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
