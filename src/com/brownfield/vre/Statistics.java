package com.brownfield.vre;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The Class Statistics.
 *
 * @author Onkar Dhuri <onkar.dhuri@synerzip.com>
 */
public class Statistics {

	/** The data. */
	List<Double> data;

	/** The size. */
	int size;

	/**
	 * Instantiates a new statistics.
	 *
	 * @param data
	 *            the data
	 */
	public Statistics(List<Double> data) {
		this.data = new ArrayList<>();
		this.data.addAll(data);
		size = data.size();
	}

	/**
	 * Gets the mean.
	 *
	 * @return the mean
	 */
	public double getMean() {
		double sum = 0.0;
		if (!data.isEmpty()) {
			for (double a : data) {
				sum += a;
			}
			return sum / size;
		}
		return 0;
	}

	/**
	 * Gets the variance.
	 *
	 * @return the variance
	 */
	public double getVariance() {
		double mean = getMean();
		double temp = 0;
		if (!data.isEmpty()) {
			for (double a : data) {
				temp += (mean - a) * (mean - a);
			}
			return temp / size;
		}
		return 0;
	}

	/**
	 * Gets the std dev.
	 *
	 * @return the std dev
	 */
	public double getStdDev() {
		return Math.sqrt(getVariance());
	}

	/**
	 * Gets the coefficient of variation.
	 *
	 * @return the coefficient of variation
	 */
	public double getCoefficientOfVariation() {
		double mean = getMean();
		if (mean != 0) {
			return getStdDev() / getMean();
		}
		return 0;
	}

	/**
	 * Gets the coefficient of variation.
	 *
	 * @param mean
	 *            the mean
	 * @return the coefficient of variation
	 */
	public double getCoefficientOfVariation(double mean) {
		if (mean != 0) {
			return getStdDev() / mean;
		}
		return 0;
	}

	/**
	 * Median.
	 *
	 * @return the double
	 */
	public double median() {
		Collections.sort(data);

		if (data.size() % 2 == 0) {
			return (data.get((data.size() / 2) - 1) + data.get(data.size() / 2)) / 2.0;
		} else {
			return data.get(data.size() / 2);
		}
	}

}
